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
import enterprises.orbital.evekit.model.common.KillItem;

public class KillItemTest extends AbstractModelTester<KillItem> {

  final long                                killID            = TestBase.getRandomInt(100000000);
  final int                                 typeID            = TestBase.getRandomInt(100000000);
  final int                                 flag              = TestBase.getRandomInt(100000000);
  final int                                 qtyDestroyed      = TestBase.getRandomInt(100000000);
  final int                                 qtyDropped        = TestBase.getRandomInt(100000000);
  final boolean                             singleton         = false;
  final int                                 sequence          = TestBase.getRandomInt(100000000);
  final int                                 containerSequence = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<KillItem> eol               = new ClassUnderTestConstructor<KillItem>() {

                                                                @Override
                                                                public KillItem getCUT() {
                                                                  return new KillItem(
                                                                      killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
                                                                }

                                                              };

  final ClassUnderTestConstructor<KillItem> live              = new ClassUnderTestConstructor<KillItem>() {
                                                                @Override
                                                                public KillItem getCUT() {
                                                                  return new KillItem(
                                                                      killID, typeID + 1, flag, qtyDestroyed, qtyDropped, singleton, sequence,
                                                                      containerSequence);
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<KillItem>() {

      @Override
      public KillItem[] getVariants() {
        return new KillItem[] {
            new KillItem(killID + 1, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence),
            new KillItem(killID, typeID + 1, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence),
            new KillItem(killID, typeID, flag + 1, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence),
            new KillItem(killID, typeID, flag, qtyDestroyed + 1, qtyDropped, singleton, sequence, containerSequence),
            new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped + 1, singleton, sequence, containerSequence),
            new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, !singleton, sequence, containerSequence),
            new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 1, containerSequence),
            new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<KillItem>() {

      @Override
      public KillItem getModel(SynchronizedEveAccount account, long time) {
        return KillItem.get(account, time, killID, sequence);
      }

    });
  }

  @Test
  public void testGetContainedKillItems() throws Exception {
    // Should exclude:
    // - items for a different account
    // - items not live at the given time
    // - items for a different kill ID
    // - items in a different container
    KillItem existing;
    Map<Integer, KillItem> listCheck = new HashMap<Integer, KillItem>();

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 10, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence + 10, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 20, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence + 20, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 30, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence + 30, existing);

    // Associated with different account
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with a different kill
    existing = new KillItem(killID + 1, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with a different container
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 40, containerSequence + 20);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 5, containerSequence);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 3, containerSequence);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contacts are returned
    List<KillItem> result = KillItem.getContainedKillItems(testAccount, 8888L, killID, containerSequence);
    Assert.assertEquals(listCheck.size(), result.size());
    for (KillItem next : result) {
      int sequence = next.getSequence();
      Assert.assertEquals(killID, next.getKillID());
      Assert.assertTrue(listCheck.containsKey(sequence));
      Assert.assertEquals(listCheck.get(sequence), next);
    }

  }

  @Test
  public void testGetAllKillItems() throws Exception {
    // Should exclude:
    // - items for a different account
    // - items not live at the given time
    // - items for a different kill ID
    // Need to test:
    // - max results limitation
    // - continuation ID
    KillItem existing;
    Map<Integer, KillItem> listCheck = new HashMap<Integer, KillItem>();

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 10, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence + 10, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 20, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence + 20, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 30, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(sequence + 30, existing);

    // Associated with different account
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with a different kill
    existing = new KillItem(killID + 1, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 5, containerSequence);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 3, containerSequence);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contacts are returned
    List<KillItem> result = KillItem.getAllKillItems(testAccount, 8888L, killID, 10, KillItem.TOP_LEVEL - 1);
    Assert.assertEquals(listCheck.size(), result.size());
    for (KillItem next : result) {
      int sequence = next.getSequence();
      Assert.assertEquals(killID, next.getKillID());
      Assert.assertTrue(listCheck.containsKey(sequence));
      Assert.assertEquals(listCheck.get(sequence), next);
    }

    // Verify limited set returned
    result = KillItem.getAllKillItems(testAccount, 8888L, killID, 2, sequence - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(sequence), result.get(0));
    Assert.assertEquals(listCheck.get(sequence + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = KillItem.getAllKillItems(testAccount, 8888L, killID, 100, sequence + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(sequence + 20), result.get(0));
    Assert.assertEquals(listCheck.get(sequence + 30), result.get(1));

  }

}
