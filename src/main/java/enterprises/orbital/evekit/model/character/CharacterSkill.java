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
    name = "evekit_data_character_skill",
    indexes = {
        @Index(
            name = "typeIDIndex",
            columnList = "typeID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterSkill.getByTypeID",
        query = "SELECT c FROM CharacterSkill c where c.owner = :owner and c.typeID = :type and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterSkill.getAll",
        query = "SELECT c FROM CharacterSkill c where c.owner = :owner and c.typeID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.typeID asc"),
})
public class CharacterSkill extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterSkill.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int typeID;
  private int trainedSkillLevel;
  private long skillpoints;
  private int activeSkillLevel;

  @SuppressWarnings("unused")
  protected CharacterSkill() {}

  public CharacterSkill(int typeID, int trainedSkillLevel, long skillpoints, int activeSkillLevel) {
    this.typeID = typeID;
    this.trainedSkillLevel = trainedSkillLevel;
    this.skillpoints = skillpoints;
    this.activeSkillLevel = activeSkillLevel;
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
    if (!(sup instanceof CharacterSkill)) return false;
    CharacterSkill other = (CharacterSkill) sup;
    return typeID == other.typeID &&
        trainedSkillLevel == other.trainedSkillLevel &&
        skillpoints == other.skillpoints &&
        activeSkillLevel == other.activeSkillLevel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getTrainedSkillLevel() {
    return trainedSkillLevel;
  }

  public long getSkillpoints() {
    return skillpoints;
  }

  public int getActiveSkillLevel() {
    return activeSkillLevel;
  }

  @Override
  public String toString() {
    return "CharacterSkill{" +
        "typeID=" + typeID +
        ", trainedSkillLevel=" + trainedSkillLevel +
        ", skillpoints=" + skillpoints +
        ", activeSkillLevel=" + activeSkillLevel +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterSkill that = (CharacterSkill) o;
    return typeID == that.typeID &&
        trainedSkillLevel == that.trainedSkillLevel &&
        skillpoints == that.skillpoints &&
        activeSkillLevel == that.activeSkillLevel;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), typeID, trainedSkillLevel, skillpoints, activeSkillLevel);
  }

  public static CharacterSkill get(
      final SynchronizedEveAccount owner,
      final long time,
      final int typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterSkill> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "CharacterSkill.getByTypeID",
                                                                                                         CharacterSkill.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("type", typeID);
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

  public static List<CharacterSkill> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector typeID,
      final AttributeSelector trainedSkillLevel,
      final AttributeSelector skillpoints,
      final AttributeSelector activeSkillLevel) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterSkill c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "trainedSkillLevel",
                                                                         trainedSkillLevel);
                                        AttributeSelector.addLongSelector(qs, "c", "skillpoints", skillpoints);
                                        AttributeSelector.addIntSelector(qs, "c", "activeSkillLevel", activeSkillLevel);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterSkill> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        CharacterSkill.class);
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
