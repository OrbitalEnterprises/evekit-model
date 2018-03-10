package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_planetary_link",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID"),
        @Index(
            name = "sourcePinIDIndex",
            columnList = "sourcePinID"),
        @Index(
            name = "destinationPinIDIndex",
            columnList = "destinationPinID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryLink.getByPlanetAndSourceAndDestID",
        query = "SELECT c FROM PlanetaryLink c where c.owner = :owner and c.planetID = :planet and c.sourcePinID = :source and c.destinationPinID = :dest and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class PlanetaryLink extends CachedData {
  private static final Logger log = Logger.getLogger(PlanetaryLink.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);

  private int planetID;
  private long sourcePinID;
  private long destinationPinID;
  private int linkLevel;

  @SuppressWarnings("unused")
  protected PlanetaryLink() {}

  public PlanetaryLink(int planetID, long sourcePinID, long destinationPinID, int linkLevel) {
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
  public void prepareTransient() {
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

  public int getPlanetID() {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    PlanetaryLink that = (PlanetaryLink) o;
    return planetID == that.planetID &&
        sourcePinID == that.sourcePinID &&
        destinationPinID == that.destinationPinID &&
        linkLevel == that.linkLevel;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), planetID, sourcePinID, destinationPinID, linkLevel);
  }

  @Override
  public String toString() {
    return "PlanetaryLink{" +
        "planetID=" + planetID +
        ", sourcePinID=" + sourcePinID +
        ", destinationPinID=" + destinationPinID +
        ", linkLevel=" + linkLevel +
        '}';
  }

  /**
   * Retrieve planetary link with given properties live at the given time, or null if no such link exists.
   *
   * @param owner     planetary link owner
   * @param time      time at which planetary link must be live
   * @param planetID  planet ID of link
   * @param sourcePin source pin ID of link
   * @param destPin   destination pin ID of link
   * @return planetary link with given properties live at the given time, or null
   */
  public static PlanetaryLink get(
      final SynchronizedEveAccount owner,
      final long time,
      final int planetID,
      final long sourcePin,
      final long destPin) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<PlanetaryLink> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(
                                                                                                        "PlanetaryLink.getByPlanetAndSourceAndDestID",
                                                                                                        PlanetaryLink.class);
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
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
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
      final AttributeSelector linkLevel) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM PlanetaryLink c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "planetID", planetID);
                                        AttributeSelector.addLongSelector(qs, "c", "sourcePinID", sourcePinID);
                                        AttributeSelector.addLongSelector(qs, "c", "destinationPinID",
                                                                          destinationPinID);
                                        AttributeSelector.addIntSelector(qs, "c", "linkLevel", linkLevel);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<PlanetaryLink> query = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createQuery(
                                                                                                       qs.toString(),
                                                                                                       PlanetaryLink.class);
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
