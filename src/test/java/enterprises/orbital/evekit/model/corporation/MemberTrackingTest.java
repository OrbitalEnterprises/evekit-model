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

public class MemberTrackingTest extends AbstractModelTester<MemberTracking> {

  final long                                      characterID    = TestBase.getRandomInt(100000000);
  final String                                    base           = "test base";
  final long                                      baseID         = TestBase.getRandomInt(100000000);
  final long                                      grantableRoles = TestBase.getRandomInt(100000000);                                                         // bit
                                                                                                                                                             // mask?
  final String                                    location       = "test location";
  final long                                      locationID     = TestBase.getRandomInt(100000000);
  final long                                      logoffDateTime = TestBase.getRandomInt(100000000);
  final long                                      logonDateTime  = TestBase.getRandomInt(100000000);
  final String                                    name           = "test name";
  final long                                      roles          = TestBase.getRandomInt(100000000);                                                         // bit
                                                                                                                                                             // mask?
  final String                                    shipType       = "test ship type";
  final int                                       shipTypeID     = TestBase.getRandomInt(100000000);
  final long                                      startDateTime  = TestBase.getRandomInt(100000000);
  final String                                    title          = "test string";

  final ClassUnderTestConstructor<MemberTracking> eol            = new ClassUnderTestConstructor<MemberTracking>() {

                                                                   @Override
                                                                   public MemberTracking getCUT() {
                                                                     return new MemberTracking(
                                                                         characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime,
                                                                         logonDateTime, name, roles, shipType, shipTypeID, startDateTime, title);
                                                                   }

                                                                 };

  final ClassUnderTestConstructor<MemberTracking> live           = new ClassUnderTestConstructor<MemberTracking>() {
                                                                   @Override
                                                                   public MemberTracking getCUT() {
                                                                     return new MemberTracking(
                                                                         characterID, base, baseID + 1, grantableRoles, location, locationID, logoffDateTime,
                                                                         logonDateTime, name, roles, shipType, shipTypeID, startDateTime, title);
                                                                   }

                                                                 };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MemberTracking>() {

      @Override
      public MemberTracking[] getVariants() {
        return new MemberTracking[] {
            new MemberTracking(
                characterID + 1, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base + " 1", baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID + 1, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles + 1, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location + " 1", locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID + 1, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime + 1, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime + 1, name, roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name + " 1", roles, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles + 1, shipType, shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType + " 1", shipTypeID,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID + 1,
                startDateTime, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime + 1, title),
            new MemberTracking(
                characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID,
                startDateTime, title + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_TRACKING));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MemberTracking>() {

      @Override
      public MemberTracking getModel(
                                     SynchronizedEveAccount account,
                                     long time) {
        return MemberTracking.get(account, time, characterID);
      }

    });
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
    Map<Long, MemberTracking> listCheck = new HashMap<Long, MemberTracking>();

    existing = new MemberTracking(
        characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID, existing);

    existing = new MemberTracking(
        characterID + 10, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID + 10, existing);

    existing = new MemberTracking(
        characterID + 20, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID + 20, existing);

    existing = new MemberTracking(
        characterID + 30, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID + 30, existing);

    // Associated with different account
    existing = new MemberTracking(
        characterID, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new MemberTracking(
        characterID + 5, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new MemberTracking(
        characterID + 3, base, baseID, grantableRoles, location, locationID, logoffDateTime, logonDateTime, name, roles, shipType, shipTypeID, startDateTime,
        title);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all member security are returned
    List<MemberTracking> result = MemberTracking.getAll(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MemberTracking next : result) {
      long characterID = next.getCharacterID();
      Assert.assertTrue(listCheck.containsKey(characterID));
      Assert.assertEquals(listCheck.get(characterID), next);
    }

    // Verify limited set returned
    result = MemberTracking.getAll(testAccount, 8888L, 2, characterID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(characterID), result.get(0));
    Assert.assertEquals(listCheck.get(characterID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = MemberTracking.getAll(testAccount, 8888L, 100, characterID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(characterID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(characterID + 30), result.get(1));

  }

}
