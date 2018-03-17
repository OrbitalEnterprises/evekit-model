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
    name = "evekit_data_character_notification",
    indexes = {
        @Index(
            name = "notificationIDIndex",
            columnList = "notificationID"),
        @Index(
            name = "sentDateIndex",
            columnList = "sentDate"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterNotification.getByNotificationID",
        query = "SELECT c FROM CharacterNotification c where c.owner = :owner and c.notificationID = :nid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterNotification extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterNotification.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_NOTIFICATIONS);

  private long notificationID;
  private String type;
  private int senderID;
  private String senderType;
  private long sentDate = -1;
  private boolean msgRead;
  @Lob
  @Column(
      length = 102400)
  private String text;

  @Transient
  @ApiModelProperty(
      value = "sentDate Date")
  @JsonProperty("sentDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date sentDateDate;

  @SuppressWarnings("unused")
  protected CharacterNotification() {}

  public CharacterNotification(long notificationID, String type, int senderID, String senderType, long sentDate,
                               boolean msgRead, String text) {
    this.notificationID = notificationID;
    this.type = type;
    this.senderID = senderID;
    this.senderType = senderType;
    this.sentDate = sentDate;
    this.msgRead = msgRead;
    this.text = text;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
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
    return notificationID == other.notificationID
        && nullSafeObjectCompare(type, other.type)
        && senderID == other.senderID
        && nullSafeObjectCompare(senderType, other.senderType)
        && sentDate == other.sentDate
        && msgRead == other.msgRead
        && nullSafeObjectCompare(text, other.text);
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

  public String getType() {
    return type;
  }

  public int getSenderID() {
    return senderID;
  }

  public String getSenderType() {
    return senderType;
  }

  public long getSentDate() {
    return sentDate;
  }

  public boolean isMsgRead() {
    return msgRead;
  }

  public String getText() {
    return text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterNotification that = (CharacterNotification) o;
    return notificationID == that.notificationID &&
        senderID == that.senderID &&
        sentDate == that.sentDate &&
        msgRead == that.msgRead &&
        Objects.equals(type, that.type) &&
        Objects.equals(senderType, that.senderType) &&
        Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), notificationID, type, senderID, senderType, sentDate, msgRead, text);
  }

  @Override
  public String toString() {
    return "CharacterNotification{" +
        "notificationID=" + notificationID +
        ", type='" + type + '\'' +
        ", senderID=" + senderID +
        ", senderType='" + senderType + '\'' +
        ", sentDate=" + sentDate +
        ", msgRead=" + msgRead +
        ", text='" + text + '\'' +
        ", sentDateDate=" + sentDateDate +
        '}';
  }

  public static CharacterNotification get(
      final SynchronizedEveAccount owner,
      final long time,
      final long notificationID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterNotification> getter = EveKitUserAccountProvider.getFactory()
                                                                                                            .getEntityManager()
                                                                                                            .createNamedQuery(
                                                                                                                "CharacterNotification.getByNotificationID",
                                                                                                                CharacterNotification.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("nid", notificationID);
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

  public static List<CharacterNotification> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector notificationID,
      final AttributeSelector type,
      final AttributeSelector senderID,
      final AttributeSelector senderType,
      final AttributeSelector sentDate,
      final AttributeSelector msgRead,
      final AttributeSelector text) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterNotification c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "notificationID", notificationID);
                                        AttributeSelector.addStringSelector(qs, "c", "type", type, p);
                                        AttributeSelector.addIntSelector(qs, "c", "senderID", senderID);
                                        AttributeSelector.addStringSelector(qs, "c", "senderType", senderType, p);
                                        AttributeSelector.addLongSelector(qs, "c", "sentDate", sentDate);
                                        AttributeSelector.addBooleanSelector(qs, "c", "msgRead", msgRead);
                                        AttributeSelector.addStringSelector(qs, "c", "text", text, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterNotification> query = EveKitUserAccountProvider.getFactory()
                                                                                                           .getEntityManager()
                                                                                                           .createQuery(
                                                                                                               qs.toString(),
                                                                                                               CharacterNotification.class);
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
