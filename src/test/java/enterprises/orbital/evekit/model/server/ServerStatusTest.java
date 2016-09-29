package enterprises.orbital.evekit.model.server;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.model.AbstractRefModelTester;
import enterprises.orbital.evekit.model.RefCachedData;

public class ServerStatusTest extends AbstractRefModelTester<ServerStatus> {

  final int                                     onlinePlayers = TestBase.getRandomInt(100000000);
  final boolean                                 serverOpen    = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<ServerStatus> eol           = new ClassUnderTestConstructor<ServerStatus>() {

                                                                @Override
                                                                public ServerStatus getCUT() {
                                                                  return new ServerStatus(onlinePlayers, serverOpen);
                                                                }

                                                              };

  final ClassUnderTestConstructor<ServerStatus> live          = new ClassUnderTestConstructor<ServerStatus>() {
                                                                @Override
                                                                public ServerStatus getCUT() {
                                                                  return new ServerStatus(onlinePlayers + 1, serverOpen);
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ServerStatus>() {

      @Override
      public ServerStatus[] getVariants() {
        return new ServerStatus[] {
            new ServerStatus(onlinePlayers + 1, serverOpen), new ServerStatus(onlinePlayers, !serverOpen)
        };
      }

    });
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ServerStatus>() {

      @Override
      public ServerStatus getModel(
                                   long time) {
        return ServerStatus.get(time);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - statuses not live at the given time
    ServerStatus existing, keyed;

    keyed = new ServerStatus(onlinePlayers, serverOpen);
    keyed.setup(8888L);
    keyed = RefCachedData.updateData(keyed);

    // Not live at the given time
    existing = new ServerStatus(onlinePlayers + 3, serverOpen);
    existing.setup(9999L);
    RefCachedData.updateData(existing);

    // EOL before the given time
    existing = new ServerStatus(onlinePlayers + 4, serverOpen);
    existing.setup(7777L);
    existing.evolve(null, 7977L);
    RefCachedData.updateData(existing);

    ServerStatus result = ServerStatus.get(8889L);
    Assert.assertEquals(keyed, result);
  }

}
