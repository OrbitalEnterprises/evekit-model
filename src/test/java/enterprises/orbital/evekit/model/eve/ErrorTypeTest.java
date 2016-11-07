package enterprises.orbital.evekit.model.eve;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class ErrorTypeTest extends AbstractRefModelTester<ErrorType> {

  final int                                  errorCode = TestBase.getRandomInt(100000000);
  final String                               errorText = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<ErrorType> eol       = new ClassUnderTestConstructor<ErrorType>() {

                                                         @Override
                                                         public ErrorType getCUT() {
                                                           return new ErrorType(errorCode, errorText);
                                                         }

                                                       };

  final ClassUnderTestConstructor<ErrorType> live      = new ClassUnderTestConstructor<ErrorType>() {
                                                         @Override
                                                         public ErrorType getCUT() {
                                                           return new ErrorType(errorCode, errorText + "1");
                                                         }

                                                       };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ErrorType>() {

      @Override
      public ErrorType[] getVariants() {
        return new ErrorType[] {
            new ErrorType(errorCode + 1, errorText), new ErrorType(errorCode, errorText + "1")
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ErrorType>() {

      @Override
      public ErrorType getModel(
                                long time) {
        return ErrorType.get(time, errorCode);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - objects with different errorCode
    // - objects not live at the given time
    ErrorType existing, keyed;

    keyed = new ErrorType(errorCode, errorText);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Different errorCode
    existing = new ErrorType(errorCode + 1, errorText);
    existing.setup(8888L);
    RefCachedData.updateData(existing);

    // Not live at the given time
    existing = new ErrorType(errorCode, errorText + "1");
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new ErrorType(errorCode, errorText + "2");
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    ErrorType result = ErrorType.get(8889L, errorCode);
    Assert.assertEquals(keyed, result);
  }

}
