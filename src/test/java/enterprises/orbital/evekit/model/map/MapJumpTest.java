package enterprises.orbital.evekit.model.map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class MapJumpTest extends AbstractRefModelTester<MapJump> {

  final int                                solarSystemID = TestBase.getRandomInt(100000000);
  final int                                shipJumps     = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<MapJump> eol           = new ClassUnderTestConstructor<MapJump>() {

                                                           @Override
                                                           public MapJump getCUT() {
                                                             return new MapJump(solarSystemID, shipJumps);
                                                           }

                                                         };

  final ClassUnderTestConstructor<MapJump> live          = new ClassUnderTestConstructor<MapJump>() {
                                                           @Override
                                                           public MapJump getCUT() {
                                                             return new MapJump(solarSystemID, shipJumps + 1);
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MapJump>() {

      @Override
      public MapJump[] getVariants() {
        return new MapJump[] {
            new MapJump(solarSystemID + 1, shipJumps), new MapJump(solarSystemID, shipJumps + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MapJump>() {

      @Override
      public MapJump getModel(
                              long time) {
        return MapJump.get(time, solarSystemID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different solar system ID
    // - objects not live at the given time
    MapJump existing, keyed;

    keyed = new MapJump(solarSystemID, shipJumps);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different solar system ID
    existing = new MapJump(solarSystemID + 1, shipJumps);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new MapJump(solarSystemID, shipJumps + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new MapJump(solarSystemID, shipJumps + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    MapJump result = MapJump.get(8889L, solarSystemID);
    Assert.assertEquals(keyed, result);
  }

}
