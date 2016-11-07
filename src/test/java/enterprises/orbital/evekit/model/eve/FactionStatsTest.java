package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionStatsTest extends AbstractRefModelTester<FactionStats> {

  final long                                    factionID              = TestBase.getRandomInt(100000000);
  final String                                  factionName            = TestBase.getRandomText(50);
  final int                                     killsLastWeek          = TestBase.getRandomInt(100000000);
  final int                                     killsTotal             = TestBase.getRandomInt(100000000);
  final int                                     killsYesterday         = TestBase.getRandomInt(100000000);
  final int                                     pilots                 = TestBase.getRandomInt(100000000);
  final int                                     systemsControlled      = TestBase.getRandomInt(100000000);
  final int                                     victoryPointsLastWeek  = TestBase.getRandomInt(100000000);
  final int                                     victoryPointsTotal     = TestBase.getRandomInt(100000000);
  final int                                     victoryPointsYesterday = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FactionStats> eol                    = new ClassUnderTestConstructor<FactionStats>() {

                                                                         @Override
                                                                         public FactionStats getCUT() {
                                                                           return new FactionStats(
                                                                               factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots,
                                                                               systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                                                                               victoryPointsYesterday);
                                                                         }

                                                                       };

  final ClassUnderTestConstructor<FactionStats> live                   = new ClassUnderTestConstructor<FactionStats>() {
                                                                         @Override
                                                                         public FactionStats getCUT() {
                                                                           return new FactionStats(
                                                                               factionID, factionName, killsLastWeek + 1, killsTotal, killsYesterday, pilots,
                                                                               systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                                                                               victoryPointsYesterday);
                                                                         }

                                                                       };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionStats>() {

      @Override
      public FactionStats[] getVariants() {
        return new FactionStats[] {
            new FactionStats(
                factionID + 1, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName + "1", killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek + 1, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal + 1, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal, killsYesterday + 1, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots + 1, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled + 1, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek + 1, victoryPointsTotal,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal + 1,
                victoryPointsYesterday),
            new FactionStats(
                factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
                victoryPointsYesterday + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FactionStats>() {

      @Override
      public FactionStats getModel(
                                   long time) {
        return FactionStats.get(time, factionID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects not live at the given time
    FactionStats existing, keyed;

    keyed = new FactionStats(
        factionID, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different faction ID
    existing = new FactionStats(
        factionID + 1, factionName, killsLastWeek, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionStats(
        factionID, factionName, killsLastWeek + 1, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionStats(
        factionID, factionName, killsLastWeek + 2, killsTotal, killsYesterday, pilots, systemsControlled, victoryPointsLastWeek, victoryPointsTotal,
        victoryPointsYesterday);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionStats result = FactionStats.get(8889L, factionID);
    Assert.assertEquals(keyed, result);
  }

}
