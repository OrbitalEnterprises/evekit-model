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
import enterprises.orbital.evekit.model.common.ContractBid;

public class ContractBidTest extends AbstractModelTester<ContractBid> {
  final long                                   bidID      = TestBase.getRandomInt(100000000);
  final long                                   contractID = TestBase.getRandomInt(100000000);
  final long                                   bidderID   = TestBase.getRandomInt(100000000);
  final long                                   dateBid    = TestBase.getRandomInt(100000000);
  final BigDecimal                             amount     = TestBase.getRandomBigDecimal(100000000);

  final ClassUnderTestConstructor<ContractBid> eol        = new ClassUnderTestConstructor<ContractBid>() {

                                                            @Override
                                                            public ContractBid getCUT() {
                                                              return new ContractBid(bidID, contractID, bidderID, dateBid, amount);
                                                            }

                                                          };

  final ClassUnderTestConstructor<ContractBid> live       = new ClassUnderTestConstructor<ContractBid>() {
                                                            @Override
                                                            public ContractBid getCUT() {
                                                              return new ContractBid(bidID, contractID, bidderID + 1, dateBid, amount);
                                                            }

                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ContractBid>() {

      @Override
      public ContractBid[] getVariants() {
        return new ContractBid[] {
            new ContractBid(bidID + 1, contractID, bidderID, dateBid, amount), new ContractBid(bidID, contractID + 1, bidderID, dateBid, amount),
            new ContractBid(bidID, contractID, bidderID + 1, dateBid, amount), new ContractBid(bidID, contractID, bidderID, dateBid + 1, amount),
            new ContractBid(bidID, contractID, bidderID, dateBid, amount.add(BigDecimal.TEN))
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ContractBid>() {

      @Override
      public ContractBid getModel(SynchronizedEveAccount account, long time) {
        return ContractBid.get(account, time, contractID, bidID);
      }

    });
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
    Map<Long, Map<Long, ContractBid>> listCheck = new HashMap<Long, Map<Long, ContractBid>>();

    existing = new ContractBid(bidID, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID, new HashMap<Long, ContractBid>());
    listCheck.get(bidID).put(contractID, existing);

    existing = new ContractBid(bidID, contractID + 10, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(bidID).put(contractID + 10, existing);

    existing = new ContractBid(bidID + 10, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID + 10, new HashMap<Long, ContractBid>());
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
    List<ContractBid> result = ContractBid.getAllBids(testAccount, 8888L, 10, -1);
    Assert.assertEquals(4, result.size());
    for (ContractBid next : result) {
      long bidID = next.getBidID();
      long contractID = next.getContractID();
      Assert.assertTrue(listCheck.containsKey(bidID));
      Assert.assertTrue(listCheck.get(bidID).containsKey(contractID));
      Assert.assertEquals(listCheck.get(bidID).get(contractID), next);
    }

    // Verify limited set returned
    long limit = listCheck.get(bidID).get(contractID).getCid();
    result = ContractBid.getAllBids(testAccount, 8888L, 2, limit - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(bidID).get(contractID), result.get(0));
    Assert.assertEquals(listCheck.get(bidID).get(contractID + 10), result.get(1));

    // Verify continuation ID returns proper set
    limit = listCheck.get(bidID).get(contractID + 10).getCid();
    result = ContractBid.getAllBids(testAccount, 8888L, 100, limit);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(bidID + 10).get(contractID), result.get(0));
    Assert.assertEquals(listCheck.get(bidID + 10).get(contractID + 10), result.get(1));

  }

  @Test
  public void testGetAllBidsByContractID() throws Exception {
    // Should exclude:
    // - bids for a different account
    // - bids not live at the given time
    // - bids for a different contract ID
    // Need to test:
    // - max results limitation
    // - continuation ID
    ContractBid existing;
    Map<Long, ContractBid> listCheck = new HashMap<Long, ContractBid>();

    existing = new ContractBid(bidID, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID, existing);

    existing = new ContractBid(bidID + 10, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID + 10, existing);

    existing = new ContractBid(bidID + 20, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID + 20, existing);

    existing = new ContractBid(bidID + 30, contractID, bidderID, dateBid, amount);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(bidID + 30, existing);

    // Associated with different account
    existing = new ContractBid(bidID, contractID, bidderID, dateBid, amount);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different contract
    existing = new ContractBid(bidID, contractID + 1, bidderID, dateBid, amount);
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
    List<ContractBid> result = ContractBid.getAllBidsByContractID(testAccount, 8888L, contractID, 10, -1);
    Assert.assertEquals(listCheck.size(), result.size());
    for (ContractBid next : result) {
      long bidID = next.getBidID();
      Assert.assertEquals(contractID, next.getContractID());
      Assert.assertTrue(listCheck.containsKey(bidID));
      Assert.assertEquals(listCheck.get(bidID), next);
    }

    // Verify limited set returned
    result = ContractBid.getAllBidsByContractID(testAccount, 8888L, contractID, 2, bidID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(bidID), result.get(0));
    Assert.assertEquals(listCheck.get(bidID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = ContractBid.getAllBidsByContractID(testAccount, 8888L, contractID, 100, bidID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(bidID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(bidID + 30), result.get(1));

  }

}
