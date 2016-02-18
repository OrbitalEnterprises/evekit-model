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
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_blueprint", indexes = {
    @Index(name = "itemIDIndex", columnList = "itemID", unique = false)
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
  private static final Logger log                 = Logger.getLogger(Blueprint.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_BLUEPRINTS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                itemID;
  private long                locationID;
  private int                 typeID;
  private String              typeName;
  private int                 flagID;
  private int                 quantity;
  private int                 timeEfficiency;
  private int                 materialEfficiency;
  private int                 runs;

  @SuppressWarnings("unused")
  private Blueprint() {}

  public Blueprint(long itemID, long locationID, int typeID, String typeName, int flagID, int quantity, int timeEfficiency, int materialEfficiency, int runs) {
    super();
    this.itemID = itemID;
    this.locationID = locationID;
    this.typeID = typeID;
    this.typeName = typeName;
    this.flagID = flagID;
    this.quantity = quantity;
    this.timeEfficiency = timeEfficiency;
    this.materialEfficiency = materialEfficiency;
    this.runs = runs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof Blueprint)) return false;
    Blueprint other = (Blueprint) sup;
    return itemID == other.itemID && locationID == other.locationID && typeID == other.typeID && nullSafeObjectCompare(typeName, other.typeName)
        && flagID == other.flagID && quantity == other.quantity && timeEfficiency == other.timeEfficiency && materialEfficiency == other.materialEfficiency
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

  public String getTypeName() {
    return typeName;
  }

  public int getFlagID() {
    return flagID;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + flagID;
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + materialEfficiency;
    result = prime * result + quantity;
    result = prime * result + runs;
    result = prime * result + timeEfficiency;
    result = prime * result + typeID;
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Blueprint other = (Blueprint) obj;
    if (flagID != other.flagID) return false;
    if (itemID != other.itemID) return false;
    if (locationID != other.locationID) return false;
    if (materialEfficiency != other.materialEfficiency) return false;
    if (quantity != other.quantity) return false;
    if (runs != other.runs) return false;
    if (timeEfficiency != other.timeEfficiency) return false;
    if (typeID != other.typeID) return false;
    if (typeName == null) {
      if (other.typeName != null) return false;
    } else if (!typeName.equals(other.typeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Blueprint [itemID=" + itemID + ", locationID=" + locationID + ", typeID=" + typeID + ", typeName=" + typeName + ", flagID=" + flagID + ", quantity="
        + quantity + ", timeEfficiency=" + timeEfficiency + ", materialEfficiency=" + materialEfficiency + ", runs=" + runs + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Return blueprint with the given ID, live at the given time. Returns null if no such blueprint exists.
   * 
   * @param owner
   *          blueprint owner
   * @param time
   *          time at which the blueprint must be live
   * @param itemID
   *          blueprint itemID
   * @return blueprint with the given ID live at the given time, or null.
   */
  public static Blueprint get(final SynchronizedEveAccount owner, final long time, final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Blueprint>() {
        @Override
        public Blueprint run() throws Exception {
          TypedQuery<Blueprint> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Blueprint.getByItemID", Blueprint.class);
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
   * List blueprints live at a given time.
   * 
   * @param owner
   *          assets owner
   * @param time
   *          time at which blueprints must be live
   * @param maxresults
   *          maximum number of blueprints to retrieve
   * @param contid
   *          itemID (exclusive) from which to start returning results
   * @return a list of blueprints no longer than maxresults with itemID greater than contid
   */
  public static List<Blueprint> getAllBlueprints(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Blueprint.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));

    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Blueprint>>() {
        @Override
        public List<Blueprint> run() throws Exception {
          TypedQuery<Blueprint> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Blueprint.listByItemID", Blueprint.class);
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

}
