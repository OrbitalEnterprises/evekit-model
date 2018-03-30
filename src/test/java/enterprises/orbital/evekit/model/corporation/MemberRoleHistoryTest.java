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

public class MemberRoleHistoryTest extends AbstractModelTester<MemberRoleHistory> {

  private final int characterID = TestBase.getRandomInt(100000000);
  private final long changedAt = TestBase.getRandomLong();
  private final int issuerID = TestBase.getRandomInt();
  private final String roleType = TestBase.getRandomText(50);
  private final String roleName = TestBase.getRandomText(50);
  private final boolean old = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<MemberRoleHistory> eol = () -> new MemberRoleHistory(characterID, changedAt, issuerID,
                                                                                       roleType, roleName, old);

  final ClassUnderTestConstructor<MemberRoleHistory> live = () -> new MemberRoleHistory(characterID, changedAt,
                                                                                        issuerID, roleType, roleName,
                                                                                        old);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MemberRoleHistory[]{
        new MemberRoleHistory(characterID + 1, changedAt, issuerID, roleType, roleName, old),
        new MemberRoleHistory(characterID, changedAt + 1, issuerID, roleType, roleName, old),
        new MemberRoleHistory(characterID, changedAt, issuerID + 1, roleType, roleName, old),
        new MemberRoleHistory(characterID, changedAt, issuerID, roleType + "1", roleName, old),
        new MemberRoleHistory(characterID, changedAt, issuerID, roleType, roleName + "1", old),
        new MemberRoleHistory(characterID, changedAt, issuerID, roleType, roleName, !old)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live,
                       (account, time) -> MemberRoleHistory.get(account, time, characterID, changedAt, issuerID,
                                                                roleType, roleName, old));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - history for a different account
    // - history not live at the given time
    MemberRoleHistory existing;
    List<MemberRoleHistory> listCheck = new ArrayList<>();

    existing = new MemberRoleHistory(characterID, changedAt, issuerID, roleType, roleName, old);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    existing = new MemberRoleHistory(characterID + 1, changedAt, issuerID, roleType, roleName, old);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    // Associated with different account
    existing = new MemberRoleHistory(characterID + 2, changedAt, issuerID, roleType, roleName, old);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MemberRoleHistory(characterID + 3, changedAt, issuerID, roleType, roleName, old);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MemberRoleHistory(characterID + 4, changedAt, issuerID, roleType, roleName, old);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<MemberRoleHistory> result = CachedData.retrieveAll(8888L,
                                                            (contid, at) -> MemberRoleHistory.accessQuery(
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
