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
import enterprises.orbital.evekit.model.common.Asset;

public class AssetTest extends AbstractModelTester<Asset> {

  final long                             itemID      = TestBase.getRandomInt(100000000);
  final long                             locationID  = TestBase.getRandomInt(100000000);
  final int                              typeID      = TestBase.getRandomInt(100000000);
  final int                              quantity    = TestBase.getRandomInt(100000000);
  final int                              flag        = TestBase.getRandomInt(100000000);
  final boolean                          singleton   = false;
  final int                              rawQuantity = TestBase.getRandomInt(100000000);
  final long                             container   = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Asset> eol         = new ClassUnderTestConstructor<Asset>() {

                                                       @Override
                                                       public Asset getCUT() {
                                                         return new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
                                                       }

                                                     };

  final ClassUnderTestConstructor<Asset> live        = new ClassUnderTestConstructor<Asset>() {
                                                       @Override
                                                       public Asset getCUT() {
                                                         return new Asset(itemID, locationID + 1, typeID, quantity, flag, singleton, rawQuantity, container);
                                                       }

                                                     };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Asset>() {

      @Override
      public Asset[] getVariants() {
        return new Asset[] {
            new Asset(itemID + 1, locationID, typeID, quantity, flag, singleton, rawQuantity, container),
            new Asset(itemID, locationID + 1, typeID, quantity, flag, singleton, rawQuantity, container),
            new Asset(itemID, locationID, typeID + 1, quantity, flag, singleton, rawQuantity, container),
            new Asset(itemID, locationID, typeID, quantity + 1, flag, singleton, rawQuantity, container),
            new Asset(itemID, locationID, typeID, quantity, flag + 1, singleton, rawQuantity, container),
            new Asset(itemID, locationID, typeID, quantity, flag, !singleton, rawQuantity, container),
            new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity + 1, container),
            new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Asset>() {

      @Override
      public Asset getModel(SynchronizedEveAccount account, long time) {
        return Asset.get(account, time, itemID);
      }

    });
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
    Map<Long, Asset> listCheck = new HashMap<Long, Asset>();

    existing = new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID, existing);

    existing = new Asset(itemID + 10, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Asset(itemID + 20, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Asset(itemID + 30, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 30, existing);

    // Associated with different account
    existing = new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Asset(itemID + 5, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Asset(itemID + 3, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

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
  public void testGetAllAssetsUnlimited() throws Exception {
    // Should exclude:
    // - assets for a different account
    // - assets not live at the given time
    Asset existing;
    Map<Long, Asset> listCheck = new HashMap<Long, Asset>();

    existing = new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID, existing);

    existing = new Asset(itemID + 10, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Asset(itemID + 20, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Asset(itemID + 30, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 30, existing);

    // Associated with different account
    existing = new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Asset(itemID + 5, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Asset(itemID + 3, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all proper keys are returned
    List<Asset> result = Asset.getAllAssets(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Asset next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertTrue(next.equivalent(listCheck.get(itemID)));
    }

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
    Map<Long, Asset> listCheck = new HashMap<Long, Asset>();

    existing = new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    existing = new Asset(itemID + 10, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Asset(itemID + 20, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Asset(itemID + 30, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 30, existing);

    existing = new Asset(itemID + 40, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 40, existing);

    // Contained by a different asset
    existing = new Asset(itemID + 45, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID + 10);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new Asset(itemID + 10, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Asset(itemID + 5, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Asset(itemID + 3, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

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

  @Test
  public void testGetContainedAssetsUnlimited() throws Exception {
    // Should exclude:
    // - assets for a different account
    // - assets not live at the given time
    // - assets contained by a different container
    Asset existing;
    Map<Long, Asset> listCheck = new HashMap<Long, Asset>();

    existing = new Asset(itemID, locationID, typeID, quantity, flag, singleton, rawQuantity, container);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    existing = new Asset(itemID + 10, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 10, existing);

    existing = new Asset(itemID + 20, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 20, existing);

    existing = new Asset(itemID + 30, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 30, existing);

    existing = new Asset(itemID + 40, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(itemID + 40, existing);

    // Contained by a different asset
    existing = new Asset(itemID + 45, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID + 10);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new Asset(itemID + 10, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Asset(itemID + 5, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Asset(itemID + 3, locationID, typeID, quantity, flag, singleton, rawQuantity, itemID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contained assets are returned
    List<Asset> result = Asset.getContainedAssetsUnlimited(testAccount, itemID, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Asset next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertTrue(next.equivalent(listCheck.get(itemID)));
    }
  }

}
