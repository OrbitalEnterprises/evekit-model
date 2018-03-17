package enterprises.orbital.evekit.model.character;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_character_role",
    indexes = {
        @Index(
            name = "roleCategoryIndex",
            columnList = "roleCategory"),
        @Index(
            name = "roleNameIndex",
            columnList = "roleName"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterRole.getByCategoryAndName",
        query = "SELECT c FROM CharacterRole c where c.owner = :owner and c.roleCategory = :cat and c.roleName = :name and c.lifeStart <= :point and c.lifeEnd > :point")
})
public class CharacterRole extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterRole.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  // Injected role categories
  public static final String CAT_CORPORATION = "CORPORATION";
  public static final String CAT_CORPORATION_AT_HQ = "CORPORATION_AT_HQ";
  public static final String CAT_CORPORATION_AT_BASE = "CORPORATION_AT_BASE";
  public static final String CAT_CORPORATION_AT_OTHER = "CORPORATION_AT_OTHER";

  private String roleCategory;
  private String roleName;

  @SuppressWarnings("unused")
  protected CharacterRole() {}

  public CharacterRole(String roleCategory, String roleName) {
    super();
    this.roleCategory = roleCategory;
    this.roleName = roleName;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterRole)) return false;
    CharacterRole other = (CharacterRole) sup;
    return nullSafeObjectCompare(roleCategory, other.roleCategory) && nullSafeObjectCompare(roleName, other.roleName);
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

  public String getRoleName() {
    return roleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterRole that = (CharacterRole) o;
    return Objects.equals(roleCategory, that.roleCategory) &&
        Objects.equals(roleName, that.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), roleCategory, roleName);
  }

  @Override
  public String toString() {
    return "CharacterRole{" +
        "roleCategory='" + roleCategory + '\'' +
        ", roleName='" + roleName + '\'' +
        '}';
  }

  public static CharacterRole get(
      final SynchronizedEveAccount owner,
      final long time,
      final String roleCategory,
      final String roleName) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterRole> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(
                                                                                                        "CharacterRole.getByCategoryAndName",
                                                                                                        CharacterRole.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("cat", roleCategory);
                                        getter.setParameter("name", roleName);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<CharacterRole> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector roleCategory,
      final AttributeSelector roleName) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(new RunInTransaction<List<CharacterRole>>() {
                                        @Override
                                        public List<CharacterRole> run() throws Exception {
                                          StringBuilder qs = new StringBuilder();
                                          qs.append("SELECT c FROM CharacterRole c WHERE ");
                                          // Constrain to specified owner
                                          qs.append("c.owner = :owner");
                                          // Constrain lifeline
                                          AttributeSelector.addLifelineSelector(qs, "c", at);
                                          // Constrain attributes
                                          AttributeParameters p = new AttributeParameters("att");
                                          AttributeSelector.addStringSelector(qs, "c", "roleCategory", roleCategory, p);
                                          AttributeSelector.addStringSelector(qs, "c", "roleName", roleName, p);
                                          // Set CID constraint and ordering
                                          setCIDOrdering(qs, contid, reverse);
                                          // Return result
                                          TypedQuery<CharacterRole> query = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createQuery(
                                                                                                         qs.toString(),
                                                                                                         CharacterRole.class);
                                          query.setParameter("owner", owner);
                                          p.fillParams(query);
                                          query.setMaxResults(maxresults);
                                          return query.getResultList();
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
