package enterprises.orbital.evekit.model.common;

import java.math.BigDecimal;
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

public class WalletJournalTest extends AbstractModelTester<WalletJournal> {

  final int                                      accountKey    = TestBase.getRandomInt(100000000);
  final long                                     refID         = TestBase.getRandomInt(100000000);
  final long                                     date          = TestBase.getRandomInt(100000000);
  final int                                      refTypeID     = TestBase.getRandomInt(100000000);
  final String                                   ownerName1    = "test owner name 1";
  final long                                     ownerID1      = TestBase.getRandomInt(100000000);
  final String                                   ownerName2    = "test owner name 2";
  final long                                     ownerID2      = TestBase.getRandomInt(100000000);
  final String                                   argName1      = "test arg name 1";
  final long                                     argID1        = TestBase.getRandomInt(100000000);
  final BigDecimal                               amount        = TestBase.getRandomBigDecimal(100000000);
  final BigDecimal                               balance       = TestBase.getRandomBigDecimal(100000000);
  final String                                   reason        = "test reason";
  final long                                     taxReceiverID = TestBase.getRandomInt(100000000);
  final BigDecimal                               taxAmount     = TestBase.getRandomBigDecimal(100000000);
  final int                                      owner1TypeID  = TestBase.getRandomInt(100000000);
  final int                                      owner2TypeID  = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<WalletJournal> eol           = new ClassUnderTestConstructor<WalletJournal>() {

                                                                 @Override
                                                                 public WalletJournal getCUT() {
                                                                   return new WalletJournal(
                                                                       accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1,
                                                                       argID1, amount, balance, reason, taxReceiverID, taxAmount, owner1TypeID, owner2TypeID);
                                                                 }

                                                               };

  final ClassUnderTestConstructor<WalletJournal> live          = new ClassUnderTestConstructor<WalletJournal>() {
                                                                 @Override
                                                                 public WalletJournal getCUT() {
                                                                   return new WalletJournal(
                                                                       accountKey, refID, date, refTypeID + 1, ownerName1, ownerID1, ownerName2, ownerID2,
                                                                       argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount, owner1TypeID,
                                                                       owner2TypeID);
                                                                 }

                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<WalletJournal>() {

      @Override
      public WalletJournal[] getVariants() {
        return new WalletJournal[] {
            new WalletJournal(
                accountKey + 1, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID + 1, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date + 1, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID + 1, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1 + " 1", ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1 + 1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2 + " 1", ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2 + 1, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1 + " 1", argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1 + 1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount.add(BigDecimal.TEN), balance, reason,
                taxReceiverID, taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance.add(BigDecimal.TEN), reason,
                taxReceiverID, taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason + " 1", taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID + 1,
                taxAmount, owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount.add(BigDecimal.TEN), owner1TypeID, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID + 1, owner2TypeID),
            new WalletJournal(
                accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
                taxAmount, owner1TypeID, owner2TypeID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_JOURNAL));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<WalletJournal>() {

      @Override
      public WalletJournal getModel(
                                    SynchronizedEveAccount account,
                                    long time) {
        return WalletJournal.get(account, time, accountKey, refID);
      }

    });
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
    Map<Long, WalletJournal> listCheck = new HashMap<Long, WalletJournal>();

    existing = new WalletJournal(
        accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
        owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(
        accountKey, refID + 10, date + 10, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(
        accountKey, refID + 20, date + 20, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(
        accountKey, refID + 30, date + 30, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(
        accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
        owner1TypeID, owner2TypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(
        accountKey, refID + 5, date + 5, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(
        accountKey, refID + 3, date + 3, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<WalletJournal> result = WalletJournal.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletJournal next : result) {
      long refID = next.getRefID();
      Assert.assertTrue(listCheck.containsKey(refID));
      Assert.assertEquals(listCheck.get(refID), next);
    }

    // Verify limited set returned
    result = WalletJournal.getAllForward(testAccount, 8888L, 2, date - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletJournal.getAllForward(testAccount, 8888L, 100, date + 10);
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
    Map<Long, WalletJournal> listCheck = new HashMap<Long, WalletJournal>();

    existing = new WalletJournal(
        accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
        owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(
        accountKey, refID + 10, date + 10, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(
        accountKey, refID + 20, date + 20, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(
        accountKey, refID + 30, date + 30, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(
        accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
        owner1TypeID, owner2TypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(
        accountKey, refID + 5, date + 5, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(
        accountKey, refID + 3, date + 3, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobs are returned
    List<WalletJournal> result = WalletJournal.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletJournal next : result) {
      long refID = next.getRefID();
      Assert.assertTrue(listCheck.containsKey(refID));
      Assert.assertEquals(listCheck.get(refID), next);
    }

    // Verify limited set returned
    result = WalletJournal.getAllBackward(testAccount, 8888L, 2, date + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletJournal.getAllBackward(testAccount, 8888L, 100, date + 20);
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
    Map<Long, WalletJournal> listCheck = new HashMap<Long, WalletJournal>();

    existing = new WalletJournal(
        accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
        owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(
        accountKey, refID + 10, date + 10, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(
        accountKey, refID + 20, date + 20, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(
        accountKey, refID + 30, date + 30, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(
        accountKey, refID, date, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
        owner1TypeID, owner2TypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Out of the specified range
    existing = new WalletJournal(
        accountKey, refID - 10, date - 10, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);
    existing = new WalletJournal(
        accountKey, refID + 40, date + 40, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(
        accountKey, refID + 5, date + 5, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(
        accountKey, refID + 3, date + 3, refTypeID, ownerName1, ownerID1, ownerName2, ownerID2, argName1, argID1, amount, balance, reason, taxReceiverID,
        taxAmount, owner1TypeID, owner2TypeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all entries are returned in ascending order
    List<WalletJournal> result = WalletJournal.getRange(testAccount, 8888L, 10, date, date + 30, true);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(2));
    Assert.assertEquals(listCheck.get(refID + 30), result.get(3));

    // Verify all entries are returned in descending order
    result = WalletJournal.getRange(testAccount, 8888L, 10, date, date + 30, false);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(2));
    Assert.assertEquals(listCheck.get(refID), result.get(3));

    // Verify limited set returned ascending
    result = WalletJournal.getRange(testAccount, 8888L, 2, date, date + 30, true);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));

    // Verify limited set returned descending
    result = WalletJournal.getRange(testAccount, 8888L, 2, date, date + 30, false);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));

  }

}
