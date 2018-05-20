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

public class FleetMemberTest extends AbstractModelTester<FleetMember> {
  private final long fleetID = TestBase.getRandomLong();
  private final int characterID = TestBase.getRandomInt();
  private final long joinTime = TestBase.getRandomLong();
  private final String role = TestBase.getRandomText(50);
  private final String roleName = TestBase.getRandomText(50);
  private final int shipTypeID = TestBase.getRandomInt();
  private final int solarSystemID = TestBase.getRandomInt();
  private final long squadID = TestBase.getRandomLong();
  private final long stationID = TestBase.getRandomLong();
  private final boolean takesFleetWarp = TestBase.getRandomBoolean();
  private final long wingID = TestBase.getRandomLong();

  final ClassUnderTestConstructor<FleetMember> eol = () -> new FleetMember(fleetID, characterID, joinTime, role,
                                                                           roleName,
                                                                           shipTypeID, solarSystemID, squadID,
                                                                           stationID,
                                                                           takesFleetWarp, wingID);

  final ClassUnderTestConstructor<FleetMember> live = () -> new FleetMember(fleetID, characterID, joinTime + 1, role,
                                                                            roleName,
                                                                            shipTypeID, solarSystemID, squadID,
                                                                            stationID,
                                                                            takesFleetWarp, wingID);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new FleetMember[]{
        new FleetMember(fleetID + 1, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID + 1, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime + 1, role, roleName, shipTypeID, solarSystemID, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role + "1", roleName, shipTypeID, solarSystemID, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName + "1", shipTypeID, solarSystemID, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID + 1, solarSystemID, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID + 1, squadID,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID + 1,
                        stationID, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                        stationID + 1, takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID, stationID,
                        !takesFleetWarp, wingID),
        new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID, stationID,
                        takesFleetWarp, wingID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> FleetMember.get(account, time, fleetID, characterID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - fleets for a different account
    // - fleets not live at the given time
    FleetMember existing;
    Map<Pair<Long, Integer>, FleetMember> listCheck = new HashMap<>();

    existing = new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                               stationID, takesFleetWarp, wingID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fleetID, characterID), existing);

    existing = new FleetMember(fleetID + 10, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                               stationID, takesFleetWarp, wingID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(fleetID + 10, characterID), existing);

    // Associated with different account
    existing = new FleetMember(fleetID, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                               stationID, takesFleetWarp, wingID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new FleetMember(fleetID + 3, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                               stationID, takesFleetWarp, wingID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new FleetMember(fleetID + 4, characterID, joinTime, role, roleName, shipTypeID, solarSystemID, squadID,
                               stationID, takesFleetWarp, wingID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<FleetMember> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> FleetMember.accessQuery(testAccount, contid, 1000,
                                                                                              false,
                                                                                              at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (FleetMember next : result) {
      long fleetID = next.getFleetID();
      int characterID = next.getCharacterID();
      Assert.assertTrue(listCheck.containsKey(Pair.of(fleetID, characterID)));
      Assert.assertEquals(listCheck.get(Pair.of(fleetID, characterID)), next);
    }

  }

}
