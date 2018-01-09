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
    name = "evekit_data_chatchannel",
    indexes = {
        @Index(
            name = "channelIDIndex",
            columnList = "channelID",
            unique = false)
    })
@NamedQueries({
    @NamedQuery(
        name = "ChatChannel.getByChannelID",
        query = "SELECT c FROM ChatChannel c where c.owner = :owner and c.channelID = :channel and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ChatChannel.list",
        query = "SELECT c FROM ChatChannel c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class ChatChannel extends CachedData {
  private static final Logger log  = Logger.getLogger(ChatChannel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS);
  private long                channelID;
  private long                ownerID;
  private String              ownerName;
  private String              displayName;
  private String              comparisonKey;
  private boolean             hasPassword;
  @Lob
  @Column(
      length = 102400)
  private String              motd;

  @SuppressWarnings("unused")
  protected ChatChannel() {}

  public ChatChannel(long channelID, long ownerID, String ownerName, String displayName, String comparisonKey, boolean hasPassword, String motd) {
    super();
    this.channelID = channelID;
    this.ownerID = ownerID;
    this.ownerName = ownerName;
    this.displayName = displayName;
    this.comparisonKey = comparisonKey;
    this.hasPassword = hasPassword;
    this.motd = motd;
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
    if (!(sup instanceof ChatChannel)) return false;
    ChatChannel other = (ChatChannel) sup;
    return channelID == other.channelID && ownerID == other.ownerID && nullSafeObjectCompare(ownerName, other.ownerName)
        && nullSafeObjectCompare(displayName, other.displayName) && nullSafeObjectCompare(comparisonKey, other.comparisonKey)
        && hasPassword == other.hasPassword && nullSafeObjectCompare(motd, other.motd);
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

  public long getOwnerID() {
    return ownerID;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getComparisonKey() {
    return comparisonKey;
  }

  public boolean isHasPassword() {
    return hasPassword;
  }

  public String getMotd() {
    return motd;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (channelID ^ (channelID >>> 32));
    result = prime * result + ((comparisonKey == null) ? 0 : comparisonKey.hashCode());
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + (hasPassword ? 1231 : 1237);
    result = prime * result + ((motd == null) ? 0 : motd.hashCode());
    result = prime * result + (int) (ownerID ^ (ownerID >>> 32));
    result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ChatChannel other = (ChatChannel) obj;
    if (channelID != other.channelID) return false;
    if (comparisonKey == null) {
      if (other.comparisonKey != null) return false;
    } else if (!comparisonKey.equals(other.comparisonKey)) return false;
    if (displayName == null) {
      if (other.displayName != null) return false;
    } else if (!displayName.equals(other.displayName)) return false;
    if (hasPassword != other.hasPassword) return false;
    if (motd == null) {
      if (other.motd != null) return false;
    } else if (!motd.equals(other.motd)) return false;
    if (ownerID != other.ownerID) return false;
    if (ownerName == null) {
      if (other.ownerName != null) return false;
    } else if (!ownerName.equals(other.ownerName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ChatChannel [channelID=" + channelID + ", ownerID=" + ownerID + ", ownerName=" + ownerName + ", displayName=" + displayName + ", comparisonKey="
        + comparisonKey + ", hasPassword=" + hasPassword + ", motd=" + motd + "]";
  }

  public static ChatChannel get(
                                final SynchronizedEveAccount owner,
                                final long time,
                                final long channelID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ChatChannel>() {
        @Override
        public ChatChannel run() throws Exception {
          TypedQuery<ChatChannel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ChatChannel.getByChannelID",
                                                                                                                      ChatChannel.class);
          getter.setParameter("owner", owner);
          getter.setParameter("channel", channelID);
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

  public static List<ChatChannel> getAllChatChannels(
                                                     final SynchronizedEveAccount owner,
                                                     final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ChatChannel>>() {
        @Override
        public List<ChatChannel> run() throws Exception {
          TypedQuery<ChatChannel> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ChatChannel.list", ChatChannel.class);
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

  public static List<ChatChannel> accessQuery(
                                              final SynchronizedEveAccount owner,
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector channelID,
                                              final AttributeSelector ownerID,
                                              final AttributeSelector ownerName,
                                              final AttributeSelector displayName,
                                              final AttributeSelector comparisonKey,
                                              final AttributeSelector hasPassword,
                                              final AttributeSelector motd) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ChatChannel>>() {
        @Override
        public List<ChatChannel> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ChatChannel c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "channelID", channelID);
          AttributeSelector.addLongSelector(qs, "c", "ownerID", ownerID);
          AttributeSelector.addStringSelector(qs, "c", "ownerName", ownerName, p);
          AttributeSelector.addStringSelector(qs, "c", "displayName", displayName, p);
          AttributeSelector.addStringSelector(qs, "c", "comparisonKey", comparisonKey, p);
          AttributeSelector.addBooleanSelector(qs, "c", "hasPassword", hasPassword);
          AttributeSelector.addStringSelector(qs, "c", "motd", motd, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<ChatChannel> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), ChatChannel.class);
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
