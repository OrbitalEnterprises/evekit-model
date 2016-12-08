package enterprises.orbital.evekit.model.eve;

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
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_eve_facwarsummary")
@NamedQueries({
    @NamedQuery(
        name = "FactionWarSummary.get",
        query = "SELECT c FROM FactionWarSummary c WHERE c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionWarSummary extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionWarSummary.class.getName());
  private int                 killsLastWeek;
  private int                 killsTotal;
  private int                 killsYesterday;
  private int                 victoryPointsLastWeek;
  private int                 victoryPointsTotal;
  private int                 victoryPointsYesterday;

  @SuppressWarnings("unused")
  private FactionWarSummary() {}

  public FactionWarSummary(int killsLastWeek, int killsTotal, int killsYesterday, int victoryPointsLastWeek, int victoryPointsTotal,
                           int victoryPointsYesterday) {
    super();
    this.killsLastWeek = killsLastWeek;
    this.killsTotal = killsTotal;
    this.killsYesterday = killsYesterday;
    this.victoryPointsLastWeek = victoryPointsLastWeek;
    this.victoryPointsTotal = victoryPointsTotal;
    this.victoryPointsYesterday = victoryPointsYesterday;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof FactionWarSummary)) return false;
    FactionWarSummary other = (FactionWarSummary) sup;
    return killsLastWeek == other.killsLastWeek && killsTotal == other.killsTotal && killsYesterday == other.killsYesterday
        && victoryPointsLastWeek == other.victoryPointsLastWeek && victoryPointsTotal == other.victoryPointsTotal
        && victoryPointsYesterday == other.victoryPointsYesterday;
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
    result = prime * result + killsLastWeek;
    result = prime * result + killsTotal;
    result = prime * result + killsYesterday;
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
    FactionWarSummary other = (FactionWarSummary) obj;
    if (killsLastWeek != other.killsLastWeek) return false;
    if (killsTotal != other.killsTotal) return false;
    if (killsYesterday != other.killsYesterday) return false;
    if (victoryPointsLastWeek != other.victoryPointsLastWeek) return false;
    if (victoryPointsTotal != other.victoryPointsTotal) return false;
    if (victoryPointsYesterday != other.victoryPointsYesterday) return false;
    return true;
  }

  @Override
  public String toString() {
    return "FactionWarSummary [killsLastWeek=" + killsLastWeek + ", killsTotal=" + killsTotal + ", killsYesterday=" + killsYesterday
        + ", victoryPointsLastWeek=" + victoryPointsLastWeek + ", victoryPointsTotal=" + victoryPointsTotal + ", victoryPointsYesterday="
        + victoryPointsYesterday + "]";
  }

  public static FactionWarSummary get(
                                      final long time) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<FactionWarSummary>() {
        @Override
        public FactionWarSummary run() throws Exception {
          TypedQuery<FactionWarSummary> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("FactionWarSummary.get",
                                                                                                                        FactionWarSummary.class);
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

  public static List<FactionWarSummary> accessQuery(
                                                    final long contid,
                                                    final int maxresults,
                                                    final boolean reverse,
                                                    final AttributeSelector at,
                                                    final AttributeSelector killsLastWeek,
                                                    final AttributeSelector killsTotal,
                                                    final AttributeSelector killsYesterday,
                                                    final AttributeSelector victoryPointsLastWeek,
                                                    final AttributeSelector victoryPointsTotal,
                                                    final AttributeSelector victoryPointsYesterday) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<FactionWarSummary>>() {
        @Override
        public List<FactionWarSummary> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM FactionWarSummary c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "killsLastWeek", killsLastWeek);
          AttributeSelector.addIntSelector(qs, "c", "killsTotal", killsTotal);
          AttributeSelector.addIntSelector(qs, "c", "killsYesterday", killsYesterday);
          AttributeSelector.addIntSelector(qs, "c", "victoryPointsLastWeek", victoryPointsLastWeek);
          AttributeSelector.addIntSelector(qs, "c", "victoryPointsTotal", victoryPointsTotal);
          AttributeSelector.addIntSelector(qs, "c", "victoryPointsYesterday", victoryPointsYesterday);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<FactionWarSummary> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), FactionWarSummary.class);
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
