package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_character_title",
    indexes = {
        @Index(
            name = "titleIDIndex",
            columnList = "titleID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterTitle.getByTitleID",
        query = "SELECT c FROM CharacterTitle c where c.owner = :owner and c.titleID = :title and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterTitle extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterTitle.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int titleID;
  private String titleName;

  @SuppressWarnings("unused")
  protected CharacterTitle() {}

  public CharacterTitle(int titleID, String titleName) {
    super();
    this.titleID = titleID;
    this.titleName = titleName;
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
    if (!(sup instanceof CharacterTitle)) return false;
    CharacterTitle other = (CharacterTitle) sup;
    return titleID == other.titleID && nullSafeObjectCompare(titleName, other.titleName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getTitleID() {
    return titleID;
  }

  public String getTitleName() {
    return titleName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterTitle that = (CharacterTitle) o;
    return titleID == that.titleID &&
        Objects.equals(titleName, that.titleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), titleID, titleName);
  }

  @Override
  public String toString() {
    return "CharacterTitle{" +
        "titleID=" + titleID +
        ", titleName='" + titleName + '\'' +
        '}';
  }

  public static CharacterTitle get(
      final SynchronizedEveAccount owner,
      final long time,
      final int titleID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterTitle> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "CharacterTitle.getByTitleID",
                                                                                                         CharacterTitle.class);
                                        getter.setParameter("owner", owner);
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

  public static List<CharacterTitle> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector titleID,
      final AttributeSelector titleName) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterTitle c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "titleID", titleID);
                                        AttributeSelector.addStringSelector(qs, "c", "titleName", titleName, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterTitle> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        CharacterTitle.class);
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
