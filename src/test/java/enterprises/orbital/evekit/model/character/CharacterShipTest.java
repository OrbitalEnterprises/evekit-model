package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

public class CharacterShipTest extends AbstractModelTester<CharacterShip> {
  private final int shipTypeID = TestBase.getRandomInt(100000000);
  private final long shipItemID = TestBase.getRandomInt(100000000);
  private final String shipName = TestBase.getRandomText(50);

  private final ClassUnderTestConstructor<CharacterShip> eol = () -> new CharacterShip(
      shipTypeID, shipItemID, shipName);
  private final ClassUnderTestConstructor<CharacterShip> live = () -> new CharacterShip(
      shipTypeID + 1, shipItemID, shipName);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterShip[]{
        new CharacterShip(shipTypeID + 1, shipItemID, shipName),
        new CharacterShip(shipTypeID, shipItemID + 1, shipName),
        new CharacterShip(shipTypeID, shipItemID, shipName + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_LOCATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CharacterShip::get);
  }

  @Test
  public void testGetLocation() throws Exception {
    // Should exclude:
    // - ship for a different account
    // - ship not live at the given time
    CharacterShip existing, keyed;

    keyed = new CharacterShip(shipTypeID, shipItemID, shipName);
    keyed.setup(testAccount, 7777L);
    keyed = CachedData.update(keyed);

    // Associated with different account
    existing = new CharacterShip(shipTypeID + 1, shipItemID, shipName);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterShip(shipTypeID + 3, shipItemID, shipName);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterShip(shipTypeID + 4, shipItemID, shipName);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    CharacterShip result = CharacterShip.get(testAccount, 8888L);
    Assert.assertEquals(keyed, result);
  }

}
