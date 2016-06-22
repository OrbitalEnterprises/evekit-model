package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

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
  private static final Logger log                 = Logger.getLogger(ResearchAgent.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_RESEARCH);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private int                 agentID;
  private double              currentPoints;
  private double              pointsPerDay;
  private double              remainderPoints;
  private long                researchStartDate   = -1;
  private int                 skillTypeID;

  @SuppressWarnings("unused")
  private ResearchAgent() {}

  public ResearchAgent(int agentID, double currentPoints, double pointsPerDay, double remainderPoints, long researchStartDate, int skillTypeID) {
    super();
    this.agentID = agentID;
    this.currentPoints = currentPoints;
    this.pointsPerDay = pointsPerDay;
    this.remainderPoints = remainderPoints;
    this.researchStartDate = researchStartDate;
    this.skillTypeID = skillTypeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof ResearchAgent)) return false;
    ResearchAgent other = (ResearchAgent) sup;
    return agentID == other.agentID && currentPoints == other.currentPoints && pointsPerDay == other.pointsPerDay && remainderPoints == other.remainderPoints
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

  public double getCurrentPoints() {
    return currentPoints;
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + agentID;
    long temp;
    temp = Double.doubleToLongBits(currentPoints);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(pointsPerDay);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(remainderPoints);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + (int) (researchStartDate ^ (researchStartDate >>> 32));
    result = prime * result + skillTypeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ResearchAgent other = (ResearchAgent) obj;
    if (agentID != other.agentID) return false;
    if (Double.doubleToLongBits(currentPoints) != Double.doubleToLongBits(other.currentPoints)) return false;
    if (Double.doubleToLongBits(pointsPerDay) != Double.doubleToLongBits(other.pointsPerDay)) return false;
    if (Double.doubleToLongBits(remainderPoints) != Double.doubleToLongBits(other.remainderPoints)) return false;
    if (researchStartDate != other.researchStartDate) return false;
    if (skillTypeID != other.skillTypeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ResearchAgent [agentID=" + agentID + ", currentPoints=" + currentPoints + ", pointsPerDay=" + pointsPerDay + ", remainderPoints=" + remainderPoints
        + ", researchStartDate=" + researchStartDate + ", skillTypeID=" + skillTypeID + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd
        + "]";
  }

  public static ResearchAgent get(
                                  final SynchronizedEveAccount owner,
                                  final long time,
                                  final int agentID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ResearchAgent>() {
        @Override
        public ResearchAgent run() throws Exception {
          TypedQuery<ResearchAgent> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ResearchAgent.getByAgentID",
                                                                                                                        ResearchAgent.class);
          getter.setParameter("owner", owner);
          getter.setParameter("aid", agentID);
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

  public static List<ResearchAgent> getAllAgents(
                                                 final SynchronizedEveAccount owner,
                                                 final long time,
                                                 int maxresults,
                                                 final int contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(ResearchAgent.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ResearchAgent>>() {
        @Override
        public List<ResearchAgent> run() throws Exception {
          TypedQuery<ResearchAgent> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ResearchAgent.getAll",
                                                                                                                        ResearchAgent.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<ResearchAgent> accessQuery(
                                                final SynchronizedEveAccount owner,
                                                final long contid,
                                                final int maxresults,
                                                final boolean reverse,
                                                final AttributeSelector at,
                                                final AttributeSelector agentID,
                                                final AttributeSelector currentPoints,
                                                final AttributeSelector pointsPerDay,
                                                final AttributeSelector remainderPoints,
                                                final AttributeSelector researchStartDate,
                                                final AttributeSelector skillTypeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ResearchAgent>>() {
        @Override
        public List<ResearchAgent> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ResearchAgent c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "agentID", agentID);
          AttributeSelector.addDoubleSelector(qs, "c", "currentPoints", currentPoints);
          AttributeSelector.addDoubleSelector(qs, "c", "pointsPerDay", pointsPerDay);
          AttributeSelector.addDoubleSelector(qs, "c", "remainderPoints", remainderPoints);
          AttributeSelector.addLongSelector(qs, "c", "researchStartDate", researchStartDate);
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
          TypedQuery<ResearchAgent> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), ResearchAgent.class);
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
