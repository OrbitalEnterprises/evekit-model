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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_planetary_pin",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID"),
        @Index(
            name = "pinIDIndex",
            columnList = "pinID")
    })
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryPin.getByPlanetAndPinID",
        query = "SELECT c FROM PlanetaryPin c where c.owner = :owner and c.planetID = :planet and c.pinID = :pin and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class PlanetaryPin extends CachedData {
  private static final Logger log = Logger.getLogger(PlanetaryPin.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);

  private int planetID;
  private long pinID;
  private int typeID;
  private int schematicID;
  private long lastCycleStart = -1;
  private int cycleTime;
  private int quantityPerCycle;
  private long installTime = -1;
  private long expiryTime = -1;
  private int productTypeID;
  private float longitude;
  private float latitude;
  private float headRadius;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "planet_pin_head", joinColumns = @JoinColumn(name = "planet_pin_cid"))
  private Set<PlanetaryPinHead> heads = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "planet_pin_content", joinColumns = @JoinColumn(name = "planet_pin_cid"))
  private Set<PlanetaryPinContent> contents = new HashSet<>();

  @Transient
  @ApiModelProperty(
      value = "lastCycleStart Date")
  @JsonProperty("lastCycleStartDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastCycleStartDate;

  @Transient
  @ApiModelProperty(
      value = "installTime Date")
  @JsonProperty("installTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date installTimeDate;

  @Transient
  @ApiModelProperty(
      value = "expiryTime Date")
  @JsonProperty("expiryTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date expiryTimeDate;

  @SuppressWarnings("unused")
  protected PlanetaryPin() {}

  public PlanetaryPin(int planetID, long pinID, int typeID, int schematicID, long lastCycleStart, int cycleTime,
                      int quantityPerCycle, long installTime, long expiryTime, int productTypeID, float longitude,
                      float latitude, float headRadius, Set<PlanetaryPinHead> heads,
                      Set<PlanetaryPinContent> contents) {
    this.planetID = planetID;
    this.pinID = pinID;
    this.typeID = typeID;
    this.schematicID = schematicID;
    this.lastCycleStart = lastCycleStart;
    this.cycleTime = cycleTime;
    this.quantityPerCycle = quantityPerCycle;
    this.installTime = installTime;
    this.expiryTime = expiryTime;
    this.productTypeID = productTypeID;
    this.longitude = longitude;
    this.latitude = latitude;
    this.headRadius = headRadius;
    this.heads = heads;
    this.contents = contents;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    lastCycleStartDate = assignDateField(lastCycleStart);
    installTimeDate = assignDateField(installTime);
    expiryTimeDate = assignDateField(expiryTime);
  }

  /**
   * Compare to pin head sets.  Normal null safe object compare doesn't work properly here.
   *
   * @param a first set to compare.
   * @param b second set to compare.
   * @return true if the sets have identical contents, false otherwise.
   */
  private boolean comparePinHeadSet(Set<PlanetaryPinHead> a, Set<PlanetaryPinHead> b) {
    if (a == b) return true;
    if (a == null || b == null) return false;
    if (a.size() != b.size()) return false;
    for (PlanetaryPinHead next : a) {
      boolean found = false;
      for (PlanetaryPinHead cmp: b) {
        if (next.equivalent(cmp)) {
          found = true;
          break;
        }
      }
      if (!found) return false;
    }
    return true;
  }

  /**
   * Compare to pin content sets.  Normal null safe object compare doesn't work properly here.
   *
   * @param a first set to compare.
   * @param b second set to compare.
   * @return true if the sets have identical contents, false otherwise.
   */
  private boolean comparePinContentSet(Set<PlanetaryPinContent> a, Set<PlanetaryPinContent> b) {
    if (a == b) return true;
    if (a == null || b == null) return false;
    if (a.size() != b.size()) return false;
    for (PlanetaryPinContent next : a) {
      boolean found = false;
      for (PlanetaryPinContent cmp: b) {
        if (next.equivalent(cmp)) {
          found = true;
          break;
        }
      }
      if (!found) return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof PlanetaryPin)) return false;
    PlanetaryPin other = (PlanetaryPin) sup;
    return planetID == other.planetID && pinID == other.pinID && typeID == other.typeID
        && schematicID == other.schematicID && lastCycleStart == other.lastCycleStart && cycleTime == other.cycleTime
        && quantityPerCycle == other.quantityPerCycle && installTime == other.installTime && expiryTime == other.expiryTime
        && productTypeID == other.productTypeID
        && floatCompare(longitude, other.longitude,0.00001F)
        && floatCompare(latitude, other.latitude,0.00001F)
        && floatCompare(headRadius, other.headRadius,0.00001F)
        && comparePinHeadSet(heads, other.heads) && comparePinContentSet(contents, other.contents);
  }

  @Override
  public String dataHash() {
    // sort pin heads and pin contents for consistent hashing
    List<PlanetaryPinHead> sortHeads = new ArrayList<>(heads);
    sortHeads.sort(Comparator.comparingInt(PlanetaryPinHead::getHeadID));
    List<PlanetaryPinContent> sortContents = new ArrayList<>(contents);
    sortContents.sort(Comparator.comparingInt(PlanetaryPinContent::getTypeID));
    return dataHashHelper(planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle,
                          installTime, expiryTime, productTypeID, longitude, latitude, headRadius,
                          dataHashHelper(sortHeads.stream().map(PlanetaryPinHead::dataHash).toArray()),
                          dataHashHelper(sortContents.stream().map(PlanetaryPinContent::dataHash).toArray()));
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

  public long getPinID() {
    return pinID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getSchematicID() {
    return schematicID;
  }

  public long getLastCycleStart() {
    return lastCycleStart;
  }

  public int getCycleTime() {
    return cycleTime;
  }

  public int getQuantityPerCycle() {
    return quantityPerCycle;
  }

  public long getInstallTime() {
    return installTime;
  }

  public long getExpiryTime() {
    return expiryTime;
  }

  public int getProductTypeID() {
    return productTypeID;
  }

  public float getLongitude() {
    return longitude;
  }

  public float getLatitude() {
    return latitude;
  }

  public float getHeadRadius() {
    return headRadius;
  }

  public Set<PlanetaryPinHead> getHeads() {
    return heads;
  }

  public Set<PlanetaryPinContent> getContents() {
    return contents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    PlanetaryPin that = (PlanetaryPin) o;
    return planetID == that.planetID &&
        pinID == that.pinID &&
        typeID == that.typeID &&
        schematicID == that.schematicID &&
        lastCycleStart == that.lastCycleStart &&
        cycleTime == that.cycleTime &&
        quantityPerCycle == that.quantityPerCycle &&
        installTime == that.installTime &&
        expiryTime == that.expiryTime &&
        productTypeID == that.productTypeID &&
        Float.compare(that.longitude, longitude) == 0 &&
        Float.compare(that.latitude, latitude) == 0 &&
        Float.compare(that.headRadius, headRadius) == 0 &&
        Objects.equals(heads, that.heads) &&
        Objects.equals(contents, that.contents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime,
                        quantityPerCycle, installTime, expiryTime, productTypeID, longitude, latitude, headRadius,
                        heads,
                        contents);
  }

  @Override
  public String toString() {
    return "PlanetaryPin{" +
        "planetID=" + planetID +
        ", pinID=" + pinID +
        ", typeID=" + typeID +
        ", schematicID=" + schematicID +
        ", lastCycleStart=" + lastCycleStart +
        ", cycleTime=" + cycleTime +
        ", quantityPerCycle=" + quantityPerCycle +
        ", installTime=" + installTime +
        ", expiryTime=" + expiryTime +
        ", productTypeID=" + productTypeID +
        ", longitude=" + longitude +
        ", latitude=" + latitude +
        ", headRadius=" + headRadius +
        ", heads=" + heads +
        ", contents=" + contents +
        ", lastCycleStartDate=" + lastCycleStartDate +
        ", installTimeDate=" + installTimeDate +
        ", expiryTimeDate=" + expiryTimeDate +
        '}';
  }

  public static PlanetaryPin get(
      final SynchronizedEveAccount owner,
      final long time,
      final int planetID,
      final long pinID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<PlanetaryPin> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "PlanetaryPin.getByPlanetAndPinID",
                                                                                                       PlanetaryPin.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("planet", planetID);
                                        getter.setParameter("pin", pinID);
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

  public static List<PlanetaryPin> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector planetID,
      final AttributeSelector pinID,
      final AttributeSelector typeID,
      final AttributeSelector schematicID,
      final AttributeSelector lastCycleStart,
      final AttributeSelector cycleTime,
      final AttributeSelector quantityPerCycle,
      final AttributeSelector installTime,
      final AttributeSelector expiryTime,
      final AttributeSelector productTypeID,
      final AttributeSelector longitude,
      final AttributeSelector latitude,
      final AttributeSelector headRadius,
      final AttributeSelector headID,
      final AttributeSelector headLongitude,
      final AttributeSelector headLatitude,
      final AttributeSelector contentTypeID,
      final AttributeSelector contentAmount) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT DISTINCT c FROM PlanetaryPin c ");
                                        qs.append("LEFT JOIN c.heads d ");
                                        qs.append("LEFT JOIN c.contents e WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");

                                        AttributeSelector.addIntSelector(qs, "c", "planetID", planetID);
                                        AttributeSelector.addLongSelector(qs, "c", "pinID", pinID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "schematicID", schematicID);
                                        AttributeSelector.addLongSelector(qs, "c", "lastCycleStart", lastCycleStart);
                                        AttributeSelector.addIntSelector(qs, "c", "cycleTime", cycleTime);
                                        AttributeSelector.addIntSelector(qs, "c", "quantityPerCycle", quantityPerCycle);
                                        AttributeSelector.addLongSelector(qs, "c", "installTime", installTime);
                                        AttributeSelector.addLongSelector(qs, "c", "expiryTime", expiryTime);
                                        AttributeSelector.addIntSelector(qs, "c", "productTypeID", productTypeID);
                                        AttributeSelector.addFloatSelector(qs, "c", "longitude", longitude);
                                        AttributeSelector.addFloatSelector(qs, "c", "latitude", latitude);
                                        AttributeSelector.addFloatSelector(qs, "c", "headRadius", headRadius);
                                        AttributeSelector.addIntSelector(qs, "d", "headID", headID);
                                        AttributeSelector.addFloatSelector(qs, "d", "longitude", headLongitude);
                                        AttributeSelector.addFloatSelector(qs, "d", "latitude", headLatitude);
                                        AttributeSelector.addIntSelector(qs, "e", "typeID", contentTypeID);
                                        AttributeSelector.addLongSelector(qs, "e", "amount", contentAmount);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<PlanetaryPin> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      PlanetaryPin.class);
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
