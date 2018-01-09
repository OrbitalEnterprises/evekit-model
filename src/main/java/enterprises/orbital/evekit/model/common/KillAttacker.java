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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_kill_attacker",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID",
            unique = false),
        @Index(
            name = "attackerCharacterIDIndex",
            columnList = "attackerCharacterID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "KillAttacker.getByKillAndAttackerCharacterID",
        query = "SELECT c FROM KillAttacker c where c.owner = :owner and c.killID = :killid and c.attackerCharacterID = :acid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "KillAttacker.getAllByKillID",
        query = "SELECT c FROM KillAttacker c where c.owner = :owner and c.killID = :killid and c.cid > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API caches for 30 minutes
public class KillAttacker extends CachedData {
  private static final Logger log                 = Logger.getLogger(KillAttacker.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                killID;
  private long                attackerCharacterID;
  private long                allianceID;
  private String              allianceName;
  private String              attackerCharacterName;
  private long                attackerCorporationID;
  private String              attackerCorporationName;
  private int                 damageDone;
  private int                 factionID;
  private String              factionName;
  private double              securityStatus;
  private int                 shipTypeID;
  private int                 weaponTypeID;
  private boolean             finalBlow;

  @SuppressWarnings("unused")
  protected KillAttacker() {}

  public KillAttacker(long killID, long attackerCharacterID, long allianceID, String allianceName, String attackerCharacterName, long attackerCorporationID,
                      String attackerCorporationName, int damageDone, int factionID, String factionName, double securityStatus, int shipTypeID,
                      int weaponTypeID, boolean finalBlow) {
    this.killID = killID;
    this.attackerCharacterID = attackerCharacterID;
    this.allianceID = allianceID;
    this.allianceName = allianceName;
    this.attackerCharacterName = attackerCharacterName;
    this.attackerCorporationID = attackerCorporationID;
    this.attackerCorporationName = attackerCorporationName;
    this.damageDone = damageDone;
    this.factionID = factionID;
    this.factionName = factionName;
    this.securityStatus = securityStatus;
    this.shipTypeID = shipTypeID;
    this.weaponTypeID = weaponTypeID;
    this.finalBlow = finalBlow;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof KillAttacker)) return false;
    KillAttacker other = (KillAttacker) sup;
    return killID == other.killID && attackerCharacterID == other.attackerCharacterID && allianceID == other.allianceID
        && nullSafeObjectCompare(allianceName, other.allianceName) && nullSafeObjectCompare(attackerCharacterName, other.attackerCharacterName)
        && attackerCorporationID == other.attackerCorporationID && nullSafeObjectCompare(attackerCorporationName, other.attackerCorporationName)
        && damageDone == other.damageDone && factionID == other.factionID && nullSafeObjectCompare(factionName, other.factionName)
        && securityStatus == other.securityStatus && shipTypeID == other.shipTypeID && weaponTypeID == other.weaponTypeID && finalBlow == other.finalBlow;
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

  public long getAttackerCharacterID() {
    return attackerCharacterID;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public String getAllianceName() {
    return allianceName;
  }

  public String getAttackerCharacterName() {
    return attackerCharacterName;
  }

  public long getAttackerCorporationID() {
    return attackerCorporationID;
  }

  public String getAttackerCorporationName() {
    return attackerCorporationName;
  }

  public int getDamageDone() {
    return damageDone;
  }

  public int getFactionID() {
    return factionID;
  }

  public String getFactionName() {
    return factionName;
  }

  public double getSecurityStatus() {
    return securityStatus;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  public int getWeaponTypeID() {
    return weaponTypeID;
  }

  public boolean isFinalBlow() {
    return finalBlow;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + ((allianceName == null) ? 0 : allianceName.hashCode());
    result = prime * result + (int) (attackerCharacterID ^ (attackerCharacterID >>> 32));
    result = prime * result + ((attackerCharacterName == null) ? 0 : attackerCharacterName.hashCode());
    result = prime * result + (int) (attackerCorporationID ^ (attackerCorporationID >>> 32));
    result = prime * result + ((attackerCorporationName == null) ? 0 : attackerCorporationName.hashCode());
    result = prime * result + damageDone;
    result = prime * result + factionID;
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    result = prime * result + (finalBlow ? 1231 : 1237);
    result = prime * result + (int) (killID ^ (killID >>> 32));
    long temp;
    temp = Double.doubleToLongBits(securityStatus);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + shipTypeID;
    result = prime * result + weaponTypeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    KillAttacker other = (KillAttacker) obj;
    if (allianceID != other.allianceID) return false;
    if (allianceName == null) {
      if (other.allianceName != null) return false;
    } else if (!allianceName.equals(other.allianceName)) return false;
    if (attackerCharacterID != other.attackerCharacterID) return false;
    if (attackerCharacterName == null) {
      if (other.attackerCharacterName != null) return false;
    } else if (!attackerCharacterName.equals(other.attackerCharacterName)) return false;
    if (attackerCorporationID != other.attackerCorporationID) return false;
    if (attackerCorporationName == null) {
      if (other.attackerCorporationName != null) return false;
    } else if (!attackerCorporationName.equals(other.attackerCorporationName)) return false;
    if (damageDone != other.damageDone) return false;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    if (finalBlow != other.finalBlow) return false;
    if (killID != other.killID) return false;
    if (Double.doubleToLongBits(securityStatus) != Double.doubleToLongBits(other.securityStatus)) return false;
    if (shipTypeID != other.shipTypeID) return false;
    if (weaponTypeID != other.weaponTypeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "KillAttacker [killID=" + killID + ", attackerCharacterID=" + attackerCharacterID + ", allianceID=" + allianceID + ", allianceName=" + allianceName
        + ", attackerCharacterName=" + attackerCharacterName + ", attackerCorporationID=" + attackerCorporationID + ", attackerCorporationName="
        + attackerCorporationName + ", damageDone=" + damageDone + ", factionID=" + factionID + ", factionName=" + factionName + ", securityStatus="
        + securityStatus + ", shipTypeID=" + shipTypeID + ", weaponTypeID=" + weaponTypeID + ", finalBlow=" + finalBlow + ", owner=" + owner + ", lifeStart="
        + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve kill attacker with the given parameters live at the give time, or null if no such kill attacker exists.
   * 
   * @param owner
   *          kill attacker owner
   * @param time
   *          time at which kill attacker should be live
   * @param killID
   *          kill ID to which kill attacker is attached
   * @param attackerCharacterID
   *          attacker character ID of kill attacker
   * @return a kill attacker with the given parameters live at the given time, or null
   */
  public static KillAttacker get(
                                 final SynchronizedEveAccount owner,
                                 final long time,
                                 final long killID,
                                 final long attackerCharacterID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<KillAttacker>() {
        @Override
        public KillAttacker run() throws Exception {
          TypedQuery<KillAttacker> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("KillAttacker.getByKillAndAttackerCharacterID", KillAttacker.class);
          getter.setParameter("owner", owner);
          getter.setParameter("killid", killID);
          getter.setParameter("acid", attackerCharacterID);
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
   * Retrieve list of kill attackers for the given kill ID live at the given time with sortKey greater than "contid"
   * 
   * @param owner
   *          kill attackers owner
   * @param time
   *          time at which kill attackers must be live
   * @param killID
   *          kill ID to which kill attackers are attached
   * @param maxresults
   *          maximum number of kill attackers to return
   * @param contid
   *          sortKey (exclusive) from which to start returning kill attackers
   * @return list of kill attackers for the given kill ID, live at the given time, with sortKey (lexicographically) greater than "contid"
   */
  public static List<KillAttacker> getAllKillAttackers(
                                                       final SynchronizedEveAccount owner,
                                                       final long time,
                                                       final long killID,
                                                       int maxresults,
                                                       final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(KillAttacker.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<KillAttacker>>() {
        @Override
        public List<KillAttacker> run() throws Exception {
          TypedQuery<KillAttacker> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("KillAttacker.getAllByKillID",
                                                                                                                       KillAttacker.class);
          getter.setParameter("owner", owner);
          getter.setParameter("killid", killID);
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

  public static List<KillAttacker> accessQuery(
                                               final SynchronizedEveAccount owner,
                                               final long contid,
                                               final int maxresults,
                                               final boolean reverse,
                                               final AttributeSelector at,
                                               final AttributeSelector killID,
                                               final AttributeSelector attackerCharacterID,
                                               final AttributeSelector allianceID,
                                               final AttributeSelector allianceName,
                                               final AttributeSelector attackerCharacterName,
                                               final AttributeSelector attackerCorporationID,
                                               final AttributeSelector attackerCorporationName,
                                               final AttributeSelector damageDone,
                                               final AttributeSelector factionID,
                                               final AttributeSelector factionName,
                                               final AttributeSelector securityStatus,
                                               final AttributeSelector shipTypeID,
                                               final AttributeSelector weaponTypeID,
                                               final AttributeSelector finalBlow) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<KillAttacker>>() {
        @Override
        public List<KillAttacker> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM KillAttacker c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "killID", killID);
          AttributeSelector.addLongSelector(qs, "c", "attackerCharacterID", attackerCharacterID);
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addStringSelector(qs, "c", "allianceName", allianceName, p);
          AttributeSelector.addStringSelector(qs, "c", "attackerCharacterName", attackerCharacterName, p);
          AttributeSelector.addLongSelector(qs, "c", "attackerCorporationID", attackerCorporationID);
          AttributeSelector.addStringSelector(qs, "c", "attackerCorporationName", attackerCorporationName, p);
          AttributeSelector.addIntSelector(qs, "c", "damageDone", damageDone);
          AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          AttributeSelector.addDoubleSelector(qs, "c", "securityStatus", securityStatus);
          AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
          AttributeSelector.addIntSelector(qs, "c", "weaponTypeID", weaponTypeID);
          AttributeSelector.addBooleanSelector(qs, "c", "finalBlow", finalBlow);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<KillAttacker> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), KillAttacker.class);
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
