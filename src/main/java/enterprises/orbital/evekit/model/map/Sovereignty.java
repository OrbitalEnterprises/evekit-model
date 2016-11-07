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
    name = "evekit_map_sov")
@NamedQueries({
    @NamedQuery(
        name = "Sovereignty.get",
        query = "SELECT c FROM Sovereignty c WHERE c.solarSystemID = :sid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class Sovereignty extends RefCachedData {
  private static final Logger log = Logger.getLogger(Sovereignty.class.getName());
  private long                allianceID;
  private long                corporationID;
  private long                factionID;
  private int                 solarSystemID;
  private String              solarSystemName;

  @SuppressWarnings("unused")
  private Sovereignty() {}

  public Sovereignty(long allianceID, long corporationID, long factionID, int solarSystemID, String solarSystemName) {
    super();
    this.allianceID = allianceID;
    this.corporationID = corporationID;
    this.factionID = factionID;
    this.solarSystemID = solarSystemID;
    this.solarSystemName = solarSystemName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof Sovereignty)) return false;
    Sovereignty other = (Sovereignty) sup;
    return allianceID == other.allianceID && corporationID == other.corporationID && factionID == other.factionID && solarSystemID == other.solarSystemID
        && nullSafeObjectCompare(solarSystemName, other.solarSystemName);
  }

  public long getAllianceID() {
    return allianceID;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public long getFactionID() {
    return factionID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getSolarSystemName() {
    return solarSystemName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + (int) (factionID ^ (factionID >>> 32));
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
    Sovereignty other = (Sovereignty) obj;
    if (allianceID != other.allianceID) return false;
    if (corporationID != other.corporationID) return false;
    if (factionID != other.factionID) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (solarSystemName == null) {
      if (other.solarSystemName != null) return false;
    } else if (!solarSystemName.equals(other.solarSystemName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Sovereignty [allianceID=" + allianceID + ", corporationID=" + corporationID + ", factionID=" + factionID + ", solarSystemID=" + solarSystemID
        + ", solarSystemName=" + solarSystemName + "]";
  }

  public static Sovereignty get(
                                final long time,
                                final int solarSystemID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<Sovereignty>() {
        @Override
        public Sovereignty run() throws Exception {
          TypedQuery<Sovereignty> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("Sovereignty.get", Sovereignty.class);
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

  public static List<Sovereignty> accessQuery(
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector allianceID,
                                              final AttributeSelector corporationID,
                                              final AttributeSelector factionID,
                                              final AttributeSelector solarSystemID,
                                              final AttributeSelector solarSystemName) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<Sovereignty>>() {
        @Override
        public List<Sovereignty> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Sovereignty c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
          AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addStringSelector(qs, "c", "solarSystemName", solarSystemName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Sovereignty> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), Sovereignty.class);
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
