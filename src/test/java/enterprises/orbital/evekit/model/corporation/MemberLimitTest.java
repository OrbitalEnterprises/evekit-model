package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class MemberLimitTest extends AbstractModelTester<MemberLimit> {

  private final int memberLimit = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<MemberLimit> eol = () -> new MemberLimit(memberLimit);

  final ClassUnderTestConstructor<MemberLimit> live = () -> new MemberLimit(memberLimit + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MemberLimit[]{
        new MemberLimit(memberLimit + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_TRACKING));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, MemberLimit::get);
  }

}
