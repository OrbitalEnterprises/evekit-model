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

public class CorporationMemberMedalTest extends AbstractModelTester<CorporationMemberMedal> {

  final int                                               medalID     = TestBase.getRandomInt(100000000);
  final long                                              characterID = TestBase.getRandomInt(100000000);
  final long                                              issued      = TestBase.getRandomInt(100000000);
  final long                                              issuerID    = TestBase.getRandomInt(100000000);
  final String                                            reason      = "test reason";
  final String                                            status      = "test status";

  final ClassUnderTestConstructor<CorporationMemberMedal> eol         = new ClassUnderTestConstructor<CorporationMemberMedal>() {

                                                                        @Override
                                                                        public CorporationMemberMedal getCUT() {
                                                                          return new CorporationMemberMedal(
                                                                              medalID, characterID, issued, issuerID, reason, status);
                                                                        }

                                                                      };

  final ClassUnderTestConstructor<CorporationMemberMedal> live        = new ClassUnderTestConstructor<CorporationMemberMedal>() {
                                                                        @Override
                                                                        public CorporationMemberMedal getCUT() {
                                                                          return new CorporationMemberMedal(
                                                                              medalID, characterID, issued, issuerID + 1, reason, status);
                                                                        }

                                                                      };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationMemberMedal>() {

      @Override
      public CorporationMemberMedal[] getVariants() {
        return new CorporationMemberMedal[] {
            new CorporationMemberMedal(medalID + 1, characterID, issued, issuerID, reason, status),
            new CorporationMemberMedal(medalID, characterID + 1, issued, issuerID, reason, status),
            new CorporationMemberMedal(medalID, characterID, issued + 1, issuerID, reason, status),
            new CorporationMemberMedal(medalID, characterID, issued, issuerID + 1, reason, status),
            new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason + " 1", status),
            new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason, status + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_MEDALS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CorporationMemberMedal>() {

      @Override
      public CorporationMemberMedal getModel(SynchronizedEveAccount account, long time) {
        return CorporationMemberMedal.get(account, time, medalID, characterID, issued);
      }

    });
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - medals for a different account
    // - medals not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    CorporationMemberMedal existing;
    Map<Long, CorporationMemberMedal> listCheck = new HashMap<Long, CorporationMemberMedal>();

    existing = new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued, existing);

    existing = new CorporationMemberMedal(medalID, characterID + 10, issued + 10, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued + 10, existing);

    existing = new CorporationMemberMedal(medalID, characterID + 20, issued + 20, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued + 20, existing);

    existing = new CorporationMemberMedal(medalID, characterID + 30, issued + 30, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued + 30, existing);

    // Associated with different account
    existing = new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason, status);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CorporationMemberMedal(medalID, characterID + 5, issued + 5, issuerID, reason, status);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CorporationMemberMedal(medalID, characterID + 3, issued + 3, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<CorporationMemberMedal> result = CorporationMemberMedal.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationMemberMedal next : result) {
      long issued = next.getIssued();
      Assert.assertTrue(listCheck.containsKey(issued));
      Assert.assertEquals(listCheck.get(issued), next);
    }

    // Verify limited set returned
    result = CorporationMemberMedal.getAllForward(testAccount, 8888L, 2, issued - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued), result.get(0));
    Assert.assertEquals(listCheck.get(issued + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = CorporationMemberMedal.getAllForward(testAccount, 8888L, 100, issued + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued + 20), result.get(0));
    Assert.assertEquals(listCheck.get(issued + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - medals for a different account
    // - medals not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    CorporationMemberMedal existing;
    Map<Long, CorporationMemberMedal> listCheck = new HashMap<Long, CorporationMemberMedal>();

    existing = new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued, existing);

    existing = new CorporationMemberMedal(medalID, characterID + 10, issued + 10, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued + 10, existing);

    existing = new CorporationMemberMedal(medalID, characterID + 20, issued + 20, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued + 20, existing);

    existing = new CorporationMemberMedal(medalID, characterID + 30, issued + 30, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(issued + 30, existing);

    // Associated with different account
    existing = new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason, status);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CorporationMemberMedal(medalID, characterID + 5, issued + 5, issuerID, reason, status);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CorporationMemberMedal(medalID, characterID + 3, issued + 3, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<CorporationMemberMedal> result = CorporationMemberMedal.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationMemberMedal next : result) {
      long issued = next.getIssued();
      Assert.assertTrue(listCheck.containsKey(issued));
      Assert.assertEquals(listCheck.get(issued), next);
    }

    // Verify limited set returned
    result = CorporationMemberMedal.getAllBackward(testAccount, 8888L, 2, issued + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued + 30), result.get(0));
    Assert.assertEquals(listCheck.get(issued + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = CorporationMemberMedal.getAllBackward(testAccount, 8888L, 100, issued + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued + 10), result.get(0));
    Assert.assertEquals(listCheck.get(issued), result.get(1));

  }

}
