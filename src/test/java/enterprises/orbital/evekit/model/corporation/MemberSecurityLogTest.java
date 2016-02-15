package enterprises.orbital.evekit.model.corporation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;

public class MemberSecurityLogTest extends AbstractModelTester<MemberSecurityLog> {

  final long      changeTime           = TestBase.getRandomInt(100000000);
  final long      changedCharacterID   = TestBase.getRandomInt(100000000);
  final String    changedCharacterName = "test changed character name";
  final long      issuerID             = TestBase.getRandomInt(100000000);
  final String    issuerName           = "test issuer name";
  final String    roleLocationType     = "test role location type";
  final Set<Long> oldRoles             = new HashSet<Long>();
  final Set<Long> newRoles             = new HashSet<Long>();

  public MemberSecurityLogTest() {
    Object[] todo = new Object[] {
        oldRoles, newRoles
    };
    for (Object next : todo) {
      @SuppressWarnings("unchecked")
      Set<Long> pop = (Set<Long>) next;
      int count = TestBase.getRandomInt(5 + 5);
      for (int i = 0; i < count; i++) {
        pop.add(TestBase.getUniqueRandomLong());
      }
    }
  }

  public void populate(MemberSecurityLog pop) {
    pop.getOldRoles().addAll(oldRoles);
    pop.getNewRoles().addAll(newRoles);
  }

  final ClassUnderTestConstructor<MemberSecurityLog> eol  = new ClassUnderTestConstructor<MemberSecurityLog>() {

                                                            @Override
                                                            public MemberSecurityLog getCUT() {
                                                              MemberSecurityLog result = new MemberSecurityLog(
                                                                  changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
                                                              populate(result);
                                                              return result;
                                                            }

                                                          };

  final ClassUnderTestConstructor<MemberSecurityLog> live = new ClassUnderTestConstructor<MemberSecurityLog>() {
                                                            @Override
                                                            public MemberSecurityLog getCUT() {
                                                              MemberSecurityLog result = new MemberSecurityLog(
                                                                  changeTime, changedCharacterID + 1, changedCharacterName, issuerID, issuerName,
                                                                  roleLocationType);
                                                              populate(result);
                                                              return result;
                                                            }

                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MemberSecurityLog>() {

      @Override
      public MemberSecurityLog[] getVariants() {
        MemberSecurityLog[] result = new MemberSecurityLog[] {
            new MemberSecurityLog(changeTime + 1, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType),
            new MemberSecurityLog(changeTime, changedCharacterID + 1, changedCharacterName, issuerID, issuerName, roleLocationType),
            new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName + " 1", issuerID, issuerName, roleLocationType),
            new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID + 1, issuerName, roleLocationType),
            new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName + " 1", roleLocationType),
            new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType + " 1"),
            new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType),
            new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType)
        };
        for (int i = 0; i < result.length; i++) {
          populate(result[i]);
        }
        result[result.length - 2].getOldRoles().add(TestBase.getUniqueRandomLong());
        result[result.length - 1].getNewRoles().add(TestBase.getUniqueRandomLong());
        return result;
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MemberSecurityLog>() {

      @Override
      public MemberSecurityLog getModel(SynchronizedEveAccount account, long time) {
        return MemberSecurityLog.get(account, time, changeTime);
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
    MemberSecurityLog existing;
    Map<Long, MemberSecurityLog> listCheck = new HashMap<Long, MemberSecurityLog>();

    existing = new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime, existing);

    existing = new MemberSecurityLog(changeTime + 10, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime + 10, existing);

    existing = new MemberSecurityLog(changeTime + 20, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime + 20, existing);

    existing = new MemberSecurityLog(changeTime + 30, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime + 30, existing);

    // Associated with different account
    existing = new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new MemberSecurityLog(changeTime + 5, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new MemberSecurityLog(changeTime + 3, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all logs are returned
    List<MemberSecurityLog> result = MemberSecurityLog.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MemberSecurityLog next : result) {
      long changeTime = next.getChangeTime();
      Assert.assertTrue(listCheck.containsKey(changeTime));
      Assert.assertEquals(listCheck.get(changeTime), next);
    }

    // Verify limited set returned
    result = MemberSecurityLog.getAllForward(testAccount, 8888L, 2, changeTime - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(changeTime), result.get(0));
    Assert.assertEquals(listCheck.get(changeTime + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = MemberSecurityLog.getAllForward(testAccount, 8888L, 100, changeTime + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(changeTime + 20), result.get(0));
    Assert.assertEquals(listCheck.get(changeTime + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - logs for a different account
    // - logs not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    MemberSecurityLog existing;
    Map<Long, MemberSecurityLog> listCheck = new HashMap<Long, MemberSecurityLog>();

    existing = new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime, existing);

    existing = new MemberSecurityLog(changeTime + 10, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime + 10, existing);

    existing = new MemberSecurityLog(changeTime + 20, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime + 20, existing);

    existing = new MemberSecurityLog(changeTime + 30, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(changeTime + 30, existing);

    // Associated with different account
    existing = new MemberSecurityLog(changeTime, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new MemberSecurityLog(changeTime + 5, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new MemberSecurityLog(changeTime + 3, changedCharacterID, changedCharacterName, issuerID, issuerName, roleLocationType);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all logs are returned
    List<MemberSecurityLog> result = MemberSecurityLog.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MemberSecurityLog next : result) {
      long changeTime = next.getChangeTime();
      Assert.assertTrue(listCheck.containsKey(changeTime));
      Assert.assertEquals(listCheck.get(changeTime), next);
    }

    // Verify limited set returned
    result = MemberSecurityLog.getAllBackward(testAccount, 8888L, 2, changeTime + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(changeTime + 30), result.get(0));
    Assert.assertEquals(listCheck.get(changeTime + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = MemberSecurityLog.getAllBackward(testAccount, 8888L, 100, changeTime + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(changeTime + 10), result.get(0));
    Assert.assertEquals(listCheck.get(changeTime), result.get(1));

  }

}
