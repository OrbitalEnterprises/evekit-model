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

public class KillTest extends AbstractModelTester<Kill> {

  final long                            killID        = TestBase.getRandomInt(100000000);
  final long                            killTime      = TestBase.getRandomInt(100000000);
  final int                             moonID        = TestBase.getRandomInt(100000000);
  final long                            solarSystemID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Kill> eol           = new ClassUnderTestConstructor<Kill>() {

                                                        @Override
                                                        public Kill getCUT() {
                                                          return new Kill(killID, killTime, moonID, solarSystemID);
                                                        }

                                                      };

  final ClassUnderTestConstructor<Kill> live          = new ClassUnderTestConstructor<Kill>() {
                                                        @Override
                                                        public Kill getCUT() {
                                                          return new Kill(killID, killTime, moonID + 1, solarSystemID);
                                                        }

                                                      };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Kill>() {

      @Override
      public Kill[] getVariants() {
        return new Kill[] {
            new Kill(killID + 1, killTime, moonID, solarSystemID), new Kill(killID, killTime + 1, moonID, solarSystemID),
            new Kill(killID, killTime, moonID + 1, solarSystemID), new Kill(killID, killTime, moonID, solarSystemID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Kill>() {

      @Override
      public Kill getModel(SynchronizedEveAccount account, long time) {
        return Kill.get(account, time, killID);
      }

    });
  }

  @Test
  public void testGetKillForward() throws Exception {
    // Should exclude:
    // - kills for a different account
    // - kills not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Kill existing;
    Map<Long, Kill> listCheck = new HashMap<Long, Kill>();

    existing = new Kill(killID, killTime, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID, existing);

    existing = new Kill(killID + 10, killTime + 10, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 10, existing);

    existing = new Kill(killID + 20, killTime + 20, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 20, existing);

    existing = new Kill(killID + 30, killTime + 30, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 30, existing);

    // Associated with different account
    existing = new Kill(killID, killTime, moonID, solarSystemID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Kill(killID + 5, killTime, moonID, solarSystemID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Kill(killID + 3, killTime, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<Kill> result = Kill.getKillsForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Kill next : result) {
      long killID = next.getKillID();
      Assert.assertTrue(listCheck.containsKey(killID));
      Assert.assertEquals(listCheck.get(killID), next);
    }

    // Verify limited set returned
    result = Kill.getKillsForward(testAccount, 8888L, 2, killTime - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(killID), result.get(0));
    Assert.assertEquals(listCheck.get(killID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = Kill.getKillsForward(testAccount, 8888L, 100, killTime + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(killID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(killID + 30), result.get(1));

  }

  @Test
  public void testGetKillsBackward() throws Exception {
    // Should exclude:
    // - kills for a different account
    // - kills not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Kill existing;
    Map<Long, Kill> listCheck = new HashMap<Long, Kill>();

    existing = new Kill(killID, killTime, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID, existing);

    existing = new Kill(killID + 10, killTime + 10, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 10, existing);

    existing = new Kill(killID + 20, killTime + 20, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 20, existing);

    existing = new Kill(killID + 30, killTime + 30, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 30, existing);

    // Associated with different account
    existing = new Kill(killID, killTime, moonID, solarSystemID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Kill(killID + 5, killTime, moonID, solarSystemID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Kill(killID + 3, killTime, moonID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<Kill> result = Kill.getKillsBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Kill next : result) {
      long killID = next.getKillID();
      Assert.assertTrue(listCheck.containsKey(killID));
      Assert.assertEquals(listCheck.get(killID), next);
    }

    // Verify limited set returned
    result = Kill.getKillsBackward(testAccount, 8888L, 2, killTime + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(killID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(killID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = Kill.getKillsBackward(testAccount, 8888L, 100, killTime + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(killID + 10), result.get(0));
    Assert.assertEquals(listCheck.get(killID), result.get(1));

  }

}
