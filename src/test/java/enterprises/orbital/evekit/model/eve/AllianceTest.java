package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class AllianceTest extends AbstractRefModelTester<Alliance> {

  final long                                allianceID     = TestBase.getRandomInt(100000000);
  final long                                executorCorpID = TestBase.getRandomInt(100000000);
  final int                                 memberCount    = TestBase.getRandomInt(100000000);
  final String                              name           = TestBase.getRandomText(50);
  final String                              shortName      = TestBase.getRandomText(50);
  final long                                startDate      = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Alliance> eol            = new ClassUnderTestConstructor<Alliance>() {

                                                             @Override
                                                             public Alliance getCUT() {
                                                               return new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate);
                                                             }

                                                           };

  final ClassUnderTestConstructor<Alliance> live           = new ClassUnderTestConstructor<Alliance>() {
                                                             @Override
                                                             public Alliance getCUT() {
                                                               return new Alliance(allianceID, executorCorpID, memberCount + 1, name, shortName, startDate);
                                                             }

                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Alliance>() {

      @Override
      public Alliance[] getVariants() {
        return new Alliance[] {
            new Alliance(allianceID + 1, executorCorpID, memberCount, name, shortName, startDate),
            new Alliance(allianceID, executorCorpID + 1, memberCount, name, shortName, startDate),
            new Alliance(allianceID, executorCorpID, memberCount + 1, name, shortName, startDate),
            new Alliance(allianceID, executorCorpID, memberCount, name + "1", shortName, startDate),
            new Alliance(allianceID, executorCorpID, memberCount, name, shortName + "1", startDate),
            new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Alliance>() {

      @Override
      public Alliance getModel(
                               long time) {
        return Alliance.get(time, allianceID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different alliance ID
    // - objects not live at the given time
    Alliance existing, keyed;

    keyed = new Alliance(allianceID, executorCorpID, memberCount, name, shortName, startDate);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different alliance ID
    existing = new Alliance(allianceID + 1, executorCorpID, memberCount, name, shortName, startDate);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new Alliance(allianceID, executorCorpID, memberCount + 1, name, shortName, startDate);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new Alliance(allianceID, executorCorpID, memberCount + 2, name, shortName, startDate);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    Alliance result = Alliance.get(8889L, allianceID);
    Assert.assertEquals(keyed, result);
  }

}
