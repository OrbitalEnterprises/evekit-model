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

public class FuelTest extends AbstractModelTester<Fuel> {

  private final long starbaseID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int quantity = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Fuel> eol = () -> new Fuel(starbaseID, typeID, quantity);

  final ClassUnderTestConstructor<Fuel> live = () -> new Fuel(starbaseID, typeID, quantity + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Fuel[]{
        new Fuel(starbaseID + 1, typeID, quantity),
        new Fuel(starbaseID, typeID + 1, quantity),
        new Fuel(starbaseID, typeID, quantity + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Fuel.get(account, time, starbaseID, typeID));
  }

  @Test
  public void testGetAllByItemID() throws Exception {
    // Should exclude:
    // - fuel for a different account
    // - fuel not live at the given time
    // - fuel for a different containerID
    Fuel existing;
    Map<Integer, Fuel> listCheck = new HashMap<>();

    existing = new Fuel(starbaseID, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID, existing);

    existing = new Fuel(starbaseID, typeID + 1, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 1, existing);

    // Associated with different account
    existing = new Fuel(starbaseID, typeID, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with different item ID
    existing = new Fuel(starbaseID + 1, typeID, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Fuel(starbaseID, typeID + 3, quantity);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Fuel(starbaseID, typeID + 4, quantity);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Fuel> result = CachedData.retrieveAll(8888L,
                                               (contid, at) -> Fuel.accessQuery(testAccount, contid, 1000, false, at,
                                                                                AttributeSelector.values(starbaseID),
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any()));
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
    Map<Long, Fuel> listCheck = new HashMap<>();

    existing = new Fuel(starbaseID, typeID, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(starbaseID, existing);

    existing = new Fuel(starbaseID + 1, typeID + 1, quantity);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(starbaseID + 1, existing);

    // Associated with different account
    existing = new Fuel(starbaseID + 2, typeID + 2, quantity);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Fuel(starbaseID + 3, typeID + 3, quantity);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Fuel(starbaseID + 4, typeID + 4, quantity);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Fuel> result = CachedData.retrieveAll(8888L,
                                               (contid, at) -> Fuel.accessQuery(testAccount, contid, 1000, false, at,
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any(),
                                                                                AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Fuel next : result) {
      long itemID = next.getStarbaseID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

  }

}
