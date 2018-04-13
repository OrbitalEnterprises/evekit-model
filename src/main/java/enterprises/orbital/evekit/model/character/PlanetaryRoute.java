package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_planetary_route",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID"),
        @Index(
            name = "routeIDIndex",
            columnList = "routeID")
    })
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryRoute.getByPlanetAndRouteID",
        query = "SELECT c FROM PlanetaryRoute c where c.owner = :owner and c.planetID = :pid and c.routeID = :rid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class PlanetaryRoute extends CachedData {
  private static final Logger log = Logger.getLogger(PlanetaryRoute.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);

  private int planetID;
  private long routeID;
  private long sourcePinID;
  private long destinationPinID;
  private int contentTypeID;
  private float quantity;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "planet_route_waypoint", joinColumns = @JoinColumn(name = "planet_route_cid"))
  @Column(name = "waypointID")
  private List<Long> waypoints = new ArrayList<>();

  @SuppressWarnings("unused")
  protected PlanetaryRoute() {}

  public PlanetaryRoute(int planetID, long routeID, long sourcePinID, long destinationPinID, int contentTypeID,
                        float quantity, List<Long> waypoints) {
    this.planetID = planetID;
    this.routeID = routeID;
    this.sourcePinID = sourcePinID;
    this.destinationPinID = destinationPinID;
    this.contentTypeID = contentTypeID;
    this.quantity = quantity;
    this.waypoints = waypoints;
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
    if (!(sup instanceof PlanetaryRoute)) return false;
    PlanetaryRoute other = (PlanetaryRoute) sup;
    return planetID == other.planetID && routeID == other.routeID && sourcePinID == other.sourcePinID && destinationPinID == other.destinationPinID
        && contentTypeID == other.contentTypeID
        && floatCompare(quantity, other.quantity,0.00001F)
        && nullSafeListCompare(waypoints, other.waypoints);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getPlanetID() {
    return planetID;
  }

  public long getRouteID() {
    return routeID;
  }

  public long getSourcePinID() {
    return sourcePinID;
  }

  public long getDestinationPinID() {
    return destinationPinID;
  }

  public int getContentTypeID() {
    return contentTypeID;
  }

  public float getQuantity() {
    return quantity;
  }

  public List<Long> getWaypoints() {
    return waypoints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    PlanetaryRoute that = (PlanetaryRoute) o;
    return planetID == that.planetID &&
        routeID == that.routeID &&
        sourcePinID == that.sourcePinID &&
        destinationPinID == that.destinationPinID &&
        contentTypeID == that.contentTypeID &&
        Float.compare(that.quantity, quantity) == 0 &&
        nullSafeListCompare(waypoints, that.waypoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), planetID, routeID, sourcePinID, destinationPinID, contentTypeID, quantity,
                        waypoints);
  }

  @Override
  public String toString() {
    return "PlanetaryRoute{" +
        "planetID=" + planetID +
        ", routeID=" + routeID +
        ", sourcePinID=" + sourcePinID +
        ", destinationPinID=" + destinationPinID +
        ", contentTypeID=" + contentTypeID +
        ", quantity=" + quantity +
        ", waypoints=" + waypoints +
        '}';
  }

  public static PlanetaryRoute get(
      final SynchronizedEveAccount owner,
      final long time,
      final int planetID,
      final long routeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<PlanetaryRoute> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "PlanetaryRoute.getByPlanetAndRouteID",
                                                                                                         PlanetaryRoute.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("pid", planetID);
                                        getter.setParameter("rid", routeID);
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

  public static List<PlanetaryRoute> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector planetID,
      final AttributeSelector routeID,
      final AttributeSelector sourcePinID,
      final AttributeSelector destinationPinID,
      final AttributeSelector contentTypeID,
      final AttributeSelector quantity,
      final AttributeSelector waypoint) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT DISTINCT c FROM PlanetaryRoute c ");
                                        qs.append("JOIN c.waypoints d WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "planetID", planetID);
                                        AttributeSelector.addLongSelector(qs, "c", "routeID", routeID);
                                        AttributeSelector.addLongSelector(qs, "c", "sourcePinID", sourcePinID);
                                        AttributeSelector.addLongSelector(qs, "c", "destinationPinID",
                                                                          destinationPinID);
                                        AttributeSelector.addIntSelector(qs, "c", "contentTypeID", contentTypeID);
                                        AttributeSelector.addFloatSelector(qs, "c", "quantity", quantity);
                                        AttributeSelector.addLongSelector(qs, null, "d", waypoint);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<PlanetaryRoute> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        PlanetaryRoute.class);
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
