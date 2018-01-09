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
import enterprises.orbital.evekit.model.character.Implant;

public class ImplantTest extends AbstractModelTester<Implant> {
  final int                                typeID   = TestBase.getRandomInt(100000000);
  final String                             typeName = "test implant";

  final ClassUnderTestConstructor<Implant> eol      = new ClassUnderTestConstructor<Implant>() {

                                                      @Override
                                                      public Implant getCUT() {
                                                        return new Implant(typeID, typeName);
                                                      }

                                                    };

  final ClassUnderTestConstructor<Implant> live     = new ClassUnderTestConstructor<Implant>() {
                                                      @Override
                                                      public Implant getCUT() {
                                                        return new Implant(typeID, typeName + " 2");
                                                      }

                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Implant>() {

      @Override
      public Implant[] getVariants() {
        return new Implant[] {
            new Implant(typeID + 1, typeName), new Implant(typeID, typeName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Implant>() {

      @Override
      public Implant getModel(SynchronizedEveAccount account, long time) {
        return Implant.get(account, time, typeID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - implants for a different account
    // - implants not live at the given time
    Implant existing;
    Map<Integer, Implant> listCheck = new HashMap<Integer, Implant>();

    existing = new Implant(typeID, typeName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID, existing);

    existing = new Implant(typeID + 10, typeName + " 1");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 10, existing);

    // Associated with different account
    existing = new Implant(typeID, typeName);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Implant(typeID + 3, typeName + " 3");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Implant(typeID + 4, typeName + " 4");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Implant> result = Implant.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (Implant next : result) {
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(typeID));
      Assert.assertEquals(listCheck.get(typeID), next);
    }

  }

}
