package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CharacterKillStatTest extends AbstractRefModelTester<CharacterKillStat> {

  final long                                         characterID   = TestBase.getRandomInt(100000000);
  final String                                       characterName = TestBase.getRandomText(50);
  final int                                          kills         = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterKillStat> eol           = new ClassUnderTestConstructor<CharacterKillStat>() {

                                                                     @Override
                                                                     public CharacterKillStat getCUT() {
                                                                       return new CharacterKillStat(characterID, characterName, kills);
                                                                     }

                                                                   };

  final ClassUnderTestConstructor<CharacterKillStat> live          = new ClassUnderTestConstructor<CharacterKillStat>() {
                                                                     @Override
                                                                     public CharacterKillStat getCUT() {
                                                                       return new CharacterKillStat(characterID, characterName, kills + 1);
                                                                     }

                                                                   };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterKillStat>() {

      @Override
      public CharacterKillStat[] getVariants() {
        return new CharacterKillStat[] {
            new CharacterKillStat(characterID + 1, characterName, kills), new CharacterKillStat(characterID, characterName + "1", kills),
            new CharacterKillStat(characterID, characterName, kills + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterKillStat>() {

      @Override
      public CharacterKillStat getModel(
                                        long time) {
        return CharacterKillStat.get(time, characterID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different character ID
    // - objects not live at the given time
    CharacterKillStat existing, keyed;

    keyed = new CharacterKillStat(characterID, characterName, kills);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different character ID
    existing = new CharacterKillStat(characterID + 1, characterName, kills);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterKillStat(characterID, characterName, kills + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterKillStat(characterID, characterName, kills + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CharacterKillStat result = CharacterKillStat.get(8889L, characterID);
    Assert.assertEquals(keyed, result);
  }

}
