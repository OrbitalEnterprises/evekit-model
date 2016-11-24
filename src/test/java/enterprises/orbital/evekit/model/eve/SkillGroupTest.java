package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class SkillGroupTest extends AbstractRefModelTester<SkillGroup> {

  final int                                   groupID   = TestBase.getRandomInt(100000000);
  final String                                groupName = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<SkillGroup> eol       = new ClassUnderTestConstructor<SkillGroup>() {

                                                          @Override
                                                          public SkillGroup getCUT() {
                                                            return new SkillGroup(groupID, groupName);
                                                          }

                                                        };

  final ClassUnderTestConstructor<SkillGroup> live      = new ClassUnderTestConstructor<SkillGroup>() {
                                                          @Override
                                                          public SkillGroup getCUT() {
                                                            return new SkillGroup(groupID, groupName + "1");
                                                          }

                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SkillGroup>() {

      @Override
      public SkillGroup[] getVariants() {
        return new SkillGroup[] {
            new SkillGroup(groupID + 1, groupName), new SkillGroup(groupID, groupName + "1")
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
        return SkillGroup.get(time, groupID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different groupID
    // - objects not live at the given time
    SkillGroup existing, keyed;

    keyed = new SkillGroup(groupID, groupName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different groupID
    existing = new SkillGroup(groupID + 1, groupName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new SkillGroup(groupID, groupName + "1");
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new SkillGroup(groupID, groupName + "2");
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    SkillGroup result = SkillGroup.get(8889L, groupID);
    Assert.assertEquals(keyed, result);
  }

}
