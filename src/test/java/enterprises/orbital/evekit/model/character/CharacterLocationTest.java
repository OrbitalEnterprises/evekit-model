package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

public class CharacterLocationTest extends AbstractModelTester<CharacterLocation> {
  private final int solarSystemID = TestBase.getRandomInt(100000000);
  private final int stationID = TestBase.getRandomInt(100000000);
  private final long structureID = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<CharacterLocation> eol = () -> new CharacterLocation(
      solarSystemID, stationID, structureID);
  private final ClassUnderTestConstructor<CharacterLocation> live = () -> new CharacterLocation(
      solarSystemID + 1, stationID, structureID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterLocation[]{
        new CharacterLocation(solarSystemID + 1, stationID, structureID),
        new CharacterLocation(solarSystemID, stationID + 1, structureID),
        new CharacterLocation(solarSystemID, stationID, structureID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_LOCATIONS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CharacterLocation::get);
  }

  @Test
  public void testGetLocation() throws Exception {
    // Should exclude:
    // - location for a different account
    // - location not live at the given time
    CharacterLocation existing, keyed;

    keyed = new CharacterLocation(solarSystemID, stationID, structureID);
    keyed.setup(testAccount, 7777L);
    keyed = CachedData.update(keyed);

    // Associated with different account
    existing = new CharacterLocation(solarSystemID + 1, stationID, structureID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterLocation(solarSystemID + 3, stationID, structureID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterLocation(solarSystemID + 4, stationID, structureID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    CharacterLocation result = CharacterLocation.get(testAccount, 8888L);
    Assert.assertEquals(keyed, result);
  }

}
