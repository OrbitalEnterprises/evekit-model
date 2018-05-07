package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiningObservationTest extends AbstractModelTester<MiningObservation> {
  private final long observerID = TestBase.getRandomLong();
  private final int characterID = TestBase.getRandomInt();
  private final int typeID = TestBase.getRandomInt();
  private final int recordedCorporationID = TestBase.getRandomInt();
  private final long quantity = TestBase.getRandomLong();
  private final long lastUpdated = TestBase.getRandomLong();

  final ClassUnderTestConstructor<MiningObservation> eol = () -> new MiningObservation(
      observerID, characterID, typeID, recordedCorporationID, quantity, lastUpdated);

  final ClassUnderTestConstructor<MiningObservation> live = () -> new MiningObservation(
      observerID, characterID, typeID, recordedCorporationID, quantity + 1, lastUpdated);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MiningObservation[]{
        new MiningObservation(observerID + 1, characterID, typeID, recordedCorporationID, quantity, lastUpdated),
        new MiningObservation(observerID, characterID + 1, typeID, recordedCorporationID, quantity, lastUpdated),
        new MiningObservation(observerID, characterID, typeID + 1, recordedCorporationID, quantity, lastUpdated),
        new MiningObservation(observerID, characterID, typeID, recordedCorporationID + 1, quantity, lastUpdated),
        new MiningObservation(observerID, characterID, typeID, recordedCorporationID, quantity + 1, lastUpdated),
        new MiningObservation(observerID, characterID, typeID, recordedCorporationID, quantity, lastUpdated + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MiningObservation.get(account, time, observerID, characterID,
                                                                           typeID));
  }

  @Test
  public void testGetAllMiningObservations() throws Exception {
    // Should exclude:
    // - mining observations for a different account
    // - mining observations not live at the given time
    MiningObservation existing;
    Map<Triple<Long, Integer, Integer>, MiningObservation> listCheck = new HashMap<>();

    existing = new MiningObservation(observerID, characterID, typeID, recordedCorporationID, quantity, lastUpdated);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(observerID, characterID, typeID), existing);

    existing = new MiningObservation(observerID, characterID, typeID + 10, recordedCorporationID, quantity,
                                     lastUpdated);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(observerID, characterID, typeID + 10), existing);

    existing = new MiningObservation(observerID, characterID, typeID + 20, recordedCorporationID, quantity,
                                     lastUpdated);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(observerID, characterID, typeID + 20), existing);

    // Associated with different account
    existing = new MiningObservation(observerID, characterID, typeID, recordedCorporationID, quantity, lastUpdated);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MiningObservation(observerID, characterID, typeID + 5, recordedCorporationID, quantity, lastUpdated);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MiningObservation(observerID, characterID, typeID + 3, recordedCorporationID, quantity, lastUpdated);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<MiningObservation> result = CachedData.retrieveAll(8888L,
                                                            (contid, at) -> MiningObservation.accessQuery(testAccount,
                                                                                                          contid,
                                                                                                          1000,
                                                                                                          false, at,
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (MiningObservation next : result) {
      Triple<Long, Integer, Integer> key = Triple.of(next.getObserverID(), next.getCharacterID(),
                                                     next.getTypeID());
      Assert.assertTrue(listCheck.containsKey(key));
      Assert.assertEquals(listCheck.get(key), next);
    }
  }

}
