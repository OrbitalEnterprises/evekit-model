package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionVictoryPointStatTest extends AbstractRefModelTester<FactionVictoryPointStat> {

  final StatAttribute                                      attribute     = StatAttribute.LAST_WEEK;
  final long                                               factionID     = TestBase.getRandomInt(100000000);
  final String                                             factionName   = TestBase.getRandomText(50);
  final int                                                victoryPoints = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FactionVictoryPointStat> eol           = new ClassUnderTestConstructor<FactionVictoryPointStat>() {

                                                                           @Override
                                                                           public FactionVictoryPointStat getCUT() {
                                                                             return new FactionVictoryPointStat(
                                                                                 attribute, victoryPoints, factionID, factionName);
                                                                           }

                                                                         };

  final ClassUnderTestConstructor<FactionVictoryPointStat> live          = new ClassUnderTestConstructor<FactionVictoryPointStat>() {
                                                                           @Override
                                                                           public FactionVictoryPointStat getCUT() {
                                                                             return new FactionVictoryPointStat(
                                                                                 attribute, victoryPoints + 1, factionID, factionName);
                                                                           }

                                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionVictoryPointStat>() {

      @Override
      public FactionVictoryPointStat[] getVariants() {
        return new FactionVictoryPointStat[] {
            new FactionVictoryPointStat(StatAttribute.TOTAL, victoryPoints, factionID, factionName),
            new FactionVictoryPointStat(attribute, victoryPoints + 1, factionID, factionName),
            new FactionVictoryPointStat(attribute, victoryPoints, factionID + 1, factionName),
            new FactionVictoryPointStat(attribute, victoryPoints, factionID, factionName + "1")
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
        return FactionVictoryPointStat.get(time, attribute, factionID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects with different attribute
    // - objects not live at the given time
    FactionVictoryPointStat existing, keyed;

    keyed = new FactionVictoryPointStat(attribute, victoryPoints, factionID, factionName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different faction ID
    existing = new FactionVictoryPointStat(attribute, victoryPoints, factionID + 1, factionName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different attribute
    existing = new FactionVictoryPointStat(StatAttribute.TOTAL, victoryPoints, factionID, factionName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionVictoryPointStat(attribute, victoryPoints + 1, factionID, factionName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionVictoryPointStat(attribute, victoryPoints + 2, factionID, factionName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionVictoryPointStat result = FactionVictoryPointStat.get(8889L, attribute, factionID);
    Assert.assertEquals(keyed, result);
  }

}
