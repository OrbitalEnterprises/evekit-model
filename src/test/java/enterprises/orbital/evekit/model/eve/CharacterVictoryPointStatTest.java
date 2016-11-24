package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CharacterVictoryPointStatTest extends AbstractRefModelTester<CharacterVictoryPointStat> {

  final StatAttribute                                        attribute     = StatAttribute.LAST_WEEK;
  final long                                                 characterID   = TestBase.getRandomInt(100000000);
  final String                                               characterName = TestBase.getRandomText(50);
  final int                                                  victoryPoints = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterVictoryPointStat> eol           = new ClassUnderTestConstructor<CharacterVictoryPointStat>() {

                                                                             @Override
                                                                             public CharacterVictoryPointStat getCUT() {
                                                                               return new CharacterVictoryPointStat(
                                                                                   attribute, victoryPoints, characterID, characterName);
                                                                             }

                                                                           };

  final ClassUnderTestConstructor<CharacterVictoryPointStat> live          = new ClassUnderTestConstructor<CharacterVictoryPointStat>() {
                                                                             @Override
                                                                             public CharacterVictoryPointStat getCUT() {
                                                                               return new CharacterVictoryPointStat(
                                                                                   attribute, victoryPoints + 1, characterID, characterName);
                                                                             }

                                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterVictoryPointStat>() {

      @Override
      public CharacterVictoryPointStat[] getVariants() {
        return new CharacterVictoryPointStat[] {
            new CharacterVictoryPointStat(StatAttribute.TOTAL, victoryPoints, characterID, characterName),
            new CharacterVictoryPointStat(attribute, victoryPoints + 1, characterID, characterName),
            new CharacterVictoryPointStat(attribute, victoryPoints, characterID + 1, characterName),
            new CharacterVictoryPointStat(attribute, victoryPoints, characterID, characterName + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterVictoryPointStat>() {

      @Override
      public CharacterVictoryPointStat getModel(
                                                long time) {
        return CharacterVictoryPointStat.get(time, attribute, characterID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different character ID
    // - objects with different attribute
    // - objects not live at the given time
    CharacterVictoryPointStat existing, keyed;

    keyed = new CharacterVictoryPointStat(attribute, victoryPoints, characterID, characterName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different character ID
    existing = new CharacterVictoryPointStat(attribute, victoryPoints, characterID + 1, characterName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different attribute
    existing = new CharacterVictoryPointStat(StatAttribute.TOTAL, victoryPoints, characterID, characterName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterVictoryPointStat(attribute, victoryPoints + 1, characterID, characterName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterVictoryPointStat(attribute, victoryPoints + 2, characterID, characterName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CharacterVictoryPointStat result = CharacterVictoryPointStat.get(8889L, attribute, characterID);
    Assert.assertEquals(keyed, result);
  }

}
