package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
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
    name = "evekit_data_fleet_members",
    indexes = {
        @Index(
            name = "fleetMemberIndex",
            columnList = "fleetID,characterID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "FleetMember.get",
        query = "SELECT c FROM FleetMember c where c.owner = :owner and c.fleetID = :fid and c.characterID = :cid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class FleetMember extends CachedData {
  private static final Logger log = Logger.getLogger(FleetMember.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS);

  private long fleetID;
  private int characterID;
  private long joinTime;
  private String role;
  private String roleName;
  private int shipTypeID;
  private int solarSystemID;
  private long squadID;
  private long stationID;
  private boolean takesFleetWarp;
  private long wingID;

  @Transient
  @ApiModelProperty(
      value = "joinTime Date")
  @JsonProperty("joinTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date joinTimeDate;

  @SuppressWarnings("unused")
  protected FleetMember() {}

  public FleetMember(long fleetID, int characterID, long joinTime, String role, String roleName, int shipTypeID,
                     int solarSystemID, long squadID, long stationID, boolean takesFleetWarp, long wingID) {
    this.fleetID = fleetID;
    this.characterID = characterID;
    this.joinTime = joinTime;
    this.role = role;
    this.roleName = roleName;
    this.shipTypeID = shipTypeID;
    this.solarSystemID = solarSystemID;
    this.squadID = squadID;
    this.stationID = stationID;
    this.takesFleetWarp = takesFleetWarp;
    this.wingID = wingID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    joinTimeDate = assignDateField(joinTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof FleetMember)) return false;
    FleetMember other = (FleetMember) sup;
    return fleetID == other.fleetID &&
        characterID == other.characterID &&
        joinTime == other.joinTime &&
        nullSafeObjectCompare(role, other.role) &&
        nullSafeObjectCompare(roleName, other.roleName) &&
        shipTypeID == other.shipTypeID &&
        solarSystemID == other.solarSystemID &&
        squadID == other.squadID &&
        stationID == other.stationID &&
        takesFleetWarp == other.takesFleetWarp &&
        wingID == other.wingID;
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

  public int getCharacterID() {
    return characterID;
  }

  public long getJoinTime() {
    return joinTime;
  }

  public String getRole() {
    return role;
  }

  public String getRoleName() {
    return roleName;
  }

  public int getShipTypeID() {
    return shipTypeID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public long getSquadID() {
    return squadID;
  }

  public long getStationID() {
    return stationID;
  }

  public boolean isTakesFleetWarp() {
    return takesFleetWarp;
  }

  public long getWingID() {
    return wingID;
  }

  @Override
  public String toString() {
    return "FleetMember{" +
        "fleetID=" + fleetID +
        ", characterID=" + characterID +
        ", joinTime=" + joinTime +
        ", role='" + role + '\'' +
        ", roleName='" + roleName + '\'' +
        ", shipTypeID=" + shipTypeID +
        ", solarSystemID=" + solarSystemID +
        ", squadID=" + squadID +
        ", stationID=" + stationID +
        ", takesFleetWarp=" + takesFleetWarp +
        ", wingID=" + wingID +
        ", joinTimeDate=" + joinTimeDate +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FleetMember that = (FleetMember) o;
    return fleetID == that.fleetID &&
        characterID == that.characterID &&
        joinTime == that.joinTime &&
        shipTypeID == that.shipTypeID &&
        solarSystemID == that.solarSystemID &&
        squadID == that.squadID &&
        stationID == that.stationID &&
        takesFleetWarp == that.takesFleetWarp &&
        wingID == that.wingID &&
        Objects.equals(role, that.role) &&
        Objects.equals(roleName, that.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID,
                        squadID, stationID, takesFleetWarp, wingID);
  }

  public static FleetMember get(
      final SynchronizedEveAccount owner,
      final long time,
      final long fleetID,
      final int characterID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<FleetMember> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                  "FleetMember.get",
                                                                                                  FleetMember.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("fid", fleetID);
                                        getter.setParameter("cid", characterID);
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

  public static List<FleetMember> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector fleetID,
      final AttributeSelector characterID,
      final AttributeSelector joinTime,
      final AttributeSelector role,
      final AttributeSelector roleName,
      final AttributeSelector shipTypeID,
      final AttributeSelector solarSystemID,
      final AttributeSelector squadID,
      final AttributeSelector stationID,
      final AttributeSelector takesFleetWarp,
      final AttributeSelector wingID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM FleetMember c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "fleetID", fleetID);
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addLongSelector(qs, "c", "joinTime", joinTime);
                                        AttributeSelector.addStringSelector(qs, "c", "role", role, p);
                                        AttributeSelector.addStringSelector(qs, "c", "roleName", roleName, p);
                                        AttributeSelector.addIntSelector(qs, "c", "shipTypeID", shipTypeID);
                                        AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                        AttributeSelector.addLongSelector(qs, "c", "squadID", squadID);
                                        AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
                                        AttributeSelector.addBooleanSelector(qs, "c", "takesFleetWarp", takesFleetWarp);
                                        AttributeSelector.addLongSelector(qs, "c", "wingID", wingID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<FleetMember> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(qs.toString(),
                                                                                                          FleetMember.class);
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
