package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterRoleTest extends AbstractModelTester<CharacterRole> {

  private final String roleCategory = "test category";
  private final String roleName = "tes role";

  final ClassUnderTestConstructor<CharacterRole> eol = () -> new CharacterRole(roleCategory, roleName);

  final ClassUnderTestConstructor<CharacterRole> live = () -> new CharacterRole(roleCategory, roleName);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterRole[]{
        new CharacterRole(roleCategory + " 2", roleName),
        new CharacterRole(roleCategory, roleName + " 2")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterRole.get(account, time, roleCategory, roleName));
  }

  @Test
  public void testGetAllRoles() throws Exception {
    // Should exclude:
    // - roles for a different account
    // - roles not live at the given time
    CharacterRole existing;
    Map<Pair<String, String>, CharacterRole> listCheck = new HashMap<>();

    existing = new CharacterRole(roleCategory, roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(roleCategory, roleName), existing);

    existing = new CharacterRole(roleCategory + " 10", roleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Pair.of(roleCategory + " 10", roleName), existing);

    // Associated with different account
    existing = new CharacterRole(roleCategory, roleName);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterRole(roleCategory + " 3", roleName);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterRole(roleCategory + " 4", roleName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CharacterRole> result = CachedData.retrieveAll(8888L,
                                                        (contid, at) -> CharacterRole.accessQuery(testAccount, contid,
                                                                                                  1000, false, at,
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterRole next : result) {
      String roleCategory = next.getRoleCategory();
      String roleName = next.getRoleName();
      Assert.assertTrue(listCheck.containsKey(Pair.of(roleCategory, roleName)));
      Assert.assertEquals(listCheck.get(Pair.of(roleCategory, roleName)), next);
    }
  }

}
