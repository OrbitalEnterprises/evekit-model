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

public class ImplantTest extends AbstractModelTester<Implant> {
  private final int typeID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Implant> eol = () -> new Implant(typeID);

  final ClassUnderTestConstructor<Implant> live = () -> new Implant(typeID);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new Implant[]{
        new Implant(typeID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Implant.get(account, time, typeID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - implants for a different account
    // - implants not live at the given time
    Implant existing;
    Map<Integer, Implant> listCheck = new HashMap<>();

    existing = new Implant(typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID, existing);

    existing = new Implant(typeID + 10);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 10, existing);

    // Associated with different account
    existing = new Implant(typeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Implant(typeID + 3);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Implant(typeID + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Implant> result = CachedData.retrieveAll(8888L,
                                                  (contid, at) -> Implant.accessQuery(testAccount, contid, 1000, false,
                                                                                      at, AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Implant next : result) {
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(typeID));
      Assert.assertEquals(listCheck.get(typeID), next);
    }

  }

}
