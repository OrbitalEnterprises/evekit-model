package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    name = "evekit_data_character_mail_message",
    indexes = {
        @Index(
            name = "messageIDIndex",
            columnList = "messageID",
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
        name = "CharacterMailMessage.getByMessageID",
        query = "SELECT c FROM CharacterMailMessage c where c.owner = :owner and c.messageID = :mid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterMailMessage.getMessageIDList",
        query = "SELECT c.messageID FROM CharacterMailMessage c where c.owner = :owner and c.sentDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.sentDate asc"),
    @NamedQuery(
        name = "CharacterMailMessage.getMessageIDListUnreadOnly",
        query = "SELECT c.messageID FROM CharacterMailMessage c where c.owner = :owner and c.msgRead = false and c.sentDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.sentDate asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class CharacterMailMessage extends CachedData {
  private static final Logger log                 = Logger.getLogger(CharacterMailMessage.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                messageID;
  private long                senderID;
  private String              senderName;
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           toCharacterID       = new HashSet<Long>();
  private long                sentDate            = -1;
  private String              title;
  private long                toCorpOrAllianceID;
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           toListID            = new HashSet<Long>();
  private boolean             msgRead;
  private int                 senderTypeID;

  @SuppressWarnings("unused")
  private CharacterMailMessage() {}

  public CharacterMailMessage(long messageID, long senderID, String senderName, long sentDate, String title, long toCorpOrAllianceID, boolean msgRead,
                              int senderTypeID) {
    super();
    this.messageID = messageID;
    this.senderID = senderID;
    this.senderName = senderName;
    this.sentDate = sentDate;
    this.title = title;
    this.toCorpOrAllianceID = toCorpOrAllianceID;
    this.msgRead = msgRead;
    this.senderTypeID = senderTypeID;
    this.toCharacterID = new HashSet<Long>();
    this.toListID = new HashSet<Long>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterMailMessage)) return false;
    CharacterMailMessage other = (CharacterMailMessage) sup;
    return messageID == other.messageID && senderID == other.senderID && nullSafeObjectCompare(senderName, other.senderName)
        && nullSafeObjectCompare(toCharacterID, other.toCharacterID) && sentDate == other.sentDate && nullSafeObjectCompare(title, other.title)
        && toCorpOrAllianceID == other.toCorpOrAllianceID && nullSafeObjectCompare(toListID, other.toListID) && msgRead == other.msgRead
        && senderTypeID == other.senderTypeID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getMessageID() {
    return messageID;
  }

  public long getSenderID() {
    return senderID;
  }

  public String getSenderName() {
    return senderName;
  }

  public Set<Long> getToCharacterID() {
    return toCharacterID;
  }

  public long getSentDate() {
    return sentDate;
  }

  public String getTitle() {
    return title;
  }

  public long getToCorpOrAllianceID() {
    return toCorpOrAllianceID;
  }

  public Set<Long> getToListID() {
    return toListID;
  }

  public boolean isMsgRead() {
    return msgRead;
  }

  public int getSenderTypeID() {
    return senderTypeID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (toCorpOrAllianceID ^ (toCorpOrAllianceID >>> 32));
    result = prime * result + (int) (messageID ^ (messageID >>> 32));
    result = prime * result + (msgRead ? 1231 : 1237);
    result = prime * result + (int) (senderID ^ (senderID >>> 32));
    result = prime * result + ((senderName == null) ? 0 : senderName.hashCode());
    result = prime * result + senderTypeID;
    result = prime * result + (int) (sentDate ^ (sentDate >>> 32));
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((toCharacterID == null) ? 0 : toCharacterID.hashCode());
    result = prime * result + ((toListID == null) ? 0 : toListID.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterMailMessage other = (CharacterMailMessage) obj;
    if (toCorpOrAllianceID != other.toCorpOrAllianceID) return false;
    if (messageID != other.messageID) return false;
    if (msgRead != other.msgRead) return false;
    if (senderID != other.senderID) return false;
    if (senderName == null) {
      if (other.senderName != null) return false;
    } else if (!senderName.equals(other.senderName)) return false;
    if (senderTypeID != other.senderTypeID) return false;
    if (sentDate != other.sentDate) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    if (toCharacterID == null) {
      if (other.toCharacterID != null) return false;
    } else if (!toCharacterID.equals(other.toCharacterID)) return false;
    if (toListID == null) {
      if (other.toListID != null) return false;
    } else if (!toListID.equals(other.toListID)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterMailMessage [messageID=" + messageID + ", senderID=" + senderID + ", senderName=" + senderName + ", toCharacterID=" + toCharacterID
        + ", sentDate=" + sentDate + ", title=" + title + ", toCorpOrAllianceID=" + toCorpOrAllianceID + ", toListID=" + toListID + ", msgRead=" + msgRead
        + ", senderTypeID=" + senderTypeID + "]";
  }

  public static CharacterMailMessage get(
                                         final SynchronizedEveAccount owner,
                                         final long time,
                                         final long messageID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterMailMessage>() {
        @Override
        public CharacterMailMessage run() throws Exception {
          TypedQuery<CharacterMailMessage> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterMailMessage.getByMessageID", CharacterMailMessage.class);
          getter.setParameter("owner", owner);
          getter.setParameter("mid", messageID);
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

  public static List<Long> getMessageIDs(
                                         final SynchronizedEveAccount owner,
                                         final long time,
                                         final boolean unreadonly,
                                         int maxresults,
                                         final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(CharacterMailMessage.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Long>>() {
        @Override
        public List<Long> run() throws Exception {
          TypedQuery<Long> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery(unreadonly ? "CharacterMailMessage.getMessageIDListUnreadOnly" : "CharacterMailMessage.getMessageIDList", Long.class);
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

  public static List<CharacterMailMessage> accessQuery(
                                                       final SynchronizedEveAccount owner,
                                                       final long contid,
                                                       final int maxresults,
                                                       final boolean reverse,
                                                       final AttributeSelector at,
                                                       final AttributeSelector messageID,
                                                       final AttributeSelector senderID,
                                                       final AttributeSelector senderName,
                                                       final AttributeSelector toCharacterID,
                                                       final AttributeSelector sentDate,
                                                       final AttributeSelector title,
                                                       final AttributeSelector toCorpOrAllianceID,
                                                       final AttributeSelector toListID,
                                                       final AttributeSelector msgRead,
                                                       final AttributeSelector senderTypeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterMailMessage>>() {
        @Override
        public List<CharacterMailMessage> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterMailMessage c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "messageID", messageID);
          AttributeSelector.addLongSelector(qs, "c", "senderID", senderID);
          AttributeSelector.addStringSelector(qs, "c", "senderName", senderName, p);
          AttributeSelector.addSetLongSelector(qs, "c", "toCharacterID", toCharacterID);
          AttributeSelector.addLongSelector(qs, "c", "sentDate", sentDate);
          AttributeSelector.addStringSelector(qs, "c", "title", title, p);
          AttributeSelector.addLongSelector(qs, "c", "toCorpOrAllianceID", toCorpOrAllianceID);
          AttributeSelector.addSetLongSelector(qs, "c", "toListID", toListID);
          AttributeSelector.addBooleanSelector(qs, "c", "msgRead", msgRead);
          AttributeSelector.addIntSelector(qs, "c", "senderTypeID", senderTypeID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterMailMessage> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                         CharacterMailMessage.class);
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
