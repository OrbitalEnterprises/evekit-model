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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_asset",
    indexes = {
        @Index(
            name = "itemIDIndex",
            columnList = "itemID",
            unique = false),
        @Index(
            name = "containerIndex",
            columnList = "container",
            unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "Asset.getByItemID",
        query = "SELECT c FROM Asset c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Asset.listFromID",
        query = "SELECT c FROM Asset c where c.owner = :owner and c.itemID > :item and c.lifeStart <= :point and c.lifeEnd > :point order by c.itemID asc"),
    @NamedQuery(
        name = "Asset.getContained",
        query = "SELECT c FROM Asset c where c.owner = :owner and c.container = :container and c.itemID > :item and c.lifeStart <= :point and c.lifeEnd > :point order by c.itemID asc"),
})
// 7 hour cache time - API caches for 6 hours
public class Asset extends CachedData {
  private static final Logger log                 = Logger.getLogger(Asset.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  public static final long    TOP_LEVEL           = -1L;

  private long                itemID;
  private long                locationID;
  private int                 typeID;
  private int                 quantity;
  private int                 flag;
  private boolean             singleton;
  private int                 rawQuantity;
  // Stores the itemID of the container which contains this asset or -1 if this is a top-level asset.
  private long                container           = TOP_LEVEL;

  @SuppressWarnings("unused")
  private Asset() {}

  public Asset(long itemID, long locationID, int typeID, int quantity, int flag, boolean singleton, int rawQuantity, long container) {
    this.itemID = itemID;
    this.locationID = locationID;
    this.typeID = typeID;
    this.quantity = quantity;
    this.flag = flag;
    this.singleton = singleton;
    this.rawQuantity = rawQuantity;
    this.container = container;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Asset)) return false;
    Asset other = (Asset) sup;
    return itemID == other.itemID && locationID == other.locationID && typeID == other.typeID && quantity == other.quantity && flag == other.flag
        && singleton == other.singleton && rawQuantity == other.rawQuantity && container == other.container;
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

  public long getLocationID() {
    return locationID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getQuantity() {
    return quantity;
  }

  public int getFlag() {
    return flag;
  }

  public boolean isSingleton() {
    return singleton;
  }

  public int getRawQuantity() {
    return rawQuantity;
  }

  public long getContainer() {
    return container;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (container ^ (container >>> 32));
    result = prime * result + flag;
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + quantity;
    result = prime * result + rawQuantity;
    result = prime * result + (singleton ? 1231 : 1237);
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Asset other = (Asset) obj;
    if (container != other.container) return false;
    if (flag != other.flag) return false;
    if (itemID != other.itemID) return false;
    if (locationID != other.locationID) return false;
    if (quantity != other.quantity) return false;
    if (rawQuantity != other.rawQuantity) return false;
    if (singleton != other.singleton) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Asset [itemID=" + itemID + ", locationID=" + locationID + ", typeID=" + typeID + ", quantity=" + quantity + ", flag=" + flag + ", singleton="
        + singleton + ", rawQuantity=" + rawQuantity + ", container=" + container + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd
        + "]";
  }

  /**
   * Retrieve an existing asset with the given ID and which is live at the given time. Returns null if no such asset exists.
   * 
   * @param owner
   *          asset owner
   * @param time
   *          time at which the asset should be live
   * @param itemID
   *          asset ID
   * @return an existing asset, or null.
   */
  public static Asset get(
                          final SynchronizedEveAccount owner,
                          final long time,
                          final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Asset>() {
        @Override
        public Asset run() throws Exception {
          TypedQuery<Asset> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Asset.getByItemID", Asset.class);
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
   * List assets live at a given time.
   * 
   * @param owner
   *          assets owner
   * @param time
   *          time at which assets must be live
   * @param maxresults
   *          maximum number of assets to retrieve
   * @param contid
   *          itemID (exclusive) from which to start returning results
   * @return a list of assets no longer than maxresults with itemID greater than contid
   */
  public static List<Asset> getAllAssets(
                                         final SynchronizedEveAccount owner,
                                         final long time,
                                         int maxresults,
                                         final long contid) {
    String key = OrbitalProperties.getPropertyName(Asset.class, "maxresults");
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty.getLongPropertyWithFallback(key, DEFAULT_MAX_RESULTS));

    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Asset>>() {
        @Override
        public List<Asset> run() throws Exception {
          TypedQuery<Asset> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Asset.listFromID", Asset.class);
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

  /**
   * Retrieve assets contained within the given asset.
   * 
   * @param owner
   *          assets owner
   * @param containerID
   *          itemID of the asset which is the container of the assets we're interested in.
   * @param time
   *          time at which assets must be live
   * @param maxresults
   *          maximum number of assets to retrieve
   * @param contid
   *          itemID from which to start returning results
   * @return a list of assets contained by the given asset, no longer than maxresults, with itemID greater than contid
   */
  public static List<Asset> getContainedAssets(
                                               final SynchronizedEveAccount owner,
                                               final long containerID,
                                               final long time,
                                               int maxresults,
                                               final long contid) {
    String key = OrbitalProperties.getPropertyName(Asset.class, "maxresults");
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty.getLongPropertyWithFallback(key, DEFAULT_MAX_RESULTS));

    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Asset>>() {
        @Override
        public List<Asset> run() throws Exception {
          TypedQuery<Asset> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Asset.getContained", Asset.class);
          getter.setParameter("owner", owner);
          getter.setParameter("container", containerID);
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

  public static List<Asset> accessQuery(
                                        final SynchronizedEveAccount owner,
                                        final long contid,
                                        final int maxresults,
                                        final AttributeSelector at,
                                        final AttributeSelector itemID,
                                        final AttributeSelector locationID,
                                        final AttributeSelector typeID,
                                        final AttributeSelector quantity,
                                        final AttributeSelector flag,
                                        final AttributeSelector singleton,
                                        final AttributeSelector rawQuantity,
                                        final AttributeSelector container) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Asset>>() {
        @Override
        public List<Asset> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Asset c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
          AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
          AttributeSelector.addIntSelector(qs, "c", "flag", flag);
          AttributeSelector.addBooleanSelector(qs, "c", "singleton", singleton);
          AttributeSelector.addIntSelector(qs, "c", "rawQuantity", rawQuantity);
          AttributeSelector.addLongSelector(qs, "c", "container", container);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<Asset> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Asset.class);
          query.setParameter("owner", owner);
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
