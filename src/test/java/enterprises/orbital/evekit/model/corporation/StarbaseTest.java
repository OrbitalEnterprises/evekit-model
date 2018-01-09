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

public class StarbaseTest extends AbstractModelTester<Starbase> {

  final long                                itemID          = TestBase.getRandomInt(100000000);
  final long                                locationID      = TestBase.getRandomInt(100000000);
  final int                                 moonID          = TestBase.getRandomInt(100000000);
  final long                                onlineTimestamp = TestBase.getRandomInt(100000000);
  final int                                 state           = TestBase.getRandomInt(100000000);
  final long                                stateTimestamp  = TestBase.getRandomInt(100000000);
  final int                                 typeID          = TestBase.getRandomInt(100000000);
  final long                                standingOwnerID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Starbase> eol             = new ClassUnderTestConstructor<Starbase>() {

                                                              @Override
                                                              public Starbase getCUT() {
                                                                return new Starbase(
                                                                    itemID, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID,
                                                                    standingOwnerID);
                                                              }

                                                            };

  final ClassUnderTestConstructor<Starbase> live            = new ClassUnderTestConstructor<Starbase>() {
                                                              @Override
                                                              public Starbase getCUT() {
                                                                return new Starbase(
                                                                    itemID, locationID + 1, moonID, onlineTimestamp, state, stateTimestamp, typeID,
                                                                    standingOwnerID);
                                                              }

                                                            };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Starbase>() {

      @Override
      public Starbase[] getVariants() {
        return new Starbase[] {
            new Starbase(itemID + 1, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID),
            new Starbase(itemID, locationID + 1, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID),
            new Starbase(itemID, locationID, moonID + 1, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID),
            new Starbase(itemID, locationID, moonID, onlineTimestamp + 1, state, stateTimestamp, typeID, standingOwnerID),
            new Starbase(itemID, locationID, moonID, onlineTimestamp, state + 1, stateTimestamp, typeID, standingOwnerID),
            new Starbase(itemID, locationID, moonID, onlineTimestamp, state, stateTimestamp + 1, typeID, standingOwnerID),
            new Starbase(itemID, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID + 1, standingOwnerID),
            new Starbase(itemID, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Starbase>() {

      @Override
      public Starbase getModel(
                               SynchronizedEveAccount account,
                               long time) {
        return Starbase.get(account, time, itemID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - bases for a different account
    // - bases not live at the given time
    Starbase existing;
    Map<Long, Starbase> listCheck = new HashMap<Long, Starbase>();

    existing = new Starbase(itemID, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID, existing);

    existing = new Starbase(itemID + 1, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 1, existing);

    // Associated with different account
    existing = new Starbase(itemID + 2, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Starbase(itemID + 3, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Starbase(itemID + 4, locationID, moonID, onlineTimestamp, state, stateTimestamp, typeID, standingOwnerID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Starbase> result = Starbase.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Starbase next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

  }

}
