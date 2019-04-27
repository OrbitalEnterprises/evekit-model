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
    name = "evekit_data_corporation_member_title",
    indexes = {
        @Index(
            name = "memberTitleCharacterIDIndex",
            columnList = "characterID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberTitle.get",
        query = "SELECT c FROM MemberTitle c where c.owner = :owner and c.characterID = :character and c.titleID = :title and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MemberTitle extends CachedData {
  private static final Logger log = Logger.getLogger(MemberTitle.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY);

  private int characterID;
  private int titleID;

  @SuppressWarnings("unused")
  protected MemberTitle() {}

  public MemberTitle(int characterID, int titleID) {
    this.characterID = characterID;
    this.titleID = titleID;
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
    if (!(sup instanceof MemberTitle)) return false;
    MemberTitle other = (MemberTitle) sup;
    return characterID == other.characterID && titleID == other.titleID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(characterID, titleID);
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

  public int getTitleID() {
    return titleID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MemberTitle that = (MemberTitle) o;
    return characterID == that.characterID &&
        titleID == that.titleID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID, titleID);
  }

  @Override
  public String toString() {
    return "MemberTitle{" +
        "characterID=" + characterID +
        ", titleID=" + titleID +
        '}';
  }

  public static MemberTitle get(
      final SynchronizedEveAccount owner,
      final long time,
      final int characterID,
      final int titleID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MemberTitle> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "MemberTitle.get",
                                                                                                      MemberTitle.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("character", characterID);
                                        getter.setParameter("title", titleID);
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

  public static List<MemberTitle> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector characterID,
      final AttributeSelector titleID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MemberTitle c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addIntSelector(qs, "c", "titleID", titleID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MemberTitle> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     MemberTitle.class);
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
