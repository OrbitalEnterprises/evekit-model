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

public class CorporationMemberMedalTest extends AbstractModelTester<CorporationMemberMedal> {

  private final int medalID = TestBase.getRandomInt(100000000);
  private final int characterID = TestBase.getRandomInt(100000000);
  private final long issued = TestBase.getRandomInt(100000000);
  private final int issuerID = TestBase.getRandomInt(100000000);
  private final String reason = "test reason";
  private final String status = "test status";

  final ClassUnderTestConstructor<CorporationMemberMedal> eol = () -> new CorporationMemberMedal(
      medalID, characterID, issued, issuerID, reason, status);

  final ClassUnderTestConstructor<CorporationMemberMedal> live = () -> new CorporationMemberMedal(
      medalID, characterID, issued, issuerID + 1, reason, status);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CorporationMemberMedal[]{
        new CorporationMemberMedal(medalID + 1, characterID, issued, issuerID, reason, status),
        new CorporationMemberMedal(medalID, characterID + 1, issued, issuerID, reason, status),
        new CorporationMemberMedal(medalID, characterID, issued + 1, issuerID, reason, status),
        new CorporationMemberMedal(medalID, characterID, issued, issuerID + 1, reason, status),
        new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason + " 1", status),
        new CorporationMemberMedal(medalID, characterID, issued, issuerID, reason, status + " 1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_MEDALS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live,
                       (account, time) -> CorporationMemberMedal.get(account, time, medalID, characterID, issued));
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
    Map<Long, CorporationMemberMedal> listCheck = new HashMap<>();

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

    // Verify all medals are returned
    List<CorporationMemberMedal> result = CachedData.retrieveAll(8888L,
                                                                 (contid, at) -> CorporationMemberMedal.accessQuery(
                                                                     testAccount, contid, 1000, false, at,
                                                                     AttributeSelector.any(), AttributeSelector.any(),
                                                                     AttributeSelector.any(), AttributeSelector.any(),
                                                                     AttributeSelector.any(), AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationMemberMedal next : result) {
      long issued = next.getIssued();
      Assert.assertTrue(listCheck.containsKey(issued));
      Assert.assertEquals(listCheck.get(issued), next);
    }

    // Verify limited set returned
    result = CorporationMemberMedal.accessQuery(testAccount, 0, 2, false,
                                                AttributeSelector.values(8888L),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.range(
                                                    issued - 1, Long.MAX_VALUE),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued), result.get(0));
    Assert.assertEquals(listCheck.get(issued + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = CorporationMemberMedal.accessQuery(testAccount, 0, 100, false,
                                                AttributeSelector.values(8888L),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.range(
                                                    issued + 10 + 1, Long.MAX_VALUE),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.any());
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
    Map<Long, CorporationMemberMedal> listCheck = new HashMap<>();

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
    List<CorporationMemberMedal> result = CorporationMemberMedal.accessQuery(
        testAccount, Long.MAX_VALUE, 10, true,
        AttributeSelector.values(8888L),
        AttributeSelector.any(), AttributeSelector.any(),
        AttributeSelector.any(), AttributeSelector.any(),
        AttributeSelector.any(), AttributeSelector.any());
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationMemberMedal next : result) {
      long issued = next.getIssued();
      Assert.assertTrue(listCheck.containsKey(issued));
      Assert.assertEquals(listCheck.get(issued), next);
    }

    // Verify limited set returned
    result = CorporationMemberMedal.accessQuery(testAccount, Long.MAX_VALUE, 2, true, AttributeSelector.values(8888L),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.range(0,
                                                                        issued + 30 + 1),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued + 30), result.get(0));
    Assert.assertEquals(listCheck.get(issued + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = CorporationMemberMedal.accessQuery(testAccount, Long.MAX_VALUE, 100, true,
                                                AttributeSelector.values(8888L),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.range(0,
                                                                        issued + 20 - 1),
                                                AttributeSelector.any(),
                                                AttributeSelector.any(),
                                                AttributeSelector.any());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(issued + 10), result.get(0));
    Assert.assertEquals(listCheck.get(issued), result.get(1));

  }

}
