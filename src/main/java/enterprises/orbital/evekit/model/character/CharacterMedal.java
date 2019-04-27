package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_character_medal",
    indexes = {
        @Index(
            name = "medalIDIndex",
            columnList = "medalID"),
        @Index(
            name = "issuedIndex",
            columnList = "issued")
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterMedal.getbyMedalIDAndIssued",
        query = "SELECT c FROM CharacterMedal c where c.owner = :owner and c.medalID = :mid and c.issued = :issued and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterMedal extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterMedal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEDALS);

  @Lob
  @Column(
      length = 102400)
  private String description;
  private int medalID;
  private String title;
  private int corporationID;
  private long issued = -1;
  private int issuerID;
  private String reason;
  private String status;
  @Transient
  @ApiModelProperty(
      value = "issued Date")
  @JsonProperty("issuedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date issuedDate;

  @SuppressWarnings("unused")
  protected CharacterMedal() {}

  public CharacterMedal(String description, int medalID, String title, int corporationID, long issued, int issuerID,
                        String reason, String status) {
    super();
    this.description = description;
    this.medalID = medalID;
    this.title = title;
    this.corporationID = corporationID;
    this.issued = issued;
    this.issuerID = issuerID;
    this.reason = reason;
    this.status = status;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    issuedDate = assignDateField(issued);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterMedal)) return false;
    CharacterMedal other = (CharacterMedal) sup;
    return nullSafeObjectCompare(description, other.description) && medalID == other.medalID && nullSafeObjectCompare(
        title, other.title)
        && corporationID == other.corporationID && issued == other.issued && issuerID == other.issuerID && nullSafeObjectCompare(
        reason, other.reason)
        && nullSafeObjectCompare(status, other.status);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(description, medalID, title, corporationID, issued, issuerID, reason, status);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getDescription() {
    return description;
  }

  public int getMedalID() {
    return medalID;
  }

  public String getTitle() {
    return title;
  }

  public int getCorporationID() {
    return corporationID;
  }

  public long getIssued() {
    return issued;
  }

  public int getIssuerID() {
    return issuerID;
  }

  public String getReason() {
    return reason;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterMedal that = (CharacterMedal) o;
    return medalID == that.medalID &&
        corporationID == that.corporationID &&
        issued == that.issued &&
        issuerID == that.issuerID &&
        Objects.equals(description, that.description) &&
        Objects.equals(title, that.title) &&
        Objects.equals(reason, that.reason) &&
        Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), description, medalID, title, corporationID, issued, issuerID, reason, status);
  }

  @Override
  public String toString() {
    return "CharacterMedal{" +
        "description='" + description + '\'' +
        ", medalID=" + medalID +
        ", title='" + title + '\'' +
        ", corporationID=" + corporationID +
        ", issued=" + issued +
        ", issuerID=" + issuerID +
        ", reason='" + reason + '\'' +
        ", status='" + status + '\'' +
        ", issuedDate=" + issuedDate +
        '}';
  }

  public static CharacterMedal get(
      final SynchronizedEveAccount owner,
      final long time,
      final int medalID,
      final long issued) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterMedal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "CharacterMedal.getbyMedalIDAndIssued",
                                                                                                         CharacterMedal.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("mid", medalID);
                                        getter.setParameter("issued", issued);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<CharacterMedal> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector description,
      final AttributeSelector medalID,
      final AttributeSelector title,
      final AttributeSelector corporationID,
      final AttributeSelector issued,
      final AttributeSelector issuerID,
      final AttributeSelector reason,
      final AttributeSelector status) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterMedal c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addStringSelector(qs, "c", "description", description, p);
                                        AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
                                        AttributeSelector.addStringSelector(qs, "c", "title", title, p);
                                        AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                        AttributeSelector.addLongSelector(qs, "c", "issued", issued);
                                        AttributeSelector.addIntSelector(qs, "c", "issuerID", issuerID);
                                        AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
                                        AttributeSelector.addStringSelector(qs, "c", "status", status, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterMedal> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        CharacterMedal.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
