package enterprises.orbital.evekit.model.common;

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

public class BlueprintTest extends AbstractModelTester<Blueprint> {
  final long                                 itemID             = TestBase.getRandomInt(100000000);
  final long                                 locationID         = TestBase.getRandomInt(100000000);
  final int                                  typeID             = TestBase.getRandomInt(100000000);
  final String                               typeName           = "test type name";
  final int                                  flagID             = TestBase.getRandomInt(100000000);
  final int                                  quantity           = TestBase.getRandomInt(100000000);
  final int                                  timeEfficiency     = TestBase.getRandomInt(100000000);
  final int                                  materialEfficiency = TestBase.getRandomInt(100000000);
  final int                                  runs               = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Blueprint> eol                = new ClassUnderTestConstructor<Blueprint>() {

                                                                  @Override
                                                                  public Blueprint getCUT() {
                                                                    return new Blueprint(
                                                                        itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency,
                                                                        materialEfficiency, runs);
                                                                  }

                                                                };

  final ClassUnderTestConstructor<Blueprint> live               = new ClassUnderTestConstructor<Blueprint>() {
                                                                  @Override
                                                                  public Blueprint getCUT() {
                                                                    return new Blueprint(
                                                                        itemID, locationID + 1, typeID, typeName, flagID, quantity, timeEfficiency,
                                                                        materialEfficiency, runs);
                                                                  }

                                                                };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Blueprint>() {

      @Override
      public Blueprint[] getVariants() {
        return new Blueprint[] {
            new Blueprint(itemID + 1, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs),
            new Blueprint(itemID, locationID + 1, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs),
            new Blueprint(itemID, locationID, typeID + 1, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs),
            new Blueprint(itemID, locationID, typeID, typeName + " 1", flagID, quantity, timeEfficiency, materialEfficiency, runs),
            new Blueprint(itemID, locationID, typeID, typeName, flagID + 1, quantity, timeEfficiency, materialEfficiency, runs),
            new Blueprint(itemID, locationID, typeID, typeName, flagID, quantity + 1, timeEfficiency, materialEfficiency, runs),
            new Blueprint(itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency + 1, materialEfficiency, runs),
            new Blueprint(itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency + 1, runs),
            new Blueprint(itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_BLUEPRINTS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Blueprint>() {

      @Override
      public Blueprint getModel(SynchronizedEveAccount account, long time) {
        return Blueprint.get(account, time, itemID);
      }

    });
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
    Map<Long, Blueprint> listCheck = new HashMap<Long, Blueprint>();

    existing = new Blueprint(itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID, existing);

    existing = new Blueprint(itemID + 10, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Blueprint(itemID + 20, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Blueprint(itemID + 30, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 30, existing);

    // Associated with different account
    existing = new Blueprint(itemID, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Blueprint(itemID + 5, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Blueprint(itemID + 3, locationID, typeID, typeName, flagID, quantity, timeEfficiency, materialEfficiency, runs);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

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
