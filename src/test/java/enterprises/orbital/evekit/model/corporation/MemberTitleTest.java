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

public class MemberTitleTest extends AbstractModelTester<MemberTitle> {

  private final int characterID = TestBase.getRandomInt(100000000);
  private final int titleID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<MemberTitle> eol = () -> new MemberTitle(characterID, titleID);

  final ClassUnderTestConstructor<MemberTitle> live = () -> new MemberTitle(characterID, titleID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MemberTitle[]{
        new MemberTitle(characterID + 1, titleID), new MemberTitle(characterID, titleID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MemberTitle.get(account, time, characterID, titleID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    MemberTitle existing;
    List<MemberTitle> listCheck = new ArrayList<>();

    existing = new MemberTitle(characterID, titleID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    existing = new MemberTitle(characterID + 1, titleID + 1);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    // Associated with different account
    existing = new MemberTitle(characterID + 2, titleID + 2);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MemberTitle(characterID + 3, titleID + 3);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MemberTitle(characterID + 4, titleID + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<MemberTitle> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> MemberTitle.accessQuery(testAccount,
                                                                                              contid, 1000,
                                                                                              false, at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (int i = 0; i < listCheck.size(); i++) {
      Assert.assertEquals(listCheck.get(i), result.get(i));
    }

  }

}
