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

public class ShareholderTest extends AbstractModelTester<Shareholder> {

  private final int shareholderID = TestBase.getRandomInt(100000000);
  private final String shareholderType = "test shareholder corporation name";
  private final long shares = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Shareholder> eol = () -> new Shareholder(
      shareholderID, shareholderType, shares);

  final ClassUnderTestConstructor<Shareholder> live = () -> new Shareholder(
      shareholderID, shareholderType + "1", shares);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Shareholder[]{
        new Shareholder(shareholderID + 1, shareholderType, shares),
        new Shareholder(shareholderID, shareholderType + " 1", shares),
        new Shareholder(shareholderID, shareholderType, shares + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_SHAREHOLDERS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Shareholder.get(account, time, shareholderID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - shareholders for a different account
    // - shareholders not live at the given time
    Shareholder existing;
    Map<Integer, Shareholder> listCheck = new HashMap<>();

    existing = new Shareholder(shareholderID, shareholderType, shares);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(shareholderID, existing);

    existing = new Shareholder(shareholderID + 1, shareholderType, shares);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(shareholderID + 1, existing);

    // Associated with different account
    existing = new Shareholder(shareholderID + 2, shareholderType, shares);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Shareholder(shareholderID + 3, shareholderType, shares);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Shareholder(shareholderID + 4, shareholderType, shares);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Shareholder> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> Shareholder.accessQuery(testAccount, contid, 1000,
                                                                                              false, at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Shareholder next : result) {
      int shareholderID = next.getShareholderID();
      Assert.assertTrue(listCheck.containsKey(shareholderID));
      Assert.assertEquals(listCheck.get(shareholderID), next);
    }

  }

}
