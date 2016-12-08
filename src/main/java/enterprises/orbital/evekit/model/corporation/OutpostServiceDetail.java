package enterprises.orbital.evekit.model.corporation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_outpost_service_detail",
    indexes = {
        @Index(
            name = "stationIDIndex",
            columnList = "stationID",
            unique = false),
        @Index(
            name = "serviceNameIndex",
            columnList = "serviceName",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "OutpostServiceDetail.getByStationIDAndServiceName",
        query = "SELECT c FROM OutpostServiceDetail c where c.owner = :owner and c.stationID = :station and c.serviceName = :service and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "OutpostServiceDetail.getAll",
        query = "SELECT c FROM OutpostServiceDetail c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "OutpostServiceDetail.getAllByStationID",
        query = "SELECT c FROM OutpostServiceDetail c where c.owner = :owner and c.stationID = :station and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class OutpostServiceDetail extends CachedData {
  private static final Logger log  = Logger.getLogger(OutpostServiceDetail.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_OUTPOST_LIST);
  private long                stationID;
  private String              serviceName;
  private long                ownerID;
  private double              minStanding;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          surchargePerBadStanding;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          discountPerGoodStanding;

  @SuppressWarnings("unused")
  private OutpostServiceDetail() {}

  public OutpostServiceDetail(long stationID, String serviceName, long ownerID, double minStanding, BigDecimal surchargePerBadStanding,
                              BigDecimal discountPerGoodStanding) {
    super();
    this.stationID = stationID;
    this.serviceName = serviceName;
    this.ownerID = ownerID;
    this.minStanding = minStanding;
    this.surchargePerBadStanding = surchargePerBadStanding;
    this.discountPerGoodStanding = discountPerGoodStanding;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof OutpostServiceDetail)) return false;
    OutpostServiceDetail other = (OutpostServiceDetail) sup;
    return stationID == other.stationID && nullSafeObjectCompare(serviceName, other.serviceName) && ownerID == other.ownerID && minStanding == other.minStanding
        && nullSafeObjectCompare(surchargePerBadStanding, other.surchargePerBadStanding)
        && nullSafeObjectCompare(discountPerGoodStanding, other.discountPerGoodStanding);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getStationID() {
    return stationID;
  }

  public String getServiceName() {
    return serviceName;
  }

  public long getOwnerID() {
    return ownerID;
  }

  public double getMinStanding() {
    return minStanding;
  }

  public BigDecimal getSurchargePerBadStanding() {
    return surchargePerBadStanding;
  }

  public BigDecimal getDiscountPerGoodStanding() {
    return discountPerGoodStanding;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((discountPerGoodStanding == null) ? 0 : discountPerGoodStanding.hashCode());
    long temp;
    temp = Double.doubleToLongBits(minStanding);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + (int) (ownerID ^ (ownerID >>> 32));
    result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
    result = prime * result + (int) (stationID ^ (stationID >>> 32));
    result = prime * result + ((surchargePerBadStanding == null) ? 0 : surchargePerBadStanding.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    OutpostServiceDetail other = (OutpostServiceDetail) obj;
    if (discountPerGoodStanding == null) {
      if (other.discountPerGoodStanding != null) return false;
    } else if (!discountPerGoodStanding.equals(other.discountPerGoodStanding)) return false;
    if (Double.doubleToLongBits(minStanding) != Double.doubleToLongBits(other.minStanding)) return false;
    if (ownerID != other.ownerID) return false;
    if (serviceName == null) {
      if (other.serviceName != null) return false;
    } else if (!serviceName.equals(other.serviceName)) return false;
    if (stationID != other.stationID) return false;
    if (surchargePerBadStanding == null) {
      if (other.surchargePerBadStanding != null) return false;
    } else if (!surchargePerBadStanding.equals(other.surchargePerBadStanding)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "OutpostServiceDetail [stationID=" + stationID + ", serviceName=" + serviceName + ", ownerID=" + ownerID + ", minStanding=" + minStanding
        + ", surchargePerBadStanding=" + surchargePerBadStanding + ", discountPerGoodStanding=" + discountPerGoodStanding + ", owner=" + owner + ", lifeStart="
        + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static OutpostServiceDetail get(
                                         final SynchronizedEveAccount owner,
                                         final long time,
                                         final long stationID,
                                         final String serviceName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<OutpostServiceDetail>() {
        @Override
        public OutpostServiceDetail run() throws Exception {
          TypedQuery<OutpostServiceDetail> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("OutpostServiceDetail.getByStationIDAndServiceName", OutpostServiceDetail.class);
          getter.setParameter("owner", owner);
          getter.setParameter("station", stationID);
          getter.setParameter("service", serviceName);
          getter.setParameter("point", time);
          try {
            return getter.getSingleResult();
          } catch (NoResultException e) {
            return null;
          }
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static List<OutpostServiceDetail> getAll(
                                                  final SynchronizedEveAccount owner,
                                                  final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<OutpostServiceDetail>>() {
        @Override
        public List<OutpostServiceDetail> run() throws Exception {
          TypedQuery<OutpostServiceDetail> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("OutpostServiceDetail.getAll",
                                                                                                                               OutpostServiceDetail.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<OutpostServiceDetail> getAllByStationID(
                                                             final SynchronizedEveAccount owner,
                                                             final long time,
                                                             final long stationID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<OutpostServiceDetail>>() {
        @Override
        public List<OutpostServiceDetail> run() throws Exception {
          TypedQuery<OutpostServiceDetail> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("OutpostServiceDetail.getAllByStationID", OutpostServiceDetail.class);
          getter.setParameter("owner", owner);
          getter.setParameter("station", stationID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<OutpostServiceDetail> accessQuery(
                                                       final SynchronizedEveAccount owner,
                                                       final long contid,
                                                       final int maxresults,
                                                       final boolean reverse,
                                                       final AttributeSelector at,
                                                       final AttributeSelector stationID,
                                                       final AttributeSelector serviceName,
                                                       final AttributeSelector ownerID,
                                                       final AttributeSelector minStanding,
                                                       final AttributeSelector surchargePerBadStanding,
                                                       final AttributeSelector discountPerGoodStanding) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<OutpostServiceDetail>>() {
        @Override
        public List<OutpostServiceDetail> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM OutpostServiceDetail c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
          AttributeSelector.addStringSelector(qs, "c", "serviceName", serviceName, p);
          AttributeSelector.addLongSelector(qs, "c", "ownerID", ownerID);
          AttributeSelector.addDoubleSelector(qs, "c", "minStanding", minStanding);
          AttributeSelector.addDoubleSelector(qs, "c", "surchargePerBadStanding", surchargePerBadStanding);
          AttributeSelector.addDoubleSelector(qs, "c", "discountPerGoodStanding", discountPerGoodStanding);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<OutpostServiceDetail> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                         OutpostServiceDetail.class);
          query.setParameter("owner", owner);
          p.fillParams(query);
          query.setMaxResults(maxresults);
          return query.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
