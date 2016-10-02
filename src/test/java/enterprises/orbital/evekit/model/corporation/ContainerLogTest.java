package enterprises.orbital.evekit.model.corporation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;

public class ContainerLogTest extends AbstractModelTester<ContainerLog> {

  final long                                    logTime          = TestBase.getRandomInt(100000000);
  final String                                  action           = "test action";
  final long                                    actorID          = TestBase.getRandomInt(100000000);
  final String                                  actorName        = "test actor name";
  final int                                     flag             = TestBase.getRandomInt(100000000);
  final long                                    itemID           = TestBase.getRandomInt(100000000);
  final int                                     itemTypeID       = TestBase.getRandomInt(100000000);
  final long                                    locationID       = TestBase.getRandomInt(100000000);
  final String                                  newConfiguration = "new test config";
  final String                                  oldConfiguration = "old test config";
  final String                                  passwordType     = "test password type";
  final long                                    quantity         = TestBase.getRandomInt(100000000);
  final int                                     typeID           = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<ContainerLog> eol              = new ClassUnderTestConstructor<ContainerLog>() {

                                                                   @Override
                                                                   public ContainerLog getCUT() {
                                                                     return new ContainerLog(
                                                                         logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID,
                                                                         newConfiguration, oldConfiguration, passwordType, quantity, typeID);
                                                                   }

                                                                 };

  final ClassUnderTestConstructor<ContainerLog> live             = new ClassUnderTestConstructor<ContainerLog>() {
                                                                   @Override
                                                                   public ContainerLog getCUT() {
                                                                     return new ContainerLog(
                                                                         logTime, action, actorID + 1, actorName, flag, itemID, itemTypeID, locationID,
                                                                         newConfiguration, oldConfiguration, passwordType, quantity, typeID);
                                                                   }

                                                                 };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ContainerLog>() {

      @Override
      public ContainerLog[] getVariants() {
        return new ContainerLog[] {
            new ContainerLog(
                logTime + 1, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action + " 1", actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID + 1, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName + " 1", flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag + 1, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID + 1, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID + 1, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID + 1, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration + 1, oldConfiguration, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration + 1, passwordType, quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType + " 1", quantity,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity + 1,
                typeID),
            new ContainerLog(
                logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity,
                typeID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTAINER_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ContainerLog>() {

      @Override
      public ContainerLog getModel(
                                   SynchronizedEveAccount account,
                                   long time) {
        return ContainerLog.get(account, time, logTime);
      }

    });
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - logs for a different account
    // - logs not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    ContainerLog existing;
    Map<Long, ContainerLog> listCheck = new HashMap<Long, ContainerLog>();

    existing = new ContainerLog(
        logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime, existing);

    existing = new ContainerLog(
        logTime + 10, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime + 10, existing);

    existing = new ContainerLog(
        logTime + 20, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime + 20, existing);

    existing = new ContainerLog(
        logTime + 30, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime + 30, existing);

    // Associated with different account
    existing = new ContainerLog(
        logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ContainerLog(
        logTime + 5, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ContainerLog(
        logTime + 3, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all jobsare returned
    List<ContainerLog> result = ContainerLog.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (ContainerLog next : result) {
      long logTime = next.getLogTime();
      Assert.assertTrue(listCheck.containsKey(logTime));
      Assert.assertEquals(listCheck.get(logTime), next);
    }

    // Verify limited set returned
    result = ContainerLog.getAllForward(testAccount, 8888L, 2, logTime - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(logTime), result.get(0));
    Assert.assertEquals(listCheck.get(logTime + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = ContainerLog.getAllForward(testAccount, 8888L, 100, logTime + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(logTime + 20), result.get(0));
    Assert.assertEquals(listCheck.get(logTime + 30), result.get(1));
  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - logs for a different account
    // - logs not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    ContainerLog existing;
    Map<Long, ContainerLog> listCheck = new HashMap<Long, ContainerLog>();

    existing = new ContainerLog(
        logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime, existing);

    existing = new ContainerLog(
        logTime + 10, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime + 10, existing);

    existing = new ContainerLog(
        logTime + 20, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime + 20, existing);

    existing = new ContainerLog(
        logTime + 30, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(logTime + 30, existing);

    // Associated with different account
    existing = new ContainerLog(
        logTime, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ContainerLog(
        logTime + 5, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ContainerLog(
        logTime + 3, action, actorID, actorName, flag, itemID, itemTypeID, locationID, newConfiguration, oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all logs are returned
    List<ContainerLog> result = ContainerLog.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (ContainerLog next : result) {
      long logTime = next.getLogTime();
      Assert.assertTrue(listCheck.containsKey(logTime));
      Assert.assertEquals(listCheck.get(logTime), next);
    }

    // Verify limited set returned
    result = ContainerLog.getAllBackward(testAccount, 8888L, 2, logTime + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(logTime + 30), result.get(0));
    Assert.assertEquals(listCheck.get(logTime + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = ContainerLog.getAllBackward(testAccount, 8888L, 100, logTime + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(logTime + 10), result.get(0));
    Assert.assertEquals(listCheck.get(logTime), result.get(1));
  }

}
