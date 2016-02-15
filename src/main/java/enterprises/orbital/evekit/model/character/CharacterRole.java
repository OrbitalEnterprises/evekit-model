package enterprises.orbital.evekit.model.character;

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
@Table(name = "evekit_data_character_role", indexes = {
    @Index(name = "roleCategoryIndex", columnList = "roleCategory", unique = false), @Index(name = "roleIDIndex", columnList = "roleID", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "CharacterRole.getByCategoryAndRoleID",
        query = "SELECT c FROM CharacterRole c where c.owner = :owner and c.roleCategory = :cat and c.roleID = :rid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterRole.getAll",
        query = "SELECT c FROM CharacterRole c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterRole extends CachedData {
  private static final Logger log  = Logger.getLogger(CharacterRole.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  private String              roleCategory;
  private long                roleID;
  private String              roleName;

  @SuppressWarnings("unused")
  private CharacterRole() {}

  public CharacterRole(String roleCategory, long roleID, String roleName) {
    super();
    this.roleCategory = roleCategory;
    this.roleID = roleID;
    this.roleName = roleName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof CharacterRole)) return false;
    CharacterRole other = (CharacterRole) sup;
    return nullSafeObjectCompare(roleCategory, other.roleCategory) && roleID == other.roleID && nullSafeObjectCompare(roleName, other.roleName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getRoleCategory() {
    return roleCategory;
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
    result = prime * result + ((roleCategory == null) ? 0 : roleCategory.hashCode());
    result = prime * result + (int) (roleID ^ (roleID >>> 32));
    result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterRole other = (CharacterRole) obj;
    if (roleCategory == null) {
      if (other.roleCategory != null) return false;
    } else if (!roleCategory.equals(other.roleCategory)) return false;
    if (roleID != other.roleID) return false;
    if (roleName == null) {
      if (other.roleName != null) return false;
    } else if (!roleName.equals(other.roleName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterRole [roleCategory=" + roleCategory + ", roleID=" + roleID + ", roleName=" + roleName + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterRole get(final SynchronizedEveAccount owner, final long time, final String roleCategory, final long roleID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterRole>() {
        @Override
        public CharacterRole run() throws Exception {
          TypedQuery<CharacterRole> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterRole.getByCategoryAndRoleID",
                                                                                                                        CharacterRole.class);
          getter.setParameter("owner", owner);
          getter.setParameter("cat", roleCategory);
          getter.setParameter("rid", roleID);
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

  public static List<CharacterRole> getAllRoles(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterRole>>() {
        @Override
        public List<CharacterRole> run() throws Exception {
          TypedQuery<CharacterRole> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterRole.getAll",
                                                                                                                        CharacterRole.class);
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
