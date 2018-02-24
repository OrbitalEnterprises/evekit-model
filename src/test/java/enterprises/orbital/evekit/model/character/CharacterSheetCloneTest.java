package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class CharacterSheetCloneTest extends AbstractModelTester<CharacterSheetClone> {
  private final long cloneJumpDate = TestBase.getRandomInt(100000000);
  private final long homeStationID = TestBase.getRandomInt(100000000);
  private final String homeStationType = TestBase.getRandomText(50);
  private final long lastStationChangeDate = TestBase.getRandomLong();

  final ClassUnderTestConstructor<CharacterSheetClone> eol = () -> new CharacterSheetClone(cloneJumpDate, homeStationID,
                                                                                           homeStationType,
                                                                                           lastStationChangeDate);

  final ClassUnderTestConstructor<CharacterSheetClone> live = () -> new CharacterSheetClone(cloneJumpDate + 1,
                                                                                            homeStationID,
                                                                                            homeStationType,
                                                                                            lastStationChangeDate);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterSheetClone[]{
        new CharacterSheetClone(cloneJumpDate + 10, homeStationID, homeStationType, lastStationChangeDate),
        new CharacterSheetClone(cloneJumpDate, homeStationID + 1, homeStationType, lastStationChangeDate),
        new CharacterSheetClone(cloneJumpDate, homeStationID, homeStationType + "1", lastStationChangeDate),
        new CharacterSheetClone(cloneJumpDate, homeStationID, homeStationType, lastStationChangeDate + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CharacterSheetClone::get);
  }
}
