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
    name = "evekit_data_member_tracking",
    indexes = {
        @Index(
            name = "characterIDIndex",
            columnList = "characterID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberTracking.getByCharacterID",
        query = "SELECT c FROM MemberTracking c where c.owner = :owner and c.characterID = :char and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "MemberTracking.getAll",
        query = "SELECT c FROM MemberTracking c where c.owner = :owner and c.characterID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.characterID asc"),
})
// 7 hour cache time - API caches for 6 hour
public class MemberTracking extends CachedData {
  private static final Logger log                 = Logger.getLogger(MemberSecurity.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_TRACKING);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                characterID;
  private String              base;
  private long                baseID;
  private long                grantableRoles;                                                                              // bit mask?
  private String              location;
  private long                locationID;
  private long                logoffDateTime      = -1;
  private long                logonDateTime       = -1;
  private String              name;
  private long                roles;                                                                                       // bit mask?
  private String              shipType;
  private int                 shipTypeID;
  private long                startDateTime       = -1;
  private String              title;
  @Transient
  @ApiModelProperty(
      value = "logoffDateTime Date")
  @JsonProperty("logoffDateTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                logoffDateTimeDate;
  @Transient
  @ApiModelProperty(
      value = "logonDateTime Date")
  @JsonProperty("logonDateTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                logonDateTimeDate;
  @Transient
  @ApiModelProperty(
      value = "startDateTime Date")
  @JsonProperty("startDateTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                startDateTimeDate;

  @SuppressWarnings("unused")
  protected MemberTracking() {}

  public MemberTracking(long characterID, String base, long baseID, long grantableRoles, String location, long locationID, long logoffDateTime,
                        long logonDateTime, String name, long roles, String shipType, int shipTypeID, long startDateTime, String title) {
    super();
    this.characterID = characterID;
    this.base = base;
    this.baseID = baseID;
    this.grantableRoles = grantableRoles;
    this.location = location;
    this.locationID = locationID;
    this.logoffDateTime = logoffDateTime;
    this.logonDateTime = logonDateTime;
    this.name = name;
    this.roles = roles;
    this.shipType = shipType;
    this.shipTypeID = shipTypeID;
    this.startDateTime = startDateTime;
    this.title = title;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    logoffDateTimeDate = assignDateField(logoffDateTime);
    logonDateTimeDate = assignDateField(logonDateTime);
    startDateTimeDate = assignDateField(startDateTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof MemberTracking)) return false;
    MemberTracking other = (MemberTracking) sup;
    return characterID == other.characterID && nullSafeObjectCompare(base, other.base) && baseID == other.baseID && grantableRoles == other.grantableRoles
        && nullSafeObjectCompare(location, other.location) && locationID == other.locationID && logoffDateTime == other.logoffDateTime
        && logonDateTime == other.logonDateTime && nullSafeObjectCompare(name, other.name) && roles == other.roles
        && nullSafeObjectCompare(shipType, other.shipType) && shipTypeID == other.shipTypeID && startDateTime == other.startDateTime
        && nullSafeObjectCompare(title, other.title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getCharacterID() {
    return characterID;
  }

  public String getBase() {
    return base;
  }

  public long getBaseID() {
    return baseID;
  }

  public long getGrantableRoles() {
    return grantableRoles;
  }

  public String getLocation() {
    return location;
  }

  public long getLocationID() {
    return locationID;
  }

  public long getLogoffDateTime() {
    return logoffDateTime;
  }

  public long getLogonDateTime() {
    return logonDateTime;
  }

  public String getName() {
    return name;
  }

  public long getRoles() {
    return roles;
  }

  public String getShipType() {
    return shipType;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  public long getStartDateTime() {
    return startDateTime;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((base == null) ? 0 : base.hashCode());
    result = prime * result + (int) (baseID ^ (baseID >>> 32));
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + (int) (grantableRoles ^ (grantableRoles >>> 32));
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + (int) (logoffDateTime ^ (logoffDateTime >>> 32));
    result = prime * result + (int) (logonDateTime ^ (logonDateTime >>> 32));
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (int) (roles ^ (roles >>> 32));
    result = prime * result + ((shipType == null) ? 0 : shipType.hashCode());
    result = prime * result + shipTypeID;
    result = prime * result + (int) (startDateTime ^ (startDateTime >>> 32));
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MemberTracking other = (MemberTracking) obj;
    if (base == null) {
      if (other.base != null) return false;
    } else if (!base.equals(other.base)) return false;
    if (baseID != other.baseID) return false;
    if (characterID != other.characterID) return false;
    if (grantableRoles != other.grantableRoles) return false;
    if (location == null) {
      if (other.location != null) return false;
    } else if (!location.equals(other.location)) return false;
    if (locationID != other.locationID) return false;
    if (logoffDateTime != other.logoffDateTime) return false;
    if (logonDateTime != other.logonDateTime) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (roles != other.roles) return false;
    if (shipType == null) {
      if (other.shipType != null) return false;
    } else if (!shipType.equals(other.shipType)) return false;
    if (shipTypeID != other.shipTypeID) return false;
    if (startDateTime != other.startDateTime) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MemberTracking [characterID=" + characterID + ", base=" + base + ", baseID=" + baseID + ", grantableRoles=" + grantableRoles + ", location="
        + location + ", locationID=" + locationID + ", logoffDateTime=" + logoffDateTime + ", logonDateTime=" + logonDateTime + ", name=" + name + ", roles="
        + roles + ", shipType=" + shipType + ", shipTypeID=" + shipTypeID + ", startDateTime=" + startDateTime + ", title=" + title + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static MemberTracking get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final long characterID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<MemberTracking>() {
        @Override
        public MemberTracking run() throws Exception {
          TypedQuery<MemberTracking> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberTracking.getByCharacterID",
                                                                                                                         MemberTracking.class);
          getter.setParameter("owner", owner);
          getter.setParameter("char", characterID);
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

  public static List<MemberTracking> getAll(
                                            final SynchronizedEveAccount owner,
                                            final long time,
                                            int maxresults,
                                            final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MemberTracking.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberTracking>>() {
        @Override
        public List<MemberTracking> run() throws Exception {
          TypedQuery<MemberTracking> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberTracking.getAll",
                                                                                                                         MemberTracking.class);
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

  public static List<MemberTracking> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector characterID,
                                                 final AttributeSelector base,
                                                 final AttributeSelector baseID,
                                                 final AttributeSelector grantableRoles,
                                                 final AttributeSelector location,
                                                 final AttributeSelector locationID,
                                                 final AttributeSelector logoffDateTime,
                                                 final AttributeSelector logonDateTime,
                                                 final AttributeSelector name,
                                                 final AttributeSelector roles,
                                                 final AttributeSelector shipType,
                                                 final AttributeSelector shipTypeID,
                                                 final AttributeSelector startDateTime,
                                                 final AttributeSelector title) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberTracking>>() {
        @Override
        public List<MemberTracking> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MemberTracking c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "characterID", characterID);
          AttributeSelector.addStringSelector(qs, "c", "base", base, p);
          AttributeSelector.addLongSelector(qs, "c", "baseID", baseID);
          AttributeSelector.addLongSelector(qs, "c", "grantableRoles", grantableRoles);
          AttributeSelector.addStringSelector(qs, "c", "location", location, p);
          AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
          AttributeSelector.addLongSelector(qs, "c", "logoffDateTime", logoffDateTime);
          AttributeSelector.addLongSelector(qs, "c", "logonDateTime", logonDateTime);
          AttributeSelector.addStringSelector(qs, "c", "name", name, p);
          AttributeSelector.addLongSelector(qs, "c", "roles", roles);
          AttributeSelector.addStringSelector(qs, "c", "shipType", shipType, p);
          AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
          AttributeSelector.addLongSelector(qs, "c", "startDateTime", startDateTime);
          AttributeSelector.addStringSelector(qs, "c", "title", title, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MemberTracking> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), MemberTracking.class);
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
