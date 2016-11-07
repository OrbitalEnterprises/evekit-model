package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CorporationKillStatTest extends AbstractRefModelTester<CorporationKillStat> {

  final long                                           corporationID   = TestBase.getRandomInt(100000000);
  final String                                         corporationName = TestBase.getRandomText(50);
  final int                                            kills           = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CorporationKillStat> eol             = new ClassUnderTestConstructor<CorporationKillStat>() {

                                                                         @Override
                                                                         public CorporationKillStat getCUT() {
                                                                           return new CorporationKillStat(corporationID, corporationName, kills);
                                                                         }

                                                                       };

  final ClassUnderTestConstructor<CorporationKillStat> live            = new ClassUnderTestConstructor<CorporationKillStat>() {
                                                                         @Override
                                                                         public CorporationKillStat getCUT() {
                                                                           return new CorporationKillStat(corporationID, corporationName, kills + 1);
                                                                         }

                                                                       };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationKillStat>() {

      @Override
      public CorporationKillStat[] getVariants() {
        return new CorporationKillStat[] {
            new CorporationKillStat(corporationID + 1, corporationName, kills), new CorporationKillStat(corporationID, corporationName + "1", kills),
            new CorporationKillStat(corporationID, corporationName, kills + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CorporationKillStat>() {

      @Override
      public CorporationKillStat getModel(
                                          long time) {
        return CorporationKillStat.get(time, corporationID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different corporation ID
    // - objects not live at the given time
    CorporationKillStat existing, keyed;

    keyed = new CorporationKillStat(corporationID, corporationName, kills);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different corporation ID
    existing = new CorporationKillStat(corporationID + 1, corporationName, kills);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CorporationKillStat(corporationID, corporationName, kills + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CorporationKillStat(corporationID, corporationName, kills + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CorporationKillStat result = CorporationKillStat.get(8889L, corporationID);
    Assert.assertEquals(keyed, result);
  }

}
