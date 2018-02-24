package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class CharacterSheetTest extends AbstractModelTester<CharacterSheet> {
  private final long characterID = TestBase.getRandomInt(100000000);
  private final String name = "test name";
  private final int corporationID = TestBase.getRandomInt(100000000);
  private final int raceID = TestBase.getRandomInt();
  private final long doB = TestBase.getRandomInt(100000000);
  private final int bloodlineID = TestBase.getRandomInt(100000000);
  private final int ancestryID = TestBase.getRandomInt(100000000);
  private final String gender = "test gender";
  private final int allianceID = TestBase.getRandomInt(100000000);
  private final int factionID = TestBase.getRandomInt(100000000);
  private final String description = TestBase.getRandomText(50);
  private final float securityStatus = TestBase.getRandomFloat(10);

  final ClassUnderTestConstructor<CharacterSheet> eol = () -> new CharacterSheet(characterID, name, corporationID,
                                                                                 raceID, doB, bloodlineID,
                                                                                 ancestryID, gender, allianceID,
                                                                                 factionID, description,
                                                                                 securityStatus);

  final ClassUnderTestConstructor<CharacterSheet> live = () -> new CharacterSheet(characterID, name, corporationID + 1,
                                                                                  raceID, doB, bloodlineID,
                                                                                  ancestryID, gender, allianceID,
                                                                                  factionID, description,
                                                                                  securityStatus);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterSheet[]{
        new CharacterSheet(
            characterID + 1, name, corporationID, raceID, doB, bloodlineID, ancestryID, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name + "1", corporationID, raceID, doB, bloodlineID, ancestryID, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID + 1, raceID, doB, bloodlineID, ancestryID, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID + 1, doB, bloodlineID, ancestryID, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB + 1, bloodlineID, ancestryID, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID + 1, ancestryID, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID + 1, gender,
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID, gender + "1",
            allianceID, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID, gender,
            allianceID + 1, factionID, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID, gender, allianceID,
            factionID + 1, description, securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID, gender, allianceID,
            factionID, description + "1", securityStatus),
        new CharacterSheet(
            characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID, gender, allianceID,
            factionID, description, securityStatus + 1.0F),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterSheet.get(account, time));
  }
}
