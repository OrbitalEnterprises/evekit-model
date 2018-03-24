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

public class DivisionTest extends AbstractModelTester<Division> {

  private final boolean wallet = true;
  private final int division = TestBase.getRandomInt(100000000);
  private final String name = "test name";

  final ClassUnderTestConstructor<Division> eol = () -> new Division(wallet, division, name);

  final ClassUnderTestConstructor<Division> live = () -> new Division(wallet, division, name + " 1");

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Division[]{
        new Division(!wallet, division, name), new Division(wallet, division + 1, name),
        new Division(wallet, division, name + " 1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Division.get(account, time, wallet, division));
  }

  @Test
  public void testGetAllByType() throws Exception {
    // Should exclude:
    // - divisions for a different account
    // - divisions not live at the given time
    // - divisions for a different type
    Division existing;
    Map<Integer, Division> listCheck = new HashMap<>();

    existing = new Division(wallet, division, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(division, existing);

    existing = new Division(wallet, division + 1, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(division + 1, existing);

    // Associated with different account
    existing = new Division(wallet, division + 2, name);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different type
    existing = new Division(!wallet, division + 5, name);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Division(wallet, division + 3, name);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Division(wallet, division + 4, name);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Division> result = CachedData.retrieveAll(8888L,
                                                   (contid, at) -> Division.accessQuery(testAccount, contid, 1000,
                                                                                        false, at,
                                                                                        AttributeSelector.values(wallet),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Division next : result) {
      int division = next.getDivision();
      Assert.assertTrue(listCheck.containsKey(division));
      Assert.assertEquals(listCheck.get(division), next);
    }

  }

}
