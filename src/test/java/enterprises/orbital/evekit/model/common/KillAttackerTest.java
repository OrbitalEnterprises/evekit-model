package enterprises.orbital.evekit.model.common;

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

public class KillAttackerTest extends AbstractModelTester<KillAttacker> {

  private final int killID = TestBase.getRandomInt(100000000);
  private final int attackerCharacterID = TestBase.getRandomInt(100000000);
  private final int allianceID = TestBase.getRandomInt(100000000);
  private final int attackerCorporationID = TestBase.getRandomInt(100000000);
  private final int damageDone = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);
  private final float securityStatus = TestBase.getRandomFloat(10);
  private final int shipTypeID = TestBase.getRandomInt(100000000);
  private final int weaponTypeID = TestBase.getRandomInt(100000000);
  private final boolean finalBlow = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<KillAttacker> eol = () -> new KillAttacker(
      killID, attackerCharacterID, allianceID,
      attackerCorporationID, damageDone, factionID,
      securityStatus, shipTypeID, weaponTypeID, finalBlow);

  final ClassUnderTestConstructor<KillAttacker> live = () -> new KillAttacker(
      killID, attackerCharacterID, allianceID + 1,
      attackerCorporationID,
      damageDone, factionID, securityStatus, shipTypeID, weaponTypeID,
      finalBlow);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new KillAttacker[]{
        new KillAttacker(
            killID + 1, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
            factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID + 1, allianceID, attackerCorporationID, damageDone,
            factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID + 1, attackerCorporationID, damageDone,
            factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID + 1, damageDone,
            factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone + 1,
            factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
            factionID + 1, securityStatus, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
            factionID, securityStatus + 1.0F, shipTypeID, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
            factionID, securityStatus, shipTypeID + 1, weaponTypeID, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
            factionID, securityStatus, shipTypeID, weaponTypeID + 1, finalBlow),
        new KillAttacker(
            killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone,
            factionID, securityStatus, shipTypeID, weaponTypeID, !finalBlow)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> KillAttacker.get(account, time, killID, attackerCharacterID));
  }

  @Test
  public void testGetAllKillAttackers() throws Exception {
    // Should exclude:
    // - attackers for a different account
    // - attackers not live at the given time
    // - attackers for a different kill ID
    KillAttacker existing;
    Map<Integer, KillAttacker> listCheck = new HashMap<>();

    existing = new KillAttacker(
        killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone, factionID,
        securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(attackerCharacterID, existing);

    existing = new KillAttacker(
        killID, attackerCharacterID + 10, allianceID, attackerCorporationID, damageDone,
        factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(attackerCharacterID + 10, existing);

    existing = new KillAttacker(
        killID, attackerCharacterID + 20, allianceID, attackerCorporationID, damageDone,
        factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(attackerCharacterID + 20, existing);

    existing = new KillAttacker(
        killID, attackerCharacterID + 30, allianceID, attackerCorporationID, damageDone,
        factionID, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(attackerCharacterID + 30, existing);

    // Associated with different account
    existing = new KillAttacker(
        killID, attackerCharacterID, allianceID, attackerCorporationID, damageDone, factionID,
        securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with a different kill
    existing = new KillAttacker(
        killID + 1, attackerCharacterID, allianceID, attackerCorporationID, damageDone, factionID,
        securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new KillAttacker(
        killID, attackerCharacterID + 5, allianceID, attackerCorporationID, damageDone, factionID,
        securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new KillAttacker(
        killID, attackerCharacterID + 3, allianceID, attackerCorporationID, damageDone, factionID,
        securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<KillAttacker> result = CachedData.retrieveAll(8888L,
                                                       (contid, at) -> KillAttacker.accessQuery(testAccount, contid,
                                                                                                1000, false, at,
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (KillAttacker next : result) {
      int attackerCharacterID = next.getAttackerCharacterID();
      Assert.assertEquals(killID, next.getKillID());
      Assert.assertTrue(listCheck.containsKey(attackerCharacterID));
      Assert.assertEquals(listCheck.get(attackerCharacterID), next);
    }

  }

}
