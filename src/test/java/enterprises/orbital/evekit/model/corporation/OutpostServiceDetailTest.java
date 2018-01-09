package enterprises.orbital.evekit.model.corporation;

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

public class OutpostServiceDetailTest extends AbstractModelTester<OutpostServiceDetail> {

  final long                                            stationID               = TestBase.getRandomInt(100000000);
  final String                                          serviceName             = "test service name";
  final long                                            ownerID                 = TestBase.getRandomInt(100000000);
  final double                                          minStanding             = TestBase.getRandomDouble(100000000);
  final BigDecimal                                      surchargePerBadStanding = TestBase.getRandomBigDecimal(100000000);
  final BigDecimal                                      discountPerGoodStanding = TestBase.getRandomBigDecimal(100000000);

  final ClassUnderTestConstructor<OutpostServiceDetail> eol                     = new ClassUnderTestConstructor<OutpostServiceDetail>() {

                                                                                  @Override
                                                                                  public OutpostServiceDetail getCUT() {
                                                                                    return new OutpostServiceDetail(
                                                                                        stationID, serviceName, ownerID, minStanding, surchargePerBadStanding,
                                                                                        discountPerGoodStanding);
                                                                                  }

                                                                                };

  final ClassUnderTestConstructor<OutpostServiceDetail> live                    = new ClassUnderTestConstructor<OutpostServiceDetail>() {
                                                                                  @Override
                                                                                  public OutpostServiceDetail getCUT() {
                                                                                    return new OutpostServiceDetail(
                                                                                        stationID, serviceName, ownerID + 1, minStanding,
                                                                                        surchargePerBadStanding, discountPerGoodStanding);
                                                                                  }

                                                                                };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<OutpostServiceDetail>() {

      @Override
      public OutpostServiceDetail[] getVariants() {
        return new OutpostServiceDetail[] {
            new OutpostServiceDetail(stationID + 1, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding),
            new OutpostServiceDetail(stationID, serviceName + " 1", ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding),
            new OutpostServiceDetail(stationID, serviceName, ownerID + 1, minStanding, surchargePerBadStanding, discountPerGoodStanding),
            new OutpostServiceDetail(stationID, serviceName, ownerID, minStanding + 1, surchargePerBadStanding, discountPerGoodStanding),
            new OutpostServiceDetail(stationID, serviceName, ownerID, minStanding, surchargePerBadStanding.add(BigDecimal.TEN), discountPerGoodStanding),
            new OutpostServiceDetail(stationID, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding.add(BigDecimal.TEN))
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_OUTPOST_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<OutpostServiceDetail>() {

      @Override
      public OutpostServiceDetail getModel(SynchronizedEveAccount account, long time) {
        return OutpostServiceDetail.get(account, time, stationID, serviceName);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - services for a different account
    // - services not live at the given time
    OutpostServiceDetail existing;
    Map<Long, OutpostServiceDetail> listCheck = new HashMap<Long, OutpostServiceDetail>();

    existing = new OutpostServiceDetail(stationID, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(stationID, existing);

    existing = new OutpostServiceDetail(stationID + 1, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(stationID + 1, existing);

    // Associated with different account
    existing = new OutpostServiceDetail(stationID + 2, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new OutpostServiceDetail(stationID + 3, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new OutpostServiceDetail(stationID + 4, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<OutpostServiceDetail> result = OutpostServiceDetail.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (OutpostServiceDetail next : result) {
      long stationID = next.getStationID();
      Assert.assertTrue(listCheck.containsKey(stationID));
      Assert.assertEquals(listCheck.get(stationID), next);
    }

  }

  @Test
  public void testGetAllByStationID() throws Exception {
    // Should exclude:
    // - services for a different account
    // - services not live at the given time
    // - services for a different station ID
    OutpostServiceDetail existing;
    Map<String, OutpostServiceDetail> listCheck = new HashMap<String, OutpostServiceDetail>();

    existing = new OutpostServiceDetail(stationID, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(serviceName, existing);

    existing = new OutpostServiceDetail(stationID, serviceName + " 1", ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(serviceName + " 1", existing);

    // Associated with different account
    existing = new OutpostServiceDetail(stationID, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different service
    existing = new OutpostServiceDetail(stationID + 1, serviceName, ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new OutpostServiceDetail(stationID, serviceName + " 2", ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new OutpostServiceDetail(stationID, serviceName + " 3", ownerID, minStanding, surchargePerBadStanding, discountPerGoodStanding);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<OutpostServiceDetail> result = OutpostServiceDetail.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (OutpostServiceDetail next : result) {
      String serviceName = next.getServiceName();
      Assert.assertTrue(listCheck.containsKey(serviceName));
      Assert.assertEquals(listCheck.get(serviceName), next);
    }

  }

}
