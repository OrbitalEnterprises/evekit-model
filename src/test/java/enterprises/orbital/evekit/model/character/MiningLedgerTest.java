package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningLedgerTest extends AbstractModelTester<MiningLedger> {
  private final long date = TestBase.getRandomLong();
  private final int solarSystemID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final long quantity = TestBase.getRandomLong();

  final ClassUnderTestConstructor<MiningLedger> eol = () -> new MiningLedger(
      date, solarSystemID, typeID, quantity);

  final ClassUnderTestConstructor<MiningLedger> live = () -> new MiningLedger(
      date, solarSystemID, typeID, quantity + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MiningLedger[]{
        new MiningLedger(date + 1, solarSystemID, typeID, quantity),
        new MiningLedger(date, solarSystemID + 1, typeID, quantity),
        new MiningLedger(date, solarSystemID, typeID + 1, quantity),
        new MiningLedger(date, solarSystemID, typeID, quantity + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MiningLedger.get(account, time, date, solarSystemID, typeID));
  }

  @Test
  public void testGetAllMiningLedgers() throws Exception {
    // Should exclude:
    // - mining ledgers for a different account
    // - mining ledgers not live at the given time
    MiningLedger existing;
    Map<Triple<Long, Integer, Integer>, MiningLedger> listCheck = new HashMap<>();

    existing = new MiningLedger(date, solarSystemID, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(date, solarSystemID, typeID), existing);

    existing = new MiningLedger(date, solarSystemID + 10, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(date, solarSystemID + 10, typeID), existing);

    existing = new MiningLedger(date, solarSystemID + 20, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(date, solarSystemID + 20, typeID), existing);

    // Associated with different account
    existing = new MiningLedger(date, solarSystemID, typeID, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MiningLedger(date, solarSystemID + 5, typeID, quantity);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MiningLedger(date, solarSystemID + 3, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<MiningLedger> result = CachedData.retrieveAll(8888L,
                                                       (contid, at) -> MiningLedger.accessQuery(testAccount, contid,
                                                                                                1000,
                                                                                                false, at,
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (MiningLedger next : result) {
      Triple<Long, Integer, Integer> key = Triple.of(next.getDate(), next.getSolarSystemID(), next.getTypeID());
      Assert.assertTrue(listCheck.containsKey(key));
      Assert.assertEquals(listCheck.get(key), next);
    }
  }

}
