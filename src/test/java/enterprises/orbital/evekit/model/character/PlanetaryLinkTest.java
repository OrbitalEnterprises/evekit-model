package enterprises.orbital.evekit.model.character;

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
import enterprises.orbital.evekit.model.character.PlanetaryLink;

public class PlanetaryLinkTest extends AbstractModelTester<PlanetaryLink> {
  final long                                     planetID         = TestBase.getRandomInt(100000000);
  final long                                     sourcePinID      = TestBase.getRandomInt(100000000);
  final long                                     destinationPinID = TestBase.getRandomInt(100000000);
  final int                                      linkLevel        = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<PlanetaryLink> eol              = new ClassUnderTestConstructor<PlanetaryLink>() {

                                                                    @Override
                                                                    public PlanetaryLink getCUT() {
                                                                      return new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
                                                                    }

                                                                  };

  final ClassUnderTestConstructor<PlanetaryLink> live             = new ClassUnderTestConstructor<PlanetaryLink>() {
                                                                    @Override
                                                                    public PlanetaryLink getCUT() {
                                                                      return new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel + 1);
                                                                    }

                                                                  };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<PlanetaryLink>() {

      @Override
      public PlanetaryLink[] getVariants() {
        return new PlanetaryLink[] {
            new PlanetaryLink(planetID + 1, sourcePinID, destinationPinID, linkLevel),
            new PlanetaryLink(planetID, sourcePinID + 1, destinationPinID, linkLevel),
            new PlanetaryLink(planetID, sourcePinID, destinationPinID + 1, linkLevel), new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<PlanetaryLink>() {

      @Override
      public PlanetaryLink getModel(SynchronizedEveAccount account, long time) {
        return PlanetaryLink.get(account, time, planetID, sourcePinID, destinationPinID);
      }

    });
  }

  @Test
  public void testGetAllPlanetaryLinks() throws Exception {
    // Should exclude:
    // - links for a different account
    // - links not live at the given time
    PlanetaryLink existing;
    Map<Long, Map<Long, Map<Long, PlanetaryLink>>> listCheck = new HashMap<Long, Map<Long, Map<Long, PlanetaryLink>>>();

    existing = new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID, new HashMap<Long, Map<Long, PlanetaryLink>>());
    listCheck.get(planetID).put(sourcePinID, new HashMap<Long, PlanetaryLink>());
    listCheck.get(planetID).get(sourcePinID).put(destinationPinID, existing);

    existing = new PlanetaryLink(planetID + 10, sourcePinID + 10, destinationPinID + 10, linkLevel + 10);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID + 10, new HashMap<Long, Map<Long, PlanetaryLink>>());
    listCheck.get(planetID + 10).put(sourcePinID + 10, new HashMap<Long, PlanetaryLink>());
    listCheck.get(planetID + 10).get(sourcePinID + 10).put(destinationPinID + 10, existing);

    // Associated with different account
    existing = new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new PlanetaryLink(planetID + 3, sourcePinID + 3, destinationPinID + 3, linkLevel + 3);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new PlanetaryLink(planetID + 4, sourcePinID + 4, destinationPinID + 4, linkLevel + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<PlanetaryLink> result = PlanetaryLink.getAllPlanetaryLinks(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (PlanetaryLink next : result) {
      long planetID = next.getPlanetID();
      long sourcePinID = next.getSourcePinID();
      long destPinID = next.getDestinationPinID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID).containsKey(sourcePinID));
      Assert.assertTrue(listCheck.get(planetID).get(sourcePinID).containsKey(destPinID));
      Assert.assertEquals(listCheck.get(planetID).get(sourcePinID).get(destPinID), next);
    }

  }

  @Test
  public void testGetAllPlanetaryLinksByPlanet() throws Exception {
    // Should exclude:
    // - links for a different account
    // - links not live at the given time
    // - links for a different planet
    PlanetaryLink existing;
    Map<Long, Map<Long, Map<Long, PlanetaryLink>>> listCheck = new HashMap<Long, Map<Long, Map<Long, PlanetaryLink>>>();

    existing = new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(planetID, new HashMap<Long, Map<Long, PlanetaryLink>>());
    listCheck.get(planetID).put(sourcePinID, new HashMap<Long, PlanetaryLink>());
    listCheck.get(planetID).get(sourcePinID).put(destinationPinID, existing);

    existing = new PlanetaryLink(planetID, sourcePinID + 10, destinationPinID + 10, linkLevel + 10);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(planetID).put(sourcePinID + 10, new HashMap<Long, PlanetaryLink>());
    listCheck.get(planetID).get(sourcePinID + 10).put(destinationPinID + 10, existing);

    // Associated with different account
    existing = new PlanetaryLink(planetID, sourcePinID, destinationPinID, linkLevel);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different planet
    existing = new PlanetaryLink(planetID + 10, sourcePinID + 10, destinationPinID + 10, linkLevel + 10);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new PlanetaryLink(planetID + 3, sourcePinID + 3, destinationPinID + 3, linkLevel + 3);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new PlanetaryLink(planetID + 4, sourcePinID + 4, destinationPinID + 4, linkLevel + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<PlanetaryLink> result = PlanetaryLink.getAllPlanetaryLinksByPlanet(testAccount, 8888L, planetID);
    Assert.assertEquals(listCheck.get(planetID).size(), result.size());
    for (PlanetaryLink next : result) {
      long planetID = next.getPlanetID();
      long sourcePinID = next.getSourcePinID();
      long destPinID = next.getDestinationPinID();
      Assert.assertTrue(listCheck.containsKey(planetID));
      Assert.assertTrue(listCheck.get(planetID).containsKey(sourcePinID));
      Assert.assertTrue(listCheck.get(planetID).get(sourcePinID).containsKey(destPinID));
      Assert.assertEquals(listCheck.get(planetID).get(sourcePinID).get(destPinID), next);
    }

  }

}
