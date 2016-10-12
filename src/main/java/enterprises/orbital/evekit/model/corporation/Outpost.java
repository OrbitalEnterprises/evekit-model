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
    name = "evekit_data_outpost",
    indexes = {
        @Index(
            name = "stationIDIndex",
            columnList = "stationID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Outpost.getByStationID",
        query = "SELECT c FROM Outpost c where c.owner = :owner and c.stationID = :station and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Outpost.getAll",
        query = "SELECT c FROM Outpost c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class Outpost extends CachedData {
  private static final Logger log  = Logger.getLogger(Outpost.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_OUTPOST_LIST);
  private long                stationID;
  private long                ownerID;
  private String              stationName;
  private int                 solarSystemID;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          dockingCostPerShipVolume;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          officeRentalCost;
  private int                 stationTypeID;
  private double              reprocessingEfficiency;
  private double              reprocessingStationTake;
  private long                standingOwnerID;
  private long                x;
  private long                y;
  private long                z;

  @SuppressWarnings("unused")
  private Outpost() {}

  public Outpost(long stationID, long ownerID, String stationName, int solarSystemID, BigDecimal dockingCostPerShipVolume, BigDecimal officeRentalCost,
                 int stationTypeID, double reprocessingEfficiency, double reprocessingStationTake, long standingOwnerID, long x, long y, long z) {
    super();
    this.stationID = stationID;
    this.ownerID = ownerID;
    this.stationName = stationName;
    this.solarSystemID = solarSystemID;
    this.dockingCostPerShipVolume = dockingCostPerShipVolume;
    this.officeRentalCost = officeRentalCost;
    this.stationTypeID = stationTypeID;
    this.reprocessingEfficiency = reprocessingEfficiency;
    this.reprocessingStationTake = reprocessingStationTake;
    this.standingOwnerID = standingOwnerID;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Outpost)) return false;
    Outpost other = (Outpost) sup;
    return stationID == other.stationID && ownerID == other.ownerID && nullSafeObjectCompare(stationName, other.stationName)
        && solarSystemID == other.solarSystemID && nullSafeObjectCompare(dockingCostPerShipVolume, other.dockingCostPerShipVolume)
        && nullSafeObjectCompare(officeRentalCost, other.officeRentalCost) && stationTypeID == other.stationTypeID
        && reprocessingEfficiency == other.reprocessingEfficiency && reprocessingStationTake == other.reprocessingStationTake
        && standingOwnerID == other.standingOwnerID && x == other.x && y == other.y && z == other.z;
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

  public long getOwnerID() {
    return ownerID;
  }

  public String getStationName() {
    return stationName;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public BigDecimal getDockingCostPerShipVolume() {
    return dockingCostPerShipVolume;
  }

  public BigDecimal getOfficeRentalCost() {
    return officeRentalCost;
  }

  public int getStationTypeID() {
    return stationTypeID;
  }

  public double getReprocessingEfficiency() {
    return reprocessingEfficiency;
  }

  public double getReprocessingStationTake() {
    return reprocessingStationTake;
  }

  public long getStandingOwnerID() {
    return standingOwnerID;
  }

  public long getX() {
    return x;
  }

  public long getY() {
    return y;
  }

  public long getZ() {
    return z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((dockingCostPerShipVolume == null) ? 0 : dockingCostPerShipVolume.hashCode());
    result = prime * result + ((officeRentalCost == null) ? 0 : officeRentalCost.hashCode());
    result = prime * result + (int) (ownerID ^ (ownerID >>> 32));
    long temp;
    temp = Double.doubleToLongBits(reprocessingEfficiency);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(reprocessingStationTake);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + solarSystemID;
    result = prime * result + (int) (standingOwnerID ^ (standingOwnerID >>> 32));
    result = prime * result + (int) (stationID ^ (stationID >>> 32));
    result = prime * result + ((stationName == null) ? 0 : stationName.hashCode());
    result = prime * result + stationTypeID;
    result = prime * result + (int) (x ^ (x >>> 32));
    result = prime * result + (int) (y ^ (y >>> 32));
    result = prime * result + (int) (z ^ (z >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Outpost other = (Outpost) obj;
    if (dockingCostPerShipVolume == null) {
      if (other.dockingCostPerShipVolume != null) return false;
    } else if (!dockingCostPerShipVolume.equals(other.dockingCostPerShipVolume)) return false;
    if (officeRentalCost == null) {
      if (other.officeRentalCost != null) return false;
    } else if (!officeRentalCost.equals(other.officeRentalCost)) return false;
    if (ownerID != other.ownerID) return false;
    if (Double.doubleToLongBits(reprocessingEfficiency) != Double.doubleToLongBits(other.reprocessingEfficiency)) return false;
    if (Double.doubleToLongBits(reprocessingStationTake) != Double.doubleToLongBits(other.reprocessingStationTake)) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (standingOwnerID != other.standingOwnerID) return false;
    if (stationID != other.stationID) return false;
    if (stationName == null) {
      if (other.stationName != null) return false;
    } else if (!stationName.equals(other.stationName)) return false;
    if (stationTypeID != other.stationTypeID) return false;
    if (x != other.x) return false;
    if (y != other.y) return false;
    if (z != other.z) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Outpost [stationID=" + stationID + ", ownerID=" + ownerID + ", stationName=" + stationName + ", solarSystemID=" + solarSystemID
        + ", dockingCostPerShipVolume=" + dockingCostPerShipVolume + ", officeRentalCost=" + officeRentalCost + ", stationTypeID=" + stationTypeID
        + ", reprocessingEfficiency=" + reprocessingEfficiency + ", reprocessingStationTake=" + reprocessingStationTake + ", standingOwnerID=" + standingOwnerID
        + ", x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  public static Outpost get(
                            final SynchronizedEveAccount owner,
                            final long time,
                            final long stationID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Outpost>() {
        @Override
        public Outpost run() throws Exception {
          TypedQuery<Outpost> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Outpost.getByStationID", Outpost.class);
          getter.setParameter("owner", owner);
          getter.setParameter("station", stationID);
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

  public static List<Outpost> getAll(
                                     final SynchronizedEveAccount owner,
                                     final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Outpost>>() {
        @Override
        public List<Outpost> run() throws Exception {
          TypedQuery<Outpost> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Outpost.getAll", Outpost.class);
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

  public static List<Outpost> accessQuery(
                                          final SynchronizedEveAccount owner,
                                          final long contid,
                                          final int maxresults,
                                          final boolean reverse,
                                          final AttributeSelector at,
                                          final AttributeSelector stationID,
                                          final AttributeSelector ownerID,
                                          final AttributeSelector stationName,
                                          final AttributeSelector solarSystemID,
                                          final AttributeSelector dockingCostPerShipVolume,
                                          final AttributeSelector officeRentalCost,
                                          final AttributeSelector stationTypeID,
                                          final AttributeSelector reprocessingEfficiency,
                                          final AttributeSelector reprocessingStationTake,
                                          final AttributeSelector standingOwnerID,
                                          final AttributeSelector x,
                                          final AttributeSelector y,
                                          final AttributeSelector z) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Outpost>>() {
        @Override
        public List<Outpost> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Outpost c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
          AttributeSelector.addLongSelector(qs, "c", "ownerID", ownerID);
          AttributeSelector.addStringSelector(qs, "c", "stationName", stationName, p);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addDoubleSelector(qs, "c", "dockingCostPerShipVolume", dockingCostPerShipVolume);
          AttributeSelector.addDoubleSelector(qs, "c", "officeRentalCost", officeRentalCost);
          AttributeSelector.addIntSelector(qs, "c", "stationTypeID", stationTypeID);
          AttributeSelector.addDoubleSelector(qs, "c", "reprocessingEfficiency", reprocessingEfficiency);
          AttributeSelector.addDoubleSelector(qs, "c", "reprocessingStationTake", reprocessingStationTake);
          AttributeSelector.addLongSelector(qs, "c", "standingOwnerID", standingOwnerID);
          AttributeSelector.addLongSelector(qs, "c", "x", x);
          AttributeSelector.addLongSelector(qs, "c", "y", y);
          AttributeSelector.addLongSelector(qs, "c", "z", z);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Outpost> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Outpost.class);
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
