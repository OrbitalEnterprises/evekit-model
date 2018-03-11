package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarEventAttendeeTest extends AbstractModelTester<CalendarEventAttendee> {
  private final int eventID = TestBase.getRandomInt();
  private final int characterID = TestBase.getRandomInt();
  private final String response = "test response";

  final ClassUnderTestConstructor<CalendarEventAttendee> eol = () -> new CalendarEventAttendee(eventID, characterID,
                                                                                               response);

  final ClassUnderTestConstructor<CalendarEventAttendee> live = () -> new CalendarEventAttendee(
      eventID, characterID, response + " 2");

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new CalendarEventAttendee[]{
        new CalendarEventAttendee(eventID + 1, characterID, response),
        new CalendarEventAttendee(eventID, characterID + 1, response),
        new CalendarEventAttendee(eventID, characterID, response + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CalendarEventAttendee.get(account, time, eventID, characterID));
  }

  @Test
  public void testGetByEventID() throws IOException {
    // Should exclude:
    // - attendees for a different account
    // - attendees not live at the given time
    int eventID = 1234;
    CalendarEventAttendee existing;
    Map<Integer, CalendarEventAttendee> listCheck = new HashMap<>();

    existing = new CalendarEventAttendee(eventID, 1234, "test response 1");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(1234, existing);

    existing = new CalendarEventAttendee(eventID, 5678, "test response 2");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(5678, existing);

    // Associated with different account
    existing = new CalendarEventAttendee(eventID, 1234, "test response 3");
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CalendarEventAttendee(eventID, 1234, "test response 4");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CalendarEventAttendee(eventID, 1234, "test response 5");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CalendarEventAttendee> result = CachedData.retrieveAll(8888L,
                                                                (contid, at) -> CalendarEventAttendee.accessQuery(
                                                                    testAccount, contid, 1000, false, at,
                                                                    AttributeSelector.any(), AttributeSelector.any(),
                                                                    AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CalendarEventAttendee next : result) {
      int charID = next.getCharacterID();
      Assert.assertTrue(listCheck.containsKey(charID));
      Assert.assertEquals(listCheck.get(charID), next);
    }
  }

}
