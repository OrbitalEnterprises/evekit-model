package enterprises.orbital.evekit.model.character;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
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
@Table(name = "evekit_data_character_skill_in_training")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSkillInTraining.get",
        query = "SELECT c FROM CharacterSkillInTraining c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterSkillInTraining extends CachedData {
  protected static final Logger log                      = Logger.getLogger(CharacterSkillInTraining.class.getName());
  private static final byte[]   MASK                     = AccountAccessMask.createMask(AccountAccessMask.ACCESS_SKILL_IN_TRAINING);
  private boolean               skillInTraining;
  private long                  currentTrainingQueueTime = -1;
  private long                  trainingStartTime        = -1;
  private long                  trainingEndTime          = -1;
  private int                   trainingStartSP;
  private int                   trainingDestinationSP;
  private int                   trainingToLevel;
  private int                   skillTypeID;

  @SuppressWarnings("unused")
  private CharacterSkillInTraining() {}

  public CharacterSkillInTraining(boolean skillInTraining, long currentTrainingQueueTime, long trainingStartTime, long trainingEndTime, int trainingStartSP,
                                  int trainingDestinationSP, int trainingToLevel, int skillTypeID) {
    super();
    this.skillInTraining = skillInTraining;
    this.currentTrainingQueueTime = currentTrainingQueueTime;
    this.trainingStartTime = trainingStartTime;
    this.trainingEndTime = trainingEndTime;
    this.trainingStartSP = trainingStartSP;
    this.trainingDestinationSP = trainingDestinationSP;
    this.trainingToLevel = trainingToLevel;
    this.skillTypeID = skillTypeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof CharacterSkillInTraining)) return false;
    CharacterSkillInTraining other = (CharacterSkillInTraining) sup;
    return skillInTraining == other.skillInTraining && currentTrainingQueueTime == other.currentTrainingQueueTime
        && trainingStartTime == other.trainingStartTime && trainingEndTime == other.trainingEndTime && trainingStartSP == other.trainingStartSP
        && trainingDestinationSP == other.trainingDestinationSP && trainingToLevel == other.trainingToLevel && skillTypeID == other.skillTypeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public boolean isSkillInTraining() {
    return skillInTraining;
  }

  public long getCurrentTrainingQueueTime() {
    return currentTrainingQueueTime;
  }

  public long getTrainingStartTime() {
    return trainingStartTime;
  }

  public long getTrainingEndTime() {
    return trainingEndTime;
  }

  public int getTrainingStartSP() {
    return trainingStartSP;
  }

  public int getTrainingDestinationSP() {
    return trainingDestinationSP;
  }

  public int getTrainingToLevel() {
    return trainingToLevel;
  }

  public long getSkillTypeID() {
    return skillTypeID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (currentTrainingQueueTime ^ (currentTrainingQueueTime >>> 32));
    result = prime * result + (skillInTraining ? 1231 : 1237);
    result = prime * result + (skillTypeID ^ (skillTypeID >>> 32));
    result = prime * result + trainingDestinationSP;
    result = prime * result + (int) (trainingEndTime ^ (trainingEndTime >>> 32));
    result = prime * result + trainingStartSP;
    result = prime * result + (int) (trainingStartTime ^ (trainingStartTime >>> 32));
    result = prime * result + trainingToLevel;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterSkillInTraining other = (CharacterSkillInTraining) obj;
    if (currentTrainingQueueTime != other.currentTrainingQueueTime) return false;
    if (skillInTraining != other.skillInTraining) return false;
    if (skillTypeID != other.skillTypeID) return false;
    if (trainingDestinationSP != other.trainingDestinationSP) return false;
    if (trainingEndTime != other.trainingEndTime) return false;
    if (trainingStartSP != other.trainingStartSP) return false;
    if (trainingStartTime != other.trainingStartTime) return false;
    if (trainingToLevel != other.trainingToLevel) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterSkillInTraining [skillInTraining=" + skillInTraining + ", currentTrainingQueueTime=" + currentTrainingQueueTime + ", trainingStartTime="
        + trainingStartTime + ", trainingEndTime=" + trainingEndTime + ", trainingStartSP=" + trainingStartSP + ", trainingDestinationSP="
        + trainingDestinationSP + ", trainingToLevel=" + trainingToLevel + ", skillTypeID=" + skillTypeID + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterSkillInTraining get(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterSkillInTraining>() {
        @Override
        public CharacterSkillInTraining run() throws Exception {
          TypedQuery<CharacterSkillInTraining> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterSkillInTraining.get", CharacterSkillInTraining.class);
          getter.setParameter("owner", owner);
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
}
