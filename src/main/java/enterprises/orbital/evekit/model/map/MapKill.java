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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_map_mapkill")
@NamedQueries({
    @NamedQuery(
        name = "MapKill.get",
        query = "SELECT c FROM MapKill c WHERE c.solarSystemID = :sid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class MapKill extends RefCachedData {
  private static final Logger log = Logger.getLogger(MapKill.class.getName());
  private int                 factionKills;
  private int                 podKills;
  private int                 shipKills;
  private int                 solarSystemID;

  @SuppressWarnings("unused")
  private MapKill() {}

  public MapKill(int factionKills, int podKills, int shipKills, int solarSystemID) {
    super();
    this.factionKills = factionKills;
    this.podKills = podKills;
    this.shipKills = shipKills;
    this.solarSystemID = solarSystemID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof MapKill)) return false;
    MapKill other = (MapKill) sup;
    return factionKills == other.factionKills && podKills == other.podKills && shipKills == other.shipKills && solarSystemID == other.solarSystemID;
  }

  public int getFactionKills() {
    return factionKills;
  }

  public int getPodKills() {
    return podKills;
  }

  public int getShipKills() {
    return shipKills;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + factionKills;
    result = prime * result + podKills;
    result = prime * result + shipKills;
    result = prime * result + solarSystemID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MapKill other = (MapKill) obj;
    if (factionKills != other.factionKills) return false;
    if (podKills != other.podKills) return false;
    if (shipKills != other.shipKills) return false;
    if (solarSystemID != other.solarSystemID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MapKill [factionKills=" + factionKills + ", podKills=" + podKills + ", shipKills=" + shipKills + ", solarSystemID=" + solarSystemID + "]";
  }

  public static MapKill get(
                            final long time,
                            final int solarSystemID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<MapKill>() {
        @Override
        public MapKill run() throws Exception {
          TypedQuery<MapKill> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("MapKill.get", MapKill.class);
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

  public static List<MapKill> accessQuery(
                                          final long contid,
                                          final int maxresults,
                                          final boolean reverse,
                                          final AttributeSelector at,
                                          final AttributeSelector factionKills,
                                          final AttributeSelector podKills,
                                          final AttributeSelector shipKills,
                                          final AttributeSelector solarSystemID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<MapKill>>() {
        @Override
        public List<MapKill> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MapKill c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "factionKills", factionKills);
          AttributeSelector.addIntSelector(qs, "c", "podKills", podKills);
          AttributeSelector.addIntSelector(qs, "c", "shipKills", shipKills);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MapKill> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), MapKill.class);
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
