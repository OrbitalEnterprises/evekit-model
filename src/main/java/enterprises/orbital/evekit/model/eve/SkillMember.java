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
    name = "evekit_eve_skillmember")
@NamedQueries({
    @NamedQuery(
        name = "SkillMember.get",
        query = "SELECT c FROM SkillMember c WHERE c.groupID = :gid AND c.typeID = :tid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class SkillMember extends RefCachedData {
  private static final Logger log = Logger.getLogger(SkillMember.class.getName());

  private int                 groupID;
  private int                 typeID;
  private String              description;
  private int                 rank;
  private String              requiredPrimaryAttribute;
  private String              requiredSecondaryAttribute;
  private String              typeName;
  private boolean             published;

  @SuppressWarnings("unused")
  private SkillMember() {}

  public SkillMember(String description, int groupID, String requiredPrimaryAttribute, String requiredSecondaryAttribute, int typeID, String typeName,
                     boolean published) {
    super();
    this.description = description;
    this.groupID = groupID;
    this.requiredPrimaryAttribute = requiredPrimaryAttribute;
    this.requiredSecondaryAttribute = requiredSecondaryAttribute;
    this.typeID = typeID;
    this.typeName = typeName;
    this.published = published;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof SkillMember)) return false;
    SkillMember other = (SkillMember) sup;
    return nullSafeObjectCompare(description, other.description) && groupID == other.groupID && rank == other.rank
        && nullSafeObjectCompare(requiredPrimaryAttribute, other.requiredPrimaryAttribute)
        && nullSafeObjectCompare(requiredSecondaryAttribute, other.requiredSecondaryAttribute) && typeID == other.typeID
        && nullSafeObjectCompare(typeName, other.typeName) && published == other.published;
  }

  public String getDescription() {
    return description;
  }

  public int getGroupID() {
    return groupID;
  }

  public int getRank() {
    return rank;
  }

  public String getRequiredPrimaryAttribute() {
    return requiredPrimaryAttribute;
  }

  public String getRequiredSecondaryAttribute() {
    return requiredSecondaryAttribute;
  }

  public int getTypeID() {
    return typeID;
  }

  public String getTypeName() {
    return typeName;
  }

  public boolean isPublished() {
    return published;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + groupID;
    result = prime * result + (published ? 1231 : 1237);
    result = prime * result + rank;
    result = prime * result + ((requiredPrimaryAttribute == null) ? 0 : requiredPrimaryAttribute.hashCode());
    result = prime * result + ((requiredSecondaryAttribute == null) ? 0 : requiredSecondaryAttribute.hashCode());
    result = prime * result + typeID;
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SkillMember other = (SkillMember) obj;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (groupID != other.groupID) return false;
    if (published != other.published) return false;
    if (rank != other.rank) return false;
    if (requiredPrimaryAttribute == null) {
      if (other.requiredPrimaryAttribute != null) return false;
    } else if (!requiredPrimaryAttribute.equals(other.requiredPrimaryAttribute)) return false;
    if (requiredSecondaryAttribute == null) {
      if (other.requiredSecondaryAttribute != null) return false;
    } else if (!requiredSecondaryAttribute.equals(other.requiredSecondaryAttribute)) return false;
    if (typeID != other.typeID) return false;
    if (typeName == null) {
      if (other.typeName != null) return false;
    } else if (!typeName.equals(other.typeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SkillMember [groupID=" + groupID + ", typeID=" + typeID + ", description=" + description + ", rank=" + rank + ", requiredPrimaryAttribute="
        + requiredPrimaryAttribute + ", requiredSecondaryAttribute=" + requiredSecondaryAttribute + ", typeName=" + typeName + ", published=" + published + "]";
  }

  public static SkillMember get(
                                final long time,
                                final int groupID,
                                final int typeID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<SkillMember>() {
        @Override
        public SkillMember run() throws Exception {
          TypedQuery<SkillMember> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("SkillMember.get", SkillMember.class);
          getter.setParameter("point", time);
          getter.setParameter("gid", groupID);
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

  public static List<SkillMember> accessQuery(
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector groupID,
                                              final AttributeSelector typeID,
                                              final AttributeSelector description,
                                              final AttributeSelector rank,
                                              final AttributeSelector requiredPrimaryAttribute,
                                              final AttributeSelector requiredSecondaryAttribute,
                                              final AttributeSelector typeName,
                                              final AttributeSelector published) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<SkillMember>>() {
        @Override
        public List<SkillMember> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM SkillMember c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "groupID", groupID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addStringSelector(qs, "c", "description", description, p);
          AttributeSelector.addIntSelector(qs, "c", "rank", rank);
          AttributeSelector.addStringSelector(qs, "c", "requiredPrimaryAttribute", requiredPrimaryAttribute, p);
          AttributeSelector.addStringSelector(qs, "c", "requiredSecondaryAttribute", requiredSecondaryAttribute, p);
          AttributeSelector.addStringSelector(qs, "c", "typeName", typeName, p);
          AttributeSelector.addBooleanSelector(qs, "c", "published", published);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<SkillMember> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), SkillMember.class);
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
