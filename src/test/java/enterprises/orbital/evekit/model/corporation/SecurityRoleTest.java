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

public class SecurityRoleTest extends AbstractModelTester<SecurityRole> {

  final long                                    roleID   = TestBase.getRandomInt(100000000);
  final String                                  roleName = "test role name";

  final ClassUnderTestConstructor<SecurityRole> eol      = new ClassUnderTestConstructor<SecurityRole>() {

                                                           @Override
                                                           public SecurityRole getCUT() {
                                                             return new SecurityRole(roleID, roleName);
                                                           }

                                                         };

  final ClassUnderTestConstructor<SecurityRole> live     = new ClassUnderTestConstructor<SecurityRole>() {
                                                           @Override
                                                           public SecurityRole getCUT() {
                                                             return new SecurityRole(roleID, roleName + " 1");
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SecurityRole>() {

      @Override
      public SecurityRole[] getVariants() {
        return new SecurityRole[] {
            new SecurityRole(roleID + 1, roleName), new SecurityRole(roleID, roleName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<SecurityRole>() {

      @Override
      public SecurityRole getModel(SynchronizedEveAccount account, long time) {
        return SecurityRole.get(account, time, roleID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - roles for a different account
    // - roles not live at the given time
    SecurityRole existing;
    Map<Long, SecurityRole> listCheck = new HashMap<Long, SecurityRole>();

    existing = new SecurityRole(roleID, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(roleID, existing);

    existing = new SecurityRole(roleID + 1, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(roleID + 1, existing);

    // Associated with different account
    existing = new SecurityRole(roleID + 2, roleName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new SecurityRole(roleID + 3, roleName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new SecurityRole(roleID + 4, roleName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<SecurityRole> result = SecurityRole.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (SecurityRole next : result) {
      long roleID = next.getRoleID();
      Assert.assertTrue(listCheck.containsKey(roleID));
      Assert.assertEquals(listCheck.get(roleID), next);
    }

  }

}
