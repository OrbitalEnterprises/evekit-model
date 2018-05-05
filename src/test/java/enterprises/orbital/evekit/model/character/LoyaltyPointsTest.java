package enterprises.orbital.evekit.model.character;

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

public class LoyaltyPointsTest extends AbstractModelTester<LoyaltyPoints> {
  private final int corporationID = TestBase.getRandomInt(100000000);
  private final int loyaltyPoints = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<LoyaltyPoints> eol = () -> new LoyaltyPoints(
      corporationID, loyaltyPoints);

  final ClassUnderTestConstructor<LoyaltyPoints> live = () -> new LoyaltyPoints(
      corporationID, loyaltyPoints + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new LoyaltyPoints[]{
        new LoyaltyPoints(corporationID + 1, loyaltyPoints),
        new LoyaltyPoints(corporationID, loyaltyPoints + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> LoyaltyPoints.get(account, time, corporationID));
  }

  @Test
  public void testGetAllLoyaltyPointss() throws Exception {
    // Should exclude:
    // - loyalty points for a different account
    // - loyalty points not live at the given time
    LoyaltyPoints existing;
    Map<Integer, LoyaltyPoints> listCheck = new HashMap<>();

    existing = new LoyaltyPoints(corporationID, loyaltyPoints);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(corporationID, existing);

    existing = new LoyaltyPoints(corporationID + 10, loyaltyPoints);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(corporationID + 10, existing);

    existing = new LoyaltyPoints(corporationID + 20, loyaltyPoints);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(corporationID + 20, existing);

    // Associated with different account
    existing = new LoyaltyPoints(corporationID, loyaltyPoints);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new LoyaltyPoints(corporationID + 5, loyaltyPoints);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new LoyaltyPoints(corporationID + 3, loyaltyPoints);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<LoyaltyPoints> result = CachedData.retrieveAll(8888L,
                                                        (contid, at) -> LoyaltyPoints.accessQuery(testAccount, contid,
                                                                                                  1000,
                                                                                                  false, at,
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (LoyaltyPoints next : result) {
      int corporationID = next.getCorporationID();
      Assert.assertTrue(listCheck.containsKey(corporationID));
      Assert.assertEquals(listCheck.get(corporationID), next);
    }
  }

}
