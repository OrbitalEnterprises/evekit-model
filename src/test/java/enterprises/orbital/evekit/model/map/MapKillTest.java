package enterprises.orbital.evekit.model.map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class MapKillTest extends AbstractRefModelTester<MapKill> {

  final int                                factionKills  = TestBase.getRandomInt(100000000);
  final int                                podKills      = TestBase.getRandomInt(100000000);
  final int                                shipKills     = TestBase.getRandomInt(100000000);
  final int                                solarSystemID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<MapKill> eol           = new ClassUnderTestConstructor<MapKill>() {

                                                           @Override
                                                           public MapKill getCUT() {
                                                             return new MapKill(factionKills, podKills, shipKills, solarSystemID);
                                                           }

                                                         };

  final ClassUnderTestConstructor<MapKill> live          = new ClassUnderTestConstructor<MapKill>() {
                                                           @Override
                                                           public MapKill getCUT() {
                                                             return new MapKill(factionKills + 1, podKills, shipKills, solarSystemID);
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MapKill>() {

      @Override
      public MapKill[] getVariants() {
        return new MapKill[] {
            new MapKill(factionKills + 1, podKills, shipKills, solarSystemID), new MapKill(factionKills, podKills + 1, shipKills, solarSystemID),
            new MapKill(factionKills, podKills, shipKills + 1, solarSystemID), new MapKill(factionKills, podKills, shipKills, solarSystemID + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MapKill>() {

      @Override
      public MapKill getModel(
                              long time) {
        return MapKill.get(time, solarSystemID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different solar system ID
    // - objects not live at the given time
    MapKill existing, keyed;

    keyed = new MapKill(factionKills, podKills, shipKills, solarSystemID);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different solar system ID
    existing = new MapKill(factionKills, podKills, shipKills, solarSystemID + 1);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new MapKill(factionKills + 1, podKills, shipKills, solarSystemID);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new MapKill(factionKills + 2, podKills, shipKills, solarSystemID);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    MapKill result = MapKill.get(8889L, solarSystemID);
    Assert.assertEquals(keyed, result);
  }

}
