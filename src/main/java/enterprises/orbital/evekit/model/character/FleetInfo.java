package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
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
    name = "evekit_data_fleet_info",
    indexes = {
        @Index(
            name = "fleetInfoIndex",
            columnList = "fleetID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "FleetInfo.get",
        query = "SELECT c FROM FleetInfo c where c.owner = :owner and c.fleetID = :fid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class FleetInfo extends CachedData {
  private static final Logger log = Logger.getLogger(FleetInfo.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS);

  private long fleetID;
  private boolean isFreeMove;
  private boolean isRegistered;
  private boolean isVoiceEnabled;
  @Lob
  @Column(
      length = 102400)
  private String motd;

  @SuppressWarnings("unused")
  protected FleetInfo() {}

  public FleetInfo(long fleetID, boolean isFreeMove, boolean isRegistered, boolean isVoiceEnabled, String motd) {
    this.fleetID = fleetID;
    this.isFreeMove = isFreeMove;
    this.isRegistered = isRegistered;
    this.isVoiceEnabled = isVoiceEnabled;
    this.motd = motd;
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
    if (!(sup instanceof FleetInfo)) return false;
    FleetInfo other = (FleetInfo) sup;
    return fleetID == other.fleetID &&
        isFreeMove == other.isFreeMove &&
        isRegistered == other.isRegistered &&
        isVoiceEnabled == other.isVoiceEnabled &&
        nullSafeObjectCompare(motd,other.motd);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(fleetID, isFreeMove, isRegistered, isVoiceEnabled, motd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getFleetID() {
    return fleetID;
  }

  public boolean isFreeMove() {
    return isFreeMove;
  }

  public boolean isRegistered() {
    return isRegistered;
  }

  public boolean isVoiceEnabled() {
    return isVoiceEnabled;
  }

  public String getMotd() {
    return motd;
  }

  @Override
  public String toString() {
    return "FleetInfo{" +
        "fleetID=" + fleetID +
        ", isFreeMove=" + isFreeMove +
        ", isRegistered=" + isRegistered +
        ", isVoiceEnabled=" + isVoiceEnabled +
        ", motd='" + motd + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FleetInfo fleetInfo = (FleetInfo) o;
    return fleetID == fleetInfo.fleetID &&
        isFreeMove == fleetInfo.isFreeMove &&
        isRegistered == fleetInfo.isRegistered &&
        isVoiceEnabled == fleetInfo.isVoiceEnabled &&
        Objects.equals(motd, fleetInfo.motd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fleetID, isFreeMove, isRegistered, isVoiceEnabled, motd);
  }

  public static FleetInfo get(
      final SynchronizedEveAccount owner,
      final long time,
      final long fleetID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<FleetInfo> getter = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery(
                                                                                                  "FleetInfo.get",
                                                                                                  FleetInfo.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("fid", fleetID);
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

  public static List<FleetInfo> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector fleetID,
      final AttributeSelector isFreeMove,
      final AttributeSelector isRegistered,
      final AttributeSelector isVoiceEnabled,
      final AttributeSelector motd) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM FleetInfo c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "fleetID", fleetID);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isFreeMove", isFreeMove);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isRegistered", isRegistered);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isVoiceEnabled", isVoiceEnabled);
                                        AttributeSelector.addStringSelector(qs, "c", "motd", motd, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<FleetInfo> query = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createQuery(qs.toString(),
                                                                                                          FleetInfo.class);
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
