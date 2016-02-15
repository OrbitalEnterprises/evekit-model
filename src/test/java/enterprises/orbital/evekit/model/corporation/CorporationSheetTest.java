package enterprises.orbital.evekit.model.corporation;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;

public class CorporationSheetTest extends AbstractModelTester<CorporationSheet> {

  final long                                        allianceID      = TestBase.getRandomInt(100000000);
  final String                                      allianceName    = "test alliance name";
  final long                                        ceoID           = TestBase.getRandomInt(100000000);
  final String                                      ceoName         = "test ceo name";
  final long                                        corporationID   = TestBase.getRandomInt(100000000);
  final String                                      corporationName = "test corporation name";
  final String                                      description     = "test description";
  final int                                         logoColor1      = TestBase.getRandomInt(100000000);
  final int                                         logoColor2      = TestBase.getRandomInt(100000000);
  final int                                         logoColor3      = TestBase.getRandomInt(100000000);
  final int                                         logoGraphicID   = TestBase.getRandomInt(100000000);
  final int                                         logoShape1      = TestBase.getRandomInt(100000000);
  final int                                         logoShape2      = TestBase.getRandomInt(100000000);
  final int                                         logoShape3      = TestBase.getRandomInt(100000000);
  final int                                         memberCount     = TestBase.getRandomInt(100000000);
  final int                                         memberLimit     = TestBase.getRandomInt(100000000);
  final int                                         shares          = TestBase.getRandomInt(100000000);
  final long                                        stationID       = TestBase.getRandomInt(100000000);
  final String                                      stationName     = "test station name";
  final double                                      taxRate         = TestBase.getRandomDouble(100000000);
  final String                                      ticker          = "test ticker";
  final String                                      url             = "test url";

  final ClassUnderTestConstructor<CorporationSheet> eol             = new ClassUnderTestConstructor<CorporationSheet>() {

                                                                      @Override
                                                                      public CorporationSheet getCUT() {
                                                                        return new CorporationSheet(
                                                                            allianceID, allianceName, ceoID, ceoName, corporationID, corporationName,
                                                                            description, logoColor1, logoColor2, logoColor3, logoGraphicID, logoShape1,
                                                                            logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName,
                                                                            taxRate, ticker, url);
                                                                      }

                                                                    };

  final ClassUnderTestConstructor<CorporationSheet> live            = new ClassUnderTestConstructor<CorporationSheet>() {
                                                                      @Override
                                                                      public CorporationSheet getCUT() {
                                                                        return new CorporationSheet(
                                                                            allianceID, allianceName, ceoID + 1, ceoName, corporationID, corporationName,
                                                                            description, logoColor1, logoColor2, logoColor3, logoGraphicID, logoShape1,
                                                                            logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName,
                                                                            taxRate, ticker, url);
                                                                      }

                                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationSheet>() {

      @Override
      public CorporationSheet[] getVariants() {
        return new CorporationSheet[] {
            new CorporationSheet(
                allianceID + 1, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName + " 1", ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID + 1, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName + " 1", corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID + 1, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName + " 1", description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description + " 1", logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1 + 1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2 + 1, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3 + 1, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID + 1,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1 + 1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2 + 1, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3 + 1, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount + 1, memberLimit, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit + 1, shares, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares + 1, stationID, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID + 1, stationName, taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName + " 1", taxRate, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate + 1, ticker, url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker + " 1", url),
            new CorporationSheet(
                allianceID, allianceName, ceoID, ceoName, corporationID, corporationName, description, logoColor1, logoColor2, logoColor3, logoGraphicID,
                logoShape1, logoShape2, logoShape3, memberCount, memberLimit, shares, stationID, stationName, taxRate, ticker, url + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CorporationSheet>() {

      @Override
      public CorporationSheet getModel(SynchronizedEveAccount account, long time) {
        return CorporationSheet.get(account, time);
      }

    });
  }

}
