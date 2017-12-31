package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_character_skill_in_training")
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
  @Transient
  @ApiModelProperty(
      value = "currentTrainingQueueTime Date")
  @JsonProperty("currentTrainingQueueTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  currentTrainingQueueTimeDate;
  @Transient
  @ApiModelProperty(
      value = "trainingStartTime Date")
  @JsonProperty("trainingStartTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  trainingStartTimeDate;
  @Transient
  @ApiModelProperty(
      value = "trainingEndTime Date")
  @JsonProperty("trainingEndTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  trainingEndTimeDate;

  @SuppressWarnings("unused")
  protected CharacterSkillInTraining() {}

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
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    currentTrainingQueueTimeDate = assignDateField(currentTrainingQueueTime);
    trainingStartTimeDate = assignDateField(trainingStartTime);
    trainingEndTimeDate = assignDateField(trainingEndTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
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
  public boolean equals(
                        Object obj) {
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

  public static CharacterSkillInTraining get(
                                             final SynchronizedEveAccount owner,
                                             final long time) {
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

  public static List<CharacterSkillInTraining> accessQuery(
                                                           final SynchronizedEveAccount owner,
                                                           final long contid,
                                                           final int maxresults,
                                                           final boolean reverse,
                                                           final AttributeSelector at,
                                                           final AttributeSelector skillInTraining,
                                                           final AttributeSelector currentTrainingQueueTime,
                                                           final AttributeSelector trainingStartTime,
                                                           final AttributeSelector trainingEndTime,
                                                           final AttributeSelector trainingStartSP,
                                                           final AttributeSelector trainingDestinationSP,
                                                           final AttributeSelector trainingToLevel,
                                                           final AttributeSelector skillTypeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterSkillInTraining>>() {
        @Override
        public List<CharacterSkillInTraining> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterSkillInTraining c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addBooleanSelector(qs, "c", "skillInTraining", skillInTraining);
          AttributeSelector.addLongSelector(qs, "c", "currentTrainingQueueTime", currentTrainingQueueTime);
          AttributeSelector.addLongSelector(qs, "c", "trainingStartTime", trainingStartTime);
          AttributeSelector.addLongSelector(qs, "c", "trainingEndTime", trainingEndTime);
          AttributeSelector.addIntSelector(qs, "c", "trainingStartSP", trainingStartSP);
          AttributeSelector.addIntSelector(qs, "c", "trainingDestinationSP", trainingDestinationSP);
          AttributeSelector.addIntSelector(qs, "c", "trainingToLevel", trainingToLevel);
          AttributeSelector.addIntSelector(qs, "c", "skillTypeID", skillTypeID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterSkillInTraining> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                             CharacterSkillInTraining.class);
          query.setParameter("owner", owner);
          query.setMaxResults(maxresults);
          return query.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
