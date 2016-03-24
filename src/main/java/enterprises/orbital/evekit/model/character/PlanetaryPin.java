package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_planetary_pin",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID",
            unique = false),
        @Index(
            name = "pinIDIndex",
            columnList = "pinID",
            unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryPin.getByPlanetAndPinID",
        query = "SELECT c FROM PlanetaryPin c where c.owner = :owner and c.planetID = :planet and c.pinID = :pin and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "PlanetaryPin.getAll",
        query = "SELECT c FROM PlanetaryPin c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "PlanetaryPin.getAllByPlanetID",
        query = "SELECT c FROM PlanetaryPin c where c.owner = :owner and c.planetID = :planet and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API cache time unknown
public class PlanetaryPin extends CachedData {
  private static final Logger log  = Logger.getLogger(PlanetaryPin.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private long                planetID;
  private long                pinID;
  private int                 typeID;
  private String              typeName;
  private int                 schematicID;
  private long                lastLaunchTime;
  private int                 cycleTime;
  private int                 quantityPerCycle;
  private long                installTime;
  private long                expiryTime;
  private int                 contentTypeID;
  private String              contentTypeName;
  private int                 contentQuantity;
  private double              longitude;
  private double              latitude;

  @SuppressWarnings("unused")
  private PlanetaryPin() {}

  public PlanetaryPin(long planetID, long pinID, int typeID, String typeName, int schematicID, long lastLaunchTime, int cycleTime, int quantityPerCycle,
                      long installTime, long expiryTime, int contentTypeID, String contentTypeName, int contentQuantity, double longitude, double latitude) {
    super();
    this.planetID = planetID;
    this.pinID = pinID;
    this.typeID = typeID;
    this.typeName = typeName;
    this.schematicID = schematicID;
    this.lastLaunchTime = lastLaunchTime;
    this.cycleTime = cycleTime;
    this.quantityPerCycle = quantityPerCycle;
    this.installTime = installTime;
    this.expiryTime = expiryTime;
    this.contentTypeID = contentTypeID;
    this.contentTypeName = contentTypeName;
    this.contentQuantity = contentQuantity;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof PlanetaryPin)) return false;
    PlanetaryPin other = (PlanetaryPin) sup;
    return planetID == other.planetID && pinID == other.pinID && typeID == other.typeID && nullSafeObjectCompare(typeName, other.typeName)
        && schematicID == other.schematicID && lastLaunchTime == other.lastLaunchTime && cycleTime == other.cycleTime
        && quantityPerCycle == other.quantityPerCycle && installTime == other.installTime && expiryTime == other.expiryTime
        && contentTypeID == other.contentTypeID && nullSafeObjectCompare(contentTypeName, other.contentTypeName) && contentQuantity == other.contentQuantity
        && longitude == other.longitude && latitude == other.latitude;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getPlanetID() {
    return planetID;
  }

  public long getPinID() {
    return pinID;
  }

  public int getTypeID() {
    return typeID;
  }

  public String getTypeName() {
    return typeName;
  }

  public int getSchematicID() {
    return schematicID;
  }

  public long getLastLaunchTime() {
    return lastLaunchTime;
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

  public int getContentTypeID() {
    return contentTypeID;
  }

  public String getContentTypeName() {
    return contentTypeName;
  }

  public int getContentQuantity() {
    return contentQuantity;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + contentQuantity;
    result = prime * result + contentTypeID;
    result = prime * result + ((contentTypeName == null) ? 0 : contentTypeName.hashCode());
    result = prime * result + cycleTime;
    result = prime * result + (int) (expiryTime ^ (expiryTime >>> 32));
    result = prime * result + (int) (installTime ^ (installTime >>> 32));
    result = prime * result + (int) (lastLaunchTime ^ (lastLaunchTime >>> 32));
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + (int) (pinID ^ (pinID >>> 32));
    result = prime * result + (int) (planetID ^ (planetID >>> 32));
    result = prime * result + quantityPerCycle;
    result = prime * result + schematicID;
    result = prime * result + typeID;
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PlanetaryPin other = (PlanetaryPin) obj;
    if (contentQuantity != other.contentQuantity) return false;
    if (contentTypeID != other.contentTypeID) return false;
    if (contentTypeName == null) {
      if (other.contentTypeName != null) return false;
    } else if (!contentTypeName.equals(other.contentTypeName)) return false;
    if (cycleTime != other.cycleTime) return false;
    if (expiryTime != other.expiryTime) return false;
    if (installTime != other.installTime) return false;
    if (lastLaunchTime != other.lastLaunchTime) return false;
    if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) return false;
    if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude)) return false;
    if (pinID != other.pinID) return false;
    if (planetID != other.planetID) return false;
    if (quantityPerCycle != other.quantityPerCycle) return false;
    if (schematicID != other.schematicID) return false;
    if (typeID != other.typeID) return false;
    if (typeName == null) {
      if (other.typeName != null) return false;
    } else if (!typeName.equals(other.typeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PlanetaryPin [planetID=" + planetID + ", pinID=" + pinID + ", typeID=" + typeID + ", typeName=" + typeName + ", schematicID=" + schematicID
        + ", lastLaunchTime=" + lastLaunchTime + ", cycleTime=" + cycleTime + ", quantityPerCycle=" + quantityPerCycle + ", installTime=" + installTime
        + ", expiryTime=" + expiryTime + ", contentTypeID=" + contentTypeID + ", contentTypeName=" + contentTypeName + ", contentQuantity=" + contentQuantity
        + ", longitude=" + longitude + ", latitude=" + latitude + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static PlanetaryPin get(
                                 final SynchronizedEveAccount owner,
                                 final long time,
                                 final long planetID,
                                 final long pinID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<PlanetaryPin>() {
        @Override
        public PlanetaryPin run() throws Exception {
          TypedQuery<PlanetaryPin> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryPin.getByPlanetAndPinID",
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
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static List<PlanetaryPin> getAllPlanetaryPins(
                                                       final SynchronizedEveAccount owner,
                                                       final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryPin>>() {
        @Override
        public List<PlanetaryPin> run() throws Exception {
          TypedQuery<PlanetaryPin> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryPin.getAll",
                                                                                                                       PlanetaryPin.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<PlanetaryPin> getAllPlanetaryPinsByPlanet(
                                                               final SynchronizedEveAccount owner,
                                                               final long time,
                                                               final long planetID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryPin>>() {
        @Override
        public List<PlanetaryPin> run() throws Exception {
          TypedQuery<PlanetaryPin> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryPin.getAllByPlanetID",
                                                                                                                       PlanetaryPin.class);
          getter.setParameter("owner", owner);
          getter.setParameter("planet", planetID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<PlanetaryPin> accessQuery(
                                               final SynchronizedEveAccount owner,
                                               final long contid,
                                               final int maxresults,
                                               final AttributeSelector at,
                                               final AttributeSelector planetID,
                                               final AttributeSelector pinID,
                                               final AttributeSelector typeID,
                                               final AttributeSelector typeName,
                                               final AttributeSelector schematicID,
                                               final AttributeSelector lastLaunchTime,
                                               final AttributeSelector cycleTime,
                                               final AttributeSelector quantityPerCycle,
                                               final AttributeSelector installTime,
                                               final AttributeSelector expiryTime,
                                               final AttributeSelector contentTypeID,
                                               final AttributeSelector contentTypeName,
                                               final AttributeSelector contentQuantity,
                                               final AttributeSelector longitude,
                                               final AttributeSelector latitude) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryPin>>() {
        @Override
        public List<PlanetaryPin> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM PlanetaryPin c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");

          AttributeSelector.addLongSelector(qs, "c", "planetID", planetID);
          AttributeSelector.addLongSelector(qs, "c", "pinID", pinID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addStringSelector(qs, "c", "typeName", typeName, p);
          AttributeSelector.addIntSelector(qs, "c", "schematicID", schematicID);
          AttributeSelector.addLongSelector(qs, "c", "lastLaunchTime", lastLaunchTime);
          AttributeSelector.addIntSelector(qs, "c", "cycleTime", cycleTime);
          AttributeSelector.addIntSelector(qs, "c", "quantityPerCycle", quantityPerCycle);
          AttributeSelector.addLongSelector(qs, "c", "installTime", installTime);
          AttributeSelector.addLongSelector(qs, "c", "expiryTime", expiryTime);
          AttributeSelector.addIntSelector(qs, "c", "contentTypeID", contentTypeID);
          AttributeSelector.addStringSelector(qs, "c", "contentTypeName", contentTypeName, p);
          AttributeSelector.addIntSelector(qs, "c", "contentQuantity", contentQuantity);
          AttributeSelector.addDoubleSelector(qs, "c", "longitude", longitude);
          AttributeSelector.addDoubleSelector(qs, "c", "latitude", latitude);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<PlanetaryPin> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), PlanetaryPin.class);
          query.setParameter("owner", owner);
          p.fillParams(query);
          query.setMaxResults(maxresults);
          return query.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
