package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_security_title",
    indexes = {
        @Index(
            name = "titleIDIndex",
            columnList = "titleID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "SecurityTitle.getByTitleID",
        query = "SELECT c FROM SecurityTitle c where c.owner = :owner and c.titleID = :title and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "SecurityTitle.getAll",
        query = "SELECT c FROM SecurityTitle c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class SecurityTitle extends CachedData {
  private static final Logger log  = Logger.getLogger(SecurityTitle.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY);
  private long                titleID;
  private String              titleName;

  @SuppressWarnings("unused")
  private SecurityTitle() {}

  public SecurityTitle(long titleID, String titleName) {
    super();
    this.titleID = titleID;
    this.titleName = titleName;
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
                            CachedData sup) {
    if (!(sup instanceof SecurityTitle)) return false;
    SecurityTitle other = (SecurityTitle) sup;
    return titleID == other.titleID && nullSafeObjectCompare(titleName, other.titleName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getTitleID() {
    return titleID;
  }

  public String getTitleName() {
    return titleName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (titleID ^ (titleID >>> 32));
    result = prime * result + ((titleName == null) ? 0 : titleName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SecurityTitle other = (SecurityTitle) obj;
    if (titleID != other.titleID) return false;
    if (titleName == null) {
      if (other.titleName != null) return false;
    } else if (!titleName.equals(other.titleName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SecurityTitle [titleID=" + titleID + ", titleName=" + titleName + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static SecurityTitle get(
                                  final SynchronizedEveAccount owner,
                                  final long time,
                                  final long titleID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<SecurityTitle>() {
        @Override
        public SecurityTitle run() throws Exception {
          TypedQuery<SecurityTitle> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("SecurityTitle.getByTitleID",
                                                                                                                        SecurityTitle.class);
          getter.setParameter("owner", owner);
          getter.setParameter("title", titleID);
          getter.setParameter("point", time);
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

  public static List<SecurityTitle> getAll(
                                           final SynchronizedEveAccount owner,
                                           final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<SecurityTitle>>() {
        @Override
        public List<SecurityTitle> run() throws Exception {
          TypedQuery<SecurityTitle> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("SecurityTitle.getAll",
                                                                                                                        SecurityTitle.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<SecurityTitle> accessQuery(
                                                final SynchronizedEveAccount owner,
                                                final long contid,
                                                final int maxresults,
                                                final boolean reverse,
                                                final AttributeSelector at,
                                                final AttributeSelector titleID,
                                                final AttributeSelector titleName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<SecurityTitle>>() {
        @Override
        public List<SecurityTitle> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM SecurityTitle c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "titleID", titleID);
          AttributeSelector.addStringSelector(qs, "c", "titleName", titleName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<SecurityTitle> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), SecurityTitle.class);
          query.setParameter("owner", owner);
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
