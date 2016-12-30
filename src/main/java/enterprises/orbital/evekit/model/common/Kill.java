package enterprises.orbital.evekit.model.common;

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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_kill",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID",
            unique = false),
        @Index(
            name = "killTimeIndex",
            columnList = "killTime",
            unique = false),
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
// 1 hour cache time - API caches for 30 minutes
public class Kill extends CachedData {
  private static final Logger log                 = Logger.getLogger(Kill.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                killID;
  private long                killTime            = -1;
  private int                 moonID;
  private long                solarSystemID;
  @Transient
  @ApiModelProperty(
      value = "killTime Date")
  @JsonProperty("killTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                killTimeDate;

  @SuppressWarnings("unused")
  private Kill() {}

  public Kill(long killID, long killTime, int moonID, long solarSystemID) {
    this.killID = killID;
    this.killTime = killTime;
    this.moonID = moonID;
    this.solarSystemID = solarSystemID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
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
    return killID == other.killID && killTime == other.killTime && moonID == other.moonID && solarSystemID == other.solarSystemID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getKillID() {
    return killID;
  }

  public long getKillTime() {
    return killTime;
  }

  public int getMoonID() {
    return moonID;
  }

  public long getSolarSystemID() {
    return solarSystemID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (killID ^ (killID >>> 32));
    result = prime * result + (int) (killTime ^ (killTime >>> 32));
    result = prime * result + moonID;
    result = prime * result + (int) (solarSystemID ^ (solarSystemID >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Kill other = (Kill) obj;
    if (killID != other.killID) return false;
    if (killTime != other.killTime) return false;
    if (moonID != other.moonID) return false;
    if (solarSystemID != other.solarSystemID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Kill [killID=" + killID + ", killTime=" + killTime + ", moonID=" + moonID + ", solarSystemID=" + solarSystemID + ", owner=" + owner + ", lifeStart="
        + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve kill live at the given time, or null if no such kill exists.
   * 
   * @param owner
   *          kill owner
   * @param time
   *          time at which kill must be live
   * @param killID
   *          kill ID of the kill to retrieve
   * @return the kill live at the given time, or null if no such kill exists.
   */
  public static Kill get(
                         final SynchronizedEveAccount owner,
                         final long time,
                         final long killID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Kill>() {
        @Override
        public Kill run() throws Exception {
          TypedQuery<Kill> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Kill.getByKillID", Kill.class);
          getter.setParameter("owner", owner);
          getter.setParameter("killid", killID);
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

  /**
   * Retrieve list of kills live at the given time with killTime greater than "contid".
   * 
   * @param owner
   *          kills owner
   * @param time
   *          time at which kills must be live
   * @param maxresults
   *          maximum number of kills to retrieve
   * @param contid
   *          killTime (exclusive) from which kills will be retrieved
   * @return a list of kills live at the given time with killTime greater than "contid"
   */
  public static List<Kill> getKillsForward(
                                           final SynchronizedEveAccount owner,
                                           final long time,
                                           int maxresults,
                                           final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Kill.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Kill>>() {
        @Override
        public List<Kill> run() throws Exception {
          TypedQuery<Kill> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Kill.getByKillTimeForward", Kill.class);
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

  /**
   * Retrieve list of kills live at the given time with killTime less than "contid".
   * 
   * @param owner
   *          kills owner
   * @param time
   *          time at which kills must be live
   * @param maxresults
   *          maximum number of kills to retrieve
   * @param contid
   *          killTime (exclusive) before which kills will be retrieved
   * @return a list of kills live at the given time with killTime less than "contid"
   */
  public static List<Kill> getKillsBackward(
                                            final SynchronizedEveAccount owner,
                                            final long time,
                                            int maxresults,
                                            final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Kill.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Kill>>() {
        @Override
        public List<Kill> run() throws Exception {
          TypedQuery<Kill> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Kill.getByKillTimeBackward", Kill.class);
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

  public static List<Kill> accessQuery(
                                       final SynchronizedEveAccount owner,
                                       final long contid,
                                       final int maxresults,
                                       final boolean reverse,
                                       final AttributeSelector at,
                                       final AttributeSelector killID,
                                       final AttributeSelector killTime,
                                       final AttributeSelector moonID,
                                       final AttributeSelector solarSystemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Kill>>() {
        @Override
        public List<Kill> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Kill c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "killID", killID);
          AttributeSelector.addLongSelector(qs, "c", "killTime", killTime);
          AttributeSelector.addIntSelector(qs, "c", "moonID", moonID);
          AttributeSelector.addLongSelector(qs, "c", "solarSystemID", solarSystemID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Kill> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Kill.class);
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
