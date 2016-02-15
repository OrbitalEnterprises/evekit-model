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

public class SecurityTitleTest extends AbstractModelTester<SecurityTitle> {

  final long                                     titleID   = TestBase.getRandomInt(100000000);
  final String                                   titleName = " test title name";

  final ClassUnderTestConstructor<SecurityTitle> eol       = new ClassUnderTestConstructor<SecurityTitle>() {

                                                             @Override
                                                             public SecurityTitle getCUT() {
                                                               return new SecurityTitle(titleID, titleName);
                                                             }

                                                           };

  final ClassUnderTestConstructor<SecurityTitle> live      = new ClassUnderTestConstructor<SecurityTitle>() {
                                                             @Override
                                                             public SecurityTitle getCUT() {
                                                               return new SecurityTitle(titleID, titleName + " 1");
                                                             }

                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SecurityTitle>() {

      @Override
      public SecurityTitle[] getVariants() {
        return new SecurityTitle[] {
            new SecurityTitle(titleID + 1, titleName), new SecurityTitle(titleID, titleName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<SecurityTitle>() {

      @Override
      public SecurityTitle getModel(SynchronizedEveAccount account, long time) {
        return SecurityTitle.get(account, time, titleID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    SecurityTitle existing;
    Map<Long, SecurityTitle> listCheck = new HashMap<Long, SecurityTitle>();

    existing = new SecurityTitle(titleID, titleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(titleID, existing);

    existing = new SecurityTitle(titleID + 1, titleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(titleID + 1, existing);

    // Associated with different account
    existing = new SecurityTitle(titleID + 2, titleName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new SecurityTitle(titleID + 3, titleName);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new SecurityTitle(titleID + 4, titleName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<SecurityTitle> result = SecurityTitle.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (SecurityTitle next : result) {
      long titleID = next.getTitleID();
      Assert.assertTrue(listCheck.containsKey(titleID));
      Assert.assertEquals(listCheck.get(titleID), next);
    }

  }

}
