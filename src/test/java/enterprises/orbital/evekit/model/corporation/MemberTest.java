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

public class MemberTest extends AbstractModelTester<Member> {

  private final int characterID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Member> eol = () -> new Member(characterID);

  final ClassUnderTestConstructor<Member> live = () -> new Member(characterID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Member[]{
        new Member(characterID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Member.get(account, time, characterID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    Member existing;
    List<Member> listCheck = new ArrayList<>();

    existing = new Member(characterID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    existing = new Member(characterID + 1);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    // Associated with different account
    existing = new Member(characterID + 2);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Member(characterID + 3);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Member(characterID + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Member> result = CachedData.retrieveAll(8888L,
                                                 (contid, at) -> Member.accessQuery(testAccount,
                                                                                    contid, 1000,
                                                                                    false, at,
                                                                                    AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (int i = 0; i < listCheck.size(); i++) {
      Assert.assertEquals(listCheck.get(i), result.get(i));
    }

  }

}
