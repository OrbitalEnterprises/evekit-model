package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CorporationVictoryPointStatTest extends AbstractRefModelTester<CorporationVictoryPointStat> {

  final long                                                   corporationID   = TestBase.getRandomInt(100000000);
  final String                                                 corporationName = TestBase.getRandomText(50);
  final int                                                    victoryPoints   = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CorporationVictoryPointStat> eol             = new ClassUnderTestConstructor<CorporationVictoryPointStat>() {

                                                                                 @Override
                                                                                 public CorporationVictoryPointStat getCUT() {
                                                                                   return new CorporationVictoryPointStat(
                                                                                       corporationID, corporationName, victoryPoints);
                                                                                 }

                                                                               };

  final ClassUnderTestConstructor<CorporationVictoryPointStat> live            = new ClassUnderTestConstructor<CorporationVictoryPointStat>() {
                                                                                 @Override
                                                                                 public CorporationVictoryPointStat getCUT() {
                                                                                   return new CorporationVictoryPointStat(
                                                                                       corporationID, corporationName, victoryPoints + 1);
                                                                                 }

                                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationVictoryPointStat>() {

      @Override
      public CorporationVictoryPointStat[] getVariants() {
        return new CorporationVictoryPointStat[] {
            new CorporationVictoryPointStat(corporationID + 1, corporationName, victoryPoints),
            new CorporationVictoryPointStat(corporationID, corporationName + "1", victoryPoints),
            new CorporationVictoryPointStat(corporationID, corporationName, victoryPoints + 1)
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
        return CorporationVictoryPointStat.get(time, corporationID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different corporation ID
    // - objects not live at the given time
    CorporationVictoryPointStat existing, keyed;

    keyed = new CorporationVictoryPointStat(corporationID, corporationName, victoryPoints);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different corporation ID
    existing = new CorporationVictoryPointStat(corporationID + 1, corporationName, victoryPoints);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CorporationVictoryPointStat(corporationID, corporationName, victoryPoints + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CorporationVictoryPointStat(corporationID, corporationName, victoryPoints + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CorporationVictoryPointStat result = CorporationVictoryPointStat.get(8889L, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
