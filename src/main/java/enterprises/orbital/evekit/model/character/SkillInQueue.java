package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
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
    name = "evekit_data_skill_in_queue",
    indexes = {
        @Index(
            name = "queuePositionIndex",
            columnList = "queuePosition",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "SkillInQueue.getByQueuePosition",
        query = "SELECT c FROM SkillInQueue c where c.owner = :owner and c.queuePosition = :qp and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "SkillInQueue.getAtOrAfterPosition",
        query = "SELECT c FROM SkillInQueue c where c.owner = :owner and c.queuePosition >= :qmax and c.lifeStart <= :point and c.lifeEnd > :point order by c.queuePosition asc"),
})
// 2 hour cache time - API caches for 1 hour
public class SkillInQueue extends CachedData {
  private static final Logger log       = Logger.getLogger(SkillInQueue.class.getName());
  private static final byte[] MASK      = AccountAccessMask.createMask(AccountAccessMask.ACCESS_SKILL_QUEUE);
  private int                 endSP;
  private long                endTime   = -1;
  private int                 level;
  private int                 queuePosition;
  private int                 startSP;
  private long                startTime = -1;
  private int                 typeID;
  @Transient
  @ApiModelProperty(
      value = "endTime Date")
  @JsonProperty("endTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                endTimeDate;
  @Transient
  @ApiModelProperty(
      value = "startTime Date")
  @JsonProperty("startTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                startTimeDate;

  @SuppressWarnings("unused")
  protected SkillInQueue() {}

  public SkillInQueue(int endSP, long endTime, int level, int queuePosition, int startSP, long startTime, int typeID) {
    super();
    this.endSP = endSP;
    this.endTime = endTime;
    this.level = level;
    this.queuePosition = queuePosition;
    this.startSP = startSP;
    this.startTime = startTime;
    this.typeID = typeID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
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
        && startTime == other.startTime && typeID == other.typeID;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + endSP;
    result = prime * result + (int) (endTime ^ (endTime >>> 32));
    result = prime * result + level;
    result = prime * result + queuePosition;
    result = prime * result + startSP;
    result = prime * result + (int) (startTime ^ (startTime >>> 32));
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SkillInQueue other = (SkillInQueue) obj;
    if (endSP != other.endSP) return false;
    if (endTime != other.endTime) return false;
    if (level != other.level) return false;
    if (queuePosition != other.queuePosition) return false;
    if (startSP != other.startSP) return false;
    if (startTime != other.startTime) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SkillInQueue [endSP=" + endSP + ", endTime=" + endTime + ", level=" + level + ", queuePosition=" + queuePosition + ", startSP=" + startSP
        + ", startTime=" + startTime + ", typeID=" + typeID + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static SkillInQueue get(
                                 final SynchronizedEveAccount owner,
                                 final long time,
                                 final int queuePosition) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<SkillInQueue>() {
        @Override
        public SkillInQueue run() throws Exception {
          TypedQuery<SkillInQueue> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("SkillInQueue.getByQueuePosition",
                                                                                                                       SkillInQueue.class);
          getter.setParameter("owner", owner);
          getter.setParameter("qp", queuePosition);
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

  public static List<SkillInQueue> getAtOrAfterPosition(
                                                        final SynchronizedEveAccount owner,
                                                        final long time,
                                                        final int maxPosition) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<SkillInQueue>>() {
        @Override
        public List<SkillInQueue> run() throws Exception {
          TypedQuery<SkillInQueue> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("SkillInQueue.getAtOrAfterPosition",
                                                                                                                       SkillInQueue.class);
          getter.setParameter("owner", owner);
          getter.setParameter("qmax", maxPosition);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
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
                                               final AttributeSelector typeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<SkillInQueue>>() {
        @Override
        public List<SkillInQueue> run() throws Exception {
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
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<SkillInQueue> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), SkillInQueue.class);
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
