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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_character_mail_message_body",
    indexes = {
        @Index(
            name = "messageIDIndex",
            columnList = "messageID",
            unique = false),
        @Index(
            name = "retrievedIndex",
            columnList = "retrieved",
            unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "CharacterMailMessageBody.getByMessageID",
        query = "SELECT c FROM CharacterMailMessageBody c where c.owner = :owner and c.messageID = :mid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterMailMessageBody.getUnretrievedMessageIDs",
        query = "SELECT c.messageID FROM CharacterMailMessageBody c where c.owner = :owner and c.retrieved = false and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class CharacterMailMessageBody extends CachedData {
  private static final Logger log  = Logger.getLogger(CharacterMailMessageBody.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL);
  // This is the message body part of a mail message. We store this separately to simplify the semantics
  // around updating mail messages.
  private long                messageID;
  private boolean             retrieved;
  @Lob
  @Column(
      length = 102400)
  private String              body;

  @SuppressWarnings("unused")
  private CharacterMailMessageBody() {}

  public CharacterMailMessageBody(long messageID, boolean retrieved, String body) {
    this.messageID = messageID;
    this.retrieved = retrieved;
    this.body = body;
    // Required per column definition above.
    if (this.body == null) this.body = "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterMailMessageBody)) return false;
    CharacterMailMessageBody other = (CharacterMailMessageBody) sup;
    return messageID == other.messageID && retrieved == other.retrieved && nullSafeObjectCompare(body, other.body);
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

  public String getBody() {
    return body;
  }

  public boolean isRetrieved() {
    return retrieved;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((body == null) ? 0 : body.hashCode());
    result = prime * result + (int) (messageID ^ (messageID >>> 32));
    result = prime * result + (retrieved ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterMailMessageBody other = (CharacterMailMessageBody) obj;
    if (body == null) {
      if (other.body != null) return false;
    } else if (!body.equals(other.body)) return false;
    if (messageID != other.messageID) return false;
    if (retrieved != other.retrieved) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterMailMessageBody [messageID=" + messageID + ", retrieved=" + retrieved + ", body=" + body + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterMailMessageBody get(
                                             final SynchronizedEveAccount owner,
                                             final long time,
                                             final long messageID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterMailMessageBody>() {
        @Override
        public CharacterMailMessageBody run() throws Exception {
          TypedQuery<CharacterMailMessageBody> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterMailMessageBody.getByMessageID", CharacterMailMessageBody.class);
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

  public static List<Long> getUnretrievedMessageIDs(
                                                    final SynchronizedEveAccount owner,
                                                    final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Long>>() {
        @Override
        public List<Long> run() throws Exception {
          TypedQuery<Long> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CharacterMailMessageBody.getUnretrievedMessageIDs", Long.class);
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

  public static List<CharacterMailMessageBody> accessQuery(
                                                           final SynchronizedEveAccount owner,
                                                           final long contid,
                                                           final int maxresults,
                                                           final AttributeSelector at,
                                                           final AttributeSelector messageID,
                                                           final AttributeSelector retrieved,
                                                           final AttributeSelector body) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterMailMessageBody>>() {
        @Override
        public List<CharacterMailMessageBody> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterMailMessageBody c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "messageID", messageID);
          AttributeSelector.addBooleanSelector(qs, "c", "retrieved", retrieved);
          AttributeSelector.addStringSelector(qs, "c", "body", body, p);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<CharacterMailMessageBody> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                             CharacterMailMessageBody.class);
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
