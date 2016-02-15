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
import enterprises.orbital.evekit.model.common.Contract;

public class ContractTest extends AbstractModelTester<Contract> {

  final long                                contractID     = TestBase.getRandomInt(100000000);
  final long                                issuerID       = TestBase.getRandomInt(100000000);
  final long                                issuerCorpID   = TestBase.getRandomInt(100000000);
  final long                                assigneeID     = TestBase.getRandomInt(100000000);
  final long                                acceptorID     = TestBase.getRandomInt(100000000);
  final int                                 startStationID = TestBase.getRandomInt(100000000);
  final int                                 endStationID   = TestBase.getRandomInt(100000000);
  final String                              type           = "test type";
  final String                              status         = "test status";
  final String                              title          = "test title";
  final boolean                             forCorp        = false;
  final String                              availability   = "test availability";
  final long                                dateIssued     = TestBase.getRandomInt(100000000);
  final long                                dateExpired    = TestBase.getRandomInt(100000000);
  final long                                dateAccepted   = TestBase.getRandomInt(100000000);
  final int                                 numDays        = TestBase.getRandomInt(100000000);
  final long                                dateCompleted  = TestBase.getRandomInt(100000000);
  final BigDecimal                          price          = TestBase.getRandomBigDecimal(100000000);
  final BigDecimal                          reward         = TestBase.getRandomBigDecimal(100000000);
  final BigDecimal                          collateral     = TestBase.getRandomBigDecimal(100000000);
  final BigDecimal                          buyout         = TestBase.getRandomBigDecimal(100000000);
  final long                                volume         = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Contract> eol            = new ClassUnderTestConstructor<Contract>() {

                                                             @Override
                                                             public Contract getCUT() {
                                                               return new Contract(
                                                                   contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID,
                                                                   type, status, title, forCorp, availability, dateIssued, dateExpired, dateAccepted, numDays,
                                                                   dateCompleted, price, reward, collateral, buyout, volume);
                                                             }

                                                           };

  final ClassUnderTestConstructor<Contract> live           = new ClassUnderTestConstructor<Contract>() {
                                                             @Override
                                                             public Contract getCUT() {
                                                               return new Contract(
                                                                   contractID, issuerID + 1, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID,
                                                                   type, status, title, forCorp, availability, dateIssued, dateExpired, dateAccepted, numDays,
                                                                   dateCompleted, price, reward, collateral, buyout, volume);
                                                             }

                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Contract>() {

      @Override
      public Contract[] getVariants() {
        return new Contract[] {
            new Contract(
                contractID + 1, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID + 1, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID + 1, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID + 1, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID + 1, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID + 1, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID + 1, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type + " 1", status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status + " 1", title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title + " 1", forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, !forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability + " 1",
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued + 1, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired + 1, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted + 1, numDays, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays + 1, dateCompleted, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted + 1, price, reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price.add(BigDecimal.TEN), reward, collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward.add(BigDecimal.TEN), collateral, buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral.add(BigDecimal.TEN), buyout, volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout.add(BigDecimal.TEN), volume),
            new Contract(
                contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
                dateIssued, dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Contract>() {

      @Override
      public Contract getModel(SynchronizedEveAccount account, long time) {
        return Contract.get(account, time, contractID);
      }

    });
  }

  @Test
  public void testGetAllContracts() throws Exception {
    // Should exclude:
    // - contracts for a different account
    // - contracts not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Contract existing;
    Map<Long, Contract> listCheck = new HashMap<Long, Contract>();

    existing = new Contract(
        contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID, existing);

    existing = new Contract(
        contractID + 10, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID + 10, existing);

    existing = new Contract(
        contractID + 20, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID + 20, existing);

    existing = new Contract(
        contractID + 30, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID + 30, existing);

    // Associated with different account
    existing = new Contract(
        contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Contract(
        contractID + 5, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Contract(
        contractID + 3, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contacts are returned
    List<Contract> result = Contract.getAllContracts(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Contract next : result) {
      long contractID = next.getContractID();
      Assert.assertTrue(listCheck.containsKey(contractID));
      Assert.assertEquals(listCheck.get(contractID), next);
    }

    // Verify limited set returned
    result = Contract.getAllContracts(testAccount, 8888L, 2, contractID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(contractID), result.get(0));
    Assert.assertEquals(listCheck.get(contractID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = Contract.getAllContracts(testAccount, 8888L, 100, contractID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(contractID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(contractID + 30), result.get(1));
  }

  @Test
  public void testGetAllItemRetrievableContracts() throws Exception {
    // Should exclude:
    // - contracts for a different account
    // - contracts not live at the given time
    // - unretrievable contracts
    // Need to test:
    // - max results limitation
    // - continuation ID
    Contract existing;
    Map<Long, Contract> listCheck = new HashMap<Long, Contract>();
    long threshold = 100000000;

    existing = new Contract(
        contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID, existing);

    existing = new Contract(
        contractID + 10, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID + 10, existing);

    existing = new Contract(
        contractID + 20, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID + 20, existing);

    existing = new Contract(
        contractID + 30, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(contractID + 30, existing);

    // Associated with different account
    existing = new Contract(
        contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Contract with unretrievable items
    existing = new Contract(
        contractID + 40, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability,
        threshold - 90000000, dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Contract(
        contractID + 5, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Contract(
        contractID + 3, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, threshold,
        dateExpired, dateAccepted, 1, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contacts are returned
    List<Contract> result = Contract.getAllItemRetrievableContracts(testAccount, 8888L, 10, 0, threshold);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Contract next : result) {
      long contractID = next.getContractID();
      Assert.assertTrue(listCheck.containsKey(contractID));
      Assert.assertEquals(listCheck.get(contractID), next);
    }

    // Verify limited set returned
    result = Contract.getAllItemRetrievableContracts(testAccount, 8888L, 2, contractID - 1, threshold);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(contractID), result.get(0));
    Assert.assertEquals(listCheck.get(contractID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = Contract.getAllItemRetrievableContracts(testAccount, 8888L, 100, contractID + 10, threshold);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(contractID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(contractID + 30), result.get(1));

  }

}
