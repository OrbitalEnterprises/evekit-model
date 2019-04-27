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
    name = "evekit_data_upcoming_calendar_event",
    indexes = {
        @Index(
            name = "eventIDIndex",
            columnList = "eventID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "UpcomingCalendarEvent.getByEventID",
        query = "SELECT c FROM UpcomingCalendarEvent c where c.owner = :owner and c.eventID = :eid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class UpcomingCalendarEvent extends CachedData {
  private static final Logger log = Logger.getLogger(UpcomingCalendarEvent.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS);

  private int duration;
  private long eventDate = -1;
  private int eventID;
  @Lob
  @Column(
      length = 102400)
  private String eventText;
  private String eventTitle;
  private int ownerID;
  private String ownerName;
  private String response;
  private int importance;
  private String ownerType;

  @Transient
  @ApiModelProperty(
      value = "eventDate Date")
  @JsonProperty("eventDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date eventDateDate;

  @SuppressWarnings("unused")
  protected UpcomingCalendarEvent() {}

  public UpcomingCalendarEvent(int duration, long eventDate, int eventID, String eventText, String eventTitle,
                               int ownerID, String ownerName, String response, int importance, String ownerType) {
    this.duration = duration;
    this.eventDate = eventDate;
    this.eventID = eventID;
    this.eventText = eventText;
    this.eventTitle = eventTitle;
    this.ownerID = ownerID;
    this.ownerName = ownerName;
    this.response = response;
    this.importance = importance;
    this.ownerType = ownerType;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    eventDateDate = assignDateField(eventDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof UpcomingCalendarEvent)) return false;
    UpcomingCalendarEvent other = (UpcomingCalendarEvent) sup;
    return duration == other.duration && eventDate == other.eventDate && eventID == other.eventID && nullSafeObjectCompare(
        eventText, other.eventText)
        && nullSafeObjectCompare(eventTitle, other.eventTitle) && ownerID == other.ownerID && nullSafeObjectCompare(
        ownerName, other.ownerName)
        && nullSafeObjectCompare(response, other.response) && importance == other.importance && nullSafeObjectCompare(
        ownerType, other.ownerType);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response,
                          importance, ownerType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getDuration() {
    return duration;
  }

  public long getEventDate() {
    return eventDate;
  }

  public int getEventID() {
    return eventID;
  }

  public String getEventText() {
    return eventText;
  }

  public String getEventTitle() {
    return eventTitle;
  }

  public int getOwnerID() {
    return ownerID;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getResponse() {
    return response;
  }

  public int getImportance() {
    return importance;
  }

  public String getOwnerType() {
    return ownerType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    UpcomingCalendarEvent that = (UpcomingCalendarEvent) o;
    return duration == that.duration &&
        eventDate == that.eventDate &&
        eventID == that.eventID &&
        ownerID == that.ownerID &&
        importance == that.importance &&
        Objects.equals(eventText, that.eventText) &&
        Objects.equals(eventTitle, that.eventTitle) &&
        Objects.equals(ownerName, that.ownerName) &&
        Objects.equals(response, that.response) &&
        Objects.equals(ownerType, that.ownerType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName,
                        response, importance, ownerType);
  }

  @Override
  public String toString() {
    return "UpcomingCalendarEvent{" +
        "duration=" + duration +
        ", eventDate=" + eventDate +
        ", eventID=" + eventID +
        ", eventText='" + eventText + '\'' +
        ", eventTitle='" + eventTitle + '\'' +
        ", ownerID=" + ownerID +
        ", ownerName='" + ownerName + '\'' +
        ", response='" + response + '\'' +
        ", importance=" + importance +
        ", ownerType='" + ownerType + '\'' +
        ", eventDateDate=" + eventDateDate +
        '}';
  }

  public static UpcomingCalendarEvent get(
      final SynchronizedEveAccount owner,
      final long time,
      final int eventID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<UpcomingCalendarEvent> getter = EveKitUserAccountProvider.getFactory()
                                                                                                            .getEntityManager()
                                                                                                            .createNamedQuery(
                                                                                                                "UpcomingCalendarEvent.getByEventID",
                                                                                                                UpcomingCalendarEvent.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("eid", eventID);
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

  public static List<UpcomingCalendarEvent> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector duration,
      final AttributeSelector eventDate,
      final AttributeSelector eventID,
      final AttributeSelector eventText,
      final AttributeSelector eventTitle,
      final AttributeSelector ownerID,
      final AttributeSelector ownerName,
      final AttributeSelector response,
      final AttributeSelector importance,
      final AttributeSelector ownerType) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM UpcomingCalendarEvent c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "duration", duration);
                                        AttributeSelector.addLongSelector(qs, "c", "eventDate", eventDate);
                                        AttributeSelector.addIntSelector(qs, "c", "eventID", eventID);
                                        AttributeSelector.addStringSelector(qs, "c", "eventText", eventText, p);
                                        AttributeSelector.addStringSelector(qs, "c", "eventTitle", eventTitle, p);
                                        AttributeSelector.addIntSelector(qs, "c", "ownerID", ownerID);
                                        AttributeSelector.addStringSelector(qs, "c", "ownerName", ownerName, p);
                                        AttributeSelector.addStringSelector(qs, "c", "response", response, p);
                                        AttributeSelector.addIntSelector(qs, "c", "importance", importance);
                                        AttributeSelector.addStringSelector(qs, "c", "ownerType", ownerType, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<UpcomingCalendarEvent> query = EveKitUserAccountProvider.getFactory()
                                                                                                           .getEntityManager()
                                                                                                           .createQuery(
                                                                                                               qs.toString(),
                                                                                                               UpcomingCalendarEvent.class);
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
