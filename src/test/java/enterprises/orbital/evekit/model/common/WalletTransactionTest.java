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

public class WalletTransactionTest extends AbstractModelTester<WalletTransaction> {

  final int                                          accountKey           = TestBase.getRandomInt(100000000);
  final long                                         transactionID        = TestBase.getRandomInt(100000000);
  final long                                         date                 = TestBase.getRandomInt(100000000);
  final int                                          quantity             = TestBase.getRandomInt(100000000);
  final String                                       typeName             = "test type name";
  final int                                          typeID               = TestBase.getRandomInt(100000000);
  final BigDecimal                                   price                = TestBase.getRandomBigDecimal(100000000);
  final long                                         clientID             = TestBase.getRandomInt(100000000);
  final String                                       clientName           = "test client name";
  final int                                          stationID            = TestBase.getRandomInt(100000000);
  final String                                       stationName          = "test station name";
  final String                                       transactionType      = "test transaction type";
  final String                                       transactionFor       = "test transaction for";
  final long                                         journalTransactionID = TestBase.getRandomInt(100000000);
  final int                                          clientTypeID         = TestBase.getRandomInt(100000000);
  final long                                         characterID          = TestBase.getRandomInt(100000000);
  final String                                       characterName        = "test character name";

  final ClassUnderTestConstructor<WalletTransaction> eol                  = new ClassUnderTestConstructor<WalletTransaction>() {

                                                                            @Override
                                                                            public WalletTransaction getCUT() {
                                                                              return new WalletTransaction(
                                                                                  accountKey, transactionID, date, quantity, typeName, typeID, price, clientID,
                                                                                  clientName, stationID, stationName, transactionType, transactionFor,
                                                                                  journalTransactionID, clientTypeID, characterID, characterName);
                                                                            }

                                                                          };

  final ClassUnderTestConstructor<WalletTransaction> live                 = new ClassUnderTestConstructor<WalletTransaction>() {
                                                                            @Override
                                                                            public WalletTransaction getCUT() {
                                                                              return new WalletTransaction(
                                                                                  accountKey, transactionID, date, quantity + 1, typeName, typeID, price,
                                                                                  clientID, clientName, stationID, stationName, transactionType, transactionFor,
                                                                                  journalTransactionID, clientTypeID, characterID, characterName);
                                                                            }

                                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<WalletTransaction>() {

      @Override
      public WalletTransaction[] getVariants() {
        return new WalletTransaction[] {
            new WalletTransaction(
                accountKey + 1, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID + 1, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date + 1, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity + 1, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName + " 1", typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID + 1, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price.add(BigDecimal.TEN), clientID, clientName, stationID, stationName,
                transactionType, transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID + 1, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName + " 1", stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID + 1, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName + " 1", transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType + " 1",
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor + " 1", journalTransactionID, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID + 1, clientTypeID, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID + 1, characterID, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID + 1, characterName),
            new WalletTransaction(
                accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
                transactionFor, journalTransactionID, clientTypeID, characterID, characterName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_TRANSACTIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<WalletTransaction>() {

      @Override
      public WalletTransaction getModel(
                                        SynchronizedEveAccount account,
                                        long time) {
        return WalletTransaction.get(account, time, transactionID);
      }

    });
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
    Map<Long, WalletTransaction> listCheck = new HashMap<Long, WalletTransaction>();

    existing = new WalletTransaction(
        accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType, transactionFor,
        journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 10, date + 10, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 10, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 20, date + 20, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 20, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 30, date + 30, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 30, existing);

    // Associated with different account
    existing = new WalletTransaction(
        accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType, transactionFor,
        journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new WalletTransaction(
        accountKey, transactionID + 5, date + 5, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new WalletTransaction(
        accountKey, transactionID + 3, date + 3, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

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
    Map<Long, WalletTransaction> listCheck = new HashMap<Long, WalletTransaction>();

    existing = new WalletTransaction(
        accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType, transactionFor,
        journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 10, date + 10, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 10, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 20, date + 20, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 20, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 30, date + 30, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 30, existing);

    // Associated with different account
    existing = new WalletTransaction(
        accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType, transactionFor,
        journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new WalletTransaction(
        accountKey, transactionID + 5, date + 5, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new WalletTransaction(
        accountKey, transactionID + 3, date + 3, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

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
    Map<Long, WalletTransaction> listCheck = new HashMap<Long, WalletTransaction>();

    existing = new WalletTransaction(
        accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType, transactionFor,
        journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 10, date + 10, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 10, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 20, date + 20, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 20, existing);

    existing = new WalletTransaction(
        accountKey, transactionID + 30, date + 30, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(transactionID + 30, existing);

    // Associated with different account
    existing = new WalletTransaction(
        accountKey, transactionID, date, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType, transactionFor,
        journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Out of the specified range
    existing = new WalletTransaction(
        accountKey, transactionID - 10, date - 10, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);
    existing = new WalletTransaction(
        accountKey, transactionID + 40, date + 40, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new WalletTransaction(
        accountKey, transactionID + 5, date + 5, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new WalletTransaction(
        accountKey, transactionID + 3, date + 3, quantity, typeName, typeID, price, clientID, clientName, stationID, stationName, transactionType,
        transactionFor, journalTransactionID, clientTypeID, characterID, characterName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

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
