package enterprises.orbital.evekit.model.corporation;

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

public class FacilityTest extends AbstractModelTester<Facility> {

  final long                                facilityID       = TestBase.getRandomInt(100000000);
  final int                                 typeID           = TestBase.getRandomInt(100000000);
  final String                              typeName         = "test type name";
  final int                                 solarSystemID    = TestBase.getRandomInt(100000000);
  final String                              solarSystemName  = "test solar system name";
  final int                                 regionID         = TestBase.getRandomInt(100000000);
  final String                              regionName       = "test region name";
  final int                                 starbaseModifier = TestBase.getRandomInt(100000000);
  final double                              tax              = TestBase.getRandomDouble(100000000);

  final ClassUnderTestConstructor<Facility> eol              = new ClassUnderTestConstructor<Facility>() {

                                                               @Override
                                                               public Facility getCUT() {
                                                                 return new Facility(
                                                                     facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName,
                                                                     starbaseModifier, tax);
                                                               }

                                                             };

  final ClassUnderTestConstructor<Facility> live             = new ClassUnderTestConstructor<Facility>() {
                                                               @Override
                                                               public Facility getCUT() {
                                                                 return new Facility(
                                                                     facilityID, typeID + 1, typeName, solarSystemID, solarSystemName, regionID, regionName,
                                                                     starbaseModifier, tax);
                                                               }

                                                             };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Facility>() {

      @Override
      public Facility[] getVariants() {
        return new Facility[] {
            new Facility(facilityID + 1, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax),
            new Facility(facilityID, typeID + 1, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax),
            new Facility(facilityID, typeID, typeName + " 1", solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax),
            new Facility(facilityID, typeID, typeName, solarSystemID + 1, solarSystemName, regionID, regionName, starbaseModifier, tax),
            new Facility(facilityID, typeID, typeName, solarSystemID, solarSystemName + " 1", regionID, regionName, starbaseModifier, tax),
            new Facility(facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID + 1, regionName, starbaseModifier, tax),
            new Facility(facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName + " 1", starbaseModifier, tax),
            new Facility(facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier + 1, tax),
            new Facility(facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Facility>() {

      @Override
      public Facility getModel(SynchronizedEveAccount account, long time) {
        return Facility.get(account, time, facilityID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - facilities for a different account
    // - facilities not live at the given time
    Facility existing;
    Map<Long, Facility> listCheck = new HashMap<Long, Facility>();

    existing = new Facility(facilityID, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(facilityID, existing);

    existing = new Facility(facilityID + 1, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(facilityID + 1, existing);

    // Associated with different account
    existing = new Facility(facilityID + 2, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Facility(facilityID + 3, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Facility(facilityID + 4, typeID, typeName, solarSystemID, solarSystemName, regionID, regionName, starbaseModifier, tax);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<Facility> result = Facility.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Facility next : result) {
      long facilityID = next.getFacilityID();
      Assert.assertTrue(listCheck.containsKey(facilityID));
      Assert.assertEquals(listCheck.get(facilityID), next);
    }

  }

}
