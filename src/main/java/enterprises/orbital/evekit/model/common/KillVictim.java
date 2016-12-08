package enterprises.orbital.evekit.model.common;

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

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_kill_victim",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "KillVictim.getByKillID",
        query = "SELECT c FROM KillVictim c where c.owner = :owner and c.killID = :killid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 1 hour cache time - API caches for 30 minutes
public class KillVictim extends CachedData {
  private static final Logger log  = Logger.getLogger(KillVictim.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private long                killID;
  private long                allianceID;
  private String              allianceName;
  private long                killCharacterID;
  private String              killCharacterName;
  private long                killCorporationID;
  private String              killCorporationName;
  private long                damageTaken;
  private long                factionID;
  private String              factionName;
  private int                 shipTypeID;

  @SuppressWarnings("unused")
  private KillVictim() {}

  public KillVictim(long killID, long allianceID, String allianceName, long killCharacterID, String killCharacterName, long killCorporationID,
                    String killCorporationName, long damageTaken, long factionID, String factionName, int shipTypeID) {
    super();
    this.killID = killID;
    this.allianceID = allianceID;
    this.allianceName = allianceName;
    this.killCharacterID = killCharacterID;
    this.killCharacterName = killCharacterName;
    this.killCorporationID = killCorporationID;
    this.killCorporationName = killCorporationName;
    this.damageTaken = damageTaken;
    this.factionID = factionID;
    this.factionName = factionName;
    this.shipTypeID = shipTypeID;
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
                            CachedData sup) {
    if (!(sup instanceof KillVictim)) return false;
    KillVictim other = (KillVictim) sup;
    return killID == other.killID && allianceID == other.allianceID && nullSafeObjectCompare(allianceName, other.allianceName)
        && killCharacterID == other.killCharacterID && nullSafeObjectCompare(killCharacterName, other.killCharacterName)
        && killCorporationID == other.killCorporationID && nullSafeObjectCompare(killCorporationName, other.killCorporationName)
        && damageTaken == other.damageTaken && factionID == other.factionID && nullSafeObjectCompare(factionName, other.factionName)
        && shipTypeID == other.shipTypeID;
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

  public long getAllianceID() {
    return allianceID;
  }

  public String getAllianceName() {
    return allianceName;
  }

  public long getKillCharacterID() {
    return killCharacterID;
  }

  public String getKillCharacterName() {
    return killCharacterName;
  }

  public long getKillCorporationID() {
    return killCorporationID;
  }

  public String getKillCorporationName() {
    return killCorporationName;
  }

  public long getDamageTaken() {
    return damageTaken;
  }

  public long getFactionID() {
    return factionID;
  }

  public String getFactionName() {
    return factionName;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + ((allianceName == null) ? 0 : allianceName.hashCode());
    result = prime * result + (int) (damageTaken ^ (damageTaken >>> 32));
    result = prime * result + (int) (factionID ^ (factionID >>> 32));
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    result = prime * result + (int) (killCharacterID ^ (killCharacterID >>> 32));
    result = prime * result + ((killCharacterName == null) ? 0 : killCharacterName.hashCode());
    result = prime * result + (int) (killCorporationID ^ (killCorporationID >>> 32));
    result = prime * result + ((killCorporationName == null) ? 0 : killCorporationName.hashCode());
    result = prime * result + (int) (killID ^ (killID >>> 32));
    result = prime * result + shipTypeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    KillVictim other = (KillVictim) obj;
    if (allianceID != other.allianceID) return false;
    if (allianceName == null) {
      if (other.allianceName != null) return false;
    } else if (!allianceName.equals(other.allianceName)) return false;
    if (damageTaken != other.damageTaken) return false;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    if (killCharacterID != other.killCharacterID) return false;
    if (killCharacterName == null) {
      if (other.killCharacterName != null) return false;
    } else if (!killCharacterName.equals(other.killCharacterName)) return false;
    if (killCorporationID != other.killCorporationID) return false;
    if (killCorporationName == null) {
      if (other.killCorporationName != null) return false;
    } else if (!killCorporationName.equals(other.killCorporationName)) return false;
    if (killID != other.killID) return false;
    if (shipTypeID != other.shipTypeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "KillVictim [killID=" + killID + ", allianceID=" + allianceID + ", allianceName=" + allianceName + ", killCharacterID=" + killCharacterID
        + ", killCharacterName=" + killCharacterName + ", killCorporationID=" + killCorporationID + ", killCorporationName=" + killCorporationName
        + ", damageTaken=" + damageTaken + ", factionID=" + factionID + ", factionName=" + factionName + ", shipTypeID=" + shipTypeID + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve kill victim with the given parameters live at the given time, or null if no such kill victim exists.
   * 
   * @param owner
   *          kill victim owner
   * @param time
   *          time at which the kill victim must be live
   * @param killID
   *          kill ID to which the kill victim is associated
   * @return the kill victim with the appropriate parameters, live at the given time, or null
   */
  public static KillVictim get(
                               final SynchronizedEveAccount owner,
                               final long time,
                               final long killID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<KillVictim>() {
        @Override
        public KillVictim run() throws Exception {
          TypedQuery<KillVictim> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("KillVictim.getByKillID",
                                                                                                                     KillVictim.class);
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

  public static List<KillVictim> accessQuery(
                                             final SynchronizedEveAccount owner,
                                             final long contid,
                                             final int maxresults,
                                             final boolean reverse,
                                             final AttributeSelector at,
                                             final AttributeSelector killID,
                                             final AttributeSelector allianceID,
                                             final AttributeSelector allianceName,
                                             final AttributeSelector killCharacterID,
                                             final AttributeSelector killCharacterName,
                                             final AttributeSelector killCorporationID,
                                             final AttributeSelector killCorporationName,
                                             final AttributeSelector damageTaken,
                                             final AttributeSelector factionID,
                                             final AttributeSelector factionName,
                                             final AttributeSelector shipTypeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<KillVictim>>() {
        @Override
        public List<KillVictim> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM KillVictim c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "killID", killID);
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addStringSelector(qs, "c", "allianceName", allianceName, p);
          AttributeSelector.addLongSelector(qs, "c", "killCharacterID", killCharacterID);
          AttributeSelector.addStringSelector(qs, "c", "killCharacterName", killCharacterName, p);
          AttributeSelector.addLongSelector(qs, "c", "killCorporationID", killCorporationID);
          AttributeSelector.addStringSelector(qs, "c", "killCorporationName", killCorporationName, p);
          AttributeSelector.addLongSelector(qs, "c", "damageTaken", damageTaken);
          AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<KillVictim> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), KillVictim.class);
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
