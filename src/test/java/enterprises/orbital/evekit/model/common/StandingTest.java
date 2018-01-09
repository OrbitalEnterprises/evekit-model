package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandingTest extends AbstractModelTester<Standing> {

  final String                              standingEntity = "test standing entity";
  final int                                 fromID         = TestBase.getRandomInt(100000000);
  final String                              fromName       = "test from name";
  final double                              standing       = TestBase.getRandomDouble(100000000);

  final ClassUnderTestConstructor<Standing> eol            = new ClassUnderTestConstructor<Standing>() {

                                                             @Override
                                                             public Standing getCUT() {
                                                               return new Standing(standingEntity, fromID, fromName, standing);
                                                             }

                                                           };

  final ClassUnderTestConstructor<Standing> live           = new ClassUnderTestConstructor<Standing>() {
                                                             @Override
                                                             public Standing getCUT() {
                                                               return new Standing(standingEntity, fromID, fromName, standing + 1);
                                                             }

                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Standing>() {

      @Override
      public Standing[] getVariants() {
        return new Standing[] {
            new Standing(standingEntity + " 1", fromID, fromName, standing), new Standing(standingEntity, fromID + 1, fromName, standing),
            new Standing(standingEntity, fromID, fromName + " 1", standing), new Standing(standingEntity, fromID, fromName, standing + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STANDINGS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Standing>() {

      @Override
      public Standing getModel(SynchronizedEveAccount account, long time) {
        return Standing.get(account, time, standingEntity, fromID);
      }

    });
  }

  @Test
  public void testGetAllStandings() throws Exception {
    // Should exclude:
    // - standings for a different account
    // - standings not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Standing existing;
    Map<Integer, Standing> listCheck = new HashMap<Integer, Standing>();

    existing = new Standing(standingEntity + " 0", fromID, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID, existing);

    existing = new Standing(standingEntity + " 1", fromID + 10, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID + 10, existing);

    existing = new Standing(standingEntity + " 2", fromID + 20, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID + 20, existing);

    existing = new Standing(standingEntity + " 3", fromID + 30, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID + 30, existing);

    // Associated with different account
    existing = new Standing(standingEntity, fromID, fromName, standing);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Standing(standingEntity, fromID + 5, fromName, standing);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Standing(standingEntity, fromID + 3, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<Standing> result = Standing.getAllStandings(testAccount, 8888L, 10, -1);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Standing next : result) {
      int fromID = next.getFromID();
      Assert.assertTrue(listCheck.containsKey(fromID));
      Assert.assertEquals(listCheck.get(fromID), next);
    }

    // Verify limited set returned
    long limit = listCheck.get(fromID).getCid();
    result = Standing.getAllStandings(testAccount, 8888L, 2, limit - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(fromID), result.get(0));
    Assert.assertEquals(listCheck.get(fromID + 10), result.get(1));

    // Verify continuation ID returns proper set
    limit = listCheck.get(fromID + 10).getCid();
    result = Standing.getAllStandings(testAccount, 8888L, 100, limit);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(fromID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(fromID + 30), result.get(1));

  }

  @Test
  public void testGetByEntity() throws Exception {
    // Should exclude:
    // - standings for a different account
    // - standings not live at the given time
    // - standings for a different entity
    // Need to test:
    // - max results limitation
    // - continuation ID
    Standing existing;
    Map<Integer, Standing> listCheck = new HashMap<Integer, Standing>();

    existing = new Standing(standingEntity, fromID, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID, existing);

    existing = new Standing(standingEntity, fromID + 10, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID + 10, existing);

    existing = new Standing(standingEntity, fromID + 20, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID + 20, existing);

    existing = new Standing(standingEntity, fromID + 30, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fromID + 30, existing);

    // Associated with different account
    existing = new Standing(standingEntity, fromID, fromName, standing);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different standing entity
    existing = new Standing(standingEntity + " 1", fromID, fromName, standing);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Standing(standingEntity, fromID + 5, fromName, standing);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Standing(standingEntity, fromID + 3, fromName, standing);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<Standing> result = Standing.getByEntity(testAccount, 8888L, standingEntity, 10, -1);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Standing next : result) {
      int fromID = next.getFromID();
      Assert.assertTrue(listCheck.containsKey(fromID));
      Assert.assertEquals(listCheck.get(fromID), next);
    }

    // Verify limited set returned
    long limit = listCheck.get(fromID).getCid();
    result = Standing.getByEntity(testAccount, 8888L, standingEntity, 2, limit - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(fromID), result.get(0));
    Assert.assertEquals(listCheck.get(fromID + 10), result.get(1));

    // Verify continuation ID returns proper set
    limit = listCheck.get(fromID + 10).getCid();
    result = Standing.getByEntity(testAccount, 8888L, standingEntity, 100, limit);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(fromID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(fromID + 30), result.get(1));

  }

}
