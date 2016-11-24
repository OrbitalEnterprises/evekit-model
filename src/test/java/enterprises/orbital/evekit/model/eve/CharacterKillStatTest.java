package enterprises.orbital.evekit.model.eve;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

public class CharacterKillStatTest extends AbstractRefModelTester<CharacterKillStat> {

  final StatAttribute                                attribute     = StatAttribute.LAST_WEEK;
  final long                                         characterID   = TestBase.getRandomInt(100000000);
  final String                                       characterName = TestBase.getRandomText(50);
  final int                                          kills         = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterKillStat> eol           = new ClassUnderTestConstructor<CharacterKillStat>() {

                                                                     @Override
                                                                     public CharacterKillStat getCUT() {
                                                                       return new CharacterKillStat(attribute, kills, characterID, characterName);
                                                                     }

                                                                   };

  final ClassUnderTestConstructor<CharacterKillStat> live          = new ClassUnderTestConstructor<CharacterKillStat>() {
                                                                     @Override
                                                                     public CharacterKillStat getCUT() {
                                                                       return new CharacterKillStat(attribute, kills + 1, characterID, characterName);
                                                                     }

                                                                   };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterKillStat>() {

      @Override
      public CharacterKillStat[] getVariants() {
        return new CharacterKillStat[] {
            new CharacterKillStat(StatAttribute.TOTAL, kills, characterID, characterName),
            new CharacterKillStat(attribute, kills + 1, characterID, characterName), new CharacterKillStat(attribute, kills, characterID + 1, characterName),
            new CharacterKillStat(attribute, kills, characterID, characterName + "1")
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
        return CharacterKillStat.get(time, attribute, characterID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different character ID
    // - objects with a different attribute
    // - objects not live at the given time
    CharacterKillStat existing, keyed;

    keyed = new CharacterKillStat(attribute, kills, characterID, characterName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different character ID
    existing = new CharacterKillStat(attribute, kills, characterID + 1, characterName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different attribute
    existing = new CharacterKillStat(StatAttribute.TOTAL, kills, characterID, characterName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterKillStat(attribute, kills + 1, characterID, characterName);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterKillStat(attribute, kills + 2, characterID, characterName);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CharacterKillStat result = CharacterKillStat.get(8889L, attribute, characterID);
    Assert.assertEquals(keyed, result);
  }

  @Test
  public void testEnumSelector() throws Exception {
    // Convenient selector
    // Setup three different selectors
    long stamp = 5555L;
    CharacterKillStat lw, t, y;
    lw = new CharacterKillStat(StatAttribute.LAST_WEEK, kills, characterID, characterName);
    lw.setup(stamp);
    lw = RefCachedData.updateData(lw);
    t = new CharacterKillStat(StatAttribute.TOTAL, kills, characterID, characterName);
    t.setup(stamp);
    t = RefCachedData.updateData(t);
    y = new CharacterKillStat(StatAttribute.YESTERDAY, kills, characterID, characterName);
    y.setup(stamp);
    y = RefCachedData.updateData(y);
    // Verify attribute selector works properly
    final AttributeSelector ANY_SELECTOR = new AttributeSelector("{ any: true }");
    final AttributeSelector atSel = new AttributeSelector("{values: [" + stamp + "]}");
    // Single select
    List<CharacterKillStat> sel = CharacterKillStat.accessQuery(-1, 1000, false, atSel, new AttributeSelector("{values: ['LAST_WEEK']}"), ANY_SELECTOR,
                                                                ANY_SELECTOR, ANY_SELECTOR);
    Assert.assertEquals(1, sel.size());
    Assert.assertEquals(lw, sel.get(0));
    // Multi select - note that results should be returned in order of created
    sel = CharacterKillStat.accessQuery(-1, 1000, false, atSel, new AttributeSelector("{values: ['LAST_WEEK', 'YESTERDAY']}"), ANY_SELECTOR, ANY_SELECTOR,
                                        ANY_SELECTOR);
    Assert.assertEquals(2, sel.size());
    Assert.assertEquals(lw, sel.get(0));
    Assert.assertEquals(y, sel.get(1));
    // Check last pair
    sel = CharacterKillStat.accessQuery(-1, 1000, false, atSel, new AttributeSelector("{values: ['YESTERDAY', 'TOTAL']}"), ANY_SELECTOR, ANY_SELECTOR,
                                        ANY_SELECTOR);
    Assert.assertEquals(2, sel.size());
    Assert.assertEquals(t, sel.get(0));
    Assert.assertEquals(y, sel.get(1));
  }

}
