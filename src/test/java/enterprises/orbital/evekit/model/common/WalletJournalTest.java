package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletJournalTest extends AbstractModelTester<WalletJournal> {

  private final int division = TestBase.getRandomInt(100000000);
  private final long refID = TestBase.getRandomInt(100000000);
  private final long date = TestBase.getRandomInt(100000000);
  private final String refType = TestBase.getRandomText(50);
  private final int firstPartyID = TestBase.getRandomInt();
  private final int secondPartyID = TestBase.getRandomInt();
  private final String argName1 = "test arg name 1";
  private final long argID1 = TestBase.getRandomInt(100000000);
  private final BigDecimal amount = TestBase.getRandomBigDecimal(100000000);
  private final BigDecimal balance = TestBase.getRandomBigDecimal(100000000);
  private final String reason = "test reason";
  private final int taxReceiverID = TestBase.getRandomInt(100000000);
  private final BigDecimal taxAmount = TestBase.getRandomBigDecimal(100000000);
  private final long contextID = TestBase.getRandomLong();
  private final String contextType = TestBase.getRandomText(50);
  private final String description = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<WalletJournal> eol = () -> new WalletJournal(division, refID, date, refType,
                                                                               firstPartyID,
                                                                               secondPartyID, argName1,
                                                                               argID1, amount, balance, reason,
                                                                               taxReceiverID, taxAmount, contextID,
                                                                               contextType, description);

  final ClassUnderTestConstructor<WalletJournal> live = () -> new WalletJournal(division, refID, date, refType,
                                                                                firstPartyID + 1,
                                                                                secondPartyID,
                                                                                argName1, argID1, amount, balance,
                                                                                reason, taxReceiverID, taxAmount,
                                                                                contextID, contextType, description);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new WalletJournal[]{
        new WalletJournal(division + 1, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID + 1, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date + 1, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType + "1", firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID + 1, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID + 1,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1 + "1", argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1 + 1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount.add(BigDecimal.ONE), balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance.add(BigDecimal.ONE), reason, taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason + "1", taxReceiverID, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID + 1, taxAmount,
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount.add(BigDecimal.ONE),
                          contextID, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID + 1, contextType, description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType + "1", description),
        new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                          argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                          contextID, contextType, description + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_JOURNAL));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> WalletJournal.get(account, time, division, refID));
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - journal entries for a different account
    // - journal entries not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    WalletJournal existing;
    Map<Long, WalletJournal> listCheck = new HashMap<>();

    existing = new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(division, refID + 10, date + 10, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(division, refID + 20, date + 20, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(division, refID + 30, date + 30, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(division, refID + 5, date + 5, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(division, refID + 3, date + 3, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify returned
    List<WalletJournal> result = WalletJournal.accessQuery(testAccount,
                                                           0,
                                                           10,
                                                           false,
                                                           AttributeSelector.values(8888L),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any());
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletJournal next : result) {
      long refID = next.getRefID();
      Assert.assertTrue(listCheck.containsKey(refID));
      Assert.assertEquals(listCheck.get(refID), next);
    }

    // Verify limited set returned
    result = WalletJournal.accessQuery(testAccount,
                                       0,
                                       2,
                                       false,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletJournal.accessQuery(testAccount,
                                       0,
                                       100,
                                       false,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(date + 10 + 1, Long.MAX_VALUE),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - journal entries for a different account
    // - journal entries not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    WalletJournal existing;
    Map<Long, WalletJournal> listCheck = new HashMap<>();

    existing = new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(division, refID + 10, date + 10, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(division, refID + 20, date + 20, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(division, refID + 30, date + 30, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(division, refID + 5, date + 5, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(division, refID + 3, date + 3, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobs are returned
    List<WalletJournal> result = WalletJournal.accessQuery(testAccount,
                                                           Long.MAX_VALUE,
                                                           10,
                                                           true,
                                                           AttributeSelector.values(8888L),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any());
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletJournal next : result) {
      long refID = next.getRefID();
      Assert.assertTrue(listCheck.containsKey(refID));
      Assert.assertEquals(listCheck.get(refID), next);
    }

    // Verify limited set returned
    result = WalletJournal.accessQuery(testAccount,
                                       Long.MAX_VALUE,
                                       2,
                                       true,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(0, date + 30),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletJournal.accessQuery(testAccount,
                                       Long.MAX_VALUE,
                                       2,
                                       true,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(0, date + 20 - 1),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 10), result.get(0));
    Assert.assertEquals(listCheck.get(refID), result.get(1));
  }

  @Test
  public void testGetRange() throws Exception {
    // Should exclude:
    // - journal entries for a different account
    // - journal entries not live at the given time
    // - journal entries out of the specified range
    // Need to test:
    // - max results limitation
    WalletJournal existing;
    Map<Long, WalletJournal> listCheck = new HashMap<>();

    existing = new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(division, refID + 10, date + 10, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(division, refID + 20, date + 20, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(division, refID + 30, date + 30, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(division, refID, date, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Out of the specified range
    existing = new WalletJournal(division, refID - 10, date - 10, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);
    existing = new WalletJournal(division, refID + 40, date + 40, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(division, refID + 5, date + 5, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(division, refID + 3, date + 3, refType, firstPartyID, secondPartyID,
                                 argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 contextID, contextType, description);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all entries are returned in ascending order
    List<WalletJournal> result = WalletJournal.accessQuery(testAccount,
                                                           0,
                                                           10,
                                                           false,
                                                           AttributeSelector.values(8888L),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.range(date, date + 30),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any(),
                                                           AttributeSelector.any());
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(2));
    Assert.assertEquals(listCheck.get(refID + 30), result.get(3));

    // Verify all entries are returned in descending order
    result = WalletJournal.accessQuery(testAccount,
                                       Long.MAX_VALUE,
                                       10,
                                       true,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(date, date + 30),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(2));
    Assert.assertEquals(listCheck.get(refID), result.get(3));

    // Verify limited set returned ascending
    result = WalletJournal.accessQuery(testAccount,
                                       0,
                                       2,
                                       false,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(date, date + 30),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));

    // Verify limited set returned descending
    result = WalletJournal.accessQuery(testAccount,
                                       Long.MAX_VALUE,
                                       2,
                                       true,
                                       AttributeSelector.values(8888L),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(date, date + 30),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));

  }

}
