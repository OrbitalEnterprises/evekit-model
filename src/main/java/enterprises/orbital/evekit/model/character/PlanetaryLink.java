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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_planetary_link",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID",
            unique = false),
        @Index(
            name = "sourcePinIDIndex",
            columnList = "sourcePinID",
            unique = false),
        @Index(
            name = "destinationPinIDIndex",
            columnList = "destinationPinID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryLink.getByPlanetAndSourceAndDestID",
        query = "SELECT c FROM PlanetaryLink c where c.owner = :owner and c.planetID = :planet and c.sourcePinID = :source and c.destinationPinID = :dest and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "PlanetaryLink.getAll",
        query = "SELECT c FROM PlanetaryLink c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "PlanetaryLink.getAllByPlanetID",
        query = "SELECT c FROM PlanetaryLink c where c.owner = :owner and c.planetID = :planet and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API cache time unknown
public class PlanetaryLink extends CachedData {
  private static final Logger log  = Logger.getLogger(PlanetaryLink.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private long                planetID;
  private long                sourcePinID;
  private long                destinationPinID;
  private int                 linkLevel;

  @SuppressWarnings("unused")
  protected PlanetaryLink() {}

  public PlanetaryLink(long planetID, long sourcePinID, long destinationPinID, int linkLevel) {
    super();
    this.planetID = planetID;
    this.sourcePinID = sourcePinID;
    this.destinationPinID = destinationPinID;
    this.linkLevel = linkLevel;
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
    if (!(sup instanceof PlanetaryLink)) return false;
    PlanetaryLink other = (PlanetaryLink) sup;
    return planetID == other.planetID && sourcePinID == other.sourcePinID && destinationPinID == other.destinationPinID && linkLevel == other.linkLevel;
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

  public long getSourcePinID() {
    return sourcePinID;
  }

  public long getDestinationPinID() {
    return destinationPinID;
  }

  public int getLinkLevel() {
    return linkLevel;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (destinationPinID ^ (destinationPinID >>> 32));
    result = prime * result + linkLevel;
    result = prime * result + (int) (planetID ^ (planetID >>> 32));
    result = prime * result + (int) (sourcePinID ^ (sourcePinID >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PlanetaryLink other = (PlanetaryLink) obj;
    if (destinationPinID != other.destinationPinID) return false;
    if (linkLevel != other.linkLevel) return false;
    if (planetID != other.planetID) return false;
    if (sourcePinID != other.sourcePinID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PlanetaryLink [planetID=" + planetID + ", sourcePinID=" + sourcePinID + ", destinationPinID=" + destinationPinID + ", linkLevel=" + linkLevel
        + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve planetary link with given properties live at the given time, or null if no such link exists.
   * 
   * @param owner
   *          planetary link owner
   * @param time
   *          time at which planetary link must be live
   * @param planetID
   *          planet ID of link
   * @param sourcePin
   *          source pin ID of link
   * @param destPin
   *          destination pin ID of link
   * @return planetary link with given properties live at the given time, or null
   */
  public static PlanetaryLink get(
                                  final SynchronizedEveAccount owner,
                                  final long time,
                                  final long planetID,
                                  final long sourcePin,
                                  final long destPin) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<PlanetaryLink>() {
        @Override
        public PlanetaryLink run() throws Exception {
          TypedQuery<PlanetaryLink> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("PlanetaryLink.getByPlanetAndSourceAndDestID", PlanetaryLink.class);
          getter.setParameter("owner", owner);
          getter.setParameter("planet", planetID);
          getter.setParameter("source", sourcePin);
          getter.setParameter("dest", destPin);
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
   * Retrieve list of all planetary colonies live at the given time.
   * 
   * @param owner
   *          planetary links owner
   * @param time
   *          time at which planetary links must be live
   * @return list of all planetary colonies live at the given time
   */
  public static List<PlanetaryLink> getAllPlanetaryLinks(
                                                         final SynchronizedEveAccount owner,
                                                         final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryLink>>() {
        @Override
        public List<PlanetaryLink> run() throws Exception {
          TypedQuery<PlanetaryLink> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryLink.getAll",
                                                                                                                        PlanetaryLink.class);
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

  /**
   * Retrieve list of planetary links for the given planet ID, live at the given time.
   * 
   * @param owner
   *          planetary links owner
   * @param time
   *          time at which planetary links must be live
   * @param planetID
   *          planet ID for which links will be retrieved
   * @return list of planetary links for the given planet ID, live at the given time
   */
  public static List<PlanetaryLink> getAllPlanetaryLinksByPlanet(
                                                                 final SynchronizedEveAccount owner,
                                                                 final long time,
                                                                 final long planetID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryLink>>() {
        @Override
        public List<PlanetaryLink> run() throws Exception {
          TypedQuery<PlanetaryLink> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryLink.getAllByPlanetID",
                                                                                                                        PlanetaryLink.class);
          getter.setParameter("owner", owner);
          getter.setParameter("planet", planetID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<PlanetaryLink> accessQuery(
                                                final SynchronizedEveAccount owner,
                                                final long contid,
                                                final int maxresults,
                                                final boolean reverse,
                                                final AttributeSelector at,
                                                final AttributeSelector planetID,
                                                final AttributeSelector sourcePinID,
                                                final AttributeSelector destinationPinID,
                                                final AttributeSelector linkLevel) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryLink>>() {
        @Override
        public List<PlanetaryLink> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM PlanetaryLink c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "planetID", planetID);
          AttributeSelector.addLongSelector(qs, "c", "sourcePinID", sourcePinID);
          AttributeSelector.addLongSelector(qs, "c", "destinationPinID", destinationPinID);
          AttributeSelector.addIntSelector(qs, "c", "linkLevel", linkLevel);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<PlanetaryLink> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), PlanetaryLink.class);
          query.setParameter("owner", owner);
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
