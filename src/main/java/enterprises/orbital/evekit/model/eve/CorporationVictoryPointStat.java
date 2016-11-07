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
    name = "evekit_eve_corpvpstat")
@NamedQueries({
    @NamedQuery(
        name = "CorporationVictoryPointStat.get",
        query = "SELECT c FROM CorporationVictoryPointStat c WHERE c.corporationID = :corpid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CorporationVictoryPointStat extends RefCachedData {
  private static final Logger log = Logger.getLogger(CorporationVictoryPointStat.class.getName());
  private long                corporationID;
  private String              corporationName;
  private int                 victoryPoints;

  @SuppressWarnings("unused")
  private CorporationVictoryPointStat() {}

  public CorporationVictoryPointStat(long corporationID, String corporationName, int victoryPoints) {
    super();
    this.corporationID = corporationID;
    this.corporationName = corporationName;
    this.victoryPoints = victoryPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof CorporationVictoryPointStat)) return false;
    CorporationVictoryPointStat other = (CorporationVictoryPointStat) sup;
    return corporationID == other.corporationID && nullSafeObjectCompare(corporationName, other.corporationName) && victoryPoints == other.victoryPoints;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public String getCorporationName() {
    return corporationName;
  }

  public int getVictoryPoints() {
    return victoryPoints;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((corporationName == null) ? 0 : corporationName.hashCode());
    result = prime * result + victoryPoints;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CorporationVictoryPointStat other = (CorporationVictoryPointStat) obj;
    if (corporationID != other.corporationID) return false;
    if (corporationName == null) {
      if (other.corporationName != null) return false;
    } else if (!corporationName.equals(other.corporationName)) return false;
    if (victoryPoints != other.victoryPoints) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CorporationVictoryPointStat [corporationID=" + corporationID + ", corporationName=" + corporationName + ", victoryPoints=" + victoryPoints + "]";
  }

  public static CorporationVictoryPointStat get(
                                                final long time,
                                                final long corporationID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<CorporationVictoryPointStat>() {
        @Override
        public CorporationVictoryPointStat run() throws Exception {
          TypedQuery<CorporationVictoryPointStat> getter = EveKitRefDataProvider.getFactory().getEntityManager()
              .createNamedQuery("CorporationVictoryPointStat.get", CorporationVictoryPointStat.class);
          getter.setParameter("point", time);
          getter.setParameter("corpid", corporationID);
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

  public static List<CorporationVictoryPointStat> accessQuery(
                                                              final long contid,
                                                              final int maxresults,
                                                              final boolean reverse,
                                                              final AttributeSelector at,
                                                              final AttributeSelector corporationID,
                                                              final AttributeSelector corporationName,
                                                              final AttributeSelector victoryPoints) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationVictoryPointStat>>() {
        @Override
        public List<CorporationVictoryPointStat> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CorporationVictoryPointStat c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
          AttributeSelector.addStringSelector(qs, "c", "corporationName", corporationName, p);
          AttributeSelector.addIntSelector(qs, "c", "victoryPoints", victoryPoints);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CorporationVictoryPointStat> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                            CorporationVictoryPointStat.class);
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
