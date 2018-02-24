package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_character_sheet_skill_points")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheetSkillPoints.get",
        query = "SELECT c FROM CharacterSheetSkillPoints c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterSheetSkillPoints extends CachedData {
  protected static final Logger log            = Logger.getLogger(CharacterSheetSkillPoints.class.getName());
  private static final byte[]   MASK           = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private long                  totalSkillPoints;
  private int unallocatedSkillPoints;

  @SuppressWarnings("unused")
  protected CharacterSheetSkillPoints() {}

  public CharacterSheetSkillPoints(long totalSkillPoints, int unallocatedSkillPoints) {
    this.totalSkillPoints = totalSkillPoints;
    this.unallocatedSkillPoints = unallocatedSkillPoints;
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
    if (!(sup instanceof CharacterSheetSkillPoints)) return false;
    CharacterSheetSkillPoints other = (CharacterSheetSkillPoints) sup;
    return totalSkillPoints == other.totalSkillPoints && unallocatedSkillPoints == other.unallocatedSkillPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getTotalSkillPoints() {
    return totalSkillPoints;
  }

  public int getUnallocatedSkillPoints() {
    return unallocatedSkillPoints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterSheetSkillPoints that = (CharacterSheetSkillPoints) o;
    return totalSkillPoints == that.totalSkillPoints &&
        unallocatedSkillPoints == that.unallocatedSkillPoints;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), totalSkillPoints, unallocatedSkillPoints);
  }

  @Override
  public String toString() {
    return "CharacterSheetSkillPoints{" +
        "totalSkillPoints=" + totalSkillPoints +
        ", unallocatedSkillPoints=" + unallocatedSkillPoints +
        '}';
  }

  public static CharacterSheetSkillPoints get(
                                       final SynchronizedEveAccount owner,
                                       final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(() -> {
        TypedQuery<CharacterSheetSkillPoints> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSheetSkillPoints.get",
                                                                                                                                  CharacterSheetSkillPoints.class);
        getter.setParameter("owner", owner);
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

  public static List<CharacterSheetSkillPoints> accessQuery(
                                                     final SynchronizedEveAccount owner,
                                                     final long contid,
                                                     final int maxresults,
                                                     final boolean reverse,
                                                     final AttributeSelector at,
                                                     final AttributeSelector totalSkillPoints,
                                                     final AttributeSelector unallocatedSkillPoints) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(() -> {
        StringBuilder qs = new StringBuilder();
        qs.append("SELECT c FROM CharacterSheetSkillPoints c WHERE ");
        // Constrain to specified owner
        qs.append("c.owner = :owner");
        // Constrain lifeline
        AttributeSelector.addLifelineSelector(qs, "c", at);
        // Constrain attributes
        AttributeSelector.addLongSelector(qs, "c", "totalSkillPoints", totalSkillPoints);
        AttributeSelector.addIntSelector(qs, "c", "unallocatedSkillPoints", unallocatedSkillPoints);
        // Set CID constraint and ordering
        setCIDOrdering(qs, contid, reverse);
        // Return result
        TypedQuery<CharacterSheetSkillPoints> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterSheetSkillPoints.class);
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
