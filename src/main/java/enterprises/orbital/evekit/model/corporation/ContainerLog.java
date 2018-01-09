package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_container_log",
    indexes = {
        @Index(
            name = "logTimeIndex",
            columnList = "logTime",
            unique = false),
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
  private int                 itemTypeID;
  private long                locationID;
  private String              newConfiguration;
  private String              oldConfiguration;
  private String              passwordType;
  private long                quantity;
  private int                 typeID;
  @Transient
  @ApiModelProperty(
      value = "logTime Date")
  @JsonProperty("logTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                logTimeDate;

  @SuppressWarnings("unused")
  protected ContainerLog() {}

  public ContainerLog(long logTime, String action, long actorID, String actorName, int flag, long itemID, int itemTypeID, long locationID,
                      String newConfiguration, String oldConfiguration, String passwordType, long quantity, int typeID) {
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
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    logTimeDate = assignDateField(logTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
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

  public int getItemTypeID() {
    return itemTypeID;
  }

  public long getLocationID() {
    return locationID;
  }

  public String getNewConfiguration() {
    return newConfiguration;
  }

  public String getOldConfiguration() {
    return oldConfiguration;
  }

  public String getPasswordType() {
    return passwordType;
  }

  public long getQuantity() {
    return quantity;
  }

  public int getTypeID() {
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
    result = prime * result + itemTypeID;
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + (int) (logTime ^ (logTime >>> 32));
    result = prime * result + ((newConfiguration == null) ? 0 : newConfiguration.hashCode());
    result = prime * result + ((oldConfiguration == null) ? 0 : oldConfiguration.hashCode());
    result = prime * result + ((passwordType == null) ? 0 : passwordType.hashCode());
    result = prime * result + (int) (quantity ^ (quantity >>> 32));
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
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
    if (newConfiguration == null) {
      if (other.newConfiguration != null) return false;
    } else if (!newConfiguration.equals(other.newConfiguration)) return false;
    if (oldConfiguration == null) {
      if (other.oldConfiguration != null) return false;
    } else if (!oldConfiguration.equals(other.oldConfiguration)) return false;
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

  public static ContainerLog get(
                                 final SynchronizedEveAccount owner,
                                 final long time,
                                 final long logTime) {
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

  public static List<ContainerLog> getAllForward(
                                                 final SynchronizedEveAccount owner,
                                                 final long time,
                                                 int maxresults,
                                                 final long contid) {
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

  public static List<ContainerLog> getAllBackward(
                                                  final SynchronizedEveAccount owner,
                                                  final long time,
                                                  int maxresults,
                                                  final long contid) {
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

  public static List<ContainerLog> accessQuery(
                                               final SynchronizedEveAccount owner,
                                               final long contid,
                                               final int maxresults,
                                               final boolean reverse,
                                               final AttributeSelector at,
                                               final AttributeSelector logTime,
                                               final AttributeSelector action,
                                               final AttributeSelector actorID,
                                               final AttributeSelector actorName,
                                               final AttributeSelector flag,
                                               final AttributeSelector itemID,
                                               final AttributeSelector itemTypeID,
                                               final AttributeSelector locationID,
                                               final AttributeSelector newConfiguration,
                                               final AttributeSelector oldConfiguration,
                                               final AttributeSelector passwordType,
                                               final AttributeSelector quantity,
                                               final AttributeSelector typeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContainerLog>>() {
        @Override
        public List<ContainerLog> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ContainerLog c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "logTime", logTime);
          AttributeSelector.addStringSelector(qs, "c", "action", action, p);
          AttributeSelector.addLongSelector(qs, "c", "actorID", actorID);
          AttributeSelector.addStringSelector(qs, "c", "actorName", actorName, p);
          AttributeSelector.addIntSelector(qs, "c", "flag", flag);
          AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
          AttributeSelector.addIntSelector(qs, "c", "itemTypeID", itemTypeID);
          AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
          AttributeSelector.addStringSelector(qs, "c", "newConfiguration", newConfiguration, p);
          AttributeSelector.addStringSelector(qs, "c", "oldConfiguration", oldConfiguration, p);
          AttributeSelector.addStringSelector(qs, "c", "passwordType", passwordType, p);
          AttributeSelector.addLongSelector(qs, "c", "quantity", quantity);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<ContainerLog> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), ContainerLog.class);
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
