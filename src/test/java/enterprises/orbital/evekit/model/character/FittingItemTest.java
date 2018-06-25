package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FittingItemTest extends AbstractModelTester<FittingItem> {
  private final int fittingID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int flag = TestBase.getRandomInt(100000000);
  private final int quantity = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FittingItem> eol = () -> new FittingItem(
      fittingID, typeID, flag, quantity);

  final ClassUnderTestConstructor<FittingItem> live = () -> new FittingItem(
      fittingID, typeID, flag, quantity + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new FittingItem[]{
        new FittingItem(fittingID + 1, typeID, flag, quantity),
        new FittingItem(fittingID, typeID + 1, flag, quantity),
        new FittingItem(fittingID, typeID, flag + 1, quantity),
        new FittingItem(fittingID, typeID, flag, quantity + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_FITTINGS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> FittingItem.get(account, time, fittingID, typeID, flag));
  }

  @Test
  public void testGetAllFittingItems() throws Exception {
    // Should exclude:
    // - fitting items for a different account
    // - fitting items not live at the given time
    FittingItem existing;
    Map<Pair<Integer, Integer>, FittingItem> listCheck = new HashMap<>();

    existing = new FittingItem(fittingID, typeID, flag, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fittingID, typeID), existing);

    existing = new FittingItem(fittingID, typeID + 10, flag, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fittingID, typeID + 10), existing);

    existing = new FittingItem(fittingID + 20, typeID, flag, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fittingID + 20, typeID), existing);

    // Associated with different account
    existing = new FittingItem(fittingID, typeID, flag, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new FittingItem(fittingID + 5, typeID, flag, quantity);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new FittingItem(fittingID + 3, typeID, flag, quantity);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<FittingItem> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> FittingItem.accessQuery(testAccount, contid, 1000,
                                                                                              false, at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (FittingItem next : result) {
      Assert.assertTrue(listCheck.containsKey(Pair.of(next.getFittingID(), next.getTypeID())));
      Assert.assertEquals(listCheck.get(Pair.of(next.getFittingID(), next.getTypeID())), next);
    }
  }

}
