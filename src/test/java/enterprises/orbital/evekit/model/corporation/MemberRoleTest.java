package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MemberRoleTest extends AbstractModelTester<MemberRole> {

  private final int characterID = TestBase.getRandomInt(100000000);
  private final String roleName = "test title name";
  private final boolean grantable = TestBase.getRandomBoolean();
  private final boolean atHQ = TestBase.getRandomBoolean();
  private final boolean atBase = TestBase.getRandomBoolean();
  private final boolean atOther = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<MemberRole> eol = () -> new MemberRole(characterID, roleName,
                                                                         grantable, atHQ, atBase,
                                                                         atOther);

  final ClassUnderTestConstructor<MemberRole> live = () -> new MemberRole(characterID, roleName,
                                                                          grantable, atHQ, atBase,
                                                                          atOther);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MemberRole[]{
        new MemberRole(characterID + 1, roleName, grantable, atHQ, atBase, atOther),
        new MemberRole(characterID, roleName + "1", grantable, atHQ, atBase, atOther),
        new MemberRole(characterID, roleName, !grantable, atHQ, atBase, atOther),
        new MemberRole(characterID, roleName, grantable, !atHQ, atBase, atOther),
        new MemberRole(characterID, roleName, grantable, atHQ, !atBase, atOther),
        new MemberRole(characterID, roleName, grantable, atHQ, atBase, !atOther),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live,
                       (account, time) -> MemberRole.get(account, time, characterID, roleName, grantable, atHQ,
                                                         atBase, atOther));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    MemberRole existing;
    List<MemberRole> listCheck = new ArrayList<>();

    existing = new MemberRole(characterID, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    existing = new MemberRole(characterID + 1, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    // Associated with different account
    existing = new MemberRole(characterID + 2, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MemberRole(characterID + 3, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MemberRole(characterID + 4, roleName, grantable, atHQ, atBase, atOther);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<MemberRole> result = CachedData.retrieveAll(8888L,
                                                     (contid, at) -> MemberRole.accessQuery(
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
    for (int i = 0; i < listCheck.size(); i++) {
      Assert.assertEquals(listCheck.get(i), result.get(i));
    }

  }

}
