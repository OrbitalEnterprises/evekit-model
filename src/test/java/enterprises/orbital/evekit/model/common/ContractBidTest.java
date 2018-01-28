package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractBidTest extends AbstractModelTester<ContractBid> {
  private final int                                   bidID      = TestBase.getRandomInt(100000000);
  private final int                                   contractID = TestBase.getRandomInt(100000000);
  private final int                                   bidderID   = TestBase.getRandomInt(100000000);
  private final long                                   dateBid    = TestBase.getRandomInt(100000000);
  private final BigDecimal                             amount     = TestBase.getRandomBigDecimal(100000000);

  final ClassUnderTestConstructor<ContractBid> eol        = () -> new ContractBid(bidID, contractID, bidderID, dateBid, amount);

  final ClassUnderTestConstructor<ContractBid> live       = () -> new ContractBid(bidID, contractID, bidderID + 1, dateBid, amount);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new ContractBid[] {
        new ContractBid(bidID + 1, contractID, bidderID, dateBid, amount), new ContractBid(bidID, contractID + 1, bidderID, dateBid, amount),
        new ContractBid(bidID, contractID, bidderID + 1, dateBid, amount), new ContractBid(bidID, contractID, bidderID, dateBid + 1, amount),
        new ContractBid(bidID, contractID, bidderID, dateBid, amount.add(BigDecimal.TEN))
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> ContractBid.get(account, time, contractID, bidID));
  }

  @Test
  public void testGetAllBids() throws Exception {
    // Should exclude:
    // - bids for a different account
    // - bids not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    ContractBid existing;
    Map<Integer, Map<Integer, ContractBid>> listCheck = new HashMap<>();

    existing = new ContractBid(bidID, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID, new HashMap<>());
    listCheck.get(bidID).put(contractID, existing);

    existing = new ContractBid(bidID, contractID + 10, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(bidID).put(contractID + 10, existing);

    existing = new ContractBid(bidID + 10, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID + 10, new HashMap<>());
    listCheck.get(bidID + 10).put(contractID, existing);

    existing = new ContractBid(bidID + 10, contractID + 10, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(bidID + 10).put(contractID + 10, existing);

    // Associated with different account
    existing = new ContractBid(bidID, contractID, bidderID, dateBid, amount);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ContractBid(bidID + 5, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ContractBid(bidID + 3, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<ContractBid> result = CachedData.retrieveAll(8888L, (long contid, AttributeSelector ats) ->
        ContractBid.accessQuery(testAccount, contid, 1000, false, ats, AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any()));
    Assert.assertEquals(4, result.size());
    for (ContractBid next : result) {
      int bidID = next.getBidID();
      int contractID = next.getContractID();
      Assert.assertTrue(listCheck.containsKey(bidID));
      Assert.assertTrue(listCheck.get(bidID).containsKey(contractID));
      Assert.assertEquals(listCheck.get(bidID).get(contractID), next);
    }
  }

}
