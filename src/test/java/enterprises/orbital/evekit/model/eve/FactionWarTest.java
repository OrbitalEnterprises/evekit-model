package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class FactionWarTest extends AbstractRefModelTester<FactionWar> {

  final long                                  againstID   = TestBase.getRandomInt(100000000);
  final String                                againstName = TestBase.getRandomText(50);
  final long                                  factionID   = TestBase.getRandomInt(100000000);
  final String                                factionName = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<FactionWar> eol         = new ClassUnderTestConstructor<FactionWar>() {

                                                            @Override
                                                            public FactionWar getCUT() {
                                                              return new FactionWar(againstID, againstName, factionID, factionName);
                                                            }

                                                          };

  final ClassUnderTestConstructor<FactionWar> live        = new ClassUnderTestConstructor<FactionWar>() {
                                                            @Override
                                                            public FactionWar getCUT() {
                                                              return new FactionWar(againstID, againstName + "1", factionID, factionName);
                                                            }

                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<FactionWar>() {

      @Override
      public FactionWar[] getVariants() {
        return new FactionWar[] {
            new FactionWar(againstID + 1, againstName, factionID, factionName), new FactionWar(againstID, againstName + "1", factionID, factionName),
            new FactionWar(againstID, againstName, factionID + 1, factionName), new FactionWar(againstID, againstName, factionID, factionName + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<FactionWar>() {

      @Override
      public FactionWar getModel(
                                 long time) {
        return FactionWar.get(time, againstID, factionID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different against ID
    // - objects with different faction ID
    // - objects not live at the given time
    FactionWar existing, keyed;

    keyed = new FactionWar(againstID, againstName, factionID, factionName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different against ID
    existing = new FactionWar(againstID + 1, againstName, factionID, factionName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different faction ID
    existing = new FactionWar(againstID, againstName, factionID + 1, factionName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new FactionWar(againstID, againstName + "1", factionID, factionName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new FactionWar(againstID, againstName + "2", factionID, factionName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    FactionWar result = FactionWar.get(8889L, againstID, factionID);
    Assert.assertEquals(keyed, result);
  }

}
