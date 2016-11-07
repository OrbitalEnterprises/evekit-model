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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_eve_factionstats")
@NamedQueries({
    @NamedQuery(
        name = "FactionStats.get",
        query = "SELECT c FROM FactionStats c WHERE c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionStats extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionStats.class.getName());
  private long                factionID;
  private String              factionName;
  private int                 killsLastWeek;
  private int                 killsTotal;
  private int                 killsYesterday;
  private int                 pilots;
  private int                 systemsControlled;
  private int                 victoryPointsLastWeek;
  private int                 victoryPointsTotal;
  private int                 victoryPointsYesterday;

  @SuppressWarnings("unused")
  private FactionStats() {}

  public FactionStats(long factionID, String factionName, int killsLastWeek, int killsTotal, int killsYesterday, int pilots, int systemsControlled,
                      int victoryPointsLastWeek, int victoryPointsTotal, int victoryPointsYesterday) {
    super();
    this.factionID = factionID;
    this.factionName = factionName;
    this.killsLastWeek = killsLastWeek;
    this.killsTotal = killsTotal;
    this.killsYesterday = killsYesterday;
    this.pilots = pilots;
    this.systemsControlled = systemsControlled;
    this.victoryPointsLastWeek = victoryPointsLastWeek;
    this.victoryPointsTotal = victoryPointsTotal;
    this.victoryPointsYesterday = victoryPointsYesterday;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof FactionStats)) return false;
    FactionStats other = (FactionStats) sup;
    return factionID == other.factionID && nullSafeObjectCompare(factionName, other.factionName) && killsLastWeek == other.killsLastWeek
        && killsTotal == other.killsTotal && killsYesterday == other.killsYesterday && pilots == other.pilots && systemsControlled == other.systemsControlled
        && victoryPointsLastWeek == other.victoryPointsLastWeek && victoryPointsTotal == other.victoryPointsTotal
        && victoryPointsYesterday == other.victoryPointsYesterday;
  }

  public long getFactionID() {
    return factionID;
  }

  public String getFactionName() {
    return factionName;
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

  public int getSystemsControlled() {
    return systemsControlled;
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
    result = prime * result + (int) (factionID ^ (factionID >>> 32));
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    result = prime * result + killsLastWeek;
    result = prime * result + killsTotal;
    result = prime * result + killsYesterday;
    result = prime * result + pilots;
    result = prime * result + systemsControlled;
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
    FactionStats other = (FactionStats) obj;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    if (killsLastWeek != other.killsLastWeek) return false;
    if (killsTotal != other.killsTotal) return false;
    if (killsYesterday != other.killsYesterday) return false;
    if (pilots != other.pilots) return false;
    if (systemsControlled != other.systemsControlled) return false;
    if (victoryPointsLastWeek != other.victoryPointsLastWeek) return false;
    if (victoryPointsTotal != other.victoryPointsTotal) return false;
    if (victoryPointsYesterday != other.victoryPointsYesterday) return false;
    return true;
  }

  @Override
  public String toString() {
    return "FactionStats [factionID=" + factionID + ", factionName=" + factionName + ", killsLastWeek=" + killsLastWeek + ", killsTotal=" + killsTotal
        + ", killsYesterday=" + killsYesterday + ", pilots=" + pilots + ", systemsControlled=" + systemsControlled + ", victoryPointsLastWeek="
        + victoryPointsLastWeek + ", victoryPointsTotal=" + victoryPointsTotal + ", victoryPointsYesterday=" + victoryPointsYesterday + "]";
  }

  public static FactionStats get(
                                 final long time,
                                 final long factionID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<FactionStats>() {
        @Override
        public FactionStats run() throws Exception {
          TypedQuery<FactionStats> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("FactionStats.get", FactionStats.class);
          getter.setParameter("point", time);
          getter.setParameter("fid", factionID);
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

  public static List<FactionStats> accessQuery(
                                               final long contid,
                                               final int maxresults,
                                               final boolean reverse,
                                               final AttributeSelector at,
                                               final AttributeSelector factionID,
                                               final AttributeSelector factionName,
                                               final AttributeSelector killsLastWeek,
                                               final AttributeSelector killsTotal,
                                               final AttributeSelector killsYesterday,
                                               final AttributeSelector pilots,
                                               final AttributeSelector systemsControlled,
                                               final AttributeSelector victoryPointsLastWeek,
                                               final AttributeSelector victoryPointsTotal,
                                               final AttributeSelector victoryPointsYesterday) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<FactionStats>>() {
        @Override
        public List<FactionStats> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM FactionStats c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          AttributeSelector.addIntSelector(qs, "c", "killsLastWeek", killsLastWeek);
          AttributeSelector.addIntSelector(qs, "c", "killsTotal", killsTotal);
          AttributeSelector.addIntSelector(qs, "c", "killsYesterday", killsYesterday);
          AttributeSelector.addIntSelector(qs, "c", "pilots", pilots);
          AttributeSelector.addIntSelector(qs, "c", "systemsControlled", systemsControlled);
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
          TypedQuery<FactionStats> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), FactionStats.class);
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
