package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningObserverTest extends AbstractModelTester<MiningObserver> {
  private final long observerID = TestBase.getRandomLong();
  private final String observerType = TestBase.getRandomText(50);
  private final long lastUpdated = TestBase.getRandomLong();

  final ClassUnderTestConstructor<MiningObserver> eol = () -> new MiningObserver(
      observerID, observerType, lastUpdated);

  final ClassUnderTestConstructor<MiningObserver> live = () -> new MiningObserver(
      observerID, observerType, lastUpdated + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MiningObserver[]{
        new MiningObserver(observerID + 1, observerType, lastUpdated),
        new MiningObserver(observerID, observerType + "1", lastUpdated),
        new MiningObserver(observerID, observerType, lastUpdated + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MiningObserver.get(account, time, observerID));
  }

  @Test
  public void testGetAllMiningObservers() throws Exception {
    // Should exclude:
    // - mining observers for a different account
    // - mining observers not live at the given time
    MiningObserver existing;
    Map<Long, MiningObserver> listCheck = new HashMap<>();

    existing = new MiningObserver(observerID, observerType, lastUpdated);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(observerID, existing);

    existing = new MiningObserver(observerID + 10, observerType, lastUpdated + 10);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(observerID + 10, existing);

    existing = new MiningObserver(observerID + 20, observerType, lastUpdated + 20);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(observerID + 20, existing);

    // Associated with different account
    existing = new MiningObserver(observerID, observerType, lastUpdated);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MiningObserver(observerID + 5, observerType, lastUpdated + 5);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MiningObserver(observerID + 3, observerType, lastUpdated + 3);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<MiningObserver> result = CachedData.retrieveAll(8888L,
                                                         (contid, at) -> MiningObserver.accessQuery(testAccount,
                                                                                                    contid,
                                                                                                    1000,
                                                                                                    false, at,
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (MiningObserver next : result) {
      Long key = next.getObserverID();
      Assert.assertTrue(listCheck.containsKey(key));
      Assert.assertEquals(listCheck.get(key), next);
    }
  }

}
