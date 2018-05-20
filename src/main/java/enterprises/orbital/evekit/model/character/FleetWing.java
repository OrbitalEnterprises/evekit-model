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
    name = "evekit_data_fleet_wings",
    indexes = {
        @Index(
            name = "fleetWingIndex",
            columnList = "fleetID, wingID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "FleetWing.get",
        query = "SELECT c FROM FleetWing c where c.owner = :owner and c.fleetID = :fid and c.wingID = :wid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class FleetWing extends CachedData {
  private static final Logger log = Logger.getLogger(FleetWing.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS);

  private long fleetID;
  private long wingID;
  private String name;

  @SuppressWarnings("unused")
  protected FleetWing() {}

  public FleetWing(long fleetID, long wingID, String name) {
    this.fleetID = fleetID;
    this.wingID = wingID;
    this.name = name;
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
    if (!(sup instanceof FleetWing)) return false;
    FleetWing other = (FleetWing) sup;
    return fleetID == other.fleetID &&
        wingID == other.wingID &&
        nullSafeObjectCompare(name, other.name);
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

  public long getWingID() {
    return wingID;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "FleetWing{" +
        "fleetID=" + fleetID +
        ", wingID=" + wingID +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FleetWing fleetWing = (FleetWing) o;
    return fleetID == fleetWing.fleetID &&
        wingID == fleetWing.wingID &&
        Objects.equals(name, fleetWing.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fleetID, wingID, name);
  }

  public static FleetWing get(
      final SynchronizedEveAccount owner,
      final long time,
      final long fleetID,
      final long wingID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<FleetWing> getter = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery(
                                                                                                  "FleetWing.get",
                                                                                                  FleetWing.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("fid", fleetID);
                                        getter.setParameter("wid", wingID);
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

  public static List<FleetWing> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector fleetID,
      final AttributeSelector wingID,
      final AttributeSelector name) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM FleetWing c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "fleetID", fleetID);
                                        AttributeSelector.addLongSelector(qs, "c", "wingID", wingID);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<FleetWing> query = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createQuery(qs.toString(),
                                                                                                          FleetWing.class);
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
