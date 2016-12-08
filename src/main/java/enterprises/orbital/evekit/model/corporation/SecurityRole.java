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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_security_role",
    indexes = {
        @Index(
            name = "roleIDIndex",
            columnList = "roleID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "SecurityRole.getByRoleID",
        query = "SELECT c FROM SecurityRole c where c.owner = :owner and c.roleID = :role and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "SecurityRole.getAll",
        query = "SELECT c FROM SecurityRole c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class SecurityRole extends CachedData {
  private static final Logger log  = Logger.getLogger(SecurityRole.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY);
  private long                roleID;
  private String              roleName;

  @SuppressWarnings("unused")
  private SecurityRole() {}

  public SecurityRole(long roleID, String roleName) {
    super();
    this.roleID = roleID;
    this.roleName = roleName;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof SecurityRole)) return false;
    SecurityRole other = (SecurityRole) sup;
    return roleID == other.roleID && nullSafeObjectCompare(roleName, other.roleName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getRoleID() {
    return roleID;
  }

  public String getRoleName() {
    return roleName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (roleID ^ (roleID >>> 32));
    result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SecurityRole other = (SecurityRole) obj;
    if (roleID != other.roleID) return false;
    if (roleName == null) {
      if (other.roleName != null) return false;
    } else if (!roleName.equals(other.roleName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SecurityRole [roleID=" + roleID + ", roleName=" + roleName + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static SecurityRole get(
                                 final SynchronizedEveAccount owner,
                                 final long time,
                                 final long roleID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<SecurityRole>() {
        @Override
        public SecurityRole run() throws Exception {
          TypedQuery<SecurityRole> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("SecurityRole.getByRoleID",
                                                                                                                       SecurityRole.class);
          getter.setParameter("owner", owner);
          getter.setParameter("role", roleID);
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

  public static List<SecurityRole> getAll(
                                          final SynchronizedEveAccount owner,
                                          final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<SecurityRole>>() {
        @Override
        public List<SecurityRole> run() throws Exception {
          TypedQuery<SecurityRole> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("SecurityRole.getAll",
                                                                                                                       SecurityRole.class);
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

  public static List<SecurityRole> accessQuery(
                                               final SynchronizedEveAccount owner,
                                               final long contid,
                                               final int maxresults,
                                               final boolean reverse,
                                               final AttributeSelector at,
                                               final AttributeSelector roleID,
                                               final AttributeSelector roleName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<SecurityRole>>() {
        @Override
        public List<SecurityRole> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM SecurityRole c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "roleID", roleID);
          AttributeSelector.addStringSelector(qs, "c", "roleName", roleName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<SecurityRole> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), SecurityRole.class);
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
