package enterprises.orbital.evekit.model.common;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_location",
    indexes = {
        @Index(
            name = "idIndex",
            columnList = "itemID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Location.getByItemID",
        query = "SELECT c FROM Location c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Location.listByItemID",
        query = "SELECT c FROM Location c where c.owner = :owner and c.itemID > :item and c.lifeStart <= :point and c.lifeEnd > :point order by c.itemID asc"),
})
public class Location extends CachedData {
  private static final Logger log                 = Logger.getLogger(Location.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_LOCATIONS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                itemID;
  private String              itemName;
  private double              x;
  private double              y;
  private double              z;

  @SuppressWarnings("unused")
  protected Location() {}

  public Location(long itemID, String itemName, double x, double y, double z) {
    super();
    this.itemID = itemID;
    this.itemName = itemName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Location)) return false;
    Location other = (Location) sup;
    return itemID == other.itemID && nullSafeObjectCompare(itemName, other.itemName) && x == other.x && y == other.y && z == other.z;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getItemID() {
    return itemID;
  }

  public String getItemName() {
    return itemName;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Location other = (Location) obj;
    if (itemID != other.itemID) return false;
    if (itemName == null) {
      if (other.itemName != null) return false;
    } else if (!itemName.equals(other.itemName)) return false;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
    if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Location [itemID=" + itemID + ", itemName=" + itemName + ", x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  /**
   * Return location with given properties, live at the given time. Returns null if no such location exists.
   * 
   * @param owner
   *          location owner
   * @param time
   *          time at which the location must be live
   * @param itemID
   *          item ID for which the location is being requested
   * @return location with the given properties live at the given time, or null.
   */
  public static Location get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Location>() {
        @Override
        public Location run() throws Exception {
          TypedQuery<Location> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Location.getByItemID", Location.class);
          getter.setParameter("owner", owner);
          getter.setParameter("item", itemID);
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

  /**
   * List locations live at the given time with a sortKey (lexicographically) greater than contid.
   * 
   * @param owner
   *          contact owner
   * @param time
   *          time at which contact must be live
   * @param maxresults
   *          maximum number of contacts to retrieve
   * @param contid
   *          sortKey (exclusive) from which to start returning results.
   * @return a list of locations no longer than maxresults with sortKey (lexicographically) greater than contid
   */
  public static List<Location> getAllLocations(
                                               final SynchronizedEveAccount owner,
                                               final long time,
                                               int maxresults,
                                               final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Location.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Location>>() {
        @Override
        public List<Location> run() throws Exception {
          TypedQuery<Location> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Location.listByItemID", Location.class);
          getter.setParameter("owner", owner);
          getter.setParameter("item", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<Location> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector itemID,
                                           final AttributeSelector itemName,
                                           final AttributeSelector x,
                                           final AttributeSelector y,
                                           final AttributeSelector z) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Location>>() {
        @Override
        public List<Location> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Location c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
          AttributeSelector.addStringSelector(qs, "c", "itemName", itemName, p);
          AttributeSelector.addDoubleSelector(qs, "c", "x", x);
          AttributeSelector.addDoubleSelector(qs, "c", "y", y);
          AttributeSelector.addDoubleSelector(qs, "c", "z", z);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Location> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Location.class);
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
