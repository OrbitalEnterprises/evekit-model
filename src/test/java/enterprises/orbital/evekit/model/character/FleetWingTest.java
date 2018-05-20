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

public class FleetWingTest extends AbstractModelTester<FleetWing> {
  private final long fleetID = TestBase.getRandomLong();
  private final long wingID = TestBase.getRandomLong();
  private final String name = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<FleetWing> eol = () -> new FleetWing(fleetID, wingID, name);

  final ClassUnderTestConstructor<FleetWing> live = () -> new FleetWing(fleetID, wingID, name + "1");

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new FleetWing[]{
        new FleetWing(fleetID + 1, wingID, name),
        new FleetWing(fleetID, wingID + 1, name),
        new FleetWing(fleetID, wingID, name + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> FleetWing.get(account, time, fleetID, wingID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - fleets for a different account
    // - fleets not live at the given time
    FleetWing existing;
    Map<Pair<Long, Long>, FleetWing> listCheck = new HashMap<>();

    existing = new FleetWing(fleetID, wingID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fleetID, wingID), existing);

    existing = new FleetWing(fleetID + 10, wingID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fleetID + 10, wingID), existing);

    // Associated with different account
    existing = new FleetWing(fleetID, wingID, name);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new FleetWing(fleetID + 3, wingID, name);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new FleetWing(fleetID + 4, wingID, name);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<FleetWing> result = CachedData.retrieveAll(8888L,
                                                    (contid, at) -> FleetWing.accessQuery(testAccount, contid, 1000,
                                                                                          false,
                                                                                          at, AttributeSelector.any(),
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (FleetWing next : result) {
      long fleetID = next.getFleetID();
      long wingID = next.getWingID();
      Assert.assertTrue(listCheck.containsKey(Pair.of(fleetID, wingID)));
      Assert.assertEquals(listCheck.get(Pair.of(fleetID, wingID)), next);
    }

  }

}
