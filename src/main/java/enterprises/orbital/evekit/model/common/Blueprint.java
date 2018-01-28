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
    name = "evekit_data_blueprint",
    indexes = {
        @Index(
            name = "itemIDIndex",
            columnList = "itemID")
    })
@NamedQueries({
    @NamedQuery(
        name = "Blueprint.getByItemID",
        query = "SELECT c FROM Blueprint c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Blueprint.listByItemID",
        query = "SELECT c FROM Blueprint c where c.owner = :owner and c.itemID > :item and c.lifeStart <= :point and c.lifeEnd > :point order by c.itemID asc"),
})
// 25 hour cache time - API caches for 24 hours
public class Blueprint extends CachedData {
  private static final Logger log = Logger.getLogger(Blueprint.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_BLUEPRINTS);
  private static final int DEFAULT_MAX_RESULTS = 1000;
  private long itemID;
  private long locationID;
  private String locationFlag;
  private int typeID;
  private int quantity;
  private int timeEfficiency;
  private int materialEfficiency;
  private int runs;

  @SuppressWarnings("unused")
  protected Blueprint() {}

  public Blueprint(long itemID, long locationID, String locationFlag, int typeID, int quantity, int timeEfficiency,
                   int materialEfficiency, int runs) {
    this.itemID = itemID;
    this.locationID = locationID;
    this.locationFlag = locationFlag;
    this.typeID = typeID;
    this.quantity = quantity;
    this.timeEfficiency = timeEfficiency;
    this.materialEfficiency = materialEfficiency;
    this.runs = runs;
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
    if (!(sup instanceof Blueprint)) return false;
    Blueprint other = (Blueprint) sup;
    return itemID == other.itemID && locationID == other.locationID &&
        nullSafeObjectCompare(locationFlag, other.locationFlag) && typeID == other.typeID &&
        quantity == other.quantity && timeEfficiency == other.timeEfficiency && materialEfficiency == other.materialEfficiency
        && runs == other.runs;
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

  public int getTimeEfficiency() {
    return timeEfficiency;
  }

  public int getMaterialEfficiency() {
    return materialEfficiency;
  }

  public int getRuns() {
    return runs;
  }

  public String getLocationFlag() {
    return locationFlag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Blueprint blueprint = (Blueprint) o;
    return itemID == blueprint.itemID &&
        locationID == blueprint.locationID &&
        typeID == blueprint.typeID &&
        quantity == blueprint.quantity &&
        timeEfficiency == blueprint.timeEfficiency &&
        materialEfficiency == blueprint.materialEfficiency &&
        runs == blueprint.runs &&
        Objects.equals(locationFlag, blueprint.locationFlag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemID, locationID, locationFlag, typeID, quantity, timeEfficiency,
                        materialEfficiency, runs);
  }

  @Override
  public String toString() {
    return "Blueprint{" +
        "itemID=" + itemID +
        ", locationID=" + locationID +
        ", locationFlag='" + locationFlag + '\'' +
        ", typeID=" + typeID +
        ", quantity=" + quantity +
        ", timeEfficiency=" + timeEfficiency +
        ", materialEfficiency=" + materialEfficiency +
        ", runs=" + runs +
        '}';
  }

  /**
   * Return blueprint with the given ID, live at the given time. Returns null if no such blueprint exists.
   *
   * @param owner  blueprint owner
   * @param time   time at which the blueprint must be live
   * @param itemID blueprint itemID
   * @return blueprint with the given ID live at the given time, or null.
   */
  public static Blueprint get(
      final SynchronizedEveAccount owner,
      final long time,
      final long itemID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Blueprint> getter = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery(
                                                                                                    "Blueprint.getByItemID",
                                                                                                    Blueprint.class);
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
   * List blueprints live at a given time.
   *
   * @param owner      assets owner
   * @param time       time at which blueprints must be live
   * @param maxresults maximum number of blueprints to retrieve
   * @param contid     itemID (exclusive) from which to start returning results
   * @return a list of blueprints no longer than maxresults with itemID greater than contid
   */
  public static List<Blueprint> getAllBlueprints(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                             OrbitalProperties.getPropertyName(Blueprint.class,
                                                                                               "maxresults"),
                                                             DEFAULT_MAX_RESULTS));

    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Blueprint> getter = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery(
                                                                                                    "Blueprint.listByItemID",
                                                                                                    Blueprint.class);
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

  public static List<Blueprint> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector itemID,
      final AttributeSelector locationID,
      final AttributeSelector locationFlag,
      final AttributeSelector typeID,
      final AttributeSelector quantity,
      final AttributeSelector timeEfficiency,
      final AttributeSelector materialEfficiency,
      final AttributeSelector runs) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Blueprint c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addStringSelector(qs, "c", "locationFlag", locationFlag, p);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
                                        AttributeSelector.addIntSelector(qs, "c", "timeEfficiency", timeEfficiency);
                                        AttributeSelector.addIntSelector(qs, "c", "materialEfficiency",
                                                                         materialEfficiency);
                                        AttributeSelector.addIntSelector(qs, "c", "runs", runs);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Blueprint> query = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createQuery(
                                                                                                   qs.toString(),
                                                                                                   Blueprint.class);
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
