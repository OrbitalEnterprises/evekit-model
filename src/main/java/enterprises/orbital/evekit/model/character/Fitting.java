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
    name = "evekit_data_character_fitting",
    indexes = {
        @Index(
            name = "fittingIDIndex",
            columnList = "fittingID"),
    }
)
@NamedQueries({
    @NamedQuery(
        name = "Fitting.get",
        query = "SELECT c FROM Fitting c where c.owner = :owner and c.fittingID = :fid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Fitting extends CachedData {
  private static final Logger log = Logger.getLogger(Fitting.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_FITTINGS);

  private int fittingID;
  private String name;
  @Lob
  @Column(
      length = 102400)
  private String description;
  private int shipTypeID;

  @SuppressWarnings("unused")
  protected Fitting() {}

  public Fitting(int fittingID, String name, String description, int shipTypeID) {
    this.fittingID = fittingID;
    this.name = name;
    this.description = description;
    this.shipTypeID = shipTypeID;
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
    if (!(sup instanceof Fitting)) return false;
    Fitting other = (Fitting) sup;
    return fittingID == other.fittingID &&
        nullSafeObjectCompare(name, other.name) &&
        nullSafeObjectCompare(description, other.description) &&
        shipTypeID == other.shipTypeID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(fittingID, name, description, shipTypeID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getFittingID() {
    return fittingID;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  @Override
  public String toString() {
    return "Fitting{" +
        "fittingID=" + fittingID +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", shipTypeID=" + shipTypeID +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Fitting fitting = (Fitting) o;
    return fittingID == fitting.fittingID &&
        shipTypeID == fitting.shipTypeID &&
        Objects.equals(name, fitting.name) &&
        Objects.equals(description, fitting.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fittingID, name, description, shipTypeID);
  }

  public static Fitting get(
      final SynchronizedEveAccount owner,
      final long time,
      final int fittingID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Fitting> getter = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createNamedQuery(
                                                                                                  "Fitting.get",
                                                                                                  Fitting.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("fid", fittingID);
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

  public static List<Fitting> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector fittingID,
      final AttributeSelector name,
      final AttributeSelector description,
      final AttributeSelector shipTypeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Fitting c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "fittingID", fittingID);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        AttributeSelector.addStringSelector(qs, "c", "description", description, p);
                                        AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Fitting> query = EveKitUserAccountProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createQuery(
                                                                                                 qs.toString(),
                                                                                                 Fitting.class);
                                        query.setParameter("owner", owner);
                                        query.setMaxResults(maxresults);
                                        p.fillParams(query);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
