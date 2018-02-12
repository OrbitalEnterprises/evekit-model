package enterprises.orbital.evekit.model.common;

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

public class KillTest extends AbstractModelTester<Kill> {

  private final int killID = TestBase.getRandomInt(100000000);
  private final long killTime = TestBase.getRandomInt(100000000);
  private final int moonID = TestBase.getRandomInt(100000000);
  private final int solarSystemID = TestBase.getRandomInt(100000000);
  private final int warID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Kill> eol = () -> new Kill(killID, killTime, moonID, solarSystemID, warID);

  final ClassUnderTestConstructor<Kill> live = () -> new Kill(killID, killTime, moonID + 1, solarSystemID, warID);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new Kill[]{
        new Kill(killID + 1, killTime, moonID, solarSystemID, warID),
        new Kill(killID, killTime + 1, moonID, solarSystemID, warID),
        new Kill(killID, killTime, moonID + 1, solarSystemID, warID),
        new Kill(killID, killTime, moonID, solarSystemID + 1, warID),
        new Kill(killID, killTime, moonID, solarSystemID, warID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Kill.get(account, time, killID));
  }

  @Test
  public void testGetKills() throws Exception {
    // Should exclude:
    // - kills for a different account
    // - kills not live at the given time
    Kill existing;
    Map<Integer, Kill> listCheck = new HashMap<>();

    existing = new Kill(killID, killTime, moonID, solarSystemID, warID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID, existing);

    existing = new Kill(killID + 10, killTime + 10, moonID, solarSystemID, warID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 10, existing);

    existing = new Kill(killID + 20, killTime + 20, moonID, solarSystemID, warID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 20, existing);

    existing = new Kill(killID + 30, killTime + 30, moonID, solarSystemID, warID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(killID + 30, existing);

    // Associated with different account
    existing = new Kill(killID, killTime, moonID, solarSystemID, warID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Kill(killID + 5, killTime, moonID, solarSystemID, warID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Kill(killID + 3, killTime, moonID, solarSystemID, warID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<Kill> result = CachedData.retrieveAll(8888L,
                                               (contid, at) -> Kill.accessQuery(testAccount, contid, 1000, false, at,
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Kill next : result) {
      int killID = next.getKillID();
      Assert.assertTrue(listCheck.containsKey(killID));
      Assert.assertEquals(listCheck.get(killID), next);
    }
  }

}
