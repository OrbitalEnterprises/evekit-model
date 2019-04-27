package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_skill_in_queue",
    indexes = {
        @Index(
            name = "queuePositionIndex",
            columnList = "queuePosition"),
    })
@NamedQueries({
    @NamedQuery(
        name = "SkillInQueue.getByQueuePosition",
        query = "SELECT c FROM SkillInQueue c where c.owner = :owner and c.queuePosition = :qp and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "SkillInQueue.getAtOrAfterPosition",
        query = "SELECT c FROM SkillInQueue c where c.owner = :owner and c.queuePosition >= :qmax and c.lifeStart <= :point and c.lifeEnd > :point order by c.queuePosition asc"),
})
public class SkillInQueue extends CachedData {
  private static final Logger log = Logger.getLogger(SkillInQueue.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_SKILL_QUEUE);

  private int endSP;
  private long endTime = -1;
  private int level;
  private int queuePosition;
  private int startSP;
  private long startTime = -1;
  private int typeID;
  private int trainingStartSP;

  @Transient
  @ApiModelProperty(
      value = "endTime Date")
  @JsonProperty("endTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date endTimeDate;
  @Transient
  @ApiModelProperty(
      value = "startTime Date")
  @JsonProperty("startTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startTimeDate;

  @SuppressWarnings("unused")
  protected SkillInQueue() {}

  public SkillInQueue(int endSP, long endTime, int level, int queuePosition, int startSP, long startTime, int typeID,
                      int trainingStartSP) {
    this.endSP = endSP;
    this.endTime = endTime;
    this.level = level;
    this.queuePosition = queuePosition;
    this.startSP = startSP;
    this.startTime = startTime;
    this.typeID = typeID;
    this.trainingStartSP = trainingStartSP;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    startTimeDate = assignDateField(startTime);
    endTimeDate = assignDateField(endTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof SkillInQueue)) return false;
    SkillInQueue other = (SkillInQueue) sup;
    return endSP == other.endSP && endTime == other.endTime && level == other.level && queuePosition == other.queuePosition && startSP == other.startSP
        && startTime == other.startTime && typeID == other.typeID && trainingStartSP == other.trainingStartSP;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(endSP, endTime, level, queuePosition, startSP, startTime, typeID, trainingStartSP);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getEndSP() {
    return endSP;
  }

  public long getEndTime() {
    return endTime;
  }

  public int getLevel() {
    return level;
  }

  public int getQueuePosition() {
    return queuePosition;
  }

  public int getStartSP() {
    return startSP;
  }

  public long getStartTime() {
    return startTime;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getTrainingStartSP() {
    return trainingStartSP;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SkillInQueue that = (SkillInQueue) o;
    return endSP == that.endSP &&
        endTime == that.endTime &&
        level == that.level &&
        queuePosition == that.queuePosition &&
        startSP == that.startSP &&
        startTime == that.startTime &&
        typeID == that.typeID &&
        trainingStartSP == that.trainingStartSP;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), endSP, endTime, level, queuePosition, startSP, startTime, typeID,
                        trainingStartSP);
  }

  @Override
  public String toString() {
    return "SkillInQueue{" +
        "endSP=" + endSP +
        ", endTime=" + endTime +
        ", level=" + level +
        ", queuePosition=" + queuePosition +
        ", startSP=" + startSP +
        ", startTime=" + startTime +
        ", typeID=" + typeID +
        ", trainingStartSP=" + trainingStartSP +
        ", endTimeDate=" + endTimeDate +
        ", startTimeDate=" + startTimeDate +
        '}';
  }

  public static SkillInQueue get(
      final SynchronizedEveAccount owner,
      final long time,
      final int queuePosition) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<SkillInQueue> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "SkillInQueue.getByQueuePosition",
                                                                                                       SkillInQueue.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("qp", queuePosition);
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

  public static List<SkillInQueue> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector endSP,
      final AttributeSelector endTime,
      final AttributeSelector level,
      final AttributeSelector queuePosition,
      final AttributeSelector startSP,
      final AttributeSelector startTime,
      final AttributeSelector typeID,
      final AttributeSelector trainingStartSP) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM SkillInQueue c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "endSP", endSP);
                                        AttributeSelector.addLongSelector(qs, "c", "endTime", endTime);
                                        AttributeSelector.addIntSelector(qs, "c", "level", level);
                                        AttributeSelector.addIntSelector(qs, "c", "queuePosition", queuePosition);
                                        AttributeSelector.addIntSelector(qs, "c", "startSP", startSP);
                                        AttributeSelector.addLongSelector(qs, "c", "startTime", startTime);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "trainingStartSP", trainingStartSP);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<SkillInQueue> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      SkillInQueue.class);
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
