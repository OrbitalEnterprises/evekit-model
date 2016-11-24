package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class SkillBonusTest extends AbstractRefModelTester<SkillBonus> {

  final int                                   typeID     = TestBase.getRandomInt(100000000);
  final String                                bonusType  = TestBase.getRandomText(50);
  final String                                bonusValue = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<SkillBonus> eol        = new ClassUnderTestConstructor<SkillBonus>() {

                                                           @Override
                                                           public SkillBonus getCUT() {
                                                             return new SkillBonus(typeID, bonusType, bonusValue);
                                                           }

                                                         };

  final ClassUnderTestConstructor<SkillBonus> live       = new ClassUnderTestConstructor<SkillBonus>() {
                                                           @Override
                                                           public SkillBonus getCUT() {
                                                             return new SkillBonus(typeID, bonusType, bonusValue + "1");
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SkillBonus>() {

      @Override
      public SkillBonus[] getVariants() {
        return new SkillBonus[] {
            new SkillBonus(typeID + 1, bonusType, bonusValue), new SkillBonus(typeID, bonusType + "1", bonusValue),
            new SkillBonus(typeID, bonusType, bonusValue + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<SkillBonus>() {

      @Override
      public SkillBonus getModel(
                                 long time) {
        return SkillBonus.get(time, typeID, bonusType);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different type ID
    // - objects with different bonus type
    // - objects not live at the given time
    SkillBonus existing, keyed;

    keyed = new SkillBonus(typeID, bonusType, bonusValue);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different type ID
    existing = new SkillBonus(typeID + 1, bonusType, bonusValue);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different bonus type
    existing = new SkillBonus(typeID, bonusType + "1", bonusValue);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new SkillBonus(typeID, bonusType, bonusValue + "1");
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new SkillBonus(typeID, bonusType, bonusValue + "2");
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    SkillBonus result = SkillBonus.get(8889L, typeID, bonusType);
    Assert.assertEquals(keyed, result);
  }

}
