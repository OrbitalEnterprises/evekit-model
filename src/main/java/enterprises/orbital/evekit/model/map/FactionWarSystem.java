package enterprises.orbital.evekit.model.map;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_map_facwarsystem")
@NamedQueries({
    @NamedQuery(
        name = "FactionWarSystem.get",
        query = "SELECT c FROM FactionWarSystem c WHERE c.solarSystemID = :sid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionWarSystem extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionWarSystem.class.getName());
  private long                occupyingFactionID;
  private String              occupyingFactionName;
  private long                owningFactionID;
  private String              owningFactionName;
  private int                 solarSystemID;
  private String              solarSystemName;
  private boolean             contested;

  @SuppressWarnings("unused")
  private FactionWarSystem() {}

  public FactionWarSystem(long occupyingFactionID, String occupyingFactionName, long owningFactionID, String owningFactionName, int solarSystemID,
                          String solarSystemName, boolean contested) {
    super();
    this.occupyingFactionID = occupyingFactionID;
    this.occupyingFactionName = occupyingFactionName;
    this.owningFactionID = owningFactionID;
    this.owningFactionName = owningFactionName;
    this.solarSystemID = solarSystemID;
    this.solarSystemName = solarSystemName;
    this.contested = contested;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof FactionWarSystem)) return false;
    FactionWarSystem other = (FactionWarSystem) sup;
    return occupyingFactionID == other.occupyingFactionID && nullSafeObjectCompare(occupyingFactionName, other.occupyingFactionName)
        && owningFactionID == other.owningFactionID && nullSafeObjectCompare(owningFactionName, other.owningFactionName) && solarSystemID == other.solarSystemID
        && nullSafeObjectCompare(solarSystemName, other.solarSystemName) && contested == other.contested;
  }

  public long getOccupyingFactionID() {
    return occupyingFactionID;
  }

  public String getOccupyingFactionName() {
    return occupyingFactionName;
  }

  public long getOwningFactionID() {
    return owningFactionID;
  }

  public String getOwningFactionName() {
    return owningFactionName;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getSolarSystemName() {
    return solarSystemName;
  }

  public boolean isContested() {
    return contested;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (contested ? 1231 : 1237);
    result = prime * result + (int) (occupyingFactionID ^ (occupyingFactionID >>> 32));
    result = prime * result + ((occupyingFactionName == null) ? 0 : occupyingFactionName.hashCode());
    result = prime * result + (int) (owningFactionID ^ (owningFactionID >>> 32));
    result = prime * result + ((owningFactionName == null) ? 0 : owningFactionName.hashCode());
    result = prime * result + solarSystemID;
    result = prime * result + ((solarSystemName == null) ? 0 : solarSystemName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    FactionWarSystem other = (FactionWarSystem) obj;
    if (contested != other.contested) return false;
    if (occupyingFactionID != other.occupyingFactionID) return false;
    if (occupyingFactionName == null) {
      if (other.occupyingFactionName != null) return false;
    } else if (!occupyingFactionName.equals(other.occupyingFactionName)) return false;
    if (owningFactionID != other.owningFactionID) return false;
    if (owningFactionName == null) {
      if (other.owningFactionName != null) return false;
    } else if (!owningFactionName.equals(other.owningFactionName)) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (solarSystemName == null) {
      if (other.solarSystemName != null) return false;
    } else if (!solarSystemName.equals(other.solarSystemName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "FactionWarSystem [occupyingFactionID=" + occupyingFactionID + ", occupyingFactionName=" + occupyingFactionName + ", owningFactionID="
        + owningFactionID + ", owningFactionName=" + owningFactionName + ", solarSystemID=" + solarSystemID + ", solarSystemName=" + solarSystemName
        + ", contested=" + contested + "]";
  }

  public static FactionWarSystem get(
                                     final long time,
                                     final int solarSystemID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<FactionWarSystem>() {
        @Override
        public FactionWarSystem run() throws Exception {
          TypedQuery<FactionWarSystem> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("FactionWarSystem.get",
                                                                                                                       FactionWarSystem.class);
          getter.setParameter("point", time);
          getter.setParameter("sid", solarSystemID);
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

  public static List<FactionWarSystem> accessQuery(
                                                   final long contid,
                                                   final int maxresults,
                                                   final boolean reverse,
                                                   final AttributeSelector at,
                                                   final AttributeSelector occupyingFactionID,
                                                   final AttributeSelector occupyingFactionName,
                                                   final AttributeSelector owningFactionID,
                                                   final AttributeSelector owningFactionName,
                                                   final AttributeSelector solarSystemID,
                                                   final AttributeSelector solarSystemName,
                                                   final AttributeSelector contested) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<FactionWarSystem>>() {
        @Override
        public List<FactionWarSystem> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM FactionWarSystem c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "occupyingFactionID", occupyingFactionID);
          AttributeSelector.addStringSelector(qs, "c", "occupyingFactionName", occupyingFactionName, p);
          AttributeSelector.addLongSelector(qs, "c", "owningFactionID", owningFactionID);
          AttributeSelector.addStringSelector(qs, "c", "owningFactionName", owningFactionName, p);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addStringSelector(qs, "c", "solarSystemName", solarSystemName, p);
          AttributeSelector.addBooleanSelector(qs, "c", "contested", contested);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<FactionWarSystem> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), FactionWarSystem.class);
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
