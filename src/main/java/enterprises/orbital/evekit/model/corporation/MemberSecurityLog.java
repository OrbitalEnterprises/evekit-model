package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    name = "evekit_data_member_security_log",
    indexes = {
        @Index(
            name = "changeTimeIndex",
            columnList = "changeTime",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberSecurityLog.getByChangeTime",
        query = "SELECT c FROM MemberSecurityLog c where c.owner = :owner and c.changeTime = :change and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "MemberSecurityLog.getAllForward",
        query = "SELECT c FROM MemberSecurityLog c where c.owner = :owner and c.changeTime > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.changeTime asc"),
    @NamedQuery(
        name = "MemberSecurityLog.getAllBackward",
        query = "SELECT c FROM MemberSecurityLog c where c.owner = :owner and c.changeTime < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.changeTime desc"),
})
// 2 hour cache time - API caches for 1 hour
public class MemberSecurityLog extends CachedData {
  private static final Logger log                 = Logger.getLogger(MemberSecurityLog.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                changeTime          = -1;
  private long                changedCharacterID;
  private String              changedCharacterName;
  private long                issuerID;
  private String              issuerName;
  private String              roleLocationType;
  // Collection of SecurityRole role IDs
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           oldRoles            = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           newRoles            = new HashSet<Long>();
  @Transient
  @ApiModelProperty(
      value = "changeTime Date")
  @JsonProperty("changeTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                changeTimeDate;

  @SuppressWarnings("unused")
  private MemberSecurityLog() {}

  public MemberSecurityLog(long changeTime, long changedCharacterID, String changedCharacterName, long issuerID, String issuerName, String roleLocationType) {
    super();
    this.changeTime = changeTime;
    this.changedCharacterID = changedCharacterID;
    this.changedCharacterName = changedCharacterName;
    this.issuerID = issuerID;
    this.issuerName = issuerName;
    this.roleLocationType = roleLocationType;
    this.oldRoles = new HashSet<Long>();
    this.newRoles = new HashSet<Long>();
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    changeTimeDate = assignDateField(changeTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof MemberSecurityLog)) return false;
    MemberSecurityLog other = (MemberSecurityLog) sup;
    return changeTime == other.changeTime && changedCharacterID == other.changedCharacterID
        && nullSafeObjectCompare(changedCharacterName, other.changedCharacterName) && issuerID == other.issuerID
        && nullSafeObjectCompare(issuerName, other.issuerName) && nullSafeObjectCompare(roleLocationType, other.roleLocationType)
        && nullSafeObjectCompare(oldRoles, other.oldRoles) && nullSafeObjectCompare(newRoles, other.newRoles);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getChangeTime() {
    return changeTime;
  }

  public long getChangedCharacterID() {
    return changedCharacterID;
  }

  public String getChangedCharacterName() {
    return changedCharacterName;
  }

  public long getIssuerID() {
    return issuerID;
  }

  public String getIssuerName() {
    return issuerName;
  }

  public String getRoleLocationType() {
    return roleLocationType;
  }

  public Set<Long> getOldRoles() {
    return oldRoles;
  }

  public Set<Long> getNewRoles() {
    return newRoles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (changeTime ^ (changeTime >>> 32));
    result = prime * result + (int) (changedCharacterID ^ (changedCharacterID >>> 32));
    result = prime * result + ((changedCharacterName == null) ? 0 : changedCharacterName.hashCode());
    result = prime * result + (int) (issuerID ^ (issuerID >>> 32));
    result = prime * result + ((issuerName == null) ? 0 : issuerName.hashCode());
    result = prime * result + ((newRoles == null) ? 0 : newRoles.hashCode());
    result = prime * result + ((oldRoles == null) ? 0 : oldRoles.hashCode());
    result = prime * result + ((roleLocationType == null) ? 0 : roleLocationType.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MemberSecurityLog other = (MemberSecurityLog) obj;
    if (changeTime != other.changeTime) return false;
    if (changedCharacterID != other.changedCharacterID) return false;
    if (changedCharacterName == null) {
      if (other.changedCharacterName != null) return false;
    } else if (!changedCharacterName.equals(other.changedCharacterName)) return false;
    if (issuerID != other.issuerID) return false;
    if (issuerName == null) {
      if (other.issuerName != null) return false;
    } else if (!issuerName.equals(other.issuerName)) return false;
    if (newRoles == null) {
      if (other.newRoles != null) return false;
    } else if (!newRoles.equals(other.newRoles)) return false;
    if (oldRoles == null) {
      if (other.oldRoles != null) return false;
    } else if (!oldRoles.equals(other.oldRoles)) return false;
    if (roleLocationType == null) {
      if (other.roleLocationType != null) return false;
    } else if (!roleLocationType.equals(other.roleLocationType)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MemberSecurityLog [changeTime=" + changeTime + ", changedCharacterID=" + changedCharacterID + ", changedCharacterName=" + changedCharacterName
        + ", issuerID=" + issuerID + ", issuerName=" + issuerName + ", roleLocationType=" + roleLocationType + ", oldRoles=" + oldRoles + ", newRoles="
        + newRoles + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static MemberSecurityLog get(
                                      final SynchronizedEveAccount owner,
                                      final long time,
                                      final long changeTime) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<MemberSecurityLog>() {
        @Override
        public MemberSecurityLog run() throws Exception {
          TypedQuery<MemberSecurityLog> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberSecurityLog.getByChangeTime",
                                                                                                                            MemberSecurityLog.class);
          getter.setParameter("owner", owner);
          getter.setParameter("change", changeTime);
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

  public static List<MemberSecurityLog> getAllForward(
                                                      final SynchronizedEveAccount owner,
                                                      final long time,
                                                      int maxresults,
                                                      final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MemberSecurityLog.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberSecurityLog>>() {
        @Override
        public List<MemberSecurityLog> run() throws Exception {
          TypedQuery<MemberSecurityLog> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberSecurityLog.getAllForward",
                                                                                                                            MemberSecurityLog.class);
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

  public static List<MemberSecurityLog> getAllBackward(
                                                       final SynchronizedEveAccount owner,
                                                       final long time,
                                                       int maxresults,
                                                       final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MemberSecurityLog.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberSecurityLog>>() {
        @Override
        public List<MemberSecurityLog> run() throws Exception {
          TypedQuery<MemberSecurityLog> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberSecurityLog.getAllBackward",
                                                                                                                            MemberSecurityLog.class);
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

  public static List<MemberSecurityLog> accessQuery(
                                                    final SynchronizedEveAccount owner,
                                                    final long contid,
                                                    final int maxresults,
                                                    final boolean reverse,
                                                    final AttributeSelector at,
                                                    final AttributeSelector changeTime,
                                                    final AttributeSelector changedCharacterID,
                                                    final AttributeSelector changedCharacterName,
                                                    final AttributeSelector issuerID,
                                                    final AttributeSelector issuerName,
                                                    final AttributeSelector roleLocationType,
                                                    final AttributeSelector oldRoles,
                                                    final AttributeSelector newRoles) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberSecurityLog>>() {
        @Override
        public List<MemberSecurityLog> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MemberSecurityLog c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "changeTime", changeTime);
          AttributeSelector.addLongSelector(qs, "c", "changedCharacterID", changedCharacterID);
          AttributeSelector.addStringSelector(qs, "c", "changedCharacterName", changedCharacterName, p);
          AttributeSelector.addLongSelector(qs, "c", "issuerID", issuerID);
          AttributeSelector.addStringSelector(qs, "c", "issuerName", issuerName, p);
          AttributeSelector.addStringSelector(qs, "c", "roleLocationType", roleLocationType, p);
          AttributeSelector.addSetLongSelector(qs, "c", "oldRoles", oldRoles);
          AttributeSelector.addSetLongSelector(qs, "c", "newRoles", newRoles);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MemberSecurityLog> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), MemberSecurityLog.class);
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
