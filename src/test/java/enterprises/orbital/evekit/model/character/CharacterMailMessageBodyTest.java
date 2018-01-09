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
import enterprises.orbital.evekit.model.character.CharacterMailMessageBody;

public class CharacterMailMessageBodyTest extends AbstractModelTester<CharacterMailMessageBody> {
  final long                                                messageID = TestBase.getRandomInt(100000000);
  final boolean                                             retrieved = true;
  final String                                              body      = "test body";

  final ClassUnderTestConstructor<CharacterMailMessageBody> eol       = new ClassUnderTestConstructor<CharacterMailMessageBody>() {

                                                                        @Override
                                                                        public CharacterMailMessageBody getCUT() {
                                                                          return new CharacterMailMessageBody(messageID, retrieved, body);
                                                                        }

                                                                      };

  ClassUnderTestConstructor<CharacterMailMessageBody>       live      = new ClassUnderTestConstructor<CharacterMailMessageBody>() {
                                                                        @Override
                                                                        public CharacterMailMessageBody getCUT() {
                                                                          return new CharacterMailMessageBody(messageID, !retrieved, body + " 2");
                                                                        }

                                                                      };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterMailMessageBody>() {

      @Override
      public CharacterMailMessageBody[] getVariants() {
        return new CharacterMailMessageBody[] {
            new CharacterMailMessageBody(messageID + 1, retrieved, body), new CharacterMailMessageBody(messageID, !retrieved, body),
            new CharacterMailMessageBody(messageID, retrieved, body + " 2"),
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, new ModelRetriever<CharacterMailMessageBody>() {

      @Override
      public CharacterMailMessageBody getModel(SynchronizedEveAccount account, long time) {
        return CharacterMailMessageBody.get(account, time, messageID);
      }

    });
  }

  @Test
  public void testGetUnretrievedMessageIDs() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // - retrieved messages
    final long messageID = TestBase.getRandomInt(100000000);
    final boolean retrieved = false;
    final String body = "test body";

    CharacterMailMessageBody existing;
    Set<Long> listCheck = new HashSet<Long>();

    existing = new CharacterMailMessageBody(messageID, retrieved, body);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(messageID);

    existing = new CharacterMailMessageBody(messageID + 10, retrieved, body + " 2");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(messageID + 10);

    // Live but already retrieved
    existing = new CharacterMailMessageBody(messageID + 20, true, body + " 3");
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = new CharacterMailMessageBody(messageID + 30, retrieved, body + " 4");
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterMailMessageBody(messageID + 40, retrieved, body + " 5");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterMailMessageBody(messageID + 50, retrieved, body + " 6");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Long> result = CharacterMailMessageBody.getUnretrievedMessageIDs(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Long next : result) {
      Assert.assertTrue(listCheck.contains(next));
    }

  }

}
