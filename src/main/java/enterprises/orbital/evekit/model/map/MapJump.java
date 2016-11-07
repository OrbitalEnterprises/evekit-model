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
    name = "evekit_map_mapjump")
@NamedQueries({
    @NamedQuery(
        name = "MapJump.get",
        query = "SELECT c FROM MapJump c WHERE c.solarSystemID = :sid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class MapJump extends RefCachedData {
  private static final Logger log = Logger.getLogger(MapJump.class.getName());
  private int                 solarSystemID;
  private int                 shipJumps;

  @SuppressWarnings("unused")
  private MapJump() {}

  public MapJump(int solarSystemID, int shipJumps) {
    super();
    this.solarSystemID = solarSystemID;
    this.shipJumps = shipJumps;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof MapJump)) return false;
    MapJump other = (MapJump) sup;
    return solarSystemID == other.solarSystemID && shipJumps == other.shipJumps;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public int getShipJumps() {
    return shipJumps;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + shipJumps;
    result = prime * result + solarSystemID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MapJump other = (MapJump) obj;
    if (shipJumps != other.shipJumps) return false;
    if (solarSystemID != other.solarSystemID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MapJump [solarSystemID=" + solarSystemID + ", shipJumps=" + shipJumps + "]";
  }

  public static MapJump get(
                            final long time,
                            final int solarSystemID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<MapJump>() {
        @Override
        public MapJump run() throws Exception {
          TypedQuery<MapJump> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("MapJump.get", MapJump.class);
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

  public static List<MapJump> accessQuery(
                                          final long contid,
                                          final int maxresults,
                                          final boolean reverse,
                                          final AttributeSelector at,
                                          final AttributeSelector solarSystemID,
                                          final AttributeSelector shipJumps) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<MapJump>>() {
        @Override
        public List<MapJump> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MapJump c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addIntSelector(qs, "c", "shipJumps", shipJumps);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MapJump> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), MapJump.class);
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
