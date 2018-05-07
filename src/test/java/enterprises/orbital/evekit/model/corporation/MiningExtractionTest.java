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

public class MiningExtractionTest extends AbstractModelTester<MiningExtraction> {
  private final int moonID = TestBase.getRandomInt();
  private final long structureID = TestBase.getRandomLong();
  private final long extractionStartTime = TestBase.getRandomLong();
  private final long chunkArrivalTime = TestBase.getRandomLong();
  private final long naturalDecayTime = TestBase.getRandomLong();

  final ClassUnderTestConstructor<MiningExtraction> eol = () -> new MiningExtraction(
      moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime);

  final ClassUnderTestConstructor<MiningExtraction> live = () -> new MiningExtraction(
      moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MiningExtraction[]{
        new MiningExtraction(moonID + 1, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime),
        new MiningExtraction(moonID, structureID + 1, extractionStartTime, chunkArrivalTime, naturalDecayTime),
        new MiningExtraction(moonID, structureID, extractionStartTime + 1, chunkArrivalTime, naturalDecayTime),
        new MiningExtraction(moonID, structureID, extractionStartTime, chunkArrivalTime + 1, naturalDecayTime),
        new MiningExtraction(moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MiningExtraction.get(account, time, moonID, structureID,
                                                                          extractionStartTime));
  }

  @Test
  public void testGetAllMiningExtractions() throws Exception {
    // Should exclude:
    // - mining extractions for a different account
    // - mining extractions not live at the given time
    MiningExtraction existing;
    Map<Triple<Integer, Long, Long>, MiningExtraction> listCheck = new HashMap<>();

    existing = new MiningExtraction(moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(moonID, structureID, extractionStartTime), existing);

    existing = new MiningExtraction(moonID, structureID, extractionStartTime + 10, chunkArrivalTime, naturalDecayTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(moonID, structureID, extractionStartTime + 10), existing);

    existing = new MiningExtraction(moonID, structureID, extractionStartTime + 20, chunkArrivalTime, naturalDecayTime);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(Triple.of(moonID, structureID, extractionStartTime + 20), existing);

    // Associated with different account
    existing = new MiningExtraction(moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MiningExtraction(moonID, structureID, extractionStartTime + 5, chunkArrivalTime, naturalDecayTime);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MiningExtraction(moonID, structureID, extractionStartTime + 3, chunkArrivalTime, naturalDecayTime);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<MiningExtraction> result = CachedData.retrieveAll(8888L,
                                                           (contid, at) -> MiningExtraction.accessQuery(testAccount,
                                                                                                        contid,
                                                                                                        1000,
                                                                                                        false, at,
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (MiningExtraction next : result) {
      Triple<Integer, Long, Long> key = Triple.of(next.getMoonID(), next.getStructureID(),
                                                  next.getExtractionStartTime());
      Assert.assertTrue(listCheck.containsKey(key));
      Assert.assertEquals(listCheck.get(key), next);
    }
  }

}
