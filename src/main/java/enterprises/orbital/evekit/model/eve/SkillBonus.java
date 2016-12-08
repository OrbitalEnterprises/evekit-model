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
    name = "evekit_eve_skillbonus")
@NamedQueries({
    @NamedQuery(
        name = "SkillBonus.get",
        query = "SELECT c FROM SkillBonus c WHERE c.typeID = :tid AND c.bonusType = :bt AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SkillBonus extends RefCachedData {
  private static final Logger log = Logger.getLogger(SkillBonus.class.getName());
  private int                 typeID;
  private String              bonusType;
  private String              bonusValue;

  @SuppressWarnings("unused")
  private SkillBonus() {}

  public SkillBonus(int typeID, String bonusType, String bonusValue) {
    super();
    this.typeID = typeID;
    this.bonusType = bonusType;
    this.bonusValue = bonusValue;
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
    if (!(sup instanceof SkillBonus)) return false;
    SkillBonus other = (SkillBonus) sup;
    return typeID == other.typeID && nullSafeObjectCompare(bonusType, other.bonusType) && nullSafeObjectCompare(bonusValue, other.bonusValue);
  }

  public int getTypeID() {
    return typeID;
  }

  public String getBonusType() {
    return bonusType;
  }

  public String getBonusValue() {
    return bonusValue;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((bonusType == null) ? 0 : bonusType.hashCode());
    result = prime * result + ((bonusValue == null) ? 0 : bonusValue.hashCode());
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SkillBonus other = (SkillBonus) obj;
    if (bonusType == null) {
      if (other.bonusType != null) return false;
    } else if (!bonusType.equals(other.bonusType)) return false;
    if (bonusValue == null) {
      if (other.bonusValue != null) return false;
    } else if (!bonusValue.equals(other.bonusValue)) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SkillBonus [typeID=" + typeID + ", bonusType=" + bonusType + ", bonusValue=" + bonusValue + "]";
  }

  public static SkillBonus get(
                               final long time,
                               final int typeID,
                               final String bonusType) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<SkillBonus>() {
        @Override
        public SkillBonus run() throws Exception {
          TypedQuery<SkillBonus> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("SkillBonus.get", SkillBonus.class);
          getter.setParameter("point", time);
          getter.setParameter("tid", typeID);
          getter.setParameter("bt", bonusType);
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

  public static List<SkillBonus> accessQuery(
                                             final long contid,
                                             final int maxresults,
                                             final boolean reverse,
                                             final AttributeSelector at,
                                             final AttributeSelector typeID,
                                             final AttributeSelector bonusType,
                                             final AttributeSelector bonusValue) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<SkillBonus>>() {
        @Override
        public List<SkillBonus> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM SkillBonus c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addStringSelector(qs, "c", "bonusType", bonusType, p);
          AttributeSelector.addStringSelector(qs, "c", "bonusValue", bonusValue, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<SkillBonus> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), SkillBonus.class);
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
