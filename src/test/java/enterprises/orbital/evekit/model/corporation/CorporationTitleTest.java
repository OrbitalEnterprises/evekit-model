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

public class CorporationTitleTest extends AbstractModelTester<CorporationTitle> {

  final long      titleID               = TestBase.getRandomInt(100000000);
  final String    titleName             = "test title name";
  final Set<Long> grantableRoles        = new HashSet<Long>();
  final Set<Long> grantableRolesAtBase  = new HashSet<Long>();
  final Set<Long> grantableRolesAtHQ    = new HashSet<Long>();
  final Set<Long> grantableRolesAtOther = new HashSet<Long>();
  final Set<Long> roles                 = new HashSet<Long>();
  final Set<Long> rolesAtBase           = new HashSet<Long>();
  final Set<Long> rolesAtHQ             = new HashSet<Long>();
  final Set<Long> rolesAtOther          = new HashSet<Long>();

  public CorporationTitleTest() {
    Object[] todo = new Object[] {
        grantableRoles, grantableRolesAtBase, grantableRolesAtHQ, grantableRolesAtOther, roles, rolesAtBase, rolesAtHQ, rolesAtOther
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

  public void populate(CorporationTitle title) {
    title.getGrantableRoles().addAll(grantableRoles);
    title.getGrantableRolesAtBase().addAll(grantableRolesAtBase);
    title.getGrantableRolesAtHQ().addAll(grantableRolesAtHQ);
    title.getGrantableRolesAtOther().addAll(grantableRolesAtOther);
    title.getRoles().addAll(roles);
    title.getRolesAtBase().addAll(rolesAtBase);
    title.getRolesAtHQ().addAll(rolesAtHQ);
    title.getRolesAtOther().addAll(rolesAtOther);
  }

  final ClassUnderTestConstructor<CorporationTitle> eol  = new ClassUnderTestConstructor<CorporationTitle>() {

                                                           @Override
                                                           public CorporationTitle getCUT() {
                                                             CorporationTitle result = new CorporationTitle(titleID, titleName);
                                                             populate(result);
                                                             return result;
                                                           }

                                                         };

  final ClassUnderTestConstructor<CorporationTitle> live = new ClassUnderTestConstructor<CorporationTitle>() {
                                                           @Override
                                                           public CorporationTitle getCUT() {
                                                             CorporationTitle result = new CorporationTitle(titleID, titleName + " 1");
                                                             populate(result);
                                                             return result;
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationTitle>() {

      @Override
      public CorporationTitle[] getVariants() {
        CorporationTitle[] result = new CorporationTitle[] {
            new CorporationTitle(titleID + 1, titleName), new CorporationTitle(titleID, titleName + " 1"), new CorporationTitle(titleID, titleName),
            new CorporationTitle(titleID, titleName), new CorporationTitle(titleID, titleName), new CorporationTitle(titleID, titleName),
            new CorporationTitle(titleID, titleName), new CorporationTitle(titleID, titleName), new CorporationTitle(titleID, titleName),
            new CorporationTitle(titleID, titleName)
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
          default:
            // NOP
          }
        }
        return result;
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_TITLES));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CorporationTitle>() {

      @Override
      public CorporationTitle getModel(SynchronizedEveAccount account, long time) {
        return CorporationTitle.get(account, time, titleID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    CorporationTitle existing;
    Map<Long, CorporationTitle> listCheck = new HashMap<Long, CorporationTitle>();

    existing = new CorporationTitle(titleID, titleName);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(titleID, existing);

    existing = new CorporationTitle(titleID + 1, titleName);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(titleID + 1, existing);

    // Associated with different account
    existing = new CorporationTitle(titleID + 2, titleName);
    populate(existing);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new CorporationTitle(titleID + 3, titleName);
    populate(existing);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new CorporationTitle(titleID + 4, titleName);
    populate(existing);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<CorporationTitle> result = CorporationTitle.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationTitle next : result) {
      long titleID = next.getTitleID();
      Assert.assertTrue(listCheck.containsKey(titleID));
      Assert.assertEquals(listCheck.get(titleID), next);
    }

  }

}
