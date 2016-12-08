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
import enterprises.orbital.evekit.model.AttributeSelector.EnumMapper;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_eve_factionkillstat")
@NamedQueries({
    @NamedQuery(
        name = "FactionKillStat.get",
        query = "SELECT c FROM FactionKillStat c WHERE c.attribute = :attr AND c.factionID = :fid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class FactionKillStat extends AbstractKillStat {
  private static final Logger log = Logger.getLogger(FactionKillStat.class.getName());
  private long                factionID;
  private String              factionName;

  private FactionKillStat() {
    super(StatAttribute.TOTAL, 0);
  }

  public FactionKillStat(StatAttribute attribute, int kills, long factionID, String factionName) {
    super(attribute, kills);
    this.factionID = factionID;
    this.factionName = factionName;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof FactionKillStat)) return false;
    if (!super.equivalent(sup)) return false;
    FactionKillStat other = (FactionKillStat) sup;
    return factionID == other.factionID && nullSafeObjectCompare(factionName, other.factionName);
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
    FactionKillStat other = (FactionKillStat) obj;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "FactionKillStat [factionID=" + factionID + ", factionName=" + factionName + ", attribute=" + attribute + ", kills=" + kills + "]";
  }

  public static FactionKillStat get(
                                    final long time,
                                    final StatAttribute attr,
                                    final long factionID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<FactionKillStat>() {
        @Override
        public FactionKillStat run() throws Exception {
          TypedQuery<FactionKillStat> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("FactionKillStat.get",
                                                                                                                      FactionKillStat.class);
          getter.setParameter("point", time);
          getter.setParameter("attr", attr);
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

  public static List<FactionKillStat> accessQuery(
                                                  final long contid,
                                                  final int maxresults,
                                                  final boolean reverse,
                                                  final AttributeSelector at,
                                                  final AttributeSelector attribute,
                                                  final AttributeSelector factionID,
                                                  final AttributeSelector factionName,
                                                  final AttributeSelector kills) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<FactionKillStat>>() {
        @Override
        public List<FactionKillStat> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM FactionKillStat c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addEnumSelector(qs, "c", "attribute", attribute, new EnumMapper<StatAttribute>() {

            @Override
            public StatAttribute mapEnumValue(
                                              String value) {
              return StatAttribute.valueOf(value);
            }
          }, p);
          AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          AttributeSelector.addIntSelector(qs, "c", "kills", kills);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<FactionKillStat> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), FactionKillStat.class);
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
