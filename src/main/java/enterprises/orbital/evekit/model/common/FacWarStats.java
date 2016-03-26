package enterprises.orbital.evekit.model.common;

import java.util.Collections;
import java.util.List;
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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_facwarstats")
@NamedQueries({
    @NamedQuery(
        name = "FacWarStats.get",
        query = "SELECT c FROM FacWarStats c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class FacWarStats extends CachedData {
  private static final Logger log  = Logger.getLogger(FacWarStats.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_FAC_WAR_STATS);
  private int                 currentRank;
  private long                enlisted;
  private int                 factionID;
  private String              factionName;
  private int                 highestRank;
  private int                 killsLastWeek;
  private int                 killsTotal;
  private int                 killsYesterday;
  private int                 pilots;
  private int                 victoryPointsLastWeek;
  private int                 victoryPointsTotal;
  private int                 victoryPointsYesterday;

  @SuppressWarnings("unused")
  private FacWarStats() {}

  public FacWarStats(int currentRank, long enlisted, int factionID, String factionName, int highestRank, int killsLastWeek, int killsTotal, int killsYesterday,
                     int pilots, int victoryPointsLastWeek, int victoryPointsTotal, int victoryPointsYesterday) {
    super();
    this.currentRank = currentRank;
    this.enlisted = enlisted;
    this.factionID = factionID;
    this.factionName = factionName;
    this.highestRank = highestRank;
    this.killsLastWeek = killsLastWeek;
    this.killsTotal = killsTotal;
    this.killsYesterday = killsYesterday;
    this.pilots = pilots;
    this.victoryPointsLastWeek = victoryPointsLastWeek;
    this.victoryPointsTotal = victoryPointsTotal;
    this.victoryPointsYesterday = victoryPointsYesterday;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof FacWarStats)) return false;
    FacWarStats other = (FacWarStats) sup;
    return currentRank == other.currentRank && enlisted == other.enlisted && factionID == other.factionID
        && nullSafeObjectCompare(factionName, other.factionName) && highestRank == other.highestRank && killsLastWeek == other.killsLastWeek
        && killsTotal == other.killsTotal && killsYesterday == other.killsYesterday && pilots == other.pilots
        && victoryPointsLastWeek == other.victoryPointsLastWeek && victoryPointsTotal == other.victoryPointsTotal
        && victoryPointsYesterday == other.victoryPointsYesterday;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getCurrentRank() {
    return currentRank;
  }

  public long getEnlisted() {
    return enlisted;
  }

  public int getFactionID() {
    return factionID;
  }

  public String getFactionName() {
    return factionName;
  }

  public int getHighestRank() {
    return highestRank;
  }

  public int getKillsLastWeek() {
    return killsLastWeek;
  }

  public int getKillsTotal() {
    return killsTotal;
  }

  public int getKillsYesterday() {
    return killsYesterday;
  }

  public int getPilots() {
    return pilots;
  }

  public int getVictoryPointsLastWeek() {
    return victoryPointsLastWeek;
  }

  public int getVictoryPointsTotal() {
    return victoryPointsTotal;
  }

  public int getVictoryPointsYesterday() {
    return victoryPointsYesterday;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + currentRank;
    result = prime * result + (int) (enlisted ^ (enlisted >>> 32));
    result = prime * result + factionID;
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    result = prime * result + highestRank;
    result = prime * result + killsLastWeek;
    result = prime * result + killsTotal;
    result = prime * result + killsYesterday;
    result = prime * result + pilots;
    result = prime * result + victoryPointsLastWeek;
    result = prime * result + victoryPointsTotal;
    result = prime * result + victoryPointsYesterday;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    FacWarStats other = (FacWarStats) obj;
    if (currentRank != other.currentRank) return false;
    if (enlisted != other.enlisted) return false;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    if (highestRank != other.highestRank) return false;
    if (killsLastWeek != other.killsLastWeek) return false;
    if (killsTotal != other.killsTotal) return false;
    if (killsYesterday != other.killsYesterday) return false;
    if (pilots != other.pilots) return false;
    if (victoryPointsLastWeek != other.victoryPointsLastWeek) return false;
    if (victoryPointsTotal != other.victoryPointsTotal) return false;
    if (victoryPointsYesterday != other.victoryPointsYesterday) return false;
    return true;
  }

  @Override
  public String toString() {
    return "FacWarStats [currentRank=" + currentRank + ", enlisted=" + enlisted + ", factionID=" + factionID + ", factionName=" + factionName + ", highestRank="
        + highestRank + ", killsLastWeek=" + killsLastWeek + ", killsTotal=" + killsTotal + ", killsYesterday=" + killsYesterday + ", pilots=" + pilots
        + ", victoryPointsLastWeek=" + victoryPointsLastWeek + ", victoryPointsTotal=" + victoryPointsTotal + ", victoryPointsYesterday="
        + victoryPointsYesterday + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve FacWarStats live at the given time, or null if no such entity exists.
   * 
   * @param owner
   *          stats owner
   * @param time
   *          time at which stats must be live
   * @return stats instance live at the given time, or null
   */
  public static FacWarStats get(
                                final SynchronizedEveAccount owner,
                                final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<FacWarStats>() {
        @Override
        public FacWarStats run() throws Exception {
          TypedQuery<FacWarStats> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("FacWarStats.get", FacWarStats.class);
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

  public static List<FacWarStats> accessQuery(
                                              final SynchronizedEveAccount owner,
                                              final long contid,
                                              final int maxresults,
                                              final AttributeSelector at,
                                              final AttributeSelector currentRank,
                                              final AttributeSelector enlisted,
                                              final AttributeSelector factionID,
                                              final AttributeSelector factionName,
                                              final AttributeSelector highestRank,
                                              final AttributeSelector killsLastWeek,
                                              final AttributeSelector killsTotal,
                                              final AttributeSelector killsYesterday,
                                              final AttributeSelector pilots,
                                              final AttributeSelector victoryPointsLastWeek,
                                              final AttributeSelector victoryPointsTotal,
                                              final AttributeSelector victoryPointsYesterday) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<FacWarStats>>() {
        @Override
        public List<FacWarStats> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM FacWarStats c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "currentRank", currentRank);
          AttributeSelector.addLongSelector(qs, "c", "enlisted", enlisted);
          AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          AttributeSelector.addIntSelector(qs, "c", "highestRank", highestRank);
          AttributeSelector.addIntSelector(qs, "c", "killsLastWeek", killsLastWeek);
          AttributeSelector.addIntSelector(qs, "c", "killsTotal", killsTotal);
          AttributeSelector.addIntSelector(qs, "c", "killsYesterday", killsYesterday);
          AttributeSelector.addIntSelector(qs, "c", "pilots", pilots);
          AttributeSelector.addIntSelector(qs, "c", "victoryPointsLastWeek", victoryPointsLastWeek);
          AttributeSelector.addIntSelector(qs, "c", "victoryPointsTotal", victoryPointsTotal);
          AttributeSelector.addIntSelector(qs, "c", "victoryPointsYesterday", victoryPointsYesterday);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<FacWarStats> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), FacWarStats.class);
          query.setParameter("owner", owner);
          p.fillParams(query);
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
