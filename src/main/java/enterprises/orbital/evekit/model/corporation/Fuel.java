package enterprises.orbital.evekit.model.corporation;

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

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_fuel", indexes = {
    @Index(name = "itemIDIndex", columnList = "itemID", unique = false), @Index(name = "typeIDIndex", columnList = "typeID", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "Fuel.getByItemAndTypeID",
        query = "SELECT c FROM Fuel c where c.owner = :owner and c.itemID = :item and c.typeID = :type and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Fuel.getAllByItemID",
        query = "SELECT c FROM Fuel c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "Fuel.getAll",
        query = "SELECT c FROM Fuel c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class Fuel extends CachedData {
  private static final Logger log  = Logger.getLogger(Fuel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST);
  private long                itemID;
  private int                 typeID;
  private int                 quantity;

  @SuppressWarnings("unused")
  private Fuel() {}

  public Fuel(long itemID, int typeID, int quantity) {
    super();
    this.itemID = itemID;
    this.typeID = typeID;
    this.quantity = quantity;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof Fuel)) return false;
    Fuel other = (Fuel) sup;
    return itemID == other.itemID && typeID == other.typeID && quantity == other.quantity;
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

  public int getTypeID() {
    return typeID;
  }

  public int getQuantity() {
    return quantity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + quantity;
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Fuel other = (Fuel) obj;
    if (itemID != other.itemID) return false;
    if (quantity != other.quantity) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Fuel [itemID=" + itemID + ", typeID=" + typeID + ", quantity=" + quantity + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd
        + "]";
  }

  public static Fuel get(final SynchronizedEveAccount owner, final long time, final long itemID, final int typeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Fuel>() {
        @Override
        public Fuel run() throws Exception {
          TypedQuery<Fuel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Fuel.getByItemAndTypeID", Fuel.class);
          getter.setParameter("owner", owner);
          getter.setParameter("item", itemID);
          getter.setParameter("type", typeID);
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

  public static List<Fuel> getAllByItemID(final SynchronizedEveAccount owner, final long time, final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Fuel>>() {
        @Override
        public List<Fuel> run() throws Exception {
          TypedQuery<Fuel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Fuel.getAllByItemID", Fuel.class);
          getter.setParameter("owner", owner);
          getter.setParameter("item", itemID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<Fuel> getAll(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Fuel>>() {
        @Override
        public List<Fuel> run() throws Exception {
          TypedQuery<Fuel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Fuel.getAll", Fuel.class);
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

}
