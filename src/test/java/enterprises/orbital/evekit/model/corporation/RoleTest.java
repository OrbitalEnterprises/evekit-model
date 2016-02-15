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

public class RoleTest extends AbstractModelTester<Role> {

  final long                            roleID          = TestBase.getRandomInt(100000000);
  final String                          roleDescription = "test role description";
  final String                          roleName        = "test role name";

  final ClassUnderTestConstructor<Role> eol             = new ClassUnderTestConstructor<Role>() {

                                                          @Override
                                                          public Role getCUT() {
                                                            return new Role(roleID, roleDescription, roleName);
                                                          }

                                                        };

  final ClassUnderTestConstructor<Role> live            = new ClassUnderTestConstructor<Role>() {
                                                          @Override
                                                          public Role getCUT() {
                                                            return new Role(roleID, roleDescription + " 1", roleName);
                                                          }

                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Role>() {

      @Override
      public Role[] getVariants() {
        return new Role[] {
            new Role(roleID + 1, roleDescription, roleName), new Role(roleID, roleDescription + " 1", roleName),
            new Role(roleID, roleDescription, roleName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_TITLES));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Role>() {

      @Override
      public Role getModel(SynchronizedEveAccount account, long time) {
        return Role.get(account, time, roleID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - roles for a different account
    // - roles not live at the given time
    Role existing;
    Map<Long, Role> listCheck = new HashMap<Long, Role>();

    existing = new Role(roleID, roleDescription, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(roleID, existing);

    existing = new Role(roleID + 1, roleDescription, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(roleID + 1, existing);

    // Associated with different account
    existing = new Role(roleID + 2, roleDescription, roleName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Role(roleID + 3, roleDescription, roleName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Role(roleID + 4, roleDescription, roleName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<Role> result = Role.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Role next : result) {
      long roleID = next.getRoleID();
      Assert.assertTrue(listCheck.containsKey(roleID));
      Assert.assertEquals(listCheck.get(roleID), next);
    }

  }

}
