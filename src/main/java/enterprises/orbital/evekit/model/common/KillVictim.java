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
    name = "evekit_data_kill_victim",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "KillVictim.getByKillID",
        query = "SELECT c FROM KillVictim c where c.owner = :owner and c.killID = :killid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 1 hour cache time - API caches for 30 minutes
public class KillVictim extends CachedData {
  private static final Logger log = Logger.getLogger(KillVictim.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private int killID;
  private int allianceID;
  private int killCharacterID;
  private int killCorporationID;
  private int damageTaken;
  private int factionID;
  private int shipTypeID;
  private double x;
  private double y;
  private double z;

  @SuppressWarnings("unused")
  protected KillVictim() {}

  public KillVictim(int killID, int allianceID, int killCharacterID, int killCorporationID, int damageTaken,
                    int factionID, int shipTypeID, double x, double y, double z) {
    this.killID = killID;
    this.allianceID = allianceID;
    this.killCharacterID = killCharacterID;
    this.killCorporationID = killCorporationID;
    this.damageTaken = damageTaken;
    this.factionID = factionID;
    this.shipTypeID = shipTypeID;
    this.x = x;
    this.y = y;
    this.z = z;
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
    if (!(sup instanceof KillVictim)) return false;
    KillVictim other = (KillVictim) sup;
    return killID == other.killID && allianceID == other.allianceID && killCharacterID == other.killCharacterID
        && killCorporationID == other.killCorporationID && damageTaken == other.damageTaken
        && factionID == other.factionID && shipTypeID == other.shipTypeID
        && doubleCompare(x, other.x,0.00000001D)
        && doubleCompare(y, other.y,0.00000001D)
        && doubleCompare(z, other.z,0.00000001D);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(killID, allianceID, killCharacterID, killCorporationID, damageTaken, factionID,
                          shipTypeID, x, y, z);
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

  public int getAllianceID() {
    return allianceID;
  }

  public int getKillCharacterID() {
    return killCharacterID;
  }

  public int getKillCorporationID() {
    return killCorporationID;
  }

  public int getDamageTaken() {
    return damageTaken;
  }

  public int getFactionID() {
    return factionID;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    KillVictim that = (KillVictim) o;
    return killID == that.killID &&
        allianceID == that.allianceID &&
        killCharacterID == that.killCharacterID &&
        killCorporationID == that.killCorporationID &&
        damageTaken == that.damageTaken &&
        factionID == that.factionID &&
        shipTypeID == that.shipTypeID &&
        Double.compare(that.x, x) == 0 &&
        Double.compare(that.y, y) == 0 &&
        Double.compare(that.z, z) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), killID, allianceID, killCharacterID, killCorporationID, damageTaken,
                        factionID,
                        shipTypeID, x, y, z);
  }

  @Override
  public String toString() {
    return "KillVictim{" +
        "killID=" + killID +
        ", allianceID=" + allianceID +
        ", killCharacterID=" + killCharacterID +
        ", killCorporationID=" + killCorporationID +
        ", damageTaken=" + damageTaken +
        ", factionID=" + factionID +
        ", shipTypeID=" + shipTypeID +
        ", x=" + x +
        ", y=" + y +
        ", z=" + z +
        '}';
  }

  /**
   * Retrieve kill victim with the given parameters live at the given time, or null if no such kill victim exists.
   *
   * @param owner  kill victim owner
   * @param time   time at which the kill victim must be live
   * @param killID kill ID to which the kill victim is associated
   * @return the kill victim with the appropriate parameters, live at the given time, or null
   */
  public static KillVictim get(
      final SynchronizedEveAccount owner,
      final long time,
      final int killID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<KillVictim> getter = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createNamedQuery(
                                                                                                     "KillVictim.getByKillID",
                                                                                                     KillVictim.class);
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

  public static List<KillVictim> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector killID,
      final AttributeSelector allianceID,
      final AttributeSelector killCharacterID,
      final AttributeSelector killCorporationID,
      final AttributeSelector damageTaken,
      final AttributeSelector factionID,
      final AttributeSelector shipTypeID,
      final AttributeSelector x,
      final AttributeSelector y,
      final AttributeSelector z) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM KillVictim c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "killID", killID);
                                        AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                        AttributeSelector.addIntSelector(qs, "c", "killCharacterID", killCharacterID);
                                        AttributeSelector.addIntSelector(qs, "c", "killCorporationID",
                                                                         killCorporationID);
                                        AttributeSelector.addIntSelector(qs, "c", "damageTaken", damageTaken);
                                        AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                        AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
                                        AttributeSelector.addDoubleSelector(qs, "c", "x", x);
                                        AttributeSelector.addDoubleSelector(qs, "c", "y", y);
                                        AttributeSelector.addDoubleSelector(qs, "c", "z", z);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<KillVictim> query = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createQuery(
                                                                                                    qs.toString(),
                                                                                                    KillVictim.class);
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
