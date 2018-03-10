package enterprises.orbital.evekit.model.character;

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

public class PlanetaryColonyTest extends AbstractModelTester<PlanetaryColony> {
  private final int planetID = TestBase.getRandomInt(100000000);
  private final int solarSystemID = TestBase.getRandomInt(100000000);
  private final String planetType = "test planet type name";
  private final int ownerID = TestBase.getRandomInt(100000000);
  private final long lastUpdate = TestBase.getRandomInt(100000000);
  private final int upgradeLevel = TestBase.getRandomInt(100000000);
  private final int numberOfPins = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<PlanetaryColony> eol = () -> new PlanetaryColony(
      planetID, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins);

  final ClassUnderTestConstructor<PlanetaryColony> live = () -> new PlanetaryColony(
      planetID, solarSystemID, planetType + "1", ownerID, lastUpdate, upgradeLevel, numberOfPins);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new PlanetaryColony[]{
        new PlanetaryColony(
            planetID + 1, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins),
        new PlanetaryColony(
            planetID, solarSystemID + 1, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins),
        new PlanetaryColony(
            planetID, solarSystemID, planetType + " 1", ownerID, lastUpdate, upgradeLevel, numberOfPins),
        new PlanetaryColony(
            planetID, solarSystemID, planetType, ownerID + 1, lastUpdate, upgradeLevel, numberOfPins),
        new PlanetaryColony(
            planetID, solarSystemID, planetType, ownerID, lastUpdate + 1, upgradeLevel, numberOfPins),
        new PlanetaryColony(
            planetID, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel + 1, numberOfPins),
        new PlanetaryColony(
            planetID, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> PlanetaryColony.get(account, time, planetID));
  }

  @Test
  public void testGetAllPlanetaryColonies() throws Exception {
    // Should exclude:
    // - planets for a different account
    // - planets not live at the given time
    PlanetaryColony existing;
    Map<Integer, PlanetaryColony> listCheck = new HashMap<>();

    existing = new PlanetaryColony(
        planetID, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID, existing);

    existing = new PlanetaryColony(
        planetID + 10, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID + 10, existing);

    // Associated with different account
    existing = new PlanetaryColony(
        planetID, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new PlanetaryColony(
        planetID + 3, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new PlanetaryColony(
        planetID + 4, solarSystemID, planetType, ownerID, lastUpdate, upgradeLevel, numberOfPins);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<PlanetaryColony> result = CachedData.retrieveAll(8888L,
                                                          (contid, at) -> PlanetaryColony.accessQuery(testAccount,
                                                                                                      contid, 1000,
                                                                                                      false, at,
                                                                                                      AttributeSelector.any(),
                                                                                                      AttributeSelector.any(),
                                                                                                      AttributeSelector.any(),
                                                                                                      AttributeSelector.any(),
                                                                                                      AttributeSelector.any(),
                                                                                                      AttributeSelector.any(),
                                                                                                      AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (PlanetaryColony next : result) {
      int planetID = next.getPlanetID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertEquals(listCheck.get(planetID), next);
    }

  }

}
