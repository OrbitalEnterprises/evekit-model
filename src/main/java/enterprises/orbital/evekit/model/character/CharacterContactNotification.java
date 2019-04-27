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
    name = "evekit_data_character_contact_notification",
    indexes = {
        @Index(
            name = "notificationIDIndex",
            columnList = "notificationID"),
        @Index(
            name = "sentDateIndex",
            columnList = "sentDate")
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterContactNotification.getByNotificationID",
        query = "SELECT c FROM CharacterContactNotification c where c.owner = :owner and c.notificationID = :nid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 1 hour cache time - API caches for 30 minutes
public class CharacterContactNotification extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterContactNotification.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS);

  private int notificationID;
  private int senderID;
  private long sentDate = -1;
  private float standingLevel;
  private String messageData;
  @Transient
  @ApiModelProperty(
      value = "sentDate Date")
  @JsonProperty("sentDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date sentDateDate;

  @SuppressWarnings("unused")
  protected CharacterContactNotification() {}

  public CharacterContactNotification(int notificationID, int senderID, long sentDate, float standingLevel,
                                      String messageData) {
    this.notificationID = notificationID;
    this.senderID = senderID;
    this.sentDate = sentDate;
    this.standingLevel = standingLevel;
    this.messageData = messageData;
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
    if (!(sup instanceof CharacterContactNotification)) return false;
    CharacterContactNotification other = (CharacterContactNotification) sup;
    return notificationID == other.notificationID && senderID == other.senderID
        && sentDate == other.sentDate
        && floatCompare(standingLevel, other.standingLevel, 0.00001F)
        && nullSafeObjectCompare(messageData, other.messageData);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(notificationID, senderID, sentDate, standingLevel, messageData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getNotificationID() {
    return notificationID;
  }

  public int getSenderID() {
    return senderID;
  }

  public long getSentDate() {
    return sentDate;
  }

  public float getStandingLevel() {
    return standingLevel;
  }

  public String getMessageData() {
    return messageData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterContactNotification that = (CharacterContactNotification) o;
    return notificationID == that.notificationID &&
        senderID == that.senderID &&
        sentDate == that.sentDate &&
        Float.compare(that.standingLevel, standingLevel) == 0 &&
        Objects.equals(messageData, that.messageData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), notificationID, senderID, sentDate, standingLevel, messageData);
  }

  @Override
  public String toString() {
    return "CharacterContactNotification{" +
        "notificationID=" + notificationID +
        ", senderID=" + senderID +
        ", sentDate=" + sentDate +
        ", standingLevel=" + standingLevel +
        ", messageData='" + messageData + '\'' +
        ", sentDateDate=" + sentDateDate +
        '}';
  }

  /**
   * Retrieve contact notification with the given ID, live at the given time, or null if no such notification exists.
   *
   * @param owner          notification owner
   * @param time           time at which notification must be live
   * @param notificationID notification ID
   * @return contact notification with the given ID live at the given time, or null
   */
  public static CharacterContactNotification get(
      final SynchronizedEveAccount owner,
      final long time,
      final int notificationID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterContactNotification> getter = EveKitUserAccountProvider.getFactory()
                                                                                                                   .getEntityManager()
                                                                                                                   .createNamedQuery(
                                                                                                                       "CharacterContactNotification.getByNotificationID",
                                                                                                                       CharacterContactNotification.class);
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

  public static List<CharacterContactNotification> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector notificationID,
      final AttributeSelector senderID,
      final AttributeSelector sentDate,
      final AttributeSelector standingLevel,
      final AttributeSelector messageData) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterContactNotification c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "notificationID", notificationID);
                                        AttributeSelector.addIntSelector(qs, "c", "senderID", senderID);
                                        AttributeSelector.addLongSelector(qs, "c", "sentDate", sentDate);
                                        AttributeSelector.addFloatSelector(qs, "c", "standingLevel", standingLevel);
                                        AttributeSelector.addStringSelector(qs, "c", "messageData", messageData, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterContactNotification> query = EveKitUserAccountProvider.getFactory()
                                                                                                                  .getEntityManager()
                                                                                                                  .createQuery(
                                                                                                                      qs.toString(),
                                                                                                                      CharacterContactNotification.class);
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
