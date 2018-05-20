package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_structures",
    indexes = {
        @Index(
            name = "structureIndex",
            columnList = "structureID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Structure.get",
        query = "SELECT c FROM Structure c where c.owner = :owner and c.structureID = :sid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Structure extends CachedData {
  private static final Logger log = Logger.getLogger(Structure.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STRUCTURES);

  private long structureID;
  private int corporationID;
  private long fuelExpires;
  private long nextReinforceApply;
  private int nextReinforceHour;
  private int nextReinforceWeekday;
  private int profileID;
  private int reinforceHour;
  private int reinforceWeekday;
  private String state;
  private long stateTimerEnd;
  private long stateTimerStart;
  private int systemID;
  private int typeID;
  private long unanchorsAt;

  @Transient
  @ApiModelProperty(
      value = "fuelExpires Date")
  @JsonProperty("fuelExpiresDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date fuelExpiresDate;

  @Transient
  @ApiModelProperty(
      value = "nextReinforceApply Date")
  @JsonProperty("nextReinforceApplyDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date nextReinforceApplyDate;

  @Transient
  @ApiModelProperty(
      value = "stateTimerEnd Date")
  @JsonProperty("stateTimerEndDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date stateTimerEndDate;

  @Transient
  @ApiModelProperty(
      value = "stateTimerStart Date")
  @JsonProperty("stateTimerStartDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date stateTimerStartDate;

  @Transient
  @ApiModelProperty(
      value = "unanchorsAt Date")
  @JsonProperty("unanchorsAtDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date unanchorsAtDate;

  @SuppressWarnings("unused")
  protected Structure() {}

  public Structure(long structureID, int corporationID, long fuelExpires, long nextReinforceApply,
                   int nextReinforceHour,
                   int nextReinforceWeekday, int profileID, int reinforceHour, int reinforceWeekday,
                   String state, long stateTimerEnd, long stateTimerStart, int systemID, int typeID, long unanchorsAt) {
    this.structureID = structureID;
    this.corporationID = corporationID;
    this.fuelExpires = fuelExpires;
    this.nextReinforceApply = nextReinforceApply;
    this.nextReinforceHour = nextReinforceHour;
    this.nextReinforceWeekday = nextReinforceWeekday;
    this.profileID = profileID;
    this.reinforceHour = reinforceHour;
    this.reinforceWeekday = reinforceWeekday;
    this.state = state;
    this.stateTimerEnd = stateTimerEnd;
    this.stateTimerStart = stateTimerStart;
    this.systemID = systemID;
    this.typeID = typeID;
    this.unanchorsAt = unanchorsAt;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    fuelExpiresDate = assignDateField(fuelExpires);
    nextReinforceApplyDate = assignDateField(nextReinforceApply);
    stateTimerEndDate = assignDateField(stateTimerEnd);
    stateTimerStartDate = assignDateField(stateTimerStart);
    unanchorsAtDate = assignDateField(unanchorsAt);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof Structure)) return false;
    Structure other = (Structure) sup;
    return structureID == other.structureID &&
        corporationID == other.corporationID &&
        fuelExpires == other.fuelExpires &&
        nextReinforceApply == other.nextReinforceApply &&
        nextReinforceHour == other.nextReinforceHour &&
        nextReinforceWeekday == other.nextReinforceWeekday &&
        profileID == other.profileID &&
        reinforceHour == other.reinforceHour &&
        reinforceWeekday == other.reinforceWeekday &&
        nullSafeObjectCompare(state, other.state) &&
        stateTimerEnd == other.stateTimerEnd &&
        stateTimerStart == other.stateTimerStart &&
        systemID == other.systemID &&
        typeID == other.typeID &&
        unanchorsAt == other.unanchorsAt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getStructureID() {
    return structureID;
  }

  public int getCorporationID() {
    return corporationID;
  }

  public long getFuelExpires() {
    return fuelExpires;
  }

  public long getNextReinforceApply() {
    return nextReinforceApply;
  }

  public int getNextReinforceHour() {
    return nextReinforceHour;
  }

  public int getNextReinforceWeekday() {
    return nextReinforceWeekday;
  }

  public int getProfileID() {
    return profileID;
  }

  public int getReinforceHour() {
    return reinforceHour;
  }

  public int getReinforceWeekday() {
    return reinforceWeekday;
  }

  public String getState() {
    return state;
  }

  public long getStateTimerEnd() {
    return stateTimerEnd;
  }

  public long getStateTimerStart() {
    return stateTimerStart;
  }

  public int getSystemID() {
    return systemID;
  }

  public int getTypeID() {
    return typeID;
  }

  public long getUnanchorsAt() {
    return unanchorsAt;
  }

  @Override
  public String toString() {
    return "Structure{" +
        "structureID=" + structureID +
        ", corporationID=" + corporationID +
        ", fuelExpires=" + fuelExpires +
        ", nextReinforceApply=" + nextReinforceApply +
        ", nextReinforceHour=" + nextReinforceHour +
        ", nextReinforceWeekday=" + nextReinforceWeekday +
        ", profileID=" + profileID +
        ", reinforceHour=" + reinforceHour +
        ", reinforceWeekday=" + reinforceWeekday +
        ", state='" + state + '\'' +
        ", stateTimerEnd=" + stateTimerEnd +
        ", stateTimerStart=" + stateTimerStart +
        ", systemID=" + systemID +
        ", typeID=" + typeID +
        ", unanchorsAt=" + unanchorsAt +
        ", fuelExpiresDate=" + fuelExpiresDate +
        ", nextReinforceApplyDate=" + nextReinforceApplyDate +
        ", stateTimerEndDate=" + stateTimerEndDate +
        ", stateTimerStartDate=" + stateTimerStartDate +
        ", unanchorsAtDate=" + unanchorsAtDate +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Structure structure = (Structure) o;
    return structureID == structure.structureID &&
        corporationID == structure.corporationID &&
        fuelExpires == structure.fuelExpires &&
        nextReinforceApply == structure.nextReinforceApply &&
        nextReinforceHour == structure.nextReinforceHour &&
        nextReinforceWeekday == structure.nextReinforceWeekday &&
        profileID == structure.profileID &&
        reinforceHour == structure.reinforceHour &&
        reinforceWeekday == structure.reinforceWeekday &&
        stateTimerEnd == structure.stateTimerEnd &&
        stateTimerStart == structure.stateTimerStart &&
        systemID == structure.systemID &&
        typeID == structure.typeID &&
        unanchorsAt == structure.unanchorsAt &&
        Objects.equals(state, structure.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), structureID, corporationID, fuelExpires, nextReinforceApply,
                        nextReinforceHour,
                        nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                        stateTimerStart, systemID, typeID, unanchorsAt);
  }

  public static Structure get(
      final SynchronizedEveAccount owner,
      final long time,
      final long structureID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Structure> getter = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery(
                                                                                                    "Structure.get",
                                                                                                    Structure.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("sid", structureID);
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

  public static List<Structure> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector structureID,
      final AttributeSelector corporationID,
      final AttributeSelector fuelExpires,
      final AttributeSelector nextReinforceApply,
      final AttributeSelector nextReinforceHour,
      final AttributeSelector nextReinforceWeekday,
      final AttributeSelector profileID,
      final AttributeSelector reinforceHour,
      final AttributeSelector reinforceWeekday,
      final AttributeSelector state,
      final AttributeSelector stateTimerEnd,
      final AttributeSelector stateTimerStart,
      final AttributeSelector systemID,
      final AttributeSelector typeID,
      final AttributeSelector unanchorsAt) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Structure c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "structureID", structureID);
                                        AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                        AttributeSelector.addLongSelector(qs, "c", "fuelExpires", fuelExpires);
                                        AttributeSelector.addLongSelector(qs, "c", "nextReinforceApply",
                                                                          nextReinforceApply);
                                        AttributeSelector.addIntSelector(qs, "c", "nextReinforceHour",
                                                                         nextReinforceHour);
                                        AttributeSelector.addIntSelector(qs, "c", "nextReinforceWeekday",
                                                                         nextReinforceWeekday);
                                        AttributeSelector.addIntSelector(qs, "c", "profileID", profileID);
                                        AttributeSelector.addIntSelector(qs, "c", "reinforceHour", reinforceHour);
                                        AttributeSelector.addIntSelector(qs, "c", "reinforceWeekday", reinforceWeekday);
                                        AttributeSelector.addStringSelector(qs, "c", "state", state, p);
                                        AttributeSelector.addLongSelector(qs, "c", "stateTimerEnd", stateTimerEnd);
                                        AttributeSelector.addLongSelector(qs, "c", "stateTimerStart", stateTimerStart);
                                        AttributeSelector.addIntSelector(qs, "c", "systemID", systemID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addLongSelector(qs, "c", "unanchorsAt", unanchorsAt);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Structure> query = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createQuery(
                                                                                                   qs.toString(),
                                                                                                   Structure.class);
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
