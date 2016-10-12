package enterprises.orbital.evekit.model.character;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;

public class CharacterSheetTest extends AbstractModelTester<CharacterSheet> {
  final long                                      characterID       = TestBase.getRandomInt(100000000);
  final String                                    name              = "test name";
  final long                                      corporationID     = TestBase.getRandomInt(100000000);
  final String                                    corporationName   = "test corp";
  final String                                    race              = "test race";
  final long                                      doB               = TestBase.getRandomInt(100000000);
  final int                                       bloodlineID       = TestBase.getRandomInt(100000000);
  final String                                    bloodline         = "test bloodline";
  final int                                       ancestryID        = TestBase.getRandomInt(100000000);
  final String                                    ancestry          = "test ancestry";
  final String                                    gender            = "test gender";
  final String                                    allianceName      = "test alliance";
  final long                                      allianceID        = TestBase.getRandomInt(100000000);
  final String                                    factionName       = "test faction";
  final long                                      factionID         = TestBase.getRandomInt(100000000);
  final int                                       intelligence      = TestBase.getRandomInt(100000000);
  final int                                       memory            = TestBase.getRandomInt(100000000);
  final int                                       charisma          = TestBase.getRandomInt(100000000);
  final int                                       perception        = TestBase.getRandomInt(100000000);
  final int                                       willpower         = TestBase.getRandomInt(100000000);
  final long                                      homeStationID     = TestBase.getRandomInt(100000000);
  final long                                      lastRespecDate    = TestBase.getRandomInt(100000000);
  final long                                      lastTimedRespec   = TestBase.getRandomInt(100000000);
  final int                                       freeRespecs       = TestBase.getRandomInt(100000000);
  final long                                      freeSkillPoints   = TestBase.getRandomInt(100000000);
  final long                                      remoteStationDate = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterSheet> eol               = new ClassUnderTestConstructor<CharacterSheet>() {

                                                                      @Override
                                                                      public CharacterSheet getCUT() {
                                                                        return new CharacterSheet(
                                                                            characterID, name, corporationID, corporationName, race, doB, bloodlineID,
                                                                            bloodline, ancestryID, ancestry, gender, allianceName, allianceID, factionName,
                                                                            factionID, intelligence, memory, charisma, perception, willpower, homeStationID,
                                                                            lastRespecDate, lastTimedRespec, freeRespecs, freeSkillPoints, remoteStationDate);
                                                                      }

                                                                    };

  final ClassUnderTestConstructor<CharacterSheet> live              = new ClassUnderTestConstructor<CharacterSheet>() {
                                                                      @Override
                                                                      public CharacterSheet getCUT() {
                                                                        return new CharacterSheet(
                                                                            characterID, name, corporationID + 1, corporationName, race, doB, bloodlineID,
                                                                            bloodline, ancestryID, ancestry, gender, allianceName, allianceID, factionName,
                                                                            factionID, intelligence, memory, charisma, perception, willpower, homeStationID,
                                                                            lastRespecDate, lastTimedRespec, freeRespecs, freeSkillPoints, remoteStationDate);
                                                                      }

                                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterSheet>() {

      @Override
      public CharacterSheet[] getVariants() {
        return new CharacterSheet[] {
            new CharacterSheet(
                characterID + 1, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name + "1", corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID + 1, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName + "1", race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race + "1", doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB + 1, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID + 1, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline + "1", ancestryID, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID + 1, ancestry, gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry + "1", gender, allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender + "1", allianceName,
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName + "1",
                allianceID, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName,
                allianceID + 1, factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName + "1", factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec,
                freeRespecs, freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID + 1, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence + 1, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory + 1, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma + 1, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception + 1, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower + 1, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID + 1, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate + 1, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec + 1, freeRespecs,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs + 1,
                freeSkillPoints, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints + 1, remoteStationDate),
            new CharacterSheet(
                characterID, name, corporationID, corporationName, race, doB, bloodlineID, bloodline, ancestryID, ancestry, gender, allianceName, allianceID,
                factionName, factionID, intelligence, memory, charisma, perception, willpower, homeStationID, lastRespecDate, lastTimedRespec, freeRespecs,
                freeSkillPoints, remoteStationDate + 1),
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterSheet>() {

      @Override
      public CharacterSheet getModel(
                                     SynchronizedEveAccount account,
                                     long time) {
        return CharacterSheet.get(account, time);
      }

    });
  }
}
