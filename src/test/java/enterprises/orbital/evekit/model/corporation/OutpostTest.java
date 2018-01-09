package enterprises.orbital.evekit.model.corporation;

import java.math.BigDecimal;
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

public class OutpostTest extends AbstractModelTester<Outpost> {

  final long                               stationID                = TestBase.getRandomInt(100000000);
  final long                               ownerID                  = TestBase.getRandomInt(100000000);
  final String                             stationName              = "station name";
  final int                                solarSystemID            = TestBase.getRandomInt(100000000);
  final BigDecimal                         dockingCostPerShipVolume = TestBase.getRandomBigDecimal(100000000);
  final BigDecimal                         officeRentalCost         = TestBase.getRandomBigDecimal(100000000);
  final int                                stationTypeID            = TestBase.getRandomInt(100000000);
  final double                             reprocessingEfficiency   = TestBase.getRandomDouble(100000000);
  final double                             reprocessingStationTake  = TestBase.getRandomDouble(100000000);
  final long                               standingOwnerID          = TestBase.getRandomInt(100000000);
  final long                               x                        = TestBase.getRandomInt(100000000);
  final long                               y                        = TestBase.getRandomInt(100000000);
  final long                               z                        = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Outpost> eol                      = new ClassUnderTestConstructor<Outpost>() {

                                                                      @Override
                                                                      public Outpost getCUT() {
                                                                        return new Outpost(
                                                                            stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume,
                                                                            officeRentalCost, stationTypeID, reprocessingEfficiency, reprocessingStationTake,
                                                                            standingOwnerID, x, y, z);
                                                                      }

                                                                    };

  final ClassUnderTestConstructor<Outpost> live                     = new ClassUnderTestConstructor<Outpost>() {
                                                                      @Override
                                                                      public Outpost getCUT() {
                                                                        return new Outpost(
                                                                            stationID, ownerID, stationName, solarSystemID + 1, dockingCostPerShipVolume,
                                                                            officeRentalCost, stationTypeID, reprocessingEfficiency, reprocessingStationTake,
                                                                            standingOwnerID, x, y, z);
                                                                      }

                                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Outpost>() {

      @Override
      public Outpost[] getVariants() {
        return new Outpost[] {
            new Outpost(
                stationID + 1, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID + 1, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName + " 1", solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID + 1, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume.add(BigDecimal.TEN), officeRentalCost, stationTypeID,
                reprocessingEfficiency, reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost.add(BigDecimal.TEN), stationTypeID,
                reprocessingEfficiency, reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID + 1, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency + 1,
                reprocessingStationTake, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake + 1, standingOwnerID, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID + 1, x, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x + 1, y, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y + 1, z),
            new Outpost(
                stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
                reprocessingStationTake, standingOwnerID, x, y, z + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_OUTPOST_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Outpost>() {

      @Override
      public Outpost getModel(
                              SynchronizedEveAccount account,
                              long time) {
        return Outpost.get(account, time, stationID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - outposts for a different account
    // - outposts not live at the given time
    Outpost existing;
    Map<Long, Outpost> listCheck = new HashMap<Long, Outpost>();

    existing = new Outpost(
        stationID, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
        reprocessingStationTake, standingOwnerID, x, y, z);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(stationID, existing);

    existing = new Outpost(
        stationID + 1, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
        reprocessingStationTake, standingOwnerID, x, y, z);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(stationID + 1, existing);

    // Associated with different account
    existing = new Outpost(
        stationID + 2, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
        reprocessingStationTake, standingOwnerID, x, y, z);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Outpost(
        stationID + 3, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
        reprocessingStationTake, standingOwnerID, x, y, z);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Outpost(
        stationID + 4, ownerID, stationName, solarSystemID, dockingCostPerShipVolume, officeRentalCost, stationTypeID, reprocessingEfficiency,
        reprocessingStationTake, standingOwnerID, x, y, z);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Outpost> result = Outpost.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Outpost next : result) {
      long stationID = next.getStationID();
      Assert.assertTrue(listCheck.containsKey(stationID));
      Assert.assertEquals(listCheck.get(stationID), next);
    }

  }

}
