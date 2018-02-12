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

public class KillItemTest extends AbstractModelTester<KillItem> {

  private final int killID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int flag = TestBase.getRandomInt(100000000);
  private final long qtyDestroyed = TestBase.getRandomInt(100000000);
  private final long qtyDropped = TestBase.getRandomInt(100000000);
  private final int singleton = TestBase.getRandomInt();
  private final int sequence = TestBase.getRandomInt(100000000);
  private final int containerSequence = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<KillItem> eol = () -> new KillItem(
      killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);

  final ClassUnderTestConstructor<KillItem> live = () -> new KillItem(
      killID, typeID + 1, flag, qtyDestroyed, qtyDropped, singleton, sequence,
      containerSequence);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new KillItem[]{
        new KillItem(killID + 1, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence),
        new KillItem(killID, typeID + 1, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence),
        new KillItem(killID, typeID, flag + 1, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence),
        new KillItem(killID, typeID, flag, qtyDestroyed + 1, qtyDropped, singleton, sequence, containerSequence),
        new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped + 1, singleton, sequence, containerSequence),
        new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton + 1, sequence, containerSequence),
        new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 1, containerSequence),
        new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> KillItem.get(account, time, killID, sequence));
  }

  @Test
  public void testGetAllKillItems() throws Exception {
    // Should exclude:
    // - items for a different account
    // - items not live at the given time
    // - items for a different kill ID
    KillItem existing;
    Map<Integer, KillItem> listCheck = new HashMap<>();

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(sequence, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 10,
                            containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(sequence + 10, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 20,
                            containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(sequence + 20, existing);

    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 30,
                            containerSequence);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(sequence + 30, existing);

    // Associated with different account
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different kill
    existing = new KillItem(killID + 1, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence, containerSequence);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 5, containerSequence);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new KillItem(killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence + 3, containerSequence);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<KillItem> result = CachedData.retrieveAll(8888L,
                                                   (contid, at) -> KillItem.accessQuery(testAccount, contid, 1000,
                                                                                        false, at,
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (KillItem next : result) {
      int sequence = next.getSequence();
      Assert.assertEquals(killID, next.getKillID());
      Assert.assertTrue(listCheck.containsKey(sequence));
      Assert.assertEquals(listCheck.get(sequence), next);
    }

  }

}
