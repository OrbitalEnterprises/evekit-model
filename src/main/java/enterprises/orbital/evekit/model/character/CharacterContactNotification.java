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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_character_contact_notification", indexes = {
    @Index(name = "notificationIDIndex", columnList = "notificationID", unique = false), @Index(name = "sentDateIndex", columnList = "sentDate", unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "CharacterContactNotification.getByNotificationID",
        query = "SELECT c FROM CharacterContactNotification c where c.owner = :owner and c.notificationID = :nid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterContactNotification.getAll",
        query = "SELECT c FROM CharacterContactNotification c where c.owner = :owner and c.sentDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.sentDate asc"),
})
// 1 hour cache time - API caches for 30 minutes
public class CharacterContactNotification extends CachedData {
  private static final Logger log                 = Logger.getLogger(CharacterContactNotification.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                notificationID;
  private long                senderID;
  private String              senderName;
  private long                sentDate            = -1;
  private String              messageData;

  @SuppressWarnings("unused")
  private CharacterContactNotification() {}

  public CharacterContactNotification(long notificationID, long senderID, String senderName, long sentDate, String messageData) {
    this.notificationID = notificationID;
    this.senderID = senderID;
    this.senderName = senderName;
    this.sentDate = sentDate;
    this.messageData = messageData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof CharacterContactNotification)) return false;
    CharacterContactNotification other = (CharacterContactNotification) sup;
    return notificationID == other.notificationID && senderID == other.senderID && nullSafeObjectCompare(senderName, other.senderName)
        && sentDate == other.sentDate && nullSafeObjectCompare(messageData, other.messageData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getNotificationID() {
    return notificationID;
  }

  public long getSenderID() {
    return senderID;
  }

  public String getSenderName() {
    return senderName;
  }

  public long getSentDate() {
    return sentDate;
  }

  public String getMessageData() {
    return messageData;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((messageData == null) ? 0 : messageData.hashCode());
    result = prime * result + (int) (notificationID ^ (notificationID >>> 32));
    result = prime * result + (int) (senderID ^ (senderID >>> 32));
    result = prime * result + ((senderName == null) ? 0 : senderName.hashCode());
    result = prime * result + (int) (sentDate ^ (sentDate >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterContactNotification other = (CharacterContactNotification) obj;
    if (messageData == null) {
      if (other.messageData != null) return false;
    } else if (!messageData.equals(other.messageData)) return false;
    if (notificationID != other.notificationID) return false;
    if (senderID != other.senderID) return false;
    if (senderName == null) {
      if (other.senderName != null) return false;
    } else if (!senderName.equals(other.senderName)) return false;
    if (sentDate != other.sentDate) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterContactNotification [notificationID=" + notificationID + ", senderID=" + senderID + ", senderName=" + senderName + ", sentDate=" + sentDate
        + ", messageData=" + messageData + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve contact notification with the given ID, live at the given time, or null if no such notification exists.
   * 
   * @param owner
   *          notification owner
   * @param time
   *          time at which notification must be live
   * @param notificationID
   *          notification ID
   * @return contact notification with the given ID live at the given time, or null
   */
  public static CharacterContactNotification get(final SynchronizedEveAccount owner, final long time, final long notificationID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterContactNotification>() {
        @Override
        public CharacterContactNotification run() throws Exception {
          TypedQuery<CharacterContactNotification> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterContactNotification.getByNotificationID", CharacterContactNotification.class);
          getter.setParameter("owner", owner);
          getter.setParameter("nid", notificationID);
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
   * Retrieve list of contact notifications live at the given time with sentDate after "contid"
   * 
   * @param owner
   *          notifications owner
   * @param time
   *          time at which notifications must be live
   * @param maxresults
   *          maximum number of notifications to retrieve
   * @param contid
   *          sentDate (exclusive) after which notifications will be retrieved
   * @return list of contact notifications live at the given time with sentDate after "contid"
   */
  public static List<CharacterContactNotification> getAllNotifications(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(CharacterContactNotification.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterContactNotification>>() {
        @Override
        public List<CharacterContactNotification> run() throws Exception {
          TypedQuery<CharacterContactNotification> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterContactNotification.getAll", CharacterContactNotification.class);
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

}
