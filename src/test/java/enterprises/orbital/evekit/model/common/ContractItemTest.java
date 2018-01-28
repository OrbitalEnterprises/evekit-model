package enterprises.orbital.evekit.model.common;

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

public class ContractItemTest extends AbstractModelTester<ContractItem> {

  private final int                                    contractID  = TestBase.getRandomInt(100000000);
  private final long                                    recordID    = TestBase.getRandomInt(100000000);
  private final int                                     typeID      = TestBase.getRandomInt(100000000);
  private final int                                    quantity    = TestBase.getRandomInt(100000000);
  private final int                                    rawQuantity = TestBase.getRandomInt(100000000);
  private final boolean                                 singleton   = true;
  private final boolean                                 included    = false;

  final ClassUnderTestConstructor<ContractItem> eol         = () -> new ContractItem(
      contractID, recordID, typeID, quantity, rawQuantity, singleton, included);

  final ClassUnderTestConstructor<ContractItem> live        = () -> new ContractItem(
      contractID, recordID, typeID + 1, quantity, rawQuantity, singleton, included);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new ContractItem[] {
        new ContractItem(contractID + 1, recordID, typeID, quantity, rawQuantity, singleton, included),
        new ContractItem(contractID, recordID + 1, typeID, quantity, rawQuantity, singleton, included),
        new ContractItem(contractID, recordID, typeID + 1, quantity, rawQuantity, singleton, included),
        new ContractItem(contractID, recordID, typeID, quantity + 1, rawQuantity, singleton, included),
        new ContractItem(contractID, recordID, typeID, quantity, rawQuantity + 1, singleton, included),
        new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, !singleton, included),
        new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, singleton, !included)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> ContractItem.get(account, time, contractID, recordID));
  }

  @Test
  public void testGetAllContractItems() throws Exception {
    // Should exclude:
    // - items for a different account
    // - items not live at the given time
    // - items for a different contract ID
    // Need to test:
    // - max results limitation
    // - continuation ID
    ContractItem existing;
    Map<Long, ContractItem> listCheck = new HashMap<>();

    existing = new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(recordID, existing);

    existing = new ContractItem(contractID, recordID + 10, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(recordID + 10, existing);

    existing = new ContractItem(contractID, recordID + 20, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(recordID + 20, existing);

    existing = new ContractItem(contractID, recordID + 30, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(recordID + 30, existing);

    // Associated with different account
    existing = new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different contract
    existing = new ContractItem(contractID + 1, recordID, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ContractItem(contractID, recordID + 5, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ContractItem(contractID, recordID + 3, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<ContractItem> result = CachedData.retrieveAll(8888L, (long contid, AttributeSelector ats) ->
        ContractItem.accessQuery(testAccount, contid, 1000, false, ats, AttributeSelector.any(),
                                 AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any(),
                                 AttributeSelector.any(), AttributeSelector.any(), AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (ContractItem next : result) {
      long recordID = next.getRecordID();
      Assert.assertEquals(contractID, next.getContractID());
      Assert.assertTrue(listCheck.containsKey(recordID));
      Assert.assertEquals(listCheck.get(recordID), next);
    }
  }

}
