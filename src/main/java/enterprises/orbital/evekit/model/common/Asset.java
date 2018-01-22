package enterprises.orbital.evekit.model.common;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_asset",
    indexes = {
        @Index(
            name = "itemIDIndex",
            columnList = "itemID"),
        @Index(
            name = "locationIndex",
            columnList = "locationID")
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
        query = "SELECT c FROM Asset c where c.owner = :owner and c.locationID = :container and c.itemID > :item and c.lifeStart <= :point and c.lifeEnd > :point order by c.itemID asc"),
})
public class Asset extends CachedData {
  private static final Logger log = Logger.getLogger(Asset.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private static final int DEFAULT_MAX_RESULTS = 1000;
  private long itemID;
  private long locationID;
  private String locationType;
  private String locationFlag;
  private int typeID;
  private int quantity;
  private boolean singleton;
  private String blueprintType;

  @SuppressWarnings("unused")
  protected Asset() {}

  public Asset(long itemID, long locationID, String locationType, String locationFlag, int typeID, int quantity,
               boolean singleton, String blueprintType) {
    this.itemID = itemID;
    this.locationID = locationID;
    this.locationType = locationType;
    this.locationFlag = locationFlag;
    this.typeID = typeID;
    this.quantity = quantity;
    this.singleton = singleton;
    this.blueprintType = blueprintType;
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
    if (!(sup instanceof Asset)) return false;
    Asset other = (Asset) sup;
    return itemID == other.itemID && locationID == other.locationID &&
        nullSafeObjectCompare(locationType, other.locationType) &&
        nullSafeObjectCompare(locationFlag, other.locationFlag) &&
        typeID == other.typeID && quantity == other.quantity &&
        singleton == other.singleton && nullSafeObjectCompare(blueprintType, other.blueprintType);
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

  public boolean isSingleton() {
    return singleton;
  }

  public String getLocationType() {
    return locationType;
  }

  public String getLocationFlag() {
    return locationFlag;
  }

  public String getBlueprintType() {
    return blueprintType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Asset asset = (Asset) o;
    return itemID == asset.itemID &&
        locationID == asset.locationID &&
        typeID == asset.typeID &&
        quantity == asset.quantity &&
        singleton == asset.singleton &&
        Objects.equals(locationType, asset.locationType) &&
        Objects.equals(locationFlag, asset.locationFlag) &&
        Objects.equals(blueprintType, asset.blueprintType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemID, locationID, locationType, locationFlag, typeID, quantity, singleton,
                        blueprintType);
  }

  @Override
  public String toString() {
    return "Asset{" +
        "itemID=" + itemID +
        ", locationID=" + locationID +
        ", locationType='" + locationType + '\'' +
        ", locationFlag='" + locationFlag + '\'' +
        ", typeID=" + typeID +
        ", quantity=" + quantity +
        ", singleton=" + singleton +
        ", blueprintType='" + blueprintType + '\'' +
        '}';
  }

  /**
   * Retrieve an existing asset with the given ID and which is live at the given time. Returns null if no such asset exists.
   *
   * @param owner  asset owner
   * @param time   time at which the asset should be live
   * @param itemID asset ID
   * @return an existing asset, or null.
   */
  public static Asset get(
      final SynchronizedEveAccount owner,
      final long time,
      final long itemID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Asset> getter = EveKitUserAccountProvider.getFactory()
                                                                                            .getEntityManager()
                                                                                            .createNamedQuery(
                                                                                                "Asset.getByItemID",
                                                                                                Asset.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("item", itemID);
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

  /**
   * List assets live at a given time.
   *
   * @param owner      assets owner
   * @param time       time at which assets must be live
   * @param maxresults maximum number of assets to retrieve
   * @param contid     itemID (exclusive) from which to start returning results
   * @return a list of assets no longer than maxresults with itemID greater than contid
   */
  public static List<Asset> getAllAssets(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    String key = OrbitalProperties.getPropertyName(Asset.class, "maxresults");
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(key,
                                                                                                              DEFAULT_MAX_RESULTS));

    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Asset> getter = EveKitUserAccountProvider.getFactory()
                                                                                            .getEntityManager()
                                                                                            .createNamedQuery(
                                                                                                "Asset.listFromID",
                                                                                                Asset.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("item", contid);
                                        getter.setParameter("point", time);
                                        getter.setMaxResults(maxr);
                                        return getter.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  /**
   * Retrieve assets contained within the given asset.
   *
   * @param owner       assets owner
   * @param containerID itemID of the asset which is the container of the assets we're interested in.
   * @param time        time at which assets must be live
   * @param maxresults  maximum number of assets to retrieve
   * @param contid      itemID from which to start returning results
   * @return a list of assets contained by the given asset, no longer than maxresults, with itemID greater than contid
   */
  public static List<Asset> getContainedAssets(
      final SynchronizedEveAccount owner,
      final long containerID,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    String key = OrbitalProperties.getPropertyName(Asset.class, "maxresults");
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(key,
                                                                                                              DEFAULT_MAX_RESULTS));

    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Asset> getter = EveKitUserAccountProvider.getFactory()
                                                                                            .getEntityManager()
                                                                                            .createNamedQuery(
                                                                                                "Asset.getContained",
                                                                                                Asset.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("container", containerID);
                                        getter.setParameter("item", contid);
                                        getter.setParameter("point", time);
                                        getter.setMaxResults(maxr);
                                        return getter.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<Asset> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector itemID,
      final AttributeSelector locationID,
      final AttributeSelector locationType,
      final AttributeSelector locationFlag,
      final AttributeSelector typeID,
      final AttributeSelector quantity,
      final AttributeSelector singleton,
      final AttributeSelector blueprintType) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Asset c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addStringSelector(qs, "c", "locationType", locationType, p);
                                        AttributeSelector.addStringSelector(qs, "c", "locationFlag", locationFlag, p);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addLongSelector(qs, "c", "quantity", quantity);
                                        AttributeSelector.addBooleanSelector(qs, "c", "singleton", singleton);
                                        AttributeSelector.addStringSelector(qs, "c", "blueprintType", blueprintType, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Asset> query = EveKitUserAccountProvider.getFactory()
                                                                                           .getEntityManager()
                                                                                           .createQuery(qs.toString(),
                                                                                                        Asset.class);
                                        query.setParameter("owner", owner);
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
