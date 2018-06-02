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

public class AssetTest extends AbstractModelTester<Asset> {

  private final long itemID = TestBase.getRandomInt(100000000);
  private final long locationID = TestBase.getRandomInt(100000000);
  private final String locationType = TestBase.getRandomText(50);
  private final String locationFlag = TestBase.getRandomText(50);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int quantity = TestBase.getRandomInt(100000000);
  private final boolean singleton = false;
  private final String blueprintType = TestBase.getRandomText(50);
  private final boolean blueprintCopy = TestBase.getRandomBoolean();

  private final ClassUnderTestConstructor<Asset> eol = () -> new Asset(itemID, locationID, locationType, locationFlag,
                                                                       typeID, quantity, singleton, blueprintType,
                                                                       blueprintCopy);
  private final ClassUnderTestConstructor<Asset> live = () -> new Asset(itemID, locationID + 1, locationType,
                                                                        locationFlag, typeID, quantity, singleton,
                                                                        blueprintType, blueprintCopy);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Asset[]{
        new Asset(itemID + 1, locationID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID + 1, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID, locationType + "1", locationFlag, typeID, quantity, singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID, locationType, locationFlag + "1", typeID, quantity, singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID, locationType, locationFlag, typeID + 1, quantity, singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity + 1, singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity, !singleton, blueprintType,
                  blueprintCopy),
        new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity, singleton, blueprintType + "1",
                  blueprintCopy),
        new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                  !blueprintCopy)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Asset.get(account, time, itemID));
  }

  @Test
  public void testGetAllAssets() throws Exception {
    // Should exclude:
    // - assets for a different account
    // - assets not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Asset existing;
    Map<Long, Asset> listCheck = new HashMap<>();

    existing = new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID, existing);

    existing = new Asset(itemID + 10, locationID, locationType, locationFlag, typeID, quantity, singleton,
                         blueprintType, blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Asset(itemID + 20, locationID, locationType, locationFlag, typeID, quantity, singleton,
                         blueprintType, blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Asset(itemID + 30, locationID, locationType, locationFlag, typeID, quantity, singleton,
                         blueprintType, blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 30, existing);

    // Associated with different account
    existing = new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Asset(itemID + 5, locationID, locationType, locationFlag, typeID, quantity, singleton,
                         blueprintType, blueprintCopy);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Asset(itemID + 3, locationID, locationType, locationFlag, typeID, quantity, singleton,
                         blueprintType, blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all assets are returned
    List<Asset> result = Asset.getAllAssets(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Asset next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

    // Verify limited set returned
    result = Asset.getAllAssets(testAccount, 8888L, 2, itemID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(itemID), result.get(0));
    Assert.assertEquals(listCheck.get(itemID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = Asset.getAllAssets(testAccount, 8888L, 100, itemID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(itemID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(itemID + 30), result.get(1));
  }

  @Test
  public void testGetContainedAssets() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // - assets contained by a different container
    // Need to test:
    // - max results limitation
    // - continuation ID
    Asset existing;
    Map<Long, Asset> listCheck = new HashMap<>();

    existing = new Asset(itemID, locationID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    existing = new Asset(itemID + 10, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Asset(itemID + 20, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Asset(itemID + 30, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 30, existing);

    existing = new Asset(itemID + 40, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 40, existing);

    // Contained by a different asset
    existing = new Asset(itemID + 45, itemID + 10, locationType, locationFlag, typeID, quantity, singleton,
                         blueprintType, blueprintCopy);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = new Asset(itemID + 10, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Asset(itemID + 5, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Asset(itemID + 3, itemID, locationType, locationFlag, typeID, quantity, singleton, blueprintType,
                         blueprintCopy);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contained assets are returned
    List<Asset> result = Asset.getContainedAssets(testAccount, itemID, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Asset next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

    // Verify limited set returned
    result = Asset.getContainedAssets(testAccount, itemID, 8888L, 2, itemID + 10 - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(itemID + 10), result.get(0));
    Assert.assertEquals(listCheck.get(itemID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = Asset.getContainedAssets(testAccount, itemID, 8888L, 100, itemID + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(itemID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(itemID + 40), result.get(1));
  }

}
