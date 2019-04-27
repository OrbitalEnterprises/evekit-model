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
    name = "evekit_data_calendar_event_attendee",
    indexes = {
        @Index(
            name = "eventIDIndex",
            columnList = "eventID"),
        @Index(
            name = "characterIDIndex",
            columnList = "characterID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CalendarEventAttendee.getByEventAndCharacterID",
        query = "SELECT c FROM CalendarEventAttendee c where c.owner = :owner and c.eventID = :event and c.characterID = :char and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CalendarEventAttendee extends CachedData {
  private static final Logger log = Logger.getLogger(CalendarEventAttendee.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES);

  private int eventID;
  private int characterID;
  private String response;

  @SuppressWarnings("unused")
  protected CalendarEventAttendee() {}

  public CalendarEventAttendee(int eventID, int characterID, String response) {
    this.eventID = eventID;
    this.characterID = characterID;
    this.response = response;
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
    if (!(sup instanceof CalendarEventAttendee)) return false;
    CalendarEventAttendee other = (CalendarEventAttendee) sup;
    return eventID == other.eventID && characterID == other.characterID && nullSafeObjectCompare(response,
                                                                                                 other.response);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(eventID, characterID, response);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getEventID() {
    return eventID;
  }

  public int getCharacterID() {
    return characterID;
  }

  public String getResponse() {
    return response;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CalendarEventAttendee that = (CalendarEventAttendee) o;
    return eventID == that.eventID &&
        characterID == that.characterID &&
        Objects.equals(response, that.response);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eventID, characterID, response);
  }

  @Override
  public String toString() {
    return "CalendarEventAttendee{" +
        "eventID=" + eventID +
        ", characterID=" + characterID +
        ", response='" + response + '\'' +
        '}';
  }

  /**
   * Retrieve calendar event attendee with the given parameters live at the given time, or null if no such attendee exists.
   *
   * @param owner attendee owner
   * @param time  time at which attendee must belive
   * @param eID   event ID for calendar event
   * @param cID   character ID of attendee
   * @return calendar event attendee with the given parameters live at the given time, or null if no such attendee exists
   */
  public static CalendarEventAttendee get(
      final SynchronizedEveAccount owner,
      final long time,
      final int eID,
      final int cID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CalendarEventAttendee> getter = EveKitUserAccountProvider.getFactory()
                                                                                                            .getEntityManager()
                                                                                                            .createNamedQuery(
                                                                                                                "CalendarEventAttendee.getByEventAndCharacterID",
                                                                                                                CalendarEventAttendee.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("event", eID);
                                        getter.setParameter("char", cID);
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

  public static List<CalendarEventAttendee> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector eventID,
      final AttributeSelector characterID,
      final AttributeSelector response) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CalendarEventAttendee c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "eventID", eventID);
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addStringSelector(qs, "c", "response", response, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CalendarEventAttendee> query = EveKitUserAccountProvider.getFactory()
                                                                                                           .getEntityManager()
                                                                                                           .createQuery(
                                                                                                               qs.toString(),
                                                                                                               CalendarEventAttendee.class);
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
