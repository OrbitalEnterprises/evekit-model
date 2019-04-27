package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_fuel",
    indexes = {
        @Index(
            name = "starbaseIDIndex",
            columnList = "starbaseID"),
        @Index(
            name = "typeIDIndex",
            columnList = "typeID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Fuel.getByItemAndTypeID",
        query = "SELECT c FROM Fuel c where c.owner = :owner and c.starbaseID = :sb and c.typeID = :tp and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Fuel extends CachedData {
  private static final Logger log = Logger.getLogger(Fuel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST);

  private long starbaseID;
  private int typeID;
  private int quantity;

  @SuppressWarnings("unused")
  protected Fuel() {}

  public Fuel(long starbaseID, int typeID, int quantity) {
    super();
    this.starbaseID = starbaseID;
    this.typeID = typeID;
    this.quantity = quantity;
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
    if (!(sup instanceof Fuel)) return false;
    Fuel other = (Fuel) sup;
    return starbaseID == other.starbaseID && typeID == other.typeID && quantity == other.quantity;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(starbaseID, typeID, quantity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getStarbaseID() {
    return starbaseID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getQuantity() {
    return quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Fuel fuel = (Fuel) o;
    return starbaseID == fuel.starbaseID &&
        typeID == fuel.typeID &&
        quantity == fuel.quantity;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), starbaseID, typeID, quantity);
  }

  @Override
  public String toString() {
    return "Fuel{" +
        "starbaseID=" + starbaseID +
        ", typeID=" + typeID +
        ", quantity=" + quantity +
        '}';
  }

  public static Fuel get(
      final SynchronizedEveAccount owner,
      final long time,
      final long starbaseID,
      final int typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Fuel> getter = EveKitUserAccountProvider.getFactory()
                                                                                           .getEntityManager()
                                                                                           .createNamedQuery(
                                                                                               "Fuel.getByItemAndTypeID",
                                                                                               Fuel.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("sb", starbaseID);
                                        getter.setParameter("tp", typeID);
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

  public static List<Fuel> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector starbaseID,
      final AttributeSelector typeID,
      final AttributeSelector quantity) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Fuel c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addLongSelector(qs, "c", "starbaseID", starbaseID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Fuel> query = EveKitUserAccountProvider.getFactory()
                                                                                          .getEntityManager()
                                                                                          .createQuery(qs.toString(),
                                                                                                       Fuel.class);
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
