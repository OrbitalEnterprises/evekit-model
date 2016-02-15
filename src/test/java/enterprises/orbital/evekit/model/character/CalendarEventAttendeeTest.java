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
import enterprises.orbital.evekit.model.character.CalendarEventAttendee;

public class CalendarEventAttendeeTest extends AbstractModelTester<CalendarEventAttendee> {
  final int                                              eventID       = TestBase.getRandomInt();
  final long                                             characterID   = TestBase.getRandomLong();
  final String                                           characterName = "test character";
  final String                                           response      = "test response";

  final ClassUnderTestConstructor<CalendarEventAttendee> eol           = new ClassUnderTestConstructor<CalendarEventAttendee>() {

                                                                         @Override
                                                                         public CalendarEventAttendee getCUT() {
                                                                           return new CalendarEventAttendee(eventID, characterID, characterName, response);
                                                                         }

                                                                       };

  final ClassUnderTestConstructor<CalendarEventAttendee> live          = new ClassUnderTestConstructor<CalendarEventAttendee>() {
                                                                         @Override
                                                                         public CalendarEventAttendee getCUT() {
                                                                           return new CalendarEventAttendee(
                                                                               eventID, characterID, characterName, response + " 2");
                                                                         }

                                                                       };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CalendarEventAttendee>() {

      @Override
      public CalendarEventAttendee[] getVariants() {
        return new CalendarEventAttendee[] {
            new CalendarEventAttendee(eventID + 1, characterID, characterName, response),
            new CalendarEventAttendee(eventID, characterID + 1, characterName, response),
            new CalendarEventAttendee(eventID, characterID, characterName + "1", response),
            new CalendarEventAttendee(eventID, characterID, characterName, response + "1")
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CALENDAR_EVENT_ATTENDEES));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CalendarEventAttendee>() {

      @Override
      public CalendarEventAttendee getModel(SynchronizedEveAccount account, long time) {
        return CalendarEventAttendee.get(account, time, eventID, characterID);
      }

    });

  }

  @Test
  public void testGetByEventID() {
    // Should exclude:
    // - attendees for a different account
    // - attendees not live at the given time
    int eventID = 1234;
    CalendarEventAttendee existing;
    Map<Long, CalendarEventAttendee> listCheck = new HashMap<Long, CalendarEventAttendee>();

    existing = new CalendarEventAttendee(eventID, 1234L, "test event", "test response 1");
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(1234L, existing);

    existing = new CalendarEventAttendee(eventID, 5678L, "test event", "test response 2");
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(5678L, existing);

    // Associated with different account
    existing = new CalendarEventAttendee(eventID, 1234L, "test event", "test response 3");
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new CalendarEventAttendee(eventID, 1234L, "test event", "test response 4");
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new CalendarEventAttendee(eventID, 1234L, "test event", "test response 5");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<CalendarEventAttendee> result = CalendarEventAttendee.getByEventID(testAccount, 8888L, eventID);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CalendarEventAttendee next : result) {
      long charID = next.getCharacterID();
      Assert.assertTrue(listCheck.containsKey(charID));
      Assert.assertEquals(listCheck.get(charID), next);
    }
  }

}
