package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_mailing_list",
    indexes = {
        @Index(
            name = "listIDIndex",
            columnList = "listID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "MailingList.getByListID",
        query = "SELECT c FROM MailingList c where c.owner = :owner and c.listID = :list and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "MailingList.getAllListID",
        query = "SELECT c.listID FROM MailingList c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.listID asc"),
    @NamedQuery(
        name = "MailingList.getAll",
        query = "SELECT c FROM MailingList c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.listID asc"),
})
// 7 hour cache time - API caches for 6 hour
public class MailingList extends CachedData {
  private static final Logger log  = Logger.getLogger(MailingList.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAILING_LISTS);
  private String              displayName;
  private long                listID;

  @SuppressWarnings("unused")
  private MailingList() {}

  public MailingList(String displayName, long listID) {
    super();
    this.displayName = displayName;
    this.listID = listID;
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
    if (!(sup instanceof MailingList)) return false;
    MailingList other = (MailingList) sup;
    return nullSafeObjectCompare(displayName, other.displayName) && listID == other.listID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getDisplayName() {
    return displayName;
  }

  public long getListID() {
    return listID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + (int) (listID ^ (listID >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MailingList other = (MailingList) obj;
    if (displayName == null) {
      if (other.displayName != null) return false;
    } else if (!displayName.equals(other.displayName)) return false;
    if (listID != other.listID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MailingList [displayName=" + displayName + ", listID=" + listID + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static MailingList get(
                                final SynchronizedEveAccount owner,
                                final long time,
                                final long listID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<MailingList>() {
        @Override
        public MailingList run() throws Exception {
          TypedQuery<MailingList> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MailingList.getByListID",
                                                                                                                      MailingList.class);
          getter.setParameter("owner", owner);
          getter.setParameter("list", listID);
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

  public static List<Long> getAllListIDs(
                                         final SynchronizedEveAccount owner,
                                         final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Long>>() {
        @Override
        public List<Long> run() throws Exception {
          TypedQuery<Long> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MailingList.getAllListID", Long.class);
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

  public static List<MailingList> getAllLists(
                                              final SynchronizedEveAccount owner,
                                              final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MailingList>>() {
        @Override
        public List<MailingList> run() throws Exception {
          TypedQuery<MailingList> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MailingList.getAll", MailingList.class);
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

  public static List<MailingList> accessQuery(
                                              final SynchronizedEveAccount owner,
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector displayName,
                                              final AttributeSelector listID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MailingList>>() {
        @Override
        public List<MailingList> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MailingList c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addStringSelector(qs, "c", "displayName", displayName, p);
          AttributeSelector.addLongSelector(qs, "c", "listID", listID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MailingList> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), MailingList.class);
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
