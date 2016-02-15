package enterprises.orbital.evekit.model.common;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.common.KillVictim;

public class KillVictimTest extends AbstractModelTester<KillVictim> {

  final long                                  killID              = TestBase.getRandomInt(100000000);
  final long                                  allianceID          = TestBase.getRandomInt(100000000);
  final String                                allianceName        = "test alliance name";
  final long                                  killCharacterID     = TestBase.getRandomInt(100000000);
  final String                                killCharacterName   = "test kill character name";
  final long                                  killCorporationID   = TestBase.getRandomInt(100000000);
  final String                                killCorporationName = "test kill corporation name";
  final long                                  damageTaken         = TestBase.getRandomInt(100000000);
  final long                                  factionID           = TestBase.getRandomInt(100000000);
  final String                                factionName         = "test faction name";
  final int                                   shipTypeID          = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<KillVictim> eol                 = new ClassUnderTestConstructor<KillVictim>() {

                                                                    @Override
                                                                    public KillVictim getCUT() {
                                                                      return new KillVictim(
                                                                          killID, allianceID, allianceName, killCharacterID, killCharacterName,
                                                                          killCorporationID, killCorporationName, damageTaken, factionID, factionName,
                                                                          shipTypeID);
                                                                    }

                                                                  };

  final ClassUnderTestConstructor<KillVictim> live                = new ClassUnderTestConstructor<KillVictim>() {
                                                                    @Override
                                                                    public KillVictim getCUT() {
                                                                      return new KillVictim(
                                                                          killID, allianceID + 1, allianceName, killCharacterID, killCharacterName,
                                                                          killCorporationID, killCorporationName, damageTaken, factionID, factionName,
                                                                          shipTypeID);
                                                                    }

                                                                  };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<KillVictim>() {

      @Override
      public KillVictim[] getVariants() {
        return new KillVictim[] {
            new KillVictim(
                killID + 1, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID + 1, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName + " 1", killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID + 1, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName + " 1", killCorporationID, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID + 1, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName + " 1", damageTaken, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken + 1, factionID,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID + 1,
                factionName, shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID,
                factionName + " 1", shipTypeID),
            new KillVictim(
                killID, allianceID, allianceName, killCharacterID, killCharacterName, killCorporationID, killCorporationName, damageTaken, factionID,
                factionName, shipTypeID + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<KillVictim>() {

      @Override
      public KillVictim getModel(SynchronizedEveAccount account, long time) {
        return KillVictim.get(account, time, killID);
      }

    });
  }

}
