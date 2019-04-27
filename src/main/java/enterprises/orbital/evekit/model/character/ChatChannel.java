package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_chatchannel",
    indexes = {
        @Index(
            name = "channelIDIndex",
            columnList = "channelID")
    })
@NamedQueries({
    @NamedQuery(
        name = "ChatChannel.getByChannelID",
        query = "SELECT c FROM ChatChannel c where c.owner = :owner and c.channelID = :channel and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class ChatChannel extends CachedData {
  private static final Logger log = Logger.getLogger(ChatChannel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS);

  private int channelID;
  private int ownerID;
  private String displayName;
  private String comparisonKey;
  private boolean hasPassword;
  @Lob
  @Column(
      length = 102400)
  private String motd;

  @SuppressWarnings("unused")
  protected ChatChannel() {}

  public ChatChannel(int channelID, int ownerID, String displayName, String comparisonKey, boolean hasPassword,
                     String motd) {
    super();
    this.channelID = channelID;
    this.ownerID = ownerID;
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
    return channelID == other.channelID && ownerID == other.ownerID
        && nullSafeObjectCompare(displayName, other.displayName) && nullSafeObjectCompare(comparisonKey,
                                                                                          other.comparisonKey)
        && hasPassword == other.hasPassword && nullSafeObjectCompare(motd, other.motd);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(channelID, ownerID, displayName, comparisonKey, hasPassword, motd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getChannelID() {
    return channelID;
  }

  public int getOwnerID() {
    return ownerID;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ChatChannel that = (ChatChannel) o;
    return channelID == that.channelID &&
        ownerID == that.ownerID &&
        hasPassword == that.hasPassword &&
        Objects.equals(displayName, that.displayName) &&
        Objects.equals(comparisonKey, that.comparisonKey) &&
        Objects.equals(motd, that.motd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), channelID, ownerID, displayName, comparisonKey, hasPassword, motd);
  }

  @Override
  public String toString() {
    return "ChatChannel{" +
        "channelID=" + channelID +
        ", ownerID=" + ownerID +
        ", displayName='" + displayName + '\'' +
        ", comparisonKey='" + comparisonKey + '\'' +
        ", hasPassword=" + hasPassword +
        ", motd='" + motd + '\'' +
        '}';
  }

  public static ChatChannel get(
      final SynchronizedEveAccount owner,
      final long time,
      final int channelID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ChatChannel> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "ChatChannel.getByChannelID",
                                                                                                      ChatChannel.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("channel", channelID);
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

  public static List<ChatChannel> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector channelID,
      final AttributeSelector ownerID,
      final AttributeSelector displayName,
      final AttributeSelector comparisonKey,
      final AttributeSelector hasPassword,
      final AttributeSelector motd) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ChatChannel c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "channelID", channelID);
                                        AttributeSelector.addIntSelector(qs, "c", "ownerID", ownerID);
                                        AttributeSelector.addStringSelector(qs, "c", "displayName", displayName, p);
                                        AttributeSelector.addStringSelector(qs, "c", "comparisonKey", comparisonKey, p);
                                        AttributeSelector.addBooleanSelector(qs, "c", "hasPassword", hasPassword);
                                        AttributeSelector.addStringSelector(qs, "c", "motd", motd, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ChatChannel> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     ChatChannel.class);
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
