package enterprises.orbital.evekit.model.character;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.character.CharacterNotification;

public class CharacterNotificationTest extends AbstractModelTester<CharacterNotification> {
  final long                                             notificationID = TestBase.getRandomInt(100000000);
  final int                                              typeID         = TestBase.getRandomInt(100000000);
  final long                                             senderID       = TestBase.getRandomInt(100000000);
  final long                                             sentDate       = TestBase.getRandomInt(100000000);
  final boolean                                          msgRead        = true;

  final ClassUnderTestConstructor<CharacterNotification> eol            = new ClassUnderTestConstructor<CharacterNotification>() {

                                                                          @Override
                                                                          public CharacterNotification getCUT() {
                                                                            return new CharacterNotification(
                                                                                notificationID, typeID, senderID, sentDate, msgRead);
                                                                          }

                                                                        };

  final ClassUnderTestConstructor<CharacterNotification> live           = new ClassUnderTestConstructor<CharacterNotification>() {
                                                                          @Override
                                                                          public CharacterNotification getCUT() {
                                                                            return new CharacterNotification(
                                                                                notificationID, typeID + 1, senderID + 1, sentDate + 1, !msgRead);
                                                                          }

                                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterNotification>() {

      @Override
      public CharacterNotification[] getVariants() {
        return new CharacterNotification[] {
            new CharacterNotification(notificationID + 1, typeID, senderID, sentDate, msgRead),
            new CharacterNotification(notificationID, typeID + 1, senderID, sentDate, msgRead),
            new CharacterNotification(notificationID, typeID, senderID + 1, sentDate, msgRead),
            new CharacterNotification(notificationID, typeID, senderID, sentDate + 1, msgRead),
            new CharacterNotification(notificationID, typeID, senderID, sentDate, !msgRead)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_NOTIFICATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, new ModelRetriever<CharacterNotification>() {

      @Override
      public CharacterNotification getModel(SynchronizedEveAccount account, long time) {
        return CharacterNotification.get(account, time, notificationID);
      }

    });
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
    Set<Long> listCheck = new HashSet<Long>();

    existing = new CharacterNotification(notificationID, typeID, senderID, sentDate, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(notificationID);

    existing = new CharacterNotification(notificationID + 10, typeID + 1, senderID + 1, sentDate + 10, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(notificationID + 10);

    existing = new CharacterNotification(notificationID + 20, typeID + 2, senderID + 2, sentDate + 20, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(notificationID + 20);

    existing = new CharacterNotification(notificationID + 30, typeID + 3, senderID + 3, sentDate + 30, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(notificationID + 30);

    // Associated with different account
    existing = new CharacterNotification(notificationID, typeID, senderID, sentDate, msgRead);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterNotification(notificationID + 5, typeID + 5, senderID + 5, sentDate + 5, msgRead);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterNotification(notificationID + 3, typeID + 6, senderID + 6, sentDate + 6, msgRead);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Unread at given time
    existing = new CharacterNotification(notificationID + 40, typeID + 7, senderID + 7, sentDate + 40, false);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(notificationID + 40);

    // Verify only unread message is returned
    List<Long> result = CharacterNotification.getNotificationIDs(testAccount, 8888L, true, 5, 0);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(notificationID + 40, result.get(0).longValue());

    // Verify all notification IDs are returned
    result = CharacterNotification.getNotificationIDs(testAccount, 8888L, false, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Long next : result) {
      Assert.assertTrue(listCheck.contains(next));
    }

    // Verify limited set returned
    result = CharacterNotification.getNotificationIDs(testAccount, 8888L, false, 2, sentDate - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(notificationID, result.get(0).longValue());
    Assert.assertEquals(notificationID + 10, result.get(1).longValue());

    // Verify continuation ID returns proper set
    result = CharacterNotification.getNotificationIDs(testAccount, 8888L, false, 100, sentDate + 10);
    Assert.assertEquals(3, result.size());
    Assert.assertEquals(notificationID + 20, result.get(0).longValue());
    Assert.assertEquals(notificationID + 30, result.get(1).longValue());
    Assert.assertEquals(notificationID + 40, result.get(2).longValue());

  }
}
