package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_contact",
    indexes = {
        @Index(
            name = "listIndex",
            columnList = "list"),
        @Index(
            name = "contactIDIndex",
            columnList = "contactID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Contact.getByContactID",
        query = "SELECT c FROM Contact c where c.owner = :owner and c.list = :list and c.contactID = :contact and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Contact extends CachedData {
  private static final Logger log = Logger.getLogger(Contact.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST);

  private String list;
  private int contactID;
  private float standing;
  private String contactType;
  private boolean inWatchlist;
  private boolean isBlocked;
  private long labelID;

  @SuppressWarnings("unused")
  protected Contact() {}

  public Contact(String list, int contactID, float standing, String contactType, boolean inWatchlist,
                 boolean isBlocked, long labelID) {
    this.list = list;
    this.contactID = contactID;
    this.standing = standing;
    this.contactType = contactType;
    this.inWatchlist = inWatchlist;
    this.isBlocked = isBlocked;
    this.labelID = labelID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof Contact)) return false;
    Contact other = (Contact) sup;
    return nullSafeObjectCompare(list, other.list) &&
        contactID == other.contactID &&
        Double.compare(standing, other.standing) == 0 &&
        nullSafeObjectCompare(contactType, other.contactType) &&
        inWatchlist == other.inWatchlist &&
        isBlocked == other.isBlocked &&
        labelID == other.labelID;
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

  public float getStanding() {
    return standing;
  }

  public String getContactType() {
    return contactType;
  }

  public boolean isInWatchlist() {
    return inWatchlist;
  }

  public boolean isBlocked() {
    return isBlocked;
  }

  public long getLabelID() {
    return labelID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Contact contact = (Contact) o;
    return contactID == contact.contactID &&
        Float.compare(contact.standing, standing) == 0 &&
        inWatchlist == contact.inWatchlist &&
        isBlocked == contact.isBlocked &&
        labelID == contact.labelID &&
        Objects.equals(list, contact.list) &&
        Objects.equals(contactType, contact.contactType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), list, contactID, standing, contactType, inWatchlist, isBlocked, labelID);
  }

  @Override
  public String toString() {
    return "Contact{" +
        "category='" + list + '\'' +
        ", contactID=" + contactID +
        ", standing=" + standing +
        ", contactType='" + contactType + '\'' +
        ", inWatchlist=" + inWatchlist +
        ", isBlocked=" + isBlocked +
        ", labelID=" + labelID +
        '}';
  }

  /**
   * Return contact with given properties, live at the given time. Returns null if no such contact exists.
   *
   * @param owner     contact owner
   * @param time      time at which the contact must be live
   * @param list      contact list
   * @param contactID contact ID
   * @return contact with the given properties live at the given time, or null.
   */
  public static Contact get(
      final SynchronizedEveAccount owner,
      final long time,
      final String list,
      final int contactID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Contact> getter = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createNamedQuery(
                                                                                                  "Contact.getByContactID",
                                                                                                  Contact.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("list", list);
                                        getter.setParameter("contact", contactID);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<Contact> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector list,
      final AttributeSelector contactID,
      final AttributeSelector standing,
      final AttributeSelector contactType,
      final AttributeSelector inWatchlist,
      final AttributeSelector isBlocked,
      final AttributeSelector labelID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
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
                                        AttributeSelector.addFloatSelector(qs, "c", "standing", standing);
                                        AttributeSelector.addStringSelector(qs, "c", "contactType", contactType, p);
                                        AttributeSelector.addBooleanSelector(qs, "c", "inWatchlist", inWatchlist);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isBlocked", isBlocked);
                                        AttributeSelector.addLongSelector(qs, "c", "labelID", labelID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Contact> query = EveKitUserAccountProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createQuery(qs.toString(),
                                                                                                          Contact.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
