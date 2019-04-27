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
    name = "evekit_data_container_log",
    indexes = {
        @Index(
            name = "logTimeIndex",
            columnList = "logTime"),
    })
@NamedQueries({
    @NamedQuery(
        name = "ContainerLog.getByLogTime",
        query = "SELECT c FROM ContainerLog c where c.owner = :owner and c.containerID = :cid and c.logTime = :time and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class ContainerLog extends CachedData {
  private static final Logger log = Logger.getLogger(ContainerLog.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTAINER_LOG);
  private long logTime = -1;
  private String action;
  private int characterID;
  private String locationFlag;
  private long containerID;
  private int containerTypeID;
  private long locationID;
  private int newConfiguration;
  private int oldConfiguration;
  private String passwordType;
  private int quantity;
  private int typeID;
  @Transient
  @ApiModelProperty(
      value = "logTime Date")
  @JsonProperty("logTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date logTimeDate;

  @SuppressWarnings("unused")
  protected ContainerLog() {}

  public ContainerLog(long logTime, String action, int characterID, String locationFlag, long containerID,
                      int containerTypeID, long locationID, int newConfiguration, int oldConfiguration,
                      String passwordType, int quantity, int typeID) {
    super();
    this.logTime = logTime;
    this.action = action;
    this.characterID = characterID;
    this.locationFlag = locationFlag;
    this.containerID = containerID;
    this.containerTypeID = containerTypeID;
    this.locationID = locationID;
    this.newConfiguration = newConfiguration;
    this.oldConfiguration = oldConfiguration;
    this.passwordType = passwordType;
    this.quantity = quantity;
    this.typeID = typeID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    logTimeDate = assignDateField(logTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof ContainerLog)) return false;
    ContainerLog other = (ContainerLog) sup;
    return logTime == other.logTime && nullSafeObjectCompare(action, other.action) && characterID == other.characterID
        && nullSafeObjectCompare(locationFlag,
                                 other.locationFlag) && containerID == other.containerID && containerTypeID == other.containerTypeID
        && locationID == other.locationID && newConfiguration == other.newConfiguration
        && oldConfiguration == other.oldConfiguration
        && nullSafeObjectCompare(passwordType,
                                 other.passwordType) && quantity == other.quantity && typeID == other.typeID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID,
                          newConfiguration, oldConfiguration, passwordType, quantity, typeID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getLogTime() {
    return logTime;
  }

  public String getAction() {
    return action;
  }

  public long getLocationID() {
    return locationID;
  }

  public String getPasswordType() {
    return passwordType;
  }

  public long getQuantity() {
    return quantity;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getCharacterID() {
    return characterID;
  }

  public String getLocationFlag() {
    return locationFlag;
  }

  public long getContainerID() {
    return containerID;
  }

  public int getContainerTypeID() {
    return containerTypeID;
  }

  public int getNewConfiguration() {
    return newConfiguration;
  }

  public int getOldConfiguration() {
    return oldConfiguration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ContainerLog that = (ContainerLog) o;
    return logTime == that.logTime &&
        characterID == that.characterID &&
        containerID == that.containerID &&
        containerTypeID == that.containerTypeID &&
        locationID == that.locationID &&
        newConfiguration == that.newConfiguration &&
        oldConfiguration == that.oldConfiguration &&
        quantity == that.quantity &&
        typeID == that.typeID &&
        Objects.equals(action, that.action) &&
        Objects.equals(locationFlag, that.locationFlag) &&
        Objects.equals(passwordType, that.passwordType) &&
        Objects.equals(logTimeDate, that.logTimeDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), logTime, action, characterID, locationFlag, containerID, containerTypeID,
                        locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID, logTimeDate);
  }

  @Override
  public String toString() {
    return "ContainerLog{" +
        "logTime=" + logTime +
        ", action='" + action + '\'' +
        ", characterID=" + characterID +
        ", locationFlag='" + locationFlag + '\'' +
        ", containerID=" + containerID +
        ", containerTypeID=" + containerTypeID +
        ", locationID=" + locationID +
        ", newConfiguration=" + newConfiguration +
        ", oldConfiguration=" + oldConfiguration +
        ", passwordType='" + passwordType + '\'' +
        ", quantity=" + quantity +
        ", typeID=" + typeID +
        ", logTimeDate=" + logTimeDate +
        '}';
  }

  public static ContainerLog get(
      final SynchronizedEveAccount owner,
      final long time,
      final long containerID,
      final long logTime) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ContainerLog> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "ContainerLog.getByLogTime",
                                                                                                       ContainerLog.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("cid", containerID);
                                        getter.setParameter("time", logTime);
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

  public static List<ContainerLog> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector logTime,
      final AttributeSelector action,
      final AttributeSelector characterID,
      final AttributeSelector locationFlag,
      final AttributeSelector containerID,
      final AttributeSelector containerTypeID,
      final AttributeSelector locationID,
      final AttributeSelector newConfiguration,
      final AttributeSelector oldConfiguration,
      final AttributeSelector passwordType,
      final AttributeSelector quantity,
      final AttributeSelector typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ContainerLog c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "logTime", logTime);
                                        AttributeSelector.addStringSelector(qs, "c", "action", action, p);
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addStringSelector(qs, "c", "locationFlag", locationFlag, p);
                                        AttributeSelector.addLongSelector(qs, "c", "containerID", containerID);
                                        AttributeSelector.addIntSelector(qs, "c", "containerTypeID", containerTypeID);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addIntSelector(qs, "c", "newConfiguration", newConfiguration);
                                        AttributeSelector.addIntSelector(qs, "c", "oldConfiguration", oldConfiguration);
                                        AttributeSelector.addStringSelector(qs, "c", "passwordType", passwordType, p);
                                        AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ContainerLog> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      ContainerLog.class);
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
