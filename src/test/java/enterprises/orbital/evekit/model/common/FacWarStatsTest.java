package enterprises.orbital.evekit.model.common;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.common.FacWarStats;

public class FacWarStatsTest extends AbstractModelTester<FacWarStats> {

  final int                                    currentRank            = TestBase.getRandomInt(100000000);
  final long                                   enlisted               = TestBase.getRandomInt(100000000);
  final int                                    factionID              = TestBase.getRandomInt(100000000);
  final String                                 factionName            = "test faction name";
  final int                                    highestRank            = TestBase.getRandomInt(100000000);
  final int                                    killsLastWeek          = TestBase.getRandomInt(100000000);
  final int                                    killsTotal             = TestBase.getRandomInt(100000000);
  final int                                    killsYesterday         = TestBase.getRandomInt(100000000);
  final int                                    pilots                 = TestBase.getRandomInt(100000000);
  final int                                    victoryPointsLastWeek  = TestBase.getRandomInt(100000000);
  final int                                    victoryPointsTotal     = TestBase.getRandomInt(100000000);
  final int                                    victoryPointsYesterday = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FacWarStats> eol                    = new ClassUnderTestConstructor<FacWarStats>() {

                                                                        @Override
                                                                        public FacWarStats getCUT() {
                                                                          return new FacWarStats(
                                                                              currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek,
                                                                              killsTotal, killsYesterday, pilots, victoryPointsLastWeek, victoryPointsTotal,
                                                                              victoryPointsYesterday);
                                                                        }

                                                                      };

  final ClassUnderTestConstructor<FacWarStats> live                   = new ClassUnderTestConstructor<FacWarStats>() {
                                                                        @Override
                                                                        public FacWarStats getCUT() {
                                                                          return new FacWarStats(
                                                                              currentRank, enlisted + 1, factionID, factionName, highestRank, killsLastWeek,
                                                                              killsTotal, killsYesterday, pilots, victoryPointsLastWeek, victoryPointsTotal,
                                                                              victoryPointsYesterday);
                                                                        }

                                                                      };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FacWarStats>() {

      @Override
      public FacWarStats[] getVariants() {
        return new FacWarStats[] {
            new FacWarStats(
                currentRank + 1, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted + 1, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID + 1, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName + " 1", highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank + 1, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek + 1, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal + 1, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday + 1, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots + 1, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek + 1,
                victoryPointsTotal, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal + 1, victoryPointsYesterday),
            new FacWarStats(
                currentRank, enlisted, factionID, factionName, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots, victoryPointsLastWeek,
                victoryPointsTotal, victoryPointsYesterday + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_FAC_WAR_STATS));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FacWarStats>() {

      @Override
      public FacWarStats getModel(SynchronizedEveAccount account, long time) {
        return FacWarStats.get(account, time);
      }

    });
  }

}
