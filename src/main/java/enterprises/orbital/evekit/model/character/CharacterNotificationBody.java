package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
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
@Table(
    name = "evekit_data_character_notification_body",
    indexes = {
        @Index(
            name = "notificationIDIndex",
            columnList = "notificationID",
            unique = false),
        @Index(
            name = "retrievedIndex",
            columnList = "retrieved",
            unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "CharacterNotificationBody.getByNotificationID",
        query = "SELECT c FROM CharacterNotificationBody c where c.owner = :owner and c.notificationID = :nid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterNotificationBody.getUnretrieved",
        query = "SELECT c.notificationID FROM CharacterNotificationBody c where c.owner = :owner and c.retrieved = false and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
public class CharacterNotificationBody extends CachedData {
  private static final Logger log  = Logger.getLogger(CharacterNotificationBody.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_NOTIFICATIONS);
  // This is the body part of a notification message. We store this separately to simplify the semantics
  // around updating notifications.
  private long                notificationID;
  private boolean             retrieved;
  @Lob
  @Column(
      length = 102400)
  private String              text;
  private boolean             missing;

  @SuppressWarnings("unused")
  private CharacterNotificationBody() {}

  public CharacterNotificationBody(long notificationID, boolean retrieved, String text, boolean missing) {
    super();
    this.notificationID = notificationID;
    this.retrieved = retrieved;
    this.text = text;
    this.missing = missing;
    // Required per column definition above
    if (this.text == null) this.text = "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterNotificationBody)) return false;
    CharacterNotificationBody other = (CharacterNotificationBody) sup;
    return notificationID == other.notificationID && retrieved == other.retrieved && nullSafeObjectCompare(text, other.text) && missing == other.missing;
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

  public boolean isRetrieved() {
    return retrieved;
  }

  public String getText() {
    return text;
  }

  public boolean isMissing() {
    return missing;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (missing ? 1231 : 1237);
    result = prime * result + (int) (notificationID ^ (notificationID >>> 32));
    result = prime * result + (retrieved ? 1231 : 1237);
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterNotificationBody other = (CharacterNotificationBody) obj;
    if (missing != other.missing) return false;
    if (notificationID != other.notificationID) return false;
    if (retrieved != other.retrieved) return false;
    if (text == null) {
      if (other.text != null) return false;
    } else if (!text.equals(other.text)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterNotificationBody [notificationID=" + notificationID + ", retrieved=" + retrieved + ", text=" + text + ", missing=" + missing + ", owner="
        + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterNotificationBody get(
                                              final SynchronizedEveAccount owner,
                                              final long time,
                                              final long notificationID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterNotificationBody>() {
        @Override
        public CharacterNotificationBody run() throws Exception {
          TypedQuery<CharacterNotificationBody> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterNotificationBody.getByNotificationID", CharacterNotificationBody.class);
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

  public static List<Long> getUnretrievedNotificationIDs(
                                                         final SynchronizedEveAccount owner,
                                                         final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Long>>() {
        @Override
        public List<Long> run() throws Exception {
          TypedQuery<Long> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterNotificationBody.getUnretrieved",
                                                                                                               Long.class);
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

}
