package enterprises.orbital.evekit.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
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
    name = "evekit_data_facwarstats")
@NamedQueries({
    @NamedQuery(
        name = "FacWarStats.get",
        query = "SELECT c FROM FacWarStats c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class FacWarStats extends CachedData {
  private static final Logger log = Logger.getLogger(FacWarStats.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_FAC_WAR_STATS);

  private int currentRank;
  private long enlisted = -1;
  private int factionID;
  private int highestRank;
  private int killsLastWeek;
  private int killsTotal;
  private int killsYesterday;
  private int pilots;
  private int victoryPointsLastWeek;
  private int victoryPointsTotal;
  private int victoryPointsYesterday;

  @Transient
  @ApiModelProperty(
      value = "enlisted Date")
  @JsonProperty("enlistedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date enlistedDate;

  @SuppressWarnings("unused")
  protected FacWarStats() {}

  public FacWarStats(int currentRank, long enlisted, int factionID, int highestRank, int killsLastWeek, int killsTotal,
                     int killsYesterday,
                     int pilots, int victoryPointsLastWeek, int victoryPointsTotal, int victoryPointsYesterday) {
    super();
    this.currentRank = currentRank;
    this.enlisted = enlisted;
    this.factionID = factionID;
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
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    enlistedDate = assignDateField(enlisted);
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
        && highestRank == other.highestRank && killsLastWeek == other.killsLastWeek
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FacWarStats that = (FacWarStats) o;
    return currentRank == that.currentRank &&
        enlisted == that.enlisted &&
        factionID == that.factionID &&
        highestRank == that.highestRank &&
        killsLastWeek == that.killsLastWeek &&
        killsTotal == that.killsTotal &&
        killsYesterday == that.killsYesterday &&
        pilots == that.pilots &&
        victoryPointsLastWeek == that.victoryPointsLastWeek &&
        victoryPointsTotal == that.victoryPointsTotal &&
        victoryPointsYesterday == that.victoryPointsYesterday;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal,
                        killsYesterday, pilots, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
  }

  @Override
  public String toString() {
    return "FacWarStats{" +
        "currentRank=" + currentRank +
        ", enlisted=" + enlisted +
        ", factionID=" + factionID +
        ", highestRank=" + highestRank +
        ", killsLastWeek=" + killsLastWeek +
        ", killsTotal=" + killsTotal +
        ", killsYesterday=" + killsYesterday +
        ", pilots=" + pilots +
        ", victoryPointsLastWeek=" + victoryPointsLastWeek +
        ", victoryPointsTotal=" + victoryPointsTotal +
        ", victoryPointsYesterday=" + victoryPointsYesterday +
        ", enlistedDate=" + enlistedDate +
        '}';
  }

  /**
   * Retrieve FacWarStats live at the given time, or null if no such entity exists.
   *
   * @param owner stats owner
   * @param time  time at which stats must be live
   * @return stats instance live at the given time, or null
   */
  public static FacWarStats get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<FacWarStats> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "FacWarStats.get",
                                                                                                      FacWarStats.class);
                                        getter.setParameter("owner", owner);
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

  public static List<FacWarStats> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector currentRank,
      final AttributeSelector enlisted,
      final AttributeSelector factionID,
      final AttributeSelector highestRank,
      final AttributeSelector killsLastWeek,
      final AttributeSelector killsTotal,
      final AttributeSelector killsYesterday,
      final AttributeSelector pilots,
      final AttributeSelector victoryPointsLastWeek,
      final AttributeSelector victoryPointsTotal,
      final AttributeSelector victoryPointsYesterday) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
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
                                        AttributeSelector.addIntSelector(qs, "c", "highestRank", highestRank);
                                        AttributeSelector.addIntSelector(qs, "c", "killsLastWeek", killsLastWeek);
                                        AttributeSelector.addIntSelector(qs, "c", "killsTotal", killsTotal);
                                        AttributeSelector.addIntSelector(qs, "c", "killsYesterday", killsYesterday);
                                        AttributeSelector.addIntSelector(qs, "c", "pilots", pilots);
                                        AttributeSelector.addIntSelector(qs, "c", "victoryPointsLastWeek",
                                                                         victoryPointsLastWeek);
                                        AttributeSelector.addIntSelector(qs, "c", "victoryPointsTotal",
                                                                         victoryPointsTotal);
                                        AttributeSelector.addIntSelector(qs, "c", "victoryPointsYesterday",
                                                                         victoryPointsYesterday);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<FacWarStats> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     FacWarStats.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
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
