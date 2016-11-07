package enterprises.orbital.evekit.model.calls;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class CallGroupTest extends AbstractRefModelTester<CallGroup> {

  final long                                 groupID     = TestBase.getRandomLong();
  final String                               name        = TestBase.getRandomText(50);
  final String                               description = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<CallGroup> eol         = new ClassUnderTestConstructor<CallGroup>() {

                                                           @Override
                                                           public CallGroup getCUT() {
                                                             return new CallGroup(groupID, name, description);
                                                           }

                                                         };

  final ClassUnderTestConstructor<CallGroup> live        = new ClassUnderTestConstructor<CallGroup>() {
                                                           @Override
                                                           public CallGroup getCUT() {
                                                             return new CallGroup(groupID, name + "1", description);
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CallGroup>() {

      @Override
      public CallGroup[] getVariants() {
        return new CallGroup[] {
            new CallGroup(groupID + 1, name, description), new CallGroup(groupID, name + "1", description), new CallGroup(groupID, name, description + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CallGroup>() {

      @Override
      public CallGroup getModel(
                                long time) {
        return CallGroup.get(time, groupID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects not live at the given time
    // - objects with a different group ID
    CallGroup existing, keyed;

    keyed = new CallGroup(groupID, name, description);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different group ID
    existing = new CallGroup(groupID + 3, name, description);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new CallGroup(groupID, name, description);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new CallGroup(groupID, name, description);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    CallGroup result = CallGroup.get(8888L, groupID);
    Assert.assertEquals(keyed, result);
  }

}
