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
@Table(name = "evekit_data_starbase_detail", indexes = {
    @Index(name = "itemIDIndex", columnList = "itemID", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "StarbaseDetail.getByItemID",
        query = "SELECT c FROM StarbaseDetail c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "StarbaseDetail.getAll",
        query = "SELECT c FROM StarbaseDetail c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class StarbaseDetail extends CachedData {
  private static final Logger log  = Logger.getLogger(StarbaseDetail.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST);
  private long                itemID;
  private int                 state;
  private long                stateTimestamp;
  private long                onlineTimestamp;
  private int                 usageFlags;
  private int                 deployFlags;
  private boolean             allowAllianceMembers;
  private boolean             allowCorporationMembers;
  private long                useStandingsFrom;
  private boolean             onAggressionEnabled;
  private int                 onAggressionStanding;
  private boolean             onCorporationWarEnabled;
  private int                 onCorporationWarStanding;
  private boolean             onStandingDropEnabled;
  private int                 onStandingDropStanding;
  private boolean             onStatusDropEnabled;
  private int                 onStatusDropStanding;

  @SuppressWarnings("unused")
  private StarbaseDetail() {}

  public StarbaseDetail(long itemID, int state, long stateTimestamp, long onlineTimestamp, int usageFlags, int deployFlags, boolean allowAllianceMembers,
                        boolean allowCorporationMembers, long useStandingsFrom, boolean onAggressionEnabled, int onAggressionStanding,
                        boolean onCorporationWarEnabled, int onCorporationWarStanding, boolean onStandingDropEnabled, int onStandingDropStanding,
                        boolean onStatusDropEnabled, int onStatusDropStanding) {
    super();
    this.itemID = itemID;
    this.state = state;
    this.stateTimestamp = stateTimestamp;
    this.onlineTimestamp = onlineTimestamp;
    this.usageFlags = usageFlags;
    this.deployFlags = deployFlags;
    this.allowAllianceMembers = allowAllianceMembers;
    this.allowCorporationMembers = allowCorporationMembers;
    this.useStandingsFrom = useStandingsFrom;
    this.onAggressionEnabled = onAggressionEnabled;
    this.onAggressionStanding = onAggressionStanding;
    this.onCorporationWarEnabled = onCorporationWarEnabled;
    this.onCorporationWarStanding = onCorporationWarStanding;
    this.onStandingDropEnabled = onStandingDropEnabled;
    this.onStandingDropStanding = onStandingDropStanding;
    this.onStatusDropEnabled = onStatusDropEnabled;
    this.onStatusDropStanding = onStatusDropStanding;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof StarbaseDetail)) return false;
    StarbaseDetail other = (StarbaseDetail) sup;
    return itemID == other.itemID && state == other.state && stateTimestamp == other.stateTimestamp && onlineTimestamp == other.onlineTimestamp
        && usageFlags == other.usageFlags && deployFlags == other.deployFlags && allowAllianceMembers == other.allowAllianceMembers
        && allowCorporationMembers == other.allowCorporationMembers && useStandingsFrom == other.useStandingsFrom
        && onAggressionEnabled == other.onAggressionEnabled && onAggressionStanding == other.onAggressionStanding
        && onCorporationWarEnabled == other.onCorporationWarEnabled && onCorporationWarStanding == other.onCorporationWarStanding
        && onStandingDropEnabled == other.onStandingDropEnabled && onStandingDropStanding == other.onStandingDropStanding
        && onStatusDropEnabled == other.onStatusDropEnabled && onStatusDropStanding == other.onStatusDropStanding;
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

  public int getState() {
    return state;
  }

  public long getStateTimestamp() {
    return stateTimestamp;
  }

  public long getOnlineTimestamp() {
    return onlineTimestamp;
  }

  public int getUsageFlags() {
    return usageFlags;
  }

  public int getDeployFlags() {
    return deployFlags;
  }

  public boolean isAllowAllianceMembers() {
    return allowAllianceMembers;
  }

  public boolean isAllowCorporationMembers() {
    return allowCorporationMembers;
  }

  public long getUseStandingsFrom() {
    return useStandingsFrom;
  }

  public boolean isOnAggressionEnabled() {
    return onAggressionEnabled;
  }

  public int getOnAggressionStanding() {
    return onAggressionStanding;
  }

  public boolean isOnCorporationWarEnabled() {
    return onCorporationWarEnabled;
  }

  public int getOnCorporationWarStanding() {
    return onCorporationWarStanding;
  }

  public boolean isOnStandingDropEnabled() {
    return onStandingDropEnabled;
  }

  public int getOnStandingDropStanding() {
    return onStandingDropStanding;
  }

  public boolean isOnStatusDropEnabled() {
    return onStatusDropEnabled;
  }

  public int getOnStatusDropStanding() {
    return onStatusDropStanding;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (allowAllianceMembers ? 1231 : 1237);
    result = prime * result + (allowCorporationMembers ? 1231 : 1237);
    result = prime * result + deployFlags;
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + (onAggressionEnabled ? 1231 : 1237);
    result = prime * result + onAggressionStanding;
    result = prime * result + (onCorporationWarEnabled ? 1231 : 1237);
    result = prime * result + onCorporationWarStanding;
    result = prime * result + (onStandingDropEnabled ? 1231 : 1237);
    result = prime * result + onStandingDropStanding;
    result = prime * result + (onStatusDropEnabled ? 1231 : 1237);
    result = prime * result + onStatusDropStanding;
    result = prime * result + (int) (onlineTimestamp ^ (onlineTimestamp >>> 32));
    result = prime * result + state;
    result = prime * result + (int) (stateTimestamp ^ (stateTimestamp >>> 32));
    result = prime * result + usageFlags;
    result = prime * result + (int) (useStandingsFrom ^ (useStandingsFrom >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    StarbaseDetail other = (StarbaseDetail) obj;
    if (allowAllianceMembers != other.allowAllianceMembers) return false;
    if (allowCorporationMembers != other.allowCorporationMembers) return false;
    if (deployFlags != other.deployFlags) return false;
    if (itemID != other.itemID) return false;
    if (onAggressionEnabled != other.onAggressionEnabled) return false;
    if (onAggressionStanding != other.onAggressionStanding) return false;
    if (onCorporationWarEnabled != other.onCorporationWarEnabled) return false;
    if (onCorporationWarStanding != other.onCorporationWarStanding) return false;
    if (onStandingDropEnabled != other.onStandingDropEnabled) return false;
    if (onStandingDropStanding != other.onStandingDropStanding) return false;
    if (onStatusDropEnabled != other.onStatusDropEnabled) return false;
    if (onStatusDropStanding != other.onStatusDropStanding) return false;
    if (onlineTimestamp != other.onlineTimestamp) return false;
    if (state != other.state) return false;
    if (stateTimestamp != other.stateTimestamp) return false;
    if (usageFlags != other.usageFlags) return false;
    if (useStandingsFrom != other.useStandingsFrom) return false;
    return true;
  }

  @Override
  public String toString() {
    return "StarbaseDetail [itemID=" + itemID + ", state=" + state + ", stateTimestamp=" + stateTimestamp + ", onlineTimestamp=" + onlineTimestamp
        + ", usageFlags=" + usageFlags + ", deployFlags=" + deployFlags + ", allowAllianceMembers=" + allowAllianceMembers + ", allowCorporationMembers="
        + allowCorporationMembers + ", useStandingsFrom=" + useStandingsFrom + ", onAggressionEnabled=" + onAggressionEnabled + ", onAggressionStanding="
        + onAggressionStanding + ", onCorporationWarEnabled=" + onCorporationWarEnabled + ", onCorporationWarStanding=" + onCorporationWarStanding
        + ", onStandingDropEnabled=" + onStandingDropEnabled + ", onStandingDropStanding=" + onStandingDropStanding + ", onStatusDropEnabled="
        + onStatusDropEnabled + ", onStatusDropStanding=" + onStatusDropStanding + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd
        + "]";
  }

  public static StarbaseDetail get(final SynchronizedEveAccount owner, final long time, final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<StarbaseDetail>() {
        @Override
        public StarbaseDetail run() throws Exception {
          TypedQuery<StarbaseDetail> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("StarbaseDetail.getByItemID",
                                                                                                                         StarbaseDetail.class);
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

  public static List<StarbaseDetail> getAll(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<StarbaseDetail>>() {
        @Override
        public List<StarbaseDetail> run() throws Exception {
          TypedQuery<StarbaseDetail> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("StarbaseDetail.getAll",
                                                                                                                         StarbaseDetail.class);
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
