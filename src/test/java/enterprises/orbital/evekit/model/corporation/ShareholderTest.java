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

public class ShareholderTest extends AbstractModelTester<Shareholder> {

  final long                                   shareholderID              = TestBase.getRandomInt(100000000);
  final boolean                                isCorporation              = true;
  final long                                   shareholderCorporationID   = TestBase.getRandomInt(100000000);
  final String                                 shareholderCorporationName = "test shareholder corporation name";
  final String                                 shareholderName            = "test shareholder name";
  final int                                    shares                     = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Shareholder> eol                        = new ClassUnderTestConstructor<Shareholder>() {

                                                                            @Override
                                                                            public Shareholder getCUT() {
                                                                              return new Shareholder(
                                                                                  shareholderID, isCorporation, shareholderCorporationID,
                                                                                  shareholderCorporationName, shareholderName, shares);
                                                                            }

                                                                          };

  final ClassUnderTestConstructor<Shareholder> live                       = new ClassUnderTestConstructor<Shareholder>() {
                                                                            @Override
                                                                            public Shareholder getCUT() {
                                                                              return new Shareholder(
                                                                                  shareholderID, isCorporation, shareholderCorporationID + 1,
                                                                                  shareholderCorporationName, shareholderName, shares);
                                                                            }

                                                                          };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Shareholder>() {

      @Override
      public Shareholder[] getVariants() {
        return new Shareholder[] {
            new Shareholder(shareholderID + 1, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares),
            new Shareholder(shareholderID, !isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares),
            new Shareholder(shareholderID, isCorporation, shareholderCorporationID + 1, shareholderCorporationName, shareholderName, shares),
            new Shareholder(shareholderID, isCorporation, shareholderCorporationID, shareholderCorporationName + " 1", shareholderName, shares),
            new Shareholder(shareholderID, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName + " 1", shares),
            new Shareholder(shareholderID, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_SHAREHOLDERS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Shareholder>() {

      @Override
      public Shareholder getModel(SynchronizedEveAccount account, long time) {
        return Shareholder.get(account, time, shareholderID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - shareholders for a different account
    // - shareholders not live at the given time
    Shareholder existing;
    Map<Long, Shareholder> listCheck = new HashMap<Long, Shareholder>();

    existing = new Shareholder(shareholderID, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(shareholderID, existing);

    existing = new Shareholder(shareholderID + 1, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(shareholderID + 1, existing);

    // Associated with different account
    existing = new Shareholder(shareholderID + 2, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new Shareholder(shareholderID + 3, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new Shareholder(shareholderID + 4, isCorporation, shareholderCorporationID, shareholderCorporationName, shareholderName, shares);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<Shareholder> result = Shareholder.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Shareholder next : result) {
      long shareholderID = next.getShareholderID();
      Assert.assertTrue(listCheck.containsKey(shareholderID));
      Assert.assertEquals(listCheck.get(shareholderID), next);
    }

  }

}
