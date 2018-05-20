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

public class CharacterFleetTest extends AbstractModelTester<CharacterFleet> {
  private final long fleetID = TestBase.getRandomLong();
  private final String role = TestBase.getRandomText(50);
  private final long squadID = TestBase.getRandomLong();
  private final long wingID = TestBase.getRandomLong();

  final ClassUnderTestConstructor<CharacterFleet> eol = () -> new CharacterFleet(fleetID, role, squadID, wingID);

  final ClassUnderTestConstructor<CharacterFleet> live = () -> new CharacterFleet(fleetID, role, squadID + 1, wingID);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new CharacterFleet[]{
        new CharacterFleet(fleetID + 1, role, squadID, wingID),
        new CharacterFleet(fleetID, role + "1", squadID, wingID),
        new CharacterFleet(fleetID, role, squadID + 1, wingID),
        new CharacterFleet(fleetID, role, squadID, wingID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_FLEETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterFleet.get(account, time, fleetID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - fleets for a different account
    // - fleets not live at the given time
    CharacterFleet existing;
    Map<Long, CharacterFleet> listCheck = new HashMap<>();

    existing = new CharacterFleet(fleetID, role, squadID, wingID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fleetID, existing);

    existing = new CharacterFleet(fleetID + 10, role, squadID, wingID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fleetID + 10, existing);

    // Associated with different account
    existing = new CharacterFleet(fleetID, role, squadID, wingID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterFleet(fleetID + 3, role, squadID, wingID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterFleet(fleetID + 4, role, squadID, wingID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CharacterFleet> result = CachedData.retrieveAll(8888L,
                                                         (contid, at) -> CharacterFleet.accessQuery(testAccount, contid,
                                                                                                    1000, false,
                                                                                                    at,
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterFleet next : result) {
      long fleetID = next.getFleetID();
      Assert.assertTrue(listCheck.containsKey(fleetID));
      Assert.assertEquals(listCheck.get(fleetID), next);
    }

  }

}
