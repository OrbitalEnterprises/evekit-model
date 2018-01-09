package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
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
    name = "evekit_data_research_agent",
    indexes = {
        @Index(
            name = "agentIDIndex",
            columnList = "agentID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "ResearchAgent.getByAgentID",
        query = "SELECT c FROM ResearchAgent c where c.owner = :owner and c.agentID = :aid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ResearchAgent.getAll",
        query = "SELECT c FROM ResearchAgent c where c.owner = :owner and c.agentID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.agentID asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class ResearchAgent extends CachedData {
  private static final Logger log = Logger.getLogger(ResearchAgent.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_RESEARCH);
  private int agentID;
  private double pointsPerDay;
  private double remainderPoints;
  private long researchStartDate = -1;
  private int skillTypeID;
  @Transient
  @ApiModelProperty(
      value = "researchStartDate Date")
  @JsonProperty("researchStartDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date researchStartDateDate;

  @SuppressWarnings("unused")
  protected ResearchAgent() {}

  public ResearchAgent(int agentID, double pointsPerDay, double remainderPoints, long researchStartDate,
                       int skillTypeID) {
    super();
    this.agentID = agentID;
    this.pointsPerDay = pointsPerDay;
    this.remainderPoints = remainderPoints;
    this.researchStartDate = researchStartDate;
    this.skillTypeID = skillTypeID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    researchStartDateDate = assignDateField(researchStartDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof ResearchAgent)) return false;
    ResearchAgent other = (ResearchAgent) sup;
    return agentID == other.agentID && pointsPerDay == other.pointsPerDay && remainderPoints == other.remainderPoints
        && researchStartDate == other.researchStartDate && skillTypeID == other.skillTypeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getAgentID() {
    return agentID;
  }

  public double getPointsPerDay() {
    return pointsPerDay;
  }

  public double getRemainderPoints() {
    return remainderPoints;
  }

  public long getResearchStartDate() {
    return researchStartDate;
  }

  public int getSkillTypeID() {
    return skillTypeID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ResearchAgent that = (ResearchAgent) o;
    return agentID == that.agentID &&
        Double.compare(that.pointsPerDay, pointsPerDay) == 0 &&
        Double.compare(that.remainderPoints, remainderPoints) == 0 &&
        researchStartDate == that.researchStartDate &&
        skillTypeID == that.skillTypeID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), agentID, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
  }

  @Override
  public String toString() {
    return "ResearchAgent{" +
        "agentID=" + agentID +
        ", pointsPerDay=" + pointsPerDay +
        ", remainderPoints=" + remainderPoints +
        ", researchStartDate=" + researchStartDate +
        ", skillTypeID=" + skillTypeID +
        ", researchStartDateDate=" + researchStartDateDate +
        '}';
  }

  public static ResearchAgent get(
      final SynchronizedEveAccount owner,
      final long time,
      final int agentID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ResearchAgent> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery("ResearchAgent.getByAgentID",
                                                                                                                      ResearchAgent.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("aid", agentID);
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

  public static List<ResearchAgent> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector agentID,
      final AttributeSelector pointsPerDay,
      final AttributeSelector remainderPoints,
      final AttributeSelector researchStartDate,
      final AttributeSelector skillTypeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ResearchAgent c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "agentID", agentID);
                                        AttributeSelector.addDoubleSelector(qs, "c", "pointsPerDay", pointsPerDay);
                                        AttributeSelector.addDoubleSelector(qs, "c", "remainderPoints", remainderPoints);
                                        AttributeSelector.addLongSelector(qs, "c", "researchStartDate", researchStartDate);
                                        AttributeSelector.addIntSelector(qs, "c", "skillTypeID", skillTypeID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ResearchAgent> query = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createQuery(qs.toString(), ResearchAgent.class);
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
