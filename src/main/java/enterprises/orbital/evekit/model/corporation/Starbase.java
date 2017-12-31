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

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_starbase",
    indexes = {
        @Index(
            name = "itemIDIndex",
            columnList = "itemID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Starbase.getByItemID",
        query = "SELECT c FROM Starbase c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Starbase.getAll",
        query = "SELECT c FROM Starbase c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})

// 7 hour cache time - API caches for 6 hours
public class Starbase extends CachedData {
  private static final Logger log             = Logger.getLogger(Starbase.class.getName());
  private static final byte[] MASK            = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST);
  private long                itemID;
  private long                locationID;
  private int                 moonID;
  private long                onlineTimestamp = -1;
  private int                 state;
  private long                stateTimestamp  = -1;
  private int                 typeID;
  private long                standingOwnerID;
  @Transient
  @ApiModelProperty(
      value = "onlineTimestamp Date")
  @JsonProperty("onlineTimestampDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                onlineTimestampDate;
  @Transient
  @ApiModelProperty(
      value = "stateTimestamp Date")
  @JsonProperty("stateTimestampDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                stateTimestampDate;

  @SuppressWarnings("unused")
  protected Starbase() {}

  public Starbase(long itemID, long locationID, int moonID, long onlineTimestamp, int state, long stateTimestamp, int typeID, long standingOwnerID) {
    super();
    this.itemID = itemID;
    this.locationID = locationID;
    this.moonID = moonID;
    this.onlineTimestamp = onlineTimestamp;
    this.state = state;
    this.stateTimestamp = stateTimestamp;
    this.typeID = typeID;
    this.standingOwnerID = standingOwnerID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    onlineTimestampDate = assignDateField(onlineTimestamp);
    stateTimestampDate = assignDateField(stateTimestamp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Starbase)) return false;
    Starbase other = (Starbase) sup;
    return itemID == other.itemID && locationID == other.locationID && moonID == other.moonID && onlineTimestamp == other.onlineTimestamp
        && state == other.state && stateTimestamp == other.stateTimestamp && typeID == other.typeID && standingOwnerID == other.standingOwnerID;
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

  public int getMoonID() {
    return moonID;
  }

  public long getOnlineTimestamp() {
    return onlineTimestamp;
  }

  public int getState() {
    return state;
  }

  public long getStateTimestamp() {
    return stateTimestamp;
  }

  public int getTypeID() {
    return typeID;
  }

  public long getStandingOwnerID() {
    return standingOwnerID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + moonID;
    result = prime * result + (int) (onlineTimestamp ^ (onlineTimestamp >>> 32));
    result = prime * result + (int) (standingOwnerID ^ (standingOwnerID >>> 32));
    result = prime * result + state;
    result = prime * result + (int) (stateTimestamp ^ (stateTimestamp >>> 32));
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Starbase other = (Starbase) obj;
    if (itemID != other.itemID) return false;
    if (locationID != other.locationID) return false;
    if (moonID != other.moonID) return false;
    if (onlineTimestamp != other.onlineTimestamp) return false;
    if (standingOwnerID != other.standingOwnerID) return false;
    if (state != other.state) return false;
    if (stateTimestamp != other.stateTimestamp) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Starbase [itemID=" + itemID + ", locationID=" + locationID + ", moonID=" + moonID + ", onlineTimestamp=" + onlineTimestamp + ", state=" + state
        + ", stateTimestamp=" + stateTimestamp + ", typeID=" + typeID + ", standingOwnerID=" + standingOwnerID + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static Starbase get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Starbase>() {
        @Override
        public Starbase run() throws Exception {
          TypedQuery<Starbase> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Starbase.getByItemID", Starbase.class);
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

  public static List<Starbase> getAll(
                                      final SynchronizedEveAccount owner,
                                      final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Starbase>>() {
        @Override
        public List<Starbase> run() throws Exception {
          TypedQuery<Starbase> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Starbase.getAll", Starbase.class);
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

  public static List<Starbase> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector itemID,
                                           final AttributeSelector locationID,
                                           final AttributeSelector moonID,
                                           final AttributeSelector onlineTimestamp,
                                           final AttributeSelector state,
                                           final AttributeSelector stateTimestamp,
                                           final AttributeSelector typeID,
                                           final AttributeSelector standingOwnerID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Starbase>>() {
        @Override
        public List<Starbase> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Starbase c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
          AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
          AttributeSelector.addIntSelector(qs, "c", "moonID", moonID);
          AttributeSelector.addLongSelector(qs, "c", "onlineTimestamp", onlineTimestamp);
          AttributeSelector.addIntSelector(qs, "c", "state", state);
          AttributeSelector.addLongSelector(qs, "c", "stateTimestamp", stateTimestamp);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addLongSelector(qs, "c", "standingOwnerID", standingOwnerID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Starbase> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Starbase.class);
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
