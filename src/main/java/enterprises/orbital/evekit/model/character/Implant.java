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
    name = "evekit_data_implant",
    indexes = {
        @Index(
            name = "typeIDIndex",
            columnList = "typeID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Implant.getByTypeID",
        query = "SELECT c FROM Implant c where c.owner = :owner and c.typeID = :type and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Implant.getAll",
        query = "SELECT c FROM Implant c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
public class Implant extends CachedData {
  private static final Logger log = Logger.getLogger(Implant.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  private int typeID;

  @SuppressWarnings("unused")
  protected Implant() {}

  public Implant(int typeID) {
    super();
    this.typeID = typeID;
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
    if (!(sup instanceof Implant)) return false;
    Implant other = (Implant) sup;
    return typeID == other.typeID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(typeID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getTypeID() {
    return typeID;
  }

  @Override
  public String toString() {
    return "Implant{" +
        "typeID=" + typeID +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Implant implant = (Implant) o;
    return typeID == implant.typeID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), typeID);
  }

  public static Implant get(
      final SynchronizedEveAccount owner,
      final long time,
      final int typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Implant> getter = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createNamedQuery(
                                                                                                  "Implant.getByTypeID",
                                                                                                  Implant.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("type", typeID);
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

  public static List<Implant> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Implant c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Implant> query = EveKitUserAccountProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createQuery(qs.toString(),
                                                                                                          Implant.class);
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
