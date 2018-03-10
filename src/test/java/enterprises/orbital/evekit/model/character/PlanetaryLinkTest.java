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

public class PlanetaryLinkTest extends AbstractModelTester<PlanetaryLink> {
  private final int planetID = TestBase.getRandomInt(100000000);
  private final long sourcePinID = TestBase.getRandomInt(100000000);
  private final long destinationPinID = TestBase.getRandomInt(100000000);
  private final int linkLevel = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<PlanetaryLink> eol = () -> new PlanetaryLink(planetID, sourcePinID, destinationPinID,
                                                                               linkLevel);

  final ClassUnderTestConstructor<PlanetaryLink> live = () -> new PlanetaryLink(planetID, sourcePinID, destinationPinID,
                                                                                linkLevel + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new PlanetaryLink[]{
        new PlanetaryLink(planetID + 1, sourcePinID, destinationPinID, linkLevel),
        new PlanetaryLink(planetID, sourcePinID + 1, destinationPinID, linkLevel),
        new PlanetaryLink(planetID, sourcePinID, destinationPinID + 1, linkLevel),
        new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live,
                       (account, time) -> PlanetaryLink.get(account, time, planetID, sourcePinID, destinationPinID));
  }

  @Test
  public void testGetAllPlanetaryLinks() throws Exception {
    // Should exclude:
    // - links for a different account
    // - links not live at the given time
    PlanetaryLink existing;
    Map<Integer, Map<Long, Map<Long, PlanetaryLink>>> listCheck = new HashMap<>();

    existing = new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID, new HashMap<>());
    listCheck.get(planetID)
             .put(sourcePinID, new HashMap<>());
    listCheck.get(planetID)
             .get(sourcePinID)
             .put(destinationPinID, existing);

    existing = new PlanetaryLink(planetID + 10, sourcePinID + 10, destinationPinID + 10, linkLevel + 10);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(planetID + 10, new HashMap<>());
    listCheck.get(planetID + 10)
             .put(sourcePinID + 10, new HashMap<>());
    listCheck.get(planetID + 10)
             .get(sourcePinID + 10)
             .put(destinationPinID + 10, existing);

    // Associated with different account
    existing = new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new PlanetaryLink(planetID + 3, sourcePinID + 3, destinationPinID + 3, linkLevel + 3);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new PlanetaryLink(planetID + 4, sourcePinID + 4, destinationPinID + 4, linkLevel + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<PlanetaryLink> result = CachedData.retrieveAll(8888L,
                                                        (contid, at) -> PlanetaryLink.accessQuery(testAccount, contid,
                                                                                                  1000, false, at,
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any(),
                                                                                                  AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (PlanetaryLink next : result) {
      int planetID = next.getPlanetID();
      long sourcePinID = next.getSourcePinID();
      long destPinID = next.getDestinationPinID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID)
                                 .containsKey(sourcePinID));
      Assert.assertTrue(listCheck.get(planetID)
                                 .get(sourcePinID)
                                 .containsKey(destPinID));
      Assert.assertEquals(listCheck.get(planetID)
                                   .get(sourcePinID)
                                   .get(destPinID), next);
    }

  }

}
