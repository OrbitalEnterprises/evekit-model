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
    name = "evekit_data_facility",
    indexes = {
        @Index(
            name = "facilityIDIndex",
            columnList = "facilityID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Facility.getByFacilityID",
        query = "SELECT c FROM Facility c where c.owner = :owner and c.facilityID = :facility and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Facility extends CachedData {
  private static final Logger log = Logger.getLogger(Facility.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS);

  private long facilityID;
  private int typeID;
  private int solarSystemID;

  @SuppressWarnings("unused")
  protected Facility() {}

  public Facility(long facilityID, int typeID, int solarSystemID) {
    this.facilityID = facilityID;
    this.typeID = typeID;
    this.solarSystemID = solarSystemID;
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
    if (!(sup instanceof Facility)) return false;
    Facility other = (Facility) sup;
    return facilityID == other.facilityID && typeID == other.typeID && solarSystemID == other.solarSystemID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(facilityID, typeID, solarSystemID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getFacilityID() {
    return facilityID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Facility facility = (Facility) o;
    return facilityID == facility.facilityID &&
        typeID == facility.typeID &&
        solarSystemID == facility.solarSystemID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), facilityID, typeID, solarSystemID);
  }

  @Override
  public String toString() {
    return "Facility{" +
        "facilityID=" + facilityID +
        ", typeID=" + typeID +
        ", solarSystemID=" + solarSystemID +
        '}';
  }

  public static Facility get(
      final SynchronizedEveAccount owner,
      final long time,
      final long facilityID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Facility> getter = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery(
                                                                                                   "Facility.getByFacilityID",
                                                                                                   Facility.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("facility", facilityID);
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

  public static List<Facility> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector facilityID,
      final AttributeSelector typeID,
      final AttributeSelector solarSystemID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Facility c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addLongSelector(qs, "c", "facilityID", facilityID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Facility> query = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(
                                                                                                  qs.toString(),
                                                                                                  Facility.class);
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
