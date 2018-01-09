package enterprises.orbital.evekit.model.corporation;

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

public class CustomsOfficeTest extends AbstractModelTester<CustomsOffice> {
  final long                                     itemID                  = TestBase.getRandomInt(100000000);
  final int                                      solarSystemID           = TestBase.getRandomInt(100000000);
  final String                                   solarSystemName         = "test solar system name";
  final int                                      reinforceHour           = TestBase.getRandomInt(100000000);
  final boolean                                  allowAlliance           = true;
  final boolean                                  allowStandings          = false;
  final double                                   standingLevel           = TestBase.getRandomDouble(100000000);
  final double                                   taxRateAlliance         = TestBase.getRandomDouble(100000000);
  final double                                   taxRateCorp             = TestBase.getRandomDouble(100000000);
  final double                                   taxRateStandingHigh     = TestBase.getRandomDouble(100000000);
  final double                                   taxRateStandingGood     = TestBase.getRandomDouble(100000000);
  final double                                   taxRateStandingNeutral  = TestBase.getRandomDouble(100000000);
  final double                                   taxRateStandingBad      = TestBase.getRandomDouble(100000000);
  final double                                   taxRateStandingHorrible = TestBase.getRandomDouble(100000000);

  final ClassUnderTestConstructor<CustomsOffice> eol                     = new ClassUnderTestConstructor<CustomsOffice>() {

                                                                           @Override
                                                                           public CustomsOffice getCUT() {
                                                                             return new CustomsOffice(
                                                                                 itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance,
                                                                                 allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                                                                                 taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral,
                                                                                 taxRateStandingBad, taxRateStandingHorrible);
                                                                           }

                                                                         };

  final ClassUnderTestConstructor<CustomsOffice> live                    = new ClassUnderTestConstructor<CustomsOffice>() {
                                                                           @Override
                                                                           public CustomsOffice getCUT() {
                                                                             return new CustomsOffice(
                                                                                 itemID, solarSystemID + 1, solarSystemName, reinforceHour, allowAlliance,
                                                                                 allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                                                                                 taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral,
                                                                                 taxRateStandingBad, taxRateStandingHorrible);
                                                                           }

                                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CustomsOffice>() {

      @Override
      public CustomsOffice[] getVariants() {
        return new CustomsOffice[] {
            new CustomsOffice(
                itemID + 1, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID + 1, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName + " 1", reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour + 1, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, !allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, !allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel + 1, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance + 1, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp + 1,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh + 1, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood + 1, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral + 1, taxRateStandingBad, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad + 1, taxRateStandingHorrible),
            new CustomsOffice(
                itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
                taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CustomsOffice>() {

      @Override
      public CustomsOffice getModel(SynchronizedEveAccount account, long time) {
        return CustomsOffice.get(account, time, itemID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - offices for a different account
    // - offices not live at the given time
    CustomsOffice existing;
    Map<Long, CustomsOffice> listCheck = new HashMap<Long, CustomsOffice>();

    existing = new CustomsOffice(
        itemID, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp, taxRateStandingHigh,
        taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);

    listCheck.put(itemID, existing);
    existing = new CustomsOffice(
        itemID + 1, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
        taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 1, existing);

    // Associated with different account
    existing = new CustomsOffice(
        itemID + 2, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
        taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CustomsOffice(
        itemID + 3, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
        taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CustomsOffice(
        itemID + 4, solarSystemID, solarSystemName, reinforceHour, allowAlliance, allowStandings, standingLevel, taxRateAlliance, taxRateCorp,
        taxRateStandingHigh, taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingHorrible);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CustomsOffice> result = CustomsOffice.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CustomsOffice next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

  }

}
