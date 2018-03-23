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

public class MemberTrackingTest extends AbstractModelTester<MemberTracking> {

  private final int characterID = TestBase.getRandomInt(100000000);
  private final int baseID = TestBase.getRandomInt(100000000);
  private final long locationID = TestBase.getRandomInt(100000000);
  private final long logoffDateTime = TestBase.getRandomInt(100000000);
  private final long logonDateTime = TestBase.getRandomInt(100000000);
  private final int shipTypeID = TestBase.getRandomInt(100000000);
  private final long startDateTime = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<MemberTracking> eol = () -> new MemberTracking(
      characterID, baseID, locationID, logoffDateTime,
      logonDateTime, shipTypeID, startDateTime);

  final ClassUnderTestConstructor<MemberTracking> live = () -> new MemberTracking(
      characterID, baseID + 1, locationID, logoffDateTime,
      logonDateTime, shipTypeID, startDateTime);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MemberTracking[]{
        new MemberTracking(
            characterID + 1, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID,
            startDateTime),
        new MemberTracking(
            characterID, baseID + 1, locationID, logoffDateTime, logonDateTime, shipTypeID,
            startDateTime),
        new MemberTracking(
            characterID, baseID, locationID + 1, logoffDateTime, logonDateTime, shipTypeID,
            startDateTime),
        new MemberTracking(
            characterID, baseID, locationID, logoffDateTime + 1, logonDateTime, shipTypeID,
            startDateTime),
        new MemberTracking(
            characterID, baseID, locationID, logoffDateTime, logonDateTime + 1, shipTypeID,
            startDateTime),
        new MemberTracking(
            characterID, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID + 1,
            startDateTime),
        new MemberTracking(
            characterID, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID,
            startDateTime + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_TRACKING));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MemberTracking.get(account, time, characterID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - members for a different account
    // - members not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    MemberTracking existing;
    Map<Integer, MemberTracking> listCheck = new HashMap<>();

    existing = new MemberTracking(
        characterID, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(characterID, existing);

    existing = new MemberTracking(
        characterID + 10, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(characterID + 10, existing);

    existing = new MemberTracking(
        characterID + 20, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(characterID + 20, existing);

    existing = new MemberTracking(
        characterID + 30, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(characterID + 30, existing);

    // Associated with different account
    existing = new MemberTracking(
        characterID, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MemberTracking(
        characterID + 5, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MemberTracking(
        characterID + 3, baseID, locationID, logoffDateTime, logonDateTime, shipTypeID, startDateTime);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all member security are returned
    List<MemberTracking> result = MemberTracking.accessQuery(testAccount, 0, 10, false, AttributeSelector.values(8888L),
                                                             AttributeSelector.any(), AttributeSelector.any(),
                                                             AttributeSelector.any(),
                                                             AttributeSelector.any(), AttributeSelector.any(),
                                                             AttributeSelector.any(),
                                                             AttributeSelector.any());
    Assert.assertEquals(listCheck.size(), result.size());
    for (MemberTracking next : result) {
      int characterID = next.getCharacterID();
      Assert.assertTrue(listCheck.containsKey(characterID));
      Assert.assertEquals(listCheck.get(characterID), next);
    }

    // Verify limited set returned
    result = MemberTracking.accessQuery(testAccount, 0, 2, false, AttributeSelector.values(8888L),
                                        AttributeSelector.range(characterID, Integer.MAX_VALUE),
                                        AttributeSelector.any(), AttributeSelector.any(),
                                        AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                                        AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(characterID), result.get(0));
    Assert.assertEquals(listCheck.get(characterID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = MemberTracking.accessQuery(testAccount, 0, 100, false, AttributeSelector.values(8888L),
                                        AttributeSelector.range(characterID + 11, Integer.MAX_VALUE),
                                        AttributeSelector.any(), AttributeSelector.any(),
                                        AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                                        AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(characterID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(characterID + 30), result.get(1));

  }

}
