package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class KillVictimTest extends AbstractModelTester<KillVictim> {

  private final int killID = TestBase.getRandomInt(100000000);
  private final int allianceID = TestBase.getRandomInt(100000000);
  private final int killCharacterID = TestBase.getRandomInt(100000000);
  private final int killCorporationID = TestBase.getRandomInt(100000000);
  private final int damageTaken = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);
  private final int shipTypeID = TestBase.getRandomInt(100000000);
  private final double x = TestBase.getRandomDouble(100000000);
  private final double y = TestBase.getRandomDouble(100000000);
  private final double z = TestBase.getRandomDouble(100000000);

  final ClassUnderTestConstructor<KillVictim> eol = () -> new KillVictim(
      killID, allianceID, killCharacterID,
      killCorporationID, damageTaken, factionID,
      shipTypeID, x, y, z);

  final ClassUnderTestConstructor<KillVictim> live = () -> new KillVictim(
      killID, allianceID + 1, killCharacterID,
      killCorporationID, damageTaken, factionID,
      shipTypeID, x, y, z);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new KillVictim[]{
        new KillVictim(
            killID + 1, allianceID, killCharacterID, killCorporationID, damageTaken, factionID,
            shipTypeID, x, y, z),
        new KillVictim(
            killID, allianceID + 1, killCharacterID, killCorporationID, damageTaken, factionID,
            shipTypeID, x, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID + 1, killCorporationID, damageTaken, factionID,
            shipTypeID, x, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID + 1, damageTaken, factionID,
            shipTypeID, x, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID, damageTaken + 1, factionID,
            shipTypeID, x, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID, damageTaken, factionID + 1,
            shipTypeID, x, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID, damageTaken, factionID,
            shipTypeID + 1, x, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID, damageTaken, factionID,
            shipTypeID, x + 1, y, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID, damageTaken, factionID,
            shipTypeID, x, y + 1, z),
        new KillVictim(
            killID, allianceID, killCharacterID, killCorporationID, damageTaken, factionID,
            shipTypeID, x, y, z + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> KillVictim.get(account, time, killID));
  }

}
