package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerLogTest extends AbstractModelTester<ContainerLog> {

  private final long logTime = TestBase.getRandomInt(100000000);
  private final String action = "test action";
  private final int characterID = TestBase.getRandomInt(100000000);
  private final String locationFlag = TestBase.getRandomText(50);
  private final long containerID = TestBase.getRandomInt(100000000);
  private final int containerTypeID = TestBase.getRandomInt(100000000);
  private final long locationID = TestBase.getRandomInt(100000000);
  private final int newConfiguration = TestBase.getRandomInt(100000000);
  private final int oldConfiguration = TestBase.getRandomInt(100000000);
  private final String passwordType = "test password type";
  private final int quantity = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<ContainerLog> eol = () -> new ContainerLog(
      logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID,
      newConfiguration, oldConfiguration, passwordType, quantity, typeID);

  final ClassUnderTestConstructor<ContainerLog> live = () -> new ContainerLog(
      logTime, action, characterID + 1, locationFlag, containerID, containerTypeID, locationID,
      newConfiguration, oldConfiguration, passwordType, quantity, typeID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new ContainerLog[]{
        new ContainerLog(
            logTime + 1, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
            oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action + " 1", characterID, locationFlag, containerID, containerTypeID, locationID,
            newConfiguration, oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID + 1, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
            oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag + "1", containerID, containerTypeID, locationID,
            newConfiguration, oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID + 1, containerTypeID, locationID, newConfiguration,
            oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID + 1, locationID, newConfiguration,
            oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID + 1, newConfiguration,
            oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration + 1,
            oldConfiguration, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
            oldConfiguration + 1, passwordType, quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
            oldConfiguration, passwordType + " 1", quantity,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
            oldConfiguration, passwordType, quantity + 1,
            typeID),
        new ContainerLog(
            logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
            oldConfiguration, passwordType, quantity,
            typeID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTAINER_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, (account, time) -> ContainerLog.get(account, time, containerID, logTime));
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - logs for a different account
    // - logs not live at the given time
    ContainerLog existing;
    Map<Long, ContainerLog> listCheck = new HashMap<>();

    existing = new ContainerLog(
        logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(logTime, existing);

    existing = new ContainerLog(
        logTime + 10, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(logTime + 10, existing);

    existing = new ContainerLog(
        logTime + 20, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(logTime + 20, existing);

    existing = new ContainerLog(
        logTime + 30, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(logTime + 30, existing);

    // Associated with different account
    existing = new ContainerLog(
        logTime, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ContainerLog(
        logTime + 5, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ContainerLog(
        logTime + 3, action, characterID, locationFlag, containerID, containerTypeID, locationID, newConfiguration,
        oldConfiguration, passwordType, quantity, typeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all logs are returned
    List<ContainerLog> result = CachedData.retrieveAll(8888L,
                                                       (contid, at) -> ContainerLog.accessQuery(testAccount, contid,
                                                                                                1000, false, at,
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (ContainerLog next : result) {
      long logTime = next.getLogTime();
      Assert.assertTrue(listCheck.containsKey(logTime));
      Assert.assertEquals(listCheck.get(logTime), next);
    }
  }

}
