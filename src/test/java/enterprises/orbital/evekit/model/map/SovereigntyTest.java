package enterprises.orbital.evekit.model.map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class SovereigntyTest extends AbstractRefModelTester<Sovereignty> {

  final long                                   allianceID      = TestBase.getRandomInt(100000000);
  final long                                   corporationID   = TestBase.getRandomInt(100000000);
  final long                                   factionID       = TestBase.getRandomInt(100000000);
  final int                                    solarSystemID   = TestBase.getRandomInt(100000000);
  final String                                 solarSystemName = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<Sovereignty> eol             = new ClassUnderTestConstructor<Sovereignty>() {

                                                                 @Override
                                                                 public Sovereignty getCUT() {
                                                                   return new Sovereignty(allianceID, corporationID, factionID, solarSystemID, solarSystemName);
                                                                 }

                                                               };

  final ClassUnderTestConstructor<Sovereignty> live            = new ClassUnderTestConstructor<Sovereignty>() {
                                                                 @Override
                                                                 public Sovereignty getCUT() {
                                                                   return new Sovereignty(
                                                                       allianceID + 1, corporationID, factionID, solarSystemID, solarSystemName);
                                                                 }

                                                               };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Sovereignty>() {

      @Override
      public Sovereignty[] getVariants() {
        return new Sovereignty[] {
            new Sovereignty(allianceID + 1, corporationID, factionID, solarSystemID, solarSystemName),
            new Sovereignty(allianceID, corporationID + 1, factionID, solarSystemID, solarSystemName),
            new Sovereignty(allianceID, corporationID, factionID + 1, solarSystemID, solarSystemName),
            new Sovereignty(allianceID, corporationID, factionID, solarSystemID + 1, solarSystemName),
            new Sovereignty(allianceID, corporationID, factionID, solarSystemID, solarSystemName + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Sovereignty>() {

      @Override
      public Sovereignty getModel(
                                  long time) {
        return Sovereignty.get(time, solarSystemID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different solar system ID
    // - objects not live at the given time
    Sovereignty existing, keyed;

    keyed = new Sovereignty(allianceID, corporationID, factionID, solarSystemID, solarSystemName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different solar system ID
    existing = new Sovereignty(allianceID, corporationID, factionID, solarSystemID + 1, solarSystemName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new Sovereignty(allianceID + 1, corporationID, factionID, solarSystemID, solarSystemName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new Sovereignty(allianceID + 2, corporationID, factionID, solarSystemID, solarSystemName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    Sovereignty result = Sovereignty.get(8889L, solarSystemID);
    Assert.assertEquals(keyed, result);
  }

}
