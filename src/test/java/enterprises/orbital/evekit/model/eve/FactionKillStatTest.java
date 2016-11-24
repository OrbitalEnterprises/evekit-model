package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionKillStatTest extends AbstractRefModelTester<FactionKillStat> {

  final StatAttribute                              attribute   = StatAttribute.LAST_WEEK;
  final long                                       factionID   = TestBase.getRandomInt(100000000);
  final String                                     factionName = TestBase.getRandomText(50);
  final int                                        kills       = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<FactionKillStat> eol         = new ClassUnderTestConstructor<FactionKillStat>() {

                                                                 @Override
                                                                 public FactionKillStat getCUT() {
                                                                   return new FactionKillStat(attribute, kills, factionID, factionName);
                                                                 }

                                                               };

  final ClassUnderTestConstructor<FactionKillStat> live        = new ClassUnderTestConstructor<FactionKillStat>() {
                                                                 @Override
                                                                 public FactionKillStat getCUT() {
                                                                   return new FactionKillStat(attribute, kills + 1, factionID, factionName);
                                                                 }

                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionKillStat>() {

      @Override
      public FactionKillStat[] getVariants() {
        return new FactionKillStat[] {
            new FactionKillStat(StatAttribute.YESTERDAY, kills, factionID, factionName), new FactionKillStat(attribute, kills + 1, factionID, factionName),
            new FactionKillStat(attribute, kills, factionID + 1, factionName), new FactionKillStat(attribute, kills, factionID, factionName + "1")
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
        return FactionKillStat.get(time, attribute, factionID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different faction ID
    // - objects with different attribute
    // - objects not live at the given time
    FactionKillStat existing, keyed;

    keyed = new FactionKillStat(attribute, kills, factionID, factionName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different faction ID
    existing = new FactionKillStat(attribute, kills, factionID + 1, factionName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different attribute
    existing = new FactionKillStat(StatAttribute.YESTERDAY, kills, factionID, factionName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionKillStat(attribute, kills + 1, factionID, factionName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionKillStat(attribute, kills + 2, factionID, factionName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionKillStat result = FactionKillStat.get(8889L, attribute, factionID);
    Assert.assertEquals(keyed, result);
  }

}
