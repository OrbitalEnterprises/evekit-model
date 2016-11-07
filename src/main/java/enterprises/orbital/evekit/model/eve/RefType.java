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
    name = "evekit_eve_reftype")
@NamedQueries({
    @NamedQuery(
        name = "RefType.get",
        query = "SELECT c FROM RefType c WHERE c.refTypeID = :rid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class RefType extends RefCachedData {
  private static final Logger log = Logger.getLogger(RefType.class.getName());
  private int                 refTypeID;
  private String              refTypeName;

  @SuppressWarnings("unused")
  private RefType() {}

  public RefType(int refTypeID, String refTypeName) {
    super();
    this.refTypeID = refTypeID;
    this.refTypeName = refTypeName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof RefType)) return false;
    RefType other = (RefType) sup;
    return refTypeID == other.refTypeID && nullSafeObjectCompare(refTypeName, other.refTypeName);
  }

  public int getRefTypeID() {
    return refTypeID;
  }

  public String getRefTypeName() {
    return refTypeName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + refTypeID;
    result = prime * result + ((refTypeName == null) ? 0 : refTypeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    RefType other = (RefType) obj;
    if (refTypeID != other.refTypeID) return false;
    if (refTypeName == null) {
      if (other.refTypeName != null) return false;
    } else if (!refTypeName.equals(other.refTypeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "RefType [refTypeID=" + refTypeID + ", refTypeName=" + refTypeName + "]";
  }

  public static RefType get(
                            final long time,
                            final int refTypeID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RefType>() {
        @Override
        public RefType run() throws Exception {
          TypedQuery<RefType> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("RefType.get", RefType.class);
          getter.setParameter("point", time);
          getter.setParameter("rid", refTypeID);
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

  public static List<RefType> accessQuery(
                                          final long contid,
                                          final int maxresults,
                                          final boolean reverse,
                                          final AttributeSelector at,
                                          final AttributeSelector refTypeID,
                                          final AttributeSelector refTypeName) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<RefType>>() {
        @Override
        public List<RefType> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM RefType c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "refTypeID", refTypeID);
          AttributeSelector.addStringSelector(qs, "c", "refTypeName", refTypeName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<RefType> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), RefType.class);
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
