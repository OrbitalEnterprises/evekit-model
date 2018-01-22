package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueprintTest extends AbstractModelTester<Blueprint> {
  private final long itemID = TestBase.getRandomInt(100000000);
  private final long locationID = TestBase.getRandomInt(100000000);
  private final String locationFlag = TestBase.getRandomText(50);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int quantity = TestBase.getRandomInt(100000000);
  private final int timeEfficiency = TestBase.getRandomInt(100000000);
  private final int materialEfficiency = TestBase.getRandomInt(100000000);
  private final int runs = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<Blueprint> eol = () -> new Blueprint(
      itemID, locationID, locationFlag, typeID, quantity, timeEfficiency,
      materialEfficiency, runs);
  private final ClassUnderTestConstructor<Blueprint> live = () -> new Blueprint(
      itemID, locationID + 1, locationFlag, typeID, quantity, timeEfficiency,
      materialEfficiency, runs);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Blueprint[]{
        new Blueprint(itemID + 1, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency, runs),
        new Blueprint(itemID, locationID + 1, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency, runs),
        new Blueprint(itemID, locationID, locationFlag + "1", typeID, quantity, timeEfficiency, materialEfficiency,
                      runs),
        new Blueprint(itemID, locationID, locationFlag, typeID + 1, quantity, timeEfficiency, materialEfficiency, runs),
        new Blueprint(itemID, locationID, locationFlag, typeID, quantity + 1, timeEfficiency, materialEfficiency, runs),
        new Blueprint(itemID, locationID, locationFlag, typeID, quantity, timeEfficiency + 1, materialEfficiency, runs),
        new Blueprint(itemID, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency + 1, runs),
        new Blueprint(itemID, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency, runs + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_BLUEPRINTS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Blueprint.get(account, time, itemID));
  }

  @Test
  public void testGetAllBlueprints() throws Exception {
    // Should exclude:
    // - blueprints for a different account
    // - blueprints not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Blueprint existing;
    Map<Long, Blueprint> listCheck = new HashMap<>();

    existing = new Blueprint(itemID, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency,
                             runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID, existing);

    existing = new Blueprint(itemID + 10, locationID, locationFlag, typeID, quantity, timeEfficiency,
                             materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Blueprint(itemID + 20, locationID, locationFlag, typeID, quantity, timeEfficiency,
                             materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Blueprint(itemID + 30, locationID, locationFlag, typeID, quantity, timeEfficiency,
                             materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 30, existing);

    // Associated with different account
    existing = new Blueprint(itemID, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency,
                             runs);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Blueprint(itemID + 5, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency,
                             runs);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Blueprint(itemID + 3, locationID, locationFlag, typeID, quantity, timeEfficiency, materialEfficiency,
                             runs);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all blueprints are returned
    List<Blueprint> result = Blueprint.getAllBlueprints(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Blueprint next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

    // Verify limited set returned
    result = Blueprint.getAllBlueprints(testAccount, 8888L, 2, itemID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(itemID), result.get(0));
    Assert.assertEquals(listCheck.get(itemID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = Blueprint.getAllBlueprints(testAccount, 8888L, 100, itemID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(itemID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(itemID + 30), result.get(1));
  }

}
