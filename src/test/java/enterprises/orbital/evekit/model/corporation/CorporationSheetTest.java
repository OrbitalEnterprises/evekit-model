package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class CorporationSheetTest extends AbstractModelTester<CorporationSheet> {

  private final int allianceID = TestBase.getRandomInt(100000000);
  private final int ceoID = TestBase.getRandomInt(100000000);
  private final long corporationID = TestBase.getRandomInt(100000000);
  private final String corporationName = "test corporation name";
  private final String description = "test name";
  private final int memberCount = TestBase.getRandomInt(100000000);
  private final long shares = TestBase.getRandomInt(100000000);
  private final int stationID = TestBase.getRandomInt(100000000);
  private final float taxRate = TestBase.getRandomFloat(10);
  private final String ticker = "test ticker";
  private final String url = "test url";
  private final long dateFounded = TestBase.getRandomLong();
  private final int creatorID = TestBase.getRandomInt();
  private final int factionID = TestBase.getRandomInt();
  private final String px64x64 = TestBase.getRandomText(50);
  private final String px128x128 = TestBase.getRandomText(50);
  private final String px256x256 = TestBase.getRandomText(50);
  private final boolean warEligible = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<CorporationSheet> eol = () -> new CorporationSheet(
      allianceID, ceoID, corporationID, corporationName,
      description, memberCount, shares, stationID,
      taxRate, ticker, url, dateFounded, creatorID,
      factionID, px64x64, px128x128, px256x256, warEligible);

  final ClassUnderTestConstructor<CorporationSheet> live = () -> new CorporationSheet(
      allianceID, ceoID + 1, corporationID, corporationName,
      description, memberCount, shares, stationID,
      taxRate, ticker, url, dateFounded, creatorID,
      factionID, px64x64, px128x128, px256x256, warEligible);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CorporationSheet[]{
        new CorporationSheet(
            allianceID + 1, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID + 1, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID + 1, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName + "1",
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description + "1", memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount + 1, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares + 1, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID + 1,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate + 0.1F, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker + "1", url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url + "1", dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded + 1, creatorID,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID + 1,
            factionID, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID + 1, px64x64, px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64 + "1", px128x128, px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128 + "1", px256x256, warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256 + "1", warEligible),
        new CorporationSheet(
            allianceID, ceoID, corporationID, corporationName,
            description, memberCount, shares, stationID,
            taxRate, ticker, url, dateFounded, creatorID,
            factionID, px64x64, px128x128, px256x256, !warEligible)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CorporationSheet::get);
  }

}
