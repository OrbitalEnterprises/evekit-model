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
    name = "evekit_eve_factionwar")
@NamedQueries({
    @NamedQuery(
        name = "FactionWar.get",
        query = "SELECT c FROM FactionWar c WHERE c.againstID = :aid AND c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionWar extends RefCachedData {
  private static final Logger log = Logger.getLogger(FactionWar.class.getName());
  private long                againstID;
  private String              againstName;
  private long                factionID;
  private String              factionName;

  @SuppressWarnings("unused")
  private FactionWar() {}

  public FactionWar(long againstID, String againstName, long factionID, String factionName) {
    super();
    this.againstID = againstID;
    this.againstName = againstName;
    this.factionID = factionID;
    this.factionName = factionName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof FactionWar)) return false;
    FactionWar other = (FactionWar) sup;
    return againstID == other.againstID && nullSafeObjectCompare(againstName, other.againstName) && factionID == other.factionID
        && nullSafeObjectCompare(factionName, other.factionName);
  }

  public long getAgainstID() {
    return againstID;
  }

  public String getAgainstName() {
    return againstName;
  }

  public long getFactionID() {
    return factionID;
  }

  public String getFactionName() {
    return factionName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (againstID ^ (againstID >>> 32));
    result = prime * result + ((againstName == null) ? 0 : againstName.hashCode());
    result = prime * result + (int) (factionID ^ (factionID >>> 32));
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    FactionWar other = (FactionWar) obj;
    if (againstID != other.againstID) return false;
    if (againstName == null) {
      if (other.againstName != null) return false;
    } else if (!againstName.equals(other.againstName)) return false;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "FactionWar [againstID=" + againstID + ", againstName=" + againstName + ", factionID=" + factionID + ", factionName=" + factionName + "]";
  }

  public static FactionWar get(
                               final long time,
                               final long againstID,
                               final long factionID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<FactionWar>() {
        @Override
        public FactionWar run() throws Exception {
          TypedQuery<FactionWar> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("FactionWar.get", FactionWar.class);
          getter.setParameter("point", time);
          getter.setParameter("aid", againstID);
          getter.setParameter("fid", factionID);
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

  public static List<FactionWar> accessQuery(
                                             final long contid,
                                             final int maxresults,
                                             final boolean reverse,
                                             final AttributeSelector at,
                                             final AttributeSelector againstID,
                                             final AttributeSelector againstName,
                                             final AttributeSelector factionID,
                                             final AttributeSelector factionName) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<FactionWar>>() {
        @Override
        public List<FactionWar> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM FactionWar c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "againstID", againstID);
          AttributeSelector.addStringSelector(qs, "c", "againstName", againstName, p);
          AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<FactionWar> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), FactionWar.class);
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
