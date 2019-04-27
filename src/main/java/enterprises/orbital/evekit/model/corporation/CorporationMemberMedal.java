package enterprises.orbital.evekit.model.corporation;

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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_corporation_member_medal",
    indexes = {
        @Index(
            name = "medalIDIndex",
            columnList = "medalID"),
        @Index(
            name = "characterIDIndex",
            columnList = "characterID"),
        @Index(
            name = "issuedIndex",
            columnList = "issued"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CorporationMemberMedal.getByMedalAndCharacterAndIssued",
        query = "SELECT c FROM CorporationMemberMedal c where c.owner = :owner and c.medalID = :medal and c.characterID = :char and c.issued = :issued and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CorporationMemberMedal extends CachedData {
  private static final Logger log = Logger.getLogger(CorporationMemberMedal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_MEDALS);

  private int medalID;
  private int characterID;
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
  protected CorporationMemberMedal() {}

  public CorporationMemberMedal(int medalID, int characterID, long issued, int issuerID, String reason, String status) {
    super();
    this.medalID = medalID;
    this.characterID = characterID;
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
    if (!(sup instanceof CorporationMemberMedal)) return false;
    CorporationMemberMedal other = (CorporationMemberMedal) sup;
    return medalID == other.medalID && characterID == other.characterID && issued == other.issued && issuerID == other.issuerID
        && nullSafeObjectCompare(reason, other.reason) && nullSafeObjectCompare(status, other.status);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(medalID, characterID, issued, issuerID, reason, status);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getMedalID() {
    return medalID;
  }

  public int getCharacterID() {
    return characterID;
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
    CorporationMemberMedal that = (CorporationMemberMedal) o;
    return medalID == that.medalID &&
        characterID == that.characterID &&
        issued == that.issued &&
        issuerID == that.issuerID &&
        Objects.equals(reason, that.reason) &&
        Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), medalID, characterID, issued, issuerID, reason, status);
  }

  @Override
  public String toString() {
    return "CorporationMemberMedal{" +
        "medalID=" + medalID +
        ", characterID=" + characterID +
        ", issued=" + issued +
        ", issuerID=" + issuerID +
        ", reason='" + reason + '\'' +
        ", status='" + status + '\'' +
        ", issuedDate=" + issuedDate +
        '}';
  }

  public static CorporationMemberMedal get(
      final SynchronizedEveAccount owner,
      final long time,
      final int medalID,
      final int characterID,
      final long issued) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CorporationMemberMedal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                             .getEntityManager()
                                                                                                             .createNamedQuery(
                                                                                                                 "CorporationMemberMedal.getByMedalAndCharacterAndIssued",
                                                                                                                 CorporationMemberMedal.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("medal", medalID);
                                        getter.setParameter("char", characterID);
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

  public static List<CorporationMemberMedal> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector medalID,
      final AttributeSelector characterID,
      final AttributeSelector issued,
      final AttributeSelector issuerID,
      final AttributeSelector reason,
      final AttributeSelector status) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CorporationMemberMedal c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addLongSelector(qs, "c", "issued", issued);
                                        AttributeSelector.addIntSelector(qs, "c", "issuerID", issuerID);
                                        AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
                                        AttributeSelector.addStringSelector(qs, "c", "status", status, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CorporationMemberMedal> query = EveKitUserAccountProvider.getFactory()
                                                                                                            .getEntityManager()
                                                                                                            .createQuery(
                                                                                                                qs.toString(),
                                                                                                                CorporationMemberMedal.class);
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
