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
    name = "evekit_data_upcoming_calendar_event",
    indexes = {
        @Index(
            name = "eventIDIndex",
            columnList = "eventID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "UpcomingCalendarEvent.getByEventID",
        query = "SELECT c FROM UpcomingCalendarEvent c where c.owner = :owner and c.eventID = :eid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "UpcomingCalendarEvent.getAll",
        query = "SELECT c FROM UpcomingCalendarEvent c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.eventDate asc"),
})
// 2 hour cache time - API caches for 1 hour
public class UpcomingCalendarEvent extends CachedData {
  private static final Logger log       = Logger.getLogger(UpcomingCalendarEvent.class.getName());
  private static final byte[] MASK      = AccountAccessMask.createMask(AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS);
  private int                 duration;
  private long                eventDate = -1;
  private long                eventID;
  @Lob
  @Column(
      length = 102400)
  private String              eventText;
  private String              eventTitle;
  private long                ownerID;
  private String              ownerName;
  private String              response;
  private boolean             important;

  @SuppressWarnings("unused")
  private UpcomingCalendarEvent() {}

  public UpcomingCalendarEvent(int duration, long eventDate, long eventID, String eventText, String eventTitle, long ownerID, String ownerName, String response,
                               boolean important) {
    super();
    this.duration = duration;
    this.eventDate = eventDate;
    this.eventID = eventID;
    this.eventText = eventText;
    this.eventTitle = eventTitle;
    this.ownerID = ownerID;
    this.ownerName = ownerName;
    this.response = response;
    this.important = important;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof UpcomingCalendarEvent)) return false;
    UpcomingCalendarEvent other = (UpcomingCalendarEvent) sup;
    return duration == other.duration && eventDate == other.eventDate && eventID == other.eventID && nullSafeObjectCompare(eventText, other.eventText)
        && nullSafeObjectCompare(eventTitle, other.eventTitle) && ownerID == other.ownerID && nullSafeObjectCompare(ownerName, other.ownerName)
        && nullSafeObjectCompare(response, other.response) && important == other.important;
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

  public long getEventID() {
    return eventID;
  }

  public String getEventText() {
    return eventText;
  }

  public String getEventTitle() {
    return eventTitle;
  }

  public long getOwnerID() {
    return ownerID;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getResponse() {
    return response;
  }

  public boolean isImportant() {
    return important;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + duration;
    result = prime * result + (int) (eventDate ^ (eventDate >>> 32));
    result = prime * result + (int) (eventID ^ (eventID >>> 32));
    result = prime * result + ((eventText == null) ? 0 : eventText.hashCode());
    result = prime * result + ((eventTitle == null) ? 0 : eventTitle.hashCode());
    result = prime * result + (important ? 1231 : 1237);
    result = prime * result + (int) (ownerID ^ (ownerID >>> 32));
    result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
    result = prime * result + ((response == null) ? 0 : response.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    UpcomingCalendarEvent other = (UpcomingCalendarEvent) obj;
    if (duration != other.duration) return false;
    if (eventDate != other.eventDate) return false;
    if (eventID != other.eventID) return false;
    if (eventText == null) {
      if (other.eventText != null) return false;
    } else if (!eventText.equals(other.eventText)) return false;
    if (eventTitle == null) {
      if (other.eventTitle != null) return false;
    } else if (!eventTitle.equals(other.eventTitle)) return false;
    if (important != other.important) return false;
    if (ownerID != other.ownerID) return false;
    if (ownerName == null) {
      if (other.ownerName != null) return false;
    } else if (!ownerName.equals(other.ownerName)) return false;
    if (response == null) {
      if (other.response != null) return false;
    } else if (!response.equals(other.response)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "UpcomingCalendarEvent [duration=" + duration + ", eventDate=" + eventDate + ", eventID=" + eventID + ", eventText=" + eventText + ", eventTitle="
        + eventTitle + ", ownerID=" + ownerID + ", ownerName=" + ownerName + ", response=" + response + ", important=" + important + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static UpcomingCalendarEvent get(
                                          final SynchronizedEveAccount owner,
                                          final long time,
                                          final long eventID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<UpcomingCalendarEvent>() {
        @Override
        public UpcomingCalendarEvent run() throws Exception {
          TypedQuery<UpcomingCalendarEvent> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("UpcomingCalendarEvent.getByEventID", UpcomingCalendarEvent.class);
          getter.setParameter("owner", owner);
          getter.setParameter("eid", eventID);
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

  public static List<UpcomingCalendarEvent> getAllUpcomingCalendarEvents(
                                                                         final SynchronizedEveAccount owner,
                                                                         final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<UpcomingCalendarEvent>>() {
        @Override
        public List<UpcomingCalendarEvent> run() throws Exception {
          TypedQuery<UpcomingCalendarEvent> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("UpcomingCalendarEvent.getAll",
                                                                                                                                UpcomingCalendarEvent.class);
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
                                                        final AttributeSelector important) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<UpcomingCalendarEvent>>() {
        @Override
        public List<UpcomingCalendarEvent> run() throws Exception {
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
          AttributeSelector.addLongSelector(qs, "c", "eventID", eventID);
          AttributeSelector.addStringSelector(qs, "c", "eventText", eventText, p);
          AttributeSelector.addStringSelector(qs, "c", "eventTitle", eventTitle, p);
          AttributeSelector.addLongSelector(qs, "c", "ownerID", ownerID);
          AttributeSelector.addStringSelector(qs, "c", "ownerName", ownerName, p);
          AttributeSelector.addStringSelector(qs, "c", "response", response, p);
          AttributeSelector.addBooleanSelector(qs, "c", "important", important);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<UpcomingCalendarEvent> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                          UpcomingCalendarEvent.class);
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
