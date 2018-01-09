package enterprises.orbital.evekit.model.corporation;

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

public class DivisionTest extends AbstractModelTester<Division> {

  final boolean                             wallet      = true;
  final int                                 accountKey  = TestBase.getRandomInt(100000000);
  final String                              description = "test description";

  final ClassUnderTestConstructor<Division> eol         = new ClassUnderTestConstructor<Division>() {

                                                          @Override
                                                          public Division getCUT() {
                                                            return new Division(wallet, accountKey, description);
                                                          }

                                                        };

  final ClassUnderTestConstructor<Division> live        = new ClassUnderTestConstructor<Division>() {
                                                          @Override
                                                          public Division getCUT() {
                                                            return new Division(wallet, accountKey, description + " 1");
                                                          }

                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Division>() {

      @Override
      public Division[] getVariants() {
        return new Division[] {
            new Division(!wallet, accountKey, description), new Division(wallet, accountKey + 1, description),
            new Division(wallet, accountKey, description + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Division>() {

      @Override
      public Division getModel(SynchronizedEveAccount account, long time) {
        return Division.get(account, time, wallet, accountKey);
      }

    });
  }

  @Test
  public void testGetAllByType() throws Exception {
    // Should exclude:
    // - divisions for a different account
    // - divisions not live at the given time
    // - divisions for a different type
    Division existing;
    Map<Integer, Division> listCheck = new HashMap<Integer, Division>();

    existing = new Division(wallet, accountKey, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(accountKey, existing);

    existing = new Division(wallet, accountKey + 1, description);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(accountKey + 1, existing);

    // Associated with different account
    existing = new Division(wallet, accountKey + 2, description);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different type
    existing = new Division(!wallet, accountKey + 5, description);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Division(wallet, accountKey + 3, description);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Division(wallet, accountKey + 4, description);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Division> result = Division.getAllByType(testAccount, 8888L, wallet);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Division next : result) {
      int accountKey = next.getAccountKey();
      Assert.assertTrue(listCheck.containsKey(accountKey));
      Assert.assertEquals(listCheck.get(accountKey), next);
    }

  }

}
