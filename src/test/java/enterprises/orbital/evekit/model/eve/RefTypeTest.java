package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class RefTypeTest extends AbstractRefModelTester<RefType> {

  final int                                refTypeID   = TestBase.getRandomInt(100000000);
  final String                             refTypeName = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<RefType> eol         = new ClassUnderTestConstructor<RefType>() {

                                                         @Override
                                                         public RefType getCUT() {
                                                           return new RefType(refTypeID, refTypeName);
                                                         }

                                                       };

  final ClassUnderTestConstructor<RefType> live        = new ClassUnderTestConstructor<RefType>() {
                                                         @Override
                                                         public RefType getCUT() {
                                                           return new RefType(refTypeID, refTypeName + "1");
                                                         }

                                                       };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<RefType>() {

      @Override
      public RefType[] getVariants() {
        return new RefType[] {
            new RefType(refTypeID + 1, refTypeName), new RefType(refTypeID, refTypeName + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<RefType>() {

      @Override
      public RefType getModel(
                              long time) {
        return RefType.get(time, refTypeID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different refTypeID
    // - objects not live at the given time
    RefType existing, keyed;

    keyed = new RefType(refTypeID, refTypeName);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different refType ID
    existing = new RefType(refTypeID + 1, refTypeName);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new RefType(refTypeID, refTypeName + "1");
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new RefType(refTypeID, refTypeName + "2");
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    RefType result = RefType.get(8889L, refTypeID);
    Assert.assertEquals(keyed, result);
  }

}
