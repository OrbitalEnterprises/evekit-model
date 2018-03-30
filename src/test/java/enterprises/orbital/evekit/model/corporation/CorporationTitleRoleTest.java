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

public class CorporationTitleRoleTest extends AbstractModelTester<CorporationTitleRole> {

  private final int titleID = TestBase.getRandomInt(100000000);
  private final String roleName = "test title name";
  private final boolean grantable = TestBase.getRandomBoolean();
  private final boolean atHQ = TestBase.getRandomBoolean();
  private final boolean atBase = TestBase.getRandomBoolean();
  private final boolean atOther = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<CorporationTitleRole> eol = () -> new CorporationTitleRole(titleID, roleName,
                                                                                             grantable, atHQ, atBase,
                                                                                             atOther);

  final ClassUnderTestConstructor<CorporationTitleRole> live = () -> new CorporationTitleRole(titleID, roleName,
                                                                                              grantable, atHQ, atBase,
                                                                                              atOther);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CorporationTitleRole[]{
        new CorporationTitleRole(titleID + 1, roleName, grantable, atHQ, atBase, atOther),
        new CorporationTitleRole(titleID, roleName + "1", grantable, atHQ, atBase, atOther),
        new CorporationTitleRole(titleID, roleName, !grantable, atHQ, atBase, atOther),
        new CorporationTitleRole(titleID, roleName, grantable, !atHQ, atBase, atOther),
        new CorporationTitleRole(titleID, roleName, grantable, atHQ, !atBase, atOther),
        new CorporationTitleRole(titleID, roleName, grantable, atHQ, atBase, !atOther),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_TITLES));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live,
                       (account, time) -> CorporationTitleRole.get(account, time, titleID, roleName, grantable, atHQ,
                                                                   atBase, atOther));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    CorporationTitleRole existing;
    Map<Integer, CorporationTitleRole> listCheck = new HashMap<>();

    existing = new CorporationTitleRole(titleID, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(titleID, existing);

    existing = new CorporationTitleRole(titleID + 1, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(titleID + 1, existing);

    // Associated with different account
    existing = new CorporationTitleRole(titleID + 2, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CorporationTitleRole(titleID + 3, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CorporationTitleRole(titleID + 4, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CorporationTitleRole> result = CachedData.retrieveAll(8888L,
                                                               (contid, at) -> CorporationTitleRole.accessQuery(
                                                                   testAccount,
                                                                   contid, 1000,
                                                                   false, at,
                                                                   AttributeSelector.any(),
                                                                   AttributeSelector.any(),
                                                                   AttributeSelector.any(),
                                                                   AttributeSelector.any(),
                                                                   AttributeSelector.any(),
                                                                   AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationTitleRole next : result) {
      int titleID = next.getTitleID();
      Assert.assertTrue(listCheck.containsKey(titleID));
      Assert.assertEquals(listCheck.get(titleID), next);
    }

  }

}
