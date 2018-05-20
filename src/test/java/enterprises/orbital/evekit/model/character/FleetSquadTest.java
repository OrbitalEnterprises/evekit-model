package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetSquadTest extends AbstractModelTester<FleetSquad> {
  private final long fleetID = TestBase.getRandomLong();
  private final long wingID = TestBase.getRandomLong();
  private final long squadID = TestBase.getRandomLong();
  private final String name = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<FleetSquad> eol = () -> new FleetSquad(fleetID, wingID, squadID, name);

  final ClassUnderTestConstructor<FleetSquad> live = () -> new FleetSquad(fleetID, wingID, squadID, name + "1");

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new FleetSquad[]{
        new FleetSquad(fleetID + 1, wingID, squadID, name),
        new FleetSquad(fleetID, wingID + 1, squadID, name),
        new FleetSquad(fleetID, wingID, squadID + 1, name),
        new FleetSquad(fleetID, wingID, squadID, name + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> FleetSquad.get(account, time, fleetID, wingID, squadID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - fleets for a different account
    // - fleets not live at the given time
    FleetSquad existing;
    Map<Triple<Long, Long, Long>, FleetSquad> listCheck = new HashMap<>();

    existing = new FleetSquad(fleetID, wingID, squadID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(fleetID, wingID, squadID), existing);

    existing = new FleetSquad(fleetID + 10, wingID, squadID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(fleetID + 10, wingID, squadID), existing);

    // Associated with different account
    existing = new FleetSquad(fleetID, wingID, squadID, name);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new FleetSquad(fleetID + 3, wingID, squadID, name);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new FleetSquad(fleetID + 4, wingID, squadID, name);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<FleetSquad> result = CachedData.retrieveAll(8888L,
                                                     (contid, at) -> FleetSquad.accessQuery(testAccount, contid, 1000,
                                                                                            false,
                                                                                            at, AttributeSelector.any(),
                                                                                            AttributeSelector.any(),
                                                                                            AttributeSelector.any(),
                                                                                            AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (FleetSquad next : result) {
      long fleetID = next.getFleetID();
      long wingID = next.getWingID();
      long squadID = next.getSquadID();
      Assert.assertTrue(listCheck.containsKey(Triple.of(fleetID, wingID, squadID)));
      Assert.assertEquals(listCheck.get(Triple.of(fleetID, wingID, squadID)), next);
    }

  }

}
