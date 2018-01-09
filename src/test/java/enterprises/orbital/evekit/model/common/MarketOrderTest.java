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
import enterprises.orbital.evekit.model.common.MarketOrder;

public class MarketOrderTest extends AbstractModelTester<MarketOrder> {

  final long                                   orderID      = TestBase.getRandomInt(100000000);
  final int                                    accountKey   = TestBase.getRandomInt(100000000);
  final boolean                                bid          = false;
  final long                                   charID       = TestBase.getRandomInt(100000000);
  final int                                    duration     = TestBase.getRandomInt(100000000);
  final BigDecimal                             escrow       = TestBase.getRandomBigDecimal(100000000);
  final long                                   issued       = TestBase.getRandomInt(100000000);
  final int                                    minVolume    = TestBase.getRandomInt(100000000);
  final int                                    orderState   = TestBase.getRandomInt(100000000);
  final BigDecimal                             price        = TestBase.getRandomBigDecimal(100000000);
  final int                                    orderRange   = TestBase.getRandomInt(100000000);
  final long                                   stationID    = TestBase.getRandomInt(100000000);
  final int                                    typeID       = TestBase.getRandomInt(100000000);
  final int                                    volEntered   = TestBase.getRandomInt(100000000);
  final int                                    volRemaining = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<MarketOrder> eol          = new ClassUnderTestConstructor<MarketOrder>() {

                                                              @Override
                                                              public MarketOrder getCUT() {
                                                                return new MarketOrder(
                                                                    orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price,
                                                                    orderRange, stationID, typeID, volEntered, volRemaining);
                                                              }

                                                            };

  final ClassUnderTestConstructor<MarketOrder> live         = new ClassUnderTestConstructor<MarketOrder>() {
                                                              @Override
                                                              public MarketOrder getCUT() {
                                                                return new MarketOrder(
                                                                    orderID, accountKey + 1, bid, charID, duration, escrow, issued, minVolume, orderState,
                                                                    price, orderRange, stationID, typeID, volEntered, volRemaining);
                                                              }

                                                            };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MarketOrder>() {

      @Override
      public MarketOrder[] getVariants() {
        return new MarketOrder[] {
            new MarketOrder(
                orderID + 1, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey + 1, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, !bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID + 1, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration + 1, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow.add(BigDecimal.ONE), issued, minVolume, orderState, price, orderRange, stationID, typeID,
                volEntered, volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued + 1, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume + 1, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState + 1, price, orderRange, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price.add(BigDecimal.ONE), orderRange, stationID, typeID,
                volEntered, volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange + 1, stationID, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID + 1, typeID, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID + 1, volEntered,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered + 1,
                volRemaining),
            new MarketOrder(
                orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
                volRemaining + 1),
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MARKET_ORDERS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MarketOrder>() {

      @Override
      public MarketOrder getModel(SynchronizedEveAccount account, long time) {
        return MarketOrder.get(account, time, orderID);
      }

    });
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
    Map<Long, MarketOrder> listCheck = new HashMap<Long, MarketOrder>();

    existing = new MarketOrder(
        orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID, existing);

    existing = new MarketOrder(
        orderID + 10, accountKey, bid, charID, duration, escrow, issued + 10, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
        volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 10, existing);

    existing = new MarketOrder(
        orderID + 20, accountKey, bid, charID, duration, escrow, issued + 20, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
        volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 20, existing);

    existing = new MarketOrder(
        orderID + 30, accountKey, bid, charID, duration, escrow, issued + 30, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
        volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 30, existing);

    // Associated with different account
    existing = new MarketOrder(
        orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MarketOrder(
        orderID + 5, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MarketOrder(
        orderID + 3, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
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
    Map<Long, MarketOrder> listCheck = new HashMap<Long, MarketOrder>();

    existing = new MarketOrder(
        orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID, existing);

    existing = new MarketOrder(
        orderID + 10, accountKey, bid, charID, duration, escrow, issued + 10, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
        volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 10, existing);

    existing = new MarketOrder(
        orderID + 20, accountKey, bid, charID, duration, escrow, issued + 20, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
        volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 20, existing);

    existing = new MarketOrder(
        orderID + 30, accountKey, bid, charID, duration, escrow, issued + 30, minVolume, orderState, price, orderRange, stationID, typeID, volEntered,
        volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 30, existing);

    // Associated with different account
    existing = new MarketOrder(
        orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MarketOrder(
        orderID + 5, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MarketOrder(
        orderID + 3, accountKey, bid, charID, duration, escrow, issued, minVolume, orderState, price, orderRange, stationID, typeID, volEntered, volRemaining);
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
    Map<Long, MarketOrder> listCheck = new HashMap<Long, MarketOrder>();

    existing = new MarketOrder(
        orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID, existing);

    existing = new MarketOrder(
        orderID + 10, accountKey, bid, charID, duration, escrow, issued + 10, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 10, existing);

    existing = new MarketOrder(
        orderID + 20, accountKey, bid, charID, duration, escrow, issued + 20, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 20, existing);

    existing = new MarketOrder(
        orderID + 30, accountKey, bid, charID, duration, escrow, issued + 30, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(orderID + 30, existing);

    // Associated with different account
    existing = new MarketOrder(
        orderID, accountKey, bid, charID, duration, escrow, issued, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Outside the duration bound
    existing = new MarketOrder(
        orderID + 7, accountKey, bid, charID, duration, escrow, issued + 6 * 24 * 60 * 60 * 1000L, minVolume, 0, price, orderRange, stationID, typeID,
        volEntered, volRemaining);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // Non-zero order state
    existing = new MarketOrder(
        orderID + 7, accountKey, bid, charID, duration, escrow, issued, minVolume, 1, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MarketOrder(
        orderID + 5, accountKey, bid, charID, duration, escrow, issued, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MarketOrder(
        orderID + 3, accountKey, bid, charID, duration, escrow, issued, minVolume, 0, price, orderRange, stationID, typeID, volEntered, volRemaining);
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
