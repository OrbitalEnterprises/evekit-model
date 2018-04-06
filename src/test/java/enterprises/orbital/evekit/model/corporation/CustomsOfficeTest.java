package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomsOfficeTest extends AbstractModelTester<CustomsOffice> {
  private final long officeID = TestBase.getRandomInt(100000000);
  private final int solarSystemID = TestBase.getRandomInt(100000000);
  private final int reinforceExitStart = TestBase.getRandomInt(100);
  private final int reinforceExitEnd = TestBase.getRandomInt(100);
  private final boolean allowAlliance = true;
  private final boolean allowStandings = false;
  private final String standingLevel = TestBase.getRandomText(50);
  private final float taxRateAlliance = TestBase.getRandomFloat(1000);
  private final float taxRateCorp = TestBase.getRandomFloat(1000);
  private final float taxRateStandingExcellent = TestBase.getRandomFloat(1000);
  private final float taxRateStandingGood = TestBase.getRandomFloat(1000);
  private final float taxRateStandingNeutral = TestBase.getRandomFloat(1000);
  private final float taxRateStandingBad = TestBase.getRandomFloat(1000);
  private final float taxRateStandingTerrible = TestBase.getRandomFloat(1000);

  final ClassUnderTestConstructor<CustomsOffice> eol = () -> new CustomsOffice(
      officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance,
      allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
      taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral,
      taxRateStandingBad,
      taxRateStandingTerrible);

  final ClassUnderTestConstructor<CustomsOffice> live = () -> new CustomsOffice(
      officeID, solarSystemID + 1, reinforceExitStart, reinforceExitEnd, allowAlliance,
      allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
      taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral,
      taxRateStandingBad,
      taxRateStandingTerrible);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CustomsOffice[]{
        new CustomsOffice(
            officeID + 1, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings,
            standingLevel, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID + 1, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings,
            standingLevel, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart + 1, reinforceExitEnd, allowAlliance, allowStandings,
            standingLevel, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd + 1, allowAlliance, allowStandings,
            standingLevel, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, !allowAlliance, allowStandings,
            standingLevel, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, !allowStandings,
            standingLevel, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings,
            standingLevel + 1, taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance + 1, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance, taxRateCorp + 1,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent + 1, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood + 1, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral + 1, taxRateStandingBad,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad + 1,
            taxRateStandingTerrible),
        new CustomsOffice(
            officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
            taxRateAlliance, taxRateCorp,
            taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
            taxRateStandingTerrible + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CustomsOffice.get(account, time, officeID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - offices for a different account
    // - offices not live at the given time
    CustomsOffice existing;
    Map<Long, CustomsOffice> listCheck = new HashMap<>();

    existing = new CustomsOffice(
        officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
        taxRateAlliance, taxRateCorp,
        taxRateStandingExcellent,
        taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingTerrible);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);

    listCheck.put(officeID, existing);
    existing = new CustomsOffice(
        officeID + 1, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
        taxRateAlliance, taxRateCorp,
        taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
        taxRateStandingTerrible);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(officeID + 1, existing);

    // Associated with different account
    existing = new CustomsOffice(
        officeID + 2, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
        taxRateAlliance, taxRateCorp,
        taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
        taxRateStandingTerrible);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CustomsOffice(
        officeID + 3, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
        taxRateAlliance, taxRateCorp,
        taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
        taxRateStandingTerrible);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CustomsOffice(
        officeID + 4, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance, allowStandings, standingLevel,
        taxRateAlliance, taxRateCorp,
        taxRateStandingExcellent, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad,
        taxRateStandingTerrible);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CustomsOffice> result = CachedData.retrieveAll(8888L,
                                                        (contid, at) -> CustomsOffice.accessQuery(testAccount, contid,
                                                                                                  1000, false, at,
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CustomsOffice next : result) {
      long itemID = next.getOfficeID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

  }

}
