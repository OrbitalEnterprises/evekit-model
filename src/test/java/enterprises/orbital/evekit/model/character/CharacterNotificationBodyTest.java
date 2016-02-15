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
import enterprises.orbital.evekit.model.character.CharacterNotificationBody;

public class CharacterNotificationBodyTest extends AbstractModelTester<CharacterNotificationBody> {
  final long                                                 notificationID = TestBase.getRandomInt(100000000);
  final boolean                                              retrieved      = true;
  final String                                               text           = "test text";
  final boolean                                              missing        = false;

  final ClassUnderTestConstructor<CharacterNotificationBody> eol            = new ClassUnderTestConstructor<CharacterNotificationBody>() {

                                                                              @Override
                                                                              public CharacterNotificationBody getCUT() {
                                                                                return new CharacterNotificationBody(notificationID, retrieved, text, missing);
                                                                              }

                                                                            };

  final ClassUnderTestConstructor<CharacterNotificationBody> live           = new ClassUnderTestConstructor<CharacterNotificationBody>() {
                                                                              @Override
                                                                              public CharacterNotificationBody getCUT() {
                                                                                return new CharacterNotificationBody(
                                                                                    notificationID, !retrieved, text + " 2", !missing);
                                                                              }

                                                                            };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterNotificationBody>() {

      @Override
      public CharacterNotificationBody[] getVariants() {
        return new CharacterNotificationBody[] {
            new CharacterNotificationBody(notificationID + 1, retrieved, text, missing),
            new CharacterNotificationBody(notificationID, !retrieved, text, missing),
            new CharacterNotificationBody(notificationID, retrieved, text + " 2", missing),
            new CharacterNotificationBody(notificationID, retrieved, text, !missing)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_NOTIFICATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterNotificationBody>() {

      @Override
      public CharacterNotificationBody getModel(SynchronizedEveAccount account, long time) {
        return CharacterNotificationBody.get(account, time, notificationID);
      }

    });
  }

  @Test
  public void testGetUnretrievedNotificationIDs() throws Exception {
    // Should exclude:
    // - notifications for a different account
    // - notifications not live at the given time
    // - retrieved notifications
    CharacterNotificationBody existing;
    Set<Long> listCheck = new HashSet<Long>();

    existing = new CharacterNotificationBody(notificationID, false, text, missing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.add(notificationID);

    existing = new CharacterNotificationBody(notificationID + 10, false, text + " 2", missing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.add(notificationID + 10);

    // Live but already retrieved
    existing = new CharacterNotificationBody(notificationID + 20, true, text + " 3", missing);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new CharacterNotificationBody(notificationID + 30, false, text + " 4", missing);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterNotificationBody(notificationID + 40, false, text + " 5", missing);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterNotificationBody(notificationID + 50, false, text + " 6", missing);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<Long> result = CharacterNotificationBody.getUnretrievedNotificationIDs(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Long next : result) {
      Assert.assertTrue(listCheck.contains(next));
    }
  }
}
