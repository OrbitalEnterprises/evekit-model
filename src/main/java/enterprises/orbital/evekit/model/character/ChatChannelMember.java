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
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_chatchannel_member",
    indexes = {
        @Index(
            name = "channelIDIndex",
            columnList = "channelID",
            unique = false),
        @Index(
            name = "categoryIndex",
            columnList = "category",
            unique = false),
        @Index(
            name = "accessorIDIndex",
            columnList = "accessorID",
            unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "ChatChannelMember.getByID",
        query = "SELECT c FROM ChatChannelMember c where c.owner = :owner and c.channelID = :channel and c.category = :category and c.accessorID = :accessor and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ChatChannelMember.list",
        query = "SELECT c FROM ChatChannelMember c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "ChatChannelMember.listByChannelID",
        query = "SELECT c FROM ChatChannelMember c where c.owner = :owner and c.channelID = :channel and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "ChatChannelMember.listByChannelAndCategory",
        query = "SELECT c FROM ChatChannelMember c where c.owner = :owner and c.channelID = :channel and c.category = :category and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class ChatChannelMember extends CachedData {
  private static final Logger log       = Logger.getLogger(ChatChannelMember.class.getName());
  private static final byte[] MASK      = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS);
  // Channel this member is attached to
  private long                channelID;
  // One of "allowed", "blocked", "muted", or "operators"
  private String              category;
  // Member fields. Not all categories will populate all fields
  private long                accessorID;
  private String              accessorName;
  private long                untilWhen = -1;
  private String              reason;

  @SuppressWarnings("unused")
  private ChatChannelMember() {}

  public ChatChannelMember(long channelID, String category, long accessorID, String accessorName, long untilWhen, String reason) {
    super();
    this.channelID = channelID;
    this.category = category;
    this.accessorID = accessorID;
    this.accessorName = accessorName;
    this.untilWhen = untilWhen;
    this.reason = reason;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof ChatChannelMember)) return false;
    ChatChannelMember other = (ChatChannelMember) sup;
    return channelID == other.channelID && nullSafeObjectCompare(category, other.category) && accessorID == other.accessorID
        && nullSafeObjectCompare(accessorName, other.accessorName) && untilWhen == other.untilWhen && nullSafeObjectCompare(reason, other.reason);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getChannelID() {
    return channelID;
  }

  public String getCategory() {
    return category;
  }

  public long getAccessorID() {
    return accessorID;
  }

  public String getAccessorName() {
    return accessorName;
  }

  public long getUntilWhen() {
    return untilWhen;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (accessorID ^ (accessorID >>> 32));
    result = prime * result + ((accessorName == null) ? 0 : accessorName.hashCode());
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result + (int) (channelID ^ (channelID >>> 32));
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + (int) (untilWhen ^ (untilWhen >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ChatChannelMember other = (ChatChannelMember) obj;
    if (accessorID != other.accessorID) return false;
    if (accessorName == null) {
      if (other.accessorName != null) return false;
    } else if (!accessorName.equals(other.accessorName)) return false;
    if (category == null) {
      if (other.category != null) return false;
    } else if (!category.equals(other.category)) return false;
    if (channelID != other.channelID) return false;
    if (reason == null) {
      if (other.reason != null) return false;
    } else if (!reason.equals(other.reason)) return false;
    if (untilWhen != other.untilWhen) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ChatChannelMember [channelID=" + channelID + ", category=" + category + ", accessorID=" + accessorID + ", accessorName=" + accessorName
        + ", untilWhen=" + untilWhen + ", reason=" + reason + "]";
  }

  public static ChatChannelMember get(
                                      final SynchronizedEveAccount owner,
                                      final long time,
                                      final long channelID,
                                      final String category,
                                      final long accessorID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ChatChannelMember>() {
        @Override
        public ChatChannelMember run() throws Exception {
          TypedQuery<ChatChannelMember> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ChatChannelMember.getByID",
                                                                                                                            ChatChannelMember.class);
          getter.setParameter("owner", owner);
          getter.setParameter("channel", channelID);
          getter.setParameter("category", category);
          getter.setParameter("accessor", accessorID);
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

  public static List<ChatChannelMember> getAllChatChannelMembers(
                                                                 final SynchronizedEveAccount owner,
                                                                 final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ChatChannelMember>>() {
        @Override
        public List<ChatChannelMember> run() throws Exception {
          TypedQuery<ChatChannelMember> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ChatChannelMember.list",
                                                                                                                            ChatChannelMember.class);
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

  public static List<ChatChannelMember> getByChannelID(
                                                       final SynchronizedEveAccount owner,
                                                       final long time,
                                                       final long channelID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ChatChannelMember>>() {
        @Override
        public List<ChatChannelMember> run() throws Exception {
          TypedQuery<ChatChannelMember> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ChatChannelMember.listByChannelID",
                                                                                                                            ChatChannelMember.class);
          getter.setParameter("owner", owner);
          getter.setParameter("channel", channelID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<ChatChannelMember> getByChannelIDAndCategory(
                                                                  final SynchronizedEveAccount owner,
                                                                  final long time,
                                                                  final long channelID,
                                                                  final String category) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ChatChannelMember>>() {
        @Override
        public List<ChatChannelMember> run() throws Exception {
          TypedQuery<ChatChannelMember> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("ChatChannelMember.listByChannelAndCategory", ChatChannelMember.class);
          getter.setParameter("owner", owner);
          getter.setParameter("channel", channelID);
          getter.setParameter("category", category);
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
