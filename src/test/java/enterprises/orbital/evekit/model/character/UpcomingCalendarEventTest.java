package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpcomingCalendarEventTest extends AbstractModelTester<UpcomingCalendarEvent> {
  private final int duration = TestBase.getRandomInt(100000000);
  private final long eventDate = TestBase.getRandomInt(100000000);
  private final int eventID = TestBase.getRandomInt(100000000);
  private final String eventText = "test event text";
  private final String eventTitle = "test event title";
  private final int ownerID = TestBase.getRandomInt(100000000);
  private final String ownerName = "test owner name";
  private final String response = "test response";
  private final int importance = TestBase.getRandomInt();
  private final String ownerType = "test owner type";

  final ClassUnderTestConstructor<UpcomingCalendarEvent> eol = () -> new UpcomingCalendarEvent(
      duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response,
      importance, ownerType);

  final ClassUnderTestConstructor<UpcomingCalendarEvent> live = () -> new UpcomingCalendarEvent(
      duration + 1, eventDate, eventID, eventText, eventTitle, ownerID, ownerName,
      response, importance, ownerType);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new UpcomingCalendarEvent[]{
        new UpcomingCalendarEvent(duration + 1, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate + 1, eventID, eventText, eventTitle, ownerID, ownerName, response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID + 1, eventText, eventTitle, ownerID, ownerName, response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText + " 1", eventTitle, ownerID, ownerName,
                                  response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle + " 1", ownerID, ownerName,
                                  response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID + 1, ownerName, response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName + " 1",
                                  response,
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName,
                                  response + " 1",
                                  importance, ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response,
                                  importance + 1,
                                  ownerType),
        new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response,
                                  importance, ownerType + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> UpcomingCalendarEvent.get(account, time, eventID));
  }

  @Test
  public void testGetAllUpcomingCalendarEvents() throws Exception {
    // Should exclude:
    // - events for a different account
    // - events not live at the given time
    UpcomingCalendarEvent existing;
    Map<Integer, UpcomingCalendarEvent> listCheck = new HashMap<>();

    existing = new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName,
                                         response,
                                         importance, ownerType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(eventID, existing);

    existing = new UpcomingCalendarEvent(duration, eventDate, eventID + 10, eventText, eventTitle, ownerID, ownerName,
                                         response,
                                         importance, ownerType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(eventID + 10, existing);

    // Associated with different account
    existing = new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName,
                                         response,
                                         importance, ownerType);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new UpcomingCalendarEvent(duration, eventDate, eventID + 3, eventText, eventTitle, ownerID, ownerName,
                                         response,
                                         importance, ownerType);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new UpcomingCalendarEvent(duration, eventDate, eventID + 4, eventText, eventTitle, ownerID, ownerName,
                                         response,
                                         importance, ownerType);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<UpcomingCalendarEvent> result = CachedData.retrieveAll(8888L,
                                                                (contid, at) -> UpcomingCalendarEvent.accessQuery(
                                                                    testAccount, contid, 1000, false, at,
                                                                    AttributeSelector.any(), AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(), AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(), AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any()));

    Assert.assertEquals(listCheck.size(), result.size());
    for (UpcomingCalendarEvent next : result) {
      int eventID = next.getEventID();
      Assert.assertTrue(listCheck.containsKey(eventID));
      Assert.assertEquals(listCheck.get(eventID), next);
    }

  }

}
