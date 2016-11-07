package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class SkillMemberTest extends AbstractRefModelTester<SkillMember> {

  final int                                    groupID                    = TestBase.getRandomInt(100000000);
  final int                                    typeID                     = TestBase.getRandomInt(100000000);
  final String                                 description                = TestBase.getRandomText(50);
  final int                                    rank                       = TestBase.getRandomInt(100000000);
  final String                                 requiredPrimaryAttribute   = TestBase.getRandomText(50);
  final String                                 requiredSecondaryAttribute = TestBase.getRandomText(50);
  final String                                 typeName                   = TestBase.getRandomText(50);
  final boolean                                published                  = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<SkillMember> eol                        = new ClassUnderTestConstructor<SkillMember>() {

                                                                            @Override
                                                                            public SkillMember getCUT() {
                                                                              return new SkillMember(
                                                                                  description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute,
                                                                                  typeID, typeName, published);
                                                                            }

                                                                          };

  final ClassUnderTestConstructor<SkillMember> live                       = new ClassUnderTestConstructor<SkillMember>() {
                                                                            @Override
                                                                            public SkillMember getCUT() {
                                                                              return new SkillMember(
                                                                                  description + "1", groupID, requiredPrimaryAttribute,
                                                                                  requiredSecondaryAttribute, typeID, typeName, published);
                                                                            }

                                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SkillMember>() {

      @Override
      public SkillMember[] getVariants() {
        return new SkillMember[] {
            new SkillMember(description + "1", groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, published),
            new SkillMember(description, groupID + 1, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, published),
            new SkillMember(description, groupID, requiredPrimaryAttribute + "1", requiredSecondaryAttribute, typeID, typeName, published),
            new SkillMember(description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute + "1", typeID, typeName, published),
            new SkillMember(description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID + 1, typeName, published),
            new SkillMember(description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName + "1", published),
            new SkillMember(description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, !published)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<SkillMember>() {

      @Override
      public SkillMember getModel(
                                  long time) {
        return SkillMember.get(time, groupID, typeID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different group ID
    // - objects with different type ID
    // - objects not live at the given time
    SkillMember existing, keyed;

    keyed = new SkillMember(description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, published);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different group ID
    existing = new SkillMember(description, groupID + 1, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, published);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different type ID
    existing = new SkillMember(description, groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID + 1, typeName, published);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new SkillMember(description + "1", groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, published);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new SkillMember(description + "2", groupID, requiredPrimaryAttribute, requiredSecondaryAttribute, typeID, typeName, published);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    SkillMember result = SkillMember.get(8889L, groupID, typeID);
    Assert.assertEquals(keyed, result);
  }

}
