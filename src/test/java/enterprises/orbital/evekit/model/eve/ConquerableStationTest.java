package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class ConquerableStationTest extends AbstractRefModelTester<ConquerableStation> {

  final long                                          corporationID   = TestBase.getRandomInt(100000000);
  final String                                        corporationName = TestBase.getRandomText(50);
  final long                                          solarSystemID   = TestBase.getRandomInt(100000000);
  final long                                          stationID       = TestBase.getRandomInt(100000000);
  final String                                        stationName     = TestBase.getRandomText(50);
  final int                                           stationTypeID   = TestBase.getRandomInt(100000000);
  final long                                          x               = TestBase.getRandomInt(100000000);
  final long                                          y               = TestBase.getRandomInt(100000000);
  final long                                          z               = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<ConquerableStation> eol             = new ClassUnderTestConstructor<ConquerableStation>() {

                                                                        @Override
                                                                        public ConquerableStation getCUT() {
                                                                          return new ConquerableStation(
                                                                              corporationID, corporationName, solarSystemID, stationID, stationName,
                                                                              stationTypeID, x, y, z);
                                                                        }

                                                                      };

  final ClassUnderTestConstructor<ConquerableStation> live            = new ClassUnderTestConstructor<ConquerableStation>() {
                                                                        @Override
                                                                        public ConquerableStation getCUT() {
                                                                          return new ConquerableStation(
                                                                              corporationID + 1, corporationName, solarSystemID, stationID, stationName,
                                                                              stationTypeID, x, y, z);
                                                                        }

                                                                      };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ConquerableStation>() {

      @Override
      public ConquerableStation[] getVariants() {
        return new ConquerableStation[] {
            new ConquerableStation(corporationID + 1, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y, z),
            new ConquerableStation(corporationID, corporationName + "1", solarSystemID, stationID, stationName, stationTypeID, x, y, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID + 1, stationID, stationName, stationTypeID, x, y, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID, stationID + 1, stationName, stationTypeID, x, y, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID, stationID, stationName + "1", stationTypeID, x, y, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID, stationID, stationName, stationTypeID + 1, x, y, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID, stationID, stationName, stationTypeID, x + 1, y, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y + 1, z),
            new ConquerableStation(corporationID, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y, z + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ConquerableStation>() {

      @Override
      public ConquerableStation getModel(
                                         long time) {
        return ConquerableStation.get(time, stationID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different station ID
    // - objects not live at the given time
    ConquerableStation existing, keyed;

    keyed = new ConquerableStation(corporationID, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y, z);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different station ID
    existing = new ConquerableStation(corporationID, corporationName, solarSystemID, stationID + 1, stationName, stationTypeID, x, y, z);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new ConquerableStation(corporationID + 1, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y, z);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new ConquerableStation(corporationID + 2, corporationName, solarSystemID, stationID, stationName, stationTypeID, x, y, z);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    ConquerableStation result = ConquerableStation.get(8889L, stationID);
    Assert.assertEquals(keyed, result);
  }

}
