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
    name = "evekit_data_planetary_colony",
    indexes = {
        @Index(
            name = "planetIDIndex",
            columnList = "planetID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "PlanetaryColony.getByPlanetID",
        query = "SELECT c FROM PlanetaryColony c where c.owner = :owner and c.planetID = :pid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "PlanetaryColony.getAll",
        query = "SELECT c FROM PlanetaryColony c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API cache time unknown
public class PlanetaryColony extends CachedData {
  private static final Logger log        = Logger.getLogger(PlanetaryColony.class.getName());
  private static final byte[] MASK       = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private long                planetID;
  private int                 solarSystemID;
  private String              solarSystemName;
  private String              planetName;
  private int                 planetTypeID;
  private String              planetTypeName;
  private long                ownerID;
  private String              ownerName;
  private long                lastUpdate = -1;
  private int                 upgradeLevel;
  private int                 numberOfPins;

  @SuppressWarnings("unused")
  private PlanetaryColony() {}

  public PlanetaryColony(long planetID, int solarSystemID, String solarSystemName, String planetName, int planetTypeID, String planetTypeName, long ownerID,
                         String ownerName, long lastUpdate, int upgradeLevel, int numberOfPins) {
    super();
    this.planetID = planetID;
    this.solarSystemID = solarSystemID;
    this.solarSystemName = solarSystemName;
    this.planetName = planetName;
    this.planetTypeID = planetTypeID;
    this.planetTypeName = planetTypeName;
    this.ownerID = ownerID;
    this.ownerName = ownerName;
    this.lastUpdate = lastUpdate;
    this.upgradeLevel = upgradeLevel;
    this.numberOfPins = numberOfPins;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof PlanetaryColony)) return false;
    PlanetaryColony other = (PlanetaryColony) sup;
    return planetID == other.planetID && solarSystemID == other.solarSystemID && nullSafeObjectCompare(solarSystemName, other.solarSystemName)
        && nullSafeObjectCompare(planetName, other.planetName) && planetTypeID == other.planetTypeID
        && nullSafeObjectCompare(planetTypeName, other.planetTypeName) && ownerID == other.ownerID && nullSafeObjectCompare(ownerName, other.ownerName)
        && lastUpdate == other.lastUpdate && upgradeLevel == other.upgradeLevel && numberOfPins == other.numberOfPins;
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

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getSolarSystemName() {
    return solarSystemName;
  }

  public String getPlanetName() {
    return planetName;
  }

  public int getPlanetTypeID() {
    return planetTypeID;
  }

  public String getPlanetTypeName() {
    return planetTypeName;
  }

  public long getOwnerID() {
    return ownerID;
  }

  public String getOwnerName() {
    return ownerName;
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (lastUpdate ^ (lastUpdate >>> 32));
    result = prime * result + numberOfPins;
    result = prime * result + (int) (ownerID ^ (ownerID >>> 32));
    result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
    result = prime * result + (int) (planetID ^ (planetID >>> 32));
    result = prime * result + ((planetName == null) ? 0 : planetName.hashCode());
    result = prime * result + planetTypeID;
    result = prime * result + ((planetTypeName == null) ? 0 : planetTypeName.hashCode());
    result = prime * result + solarSystemID;
    result = prime * result + ((solarSystemName == null) ? 0 : solarSystemName.hashCode());
    result = prime * result + upgradeLevel;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PlanetaryColony other = (PlanetaryColony) obj;
    if (lastUpdate != other.lastUpdate) return false;
    if (numberOfPins != other.numberOfPins) return false;
    if (ownerID != other.ownerID) return false;
    if (ownerName == null) {
      if (other.ownerName != null) return false;
    } else if (!ownerName.equals(other.ownerName)) return false;
    if (planetID != other.planetID) return false;
    if (planetName == null) {
      if (other.planetName != null) return false;
    } else if (!planetName.equals(other.planetName)) return false;
    if (planetTypeID != other.planetTypeID) return false;
    if (planetTypeName == null) {
      if (other.planetTypeName != null) return false;
    } else if (!planetTypeName.equals(other.planetTypeName)) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (solarSystemName == null) {
      if (other.solarSystemName != null) return false;
    } else if (!solarSystemName.equals(other.solarSystemName)) return false;
    if (upgradeLevel != other.upgradeLevel) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PlanetaryColony [planetID=" + planetID + ", solarSystemID=" + solarSystemID + ", solarSystemName=" + solarSystemName + ", planetName=" + planetName
        + ", planetTypeID=" + planetTypeID + ", planetTypeName=" + planetTypeName + ", ownerID=" + ownerID + ", ownerName=" + ownerName + ", lastUpdate="
        + lastUpdate + ", upgradeLevel=" + upgradeLevel + ", numberOfPins=" + numberOfPins + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd="
        + lifeEnd + "]";
  }

  /**
   * Retrieve planetary colony with the given properties live at the given time, or null if no such colony exists.
   * 
   * @param owner
   *          planetary colony owner
   * @param time
   *          time at which property colony must be live
   * @param planetID
   *          planet ID of planet where colony is located
   * @return planetary colony with the given properties live at the given time, or null
   */
  public static PlanetaryColony get(
                                    final SynchronizedEveAccount owner,
                                    final long time,
                                    final long planetID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<PlanetaryColony>() {
        @Override
        public PlanetaryColony run() throws Exception {
          TypedQuery<PlanetaryColony> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryColony.getByPlanetID",
                                                                                                                          PlanetaryColony.class);
          getter.setParameter("owner", owner);
          getter.setParameter("pid", planetID);
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

  /**
   * Retrieve list of all planetary colonies live at the given time.
   * 
   * @param owner
   *          planetary colonies owner
   * @param time
   *          time at which colonies must be live
   * @return list of planetary colonies live at the given time
   */
  public static List<PlanetaryColony> getAllPlanetaryColonies(
                                                              final SynchronizedEveAccount owner,
                                                              final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryColony>>() {
        @Override
        public List<PlanetaryColony> run() throws Exception {
          TypedQuery<PlanetaryColony> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("PlanetaryColony.getAll",
                                                                                                                          PlanetaryColony.class);
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

  public static List<PlanetaryColony> accessQuery(
                                                  final SynchronizedEveAccount owner,
                                                  final long contid,
                                                  final int maxresults,
                                                  final boolean reverse,
                                                  final AttributeSelector at,
                                                  final AttributeSelector planetID,
                                                  final AttributeSelector solarSystemID,
                                                  final AttributeSelector solarSystemName,
                                                  final AttributeSelector planetName,
                                                  final AttributeSelector planetTypeID,
                                                  final AttributeSelector planetTypeName,
                                                  final AttributeSelector ownerID,
                                                  final AttributeSelector ownerName,
                                                  final AttributeSelector lastUpdate,
                                                  final AttributeSelector upgradeLevel,
                                                  final AttributeSelector numberOfPins) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<PlanetaryColony>>() {
        @Override
        public List<PlanetaryColony> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM PlanetaryColony c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "planetID", planetID);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addStringSelector(qs, "c", "solarSystemName", solarSystemName, p);
          AttributeSelector.addStringSelector(qs, "c", "planetName", planetName, p);
          AttributeSelector.addIntSelector(qs, "c", "planetTypeID", planetTypeID);
          AttributeSelector.addStringSelector(qs, "c", "planetTypeName", planetTypeName, p);
          AttributeSelector.addLongSelector(qs, "c", "ownerID", ownerID);
          AttributeSelector.addStringSelector(qs, "c", "ownerName", ownerName, p);
          AttributeSelector.addLongSelector(qs, "c", "lastUpdate", lastUpdate);
          AttributeSelector.addIntSelector(qs, "c", "upgradeLevel", upgradeLevel);
          AttributeSelector.addIntSelector(qs, "c", "numberOfPins", numberOfPins);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<PlanetaryColony> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), PlanetaryColony.class);
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
