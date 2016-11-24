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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_eve_requiredskill")
@NamedQueries({
    @NamedQuery(
        name = "RequiredSkill.get",
        query = "SELECT c FROM RequiredSkill c WHERE c.parentTypeID = :ptid AND c.typeID = :tid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class RequiredSkill extends RefCachedData {
  private static final Logger log = Logger.getLogger(RequiredSkill.class.getName());
  private int                 parentTypeID;
  private int                 typeID;
  private int                 level;

  @SuppressWarnings("unused")
  private RequiredSkill() {}

  public RequiredSkill(int parentTypeID, int typeID, int level) {
    super();
    this.parentTypeID = parentTypeID;
    this.typeID = typeID;
    this.level = level;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof RequiredSkill)) return false;
    RequiredSkill other = (RequiredSkill) sup;
    return parentTypeID == other.parentTypeID && typeID == other.typeID && level == other.level;
  }

  public int getParentTypeID() {
    return parentTypeID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + level;
    result = prime * result + parentTypeID;
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    RequiredSkill other = (RequiredSkill) obj;
    if (level != other.level) return false;
    if (parentTypeID != other.parentTypeID) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "RequiredSkill [parentTypeID=" + parentTypeID + ", typeID=" + typeID + ", level=" + level + "]";
  }

  public static RequiredSkill get(
                                  final long time,
                                  final int parentTypeID,
                                  final int typeID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RequiredSkill>() {
        @Override
        public RequiredSkill run() throws Exception {
          TypedQuery<RequiredSkill> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("RequiredSkill.get", RequiredSkill.class);
          getter.setParameter("point", time);
          getter.setParameter("ptid", parentTypeID);
          getter.setParameter("tid", typeID);
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

  public static List<RequiredSkill> accessQuery(
                                                final long contid,
                                                final int maxresults,
                                                final boolean reverse,
                                                final AttributeSelector at,
                                                final AttributeSelector parentTypeID,
                                                final AttributeSelector typeID,
                                                final AttributeSelector level) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<RequiredSkill>>() {
        @Override
        public List<RequiredSkill> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM RequiredSkill c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "parentTypeID", parentTypeID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addIntSelector(qs, "c", "level", level);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<RequiredSkill> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), RequiredSkill.class);
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
