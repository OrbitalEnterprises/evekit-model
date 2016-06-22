package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_facility",
    indexes = {
        @Index(
            name = "facilityIDIndex",
            columnList = "facilityID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Facility.getByFacilityID",
        query = "SELECT c FROM Facility c where c.owner = :owner and c.facilityID = :facility and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Facility.getAll",
        query = "SELECT c FROM Facility c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class Facility extends CachedData {
  private static final Logger log  = Logger.getLogger(Facility.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS);
  private long                facilityID;
  private int                 typeID;
  private String              typeName;
  private int                 solarSystemID;
  private String              solarSystemName;
  private int                 regionID;
  private String              regionName;
  private int                 starbaseModifier;
  private double              tax;

  @SuppressWarnings("unused")
  private Facility() {}

  public Facility(long facilityID, int typeID, String typeName, int solarSystemID, String solarSystemName, int regionID, String regionName,
                  int starbaseModifier, double tax) {
    super();
    this.facilityID = facilityID;
    this.typeID = typeID;
    this.typeName = typeName;
    this.solarSystemID = solarSystemID;
    this.solarSystemName = solarSystemName;
    this.regionID = regionID;
    this.regionName = regionName;
    this.starbaseModifier = starbaseModifier;
    this.tax = tax;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Facility)) return false;
    Facility other = (Facility) sup;
    return facilityID == other.facilityID && typeID == other.typeID && nullSafeObjectCompare(typeName, other.typeName) && solarSystemID == other.solarSystemID
        && nullSafeObjectCompare(solarSystemName, other.solarSystemName) && regionID == other.regionID && nullSafeObjectCompare(regionName, other.regionName)
        && starbaseModifier == other.starbaseModifier && tax == other.tax;
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

  public String getTypeName() {
    return typeName;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getSolarSystemName() {
    return solarSystemName;
  }

  public int getRegionID() {
    return regionID;
  }

  public String getRegionName() {
    return regionName;
  }

  public int getStarbaseModifier() {
    return starbaseModifier;
  }

  public double getTax() {
    return tax;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (facilityID ^ (facilityID >>> 32));
    result = prime * result + regionID;
    result = prime * result + ((regionName == null) ? 0 : regionName.hashCode());
    result = prime * result + solarSystemID;
    result = prime * result + ((solarSystemName == null) ? 0 : solarSystemName.hashCode());
    result = prime * result + starbaseModifier;
    long temp;
    temp = Double.doubleToLongBits(tax);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    Facility other = (Facility) obj;
    if (facilityID != other.facilityID) return false;
    if (regionID != other.regionID) return false;
    if (regionName == null) {
      if (other.regionName != null) return false;
    } else if (!regionName.equals(other.regionName)) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (solarSystemName == null) {
      if (other.solarSystemName != null) return false;
    } else if (!solarSystemName.equals(other.solarSystemName)) return false;
    if (starbaseModifier != other.starbaseModifier) return false;
    if (Double.doubleToLongBits(tax) != Double.doubleToLongBits(other.tax)) return false;
    if (typeID != other.typeID) return false;
    if (typeName == null) {
      if (other.typeName != null) return false;
    } else if (!typeName.equals(other.typeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Facility [facilityID=" + facilityID + ", typeID=" + typeID + ", typeName=" + typeName + ", solarSystemID=" + solarSystemID + ", solarSystemName="
        + solarSystemName + ", regionID=" + regionID + ", regionName=" + regionName + ", starbaseModifier=" + starbaseModifier + ", tax=" + tax + ", owner="
        + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static Facility get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final long facilityID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Facility>() {
        @Override
        public Facility run() throws Exception {
          TypedQuery<Facility> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Facility.getByFacilityID", Facility.class);
          getter.setParameter("owner", owner);
          getter.setParameter("facility", facilityID);
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

  public static List<Facility> getAll(
                                      final SynchronizedEveAccount owner,
                                      final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Facility>>() {
        @Override
        public List<Facility> run() throws Exception {
          TypedQuery<Facility> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Facility.getAll", Facility.class);
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

  public static List<Facility> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector facilityID,
                                           final AttributeSelector typeID,
                                           final AttributeSelector typeName,
                                           final AttributeSelector solarSystemID,
                                           final AttributeSelector solarSystemName,
                                           final AttributeSelector regionID,
                                           final AttributeSelector regionName,
                                           final AttributeSelector starbaseModifier,
                                           final AttributeSelector tax) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Facility>>() {
        @Override
        public List<Facility> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Facility c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "facilityID", facilityID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addStringSelector(qs, "c", "typeName", typeName, p);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addStringSelector(qs, "c", "solarSystemName", solarSystemName, p);
          AttributeSelector.addIntSelector(qs, "c", "regionID", regionID);
          AttributeSelector.addStringSelector(qs, "c", "regionName", regionName, p);
          AttributeSelector.addIntSelector(qs, "c", "starbaseModifier", starbaseModifier);
          AttributeSelector.addDoubleSelector(qs, "c", "tax", tax);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Facility> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Facility.class);
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
