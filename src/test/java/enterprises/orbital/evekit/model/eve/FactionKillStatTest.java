package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionKillStatTest extends AbstractRefModelTester<FactionKillStat> {

  final long                                       factionID   = TestBase.getRandomInt(100000000);
  final String                                     factionName = TestBase.getRandomText(50);
  final int                                        kills       = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FactionKillStat> eol         = new ClassUnderTestConstructor<FactionKillStat>() {

                                                                 @Override
                                                                 public FactionKillStat getCUT() {
                                                                   return new FactionKillStat(factionID, factionName, kills);
                                                                 }

                                                               };

  final ClassUnderTestConstructor<FactionKillStat> live        = new ClassUnderTestConstructor<FactionKillStat>() {
                                                                 @Override
                                                                 public FactionKillStat getCUT() {
                                                                   return new FactionKillStat(factionID, factionName, kills + 1);
                                                                 }

                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionKillStat>() {

      @Override
      public FactionKillStat[] getVariants() {
        return new FactionKillStat[] {
            new FactionKillStat(factionID + 1, factionName, kills), new FactionKillStat(factionID, factionName + "1", kills),
            new FactionKillStat(factionID, factionName, kills + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FactionKillStat>() {

      @Override
      public FactionKillStat getModel(
                                      long time) {
        return FactionKillStat.get(time, factionID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects not live at the given time
    FactionKillStat existing, keyed;

    keyed = new FactionKillStat(factionID, factionName, kills);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different faction ID
    existing = new FactionKillStat(factionID + 1, factionName, kills);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionKillStat(factionID, factionName, kills + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionKillStat(factionID, factionName, kills + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionKillStat result = FactionKillStat.get(8889L, factionID);
    Assert.assertEquals(keyed, result);
  }

}
