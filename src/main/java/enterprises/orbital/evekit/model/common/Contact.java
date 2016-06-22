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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_contact",
    indexes = {
        @Index(
            name = "listIndex",
            columnList = "list",
            unique = false),
        @Index(
            name = "contactIDIndex",
            columnList = "contactID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Contact.getByContactID",
        query = "SELECT c FROM Contact c where c.owner = :owner and c.list = :list and c.contactID = :contact and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Contact.getAll",
        query = "SELECT c FROM Contact c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "Contact.getAllBounded",
        query = "SELECT c FROM Contact c where c.owner = :owner and c.cid > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "Contact.getAllByList",
        query = "SELECT c FROM Contact c where c.owner = :owner and c.list = :list and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "Contact.getAllByListBounded",
        query = "SELECT c FROM Contact c where c.owner = :owner and c.list = :list and c.cid > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class Contact extends CachedData {
  private static final Logger log                 = Logger.getLogger(Contact.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private String              list;
  private int                 contactID;
  private String              contactName;
  private double              standing;
  private int                 contactTypeID;
  // Only valid for character contacts, false for everything else
  private boolean             inWatchlist;
  private long                labelMask;

  @SuppressWarnings("unused")
  private Contact() {}

  public Contact(String list, int contactID, String contactName, double standing, int contactTypeID, boolean inWatchlist, long labelMask) {
    this.list = list;
    this.contactID = contactID;
    this.contactName = contactName;
    this.standing = standing;
    this.contactTypeID = contactTypeID;
    this.inWatchlist = inWatchlist;
    this.labelMask = labelMask;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Contact)) return false;
    Contact other = (Contact) sup;
    return nullSafeObjectCompare(list, other.list) && contactID == other.contactID && nullSafeObjectCompare(contactName, other.contactName)
        && standing == other.standing && contactTypeID == other.contactTypeID && inWatchlist == other.inWatchlist && labelMask == other.labelMask;
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

  public int getContactID() {
    return contactID;
  }

  public String getContactName() {
    return contactName;
  }

  public double getStanding() {
    return standing;
  }

  public int getContactTypeID() {
    return contactTypeID;
  }

  public boolean isInWatchlist() {
    return inWatchlist;
  }

  public long getLabelMask() {
    return labelMask;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + contactID;
    result = prime * result + ((contactName == null) ? 0 : contactName.hashCode());
    result = prime * result + contactTypeID;
    result = prime * result + (inWatchlist ? 1231 : 1237);
    result = prime * result + (int) (labelMask ^ (labelMask >>> 32));
    result = prime * result + ((list == null) ? 0 : list.hashCode());
    long temp;
    temp = Double.doubleToLongBits(standing);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Contact other = (Contact) obj;
    if (contactID != other.contactID) return false;
    if (contactName == null) {
      if (other.contactName != null) return false;
    } else if (!contactName.equals(other.contactName)) return false;
    if (contactTypeID != other.contactTypeID) return false;
    if (inWatchlist != other.inWatchlist) return false;
    if (labelMask != other.labelMask) return false;
    if (list == null) {
      if (other.list != null) return false;
    } else if (!list.equals(other.list)) return false;
    if (Double.doubleToLongBits(standing) != Double.doubleToLongBits(other.standing)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Contact [list=" + list + ", contactID=" + contactID + ", contactName=" + contactName + ", standing=" + standing + ", contactTypeID=" + contactTypeID
        + ", inWatchlist=" + inWatchlist + ", labelMask=" + labelMask + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Return contact with given properties, live at the given time. Returns null if no such contact exists.
   * 
   * @param owner
   *          contact owner
   * @param time
   *          time at which the contact must be live
   * @param list
   *          contact list
   * @param contactID
   *          contact ID
   * @return contact with the given properties live at the given time, or null.
   */
  public static Contact get(
                            final SynchronizedEveAccount owner,
                            final long time,
                            final String list,
                            final int contactID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Contact>() {
        @Override
        public Contact run() throws Exception {
          TypedQuery<Contact> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Contact.getByContactID", Contact.class);
          getter.setParameter("owner", owner);
          getter.setParameter("list", list);
          getter.setParameter("contact", contactID);
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

  /**
   * List contacts live at the given time with a sortKey (lexicographically) greater than contid.
   * 
   * @param owner
   *          contact owner
   * @param time
   *          time at which contact must be live
   * @param maxresults
   *          maximum number of contacts to retrieve
   * @param contid
   *          sortKey (exclusive) from which to start returning results.
   * @return a list of contacts no longer than maxresults with sortKey (lexicographically) greater than contid
   */
  public static List<Contact> getAllContacts(
                                             final SynchronizedEveAccount owner,
                                             final long time,
                                             int maxresults,
                                             final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Contact.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Contact>>() {
        @Override
        public List<Contact> run() throws Exception {
          TypedQuery<Contact> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Contact.getAllBounded", Contact.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  /**
   * List contacts for a given contact list, live at the given time, with a sortKey (lexicographically) greater than contid.
   * 
   * @param owner
   *          contact owner
   * @param time
   *          time at which contact must be live
   * @param list
   *          list name from which contacts should be returned
   * @param maxresults
   *          maximum number of contacts to retrieve
   * @param contid
   *          sortKey (exclusive) from which to start returning results
   * @return a list of contacts from the given list, no longer than maxresults, with sortKey (lexicographically) greater than contid
   */
  public static List<Contact> getByList(
                                        final SynchronizedEveAccount owner,
                                        final long time,
                                        final String list,
                                        int maxresults,
                                        final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Contact.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Contact>>() {
        @Override
        public List<Contact> run() throws Exception {
          TypedQuery<Contact> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Contact.getAllByListBounded", Contact.class);
          getter.setParameter("owner", owner);
          getter.setParameter("list", list);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<Contact> accessQuery(
                                          final SynchronizedEveAccount owner,
                                          final long contid,
                                          final int maxresults,
                                          final boolean reverse,
                                          final AttributeSelector at,
                                          final AttributeSelector list,
                                          final AttributeSelector contactID,
                                          final AttributeSelector contactName,
                                          final AttributeSelector standing,
                                          final AttributeSelector contactTypeID,
                                          final AttributeSelector inWatchlist,
                                          final AttributeSelector labelMask) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Contact>>() {
        @Override
        public List<Contact> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Contact c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addStringSelector(qs, "c", "list", list, p);
          AttributeSelector.addIntSelector(qs, "c", "contactID", contactID);
          AttributeSelector.addStringSelector(qs, "c", "contactName", contactName, p);
          AttributeSelector.addDoubleSelector(qs, "c", "standing", standing);
          AttributeSelector.addIntSelector(qs, "c", "contactTypeID", contactTypeID);
          AttributeSelector.addBooleanSelector(qs, "c", "inWatchlist", inWatchlist);
          AttributeSelector.addLongSelector(qs, "c", "labelMask", labelMask);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Contact> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Contact.class);
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
