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

public class CorporationTitleTest extends AbstractModelTester<CorporationTitle> {

  private final int titleID = TestBase.getRandomInt(100000000);
  private final String titleName = "test title name";

  final ClassUnderTestConstructor<CorporationTitle> eol = () -> new CorporationTitle(titleID, titleName);

  final ClassUnderTestConstructor<CorporationTitle> live = () -> new CorporationTitle(titleID, titleName + " 1");

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CorporationTitle[]{
        new CorporationTitle(titleID + 1, titleName), new CorporationTitle(titleID, titleName + " 1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_TITLES));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CorporationTitle.get(account, time, titleID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    CorporationTitle existing;
    Map<Integer, CorporationTitle> listCheck = new HashMap<>();

    existing = new CorporationTitle(titleID, titleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(titleID, existing);

    existing = new CorporationTitle(titleID + 1, titleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(titleID + 1, existing);

    // Associated with different account
    existing = new CorporationTitle(titleID + 2, titleName);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CorporationTitle(titleID + 3, titleName);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CorporationTitle(titleID + 4, titleName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CorporationTitle> result = CachedData.retrieveAll(8888L,
                                                           (contid, at) -> CorporationTitle.accessQuery(testAccount,
                                                                                                        contid, 1000,
                                                                                                        false, at,
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationTitle next : result) {
      int titleID = next.getTitleID();
      Assert.assertTrue(listCheck.containsKey(titleID));
      Assert.assertEquals(listCheck.get(titleID), next);
    }

  }

}
