package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_chatchannel_member",
    indexes = {
        @Index(
            name = "channelIDIndex",
            columnList = "channelID"),
        @Index(
            name = "categoryIndex",
            columnList = "category"),
        @Index(
            name = "accessorIDIndex",
            columnList = "accessorID")
    })
@NamedQueries({
    @NamedQuery(
        name = "ChatChannelMember.getByID",
        query = "SELECT c FROM ChatChannelMember c where c.owner = :owner and c.channelID = :channel and c.category = :category and c.accessorID = :accessor and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class ChatChannelMember extends CachedData {
  private static final Logger log = Logger.getLogger(ChatChannelMember.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS);

  public static final String CAT_ALLOWED = "allowed";
  public static final String CAT_BLOCKED = "blocked";
  public static final String CAT_MUTED = "muted";
  public static final String CAT_OPERATOR = "operator";

  // Channel this member is attached to
  private int channelID;
  // One of "allowed", "blocked", "muted", or "operators"
  private String category;
  // Member fields. Not all categories will populate all fields
  private int accessorID;
  private String accessorType;
  private long untilWhen = -1;
  private String reason;

  @Transient
  @ApiModelProperty(
      value = "untilWhen Date")
  @JsonProperty("untilWhenDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date untilWhenDate;

  @SuppressWarnings("unused")
  protected ChatChannelMember() {}

  public ChatChannelMember(int channelID, String category, int accessorID, String accessorType, long untilWhen,
                           String reason) {
    super();
    this.channelID = channelID;
    this.category = category;
    this.accessorID = accessorID;
    this.accessorType = accessorType;
    this.untilWhen = untilWhen;
    this.reason = reason;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    untilWhenDate = assignDateField(untilWhen);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof ChatChannelMember)) return false;
    ChatChannelMember other = (ChatChannelMember) sup;
    return channelID == other.channelID && nullSafeObjectCompare(category,
                                                                 other.category) && accessorID == other.accessorID
        && nullSafeObjectCompare(accessorType,
                                 other.accessorType) && untilWhen == other.untilWhen && nullSafeObjectCompare(reason,
                                                                                                              other.reason);
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

  public String getCategory() {
    return category;
  }

  public int getAccessorID() {
    return accessorID;
  }

  public String getAccessorType() {
    return accessorType;
  }

  public long getUntilWhen() {
    return untilWhen;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ChatChannelMember that = (ChatChannelMember) o;
    return channelID == that.channelID &&
        accessorID == that.accessorID &&
        untilWhen == that.untilWhen &&
        Objects.equals(category, that.category) &&
        Objects.equals(accessorType, that.accessorType) &&
        Objects.equals(reason, that.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), channelID, category, accessorID, accessorType, untilWhen, reason);
  }

  @Override
  public String toString() {
    return "ChatChannelMember{" +
        "channelID=" + channelID +
        ", category='" + category + '\'' +
        ", accessorID=" + accessorID +
        ", accessorType='" + accessorType + '\'' +
        ", untilWhen=" + untilWhen +
        ", reason='" + reason + '\'' +
        ", untilWhenDate=" + untilWhenDate +
        '}';
  }

  public static ChatChannelMember get(
      final SynchronizedEveAccount owner,
      final long time,
      final int channelID,
      final String category,
      final int accessorID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ChatChannelMember> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "ChatChannelMember.getByID",
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
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<ChatChannelMember> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector channelID,
      final AttributeSelector category,
      final AttributeSelector accessorID,
      final AttributeSelector accessorType,
      final AttributeSelector untilWhen,
      final AttributeSelector reason) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ChatChannelMember c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "channelID", channelID);
                                        AttributeSelector.addStringSelector(qs, "c", "category", category, p);
                                        AttributeSelector.addIntSelector(qs, "c", "accessorID", accessorID);
                                        AttributeSelector.addStringSelector(qs, "c", "accessorType", accessorType, p);
                                        AttributeSelector.addLongSelector(qs, "c", "untilWhen", untilWhen);
                                        AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ChatChannelMember> query = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(
                                                                                                           qs.toString(),
                                                                                                           ChatChannelMember.class);
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
