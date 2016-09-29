package enterprises.orbital.evekit.model;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.DBPropertyProvider;
import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;

public class AbstractRefModelTester<A extends RefCachedData> {

  @Before
  public void setUp() throws Exception {
    OrbitalProperties.addPropertyFile("ModelTest.properties");
    PersistentProperty.setProvider(new DBPropertyProvider(OrbitalProperties.getGlobalProperty(EveKitUserAccountProvider.USER_ACCOUNT_PU_PROP)));
  }

  @After
  public void tearDown() throws IOException {}

  public interface ClassUnderTestConstructor<A> {
    public A getCUT();
  }

  public interface ModelRetriever<A> {
    public A getModel(
                      long time);
  }

  public interface CtorVariants<A> {
    public A[] getVariants();
  }

  // Standard checks for all model types:
  //
  // @formatter:off
  // 1) Equiv check is correct
  // 2) Init sets proper timeline
  // 3) Get retrieval works for (this test uses two elements):
  //    a) EOL element at a given time
  //    b) Current live element
  //    c) No element at the given time
  // 4) Test other non-standard retrieval variants
  // @formatter:on

  protected void runBasicTests(
                               ClassUnderTestConstructor<A> ctor,
                               CtorVariants<A> vars) {
    long time = TestBase.getRandomInt(100000000) + 5000L;
    A cut = ctor.getCUT();
    cut.setup(time);

    A equiv = ctor.getCUT();
    equiv.setup(time);
    Assert.assertTrue(cut.equivalent(equiv));
    for (A next : vars.getVariants()) {
      next.setup(time);
      Assert.assertFalse(cut.equivalent(next));
    }

    Assert.assertEquals(time, cut.getLifeStart());
    Assert.assertEquals(Long.MAX_VALUE, cut.getLifeEnd());
  }

  protected void runGetLifelineTest(
                                    ClassUnderTestConstructor<A> eolMaker,
                                    ClassUnderTestConstructor<A> liveMaker,
                                    ModelRetriever<A> modelGetter) {
    A eol, live;
    long t1 = TestBase.getRandomInt(10000000) + 5000L;
    long t2 = t1 + TestBase.getRandomInt(10000) + 5000L;
    eol = eolMaker.getCUT();
    eol.setup(t1);
    live = liveMaker.getCUT();
    live.setup(t2);
    eol.evolve(live, t2);
    eol = RefCachedData.updateData(eol);
    Assert.assertNotNull(eol);
    live = RefCachedData.updateData(live);
    Assert.assertNotNull(live);
    Assert.assertNotNull(RefModelTypeMap.retrieve(eol.getCid()));
    Assert.assertNotNull(RefModelTypeMap.retrieve(live.getCid()));

    A eolCheck = modelGetter.getModel(t1 + 5);
    A liveCheck = modelGetter.getModel(t2 + 5);
    A missingCheck = modelGetter.getModel(t1 - 5);

    Assert.assertNotNull(eolCheck);
    Assert.assertTrue(eol.equivalent(eolCheck));
    Assert.assertNotNull(liveCheck);
    Assert.assertTrue(live.equivalent(liveCheck));
    Assert.assertNull(missingCheck);
  }

}
