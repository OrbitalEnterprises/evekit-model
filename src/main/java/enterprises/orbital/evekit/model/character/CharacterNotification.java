package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_character_notification",
    indexes = {
        @Index(
            name = "notificationIDIndex",
            columnList = "notificationID",
            unique = false),
        @Index(
            name = "sentDateIndex",
            columnList = "sentDate",
            unique = false),
        @Index(
            name = "msgReadIndex",
            columnList = "msgRead",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterNotification.getByNotificationID",
        query = "SELECT c FROM CharacterNotification c where c.owner = :owner and c.notificationID = :nid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterNotification.getAllIDs",
        query = "SELECT c.notificationID FROM CharacterNotification c where c.owner = :owner and c.sentDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.sentDate asc"),
    @NamedQuery(
        name = "CharacterNotification.getAllIDsUnread",
        query = "SELECT c.notificationID FROM CharacterNotification c where c.owner = :owner and c.msgRead = false and c.sentDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.sentDate asc"),
})
public class CharacterNotification extends CachedData {
  private static final Logger log                 = Logger.getLogger(CharacterNotification.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_NOTIFICATIONS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                notificationID;
  private int                 typeID;
  private long                senderID;
  private long                sentDate            = -1;
  private boolean             msgRead;
  @Transient
  @ApiModelProperty(
      value = "sentDate Date")
  @JsonProperty("sentDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                sentDateDate;

  @SuppressWarnings("unused")
  private CharacterNotification() {}

  public CharacterNotification(long notificationID, int typeID, long senderID, long sentDate, boolean msgRead) {
    super();
    this.notificationID = notificationID;
    this.typeID = typeID;
    this.senderID = senderID;
    this.sentDate = sentDate;
    this.msgRead = msgRead;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    sentDateDate = assignDateField(sentDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterNotification)) return false;
    CharacterNotification other = (CharacterNotification) sup;
    return notificationID == other.notificationID && typeID == other.typeID && senderID == other.senderID && sentDate == other.sentDate
        && msgRead == other.msgRead;
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

  public int getTypeID() {
    return typeID;
  }

  public long getSenderID() {
    return senderID;
  }

  public long getSentDate() {
    return sentDate;
  }

  public boolean isMsgRead() {
    return msgRead;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (msgRead ? 1231 : 1237);
    result = prime * result + (int) (notificationID ^ (notificationID >>> 32));
    result = prime * result + (int) (senderID ^ (senderID >>> 32));
    result = prime * result + (int) (sentDate ^ (sentDate >>> 32));
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterNotification other = (CharacterNotification) obj;
    if (msgRead != other.msgRead) return false;
    if (notificationID != other.notificationID) return false;
    if (senderID != other.senderID) return false;
    if (sentDate != other.sentDate) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterNotification [notificationID=" + notificationID + ", typeID=" + typeID + ", senderID=" + senderID + ", sentDate=" + sentDate + ", msgRead="
        + msgRead + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterNotification get(
                                          final SynchronizedEveAccount owner,
                                          final long time,
                                          final long notificationID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterNotification>() {
        @Override
        public CharacterNotification run() throws Exception {
          TypedQuery<CharacterNotification> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterNotification.getByNotificationID", CharacterNotification.class);
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

  public static List<Long> getNotificationIDs(
                                              final SynchronizedEveAccount owner,
                                              final long time,
                                              final boolean unreadonly,
                                              int maxresults,
                                              final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(CharacterNotification.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Long>>() {
        @Override
        public List<Long> run() throws Exception {
          TypedQuery<Long> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery(unreadonly ? "CharacterNotification.getAllIDsUnread" : "CharacterNotification.getAllIDs", Long.class);
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

  public static List<CharacterNotification> accessQuery(
                                                        final SynchronizedEveAccount owner,
                                                        final long contid,
                                                        final int maxresults,
                                                        final boolean reverse,
                                                        final AttributeSelector at,
                                                        final AttributeSelector notificationID,
                                                        final AttributeSelector typeID,
                                                        final AttributeSelector senderID,
                                                        final AttributeSelector sentDate,
                                                        final AttributeSelector msgRead) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterNotification>>() {
        @Override
        public List<CharacterNotification> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterNotification c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "notificationID", notificationID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addLongSelector(qs, "c", "senderID", senderID);
          AttributeSelector.addLongSelector(qs, "c", "sentDate", sentDate);
          AttributeSelector.addBooleanSelector(qs, "c", "msgRead", msgRead);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterNotification> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                          CharacterNotification.class);
          query.setParameter("owner", owner);
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
