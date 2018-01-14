package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletTransactionTest extends AbstractModelTester<WalletTransaction> {

  private final int division = TestBase.getRandomInt(100000000);
  private final long transactionID = TestBase.getRandomInt(100000000);
  private final long date = TestBase.getRandomInt(100000000);
  private final int quantity = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final BigDecimal price = TestBase.getRandomBigDecimal(100000000);
  private final int clientID = TestBase.getRandomInt(100000000);
  private final long locationID = TestBase.getRandomInt(100000000);
  private final boolean isBuy = TestBase.getRandomBoolean();
  private final boolean isPersonal = TestBase.getRandomBoolean();
  private final long journalTransactionID = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<WalletTransaction> eol = () -> new WalletTransaction(
      division, transactionID, date, quantity, typeID, price, clientID,
      locationID, isBuy, isPersonal,
      journalTransactionID);
  private final ClassUnderTestConstructor<WalletTransaction> live = () -> new WalletTransaction(
      division, transactionID, date, quantity + 1, typeID, price,
      clientID, locationID, isBuy, isPersonal,
      journalTransactionID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new WalletTransaction[]{
        new WalletTransaction(
            division + 1, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID + 1, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date + 1, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity + 1, typeID, price, clientID, locationID, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID + 1, price, clientID, locationID, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID, price.add(BigDecimal.TEN), clientID, locationID, isBuy,
            isPersonal, journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID, price, clientID + 1, locationID, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID, price, clientID, locationID + 1, isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID, price, clientID, locationID, !isBuy, isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, !isPersonal,
            journalTransactionID),
        new WalletTransaction(
            division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
            journalTransactionID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_TRANSACTIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> WalletTransaction.get(account, time, division, transactionID));
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - transaction entries for a different account
    // - transaction entries not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    WalletTransaction existing;
    Map<Long, WalletTransaction> listCheck = new HashMap<>();

    existing = new WalletTransaction(
        division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID, existing);

    existing = new WalletTransaction(
        division, transactionID + 10, date + 10, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 10, existing);

    existing = new WalletTransaction(
        division, transactionID + 20, date + 20, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 20, existing);

    existing = new WalletTransaction(
        division, transactionID + 30, date + 30, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 30, existing);

    // Associated with different account
    existing = new WalletTransaction(
        division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletTransaction(
        division, transactionID + 5, date + 5, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletTransaction(
        division, transactionID + 3, date + 3, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all transactions are returned
    List<WalletTransaction> result = WalletTransaction.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletTransaction next : result) {
      long transactionID = next.getTransactionID();
      Assert.assertTrue(listCheck.containsKey(transactionID));
      Assert.assertEquals(listCheck.get(transactionID), next);
    }

    // Verify limited set returned
    result = WalletTransaction.getAllForward(testAccount, 8888L, 2, date - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(transactionID), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletTransaction.getAllForward(testAccount, 8888L, 100, date + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(transactionID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - transaction entries for a different account
    // - transaction entries not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    WalletTransaction existing;
    Map<Long, WalletTransaction> listCheck = new HashMap<>();

    existing = new WalletTransaction(
        division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID, existing);

    existing = new WalletTransaction(
        division, transactionID + 10, date + 10, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 10, existing);

    existing = new WalletTransaction(
        division, transactionID + 20, date + 20, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 20, existing);

    existing = new WalletTransaction(
        division, transactionID + 30, date + 30, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 30, existing);

    // Associated with different account
    existing = new WalletTransaction(
        division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletTransaction(
        division, transactionID + 5, date + 5, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletTransaction(
        division, transactionID + 3, date + 3, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all transactions sare returned
    List<WalletTransaction> result = WalletTransaction.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletTransaction next : result) {
      long transactionID = next.getTransactionID();
      Assert.assertTrue(listCheck.containsKey(transactionID));
      Assert.assertEquals(listCheck.get(transactionID), next);
    }

    // Verify limited set returned
    result = WalletTransaction.getAllBackward(testAccount, 8888L, 2, date + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(transactionID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletTransaction.getAllBackward(testAccount, 8888L, 100, date + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(transactionID + 10), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID), result.get(1));
  }

  @Test
  public void testGetRange() throws Exception {
    // Should exclude:
    // - transaction entries for a different account
    // - transaction entries not live at the given time
    // - transaction entries out of the specified range
    // Need to test:
    // - max results limitation
    WalletTransaction existing;
    Map<Long, WalletTransaction> listCheck = new HashMap<>();

    existing = new WalletTransaction(
        division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID, existing);

    existing = new WalletTransaction(
        division, transactionID + 10, date + 10, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 10, existing);

    existing = new WalletTransaction(
        division, transactionID + 20, date + 20, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 20, existing);

    existing = new WalletTransaction(
        division, transactionID + 30, date + 30, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(transactionID + 30, existing);

    // Associated with different account
    existing = new WalletTransaction(
        division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Out of the specified range
    existing = new WalletTransaction(
        division, transactionID - 10, date - 10, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);
    existing = new WalletTransaction(
        division, transactionID + 40, date + 40, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletTransaction(
        division, transactionID + 5, date + 5, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletTransaction(
        division, transactionID + 3, date + 3, quantity, typeID, price, clientID, locationID, isBuy, isPersonal,
        journalTransactionID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all entries are returned in ascending order
    List<WalletTransaction> result = WalletTransaction.getRange(testAccount, 8888L, 10, date, date + 30, true);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(transactionID), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 10), result.get(1));
    Assert.assertEquals(listCheck.get(transactionID + 20), result.get(2));
    Assert.assertEquals(listCheck.get(transactionID + 30), result.get(3));

    // Verify all entries are returned in descending order
    result = WalletTransaction.getRange(testAccount, 8888L, 10, date, date + 30, false);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(transactionID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 20), result.get(1));
    Assert.assertEquals(listCheck.get(transactionID + 10), result.get(2));
    Assert.assertEquals(listCheck.get(transactionID), result.get(3));

    // Verify limited set returned ascending
    result = WalletTransaction.getRange(testAccount, 8888L, 2, date, date + 30, true);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(transactionID), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 10), result.get(1));

    // Verify limited set returned descending
    result = WalletTransaction.getRange(testAccount, 8888L, 2, date, date + 30, false);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(transactionID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(transactionID + 20), result.get(1));

  }

}
