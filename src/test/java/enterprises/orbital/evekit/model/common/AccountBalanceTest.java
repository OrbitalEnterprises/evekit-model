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

  private final int                                       division  = TestBase.getRandomInt(100000000);
  private final BigDecimal                                balance    = TestBase.getRandomBigDecimal(100000000);

  private final ClassUnderTestConstructor<AccountBalance> eol        = () -> new AccountBalance(division, balance);
  private final ClassUnderTestConstructor<AccountBalance> live       = () -> new AccountBalance(division, balance.add(BigDecimal.ONE));

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new AccountBalance[] {
            new AccountBalance(division + 1, balance), new AccountBalance(division, balance.add(BigDecimal.TEN))
        }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> AccountBalance.get(account, time, division));
  }

  @Test
  public void testGetByKey() throws Exception {
    // Should exclude:
    // - balances for a different account
    // - balances not live at the given time
    // - balances with a different division
    AccountBalance existing, keyed;

    keyed = new AccountBalance(division, balance);
    keyed.setup(testAccount, 7777L);
    keyed = CachedData.update(keyed);

    // Different division
    existing = new AccountBalance(division + 10, balance);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = new AccountBalance(division, balance);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new AccountBalance(division + 3, balance);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new AccountBalance(division + 4, balance);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    AccountBalance result = AccountBalance.get(testAccount, 8888L, division);
    Assert.assertEquals(keyed, result);
  }

}
