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

public class FacilityTest extends AbstractModelTester<Facility> {

  private final long facilityID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int solarSystemID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Facility> eol = () -> new Facility(
      facilityID, typeID, solarSystemID);

  final ClassUnderTestConstructor<Facility> live = () -> new Facility(
      facilityID, typeID + 1, solarSystemID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Facility[]{
        new Facility(facilityID + 1, typeID, solarSystemID),
        new Facility(facilityID, typeID + 1, solarSystemID),
        new Facility(facilityID, typeID, solarSystemID + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Facility.get(account, time, facilityID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - facilities for a different account
    // - facilities not live at the given time
    Facility existing;
    Map<Long, Facility> listCheck = new HashMap<>();

    existing = new Facility(facilityID, typeID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(facilityID, existing);

    existing = new Facility(facilityID + 1, typeID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(facilityID + 1, existing);

    // Associated with different account
    existing = new Facility(facilityID + 2, typeID, solarSystemID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Facility(facilityID + 3, typeID, solarSystemID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Facility(facilityID + 4, typeID, solarSystemID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Facility> result = CachedData.retrieveAll(8888L,
                                                   (contid, at) -> Facility.accessQuery(testAccount, contid, 1000,
                                                                                        false, at,
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Facility next : result) {
      long facilityID = next.getFacilityID();
      Assert.assertTrue(listCheck.containsKey(facilityID));
      Assert.assertEquals(listCheck.get(facilityID), next);
    }

  }

}
