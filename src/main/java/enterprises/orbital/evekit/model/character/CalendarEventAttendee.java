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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_calendar_event_attendee",
    indexes = {
        @Index(
            name = "eventIDIndex",
            columnList = "eventID",
            unique = false),
        @Index(
            name = "characterIDIndex",
            columnList = "characterID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "CalendarEventAttendee.getByEventAndCharacterID",
        query = "SELECT c FROM CalendarEventAttendee c where c.owner = :owner and c.eventID = :event and c.characterID = :char and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CalendarEventAttendee.getAllByEventID",
        query = "SELECT c FROM CalendarEventAttendee c where c.owner = :owner and c.eventID = :event and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API cache time unknown
public class CalendarEventAttendee extends CachedData {
  private static final Logger log  = Logger.getLogger(CalendarEventAttendee.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES);
  private long                eventID;
  private long                characterID;
  private String              characterName;
  private String              response;

  @SuppressWarnings("unused")
  protected CalendarEventAttendee() {}

  public CalendarEventAttendee(long eventID, long characterID, String characterName, String response) {
    this.eventID = eventID;
    this.characterID = characterID;
    this.characterName = characterName;
    this.response = response;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CalendarEventAttendee)) return false;
    CalendarEventAttendee other = (CalendarEventAttendee) sup;
    return eventID == other.eventID && characterID == other.characterID && nullSafeObjectCompare(characterName, other.characterName)
        && nullSafeObjectCompare(response, other.response);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getEventID() {
    return eventID;
  }

  public long getCharacterID() {
    return characterID;
  }

  public String getCharacterName() {
    return characterName;
  }

  public String getResponse() {
    return response;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + ((characterName == null) ? 0 : characterName.hashCode());
    result = prime * result + (int) (eventID ^ (eventID >>> 32));
    result = prime * result + ((response == null) ? 0 : response.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CalendarEventAttendee other = (CalendarEventAttendee) obj;
    if (characterID != other.characterID) return false;
    if (characterName == null) {
      if (other.characterName != null) return false;
    } else if (!characterName.equals(other.characterName)) return false;
    if (eventID != other.eventID) return false;
    if (response == null) {
      if (other.response != null) return false;
    } else if (!response.equals(other.response)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CalendarEventAttendee [eventID=" + eventID + ", characterID=" + characterID + ", characterName=" + characterName + ", response=" + response
        + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve calendar event attendee with the given parameters live at the given time, or null if no such attendee exists.
   * 
   * @param owner
   *          attendee owner
   * @param time
   *          time at which attendee must belive
   * @param eID
   *          event ID for calendar event
   * @param cID
   *          character ID of attendee
   * @return calendar event attendee with the given parameters live at the given time, or null if no such attendee exists
   */
  public static CalendarEventAttendee get(
                                          final SynchronizedEveAccount owner,
                                          final long time,
                                          final long eID,
                                          final long cID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CalendarEventAttendee>() {
        @Override
        public CalendarEventAttendee run() throws Exception {
          TypedQuery<CalendarEventAttendee> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CalendarEventAttendee.getByEventAndCharacterID", CalendarEventAttendee.class);
          getter.setParameter("owner", owner);
          getter.setParameter("event", eID);
          getter.setParameter("char", cID);
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

  /**
   * Retrieve all calendar event attendees for the given event, live at the given time
   * 
   * @param owner
   *          attendees owner
   * @param time
   *          time at which attendees must be live
   * @param eID
   *          event ID for the calendar event
   * @return list of calendar event attendees for the given event, live at the given time
   */
  public static List<CalendarEventAttendee> getByEventID(
                                                         final SynchronizedEveAccount owner,
                                                         final long time,
                                                         final long eID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CalendarEventAttendee>>() {
        @Override
        public List<CalendarEventAttendee> run() throws Exception {
          TypedQuery<CalendarEventAttendee> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CalendarEventAttendee.getAllByEventID", CalendarEventAttendee.class);
          getter.setParameter("owner", owner);
          getter.setParameter("event", eID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<CalendarEventAttendee> accessQuery(
                                                        final SynchronizedEveAccount owner,
                                                        final long contid,
                                                        final int maxresults,
                                                        final boolean reverse,
                                                        final AttributeSelector at,
                                                        final AttributeSelector eventID,
                                                        final AttributeSelector characterID,
                                                        final AttributeSelector characterName,
                                                        final AttributeSelector response) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CalendarEventAttendee>>() {
        @Override
        public List<CalendarEventAttendee> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CalendarEventAttendee c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "eventID", eventID);
          AttributeSelector.addLongSelector(qs, "c", "characterID", characterID);
          AttributeSelector.addStringSelector(qs, "c", "characterName", characterName, p);
          AttributeSelector.addStringSelector(qs, "c", "response", response, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CalendarEventAttendee> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                          CalendarEventAttendee.class);
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
