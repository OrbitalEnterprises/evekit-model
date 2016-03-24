package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_planetary_route",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID",
            unique = false),
        @Index(
            name = "routeIDIndex",
            columnList = "routeID",
            unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryRoute.getByPlanetAndRouteID",
        query = "SELECT c FROM PlanetaryRoute c where c.owner = :owner and c.planetID = :pid and c.routeID = :rid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "PlanetaryRoute.getAll",
        query = "SELECT c FROM PlanetaryRoute c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.routeID asc"),
    @NamedQuery(
        name = "PlanetaryRoute.getAllByPlanetID",
        query = "SELECT c FROM PlanetaryRoute c where c.owner = :owner and c.planetID = :pid and c.lifeStart <= :point and c.lifeEnd > :point order by c.routeID asc"),
})
// 1 hour cache time - API cache time unknown
public class PlanetaryRoute extends CachedData {
  private static final Logger log  = Logger.getLogger(PlanetaryRoute.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private long                planetID;
  private long                routeID;
  private long                sourcePinID;
  private long                destinationPinID;
  private int                 contentTypeID;
  private String              contentTypeName;
  private int                 quantity;
  private long                waypoint1;
  private long                waypoint2;
  private long                waypoint3;
  private long                waypoint4;
  private long                waypoint5;

  @SuppressWarnings("unused")
  private PlanetaryRoute() {}

  public PlanetaryRoute(long planetID, long routeID, long sourcePinID, long destinationPinID, int contentTypeID, String contentTypeName, int quantity,
                        long waypoint1, long waypoint2, long waypoint3, long waypoint4, long waypoint5) {
    super();
    this.planetID = planetID;
    this.routeID = routeID;
    this.sourcePinID = sourcePinID;
    this.destinationPinID = destinationPinID;
    this.contentTypeID = contentTypeID;
    this.contentTypeName = contentTypeName;
    this.quantity = quantity;
    this.waypoint1 = waypoint1;
    this.waypoint2 = waypoint2;
    this.waypoint3 = waypoint3;
    this.waypoint4 = waypoint4;
    this.waypoint5 = waypoint5;
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
        && contentTypeID == other.contentTypeID && nullSafeObjectCompare(contentTypeName, other.contentTypeName) && quantity == other.quantity
        && waypoint1 == other.waypoint1 && waypoint2 == other.waypoint2 && waypoint3 == other.waypoint3 && waypoint4 == other.waypoint4
        && waypoint5 == other.waypoint5;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getPlanetID() {
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

  public String getContentTypeName() {
    return contentTypeName;
  }

  public int getQuantity() {
    return quantity;
  }

  public long getWaypoint1() {
    return waypoint1;
  }

  public long getWaypoint2() {
    return waypoint2;
  }

  public long getWaypoint3() {
    return waypoint3;
  }

  public long getWaypoint4() {
    return waypoint4;
  }

  public long getWaypoint5() {
    return waypoint5;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + contentTypeID;
    result = prime * result + ((contentTypeName == null) ? 0 : contentTypeName.hashCode());
    result = prime * result + (int) (destinationPinID ^ (destinationPinID >>> 32));
    result = prime * result + (int) (planetID ^ (planetID >>> 32));
    result = prime * result + quantity;
    result = prime * result + (int) (routeID ^ (routeID >>> 32));
    result = prime * result + (int) (sourcePinID ^ (sourcePinID >>> 32));
    result = prime * result + (int) (waypoint1 ^ (waypoint1 >>> 32));
    result = prime * result + (int) (waypoint2 ^ (waypoint2 >>> 32));
    result = prime * result + (int) (waypoint3 ^ (waypoint3 >>> 32));
    result = prime * result + (int) (waypoint4 ^ (waypoint4 >>> 32));
    result = prime * result + (int) (waypoint5 ^ (waypoint5 >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PlanetaryRoute other = (PlanetaryRoute) obj;
    if (contentTypeID != other.contentTypeID) return false;
    if (contentTypeName == null) {
      if (other.contentTypeName != null) return false;
    } else if (!contentTypeName.equals(other.contentTypeName)) return false;
    if (destinationPinID != other.destinationPinID) return false;
    if (planetID != other.planetID) return false;
    if (quantity != other.quantity) return false;
    if (routeID != other.routeID) return false;
    if (sourcePinID != other.sourcePinID) return false;
    if (waypoint1 != other.waypoint1) return false;
    if (waypoint2 != other.waypoint2) return false;
    if (waypoint3 != other.waypoint3) return false;
    if (waypoint4 != other.waypoint4) return false;
    if (waypoint5 != other.waypoint5) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PlanetaryRoute [planetID=" + planetID + ", routeID=" + routeID + ", sourcePinID=" + sourcePinID + ", destinationPinID=" + destinationPinID
        + ", contentTypeID=" + contentTypeID + ", contentTypeName=" + contentTypeName + ", quantity=" + quantity + ", waypoint1=" + waypoint1 + ", waypoint2="
        + waypoint2 + ", waypoint3=" + waypoint3 + ", waypoint4=" + waypoint4 + ", waypoint5=" + waypoint5 + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static PlanetaryRoute get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final long planetID,
                                   final long routeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<PlanetaryRoute>() {
        @Override
        public PlanetaryRoute run() throws Exception {
          TypedQuery<PlanetaryRoute> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryRoute.getByPlanetAndRouteID",
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
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static List<PlanetaryRoute> getAllPlanetaryRoutes(
                                                           final SynchronizedEveAccount owner,
                                                           final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryRoute>>() {
        @Override
        public List<PlanetaryRoute> run() throws Exception {
          TypedQuery<PlanetaryRoute> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryRoute.getAll",
                                                                                                                         PlanetaryRoute.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<PlanetaryRoute> getAllPlanetaryRoutesByPlanet(
                                                                   final SynchronizedEveAccount owner,
                                                                   final long time,
                                                                   final long planetID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryRoute>>() {
        @Override
        public List<PlanetaryRoute> run() throws Exception {
          TypedQuery<PlanetaryRoute> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryRoute.getAllByPlanetID",
                                                                                                                         PlanetaryRoute.class);
          getter.setParameter("owner", owner);
          getter.setParameter("pid", planetID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<PlanetaryRoute> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final AttributeSelector at,
                                                 final AttributeSelector planetID,
                                                 final AttributeSelector routeID,
                                                 final AttributeSelector sourcePinID,
                                                 final AttributeSelector destinationPinID,
                                                 final AttributeSelector contentTypeID,
                                                 final AttributeSelector contentTypeName,
                                                 final AttributeSelector quantity,
                                                 final AttributeSelector waypoint1,
                                                 final AttributeSelector waypoint2,
                                                 final AttributeSelector waypoint3,
                                                 final AttributeSelector waypoint4,
                                                 final AttributeSelector waypoint5) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryRoute>>() {
        @Override
        public List<PlanetaryRoute> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM PlanetaryRoute c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "planetID", planetID);
          AttributeSelector.addLongSelector(qs, "c", "routeID", routeID);
          AttributeSelector.addLongSelector(qs, "c", "sourcePinID", sourcePinID);
          AttributeSelector.addLongSelector(qs, "c", "destinationPinID", destinationPinID);
          AttributeSelector.addIntSelector(qs, "c", "contentTypeID", contentTypeID);
          AttributeSelector.addStringSelector(qs, "c", "contentTypeName", contentTypeName, p);
          AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
          AttributeSelector.addLongSelector(qs, "c", "waypoint1", waypoint1);
          AttributeSelector.addLongSelector(qs, "c", "waypoint2", waypoint2);
          AttributeSelector.addLongSelector(qs, "c", "waypoint3", waypoint3);
          AttributeSelector.addLongSelector(qs, "c", "waypoint4", waypoint4);
          AttributeSelector.addLongSelector(qs, "c", "waypoint5", waypoint5);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<PlanetaryRoute> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), PlanetaryRoute.class);
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
