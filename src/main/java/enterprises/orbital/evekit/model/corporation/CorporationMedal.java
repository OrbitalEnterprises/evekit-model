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
    name = "evekit_data_corporation_medal",
    indexes = {
        @Index(
            name = "medalIDIndex",
            columnList = "medalID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CorporationMedal.getByMedalID",
        query = "SELECT c FROM CorporationMedal c where c.owner = :owner and c.medalID = :medal and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CorporationMedal extends CachedData {
  private static final Logger log = Logger.getLogger(CorporationMedal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_MEDALS);

  private int medalID;
  @Lob
  @Column(
      length = 102400)
  private String description;
  private String title;
  private long created = -1;
  private int creatorID;

  @Transient
  @ApiModelProperty(
      value = "created Date")
  @JsonProperty("createdDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date createdDate;

  @SuppressWarnings("unused")
  protected CorporationMedal() {}

  public CorporationMedal(int medalID, String description, String title, long created, int creatorID) {
    super();
    this.medalID = medalID;
    this.description = description;
    this.title = title;
    this.created = created;
    this.creatorID = creatorID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    createdDate = assignDateField(created);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CorporationMedal)) return false;
    CorporationMedal other = (CorporationMedal) sup;
    return medalID == other.medalID && nullSafeObjectCompare(description, other.description) && nullSafeObjectCompare(
        title, other.title)
        && created == other.created && creatorID == other.creatorID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(medalID, description, title, created, creatorID);
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

  public String getDescription() {
    return description;
  }

  public String getTitle() {
    return title;
  }

  public long getCreated() {
    return created;
  }

  public int getCreatorID() {
    return creatorID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CorporationMedal that = (CorporationMedal) o;
    return medalID == that.medalID &&
        created == that.created &&
        creatorID == that.creatorID &&
        Objects.equals(description, that.description) &&
        Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), medalID, description, title, created, creatorID);
  }

  @Override
  public String toString() {
    return "CorporationMedal{" +
        "medalID=" + medalID +
        ", description='" + description + '\'' +
        ", title='" + title + '\'' +
        ", created=" + created +
        ", creatorID=" + creatorID +
        ", createdDate=" + createdDate +
        '}';
  }

  public static CorporationMedal get(
      final SynchronizedEveAccount owner,
      final long time,
      final int medalID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CorporationMedal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createNamedQuery(
                                                                                                           "CorporationMedal.getByMedalID",
                                                                                                           CorporationMedal.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("medal", medalID);
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

  public static List<CorporationMedal> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector medalID,
      final AttributeSelector description,
      final AttributeSelector title,
      final AttributeSelector created,
      final AttributeSelector creatorID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CorporationMedal c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
                                        AttributeSelector.addStringSelector(qs, "c", "description", description, p);
                                        AttributeSelector.addStringSelector(qs, "c", "title", title, p);
                                        AttributeSelector.addLongSelector(qs, "c", "created", created);
                                        AttributeSelector.addIntSelector(qs, "c", "creatorID", creatorID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CorporationMedal> query = EveKitUserAccountProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createQuery(
                                                                                                          qs.toString(),
                                                                                                          CorporationMedal.class);
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
