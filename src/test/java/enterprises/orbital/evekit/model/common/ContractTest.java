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

public class ContractTest extends AbstractModelTester<Contract> {

  private final int                                contractID     = TestBase.getRandomInt(100000000);
  private final int                                issuerID       = TestBase.getRandomInt(100000000);
  private final int                                issuerCorpID   = TestBase.getRandomInt(100000000);
  private final int                                assigneeID     = TestBase.getRandomInt(100000000);
  private final int                                acceptorID     = TestBase.getRandomInt(100000000);
  private final long                                startStationID = TestBase.getRandomInt(100000000);
  private final long                                endStationID   = TestBase.getRandomInt(100000000);
  private final String                              type           = "test type";
  private final String                              status         = "test status";
  private final String                              title          = "test title";
  private final boolean                             forCorp        = false;
  private final String                              availability   = "test availability";
  private final long                                dateIssued     = TestBase.getRandomInt(100000000);
  private final long                                dateExpired    = TestBase.getRandomInt(100000000);
  private final long                                dateAccepted   = TestBase.getRandomInt(100000000);
  private final int                                 numDays        = TestBase.getRandomInt(100000000);
  private final long                                dateCompleted  = TestBase.getRandomInt(100000000);
  private final BigDecimal                          price          = TestBase.getRandomBigDecimal(100000000);
  private final BigDecimal                          reward         = TestBase.getRandomBigDecimal(100000000);
  private final BigDecimal                          collateral     = TestBase.getRandomBigDecimal(100000000);
  private final BigDecimal                          buyout         = TestBase.getRandomBigDecimal(100000000);
  private final double                              volume         = TestBase.getRandomDouble(100000000);

  final ClassUnderTestConstructor<Contract> eol            = () -> new Contract(
      contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID,
      type, status, title, forCorp, availability, dateIssued, dateExpired, dateAccepted, numDays,
      dateCompleted, price, reward, collateral, buyout, volume);

  final ClassUnderTestConstructor<Contract> live           = () -> new Contract(
      contractID, issuerID + 1, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID,
      type, status, title, forCorp, availability, dateIssued, dateExpired, dateAccepted, numDays,
      dateCompleted, price, reward, collateral, buyout, volume);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new Contract[] {
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
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Contract.get(account, time, contractID));
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
    Map<Integer, Contract> listCheck = new HashMap<>();

    existing = new Contract(
        contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(contractID, existing);

    existing = new Contract(
        contractID + 10, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(contractID + 10, existing);

    existing = new Contract(
        contractID + 20, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(contractID + 20, existing);

    existing = new Contract(
        contractID + 30, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(contractID + 30, existing);

    // Associated with different account
    existing = new Contract(
        contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Contract(
        contractID + 5, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Contract(
        contractID + 3, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID, endStationID, type, status, title, forCorp, availability, dateIssued,
        dateExpired, dateAccepted, numDays, dateCompleted, price, reward, collateral, buyout, volume);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contracts are returned
    List<Contract> result = CachedData.retrieveAll(8888L, (long contid, AttributeSelector ats) ->
        Contract.accessQuery(testAccount, contid, 1000, false, ats, AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                             AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Contract next : result) {
      int contractID = next.getContractID();
      Assert.assertTrue(listCheck.containsKey(contractID));
      Assert.assertEquals(listCheck.get(contractID), next);
    }
  }


}
