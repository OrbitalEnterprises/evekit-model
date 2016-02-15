package enterprises.orbital.evekit.model.character;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.character.UpcomingCalendarEvent;

public class UpcomingCalendarEventTest extends AbstractModelTester<UpcomingCalendarEvent> {
  final int                                              duration   = TestBase.getRandomInt(100000000);
  final long                                             eventDate  = TestBase.getRandomInt(100000000);
  final int                                              eventID    = TestBase.getRandomInt(100000000);
  final String                                           eventText  = "test event text";
  final String                                           eventTitle = "test event title";
  final long                                             ownerID    = TestBase.getRandomInt(100000000);
  final String                                           ownerName  = "test owner name";
  final String                                           response   = "test response";
  final boolean                                          important  = false;

  final ClassUnderTestConstructor<UpcomingCalendarEvent> eol        = new ClassUnderTestConstructor<UpcomingCalendarEvent>() {

                                                                      @Override
                                                                      public UpcomingCalendarEvent getCUT() {
                                                                        return new UpcomingCalendarEvent(
                                                                            duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response,
                                                                            important);
                                                                      }

                                                                    };

  final ClassUnderTestConstructor<UpcomingCalendarEvent> live       = new ClassUnderTestConstructor<UpcomingCalendarEvent>() {
                                                                      @Override
                                                                      public UpcomingCalendarEvent getCUT() {
                                                                        return new UpcomingCalendarEvent(
                                                                            duration + 1, eventDate, eventID, eventText, eventTitle, ownerID, ownerName,
                                                                            response, important);
                                                                      }

                                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<UpcomingCalendarEvent>() {

      @Override
      public UpcomingCalendarEvent[] getVariants() {
        return new UpcomingCalendarEvent[] {
            new UpcomingCalendarEvent(duration + 1, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response, important),
            new UpcomingCalendarEvent(duration, eventDate + 1, eventID, eventText, eventTitle, ownerID, ownerName, response, important),
            new UpcomingCalendarEvent(duration, eventDate, eventID + 1, eventText, eventTitle, ownerID, ownerName, response, important),
            new UpcomingCalendarEvent(duration, eventDate, eventID, eventText + " 1", eventTitle, ownerID, ownerName, response, important),
            new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle + " 1", ownerID, ownerName, response, important),
            new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID + 1, ownerName, response, important),
            new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName + " 1", response, important),
            new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response + " 1", important),
            new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response, !important)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_UPCOMING_CALENDAR_EVENTS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<UpcomingCalendarEvent>() {

      @Override
      public UpcomingCalendarEvent getModel(SynchronizedEveAccount account, long time) {
        return UpcomingCalendarEvent.get(account, time, eventID);
      }

    });
  }

  @Test
  public void testGetAllUpcomingCalendarEvents() throws Exception {
    // Should exclude:
    // - events for a different account
    // - events not live at the given time
    UpcomingCalendarEvent existing;
    Map<Integer, UpcomingCalendarEvent> listCheck = new HashMap<Integer, UpcomingCalendarEvent>();

    existing = new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response, important);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(eventID, existing);

    existing = new UpcomingCalendarEvent(duration, eventDate, eventID + 10, eventText, eventTitle, ownerID, ownerName, response, important);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(eventID + 10, existing);

    // Associated with different account
    existing = new UpcomingCalendarEvent(duration, eventDate, eventID, eventText, eventTitle, ownerID, ownerName, response, important);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new UpcomingCalendarEvent(duration, eventDate, eventID + 3, eventText, eventTitle, ownerID, ownerName, response, important);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new UpcomingCalendarEvent(duration, eventDate, eventID + 4, eventText, eventTitle, ownerID, ownerName, response, important);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<UpcomingCalendarEvent> result = UpcomingCalendarEvent.getAllUpcomingCalendarEvents(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (UpcomingCalendarEvent next : result) {
      int eventID = next.getEventID();
      Assert.assertTrue(listCheck.containsKey(eventID));
      Assert.assertEquals(listCheck.get(eventID), next);
    }

  }

}
