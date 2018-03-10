package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetaryRouteTest extends AbstractModelTester<PlanetaryRoute> {
  private final int planetID = TestBase.getRandomInt(100000000);
  private final long routeID = TestBase.getRandomInt(100000000);
  private final long sourcePinID = TestBase.getRandomInt(100000000);
  private final long destinationPinID = TestBase.getRandomInt(100000000);
  private final int contentTypeID = TestBase.getRandomInt(100000000);
  private final float quantity = TestBase.getRandomFloat(1000);
  private final List<Long> waypoints = new ArrayList<>();

  public PlanetaryRouteTest() {
    int numWaypoints = TestBase.getRandomInt(5) + 10;
    for (int i = 0; i < numWaypoints; i++) {
      waypoints.add(TestBase.getUniqueRandomLong());
    }
  }

  final ClassUnderTestConstructor<PlanetaryRoute> eol = () -> new PlanetaryRoute(
      planetID, routeID, sourcePinID, destinationPinID, contentTypeID,
      quantity, waypoints);

  final ClassUnderTestConstructor<PlanetaryRoute> live = () -> new PlanetaryRoute(
      planetID, routeID, sourcePinID + 1, destinationPinID, contentTypeID,
      quantity, waypoints);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> {
      List<Long> waypointsCopy = new ArrayList<>(waypoints);
      waypointsCopy.add(TestBase.getUniqueRandomLong());
      return new PlanetaryRoute[]{
          new PlanetaryRoute(
              planetID + 1, routeID, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints),
          new PlanetaryRoute(
              planetID, routeID + 1, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints),
          new PlanetaryRoute(
              planetID, routeID, sourcePinID + 1, destinationPinID, contentTypeID, quantity, waypoints),
          new PlanetaryRoute(
              planetID, routeID, sourcePinID, destinationPinID + 1, contentTypeID, quantity, waypoints),
          new PlanetaryRoute(
              planetID, routeID, sourcePinID, destinationPinID, contentTypeID + 1, quantity, waypoints),
          new PlanetaryRoute(
              planetID, routeID, sourcePinID, destinationPinID, contentTypeID, quantity + 1, waypoints),
          new PlanetaryRoute(
              planetID, routeID, sourcePinID, destinationPinID, contentTypeID, quantity, waypointsCopy)
      };

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> PlanetaryRoute.get(account, time, planetID, routeID));
  }

  @Test
  public void testGetAllPlanetaryRoutes() throws Exception {
    // Should exclude:
    // - routes for a different account
    // - routes not live at the given time
    PlanetaryRoute existing;
    Map<Integer, Map<Long, PlanetaryRoute>> listCheck = new HashMap<>();

    existing = new PlanetaryRoute(
        planetID, routeID, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID, new HashMap<>());
    listCheck.get(planetID)
             .put(routeID, existing);

    existing = new PlanetaryRoute(
        planetID + 10, routeID + 10, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID + 10, new HashMap<>());
    listCheck.get(planetID + 10)
             .put(routeID + 10, existing);

    // Associated with different account
    existing = new PlanetaryRoute(
        planetID, routeID, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new PlanetaryRoute(
        planetID + 3, routeID + 3, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new PlanetaryRoute(
        planetID + 4, routeID + 4, sourcePinID, destinationPinID, contentTypeID, quantity, waypoints);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<PlanetaryRoute> result = CachedData.retrieveAll(8888L,
                                                         (contid, at) -> PlanetaryRoute.accessQuery(testAccount, contid,
                                                                                                    1000, false, at,
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (PlanetaryRoute next : result) {
      int planetID = next.getPlanetID();
      long routeID = next.getRouteID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID)
                                 .containsKey(routeID));
      Assert.assertEquals(listCheck.get(planetID)
                                   .get(routeID), next);
    }

  }

}
