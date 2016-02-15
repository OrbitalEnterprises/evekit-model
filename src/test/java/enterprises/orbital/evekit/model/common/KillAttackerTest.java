package enterprises.orbital.evekit.model.common;

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
import enterprises.orbital.evekit.model.common.KillAttacker;

public class KillAttackerTest extends AbstractModelTester<KillAttacker> {

  final long                                    killID                  = TestBase.getRandomInt(100000000);
  final long                                    attackerCharacterID     = TestBase.getRandomInt(100000000);
  final long                                    allianceID              = TestBase.getRandomInt(100000000);
  final String                                  allianceName            = "test alliance name";
  final String                                  attackerCharacterName   = "test attacker character name";
  final long                                    attackerCorporationID   = TestBase.getRandomInt(100000000);
  final String                                  attackerCorporationName = "teset attacker corporation name";
  final int                                     damageDone              = TestBase.getRandomInt(100000000);
  final int                                     factionID               = TestBase.getRandomInt(100000000);
  final String                                  factionName             = "test faction name";
  final double                                  securityStatus          = TestBase.getRandomDouble(100000000);
  final int                                     shipTypeID              = TestBase.getRandomInt(100000000);
  final int                                     weaponTypeID            = TestBase.getRandomInt(100000000);
  final boolean                                 finalBlow               = true;

  final ClassUnderTestConstructor<KillAttacker> eol                     = new ClassUnderTestConstructor<KillAttacker>() {

                                                                          @Override
                                                                          public KillAttacker getCUT() {
                                                                            return new KillAttacker(
                                                                                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName,
                                                                                attackerCorporationID, attackerCorporationName, damageDone, factionID,
                                                                                factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
                                                                          }

                                                                        };

  final ClassUnderTestConstructor<KillAttacker> live                    = new ClassUnderTestConstructor<KillAttacker>() {
                                                                          @Override
                                                                          public KillAttacker getCUT() {
                                                                            return new KillAttacker(
                                                                                killID, attackerCharacterID, allianceID + 1, allianceName,
                                                                                attackerCharacterName, attackerCorporationID, attackerCorporationName,
                                                                                damageDone, factionID, factionName, securityStatus, shipTypeID, weaponTypeID,
                                                                                finalBlow);
                                                                          }

                                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<KillAttacker>() {

      @Override
      public KillAttacker[] getVariants() {
        return new KillAttacker[] {
            new KillAttacker(
                killID + 1, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID + 1, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID + 1, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName + " 1", attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName + " 1", attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID + 1, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName + " 1", damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone + 1,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID + 1, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName + " 1", securityStatus, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus + 1, shipTypeID, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID + 1, weaponTypeID, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID + 1, finalBlow),
            new KillAttacker(
                killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
                factionID, factionName, securityStatus, shipTypeID, weaponTypeID, !finalBlow)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<KillAttacker>() {

      @Override
      public KillAttacker getModel(SynchronizedEveAccount account, long time) {
        return KillAttacker.get(account, time, killID, attackerCharacterID);
      }

    });
  }

  @Test
  public void testGetAllKillAttackers() throws Exception {
    // Should exclude:
    // - attackers for a different account
    // - attackers not live at the given time
    // - attackers for a different kill ID
    // Need to test:
    // - max results limitation
    // - continuation ID
    KillAttacker existing;
    Map<Long, KillAttacker> listCheck = new HashMap<Long, KillAttacker>();

    existing = new KillAttacker(
        killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone, factionID,
        factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(attackerCharacterID, existing);

    existing = new KillAttacker(
        killID, attackerCharacterID + 10, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
        factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(attackerCharacterID + 10, existing);

    existing = new KillAttacker(
        killID, attackerCharacterID + 20, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
        factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(attackerCharacterID + 20, existing);

    existing = new KillAttacker(
        killID, attackerCharacterID + 30, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone,
        factionID, factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(attackerCharacterID + 30, existing);

    // Associated with different account
    existing = new KillAttacker(
        killID, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone, factionID,
        factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with a different kill
    existing = new KillAttacker(
        killID + 1, attackerCharacterID, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone, factionID,
        factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new KillAttacker(
        killID, attackerCharacterID + 5, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone, factionID,
        factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new KillAttacker(
        killID, attackerCharacterID + 3, allianceID, allianceName, attackerCharacterName, attackerCorporationID, attackerCorporationName, damageDone, factionID,
        factionName, securityStatus, shipTypeID, weaponTypeID, finalBlow);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contacts are returned
    List<KillAttacker> result = KillAttacker.getAllKillAttackers(testAccount, 8888L, killID, 10, -1);
    Assert.assertEquals(listCheck.size(), result.size());
    for (KillAttacker next : result) {
      long attackerCHaracterID = next.getAttackerCharacterID();
      Assert.assertEquals(killID, next.getKillID());
      Assert.assertTrue(listCheck.containsKey(attackerCHaracterID));
      Assert.assertEquals(listCheck.get(attackerCHaracterID), next);
    }

    // Verify limited set returned
    long limit = listCheck.get(attackerCharacterID).getCid();
    result = KillAttacker.getAllKillAttackers(testAccount, 8888L, killID, 2, limit - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(attackerCharacterID), result.get(0));
    Assert.assertEquals(listCheck.get(attackerCharacterID + 10), result.get(1));

    // Verify continuation ID returns proper set
    limit = listCheck.get(attackerCharacterID + 10).getCid();
    result = KillAttacker.getAllKillAttackers(testAccount, 8888L, killID, 100, limit);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(attackerCharacterID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(attackerCharacterID + 30), result.get(1));

  }

}
