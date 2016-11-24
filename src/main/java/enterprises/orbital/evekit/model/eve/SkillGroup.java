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
    name = "evekit_eve_skillgroup")
@NamedQueries({
    @NamedQuery(
        name = "SkillGroup.get",
        query = "SELECT c FROM SkillGroup c WHERE c.groupID = :gid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SkillGroup extends RefCachedData {
  private static final Logger log = Logger.getLogger(SkillGroup.class.getName());
  private int                 groupID;
  private String              groupName;

  @SuppressWarnings("unused")
  private SkillGroup() {}

  public SkillGroup(int groupID, String groupName) {
    super();
    this.groupID = groupID;
    this.groupName = groupName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof SkillGroup)) return false;
    SkillGroup other = (SkillGroup) sup;
    return groupID == other.groupID && nullSafeObjectCompare(groupName, other.groupName);
  }

  public int getGroupID() {
    return groupID;
  }

  public String getGroupName() {
    return groupName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + groupID;
    result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SkillGroup other = (SkillGroup) obj;
    if (groupID != other.groupID) return false;
    if (groupName == null) {
      if (other.groupName != null) return false;
    } else if (!groupName.equals(other.groupName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SkillGroup [groupID=" + groupID + ", groupName=" + groupName + "]";
  }

  public static SkillGroup get(
                               final long time,
                               final int groupID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<SkillGroup>() {
        @Override
        public SkillGroup run() throws Exception {
          TypedQuery<SkillGroup> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("SkillGroup.get", SkillGroup.class);
          getter.setParameter("point", time);
          getter.setParameter("gid", groupID);
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

  public static List<SkillGroup> accessQuery(
                                             final long contid,
                                             final int maxresults,
                                             final boolean reverse,
                                             final AttributeSelector at,
                                             final AttributeSelector groupID,
                                             final AttributeSelector groupName) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<SkillGroup>>() {
        @Override
        public List<SkillGroup> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM SkillGroup c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "groupID", groupID);
          AttributeSelector.addStringSelector(qs, "c", "groupName", groupName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<SkillGroup> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), SkillGroup.class);
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
