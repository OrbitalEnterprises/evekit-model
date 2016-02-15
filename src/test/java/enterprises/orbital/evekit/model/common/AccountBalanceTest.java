package enterprises.orbital.evekit.model.common;

import java.math.BigDecimal;
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
import enterprises.orbital.evekit.model.common.AccountBalance;

public class AccountBalanceTest extends AbstractModelTester<AccountBalance> {

  final int                                       accountID  = TestBase.getRandomInt(100000000);
  final int                                       accountKey = TestBase.getRandomInt(100000000);
  final BigDecimal                                balance    = TestBase.getRandomBigDecimal(100000000);

  final ClassUnderTestConstructor<AccountBalance> eol        = new ClassUnderTestConstructor<AccountBalance>() {

                                                               @Override
                                                               public AccountBalance getCUT() {
                                                                 return new AccountBalance(accountID, accountKey, balance);
                                                               }

                                                             };

  final ClassUnderTestConstructor<AccountBalance> live       = new ClassUnderTestConstructor<AccountBalance>() {
                                                               @Override
                                                               public AccountBalance getCUT() {
                                                                 return new AccountBalance(accountID, accountKey + 1, balance);
                                                               }

                                                             };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<AccountBalance>() {

      @Override
      public AccountBalance[] getVariants() {
        return new AccountBalance[] {
            new AccountBalance(accountID + 1, accountKey, balance), new AccountBalance(accountID, accountKey + 1, balance),
            new AccountBalance(accountID, accountKey, balance.add(BigDecimal.TEN))
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<AccountBalance>() {

      @Override
      public AccountBalance getModel(SynchronizedEveAccount account, long time) {
        return AccountBalance.get(account, time, accountID);
      }

    });
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - balances for a different account
    // - balances not live at the given time
    // - balances with a different key
    AccountBalance existing, keyed;

    keyed = new AccountBalance(accountID, accountKey, balance);
    keyed.setup(testAccount, 7777L);
    keyed = CachedData.updateData(keyed);

    // Different key
    existing = new AccountBalance(accountID + 10, accountKey + 10, balance);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new AccountBalance(accountID, accountKey, balance);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new AccountBalance(accountID + 3, accountKey, balance);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new AccountBalance(accountID + 4, accountKey, balance);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    AccountBalance result = AccountBalance.getByKey(testAccount, 8888L, accountKey);
    Assert.assertEquals(keyed, result);
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - balances for a different account
    // - balances not live at the given time
    AccountBalance existing;
    Map<Integer, AccountBalance> listCheck = new HashMap<Integer, AccountBalance>();

    existing = new AccountBalance(accountID, accountKey, balance);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(accountID, existing);

    existing = new AccountBalance(accountID + 10, accountKey, balance);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(accountID + 10, existing);

    // Associated with different account
    existing = new AccountBalance(accountID, accountKey, balance);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new AccountBalance(accountID + 3, accountKey, balance);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new AccountBalance(accountID + 4, accountKey, balance);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<AccountBalance> result = AccountBalance.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (AccountBalance next : result) {
      int accountID = next.getAccountID();
      Assert.assertTrue(listCheck.containsKey(accountID));
      Assert.assertEquals(listCheck.get(accountID), next);
    }

  }

}
