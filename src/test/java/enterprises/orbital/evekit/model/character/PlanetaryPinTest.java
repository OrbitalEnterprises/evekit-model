package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class PlanetaryPinTest extends AbstractModelTester<PlanetaryPin> {
  private final int planetID = TestBase.getRandomInt(100000000);
  private final long pinID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int schematicID = TestBase.getRandomInt(100000000);
  private final long lastCycleStart = TestBase.getRandomInt(100000000);
  private final int cycleTime = TestBase.getRandomInt(100000000);
  private final int quantityPerCycle = TestBase.getRandomInt(100000000);
  private final long installTime = TestBase.getRandomInt(100000000);
  private final long expiryTime = TestBase.getRandomInt(100000000);
  private final int productTypeID = TestBase.getRandomInt(100000000);
  private final float longitude = TestBase.getRandomFloat(1000);
  private final float latitude = TestBase.getRandomFloat(1000);
  private final float headRadius = TestBase.getRandomFloat(1000);
  private final Set<PlanetaryPinHead> heads = new HashSet<>();
  private final Set<PlanetaryPinContent> contents = new HashSet<>();

  public PlanetaryPinTest() {
    int numHeads = TestBase.getRandomInt(5) + 10;
    int numContents = TestBase.getRandomInt(10) + 10;
    for (int i = 0; i < numHeads; i++) {
      heads.add(new PlanetaryPinHead(TestBase.getUniqueRandomInteger(),
                                     TestBase.getRandomFloat(100),
                                     TestBase.getRandomFloat(100)));
    }
    for (int i = 0; i < numContents; i++) {
      contents.add(new PlanetaryPinContent(TestBase.getUniqueRandomInteger(),
                                           TestBase.getUniqueRandomLong()));
    }
  }


  final ClassUnderTestConstructor<PlanetaryPin> eol = () -> new PlanetaryPin(
      planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime,
      quantityPerCycle, installTime, expiryTime, productTypeID,
      longitude, latitude, headRadius, heads, contents);

  final ClassUnderTestConstructor<PlanetaryPin> live = () -> new PlanetaryPin(
      planetID, pinID, typeID + 1, schematicID, lastCycleStart, cycleTime,
      quantityPerCycle, installTime, expiryTime, productTypeID,
      longitude, latitude, headRadius, heads, contents);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> {
      Set<PlanetaryPinHead> headCopy = new HashSet<>(heads);
      headCopy.add(new PlanetaryPinHead(TestBase.getUniqueRandomInteger(),
                                        TestBase.getRandomFloat(100),
                                        TestBase.getRandomFloat(100)));
      Set<PlanetaryPinContent> contentCopy = new HashSet<>(contents);
      contentCopy.add(new PlanetaryPinContent(TestBase.getUniqueRandomInteger(),
                                              TestBase.getRandomLong()));

      return new PlanetaryPin[]{
          new PlanetaryPin(
              planetID + 1, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID + 1, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID + 1, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID + 1, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart + 1, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime + 1, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle + 1, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime + 1,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime + 1, productTypeID,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID + 1,
              longitude, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude + 1, latitude, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude + 1, headRadius, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius + 1, heads, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, headCopy, contents),
          new PlanetaryPin(
              planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
              expiryTime, productTypeID,
              longitude, latitude, headRadius, heads, contentCopy)
      };

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> PlanetaryPin.get(account, time, planetID, pinID));
  }

  @Test
  public void testGetAllPlanetaryPins() throws Exception {
    // Should exclude:
    // - pins for a different account
    // - pins not live at the given time
    PlanetaryPin existing;
    Map<Integer, Map<Long, PlanetaryPin>> listCheck = new HashMap<>();

    existing = new PlanetaryPin(
        planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime, expiryTime,
        productTypeID,
        longitude, latitude, headRadius, heads, contents);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID, new HashMap<>());
    listCheck.get(planetID)
             .put(pinID, existing);

    existing = new PlanetaryPin(
        planetID, pinID + 10, typeID + 10, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
        expiryTime, productTypeID,
        longitude, latitude, headRadius, heads, contents);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(planetID)
             .put(pinID + 10, existing);

    existing = new PlanetaryPin(
        planetID + 10, pinID + 10, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
        expiryTime, productTypeID,
        longitude, latitude, headRadius, heads, contents);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID + 10, new HashMap<>());
    listCheck.get(planetID + 10)
             .put(pinID + 10, existing);

    // Associated with different account
    existing = new PlanetaryPin(
        planetID, pinID, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime, expiryTime,
        productTypeID,
        longitude, latitude, headRadius, heads, contents);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new PlanetaryPin(
        planetID + 3, pinID + 3, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
        expiryTime, productTypeID,
        longitude, latitude, headRadius, heads, contents);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new PlanetaryPin(
        planetID + 4, pinID + 4, typeID, schematicID, lastCycleStart, cycleTime, quantityPerCycle, installTime,
        expiryTime, productTypeID,
        longitude, latitude, headRadius, heads, contents);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<PlanetaryPin> result = CachedData.retrieveAll(8888L,
                                                       (contid, at) -> PlanetaryPin.accessQuery(testAccount, contid,
                                                                                                1000, false, at,
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
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (PlanetaryPin next : result) {
      int planetID = next.getPlanetID();
      long pinID = next.getPinID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID)
                                 .containsKey(pinID));
      Assert.assertEquals(listCheck.get(planetID)
                                   .get(pinID), next);
    }

  }

}
