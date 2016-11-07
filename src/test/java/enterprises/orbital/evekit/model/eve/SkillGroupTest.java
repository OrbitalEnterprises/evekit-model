package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class SkillGroupTest extends AbstractRefModelTester<SkillGroup> {

  final String                                type        = TestBase.getRandomText(50);
  final String                                name        = TestBase.getRandomText(50);
  final long                                  groupID     = TestBase.getRandomInt(100000000);
  final String                                description = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<SkillGroup> eol         = new ClassUnderTestConstructor<SkillGroup>() {

                                                            @Override
                                                            public SkillGroup getCUT() {
                                                              return new SkillGroup(type, name, groupID, description);
                                                            }

                                                          };

  final ClassUnderTestConstructor<SkillGroup> live        = new ClassUnderTestConstructor<SkillGroup>() {
                                                            @Override
                                                            public SkillGroup getCUT() {
                                                              return new SkillGroup(type, name, groupID + 1, description);
                                                            }

                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SkillGroup>() {

      @Override
      public SkillGroup[] getVariants() {
        return new SkillGroup[] {
            new SkillGroup(type + "1", name, groupID, description), new SkillGroup(type, name + "1", groupID, description),
            new SkillGroup(type, name, groupID + 1, description), new SkillGroup(type, name, groupID, description + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<SkillGroup>() {

      @Override
      public SkillGroup getModel(
                                 long time) {
        return SkillGroup.get(time, type, name);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different type
    // - objects with different name
    // - objects not live at the given time
    SkillGroup existing, keyed;

    keyed = new SkillGroup(type, name, groupID, description);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different type
    existing = new SkillGroup(type + "1", name, groupID, description);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different name
    existing = new SkillGroup(type, name + "1", groupID, description);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new SkillGroup(type, name, groupID + 1, description);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new SkillGroup(type, name, groupID + 2, description);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    SkillGroup result = SkillGroup.get(8889L, type, name);
    Assert.assertEquals(keyed, result);
  }

}
