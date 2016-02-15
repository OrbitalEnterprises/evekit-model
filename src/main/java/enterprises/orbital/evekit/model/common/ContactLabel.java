package enterprises.orbital.evekit.model.common;

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
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_contact_label", indexes = {
    @Index(name = "listIndex", columnList = "list", unique = false), @Index(name = "labelIDIndex", columnList = "labelID", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "ContactLabel.getByListAndLabelID",
        query = "SELECT c FROM ContactLabel c where c.owner = :owner and c.list = :list and c.labelID = :label and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ContactLabel.getAll",
        query = "SELECT c FROM ContactLabel c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "ContactLabel.getAllByList",
        query = "SELECT c FROM ContactLabel c where c.owner = :owner and c.list = :list and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class ContactLabel extends CachedData {
  private static final Logger log  = Logger.getLogger(ContactLabel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST);
  private String              list;
  private long                labelID;
  private String              name;

  @SuppressWarnings("unused")
  private ContactLabel() {}

  public ContactLabel(String list, long labelID, String name) {
    super();
    this.list = list;
    this.labelID = labelID;
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof ContactLabel)) return false;
    ContactLabel other = (ContactLabel) sup;
    return nullSafeObjectCompare(list, other.list) && labelID == other.labelID && nullSafeObjectCompare(name, other.name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getList() {
    return list;
  }

  public long getLabelID() {
    return labelID;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (labelID ^ (labelID >>> 32));
    result = prime * result + ((list == null) ? 0 : list.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ContactLabel other = (ContactLabel) obj;
    if (labelID != other.labelID) return false;
    if (list == null) {
      if (other.list != null) return false;
    } else if (!list.equals(other.list)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ContactLabel [list=" + list + ", labelID=" + labelID + ", name=" + name + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd
        + "]";
  }

  public static ContactLabel get(final SynchronizedEveAccount owner, final long time, final String list, final long labelID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ContactLabel>() {
        @Override
        public ContactLabel run() throws Exception {
          TypedQuery<ContactLabel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContactLabel.getByListAndLabelID",
                                                                                                                       ContactLabel.class);
          getter.setParameter("owner", owner);
          getter.setParameter("list", list);
          getter.setParameter("label", labelID);
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

  public static List<ContactLabel> getAllContactLabels(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContactLabel>>() {
        @Override
        public List<ContactLabel> run() throws Exception {
          TypedQuery<ContactLabel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContactLabel.getAll",
                                                                                                                       ContactLabel.class);
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

  public static List<ContactLabel> getByList(final SynchronizedEveAccount owner, final long time, final String list) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContactLabel>>() {
        @Override
        public List<ContactLabel> run() throws Exception {
          TypedQuery<ContactLabel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContactLabel.getAllByList",
                                                                                                                       ContactLabel.class);
          getter.setParameter("owner", owner);
          getter.setParameter("list", list);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
