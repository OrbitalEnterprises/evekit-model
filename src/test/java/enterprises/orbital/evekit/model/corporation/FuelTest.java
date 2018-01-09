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

public class FuelTest extends AbstractModelTester<Fuel> {

  final long                            itemID   = TestBase.getRandomInt(100000000);
  final int                             typeID   = TestBase.getRandomInt(100000000);
  final int                             quantity = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Fuel> eol      = new ClassUnderTestConstructor<Fuel>() {

                                                   @Override
                                                   public Fuel getCUT() {
                                                     return new Fuel(itemID, typeID, quantity);
                                                   }

                                                 };

  final ClassUnderTestConstructor<Fuel> live     = new ClassUnderTestConstructor<Fuel>() {
                                                   @Override
                                                   public Fuel getCUT() {
                                                     return new Fuel(itemID, typeID, quantity + 1);
                                                   }

                                                 };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Fuel>() {

      @Override
      public Fuel[] getVariants() {
        return new Fuel[] {
            new Fuel(itemID + 1, typeID, quantity), new Fuel(itemID, typeID + 1, quantity), new Fuel(itemID, typeID, quantity + 1),
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Fuel>() {

      @Override
      public Fuel getModel(SynchronizedEveAccount account, long time) {
        return Fuel.get(account, time, itemID, typeID);
      }

    });
  }

  @Test
  public void testGetAllByItemID() throws Exception {
    // Should exclude:
    // - fuel for a different account
    // - fuel not live at the given time
    // - fuel for a different itemID
    Fuel existing;
    Map<Integer, Fuel> listCheck = new HashMap<Integer, Fuel>();

    existing = new Fuel(itemID, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID, existing);

    existing = new Fuel(itemID, typeID + 1, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 1, existing);

    // Associated with different account
    existing = new Fuel(itemID, typeID, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with different item ID
    existing = new Fuel(itemID + 1, typeID, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Fuel(itemID, typeID + 3, quantity);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Fuel(itemID, typeID + 4, quantity);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Fuel> result = Fuel.getAllByItemID(testAccount, 8888L, itemID);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Fuel next : result) {
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(typeID));
      Assert.assertEquals(listCheck.get(typeID), next);
    }
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - fuel for a different account
    // - fuel not live at the given time
    Fuel existing;
    Map<Long, Fuel> listCheck = new HashMap<Long, Fuel>();

    existing = new Fuel(itemID, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID, existing);

    existing = new Fuel(itemID + 1, typeID + 1, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 1, existing);

    // Associated with different account
    existing = new Fuel(itemID + 2, typeID + 2, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Fuel(itemID + 3, typeID + 3, quantity);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Fuel(itemID + 4, typeID + 4, quantity);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Fuel> result = Fuel.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Fuel next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

  }

}
