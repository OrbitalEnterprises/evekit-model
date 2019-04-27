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
    name = "evekit_data_char_ship"
)
@NamedQueries({
    @NamedQuery(
        name = "CharacterShip.get",
        query = "SELECT c FROM CharacterShip c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterShip extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterShip.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_LOCATIONS);
  private int shipTypeID;
  private long shipItemID;
  private String shipName;

  @SuppressWarnings("unused")
  protected CharacterShip() {}

  public CharacterShip(int shipTypeID, long shipItemID, String shipName) {
    this.shipTypeID = shipTypeID;
    this.shipItemID = shipItemID;
    this.shipName = shipName;
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
    if (!(sup instanceof CharacterShip)) return false;
    CharacterShip other = (CharacterShip) sup;
    return shipTypeID == other.shipTypeID && shipItemID == other.shipItemID && nullSafeObjectCompare(shipName,
                                                                                                     other.shipName);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(shipTypeID, shipItemID, shipName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  public long getShipItemID() {
    return shipItemID;
  }

  public String getShipName() {
    return shipName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterShip that = (CharacterShip) o;
    return shipTypeID == that.shipTypeID &&
        shipItemID == that.shipItemID &&
        Objects.equals(shipName, that.shipName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), shipTypeID, shipItemID, shipName);
  }

  @Override
  public String toString() {
    return "CharacterShip{" +
        "shipTypeID=" + shipTypeID +
        ", shipItemID=" + shipItemID +
        ", shipName='" + shipName + '\'' +
        '}';
  }

  public static CharacterShip get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterShip> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(
                                                                                                        "CharacterShip.get",
                                                                                                        CharacterShip.class);
                                        getter.setParameter("owner", owner);
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

  public static List<CharacterShip> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector shipTypeID,
      final AttributeSelector shipItemID,
      final AttributeSelector shipName) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterShip c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
                                        AttributeSelector.addLongSelector(qs, "c", "shipItemID", shipItemID);
                                        AttributeSelector.addStringSelector(qs, "c", "shipName", shipName, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterShip> query = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createQuery(
                                                                                                       qs.toString(),
                                                                                                       CharacterShip.class);
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
