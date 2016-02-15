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
import enterprises.orbital.evekit.model.character.PlanetaryColony;

public class PlanetaryColonyTest extends AbstractModelTester<PlanetaryColony> {
  final long                                       planetID        = TestBase.getRandomInt(100000000);
  final int                                        solarSystemID   = TestBase.getRandomInt(100000000);
  final String                                     solarSystemName = "test solar system name";
  final String                                     planetName      = "test planet name";
  final int                                        planetTypeID    = TestBase.getRandomInt(100000000);
  final String                                     planetTypeName  = "test planet type name";
  final long                                       ownerID         = TestBase.getRandomInt(100000000);
  final String                                     ownerName       = " test owner name";
  final long                                       lastUpdate      = TestBase.getRandomInt(100000000);
  final int                                        upgradeLevel    = TestBase.getRandomInt(100000000);
  final int                                        numberOfPins    = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<PlanetaryColony> eol             = new ClassUnderTestConstructor<PlanetaryColony>() {

                                                                     @Override
                                                                     public PlanetaryColony getCUT() {
                                                                       return new PlanetaryColony(
                                                                           planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName,
                                                                           ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
                                                                     }

                                                                   };

  final ClassUnderTestConstructor<PlanetaryColony> live            = new ClassUnderTestConstructor<PlanetaryColony>() {
                                                                     @Override
                                                                     public PlanetaryColony getCUT() {
                                                                       return new PlanetaryColony(
                                                                           planetID, solarSystemID, solarSystemName + " 2", planetName, planetTypeID,
                                                                           planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
                                                                     }

                                                                   };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<PlanetaryColony>() {

      @Override
      public PlanetaryColony[] getVariants() {
        return new PlanetaryColony[] {
            new PlanetaryColony(
                planetID + 1, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID + 1, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName + " 1", planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName + " 1", planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID + 1, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName + " 1", ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID + 1, ownerName, lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName + " 1", lastUpdate, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate + 1, upgradeLevel,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel + 1,
                numberOfPins),
            new PlanetaryColony(
                planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel,
                numberOfPins + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<PlanetaryColony>() {

      @Override
      public PlanetaryColony getModel(SynchronizedEveAccount account, long time) {
        return PlanetaryColony.get(account, time, planetID);
      }

    });
  }

  @Test
  public void testGetAllPlanetaryColonies() throws Exception {
    // Should exclude:
    // - planets for a different account
    // - planets not live at the given time
    PlanetaryColony existing;
    Map<Long, PlanetaryColony> listCheck = new HashMap<Long, PlanetaryColony>();

    existing = new PlanetaryColony(
        planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID, existing);

    existing = new PlanetaryColony(
        planetID + 10, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID + 10, existing);

    // Associated with different account
    existing = new PlanetaryColony(
        planetID, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new PlanetaryColony(
        planetID + 3, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new PlanetaryColony(
        planetID + 4, solarSystemID, solarSystemName, planetName, planetTypeID, planetTypeName, ownerID, ownerName, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<PlanetaryColony> result = PlanetaryColony.getAllPlanetaryColonies(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (PlanetaryColony next : result) {
      long planetID = next.getPlanetID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertEquals(listCheck.get(planetID), next);
    }

  }

}
