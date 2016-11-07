package enterprises.orbital.evekit.model.calls;

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
    name = "evekit_calls_call_group")
@NamedQueries({
    @NamedQuery(
        name = "CallGroup.get",
        query = "SELECT c FROM CallGroup c WHERE c.groupID = :gid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CallGroup extends RefCachedData {
  private static final Logger log = Logger.getLogger(CallGroup.class.getName());
  private long                groupID;
  private String              name;
  private String              description;

  @SuppressWarnings("unused")
  private CallGroup() {}

  public CallGroup(long groupID, String name, String description) {
    super();
    this.groupID = groupID;
    this.name = name;
    this.description = description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof CallGroup)) return false;
    CallGroup other = (CallGroup) sup;
    return groupID == other.groupID && nullSafeObjectCompare(name, other.name) && nullSafeObjectCompare(description, other.description);
  }

  public long getGroupID() {
    return groupID;
  }

  public String getName() {
    return name;
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
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CallGroup other = (CallGroup) obj;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (groupID != other.groupID) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CallGroup [groupID=" + groupID + ", name=" + name + ", description=" + description + "]";
  }

  public static CallGroup get(
                              final long time,
                              final long groupID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<CallGroup>() {
        @Override
        public CallGroup run() throws Exception {
          TypedQuery<CallGroup> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("CallGroup.get", CallGroup.class);
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

  public static List<CallGroup> accessQuery(
                                            final long contid,
                                            final int maxresults,
                                            final boolean reverse,
                                            final AttributeSelector at,
                                            final AttributeSelector groupID,
                                            final AttributeSelector name,
                                            final AttributeSelector description) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<CallGroup>>() {
        @Override
        public List<CallGroup> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CallGroup c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "groupID", groupID);
          AttributeSelector.addStringSelector(qs, "c", "name", name, p);
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
          TypedQuery<CallGroup> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), CallGroup.class);
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
