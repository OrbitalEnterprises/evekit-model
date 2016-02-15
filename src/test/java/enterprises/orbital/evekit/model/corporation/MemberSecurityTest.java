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

public class MemberSecurityTest extends AbstractModelTester<MemberSecurity> {

  final long      characterID           = TestBase.getRandomInt(100000000);
  final String    name                  = "test name";
  final Set<Long> grantableRoles        = new HashSet<Long>();
  final Set<Long> grantableRolesAtBase  = new HashSet<Long>();
  final Set<Long> grantableRolesAtHQ    = new HashSet<Long>();
  final Set<Long> grantableRolesAtOther = new HashSet<Long>();
  final Set<Long> roles                 = new HashSet<Long>();
  final Set<Long> rolesAtBase           = new HashSet<Long>();
  final Set<Long> rolesAtHQ             = new HashSet<Long>();
  final Set<Long> rolesAtOther          = new HashSet<Long>();
  final Set<Long> titles                = new HashSet<Long>();

  public MemberSecurityTest() {
    Object[] todo = new Object[] {
        grantableRoles, grantableRolesAtBase, grantableRolesAtHQ, grantableRolesAtOther, roles, rolesAtBase, rolesAtHQ, rolesAtOther, titles
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

  public void populate(MemberSecurity pop) {
    pop.getGrantableRoles().addAll(grantableRoles);
    pop.getGrantableRolesAtBase().addAll(grantableRolesAtBase);
    pop.getGrantableRolesAtHQ().addAll(grantableRolesAtHQ);
    pop.getGrantableRolesAtOther().addAll(grantableRolesAtOther);
    pop.getRoles().addAll(roles);
    pop.getRolesAtBase().addAll(rolesAtBase);
    pop.getRolesAtHQ().addAll(rolesAtHQ);
    pop.getRolesAtOther().addAll(rolesAtOther);
    pop.getTitles().addAll(titles);
  }

  final ClassUnderTestConstructor<MemberSecurity> eol  = new ClassUnderTestConstructor<MemberSecurity>() {

                                                         @Override
                                                         public MemberSecurity getCUT() {
                                                           MemberSecurity result = new MemberSecurity(characterID, name);
                                                           populate(result);
                                                           return result;
                                                         }

                                                       };

  final ClassUnderTestConstructor<MemberSecurity> live = new ClassUnderTestConstructor<MemberSecurity>() {
                                                         @Override
                                                         public MemberSecurity getCUT() {
                                                           MemberSecurity result = new MemberSecurity(characterID, name + " 1");
                                                           populate(result);
                                                           return result;
                                                         }

                                                       };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MemberSecurity>() {

      @Override
      public MemberSecurity[] getVariants() {
        MemberSecurity[] result = new MemberSecurity[] {
            new MemberSecurity(characterID + 1, name), new MemberSecurity(characterID, name + " 1"), new MemberSecurity(characterID, name),
            new MemberSecurity(characterID, name), new MemberSecurity(characterID, name), new MemberSecurity(characterID, name),
            new MemberSecurity(characterID, name), new MemberSecurity(characterID, name), new MemberSecurity(characterID, name),
            new MemberSecurity(characterID, name), new MemberSecurity(characterID, name)
        };
        for (int i = 0; i < result.length; i++) {
          populate(result[i]);
          switch (i) {
          case 2:
            result[i].getGrantableRoles().add(TestBase.getUniqueRandomLong());
            break;
          case 3:
            result[i].getGrantableRolesAtBase().add(TestBase.getUniqueRandomLong());
            break;
          case 4:
            result[i].getGrantableRolesAtHQ().add(TestBase.getUniqueRandomLong());
            break;
          case 5:
            result[i].getGrantableRolesAtOther().add(TestBase.getUniqueRandomLong());
            break;
          case 6:
            result[i].getRoles().add(TestBase.getUniqueRandomLong());
            break;
          case 7:
            result[i].getRolesAtBase().add(TestBase.getUniqueRandomLong());
            break;
          case 8:
            result[i].getRolesAtHQ().add(TestBase.getUniqueRandomLong());
            break;
          case 9:
            result[i].getRolesAtOther().add(TestBase.getUniqueRandomLong());
            break;
          case 10:
            result[i].getTitles().add(TestBase.getUniqueRandomLong());
            break;
          default:
            // NOP
          }
        }
        return result;
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MemberSecurity>() {

      @Override
      public MemberSecurity getModel(SynchronizedEveAccount account, long time) {
        return MemberSecurity.get(account, time, characterID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - member security for a different account
    // - member security not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    MemberSecurity existing;
    Map<Long, MemberSecurity> listCheck = new HashMap<Long, MemberSecurity>();

    existing = new MemberSecurity(characterID, name);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID, existing);

    existing = new MemberSecurity(characterID + 10, name);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID + 10, existing);

    existing = new MemberSecurity(characterID + 20, name);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID + 20, existing);

    existing = new MemberSecurity(characterID + 30, name);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(characterID + 30, existing);

    // Associated with different account
    existing = new MemberSecurity(characterID, name);
    populate(existing);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new MemberSecurity(characterID + 5, name);
    populate(existing);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new MemberSecurity(characterID + 3, name);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all member security are returned
    List<MemberSecurity> result = MemberSecurity.getAll(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MemberSecurity next : result) {
      long characterID = next.getCharacterID();
      Assert.assertTrue(listCheck.containsKey(characterID));
      Assert.assertEquals(listCheck.get(characterID), next);
    }

    // Verify limited set returned
    result = MemberSecurity.getAll(testAccount, 8888L, 2, characterID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(characterID), result.get(0));
    Assert.assertEquals(listCheck.get(characterID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = MemberSecurity.getAll(testAccount, 8888L, 100, characterID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(characterID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(characterID + 30), result.get(1));

  }

}
