package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
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
@Table(
    name = "evekit_data_role",
    indexes = {
        @Index(
            name = "roleIDIndex",
            columnList = "roleID",
            unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "Role.getByRoleID",
        query = "SELECT c FROM Role c where c.owner = :owner and c.roleID = :role and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Role.getAll",
        query = "SELECT c FROM Role c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class Role extends CachedData {
  private static final Logger log  = Logger.getLogger(Role.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_TITLES);
  private long                roleID;
  @Lob
  @Column(
      length = 102400)
  private String              roleDescription;
  private String              roleName;

  @SuppressWarnings("unused")
  private Role() {}

  public Role(long roleID, String roleDescription, String roleName) {
    super();
    this.roleID = roleID;
    this.roleDescription = roleDescription;
    this.roleName = roleName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Role)) return false;
    Role other = (Role) sup;
    return roleID == other.roleID && nullSafeObjectCompare(roleDescription, other.roleDescription) && nullSafeObjectCompare(roleName, other.roleName);
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

  public String getRoleDescription() {
    return roleDescription;
  }

  public String getRoleName() {
    return roleName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((roleDescription == null) ? 0 : roleDescription.hashCode());
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
    Role other = (Role) obj;
    if (roleDescription == null) {
      if (other.roleDescription != null) return false;
    } else if (!roleDescription.equals(other.roleDescription)) return false;
    if (roleID != other.roleID) return false;
    if (roleName == null) {
      if (other.roleName != null) return false;
    } else if (!roleName.equals(other.roleName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Role [roleID=" + roleID + ", roleDescription=" + roleDescription + ", roleName=" + roleName + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static Role get(
                         final SynchronizedEveAccount owner,
                         final long time,
                         final long roleID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Role>() {
        @Override
        public Role run() throws Exception {
          TypedQuery<Role> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Role.getByRoleID", Role.class);
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

  public static List<Role> getAll(
                                  final SynchronizedEveAccount owner,
                                  final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Role>>() {
        @Override
        public List<Role> run() throws Exception {
          TypedQuery<Role> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Role.getAll", Role.class);
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
