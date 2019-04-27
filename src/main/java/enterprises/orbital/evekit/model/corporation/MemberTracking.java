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
    name = "evekit_data_member_tracking",
    indexes = {
        @Index(
            name = "characterIDIndex",
            columnList = "characterID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberTracking.getByCharacterID",
        query = "SELECT c FROM MemberTracking c where c.owner = :owner and c.characterID = :char and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MemberTracking extends CachedData {
  private static final Logger log = Logger.getLogger(MemberTracking.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_TRACKING);

  private int characterID;
  private int baseID;
  private long locationID;
  private long logoffDateTime = -1;
  private long logonDateTime = -1;
  private int shipTypeID;
  private long startDateTime = -1;

  @Transient
  @ApiModelProperty(
      value = "logoffDateTime Date")
  @JsonProperty("logoffDateTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date logoffDateTimeDate;
  @Transient
  @ApiModelProperty(
      value = "logonDateTime Date")
  @JsonProperty("logonDateTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date logonDateTimeDate;
  @Transient
  @ApiModelProperty(
      value = "startDateTime Date")
  @JsonProperty("startDateTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startDateTimeDate;

  @SuppressWarnings("unused")
  protected MemberTracking() {}

  public MemberTracking(int characterID, int baseID, long locationID, long logoffDateTime, long logonDateTime,
                        int shipTypeID, long startDateTime) {
    this.characterID = characterID;
    this.baseID = baseID;
    this.locationID = locationID;
    this.logoffDateTime = logoffDateTime;
    this.logonDateTime = logonDateTime;
    this.shipTypeID = shipTypeID;
    this.startDateTime = startDateTime;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    logoffDateTimeDate = assignDateField(logoffDateTime);
    logonDateTimeDate = assignDateField(logonDateTime);
    startDateTimeDate = assignDateField(startDateTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MemberTracking)) return false;
    MemberTracking other = (MemberTracking) sup;
    return characterID == other.characterID && baseID == other.baseID
        && locationID == other.locationID && logoffDateTime == other.logoffDateTime
        && logonDateTime == other.logonDateTime && shipTypeID == other.shipTypeID
        && startDateTime == other.startDateTime;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(characterID, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getCharacterID() {
    return characterID;
  }

  public int getBaseID() {
    return baseID;
  }

  public long getLocationID() {
    return locationID;
  }

  public long getLogoffDateTime() {
    return logoffDateTime;
  }

  public long getLogonDateTime() {
    return logonDateTime;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  public long getStartDateTime() {
    return startDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MemberTracking that = (MemberTracking) o;
    return characterID == that.characterID &&
        baseID == that.baseID &&
        locationID == that.locationID &&
        logoffDateTime == that.logoffDateTime &&
        logonDateTime == that.logonDateTime &&
        shipTypeID == that.shipTypeID &&
        startDateTime == that.startDateTime;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID,
                        startDateTime);
  }

  @Override
  public String toString() {
    return "MemberTracking{" +
        "characterID=" + characterID +
        ", baseID=" + baseID +
        ", locationID=" + locationID +
        ", logoffDateTime=" + logoffDateTime +
        ", logonDateTime=" + logonDateTime +
        ", shipTypeID=" + shipTypeID +
        ", startDateTime=" + startDateTime +
        ", logoffDateTimeDate=" + logoffDateTimeDate +
        ", logonDateTimeDate=" + logonDateTimeDate +
        ", startDateTimeDate=" + startDateTimeDate +
        '}';
  }

  public static MemberTracking get(
      final SynchronizedEveAccount owner,
      final long time,
      final int characterID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MemberTracking> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "MemberTracking.getByCharacterID",
                                                                                                         MemberTracking.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("char", characterID);
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

  public static List<MemberTracking> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector characterID,
      final AttributeSelector baseID,
      final AttributeSelector locationID,
      final AttributeSelector logoffDateTime,
      final AttributeSelector logonDateTime,
      final AttributeSelector shipTypeID,
      final AttributeSelector startDateTime) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MemberTracking c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addIntSelector(qs, "c", "baseID", baseID);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addLongSelector(qs, "c", "logoffDateTime", logoffDateTime);
                                        AttributeSelector.addLongSelector(qs, "c", "logonDateTime", logonDateTime);
                                        AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
                                        AttributeSelector.addLongSelector(qs, "c", "startDateTime", startDateTime);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MemberTracking> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        MemberTracking.class);
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
