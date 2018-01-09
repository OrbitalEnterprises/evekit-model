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

public class LocationTest extends AbstractModelTester<Location> {
  final long                                itemID   = TestBase.getRandomInt(100000000);
  final String                              itemName = "test item name";
  final double                              x        = TestBase.getRandomDouble(100000000);
  final double                              y        = TestBase.getRandomDouble(100000000);
  final double                              z        = TestBase.getRandomDouble(100000000);

  final ClassUnderTestConstructor<Location> eol      = new ClassUnderTestConstructor<Location>() {

                                                       @Override
                                                       public Location getCUT() {
                                                         return new Location(itemID, itemName, x, y, z);
                                                       }

                                                     };

  final ClassUnderTestConstructor<Location> live     = new ClassUnderTestConstructor<Location>() {
                                                       @Override
                                                       public Location getCUT() {
                                                         return new Location(itemID, itemName, x + 1, y, z);
                                                       }

                                                     };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Location>() {

      @Override
      public Location[] getVariants() {
        return new Location[] {
            new Location(itemID + 1, itemName, x, y, z), new Location(itemID, itemName + "x", x, y, z), new Location(itemID, itemName, x + 1, y, z),
            new Location(itemID, itemName, x, y + 1, z), new Location(itemID, itemName, x, y, z + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_LOCATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Location>() {

      @Override
      public Location getModel(
                               SynchronizedEveAccount account,
                               long time) {
        return Location.get(account, time, itemID);
      }

    });
  }

  @Test
  public void testGetAllLocations() throws Exception {
    // Should exclude:
    // - locations for a different account
    // - locations not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Location existing;
    Map<Long, Location> itemCheck = new HashMap<Long, Location>();

    existing = new Location(itemID, itemName, x, y, z);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    itemCheck.put(itemID, existing);

    existing = new Location(itemID + 10, itemName, x, y, z);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    itemCheck.put(itemID + 10, existing);

    existing = new Location(itemID + 20, itemName, x, y, z);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    itemCheck.put(itemID + 20, existing);

    existing = new Location(itemID + 30, itemName, x, y, z);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    itemCheck.put(itemID + 30, existing);

    // Associated with different account
    existing = new Location(itemID, itemName, x, y, z);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Location(itemID + 5, itemName, x, y, z);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Location(itemID + 3, itemName, x, y, z);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all Locations are returned
    List<Location> result = Location.getAllLocations(testAccount, 8888L, 10, 0);
    Assert.assertEquals(itemCheck.size(), result.size());
    for (Location next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(itemCheck.containsKey(itemID));
      Assert.assertEquals(itemCheck.get(itemID), next);
    }

    // Verify limited set returned
    result = Location.getAllLocations(testAccount, 8888L, 2, itemID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(itemCheck.get(itemID), result.get(0));
    Assert.assertEquals(itemCheck.get(itemID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = Location.getAllLocations(testAccount, 8888L, 100, itemID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(itemCheck.get(itemID + 20), result.get(0));
    Assert.assertEquals(itemCheck.get(itemID + 30), result.get(1));
  }

}
