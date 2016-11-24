package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class RequiredSkillTest extends AbstractRefModelTester<RequiredSkill> {

  final int                                      parentTypeID = TestBase.getRandomInt(100000000);
  final int                                      typeID       = TestBase.getRandomInt(100000000);
  final int                                      level        = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<RequiredSkill> eol          = new ClassUnderTestConstructor<RequiredSkill>() {

                                                                @Override
                                                                public RequiredSkill getCUT() {
                                                                  return new RequiredSkill(parentTypeID, typeID, level);
                                                                }

                                                              };

  final ClassUnderTestConstructor<RequiredSkill> live         = new ClassUnderTestConstructor<RequiredSkill>() {
                                                                @Override
                                                                public RequiredSkill getCUT() {
                                                                  return new RequiredSkill(parentTypeID, typeID, level + 1);
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<RequiredSkill>() {

      @Override
      public RequiredSkill[] getVariants() {
        return new RequiredSkill[] {
            new RequiredSkill(parentTypeID + 1, typeID, level), new RequiredSkill(parentTypeID, typeID + 1, level),
            new RequiredSkill(parentTypeID, typeID, level + 1)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<RequiredSkill>() {

      @Override
      public RequiredSkill getModel(
                                    long time) {
        return RequiredSkill.get(time, parentTypeID, typeID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different parent type ID
    // - objects with different type ID
    // - objects not live at the given time
    RequiredSkill existing, keyed;

    keyed = new RequiredSkill(parentTypeID, typeID, level);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different parent type ID
    existing = new RequiredSkill(parentTypeID + 1, typeID, level);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different type ID
    existing = new RequiredSkill(parentTypeID, typeID + 1, level);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new RequiredSkill(parentTypeID, typeID, level + 1);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new RequiredSkill(parentTypeID, typeID, level + 2);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    RequiredSkill result = RequiredSkill.get(8889L, parentTypeID, typeID);
    Assert.assertEquals(keyed, result);
  }

}
