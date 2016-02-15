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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_container_log", indexes = {
    @Index(name = "logTimeIndex", columnList = "logTime", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "ContainerLog.getByLogTime",
        query = "SELECT c FROM ContainerLog c where c.owner = :owner and c.logTime = :time and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ContainerLog.getByLogTimeForward",
        query = "SELECT c FROM ContainerLog c where c.owner = :owner and c.logTime > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.logTime asc"),
    @NamedQuery(
        name = "ContainerLog.getByLogTimeBackward",
        query = "SELECT c FROM ContainerLog c where c.owner = :owner and c.logTime < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.logTime desc"),
})
// 4 hour cache time - API caches for 3 hours
public class ContainerLog extends CachedData {
  private static final Logger log                 = Logger.getLogger(ContainerLog.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTAINER_LOG);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                logTime             = -1;
  private String              action;
  private long                actorID;
  private String              actorName;
  private int                 flag;
  private long                itemID;
  private long                itemTypeID;
  private int                 locationID;
  private int                 newConfiguration;
  private int                 oldConfiguration;
  private String              passwordType;
  private int                 quantity;
  private long                typeID;

  @SuppressWarnings("unused")
  private ContainerLog() {}

  public ContainerLog(long logTime, String action, long actorID, String actorName, int flag, long itemID, long itemTypeID, int locationID, int newConfiguration,
                      int oldConfiguration, String passwordType, int quantity, long typeID) {
    super();
    this.logTime = logTime;
    this.action = action;
    this.actorID = actorID;
    this.actorName = actorName;
    this.flag = flag;
    this.itemID = itemID;
    this.itemTypeID = itemTypeID;
    this.locationID = locationID;
    this.newConfiguration = newConfiguration;
    this.oldConfiguration = oldConfiguration;
    this.passwordType = passwordType;
    this.quantity = quantity;
    this.typeID = typeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof ContainerLog)) return false;
    ContainerLog other = (ContainerLog) sup;
    return logTime == other.logTime && nullSafeObjectCompare(action, other.action) && actorID == other.actorID
        && nullSafeObjectCompare(actorName, other.actorName) && flag == other.flag && itemID == other.itemID && itemTypeID == other.itemTypeID
        && locationID == other.locationID && newConfiguration == other.newConfiguration && oldConfiguration == other.oldConfiguration
        && nullSafeObjectCompare(passwordType, other.passwordType) && quantity == other.quantity && typeID == other.typeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getLogTime() {
    return logTime;
  }

  public String getAction() {
    return action;
  }

  public long getActorID() {
    return actorID;
  }

  public String getActorName() {
    return actorName;
  }

  public int getFlag() {
    return flag;
  }

  public long getItemID() {
    return itemID;
  }

  public long getItemTypeID() {
    return itemTypeID;
  }

  public int getLocationID() {
    return locationID;
  }

  public int getNewConfiguration() {
    return newConfiguration;
  }

  public int getOldConfiguration() {
    return oldConfiguration;
  }

  public String getPasswordType() {
    return passwordType;
  }

  public int getQuantity() {
    return quantity;
  }

  public long getTypeID() {
    return typeID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((action == null) ? 0 : action.hashCode());
    result = prime * result + (int) (actorID ^ (actorID >>> 32));
    result = prime * result + ((actorName == null) ? 0 : actorName.hashCode());
    result = prime * result + flag;
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + (int) (itemTypeID ^ (itemTypeID >>> 32));
    result = prime * result + locationID;
    result = prime * result + (int) (logTime ^ (logTime >>> 32));
    result = prime * result + newConfiguration;
    result = prime * result + oldConfiguration;
    result = prime * result + ((passwordType == null) ? 0 : passwordType.hashCode());
    result = prime * result + quantity;
    result = prime * result + (int) (typeID ^ (typeID >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ContainerLog other = (ContainerLog) obj;
    if (action == null) {
      if (other.action != null) return false;
    } else if (!action.equals(other.action)) return false;
    if (actorID != other.actorID) return false;
    if (actorName == null) {
      if (other.actorName != null) return false;
    } else if (!actorName.equals(other.actorName)) return false;
    if (flag != other.flag) return false;
    if (itemID != other.itemID) return false;
    if (itemTypeID != other.itemTypeID) return false;
    if (locationID != other.locationID) return false;
    if (logTime != other.logTime) return false;
    if (newConfiguration != other.newConfiguration) return false;
    if (oldConfiguration != other.oldConfiguration) return false;
    if (passwordType == null) {
      if (other.passwordType != null) return false;
    } else if (!passwordType.equals(other.passwordType)) return false;
    if (quantity != other.quantity) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ContainerLog [logTime=" + logTime + ", action=" + action + ", actorID=" + actorID + ", actorName=" + actorName + ", flag=" + flag + ", itemID="
        + itemID + ", itemTypeID=" + itemTypeID + ", locationID=" + locationID + ", newConfiguration=" + newConfiguration + ", oldConfiguration="
        + oldConfiguration + ", passwordType=" + passwordType + ", quantity=" + quantity + ", typeID=" + typeID + ", owner=" + owner + ", lifeStart="
        + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static ContainerLog get(final SynchronizedEveAccount owner, final long time, final long logTime) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ContainerLog>() {
        @Override
        public ContainerLog run() throws Exception {
          TypedQuery<ContainerLog> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContainerLog.getByLogTime",
                                                                                                                       ContainerLog.class);
          getter.setParameter("owner", owner);
          getter.setParameter("time", logTime);
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

  public static List<ContainerLog> getAllForward(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(ContainerLog.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContainerLog>>() {
        @Override
        public List<ContainerLog> run() throws Exception {
          TypedQuery<ContainerLog> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContainerLog.getByLogTimeForward",
                                                                                                                       ContainerLog.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
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

  public static List<ContainerLog> getAllBackward(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(ContainerLog.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContainerLog>>() {
        @Override
        public List<ContainerLog> run() throws Exception {
          TypedQuery<ContainerLog> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContainerLog.getByLogTimeBackward",
                                                                                                                       ContainerLog.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
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
