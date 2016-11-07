package enterprises.orbital.evekit.model.eve;

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
    name = "evekit_eve_conq_station")
@NamedQueries({
    @NamedQuery(
        name = "ConquerableStation.get",
        query = "SELECT c FROM ConquerableStation c WHERE c.stationID = :stationid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class ConquerableStation extends RefCachedData {
  private static final Logger log = Logger.getLogger(ConquerableStation.class.getName());
  private long                corporationID;
  private String              corporationName;
  private long                solarSystemID;
  private long                stationID;
  private String              stationName;
  private int                 stationTypeID;
  private long                x;
  private long                y;
  private long                z;

  @SuppressWarnings("unused")
  private ConquerableStation() {}

  public ConquerableStation(long corporationID, String corporationName, long solarSystemID, long stationID, String stationName, int stationTypeID, long x,
                            long y, long z) {
    super();
    this.corporationID = corporationID;
    this.corporationName = corporationName;
    this.solarSystemID = solarSystemID;
    this.stationID = stationID;
    this.stationName = stationName;
    this.stationTypeID = stationTypeID;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof ConquerableStation)) return false;
    ConquerableStation other = (ConquerableStation) sup;
    return corporationID == other.corporationID && nullSafeObjectCompare(corporationName, other.corporationName) && solarSystemID == other.solarSystemID
        && stationID == other.stationID && nullSafeObjectCompare(stationName, other.stationName) && stationTypeID == other.stationTypeID && x == other.x
        && y == other.y && z == other.z;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public String getCorporationName() {
    return corporationName;
  }

  public long getSolarSystemID() {
    return solarSystemID;
  }

  public long getStationID() {
    return stationID;
  }

  public String getStationName() {
    return stationName;
  }

  public int getStationTypeID() {
    return stationTypeID;
  }

  public long getX() {
    return x;
  }

  public long getY() {
    return y;
  }

  public long getZ() {
    return z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((corporationName == null) ? 0 : corporationName.hashCode());
    result = prime * result + (int) (solarSystemID ^ (solarSystemID >>> 32));
    result = prime * result + (int) (stationID ^ (stationID >>> 32));
    result = prime * result + ((stationName == null) ? 0 : stationName.hashCode());
    result = prime * result + stationTypeID;
    result = prime * result + (int) (x ^ (x >>> 32));
    result = prime * result + (int) (y ^ (y >>> 32));
    result = prime * result + (int) (z ^ (z >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ConquerableStation other = (ConquerableStation) obj;
    if (corporationID != other.corporationID) return false;
    if (corporationName == null) {
      if (other.corporationName != null) return false;
    } else if (!corporationName.equals(other.corporationName)) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (stationID != other.stationID) return false;
    if (stationName == null) {
      if (other.stationName != null) return false;
    } else if (!stationName.equals(other.stationName)) return false;
    if (stationTypeID != other.stationTypeID) return false;
    if (x != other.x) return false;
    if (y != other.y) return false;
    if (z != other.z) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ConquerableStation [corporationID=" + corporationID + ", corporationName=" + corporationName + ", solarSystemID=" + solarSystemID + ", stationID="
        + stationID + ", stationName=" + stationName + ", stationTypeID=" + stationTypeID + ", x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  public static ConquerableStation get(
                                       final long time,
                                       final long stationID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<ConquerableStation>() {
        @Override
        public ConquerableStation run() throws Exception {
          TypedQuery<ConquerableStation> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("ConquerableStation.get",
                                                                                                                         ConquerableStation.class);
          getter.setParameter("point", time);
          getter.setParameter("stationid", stationID);
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

  public static List<ConquerableStation> accessQuery(
                                                     final long contid,
                                                     final int maxresults,
                                                     final boolean reverse,
                                                     final AttributeSelector at,
                                                     final AttributeSelector corporationID,
                                                     final AttributeSelector corporationName,
                                                     final AttributeSelector solarSystemID,
                                                     final AttributeSelector stationID,
                                                     final AttributeSelector stationName,
                                                     final AttributeSelector stationTypeID,
                                                     final AttributeSelector x,
                                                     final AttributeSelector y,
                                                     final AttributeSelector z) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<ConquerableStation>>() {
        @Override
        public List<ConquerableStation> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ConquerableStation c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
          AttributeSelector.addStringSelector(qs, "c", "corporationName", corporationName, p);
          AttributeSelector.addLongSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
          AttributeSelector.addStringSelector(qs, "c", "stationName", stationName, p);
          AttributeSelector.addIntSelector(qs, "c", "stationTypeID", stationTypeID);
          AttributeSelector.addLongSelector(qs, "c", "x", x);
          AttributeSelector.addLongSelector(qs, "c", "y", y);
          AttributeSelector.addLongSelector(qs, "c", "z", z);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<ConquerableStation> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), ConquerableStation.class);
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
