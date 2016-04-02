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

public class PlanetaryPinTest extends AbstractModelTester<PlanetaryPin> {
  final long                                    planetID         = TestBase.getRandomInt(100000000);
  final long                                    pinID            = TestBase.getRandomInt(100000000);
  final int                                     typeID           = TestBase.getRandomInt(100000000);
  final String                                  typeName         = "test type name";
  final int                                     schematicID      = TestBase.getRandomInt(100000000);
  final long                                    lastLaunchTime   = TestBase.getRandomInt(100000000);
  final int                                     cycleTime        = TestBase.getRandomInt(100000000);
  final int                                     quantityPerCycle = TestBase.getRandomInt(100000000);
  final long                                    installTime      = TestBase.getRandomInt(100000000);
  final long                                    expiryTime       = TestBase.getRandomInt(100000000);
  final int                                     contentTypeID    = TestBase.getRandomInt(100000000);
  final String                                  contentTypeName  = "test content type nae";
  final int                                     contentQuantity  = TestBase.getRandomInt(100000000);
  final double                                  longitude        = TestBase.getRandomDouble(1000);
  final double                                  latitude         = TestBase.getRandomDouble(1000);

  final ClassUnderTestConstructor<PlanetaryPin> eol              = new ClassUnderTestConstructor<PlanetaryPin>() {

                                                                   @Override
                                                                   public PlanetaryPin getCUT() {
                                                                     return new PlanetaryPin(
                                                                         planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime,
                                                                         quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
                                                                         contentQuantity, longitude, latitude);
                                                                   }

                                                                 };

  final ClassUnderTestConstructor<PlanetaryPin> live             = new ClassUnderTestConstructor<PlanetaryPin>() {
                                                                   @Override
                                                                   public PlanetaryPin getCUT() {
                                                                     return new PlanetaryPin(
                                                                         planetID, pinID, typeID, typeName + " 1", schematicID, lastLaunchTime, cycleTime,
                                                                         quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
                                                                         contentQuantity, longitude, latitude);
                                                                   }

                                                                 };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<PlanetaryPin>() {

      @Override
      public PlanetaryPin[] getVariants() {
        return new PlanetaryPin[] {
            new PlanetaryPin(
                planetID + 1, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID + 1, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID + 1, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName + " 1", schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID + 1, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime + 1, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime + 1, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle + 1, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime + 1, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime + 1, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID + 1,
                contentTypeName, contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName + " 1", contentQuantity, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity + 1, longitude, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude + 1, latitude),
            new PlanetaryPin(
                planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
                contentTypeName, contentQuantity, longitude, latitude + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<PlanetaryPin>() {

      @Override
      public PlanetaryPin getModel(
                                   SynchronizedEveAccount account,
                                   long time) {
        return PlanetaryPin.get(account, time, planetID, pinID, contentTypeID);
      }

    });
  }

  @Test
  public void testGetAllPlanetaryPins() throws Exception {
    // Should exclude:
    // - pins for a different account
    // - pins not live at the given time
    PlanetaryPin existing;
    Map<Long, Map<Long, Map<Integer, PlanetaryPin>>> listCheck = new HashMap<Long, Map<Long, Map<Integer, PlanetaryPin>>>();

    existing = new PlanetaryPin(
        planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
        contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID, new HashMap<Long, Map<Integer, PlanetaryPin>>());
    listCheck.get(planetID).put(pinID, new HashMap<Integer, PlanetaryPin>());
    listCheck.get(planetID).get(pinID).put(contentTypeID, existing);

    existing = new PlanetaryPin(
        planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID + 10,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(planetID).get(pinID).put(contentTypeID + 10, existing);

    existing = new PlanetaryPin(
        planetID + 10, pinID + 10, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID + 10, new HashMap<Long, Map<Integer, PlanetaryPin>>());
    listCheck.get(planetID + 10).put(pinID + 10, new HashMap<Integer, PlanetaryPin>());
    listCheck.get(planetID + 10).get(pinID + 10).put(contentTypeID, existing);

    // Associated with different account
    existing = new PlanetaryPin(
        planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
        contentQuantity, longitude, latitude);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new PlanetaryPin(
        planetID + 3, pinID + 3, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new PlanetaryPin(
        planetID + 4, pinID + 4, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<PlanetaryPin> result = PlanetaryPin.getAllPlanetaryPins(testAccount, 8888L);
    Assert.assertEquals(3, result.size());
    for (PlanetaryPin next : result) {
      long planetID = next.getPlanetID();
      long pinID = next.getPinID();
      int contentTypeID = next.getContentTypeID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID).containsKey(pinID));
      Assert.assertTrue(listCheck.get(planetID).get(pinID).containsKey(contentTypeID));
      Assert.assertEquals(listCheck.get(planetID).get(pinID).get(contentTypeID), next);
    }

  }

  @Test
  public void testGetAllPlanetaryPinsByPlanet() throws Exception {
    // Should exclude:
    // - pins for a different account
    // - pins not live at the given time
    // - pins for a different planet
    PlanetaryPin existing;
    Map<Long, Map<Long, PlanetaryPin>> listCheck = new HashMap<Long, Map<Long, PlanetaryPin>>();

    existing = new PlanetaryPin(
        planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
        contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID, new HashMap<Long, PlanetaryPin>());
    listCheck.get(planetID).put(pinID, existing);

    existing = new PlanetaryPin(
        planetID, pinID + 10, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(planetID).put(pinID + 10, existing);

    // Associated with different account
    existing = new PlanetaryPin(
        planetID, pinID, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID, contentTypeName,
        contentQuantity, longitude, latitude);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different planet
    existing = new PlanetaryPin(
        planetID + 10, pinID + 10, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new PlanetaryPin(
        planetID + 3, pinID + 3, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new PlanetaryPin(
        planetID + 4, pinID + 4, typeID, typeName, schematicID, lastLaunchTime, cycleTime, quantityPerCycle, installTime, expiryTime, contentTypeID,
        contentTypeName, contentQuantity, longitude, latitude);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<PlanetaryPin> result = PlanetaryPin.getAllPlanetaryPins(testAccount, 8888L);
    Assert.assertEquals(listCheck.get(planetID).size(), result.size());
    for (PlanetaryPin next : result) {
      long planetID = next.getPlanetID();
      long pinID = next.getPinID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID).containsKey(pinID));
      Assert.assertEquals(listCheck.get(planetID).get(pinID), next);
    }

  }

}
