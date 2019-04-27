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
    name = "evekit_data_character_fleets",
    indexes = {
        @Index(
            name = "characterFleetIndex",
            columnList = "fleetID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterFleet.get",
        query = "SELECT c FROM CharacterFleet c where c.owner = :owner and c.fleetID = :fid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterFleet extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterFleet.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS);

  private long fleetID;
  private String role;
  private long squadID;
  private long wingID;

  @SuppressWarnings("unused")
  protected CharacterFleet() {}

  public CharacterFleet(long fleetID, String role, long squadID, long wingID) {
    this.fleetID = fleetID;
    this.role = role;
    this.squadID = squadID;
    this.wingID = wingID;
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
    if (!(sup instanceof CharacterFleet)) return false;
    CharacterFleet other = (CharacterFleet) sup;
    return fleetID == other.fleetID &&
        nullSafeObjectCompare(role, other.role) &&
        squadID == other.squadID &&
        wingID == other.wingID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(fleetID, role, squadID, wingID);
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

  public String getRole() {
    return role;
  }

  public long getSquadID() {
    return squadID;
  }

  public long getWingID() {
    return wingID;
  }

  @Override
  public String toString() {
    return "CharacterFleet{" +
        "fleetID=" + fleetID +
        ", role='" + role + '\'' +
        ", squadID=" + squadID +
        ", wingID=" + wingID +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterFleet that = (CharacterFleet) o;
    return fleetID == that.fleetID &&
        squadID == that.squadID &&
        wingID == that.wingID &&
        Objects.equals(role, that.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fleetID, role, squadID, wingID);
  }

  public static CharacterFleet get(
      final SynchronizedEveAccount owner,
      final long time,
      final long fleetID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterFleet> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                  "CharacterFleet.get",
                                                                                                  CharacterFleet.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("fid", fleetID);
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

  public static List<CharacterFleet> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector fleetID,
      final AttributeSelector role,
      final AttributeSelector squadID,
      final AttributeSelector wingID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterFleet c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "fleetID", fleetID);
                                        AttributeSelector.addStringSelector(qs, "c", "role", role, p);
                                        AttributeSelector.addLongSelector(qs, "c", "squadID", squadID);
                                        AttributeSelector.addLongSelector(qs, "c", "wingID", wingID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterFleet> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(qs.toString(),
                                                                                                          CharacterFleet.class);
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
