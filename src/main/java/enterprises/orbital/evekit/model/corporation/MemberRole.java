package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_corporation_member_role",
    indexes = {
        @Index(
            name = "corporationMemberRoleCharacterIDIndex",
            columnList = "characterID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberRole.get",
        query = "SELECT c FROM MemberRole c where c.owner = :owner and c.characterID = :character and c.roleName = :rolename and c.grantable = :grantable and c.atHQ = :athq and c.atBase = :atbase and c.atOther = :atother and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MemberRole extends CachedData {
  private static final Logger log = Logger.getLogger(MemberRole.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY);

  private int characterID;
  private String roleName;
  private boolean grantable;
  private boolean atHQ;
  private boolean atBase;
  private boolean atOther;

  @SuppressWarnings("unused")
  protected MemberRole() {}

  public MemberRole(int characterID, String roleName, boolean grantable, boolean atHQ, boolean atBase,
                    boolean atOther) {
    this.characterID = characterID;
    this.roleName = roleName;
    this.grantable = grantable;
    this.atHQ = atHQ;
    this.atBase = atBase;
    this.atOther = atOther;
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
    if (!(sup instanceof MemberRole)) return false;
    MemberRole other = (MemberRole) sup;
    return characterID == other.characterID &&
        nullSafeObjectCompare(roleName, other.roleName) &&
        grantable == other.grantable &&
        atHQ == other.atHQ &&
        atBase == other.atBase &&
        atOther == other.atOther;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(characterID, roleName, grantable, atHQ, atBase, atOther);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getCharacterID() {
    return characterID;
  }

  public String getRoleName() {
    return roleName;
  }

  public boolean isGrantable() {
    return grantable;
  }

  public boolean isAtHQ() {
    return atHQ;
  }

  public boolean isAtBase() {
    return atBase;
  }

  public boolean isAtOther() {
    return atOther;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MemberRole that = (MemberRole) o;
    return characterID == that.characterID &&
        grantable == that.grantable &&
        atHQ == that.atHQ &&
        atBase == that.atBase &&
        atOther == that.atOther &&
        Objects.equals(roleName, that.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID, roleName, grantable, atHQ, atBase, atOther);
  }

  @Override
  public String toString() {
    return "MemberRole{" +
        "characterID=" + characterID +
        ", roleName='" + roleName + '\'' +
        ", grantable=" + grantable +
        ", atHQ=" + atHQ +
        ", atBase=" + atBase +
        ", atOther=" + atOther +
        '}';
  }

  public static MemberRole get(
      final SynchronizedEveAccount owner,
      final long time,
      final int characterID,
      final String roleName,
      final boolean grantable,
      final boolean atHQ,
      final boolean atBase,
      final boolean atOther) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MemberRole> getter = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createNamedQuery(
                                                                                                     "MemberRole.get",
                                                                                                     MemberRole.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("character", characterID);
                                        getter.setParameter("rolename", roleName);
                                        getter.setParameter("grantable", grantable);
                                        getter.setParameter("athq", atHQ);
                                        getter.setParameter("atbase", atBase);
                                        getter.setParameter("atother", atOther);
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

  public static List<MemberRole> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector characterID,
      final AttributeSelector roleName,
      final AttributeSelector grantable,
      final AttributeSelector atHQ,
      final AttributeSelector atBase,
      final AttributeSelector atOther) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MemberRole c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addStringSelector(qs, "c", "roleName", roleName, p);
                                        AttributeSelector.addBooleanSelector(qs, "c", "grantable", grantable);
                                        AttributeSelector.addBooleanSelector(qs, "c", "atHQ", atHQ);
                                        AttributeSelector.addBooleanSelector(qs, "c", "atBase", atBase);
                                        AttributeSelector.addBooleanSelector(qs, "c", "atOther", atOther);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MemberRole> query = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createQuery(
                                                                                                    qs.toString(),
                                                                                                    MemberRole.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
