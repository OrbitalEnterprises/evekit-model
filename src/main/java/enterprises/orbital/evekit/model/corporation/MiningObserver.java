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
    name = "evekit_data_corporation_mining_observer",
    indexes = {
        @Index(
            name = "miningObserverIndex",
            columnList = "observerID"),
    }
)
@NamedQueries({
    @NamedQuery(
        name = "MiningObserver.get",
        query = "SELECT c FROM MiningObserver c where c.owner = :owner and c.observerID = :oid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MiningObserver extends CachedData {
  private static final Logger log = Logger.getLogger(MiningObserver.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER);

  private long observerID;
  private String observerType;
  private long lastUpdated;

  @Transient
  @ApiModelProperty(
      value = "lastUpdated Date")
  @JsonProperty("lastUpdatedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastUpdatedDate;

  @SuppressWarnings("unused")
  protected MiningObserver() {}

  public MiningObserver(long observerID, String observerType, long lastUpdated) {
    this.observerID = observerID;
    this.observerType = observerType;
    this.lastUpdated = lastUpdated;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    lastUpdatedDate = assignDateField(lastUpdated);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MiningObserver)) return false;
    MiningObserver other = (MiningObserver) sup;
    return observerID == other.observerID &&
        nullSafeObjectCompare(observerType, other.observerType) &&
        lastUpdated == other.lastUpdated;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getObserverID() {
    return observerID;
  }

  public String getObserverType() {
    return observerType;
  }

  public long getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public String toString() {
    return "MiningObserver{" +
        "observerID=" + observerID +
        ", observerType='" + observerType + '\'' +
        ", lastUpdated=" + lastUpdated +
        ", lastUpdatedDate=" + lastUpdatedDate +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MiningObserver that = (MiningObserver) o;
    return observerID == that.observerID &&
        lastUpdated == that.lastUpdated &&
        Objects.equals(observerType, that.observerType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), observerID, observerType, lastUpdated);
  }

  public static MiningObserver get(
      final SynchronizedEveAccount owner,
      final long time,
      final long observerID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MiningObserver> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "MiningObserver.get",
                                                                                                         MiningObserver.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("oid", observerID);
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

  public static List<MiningObserver> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector observerID,
      final AttributeSelector observerType,
      final AttributeSelector lastUpdated) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MiningObserver c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "observerID", observerID);
                                        AttributeSelector.addStringSelector(qs, "c", "observerType", observerType, p);
                                        AttributeSelector.addLongSelector(qs, "c", "lastUpdated",
                                                                          lastUpdated);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MiningObserver> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        MiningObserver.class);
                                        query.setParameter("owner", owner);
                                        query.setMaxResults(maxresults);
                                        p.fillParams(query);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
