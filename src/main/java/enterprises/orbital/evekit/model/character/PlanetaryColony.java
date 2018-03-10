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
    name = "evekit_data_planetary_colony",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryColony.getByPlanetID",
        query = "SELECT c FROM PlanetaryColony c where c.owner = :owner and c.planetID = :pid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class PlanetaryColony extends CachedData {
  private static final Logger log = Logger.getLogger(PlanetaryColony.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);

  private int planetID;
  private int solarSystemID;
  private String planetType;
  private int ownerID;
  private long lastUpdate = -1;
  private int upgradeLevel;
  private int numberOfPins;

  @Transient
  @ApiModelProperty(
      value = "lastUpdate Date")
  @JsonProperty("lastUpdateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastUpdateDate;

  @SuppressWarnings("unused")
  protected PlanetaryColony() {}

  public PlanetaryColony(int planetID, int solarSystemID, String planetType, int ownerID, long lastUpdate,
                         int upgradeLevel, int numberOfPins) {
    this.planetID = planetID;
    this.solarSystemID = solarSystemID;
    this.planetType = planetType;
    this.ownerID = ownerID;
    this.lastUpdate = lastUpdate;
    this.upgradeLevel = upgradeLevel;
    this.numberOfPins = numberOfPins;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    lastUpdateDate = assignDateField(lastUpdate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof PlanetaryColony)) return false;
    PlanetaryColony other = (PlanetaryColony) sup;
    return planetID == other.planetID && solarSystemID == other.solarSystemID
        && nullSafeObjectCompare(planetType, other.planetType) && ownerID == other.ownerID
        && lastUpdate == other.lastUpdate && upgradeLevel == other.upgradeLevel && numberOfPins == other.numberOfPins;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getPlanetID() {
    return planetID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getPlanetType() {
    return planetType;
  }

  public long getOwnerID() {
    return ownerID;
  }

  public long getLastUpdate() {
    return lastUpdate;
  }

  public int getUpgradeLevel() {
    return upgradeLevel;
  }

  public int getNumberOfPins() {
    return numberOfPins;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    PlanetaryColony that = (PlanetaryColony) o;
    return planetID == that.planetID &&
        solarSystemID == that.solarSystemID &&
        ownerID == that.ownerID &&
        lastUpdate == that.lastUpdate &&
        upgradeLevel == that.upgradeLevel &&
        numberOfPins == that.numberOfPins &&
        Objects.equals(planetType, that.planetType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), planetID, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel,
                        numberOfPins);
  }

  @Override
  public String toString() {
    return "PlanetaryColony{" +
        "planetID=" + planetID +
        ", solarSystemID=" + solarSystemID +
        ", planetType='" + planetType + '\'' +
        ", ownerID=" + ownerID +
        ", lastUpdate=" + lastUpdate +
        ", upgradeLevel=" + upgradeLevel +
        ", numberOfPins=" + numberOfPins +
        ", lastUpdateDate=" + lastUpdateDate +
        '}';
  }

  /**
   * Retrieve planetary colony with the given properties live at the given time, or null if no such colony exists.
   *
   * @param owner    planetary colony owner
   * @param time     time at which property colony must be live
   * @param planetID planet ID of planet where colony is located
   * @return planetary colony with the given properties live at the given time, or null
   */
  public static PlanetaryColony get(
      final SynchronizedEveAccount owner,
      final long time,
      final int planetID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<PlanetaryColony> getter = EveKitUserAccountProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createNamedQuery(
                                                                                                          "PlanetaryColony.getByPlanetID",
                                                                                                          PlanetaryColony.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("pid", planetID);
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

  public static List<PlanetaryColony> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector planetID,
      final AttributeSelector solarSystemID,
      final AttributeSelector planetType,
      final AttributeSelector ownerID,
      final AttributeSelector lastUpdate,
      final AttributeSelector upgradeLevel,
      final AttributeSelector numberOfPins) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM PlanetaryColony c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "planetID", planetID);
                                        AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                        AttributeSelector.addStringSelector(qs, "c", "planetType", planetType, p);
                                        AttributeSelector.addLongSelector(qs, "c", "ownerID", ownerID);
                                        AttributeSelector.addLongSelector(qs, "c", "lastUpdate", lastUpdate);
                                        AttributeSelector.addIntSelector(qs, "c", "upgradeLevel", upgradeLevel);
                                        AttributeSelector.addIntSelector(qs, "c", "numberOfPins", numberOfPins);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<PlanetaryColony> query = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createQuery(
                                                                                                         qs.toString(),
                                                                                                         PlanetaryColony.class);
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
