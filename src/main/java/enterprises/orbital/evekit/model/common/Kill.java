package enterprises.orbital.evekit.model.common;

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
    name = "evekit_data_kill",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID"),
        @Index(
            name = "killTimeIndex",
            columnList = "killTime"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Kill.getByKillID",
        query = "SELECT c FROM Kill c where c.owner = :owner and c.killID = :killid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Kill.getByKillTimeForward",
        query = "SELECT c FROM Kill c where c.owner = :owner and c.killTime > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.killTime asc"),
    @NamedQuery(
        name = "Kill.getByKillTimeBackward",
        query = "SELECT c FROM Kill c where c.owner = :owner and c.killTime < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.killTime desc"),
})
public class Kill extends CachedData {
  private static final Logger log = Logger.getLogger(Kill.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private int killID;
  private long killTime = -1;
  private int moonID;
  private int solarSystemID;
  private int warID;
  @Transient
  @ApiModelProperty(
      value = "killTime Date")
  @JsonProperty("killTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date killTimeDate;

  @SuppressWarnings("unused")
  protected Kill() {}

  public Kill(int killID, long killTime, int moonID, int solarSystemID, int warID) {
    this.killID = killID;
    this.killTime = killTime;
    this.moonID = moonID;
    this.solarSystemID = solarSystemID;
    this.warID = warID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    killTimeDate = assignDateField(killTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof Kill)) return false;
    Kill other = (Kill) sup;
    return killID == other.killID && killTime == other.killTime &&
        moonID == other.moonID && solarSystemID == other.solarSystemID &&
        warID == other.warID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getKillID() {
    return killID;
  }

  public long getKillTime() {
    return killTime;
  }

  public int getMoonID() {
    return moonID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public int getWarID() {
    return warID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Kill kill = (Kill) o;
    return killID == kill.killID &&
        killTime == kill.killTime &&
        moonID == kill.moonID &&
        solarSystemID == kill.solarSystemID &&
        warID == kill.warID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), killID, killTime, moonID, solarSystemID, warID);
  }

  @Override
  public String toString() {
    return "Kill{" +
        "killID=" + killID +
        ", killTime=" + killTime +
        ", moonID=" + moonID +
        ", solarSystemID=" + solarSystemID +
        ", warID=" + warID +
        ", killTimeDate=" + killTimeDate +
        '}';
  }

  /**
   * Retrieve kill live at the given time, or null if no such kill exists.
   *
   * @param owner  kill owner
   * @param time   time at which kill must be live
   * @param killID kill ID of the kill to retrieve
   * @return the kill live at the given time, or null if no such kill exists.
   */
  public static Kill get(
      final SynchronizedEveAccount owner,
      final long time,
      final int killID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Kill> getter = EveKitUserAccountProvider.getFactory()
                                                                                           .getEntityManager()
                                                                                           .createNamedQuery(
                                                                                               "Kill.getByKillID",
                                                                                               Kill.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("killid", killID);
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

  public static List<Kill> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector killID,
      final AttributeSelector killTime,
      final AttributeSelector moonID,
      final AttributeSelector solarSystemID,
      final AttributeSelector warID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Kill c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "killID", killID);
                                        AttributeSelector.addLongSelector(qs, "c", "killTime", killTime);
                                        AttributeSelector.addIntSelector(qs, "c", "moonID", moonID);
                                        AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                        AttributeSelector.addIntSelector(qs, "c", "warID", warID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Kill> query = EveKitUserAccountProvider.getFactory()
                                                                                          .getEntityManager()
                                                                                          .createQuery(qs.toString(),
                                                                                                       Kill.class);
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
