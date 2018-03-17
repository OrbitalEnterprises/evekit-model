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

public class CharacterContactNotificationTest extends AbstractModelTester<CharacterContactNotification> {
  private final int notificationID = TestBase.getRandomInt(100000000);
  private final int senderID = TestBase.getRandomInt(100000000);
  private final long sentDate = TestBase.getRandomInt(100000000);
  private final float standingLevel = TestBase.getRandomFloat(10);
  private final String messageData = "test message data";

  final ClassUnderTestConstructor<CharacterContactNotification> eol = () -> new CharacterContactNotification(
      notificationID, senderID, sentDate, standingLevel, messageData);

  final ClassUnderTestConstructor<CharacterContactNotification> live = () -> new CharacterContactNotification(
      notificationID, senderID, sentDate, standingLevel,
      messageData + " 2");

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterContactNotification[]{
        new CharacterContactNotification(notificationID + 1, senderID, sentDate, standingLevel, messageData),
        new CharacterContactNotification(notificationID, senderID + 1, sentDate, standingLevel, messageData),
        new CharacterContactNotification(notificationID, senderID, sentDate + 1, standingLevel, messageData),
        new CharacterContactNotification(notificationID, senderID, sentDate, standingLevel + 1, messageData),
        new CharacterContactNotification(notificationID, senderID, sentDate, standingLevel, messageData + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterContactNotification.get(account, time, notificationID));
  }

  @Test
  public void testGetAllNotifications() throws Exception {
    // Should exclude:
    // - notifications for a different account
    // - notifications not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    CharacterContactNotification existing;
    Map<Integer, CharacterContactNotification> listCheck = new HashMap<>();

    existing = new CharacterContactNotification(notificationID, senderID, sentDate, standingLevel, messageData);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID, existing);

    existing = new CharacterContactNotification(notificationID + 10, senderID, sentDate + 10, standingLevel,
                                                messageData + " 2");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 10, existing);

    existing = new CharacterContactNotification(notificationID + 20, senderID, sentDate + 20, standingLevel,
                                                messageData + " 3");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 20, existing);

    existing = new CharacterContactNotification(notificationID + 30, senderID, sentDate + 30, standingLevel,
                                                messageData + " 4");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 30, existing);

    // Associated with different account
    existing = new CharacterContactNotification(notificationID, senderID, sentDate, standingLevel, messageData);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterContactNotification(notificationID + 5, senderID, sentDate + 5, standingLevel,
                                                messageData + " 0.5");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterContactNotification(notificationID + 3, senderID, sentDate + 3, standingLevel,
                                                messageData + " 0.3");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all notifications are returned
    List<CharacterContactNotification> result = CachedData.retrieveAll(8888L,
                                                                       (contid, at) -> CharacterContactNotification.accessQuery(
                                                                           testAccount, contid, 1000, false, at,
                                                                           AttributeSelector.any(),
                                                                           AttributeSelector.any(),
                                                                           AttributeSelector.any(),
                                                                           AttributeSelector.any(),
                                                                           AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterContactNotification next : result) {
      int checkID = next.getNotificationID();
      Assert.assertTrue(listCheck.containsKey(checkID));
      Assert.assertEquals(listCheck.get(checkID), next);
    }
  }

}
