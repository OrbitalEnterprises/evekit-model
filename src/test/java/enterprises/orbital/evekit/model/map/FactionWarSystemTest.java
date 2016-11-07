package enterprises.orbital.evekit.model.map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionWarSystemTest extends AbstractRefModelTester<FactionWarSystem> {

  final long                                        occupyingFactionID   = TestBase.getRandomInt(100000000);
  final String                                      occupyingFactionName = TestBase.getRandomText(50);
  final long                                        owningFactionID      = TestBase.getRandomInt(100000000);
  final String                                      owningFactionName    = TestBase.getRandomText(50);
  final int                                         solarSystemID        = TestBase.getRandomInt(100000000);
  final String                                      solarSystemName      = TestBase.getRandomText(50);
  final boolean                                     contested            = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<FactionWarSystem> eol                  = new ClassUnderTestConstructor<FactionWarSystem>() {

                                                                           @Override
                                                                           public FactionWarSystem getCUT() {
                                                                             return new FactionWarSystem(
                                                                                 occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName,
                                                                                 solarSystemID, solarSystemName, contested);
                                                                           }

                                                                         };

  final ClassUnderTestConstructor<FactionWarSystem> live                 = new ClassUnderTestConstructor<FactionWarSystem>() {
                                                                           @Override
                                                                           public FactionWarSystem getCUT() {
                                                                             return new FactionWarSystem(
                                                                                 occupyingFactionID + 1, occupyingFactionName, owningFactionID,
                                                                                 owningFactionName, solarSystemID, solarSystemName, contested);
                                                                           }

                                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionWarSystem>() {

      @Override
      public FactionWarSystem[] getVariants() {
        return new FactionWarSystem[] {
            new FactionWarSystem(occupyingFactionID + 1, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName, contested),
            new FactionWarSystem(occupyingFactionID, occupyingFactionName + "1", owningFactionID, owningFactionName, solarSystemID, solarSystemName, contested),
            new FactionWarSystem(occupyingFactionID, occupyingFactionName, owningFactionID + 1, owningFactionName, solarSystemID, solarSystemName, contested),
            new FactionWarSystem(occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName + "1", solarSystemID, solarSystemName, contested),
            new FactionWarSystem(occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID + 1, solarSystemName, contested),
            new FactionWarSystem(occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName + "1", contested),
            new FactionWarSystem(occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName, !contested)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FactionWarSystem>() {

      @Override
      public FactionWarSystem getModel(
                                       long time) {
        return FactionWarSystem.get(time, solarSystemID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different solar system ID
    // - objects not live at the given time
    FactionWarSystem existing, keyed;

    keyed = new FactionWarSystem(occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName, contested);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different solaar system ID
    existing = new FactionWarSystem(
        occupyingFactionID, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID + 1, solarSystemName, contested);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionWarSystem(
        occupyingFactionID + 1, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName, contested);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionWarSystem(
        occupyingFactionID + 2, occupyingFactionName, owningFactionID, owningFactionName, solarSystemID, solarSystemName, contested);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionWarSystem result = FactionWarSystem.get(8889L, solarSystemID);
    Assert.assertEquals(keyed, result);
  }

}
