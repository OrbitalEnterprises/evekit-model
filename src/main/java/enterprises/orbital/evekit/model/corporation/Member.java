package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
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
    name = "evekit_data_corporation_member",
    indexes = {
        @Index(
            name = "corporationMemberCharacterIDIndex",
            columnList = "characterID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Member.get",
        query = "SELECT c FROM Member c where c.owner = :owner and c.characterID = :character and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Member extends CachedData {
  private static final Logger log = Logger.getLogger(Member.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY);

  private int characterID;

  @SuppressWarnings("unused")
  protected Member() {}

  public Member(int characterID) {
    this.characterID = characterID;
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
    if (!(sup instanceof Member)) return false;
    Member other = (Member) sup;
    return characterID == other.characterID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(characterID);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Member member = (Member) o;
    return characterID == member.characterID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID);
  }

  @Override
  public String toString() {
    return "Member{" +
        "characterID=" + characterID +
        '}';
  }

  public static Member get(
      final SynchronizedEveAccount owner,
      final long time,
      final int characterID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Member> getter = EveKitUserAccountProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createNamedQuery(
                                                                                                 "Member.get",
                                                                                                 Member.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("character", characterID);
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

  public static List<Member> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector characterID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Member c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Member> query = EveKitUserAccountProvider.getFactory()
                                                                                            .getEntityManager()
                                                                                            .createQuery(
                                                                                                qs.toString(),
                                                                                                Member.class);
                                        query.setParameter("owner", owner);
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
