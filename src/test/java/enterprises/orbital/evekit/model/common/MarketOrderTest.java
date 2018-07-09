package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketOrderTest extends AbstractModelTester<MarketOrder> {

  private final long orderID = TestBase.getRandomInt(100000000);
  private final int walletDivision = TestBase.getRandomInt(100000000);
  private final boolean bid = false;
  private final long charID = TestBase.getRandomInt(100000000);
  private final int duration = TestBase.getRandomInt(100000000);
  private final BigDecimal escrow = TestBase.getRandomBigDecimal(100000000);
  private final long issued = TestBase.getRandomInt(100000000);
  private final int issuedBy = TestBase.getRandomInt(100000000);
  private final int minVolume = TestBase.getRandomInt(100000000);
  private final String orderState = TestBase.getRandomText(50);
  private final BigDecimal price = TestBase.getRandomBigDecimal(100000000);
  private final String orderRange = TestBase.getRandomText(50);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int volEntered = TestBase.getRandomInt(100000000);
  private final int volRemaining = TestBase.getRandomInt(100000000);
  private final int regionID = TestBase.getRandomInt();
  private final long locationID = TestBase.getRandomLong();
  private final boolean isCorp = TestBase.getRandomBoolean();

  private final ClassUnderTestConstructor<MarketOrder> eol = () -> new MarketOrder(
      orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
      orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp);

  private final ClassUnderTestConstructor<MarketOrder> live = () -> new MarketOrder(
      orderID, walletDivision + 1, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
      price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MarketOrder[]{
        new MarketOrder(orderID + 1, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision + 1, bid, charID, duration, escrow, issued, issuedBy, minVolume,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, !bid, charID, duration, escrow, issued, issuedBy, minVolume,
                        orderState, price,
                        orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID + 1, duration, escrow, issued, issuedBy, minVolume,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration + 1, escrow, issued, issuedBy, minVolume,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow.add(BigDecimal.ONE), issued, issuedBy,
                        minVolume,
                        orderState, price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued + 1, issuedBy, minVolume,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy + 1, minVolume,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume + 1,
                        orderState,
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume,
                        orderState + "1",
                        price, orderRange, typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price.add(BigDecimal.ONE), orderRange, typeID, volEntered, volRemaining, regionID, locationID,
                        isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange + "1", typeID, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange, typeID + 1, volEntered, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange, typeID, volEntered + 1, volRemaining, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange, typeID, volEntered, volRemaining + 1, regionID, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange, typeID, volEntered, volRemaining, regionID + 1, locationID, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange, typeID, volEntered, volRemaining, regionID, locationID + 1, isCorp),
        new MarketOrder(orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState,
                        price,
                        orderRange, typeID, volEntered, volRemaining, regionID, locationID, !isCorp)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MARKET_ORDERS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MarketOrder.get(account, time, orderID));
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - orders for a different account
    // - orders not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    MarketOrder existing;
    Map<Long, MarketOrder> listCheck = new HashMap<>();

    existing = new MarketOrder(
        orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID, existing);

    existing = new MarketOrder(
        orderID + 10, walletDivision, bid, charID, duration, escrow, issued + 10, issuedBy, minVolume, orderState,
        price,
        orderRange, typeID, volEntered,
        volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 10, existing);

    existing = new MarketOrder(
        orderID + 20, walletDivision, bid, charID, duration, escrow, issued + 20, issuedBy, minVolume, orderState,
        price,
        orderRange, typeID, volEntered,
        volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 20, existing);

    existing = new MarketOrder(
        orderID + 30, walletDivision, bid, charID, duration, escrow, issued + 30, issuedBy, minVolume, orderState,
        price,
        orderRange, typeID, volEntered,
        volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 30, existing);

    // Associated with different account
    existing = new MarketOrder(
        orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining,
        regionID, locationID, isCorp);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MarketOrder(
        orderID + 5, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MarketOrder(
        orderID + 3, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<MarketOrder> result = MarketOrder.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MarketOrder next : result) {
      long orderID = next.getOrderID();
      Assert.assertTrue(listCheck.containsKey(orderID));
      Assert.assertEquals(listCheck.get(orderID), next);
    }

    // Verify limited set returned
    result = MarketOrder.getAllForward(testAccount, 8888L, 2, issued - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(orderID), result.get(0));
    Assert.assertEquals(listCheck.get(orderID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = MarketOrder.getAllForward(testAccount, 8888L, 100, issued + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(orderID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(orderID + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - orders for a different account
    // - orders not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    MarketOrder existing;
    Map<Long, MarketOrder> listCheck = new HashMap<>();

    existing = new MarketOrder(
        orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID, existing);

    existing = new MarketOrder(
        orderID + 10, walletDivision, bid, charID, duration, escrow, issued + 10, issuedBy, minVolume, orderState,
        price,
        orderRange, typeID, volEntered,
        volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 10, existing);

    existing = new MarketOrder(
        orderID + 20, walletDivision, bid, charID, duration, escrow, issued + 20, issuedBy, minVolume, orderState,
        price,
        orderRange, typeID, volEntered,
        volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 20, existing);

    existing = new MarketOrder(
        orderID + 30, walletDivision, bid, charID, duration, escrow, issued + 30, issuedBy, minVolume, orderState,
        price,
        orderRange, typeID, volEntered,
        volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 30, existing);

    // Associated with different account
    existing = new MarketOrder(
        orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MarketOrder(
        orderID + 5, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MarketOrder(
        orderID + 3, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, orderState, price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<MarketOrder> result = MarketOrder.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MarketOrder next : result) {
      long orderID = next.getOrderID();
      Assert.assertTrue(listCheck.containsKey(orderID));
      Assert.assertEquals(listCheck.get(orderID), next);
    }

    // Verify limited set returned
    result = MarketOrder.getAllBackward(testAccount, 8888L, 2, issued + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(orderID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(orderID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = MarketOrder.getAllForward(testAccount, 8888L, 100, issued + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(orderID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(orderID + 30), result.get(1));

  }

  @Test
  public void testGetAllActive() throws Exception {
    // Should exclude:
    // - orders for a different account
    // - orders not live at the given time
    // - orders not active
    // - orders outside of duration bound
    // Need to test:
    // - max results limitation
    // - continuation ID
    MarketOrder existing;
    Map<Long, MarketOrder> listCheck = new HashMap<>();

    existing = new MarketOrder(
        orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, "open", price, orderRange,
        typeID,
        volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID, existing);

    existing = new MarketOrder(
        orderID + 10, walletDivision, bid, charID, duration, escrow, issued + 10, issuedBy, minVolume, "open", price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);

    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 10, existing);

    existing = new MarketOrder(
        orderID + 20, walletDivision, bid, charID, duration, escrow, issued + 20, issuedBy, minVolume, "open", price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 20, existing);

    existing = new MarketOrder(
        orderID + 30, walletDivision, bid, charID, duration, escrow, issued + 30, issuedBy, minVolume, "open", price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 30, existing);

    // Associated with different account
    existing = new MarketOrder(
        orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, "open", price, orderRange,
        typeID,
        volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Outside the duration bound
    existing = new MarketOrder(
        orderID + 7, walletDivision, bid, charID, duration, escrow, issued + 6 * 24 * 60 * 60 * 1000L, issuedBy,
        minVolume,
        "open", price, orderRange, typeID,
        volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // Non-zero order state
    existing = new MarketOrder(
        orderID + 7, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, "closed", price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MarketOrder(
        orderID + 5, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, "open", price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MarketOrder(
        orderID + 3, walletDivision, bid, charID, duration, escrow, issued, issuedBy, minVolume, "open", price,
        orderRange,
        typeID, volEntered, volRemaining, regionID, locationID, isCorp);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobs are returned
    List<MarketOrder> result = MarketOrder.getAllActive(testAccount, 8888L, 10, 0, issued, 5);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MarketOrder next : result) {
      long orderID = next.getOrderID();
      Assert.assertTrue(listCheck.containsKey(orderID));
      Assert.assertEquals(listCheck.get(orderID), next);
    }

    // Verify limited set returned
    result = MarketOrder.getAllActive(testAccount, 8888L, 2, issued - 1, issued, 5);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(orderID), result.get(0));
    Assert.assertEquals(listCheck.get(orderID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = MarketOrder.getAllActive(testAccount, 8888L, 100, issued + 10, issued, 5);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(orderID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(orderID + 30), result.get(1));

  }

}
