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
        query = "SELECT c FROM SkillGroup c WHERE c.type = :type AND c.name = :name AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SkillGroup extends RefCachedData {
  private static final Logger log = Logger.getLogger(SkillGroup.class.getName());
  private String              type;
  private String              name;
  private long                groupID;
  private String              description;

  @SuppressWarnings("unused")
  private SkillGroup() {}

  public SkillGroup(String type, String name, long groupID, String description) {
    super();
    this.type = type;
    this.name = name;
    this.groupID = groupID;
    this.description = description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof SkillGroup)) return false;
    SkillGroup other = (SkillGroup) sup;
    return nullSafeObjectCompare(type, other.type) && nullSafeObjectCompare(name, other.name) && groupID == other.groupID
        && nullSafeObjectCompare(description, other.description);
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public long getGroupID() {
    return groupID;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (int) (groupID ^ (groupID >>> 32));
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SkillGroup other = (SkillGroup) obj;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (groupID != other.groupID) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (type == null) {
      if (other.type != null) return false;
    } else if (!type.equals(other.type)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SkillGroup [type=" + type + ", name=" + name + ", groupID=" + groupID + ", description=" + description + "]";
  }

  public static SkillGroup get(
                               final long time,
                               final String type,
                               final String name) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<SkillGroup>() {
        @Override
        public SkillGroup run() throws Exception {
          TypedQuery<SkillGroup> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("SkillGroup.get", SkillGroup.class);
          getter.setParameter("point", time);
          getter.setParameter("type", type);
          getter.setParameter("name", name);
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
                                             final AttributeSelector type,
                                             final AttributeSelector name,
                                             final AttributeSelector groupID,
                                             final AttributeSelector description) {
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
          AttributeSelector.addStringSelector(qs, "c", "type", type, p);
          AttributeSelector.addStringSelector(qs, "c", "name", name, p);
          AttributeSelector.addLongSelector(qs, "c", "groupID", groupID);
          AttributeSelector.addStringSelector(qs, "c", "description", description, p);
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
