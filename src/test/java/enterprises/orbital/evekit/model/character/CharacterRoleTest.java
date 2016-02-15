package enterprises.orbital.evekit.model.character;

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
import enterprises.orbital.evekit.model.character.CharacterRole;

public class CharacterRoleTest extends AbstractModelTester<CharacterRole> {

  final String                                   roleCategory = "test category";
  final long                                     roleID       = TestBase.getRandomInt(100000000);
  final String                                   roleName     = "tes role";

  final ClassUnderTestConstructor<CharacterRole> eol          = new ClassUnderTestConstructor<CharacterRole>() {

                                                                @Override
                                                                public CharacterRole getCUT() {
                                                                  return new CharacterRole(roleCategory, roleID, roleName);
                                                                }

                                                              };

  final ClassUnderTestConstructor<CharacterRole> live         = new ClassUnderTestConstructor<CharacterRole>() {
                                                                @Override
                                                                public CharacterRole getCUT() {
                                                                  return new CharacterRole(roleCategory, roleID, roleName + " 2");
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterRole>() {

      @Override
      public CharacterRole[] getVariants() {
        return new CharacterRole[] {
            new CharacterRole(roleCategory + " 2", roleID, roleName), new CharacterRole(roleCategory, roleID + 1, roleName),
            new CharacterRole(roleCategory, roleID, roleName + " 2")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterRole>() {

      @Override
      public CharacterRole getModel(SynchronizedEveAccount account, long time) {
        return CharacterRole.get(account, time, roleCategory, roleID);
      }

    });
  }

  @Test
  public void testGetAllRoles() throws Exception {
    // Should exclude:
    // - roles for a different account
    // - roles not live at the given time
    CharacterRole existing;
    Map<String, Map<Long, CharacterRole>> listCheck = new HashMap<String, Map<Long, CharacterRole>>();

    existing = new CharacterRole(roleCategory, roleID, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(roleCategory, new HashMap<Long, CharacterRole>());
    listCheck.get(roleCategory).put(roleID, existing);

    existing = new CharacterRole(roleCategory + " 10", roleID + 10, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(roleCategory + " 10", new HashMap<Long, CharacterRole>());
    listCheck.get(roleCategory + " 10").put(roleID + 10, existing);

    // Associated with different account
    existing = new CharacterRole(roleCategory, roleID, roleName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterRole(roleCategory + " 3", roleID + 3, roleName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterRole(roleCategory + " 4", roleID + 4, roleName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<CharacterRole> result = CharacterRole.getAllRoles(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterRole next : result) {
      String roleCategory = next.getRoleCategory();
      long roleID = next.getRoleID();
      Assert.assertTrue(listCheck.containsKey(roleCategory));
      Assert.assertTrue(listCheck.get(roleCategory).containsKey(roleID));
      Assert.assertEquals(listCheck.get(roleCategory).get(roleID), next);
    }
  }

}
