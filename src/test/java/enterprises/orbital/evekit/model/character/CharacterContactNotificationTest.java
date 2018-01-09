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
import enterprises.orbital.evekit.model.character.CharacterContactNotification;

public class CharacterContactNotificationTest extends AbstractModelTester<CharacterContactNotification> {
  final long                                                    notificationID = TestBase.getRandomInt(100000000);
  final long                                                    senderID       = TestBase.getRandomInt(100000000);
  final String                                                  senderName     = "test sender";
  final long                                                    sentDate       = TestBase.getRandomInt(100000000);
  final String                                                  messageData    = "test message data";

  final ClassUnderTestConstructor<CharacterContactNotification> eol            = new ClassUnderTestConstructor<CharacterContactNotification>() {

                                                                                 @Override
                                                                                 public CharacterContactNotification getCUT() {
                                                                                   return new CharacterContactNotification(
                                                                                       notificationID, senderID, senderName, sentDate, messageData);
                                                                                 }

                                                                               };

  final ClassUnderTestConstructor<CharacterContactNotification> live           = new ClassUnderTestConstructor<CharacterContactNotification>() {
                                                                                 @Override
                                                                                 public CharacterContactNotification getCUT() {
                                                                                   return new CharacterContactNotification(
                                                                                       notificationID, senderID, senderName + " 2", sentDate,
                                                                                       messageData + " 2");
                                                                                 }

                                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterContactNotification>() {

      @Override
      public CharacterContactNotification[] getVariants() {
        return new CharacterContactNotification[] {
            new CharacterContactNotification(notificationID + 1, senderID, senderName, sentDate, messageData),
            new CharacterContactNotification(notificationID, senderID + 1, senderName, sentDate, messageData),
            new CharacterContactNotification(notificationID, senderID, senderName + "1", sentDate, messageData),
            new CharacterContactNotification(notificationID, senderID, senderName, sentDate + 1, messageData),
            new CharacterContactNotification(notificationID, senderID, senderName, sentDate, messageData + "1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_NOTIFICATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterContactNotification>() {

      @Override
      public CharacterContactNotification getModel(SynchronizedEveAccount account, long time) {
        return CharacterContactNotification.get(account, time, notificationID);
      }

    });
  }

  @Test
  public void testGetAllNotifications() throws Exception {
    // Should exclude:
    // - notifications for a different account
    // - notifications not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    final long notificationID = TestBase.getRandomInt(100000000);
    final long senderID = TestBase.getRandomInt(100000000);
    final String senderName = "test sender";
    final long sentDate = TestBase.getRandomInt(100000000);
    final String messageData = "test message data";

    CharacterContactNotification existing;
    Map<Long, CharacterContactNotification> listCheck = new HashMap<Long, CharacterContactNotification>();

    existing = new CharacterContactNotification(notificationID, senderID, senderName, sentDate, messageData);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID, existing);

    existing = new CharacterContactNotification(notificationID + 10, senderID, senderName + " 2", sentDate + 10, messageData + " 2");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 10, existing);

    existing = new CharacterContactNotification(notificationID + 20, senderID, senderName + " 3", sentDate + 20, messageData + " 3");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 20, existing);

    existing = new CharacterContactNotification(notificationID + 30, senderID, senderName + " 4", sentDate + 30, messageData + " 4");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(notificationID + 30, existing);

    // Associated with different account
    existing = new CharacterContactNotification(notificationID, senderID, senderName, sentDate, messageData);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterContactNotification(notificationID + 5, senderID, senderName + " 0.5", sentDate + 5, messageData + " 0.5");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterContactNotification(notificationID + 3, senderID, senderName + " 0.3", sentDate + 3, messageData + " 0.3");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all notifications are returned
    List<CharacterContactNotification> result = CharacterContactNotification.getAllNotifications(testAccount, 8888L, 5, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterContactNotification next : result) {
      long checkID = next.getNotificationID();
      Assert.assertTrue(listCheck.containsKey(checkID));
      Assert.assertEquals(listCheck.get(checkID), next);
    }

    // Verify limited set returned
    result = CharacterContactNotification.getAllNotifications(testAccount, 8888L, 2, sentDate - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(notificationID), result.get(0));
    Assert.assertEquals(listCheck.get(notificationID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = CharacterContactNotification.getAllNotifications(testAccount, 8888L, 100, sentDate + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(notificationID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(notificationID + 30), result.get(1));
  }

}
