package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class AllianceMemberCorporationTest extends AbstractRefModelTester<AllianceMemberCorporation> {
  final long                                                 allianceID    = TestBase.getRandomInt(100000000);
  final long                                                 corporationID = TestBase.getRandomInt(100000000);
  final long                                                 startDate     = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<AllianceMemberCorporation> eol           = new ClassUnderTestConstructor<AllianceMemberCorporation>() {

                                                                             @Override
                                                                             public AllianceMemberCorporation getCUT() {
                                                                               return new AllianceMemberCorporation(allianceID, corporationID, startDate);
                                                                             }

                                                                           };

  final ClassUnderTestConstructor<AllianceMemberCorporation> live          = new ClassUnderTestConstructor<AllianceMemberCorporation>() {
                                                                             @Override
                                                                             public AllianceMemberCorporation getCUT() {
                                                                               return new AllianceMemberCorporation(allianceID, corporationID, startDate + 1);
                                                                             }

                                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<AllianceMemberCorporation>() {

      @Override
      public AllianceMemberCorporation[] getVariants() {
        return new AllianceMemberCorporation[] {
            new AllianceMemberCorporation(allianceID + 1, corporationID, startDate), new AllianceMemberCorporation(allianceID, corporationID + 1, startDate),
            new AllianceMemberCorporation(allianceID, corporationID, startDate + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<AllianceMemberCorporation>() {

      @Override
      public AllianceMemberCorporation getModel(
                                                long time) {
        return AllianceMemberCorporation.get(time, allianceID, corporationID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different alliance ID
    // - objects with different corporation ID
    // - objects not live at the given time
    AllianceMemberCorporation existing, keyed;

    keyed = new AllianceMemberCorporation(allianceID, corporationID, startDate);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different alliance ID
    existing = new AllianceMemberCorporation(allianceID + 1, corporationID, startDate);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different corporationID
    existing = new AllianceMemberCorporation(allianceID, corporationID + 1, startDate);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new AllianceMemberCorporation(allianceID, corporationID, startDate + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new AllianceMemberCorporation(allianceID, corporationID, startDate + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    AllianceMemberCorporation result = AllianceMemberCorporation.get(8889L, allianceID, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
