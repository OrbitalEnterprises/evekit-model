package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class CharacterSheetSkillPointsTest extends AbstractModelTester<CharacterSheetSkillPoints> {
  private final long totalSkillPoints = TestBase.getRandomInt(100000000);
  private final int unallocatedSkillPoints = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterSheetSkillPoints> eol = () -> new CharacterSheetSkillPoints(totalSkillPoints,
                                                                                                       unallocatedSkillPoints);

  final ClassUnderTestConstructor<CharacterSheetSkillPoints> live = () -> new CharacterSheetSkillPoints(
      totalSkillPoints + 1, unallocatedSkillPoints);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new CharacterSheetSkillPoints[]{
        new CharacterSheetSkillPoints(totalSkillPoints + 1, unallocatedSkillPoints),
        new CharacterSheetSkillPoints(totalSkillPoints, unallocatedSkillPoints + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CharacterSheetSkillPoints::get);
  }
}
