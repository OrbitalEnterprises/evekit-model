package enterprises.orbital.evekit.model.character;

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

public class FittingTest extends AbstractModelTester<Fitting> {
  private final int fittingID = TestBase.getRandomInt(100000000);
  private final String name = "test display name";
  private final String description = "test key value";
  private final int shipTypeID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Fitting> eol = () -> new Fitting(
      fittingID, name, description, shipTypeID);

  final ClassUnderTestConstructor<Fitting> live = () -> new Fitting(
      fittingID, name + "1", description, shipTypeID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Fitting[]{
        new Fitting(fittingID + 1, name, description, shipTypeID),
        new Fitting(fittingID, name + "1", description, shipTypeID),
        new Fitting(fittingID, name, description + "1", shipTypeID),
        new Fitting(fittingID, name, description, shipTypeID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_FITTINGS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Fitting.get(account, time, fittingID));
  }

  @Test
  public void testGetAllFittings() throws Exception {
    // Should exclude:
    // - fittings for a different account
    // - fittings not live at the given time
    Fitting existing;
    Map<Integer, Fitting> listCheck = new HashMap<>();

    existing = new Fitting(fittingID, name, description, shipTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fittingID, existing);

    existing = new Fitting(fittingID + 10, name, description, shipTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fittingID + 10, existing);

    existing = new Fitting(fittingID + 20, name, description, shipTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(fittingID + 20, existing);

    // Associated with different account
    existing = new Fitting(fittingID, name, description, shipTypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Fitting(fittingID + 5, name, description, shipTypeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Fitting(fittingID + 3, name, description, shipTypeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<Fitting> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> Fitting.accessQuery(testAccount, contid, 1000,
                                                                                              false, at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (Fitting next : result) {
      int fittingID = next.getFittingID();
      Assert.assertTrue(listCheck.containsKey(fittingID));
      Assert.assertEquals(listCheck.get(fittingID), next);
    }
  }

}
