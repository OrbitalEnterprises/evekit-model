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

public class CharacterNotificationTest extends AbstractModelTester<CharacterNotification> {
  private final long notificationID = TestBase.getRandomInt(100000000);
  private final String type = TestBase.getRandomText(50);
  private final int senderID = TestBase.getRandomInt(100000000);
  private final String senderType = TestBase.getRandomText(50);
  private final long sentDate = TestBase.getRandomInt(100000000);
  private final boolean msgRead = true;
  private final String text = TestBase.getRandomText(1000);

  final ClassUnderTestConstructor<CharacterNotification> eol = () -> new CharacterNotification(
      notificationID, type, senderID, senderType, sentDate, msgRead, text);

  final ClassUnderTestConstructor<CharacterNotification> live = () -> new CharacterNotification(
      notificationID, type, senderID + 1, senderType, sentDate + 1, !msgRead, text);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterNotification[]{
        new CharacterNotification(notificationID + 1, type, senderID, senderType, sentDate, msgRead, text),
        new CharacterNotification(notificationID, type + "1", senderID, senderType, sentDate, msgRead, text),
        new CharacterNotification(notificationID, type, senderID + 1, senderType, sentDate, msgRead, text),
        new CharacterNotification(notificationID, type, senderID, senderType + "1", sentDate, msgRead, text),
        new CharacterNotification(notificationID, type, senderID, senderType, sentDate + 1, msgRead, text),
        new CharacterNotification(notificationID, type, senderID, senderType, sentDate, !msgRead, text),
        new CharacterNotification(notificationID, type, senderID, senderType, sentDate, msgRead, text + "1"),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_NOTIFICATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterNotification.get(account, time, notificationID));
  }

  @Test
  public void testGetNotificationIDs() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    // - unread limitation

    CharacterNotification existing;
    Map<Long, CharacterNotification> listCheck = new HashMap<>();

    existing = new CharacterNotification(notificationID, type, senderID, senderType, sentDate, msgRead, text);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID, existing);

    existing = new CharacterNotification(notificationID + 10, type, senderID + 1, senderType, sentDate + 10, msgRead,
                                         text);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 10, existing);

    existing = new CharacterNotification(notificationID + 20, type, senderID + 2, senderType, sentDate + 20, msgRead,
                                         text);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 20, existing);

    existing = new CharacterNotification(notificationID + 30, type, senderID + 3, senderType, sentDate + 30, msgRead,
                                         text);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 30, existing);

    // Associated with different account
    existing = new CharacterNotification(notificationID, type, senderID, senderType, sentDate, msgRead, text);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterNotification(notificationID + 5, type, senderID + 5, senderType, sentDate + 5, msgRead,
                                         text);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterNotification(notificationID + 3, type, senderID + 6, senderType, sentDate + 6, msgRead,
                                         text);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Unread at given time
    existing = new CharacterNotification(notificationID + 40, type, senderID + 7, senderType, sentDate + 40, false,
                                         text);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 40, existing);

    // Verify only unread message is returned
    List<CharacterNotification> result = CachedData.retrieveAll(8888L,
                                                                (contid, at) -> CharacterNotification.accessQuery(
                                                                    testAccount, contid, 1000, false, at,
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any()));
    Assert.assertEquals(5, result.size());
    for (CharacterNotification next : result) {
      Assert.assertTrue(listCheck.containsKey(next.getNotificationID()));
      Assert.assertEquals(listCheck.get(next.getNotificationID()), next);
    }
  }
}