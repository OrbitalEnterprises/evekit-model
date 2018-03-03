package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class FacWarStatsTest extends AbstractModelTester<FacWarStats> {

  private final int currentRank = TestBase.getRandomInt(100000000);
  private final long enlisted = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);
  private final int highestRank = TestBase.getRandomInt(100000000);
  private final int killsLastWeek = TestBase.getRandomInt(100000000);
  private final int killsTotal = TestBase.getRandomInt(100000000);
  private final int killsYesterday = TestBase.getRandomInt(100000000);
  private final int pilots = TestBase.getRandomInt(100000000);
  private final int victoryPointsLastWeek = TestBase.getRandomInt(100000000);
  private final int victoryPointsTotal = TestBase.getRandomInt(100000000);
  private final int victoryPointsYesterday = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FacWarStats> eol = () -> new FacWarStats(
      currentRank, enlisted, factionID, highestRank, killsLastWeek,
      killsTotal, killsYesterday, pilots, victoryPointsLastWeek, victoryPointsTotal,
      victoryPointsYesterday);

  final ClassUnderTestConstructor<FacWarStats> live = () -> new FacWarStats(
      currentRank, enlisted + 1, factionID, highestRank, killsLastWeek,
      killsTotal, killsYesterday, pilots, victoryPointsLastWeek, victoryPointsTotal,
      victoryPointsYesterday);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new FacWarStats[]{
        new FacWarStats(
            currentRank + 1, enlisted, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted + 1, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID + 1, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank + 1, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek + 1, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal + 1, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday + 1, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots + 1,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek + 1,
            victoryPointsTotal, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal + 1, victoryPointsYesterday),
        new FacWarStats(
            currentRank, enlisted, factionID, highestRank, killsLastWeek, killsTotal, killsYesterday, pilots,
            victoryPointsLastWeek,
            victoryPointsTotal, victoryPointsYesterday + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_FAC_WAR_STATS));

  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, FacWarStats::get);
  }

}
