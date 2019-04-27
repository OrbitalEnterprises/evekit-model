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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Entity
@Table(
    name = "evekit_data_character_mail_message",
    indexes = {
        @Index(
            name = "messageIDIndex",
            columnList = "messageID"),
        @Index(
            name = "sentDateIndex",
            columnList = "sentDate"),
        @Index(
            name = "msgReadIndex",
            columnList = "msgRead"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterMailMessage.getByMessageID",
        query = "SELECT c FROM CharacterMailMessage c where c.owner = :owner and c.messageID = :mid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterMailMessage extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterMailMessage.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL);

  private long messageID;
  private int senderID;
  private long sentDate = -1;
  private String title;
  private boolean msgRead;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "mail_message_label", joinColumns = @JoinColumn(name = "mail_cid"))
  @Column(name = "labelID")
  private Set<Integer> labels = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "mail_message_recipient", joinColumns = @JoinColumn(name = "mail_cid"))
  private Set<MailMessageRecipient> recipients = new HashSet<>();

  @Lob
  @Column(
      length = 102400)
  private String body;

  @Transient
  @ApiModelProperty(
      value = "sentDate Date")
  @JsonProperty("sentDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date sentDateDate;

  @SuppressWarnings("unused")
  protected CharacterMailMessage() {}

  public CharacterMailMessage(long messageID, int senderID, long sentDate, String title, boolean msgRead,
                              Set<Integer> labels, Set<MailMessageRecipient> recipients, String body) {
    this.messageID = messageID;
    this.senderID = senderID;
    this.sentDate = sentDate;
    this.title = title;
    this.msgRead = msgRead;
    this.labels = labels;
    this.recipients = recipients;
    this.body = body;
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
    if (!(sup instanceof CharacterMailMessage)) return false;
    CharacterMailMessage other = (CharacterMailMessage) sup;
    return messageID == other.messageID && senderID == other.senderID && nullSafeObjectCompare(labels, other.labels)
        && sentDate == other.sentDate && nullSafeObjectCompare(title, other.title)
        && nullSafeObjectCompare(recipients, other.recipients) && msgRead == other.msgRead
        && nullSafeObjectCompare(body, other.body);
  }

  @Override
  public String dataHash() {
    // sort labels and recipients for consistent hashing
    List<Integer> sortLabels = new ArrayList<>(labels);
    sortLabels.sort(Comparator.comparingInt(Integer::intValue));
    List<MailMessageRecipient> sortRecipients = new ArrayList<>(recipients);
    sortRecipients.sort(Comparator.comparingInt(MailMessageRecipient::getRecipientID));
    return dataHashHelper(messageID, senderID, sentDate, title, msgRead, body,
                          dataHashHelper(sortLabels.toArray()),
                          dataHashHelper(sortRecipients.stream()
                                                       .map(x -> x.getRecipientType() + x.getRecipientID())
                                                       .toArray()));
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

  public int getSenderID() {
    return senderID;
  }

  public long getSentDate() {
    return sentDate;
  }

  public String getTitle() {
    return title;
  }

  public boolean isMsgRead() {
    return msgRead;
  }

  public Set<Integer> getLabels() {
    return labels;
  }

  public Set<MailMessageRecipient> getRecipients() {
    return recipients;
  }

  public String getBody() {
    return body;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterMailMessage that = (CharacterMailMessage) o;
    return messageID == that.messageID &&
        senderID == that.senderID &&
        sentDate == that.sentDate &&
        msgRead == that.msgRead &&
        Objects.equals(title, that.title) &&
        Objects.equals(labels, that.labels) &&
        Objects.equals(recipients, that.recipients) &&
        Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), messageID, senderID, sentDate, title, msgRead, labels, recipients, body);
  }

  @Override
  public String toString() {
    return "CharacterMailMessage{" +
        "messageID=" + messageID +
        ", senderID=" + senderID +
        ", sentDate=" + sentDate +
        ", title='" + title + '\'' +
        ", msgRead=" + msgRead +
        ", labels=" + labels +
        ", recipients=" + recipients +
        ", body='" + body + '\'' +
        ", sentDateDate=" + sentDateDate +
        '}';
  }

  public static CharacterMailMessage get(
      final SynchronizedEveAccount owner,
      final long time,
      final long messageID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterMailMessage> getter = EveKitUserAccountProvider.getFactory()
                                                                                                           .getEntityManager()
                                                                                                           .createNamedQuery(
                                                                                                               "CharacterMailMessage.getByMessageID",
                                                                                                               CharacterMailMessage.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("mid", messageID);
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

  public static List<CharacterMailMessage> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector messageID,
      final AttributeSelector senderID,
      final AttributeSelector sentDate,
      final AttributeSelector title,
      final AttributeSelector msgRead,
      final AttributeSelector labelID,
      final AttributeSelector recipientType,
      final AttributeSelector recipientID,
      final AttributeSelector body) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT DISTINCT c FROM CharacterMailMessage c ");
                                        qs.append("JOIN c.labels d ");
                                        qs.append("JOIN c.recipients e WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "messageID", messageID);
                                        AttributeSelector.addLongSelector(qs, "c", "senderID", senderID);
                                        AttributeSelector.addLongSelector(qs, "c", "sentDate", sentDate);
                                        AttributeSelector.addStringSelector(qs, "c", "title", title, p);
                                        AttributeSelector.addBooleanSelector(qs, "c", "msgRead", msgRead);
                                        AttributeSelector.addIntSelector(qs, null, "d", labelID);
                                        AttributeSelector.addStringSelector(qs, "e", "recipientType", recipientType, p);
                                        AttributeSelector.addIntSelector(qs, "e", "recipientID", recipientID);
                                        AttributeSelector.addStringSelector(qs, "c", "body", body, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterMailMessage> query = EveKitUserAccountProvider.getFactory()
                                                                                                          .getEntityManager()
                                                                                                          .createQuery(
                                                                                                              qs.toString(),
                                                                                                              CharacterMailMessage.class);
                                        query.setParameter("owner", owner);
                                        query.setMaxResults(maxresults);
                                        p.fillParams(query);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
