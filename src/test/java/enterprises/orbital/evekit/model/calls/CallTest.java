package enterprises.orbital.evekit.model.calls;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CallTest extends AbstractRefModelTester<Call> {

  final long                            accessMask  = TestBase.getRandomLong();
  final String                          type        = TestBase.getRandomText(50);
  final String                          name        = TestBase.getRandomText(50);
  final long                            groupID     = TestBase.getRandomLong();
  final String                          description = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<Call> eol         = new ClassUnderTestConstructor<Call>() {

                                                      @Override
                                                      public Call getCUT() {
                                                        return new Call(accessMask, type, name, groupID, description);
                                                      }

                                                    };

  final ClassUnderTestConstructor<Call> live        = new ClassUnderTestConstructor<Call>() {
                                                      @Override
                                                      public Call getCUT() {
                                                        return new Call(accessMask, type, name, groupID + 1, description);
                                                      }

                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Call>() {

      @Override
      public Call[] getVariants() {
        return new Call[] {
            new Call(accessMask + 1, type, name, groupID, description), new Call(accessMask, type + "1", name, groupID, description),
            new Call(accessMask, type, name + "1", groupID, description), new Call(accessMask, type, name, groupID + 1, description),
            new Call(accessMask, type, name, groupID, description + "1"),
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Call>() {

      @Override
      public Call getModel(
                           long time) {
        return Call.get(time, type, name);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects not live at the given time
    // - objects with a different type
    // - objects with a different name
    Call existing, keyed;

    keyed = new Call(accessMask, type, name, groupID, description);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different type
    existing = new Call(accessMask, type + "1", name, groupID, description);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Different name
    existing = new Call(accessMask, type, name + "1", groupID, description);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new Call(accessMask, type, name, groupID + 1, description);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new Call(accessMask, type, name, groupID + 2, description);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    Call result = Call.get(8888L, type, name);
    Assert.assertEquals(keyed, result);
  }

}
