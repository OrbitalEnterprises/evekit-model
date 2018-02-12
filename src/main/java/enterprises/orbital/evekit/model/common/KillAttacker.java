package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_kill_attacker",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID"),
        @Index(
            name = "attackerCharacterIDIndex",
            columnList = "attackerCharacterID"),
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
  private static final Logger log = Logger.getLogger(KillAttacker.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private int killID;
  private int attackerCharacterID;
  private int allianceID;
  private int attackerCorporationID;
  private int damageDone;
  private int factionID;
  private float securityStatus;
  private int shipTypeID;
  private int weaponTypeID;
  private boolean finalBlow;

  @SuppressWarnings("unused")
  protected KillAttacker() {}

  public KillAttacker(int killID, int attackerCharacterID, int allianceID, int attackerCorporationID, int damageDone,
                      int factionID, float securityStatus, int shipTypeID, int weaponTypeID, boolean finalBlow) {
    this.killID = killID;
    this.attackerCharacterID = attackerCharacterID;
    this.allianceID = allianceID;
    this.attackerCorporationID = attackerCorporationID;
    this.damageDone = damageDone;
    this.factionID = factionID;
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
        && attackerCorporationID == other.attackerCorporationID && damageDone == other.damageDone
        && factionID == other.factionID &&
        Float.compare(securityStatus, other.securityStatus) == 0 && shipTypeID == other.shipTypeID
        && weaponTypeID == other.weaponTypeID && finalBlow == other.finalBlow;
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

  public int getAttackerCharacterID() {
    return attackerCharacterID;
  }

  public int getAllianceID() {
    return allianceID;
  }

  public int getAttackerCorporationID() {
    return attackerCorporationID;
  }

  public int getDamageDone() {
    return damageDone;
  }

  public int getFactionID() {
    return factionID;
  }

  public float getSecurityStatus() {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    KillAttacker that = (KillAttacker) o;
    return killID == that.killID &&
        attackerCharacterID == that.attackerCharacterID &&
        allianceID == that.allianceID &&
        attackerCorporationID == that.attackerCorporationID &&
        damageDone == that.damageDone &&
        factionID == that.factionID &&
        Float.compare(that.securityStatus, securityStatus) == 0 &&
        shipTypeID == that.shipTypeID &&
        weaponTypeID == that.weaponTypeID &&
        finalBlow == that.finalBlow;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
                        factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow);
  }

  @Override
  public String toString() {
    return "KillAttacker{" +
        "killID=" + killID +
        ", attackerCharacterID=" + attackerCharacterID +
        ", allianceID=" + allianceID +
        ", attackerCorporationID=" + attackerCorporationID +
        ", damageDone=" + damageDone +
        ", factionID=" + factionID +
        ", securityStatus=" + securityStatus +
        ", shipTypeID=" + shipTypeID +
        ", weaponTypeID=" + weaponTypeID +
        ", finalBlow=" + finalBlow +
        '}';
  }

  /**
   * Retrieve kill attacker with the given parameters live at the give time, or null if no such kill attacker exists.
   *
   * @param owner               kill attacker owner
   * @param time                time at which kill attacker should be live
   * @param killID              kill ID to which kill attacker is attached
   * @param attackerCharacterID attacker character ID of kill attacker
   * @return a kill attacker with the given parameters live at the given time, or null
   */
  public static KillAttacker get(
      final SynchronizedEveAccount owner,
      final long time,
      final int killID,
      final int attackerCharacterID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<KillAttacker> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "KillAttacker.getByKillAndAttackerCharacterID",
                                                                                                       KillAttacker.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("killid", killID);
                                        getter.setParameter("acid", attackerCharacterID);
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

  public static List<KillAttacker> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector killID,
      final AttributeSelector attackerCharacterID,
      final AttributeSelector allianceID,
      final AttributeSelector attackerCorporationID,
      final AttributeSelector damageDone,
      final AttributeSelector factionID,
      final AttributeSelector securityStatus,
      final AttributeSelector shipTypeID,
      final AttributeSelector weaponTypeID,
      final AttributeSelector finalBlow) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM KillAttacker c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "killID", killID);
                                        AttributeSelector.addIntSelector(qs, "c", "attackerCharacterID",
                                                                         attackerCharacterID);
                                        AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                        AttributeSelector.addIntSelector(qs, "c", "attackerCorporationID",
                                                                         attackerCorporationID);
                                        AttributeSelector.addIntSelector(qs, "c", "damageDone", damageDone);
                                        AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                        AttributeSelector.addFloatSelector(qs, "c", "securityStatus", securityStatus);
                                        AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
                                        AttributeSelector.addIntSelector(qs, "c", "weaponTypeID", weaponTypeID);
                                        AttributeSelector.addBooleanSelector(qs, "c", "finalBlow", finalBlow);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<KillAttacker> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      KillAttacker.class);
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
