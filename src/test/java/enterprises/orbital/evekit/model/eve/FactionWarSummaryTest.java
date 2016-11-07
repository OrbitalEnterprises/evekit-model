package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionWarSummaryTest extends AbstractRefModelTester<FactionWarSummary> {

  final int                                          killsLastWeek          = TestBase.getRandomInt(100000000);
  final int                                          killsTotal             = TestBase.getRandomInt(100000000);
  final int                                          killsYesterday         = TestBase.getRandomInt(100000000);
  final int                                          victoryPointsLastWeek  = TestBase.getRandomInt(100000000);
  final int                                          victoryPointsTotal     = TestBase.getRandomInt(100000000);
  final int                                          victoryPointsYesterday = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FactionWarSummary> eol                    = new ClassUnderTestConstructor<FactionWarSummary>() {

                                                                              @Override
                                                                              public FactionWarSummary getCUT() {
                                                                                return new FactionWarSummary(
                                                                                    killsLastWeek, killsTotal, killsYesterday, victoryPointsLastWeek,
                                                                                    victoryPointsTotal, victoryPointsYesterday);
                                                                              }

                                                                            };

  final ClassUnderTestConstructor<FactionWarSummary> live                   = new ClassUnderTestConstructor<FactionWarSummary>() {
                                                                              @Override
                                                                              public FactionWarSummary getCUT() {
                                                                                return new FactionWarSummary(
                                                                                    killsLastWeek + 1, killsTotal, killsYesterday, victoryPointsLastWeek,
                                                                                    victoryPointsTotal, victoryPointsYesterday);
                                                                              }

                                                                            };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionWarSummary>() {

      @Override
      public FactionWarSummary[] getVariants() {
        return new FactionWarSummary[] {
            new FactionWarSummary(killsLastWeek + 1, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday),
            new FactionWarSummary(killsLastWeek, killsTotal + 1, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday),
            new FactionWarSummary(killsLastWeek, killsTotal, killsYesterday + 1, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday),
            new FactionWarSummary(killsLastWeek, killsTotal, killsYesterday, victoryPointsLastWeek + 1, victoryPointsTotal, victoryPointsYesterday),
            new FactionWarSummary(killsLastWeek, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal + 1, victoryPointsYesterday),
            new FactionWarSummary(killsLastWeek, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FactionWarSummary>() {

      @Override
      public FactionWarSummary getModel(
                                        long time) {
        return FactionWarSummary.get(time);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects not live at the given time
    FactionWarSummary existing, keyed;

    keyed = new FactionWarSummary(killsLastWeek, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Not live at the given time
    existing = new FactionWarSummary(killsLastWeek + 1, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionWarSummary(killsLastWeek + 2, killsTotal, killsYesterday, victoryPointsLastWeek, victoryPointsTotal, victoryPointsYesterday);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionWarSummary result = FactionWarSummary.get(8889L);
    Assert.assertEquals(keyed, result);
  }

}
