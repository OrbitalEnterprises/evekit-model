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
    name = "evekit_data_char_location"
)
@NamedQueries({
    @NamedQuery(
        name = "CharacterLocation.get",
        query = "SELECT c FROM CharacterLocation c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterLocation extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterLocation.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_LOCATIONS);
  private int solarSystemID;
  private int stationID;
  private long structureID;

  @SuppressWarnings("unused")
  protected CharacterLocation() {}

  public CharacterLocation(int solarSystemID, int stationID, long structureID) {
    super();
    this.solarSystemID = solarSystemID;
    this.stationID = stationID;
    this.structureID = structureID;
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
    if (!(sup instanceof CharacterLocation)) return false;
    CharacterLocation other = (CharacterLocation) sup;
    return solarSystemID == other.solarSystemID && stationID == other.stationID && structureID == other.structureID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public int getStationID() {
    return stationID;
  }

  public long getStructureID() {
    return structureID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterLocation that = (CharacterLocation) o;
    return solarSystemID == that.solarSystemID &&
        stationID == that.stationID &&
        structureID == that.structureID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), solarSystemID, stationID, structureID);
  }

  @Override
  public String toString() {
    return "CharacterLocation{" +
        "solarSystemID=" + solarSystemID +
        ", stationID=" + stationID +
        ", structureID=" + structureID +
        '}';
  }

  public static CharacterLocation get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterLocation> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "CharacterLocation.get",
                                                                                                            CharacterLocation.class);
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

  public static List<CharacterLocation> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector solarSystemID,
      final AttributeSelector stationID,
      final AttributeSelector structureID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterLocation c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                        AttributeSelector.addIntSelector(qs, "c", "stationID", stationID);
                                        AttributeSelector.addLongSelector(qs, "c", "structureID", structureID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterLocation> query = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(
                                                                                                           qs.toString(),
                                                                                                           CharacterLocation.class);
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
