package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CorporationVictoryPointStatTest extends AbstractRefModelTester<CorporationVictoryPointStat> {

  final StatAttribute                                          attribute       = StatAttribute.LAST_WEEK;
  final long                                                   corporationID   = TestBase.getRandomInt(100000000);
  final String                                                 corporationName = TestBase.getRandomText(50);
  final int                                                    victoryPoints   = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CorporationVictoryPointStat> eol             = new ClassUnderTestConstructor<CorporationVictoryPointStat>() {

                                                                                 @Override
                                                                                 public CorporationVictoryPointStat getCUT() {
                                                                                   return new CorporationVictoryPointStat(
                                                                                       attribute, victoryPoints, corporationID, corporationName);
                                                                                 }

                                                                               };

  final ClassUnderTestConstructor<CorporationVictoryPointStat> live            = new ClassUnderTestConstructor<CorporationVictoryPointStat>() {
                                                                                 @Override
                                                                                 public CorporationVictoryPointStat getCUT() {
                                                                                   return new CorporationVictoryPointStat(
                                                                                       attribute, victoryPoints + 1, corporationID, corporationName);
                                                                                 }

                                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationVictoryPointStat>() {

      @Override
      public CorporationVictoryPointStat[] getVariants() {
        return new CorporationVictoryPointStat[] {
            new CorporationVictoryPointStat(StatAttribute.TOTAL, victoryPoints, corporationID, corporationName),
            new CorporationVictoryPointStat(attribute, victoryPoints + 1, corporationID, corporationName),
            new CorporationVictoryPointStat(attribute, victoryPoints, corporationID + 1, corporationName),
            new CorporationVictoryPointStat(attribute, victoryPoints, corporationID, corporationName + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CorporationVictoryPointStat>() {

      @Override
      public CorporationVictoryPointStat getModel(
                                                  long time) {
        return CorporationVictoryPointStat.get(time, attribute, corporationID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different corporation ID
    // - objects with different attribute
    // - objects not live at the given time
    CorporationVictoryPointStat existing, keyed;

    keyed = new CorporationVictoryPointStat(attribute, victoryPoints, corporationID, corporationName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different corporation ID
    existing = new CorporationVictoryPointStat(attribute, victoryPoints, corporationID + 1, corporationName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different attribute
    existing = new CorporationVictoryPointStat(StatAttribute.TOTAL, victoryPoints, corporationID, corporationName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CorporationVictoryPointStat(attribute, victoryPoints + 1, corporationID, corporationName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CorporationVictoryPointStat(attribute, victoryPoints + 2, corporationID, corporationName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CorporationVictoryPointStat result = CorporationVictoryPointStat.get(8889L, attribute, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
