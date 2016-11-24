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
                                                                                  groupID, typeID, description, rank, requiredPrimaryAttribute,
                                                                                  requiredSecondaryAttribute, typeName, published);
                                                                            }

                                                                          };

  final ClassUnderTestConstructor<SkillMember> live                       = new ClassUnderTestConstructor<SkillMember>() {
                                                                            @Override
                                                                            public SkillMember getCUT() {
                                                                              return new SkillMember(
                                                                                  groupID, typeID, description + "1", rank, requiredPrimaryAttribute,
                                                                                  requiredSecondaryAttribute, typeName, published);
                                                                            }

                                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SkillMember>() {

      @Override
      public SkillMember[] getVariants() {
        return new SkillMember[] {
            new SkillMember(groupID + 1, typeID, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published),
            new SkillMember(groupID, typeID + 1, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published),
            new SkillMember(groupID, typeID, description + "1", rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published),
            new SkillMember(groupID, typeID, description, rank + 1, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published),
            new SkillMember(groupID, typeID, description, rank, requiredPrimaryAttribute + "1", requiredSecondaryAttribute, typeName, published),
            new SkillMember(groupID, typeID, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute + "1", typeName, published),
            new SkillMember(groupID, typeID, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName + "1", published),
            new SkillMember(groupID, typeID, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, !published)
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
        return SkillMember.get(time, typeID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different type ID
    // - objects not live at the given time
    SkillMember existing, keyed;

    keyed = new SkillMember(groupID, typeID, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different type ID
    existing = new SkillMember(groupID, typeID + 1, description, rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new SkillMember(groupID, typeID, description + "1", rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new SkillMember(groupID, typeID, description + "2", rank, requiredPrimaryAttribute, requiredSecondaryAttribute, typeName, published);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    SkillMember result = SkillMember.get(8889L, typeID);
    Assert.assertEquals(keyed, result);
  }

}
