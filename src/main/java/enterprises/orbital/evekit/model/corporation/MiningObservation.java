package enterprises.orbital.evekit.model.corporation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_corporation_mining_observation",
    indexes = {
        @Index(
            name = "miningObservationIndex",
            columnList = "observerID,characterID,typeID"),
    }
)
@NamedQueries({
    @NamedQuery(
        name = "MiningObservation.get",
        query = "SELECT c FROM MiningObservation c where c.owner = :owner and c.observerID = :oid and c.characterID = :cid and c.typeID = :tid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MiningObservation extends CachedData {
  private static final Logger log = Logger.getLogger(MiningObservation.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER);

  private long observerID;
  private int characterID;
  private int typeID;
  private int recordedCorporationID;
  private long quantity;
  private long lastUpdated;

  @Transient
  @ApiModelProperty(
      value = "lastUpdated Date")
  @JsonProperty("lastUpdatedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastUpdatedDate;

  @SuppressWarnings("unused")
  protected MiningObservation() {}

  public MiningObservation(long observerID, int characterID, int typeID, int recordedCorporationID, long quantity,
                           long lastUpdated) {
    this.observerID = observerID;
    this.characterID = characterID;
    this.typeID = typeID;
    this.recordedCorporationID = recordedCorporationID;
    this.quantity = quantity;
    this.lastUpdated = lastUpdated;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    lastUpdatedDate = assignDateField(lastUpdated);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MiningObservation)) return false;
    MiningObservation other = (MiningObservation) sup;
    return observerID == other.observerID &&
        characterID == other.characterID &&
        typeID == other.typeID &&
        recordedCorporationID == other.recordedCorporationID &&
        quantity == other.quantity &&
        lastUpdated == other.lastUpdated;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getObserverID() {
    return observerID;
  }

  public int getCharacterID() {
    return characterID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getRecordedCorporationID() {
    return recordedCorporationID;
  }

  public long getQuantity() {
    return quantity;
  }

  public long getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public String toString() {
    return "MiningObservation{" +
        "observerID=" + observerID +
        ", characterID=" + characterID +
        ", typeID=" + typeID +
        ", recordedCorporationID=" + recordedCorporationID +
        ", quantity=" + quantity +
        ", lastUpdated=" + lastUpdated +
        ", lastUpdatedDate=" + lastUpdatedDate +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MiningObservation that = (MiningObservation) o;
    return observerID == that.observerID &&
        characterID == that.characterID &&
        typeID == that.typeID &&
        recordedCorporationID == that.recordedCorporationID &&
        quantity == that.quantity &&
        lastUpdated == that.lastUpdated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), observerID, characterID, typeID, recordedCorporationID, quantity,
                        lastUpdated);
  }

  public static MiningObservation get(
      final SynchronizedEveAccount owner,
      final long time,
      final long observerID,
      final int characterID,
      final int typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MiningObservation> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "MiningObservation.get",
                                                                                                            MiningObservation.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("oid", observerID);
                                        getter.setParameter("cid", characterID);
                                        getter.setParameter("tid", typeID);
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

  public static List<MiningObservation> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector observerID,
      final AttributeSelector characterID,
      final AttributeSelector typeID,
      final AttributeSelector recordedCorporationID,
      final AttributeSelector quantity,
      final AttributeSelector lastUpdated) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MiningObservation c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addLongSelector(qs, "c", "observerID", observerID);
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID",
                                                                         typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "recordedCorporationID",
                                                                         recordedCorporationID);
                                        AttributeSelector.addLongSelector(qs, "c", "quantity",
                                                                          quantity);
                                        AttributeSelector.addLongSelector(qs, "c", "lastUpdated",
                                                                          lastUpdated);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MiningObservation> query = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(
                                                                                                           qs.toString(),
                                                                                                           MiningObservation.class);
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
