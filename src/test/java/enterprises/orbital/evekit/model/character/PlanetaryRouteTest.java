package enterprises.orbital.evekit.model.character;

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
import enterprises.orbital.evekit.model.character.PlanetaryRoute;

public class PlanetaryRouteTest extends AbstractModelTester<PlanetaryRoute> {
  final long                                      planetID         = TestBase.getRandomInt(100000000);
  final long                                      routeID          = TestBase.getRandomInt(100000000);
  final long                                      sourcePinID      = TestBase.getRandomInt(100000000);
  final long                                      destinationPinID = TestBase.getRandomInt(100000000);
  final int                                       contentTypeID    = TestBase.getRandomInt(100000000);
  final String                                    contentTypeName  = "test content type name";
  final int                                       quantity         = TestBase.getRandomInt(100000000);
  final long                                      waypoint1        = TestBase.getRandomInt(100000000);
  final long                                      waypoint2        = TestBase.getRandomInt(100000000);
  final long                                      waypoint3        = TestBase.getRandomInt(100000000);
  final long                                      waypoint4        = TestBase.getRandomInt(100000000);
  final long                                      waypoint5        = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<PlanetaryRoute> eol              = new ClassUnderTestConstructor<PlanetaryRoute>() {

                                                                     @Override
                                                                     public PlanetaryRoute getCUT() {
                                                                       return new PlanetaryRoute(
                                                                           planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName,
                                                                           quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
                                                                     }

                                                                   };

  final ClassUnderTestConstructor<PlanetaryRoute> live             = new ClassUnderTestConstructor<PlanetaryRoute>() {
                                                                     @Override
                                                                     public PlanetaryRoute getCUT() {
                                                                       return new PlanetaryRoute(
                                                                           planetID, routeID, sourcePinID + 1, destinationPinID, contentTypeID, contentTypeName,
                                                                           quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
                                                                     }

                                                                   };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<PlanetaryRoute>() {

      @Override
      public PlanetaryRoute[] getVariants() {
        return new PlanetaryRoute[] {
            new PlanetaryRoute(
                planetID + 1, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID + 1, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID + 1, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID + 1, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID + 1, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName + " 1", quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity + 1, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1 + 1, waypoint2, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2 + 1, waypoint3, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3 + 1, waypoint4,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4 + 1,
                waypoint5),
            new PlanetaryRoute(
                planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
                waypoint5 + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<PlanetaryRoute>() {

      @Override
      public PlanetaryRoute getModel(SynchronizedEveAccount account, long time) {
        return PlanetaryRoute.get(account, time, planetID, routeID);
      }

    });
  }

  @Test
  public void testGetAllPlanetaryRoutes() throws Exception {
    // Should exclude:
    // - routes for a different account
    // - routes not live at the given time
    PlanetaryRoute existing;
    Map<Long, Map<Long, PlanetaryRoute>> listCheck = new HashMap<Long, Map<Long, PlanetaryRoute>>();

    existing = new PlanetaryRoute(
        planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID, new HashMap<Long, PlanetaryRoute>());
    listCheck.get(planetID).put(routeID, existing);

    existing = new PlanetaryRoute(
        planetID + 10, routeID + 10, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
        waypoint5);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID + 10, new HashMap<Long, PlanetaryRoute>());
    listCheck.get(planetID + 10).put(routeID + 10, existing);

    // Associated with different account
    existing = new PlanetaryRoute(
        planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new PlanetaryRoute(
        planetID + 3, routeID + 3, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
        waypoint5);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new PlanetaryRoute(
        planetID + 4, routeID + 4, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
        waypoint5);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<PlanetaryRoute> result = PlanetaryRoute.getAllPlanetaryRoutes(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (PlanetaryRoute next : result) {
      long planetID = next.getPlanetID();
      long routeID = next.getRouteID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID).containsKey(routeID));
      Assert.assertEquals(listCheck.get(planetID).get(routeID), next);
    }

  }

  @Test
  public void testGetAllPlanetaryRoutesByPlanet() throws Exception {
    // Should exclude:
    // - routes for a different account
    // - routes not live at the given time
    // - routes for a different planet
    PlanetaryRoute existing;
    Map<Long, Map<Long, PlanetaryRoute>> listCheck = new HashMap<Long, Map<Long, PlanetaryRoute>>();

    existing = new PlanetaryRoute(
        planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID, new HashMap<Long, PlanetaryRoute>());
    listCheck.get(planetID).put(routeID, existing);

    existing = new PlanetaryRoute(
        planetID, routeID + 10, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(planetID).put(routeID + 10, existing);

    // Associated with different account
    existing = new PlanetaryRoute(
        planetID, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with different planet
    existing = new PlanetaryRoute(
        planetID + 10, routeID, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4, waypoint5);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new PlanetaryRoute(
        planetID + 3, routeID + 3, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
        waypoint5);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new PlanetaryRoute(
        planetID + 4, routeID + 4, sourcePinID, destinationPinID, contentTypeID, contentTypeName, quantity, waypoint1, waypoint2, waypoint3, waypoint4,
        waypoint5);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<PlanetaryRoute> result = PlanetaryRoute.getAllPlanetaryRoutes(testAccount, 8888L);
    Assert.assertEquals(listCheck.get(planetID).size(), result.size());
    for (PlanetaryRoute next : result) {
      long planetID = next.getPlanetID();
      long routeID = next.getRouteID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID).containsKey(routeID));
      Assert.assertEquals(listCheck.get(planetID).get(routeID), next);
    }

  }

}
