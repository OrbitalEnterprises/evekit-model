package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CharacterMailMessageTest extends AbstractModelTester<CharacterMailMessage> {

  private final long messageID = TestBase.getRandomInt(100000000);
  private final int senderID = TestBase.getRandomInt(100000000);
  private final long sentDate = TestBase.getRandomInt(100000000);
  private final String title = "test title";
  private final boolean msgRead = true;
  private final Set<Integer> labels = new HashSet<>();
  private final Set<MailMessageRecipient> recipients = new HashSet<>();
  private final String body = TestBase.getRandomText(1000);
  private final String[] recipientTypes;

  public CharacterMailMessageTest() {
    int numLabels = TestBase.getRandomInt(5) + 10;
    int numRecipients = TestBase.getRandomInt(10) + 10;
    for (int i = 0; i < numLabels; i++) {
      labels.add(TestBase.getUniqueRandomInteger());
    }
    recipientTypes = new String[]{"alliance", "character", "corporation", "mailing_list"};
    for (int i = 0; i < numRecipients; i++) {
      String rt = recipientTypes[TestBase.getRandomInt(recipientTypes.length)];
      int ri = TestBase.getUniqueRandomInteger();
      recipients.add(new MailMessageRecipient(rt, ri));
    }
  }

  private CharacterMailMessage makeMessage(long mid, int sid, int[] lbls, String[] rts, int[] rtis) {
    Set<Integer> labelSet = new HashSet<>();
    for (int lbl : lbls) {
      labelSet.add(lbl);
    }
    Set<MailMessageRecipient> recipientSet = new HashSet<>();
    for (int i = 0; i < rts.length; i++) {
      recipientSet.add(new MailMessageRecipient(rts[i], rtis[i]));
    }
    return new CharacterMailMessage(mid, sid, sentDate, title, msgRead, labelSet, recipientSet, body);
  }

  final ClassUnderTestConstructor<CharacterMailMessage> eol = () -> new CharacterMailMessage(messageID, senderID,
                                                                                             sentDate, title, msgRead,
                                                                                             labels, recipients, body);

  final ClassUnderTestConstructor<CharacterMailMessage> live = () -> new CharacterMailMessage(messageID, senderID + 1,
                                                                                              sentDate, title, msgRead,
                                                                                              labels, recipients, body);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> {
      Set<Integer> labelCopy = new HashSet<>(labels);
      labelCopy.add(TestBase.getUniqueRandomInteger());
      Set<MailMessageRecipient> recipientCopy = new HashSet<>(recipients);
      String rt = recipientTypes[TestBase.getRandomInt(recipientTypes.length)];
      int ri = TestBase.getUniqueRandomInteger();
      recipientCopy.add(new MailMessageRecipient(rt, ri));
      return new CharacterMailMessage[]{
          new CharacterMailMessage(messageID + 1, senderID, sentDate, title, msgRead, labels, recipients, body),
          new CharacterMailMessage(messageID, senderID + 1, sentDate, title, msgRead, labels, recipients, body),
          new CharacterMailMessage(messageID, senderID, sentDate + 1, title, msgRead, labels, recipients, body),
          new CharacterMailMessage(messageID, senderID, sentDate, title + "1", msgRead, labels, recipients, body),
          new CharacterMailMessage(messageID, senderID, sentDate, title, !msgRead, labels, recipients, body),
          new CharacterMailMessage(messageID, senderID, sentDate, title, msgRead, labelCopy, recipients, body),
          new CharacterMailMessage(messageID, senderID, sentDate, title, msgRead, labels, recipientCopy, body),
          new CharacterMailMessage(messageID, senderID, sentDate, title, msgRead, labels, recipients, body + "1"),
      };
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterMailMessage.get(account, time, messageID));
  }

  @Test
  public void testSelectByLabel() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // Need to test:
    // - messages with a specific label
    // - messages without a specific label

    CharacterMailMessage existing, sample;

    int[] lbls = new int[]{1, 2, 3};
    String[] rts = new String[]{"alliance", "character", "corporation", "mailing_list"};
    int[] ris = new int[]{1, 2, 3, 4};

    existing = makeMessage(messageID, senderID, lbls, rts, ris);
    existing.setup(testAccount, 7777L);
    sample = CachedData.update(existing);

    // Different label set
    existing = makeMessage(messageID + 10, senderID + 10, new int[]{4, 5, 6}, rts, ris);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = makeMessage(messageID, senderID, lbls, rts, ris);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = makeMessage(messageID + 5, senderID + 5, lbls, rts, ris);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = makeMessage(messageID + 3, senderID + 3, lbls, rts, ris);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify message with requested label is selected
    List<CharacterMailMessage> check = CachedData.retrieveAll(8888L,
                                                              (contid, at) -> CharacterMailMessage.accessQuery(
                                                                  testAccount,
                                                                  contid,
                                                                  1000,
                                                                  false,
                                                                  at,
                                                                  AttributeSelector.values(messageID),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.values(2, 3, 4, 5),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.values(5, 6),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());

    // Verify message with requested label is selected by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(0, 10),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(10, 100),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());

  }

  @Test
  public void testSelectByRecipientType() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // Need to test:
    // - messages with a specific recipient type
    // - messages without a specific recipient type

    CharacterMailMessage existing, sample;

    int[] lbls = new int[]{1, 2, 3};
    String[] rts = new String[]{"alliance", "character"};
    int[] ris = new int[]{1, 2, 3, 4};

    existing = makeMessage(messageID, senderID, lbls, rts, ris);
    existing.setup(testAccount, 7777L);
    sample = CachedData.update(existing);

    // Different label set
    existing = makeMessage(messageID + 10, senderID + 10, new int[]{4, 5, 6}, rts, ris);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = makeMessage(messageID, senderID, lbls, rts, ris);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = makeMessage(messageID + 5, senderID + 5, lbls, rts, ris);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = makeMessage(messageID + 3, senderID + 3, lbls, rts, ris);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify message with requested label is selected
    List<CharacterMailMessage> check = CachedData.retrieveAll(8888L,
                                                              (contid, at) -> CharacterMailMessage.accessQuery(
                                                                  testAccount,
                                                                  contid,
                                                                  1000,
                                                                  false,
                                                                  at,
                                                                  AttributeSelector.values(messageID),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.values("alliance", "corporation"),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.values("corporation", "mailing_list"),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());

    // Verify message with requested label is selected by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range("a", "z"),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range("q", "z"),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());

    // Verify message with requested label is selected by "like"
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.like("char%"),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.like("corp%"),
                                       AttributeSelector.any(),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());

  }

  @Test
  public void testSelectByRecipientID() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // Need to test:
    // - messages with a specific recipient ID
    // - messages without a specific recipient ID

    CharacterMailMessage existing, sample;

    int[] lbls = new int[]{1, 2, 3};
    String[] rts = new String[]{"alliance", "character"};
    int[] ris = new int[]{1, 2};

    existing = makeMessage(messageID, senderID, lbls, rts, ris);
    existing.setup(testAccount, 7777L);
    sample = CachedData.update(existing);

    // Different label set
    existing = makeMessage(messageID + 10, senderID + 10, new int[]{4, 5, 6}, rts, ris);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = makeMessage(messageID, senderID, lbls, rts, ris);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = makeMessage(messageID + 5, senderID + 5, lbls, rts, ris);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = makeMessage(messageID + 3, senderID + 3, lbls, rts, ris);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify message with requested recipient ID is selected
    List<CharacterMailMessage> check = CachedData.retrieveAll(8888L,
                                                              (contid, at) -> CharacterMailMessage.accessQuery(
                                                                  testAccount,
                                                                  contid,
                                                                  1000,
                                                                  false,
                                                                  at,
                                                                  AttributeSelector.values(messageID),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.any(),
                                                                  AttributeSelector.values(2, 6, 7),
                                                                  AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.values(6, 7),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());

    // Verify message with requested recipient ID is selected by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(0, 10),
                                       AttributeSelector.any()));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no messages match by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> CharacterMailMessage.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.values(messageID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(5, 10),
                                       AttributeSelector.any()));
    Assert.assertEquals(0, check.size());
  }


}
