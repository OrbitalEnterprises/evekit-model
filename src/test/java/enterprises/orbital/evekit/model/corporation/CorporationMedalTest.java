package enterprises.orbital.evekit.model.corporation;

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

public class CorporationMedalTest extends AbstractModelTester<CorporationMedal> {

  final int                                         medalID     = TestBase.getRandomInt(100000000);
  final String                                      description = "test description";
  final String                                      title       = "test title";
  final long                                        created     = TestBase.getRandomInt(100000000);
  final long                                        creatorID   = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CorporationMedal> eol         = new ClassUnderTestConstructor<CorporationMedal>() {

                                                                  @Override
                                                                  public CorporationMedal getCUT() {
                                                                    return new CorporationMedal(medalID, description, title, created, creatorID);
                                                                  }

                                                                };

  final ClassUnderTestConstructor<CorporationMedal> live        = new ClassUnderTestConstructor<CorporationMedal>() {
                                                                  @Override
                                                                  public CorporationMedal getCUT() {
                                                                    return new CorporationMedal(medalID, description, title, created + 1, creatorID);
                                                                  }

                                                                };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CorporationMedal>() {

      @Override
      public CorporationMedal[] getVariants() {
        return new CorporationMedal[] {
            new CorporationMedal(medalID + 1, description, title, created, creatorID),
            new CorporationMedal(medalID, description + " 1", title, created, creatorID),
            new CorporationMedal(medalID, description, title + " 1", created, creatorID),
            new CorporationMedal(medalID, description, title, created + 1, creatorID), new CorporationMedal(medalID, description, title, created, creatorID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_MEDALS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CorporationMedal>() {

      @Override
      public CorporationMedal getModel(SynchronizedEveAccount account, long time) {
        return CorporationMedal.get(account, time, medalID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - medals for a different account
    // - medals not live at the given time
    CorporationMedal existing;
    Map<Integer, CorporationMedal> listCheck = new HashMap<Integer, CorporationMedal>();

    existing = new CorporationMedal(medalID, description, title, created, creatorID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(medalID, existing);

    existing = new CorporationMedal(medalID + 1, description, title, created, creatorID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(medalID + 1, existing);

    // Associated with different account
    existing = new CorporationMedal(medalID + 2, description, title, created, creatorID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CorporationMedal(medalID + 3, description, title, created, creatorID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CorporationMedal(medalID + 4, description, title, created, creatorID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CorporationMedal> result = CorporationMedal.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CorporationMedal next : result) {
      int medalID = next.getMedalID();
      Assert.assertTrue(listCheck.containsKey(medalID));
      Assert.assertEquals(listCheck.get(medalID), next);
    }

  }

}
