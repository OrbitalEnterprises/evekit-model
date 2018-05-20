package enterprises.orbital.evekit.model.character;

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

public class FleetInfoTest extends AbstractModelTester<FleetInfo> {
  private final long fleetID = TestBase.getRandomLong();
  private final boolean isFreeMove = TestBase.getRandomBoolean();
  private final boolean isRegistered = TestBase.getRandomBoolean();
  private final boolean isVoiceEnabled = TestBase.getRandomBoolean();
  private final String motd = TestBase.getRandomText(1000);

  final ClassUnderTestConstructor<FleetInfo> eol = () -> new FleetInfo(fleetID, isFreeMove, isRegistered,
                                                                       isVoiceEnabled, motd);

  final ClassUnderTestConstructor<FleetInfo> live = () -> new FleetInfo(fleetID, isFreeMove, isRegistered,
                                                                        isVoiceEnabled, motd + "1");

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new FleetInfo[]{
        new FleetInfo(fleetID + 1, isFreeMove, isRegistered, isVoiceEnabled, motd),
        new FleetInfo(fleetID, !isFreeMove, isRegistered, isVoiceEnabled, motd),
        new FleetInfo(fleetID, isFreeMove, !isRegistered, isVoiceEnabled, motd),
        new FleetInfo(fleetID, isFreeMove, isRegistered, !isVoiceEnabled, motd),
        new FleetInfo(fleetID, isFreeMove, isRegistered, isVoiceEnabled, motd + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> FleetInfo.get(account, time, fleetID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - fleets for a different account
    // - fleets not live at the given time
    FleetInfo existing;
    Map<Long, FleetInfo> listCheck = new HashMap<>();

    existing = new FleetInfo(fleetID, isFreeMove, isRegistered, isVoiceEnabled, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fleetID, existing);

    existing = new FleetInfo(fleetID + 10, isFreeMove, isRegistered, isVoiceEnabled, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fleetID + 10, existing);

    // Associated with different account
    existing = new FleetInfo(fleetID, isFreeMove, isRegistered, isVoiceEnabled, motd);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new FleetInfo(fleetID + 3, isFreeMove, isRegistered, isVoiceEnabled, motd);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new FleetInfo(fleetID + 4, isFreeMove, isRegistered, isVoiceEnabled, motd);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<FleetInfo> result = CachedData.retrieveAll(8888L,
                                                    (contid, at) -> FleetInfo.accessQuery(testAccount, contid, 1000,
                                                                                          false,
                                                                                          at, AttributeSelector.any(),
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (FleetInfo next : result) {
      long fleetID = next.getFleetID();
      Assert.assertTrue(listCheck.containsKey(fleetID));
      Assert.assertEquals(listCheck.get(fleetID), next);
    }

  }

}
