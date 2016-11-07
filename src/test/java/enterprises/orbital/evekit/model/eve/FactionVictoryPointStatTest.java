package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionVictoryPointStatTest extends AbstractRefModelTester<FactionVictoryPointStat> {

  final long                                               factionID     = TestBase.getRandomInt(100000000);
  final String                                             factionName   = TestBase.getRandomText(50);
  final int                                                victoryPoints = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FactionVictoryPointStat> eol           = new ClassUnderTestConstructor<FactionVictoryPointStat>() {

                                                                           @Override
                                                                           public FactionVictoryPointStat getCUT() {
                                                                             return new FactionVictoryPointStat(factionID, factionName, victoryPoints);
                                                                           }

                                                                         };

  final ClassUnderTestConstructor<FactionVictoryPointStat> live          = new ClassUnderTestConstructor<FactionVictoryPointStat>() {
                                                                           @Override
                                                                           public FactionVictoryPointStat getCUT() {
                                                                             return new FactionVictoryPointStat(factionID, factionName, victoryPoints + 1);
                                                                           }

                                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionVictoryPointStat>() {

      @Override
      public FactionVictoryPointStat[] getVariants() {
        return new FactionVictoryPointStat[] {
            new FactionVictoryPointStat(factionID + 1, factionName, victoryPoints), new FactionVictoryPointStat(factionID, factionName + "1", victoryPoints),
            new FactionVictoryPointStat(factionID, factionName, victoryPoints + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FactionVictoryPointStat>() {

      @Override
      public FactionVictoryPointStat getModel(
                                              long time) {
        return FactionVictoryPointStat.get(time, factionID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects not live at the given time
    FactionVictoryPointStat existing, keyed;

    keyed = new FactionVictoryPointStat(factionID, factionName, victoryPoints);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different faction ID
    existing = new FactionVictoryPointStat(factionID + 1, factionName, victoryPoints);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionVictoryPointStat(factionID, factionName, victoryPoints + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionVictoryPointStat(factionID, factionName, victoryPoints + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionVictoryPointStat result = FactionVictoryPointStat.get(8889L, factionID);
    Assert.assertEquals(keyed, result);
  }

}
