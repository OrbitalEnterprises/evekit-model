package enterprises.orbital.evekit.model.common;

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
import enterprises.orbital.evekit.model.common.ContractItem;

public class ContractItemTest extends AbstractModelTester<ContractItem> {

  final long                                    contractID  = TestBase.getRandomInt(100000000);
  final long                                    recordID    = TestBase.getRandomInt(100000000);
  final int                                     typeID      = TestBase.getRandomInt(100000000);
  final long                                    quantity    = TestBase.getRandomInt(100000000);
  final int                                     rawQuantity = TestBase.getRandomInt(100000000);
  final boolean                                 singleton   = true;
  final boolean                                 included    = false;

  final ClassUnderTestConstructor<ContractItem> eol         = new ClassUnderTestConstructor<ContractItem>() {

                                                              @Override
                                                              public ContractItem getCUT() {
                                                                return new ContractItem(
                                                                    contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
                                                              }

                                                            };

  final ClassUnderTestConstructor<ContractItem> live        = new ClassUnderTestConstructor<ContractItem>() {
                                                              @Override
                                                              public ContractItem getCUT() {
                                                                return new ContractItem(
                                                                    contractID, recordID, typeID + 1, quantity, rawQuantity, singleton, included);
                                                              }

                                                            };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ContractItem>() {

      @Override
      public ContractItem[] getVariants() {
        return new ContractItem[] {
            new ContractItem(contractID + 1, recordID, typeID, quantity, rawQuantity, singleton, included),
            new ContractItem(contractID, recordID + 1, typeID, quantity, rawQuantity, singleton, included),
            new ContractItem(contractID, recordID, typeID + 1, quantity, rawQuantity, singleton, included),
            new ContractItem(contractID, recordID, typeID, quantity + 1, rawQuantity, singleton, included),
            new ContractItem(contractID, recordID, typeID, quantity, rawQuantity + 1, singleton, included),
            new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, !singleton, included),
            new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, singleton, !included)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ContractItem>() {

      @Override
      public ContractItem getModel(SynchronizedEveAccount account, long time) {
        return ContractItem.get(account, time, contractID, recordID);
      }

    });
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
    Map<Long, ContractItem> listCheck = new HashMap<Long, ContractItem>();

    existing = new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(recordID, existing);

    existing = new ContractItem(contractID, recordID + 10, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(recordID + 10, existing);

    existing = new ContractItem(contractID, recordID + 20, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(recordID + 20, existing);

    existing = new ContractItem(contractID, recordID + 30, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(recordID + 30, existing);

    // Associated with different account
    existing = new ContractItem(contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with a different contract
    existing = new ContractItem(contractID + 1, recordID, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ContractItem(contractID, recordID + 5, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ContractItem(contractID, recordID + 3, typeID, quantity, rawQuantity, singleton, included);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contacts are returned
    List<ContractItem> result = ContractItem.getAllContractItems(testAccount, 8888L, contractID, 10, -1);
    Assert.assertEquals(listCheck.size(), result.size());
    for (ContractItem next : result) {
      long recordID = next.getRecordID();
      Assert.assertEquals(contractID, next.getContractID());
      Assert.assertTrue(listCheck.containsKey(recordID));
      Assert.assertEquals(listCheck.get(recordID), next);
    }

    // Verify limited set returned
    result = ContractItem.getAllContractItems(testAccount, 8888L, contractID, 2, recordID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(recordID), result.get(0));
    Assert.assertEquals(listCheck.get(recordID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = ContractItem.getAllContractItems(testAccount, 8888L, contractID, 100, recordID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(recordID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(recordID + 30), result.get(1));

  }

}
