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
import enterprises.orbital.evekit.model.character.CharacterMailMessage;

public class CharacterMailMessageTest extends AbstractModelTester<CharacterMailMessage> {

  final long      messageID        = TestBase.getRandomInt(100000000);
  final long      senderID         = TestBase.getRandomInt(100000000);
  final String    senderName       = "test sender";
  final Set<Long> toCharacterID    = new HashSet<Long>();
  final long      sentDate         = TestBase.getRandomInt(100000000);
  final String    title            = "test title";
  final long      toCorpOrAllianceID = TestBase.getRandomInt(100000000);
  final Set<Long> toListID         = new HashSet<Long>();
  final boolean   msgRead          = true;
  final int       senderTypeID     = TestBase.getRandomInt(100000000);

  public CharacterMailMessageTest() {
    int numReceivers = TestBase.getRandomInt(5) + 1;
    int numLists = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < numReceivers; i++) {
      toCharacterID.add(TestBase.getUniqueRandomLong());
    }
    for (int i = 0; i < numLists; i++) {
      toListID.add(TestBase.getUniqueRandomLong());
    }
  }

  public CharacterMailMessage makeMessage(long messageID, String senderName, long sentDate, boolean msgRead) {
    CharacterMailMessage result = new CharacterMailMessage(messageID, senderID, senderName, sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID);
    result.getToCharacterID().addAll(toCharacterID);
    result.getToListID().addAll(toListID);
    return result;
  }

  final ClassUnderTestConstructor<CharacterMailMessage> eol  = new ClassUnderTestConstructor<CharacterMailMessage>() {

                                                               @Override
                                                               public CharacterMailMessage getCUT() {
                                                                 return makeMessage(messageID, senderName, sentDate, msgRead);
                                                               }

                                                             };

  final ClassUnderTestConstructor<CharacterMailMessage> live = new ClassUnderTestConstructor<CharacterMailMessage>() {
                                                               @Override
                                                               public CharacterMailMessage getCUT() {
                                                                 return makeMessage(messageID, senderName + " 2", sentDate, msgRead);
                                                               }

                                                             };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterMailMessage>() {

      @Override
      public CharacterMailMessage[] getVariants() {
        CharacterMailMessage[] result = new CharacterMailMessage[] {
            new CharacterMailMessage(messageID + 1, senderID, senderName, sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID + 1, senderName, sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName + "1", sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate + 1, title, toCorpOrAllianceID, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate, title + "1", toCorpOrAllianceID, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate, title, toCorpOrAllianceID + 1, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate, title, toCorpOrAllianceID, !msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID + 1),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID),
            new CharacterMailMessage(messageID, senderID, senderName, sentDate, title, toCorpOrAllianceID, msgRead, senderTypeID)
        };
        for (int i = 0; i < result.length; i++) {
          result[i].getToCharacterID().addAll(toCharacterID);
          result[i].getToListID().addAll(toListID);
        }
        result[result.length - 2].getToCharacterID().add(1L);
        result[result.length - 1].getToListID().add(1L);
        return result;

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, new ModelRetriever<CharacterMailMessage>() {

      @Override
      public CharacterMailMessage getModel(SynchronizedEveAccount account, long time) {
        return CharacterMailMessage.get(account, time, messageID);
      }

    });
  }

  @Test
  public void testGetMessageIDs() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    // - unread limitation

    CharacterMailMessage existing;
    Set<Long> listCheck = new HashSet<Long>();

    existing = makeMessage(messageID, senderName, sentDate, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(messageID);

    existing = makeMessage(messageID + 10, senderName + " 2", sentDate + 10, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(messageID + 10);

    existing = makeMessage(messageID + 20, senderName + " 3", sentDate + 20, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(messageID + 20);

    existing = makeMessage(messageID + 30, senderName + " 4", sentDate + 30, msgRead);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(messageID + 30);

    // Associated with different account
    existing = makeMessage(messageID, senderName, sentDate, msgRead);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = makeMessage(messageID + 5, senderName + " 0.5", sentDate + 5, msgRead);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = makeMessage(messageID + 3, senderName + " 0.3", sentDate + 3, msgRead);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Unread at given time
    existing = makeMessage(messageID + 40, senderName + " 5", sentDate + 40, false);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);
    listCheck.add(messageID + 40);

    // Verify only unread message is returned
    List<Long> result = CharacterMailMessage.getMessageIDs(testAccount, 8888L, true, 5, 0);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(messageID + 40, result.get(0).longValue());

    // Verify all message IDs are returned
    result = CharacterMailMessage.getMessageIDs(testAccount, 8888L, false, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Long next : result) {
      Assert.assertTrue(listCheck.contains(next));
    }

    // Verify limited set returned
    result = CharacterMailMessage.getMessageIDs(testAccount, 8888L, false, 2, sentDate - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(messageID, result.get(0).longValue());
    Assert.assertEquals(messageID + 10, result.get(1).longValue());

    // Verify continuation ID returns proper set
    result = CharacterMailMessage.getMessageIDs(testAccount, 8888L, false, 100, sentDate + 10);
    Assert.assertEquals(3, result.size());
    Assert.assertEquals(messageID + 20, result.get(0).longValue());
    Assert.assertEquals(messageID + 30, result.get(1).longValue());
    Assert.assertEquals(messageID + 40, result.get(2).longValue());
  }

}
