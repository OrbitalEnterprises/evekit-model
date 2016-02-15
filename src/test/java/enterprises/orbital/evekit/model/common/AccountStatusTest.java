package enterprises.orbital.evekit.model.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.common.AccountStatus;

public class AccountStatusTest extends AbstractModelTester<AccountStatus> {

  final long       paidUntil              = TestBase.getRandomInt(100000000);
  final long       createDate             = TestBase.getRandomInt(100000000);
  final int        logonCount             = TestBase.getRandomInt(100000000);
  final int        logonMinutes           = TestBase.getRandomInt(100000000);
  final List<Long> multiCharacterTraining = new ArrayList<Long>();

  public AccountStatusTest() {
    int numChars = TestBase.getRandomInt(5) + 2;
    for (int i = 0; i < numChars; i++) {
      multiCharacterTraining.add((long) TestBase.getRandomInt(100000000));
    }
  }

  final ClassUnderTestConstructor<AccountStatus> eol  = new ClassUnderTestConstructor<AccountStatus>() {

                                                        @Override
                                                        public AccountStatus getCUT() {
                                                          AccountStatus result = new AccountStatus(paidUntil, createDate, logonCount, logonMinutes);
                                                          result.getMultiCharacterTraining().addAll(multiCharacterTraining);
                                                          return result;
                                                        }

                                                      };

  final ClassUnderTestConstructor<AccountStatus> live = new ClassUnderTestConstructor<AccountStatus>() {
                                                        @Override
                                                        public AccountStatus getCUT() {
                                                          AccountStatus result = new AccountStatus(paidUntil + 1, createDate, logonCount, logonMinutes);
                                                          result.getMultiCharacterTraining().addAll(multiCharacterTraining);
                                                          return result;
                                                        }

                                                      };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<AccountStatus>() {

      @Override
      public AccountStatus[] getVariants() {
        List<AccountStatus> result = new ArrayList<AccountStatus>();
        result.add(new AccountStatus(paidUntil + 1, createDate, logonCount, logonMinutes));
        result.add(new AccountStatus(paidUntil, createDate + 1, logonCount, logonMinutes));
        result.add(new AccountStatus(paidUntil, createDate, logonCount + 1, logonMinutes));
        result.add(new AccountStatus(paidUntil, createDate, logonCount, logonMinutes + 1));
        result.add(new AccountStatus(paidUntil, createDate, logonCount, logonMinutes));
        for (AccountStatus next : result) {
          next.getMultiCharacterTraining().addAll(multiCharacterTraining);
        }
        result.get(result.size() - 1).getMultiCharacterTraining().add(1234L);
        return result.toArray(new AccountStatus[result.size()]);
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_STATUS));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<AccountStatus>() {

      @Override
      public AccountStatus getModel(SynchronizedEveAccount account, long time) {
        return AccountStatus.get(account, time);
      }

    });
  }

}
