package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class CharacterSheetJumpTest extends AbstractModelTester<CharacterSheetJump> {
  private final long jumpActivation = TestBase.getRandomInt(100000000);
  private final long jumpFatigue = TestBase.getRandomInt(100000000);
  private final long jumpLastUpdate = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterSheetJump> eol = () -> new CharacterSheetJump(jumpActivation, jumpFatigue,
                                                                                         jumpLastUpdate);

  final ClassUnderTestConstructor<CharacterSheetJump> live = () -> new CharacterSheetJump(jumpActivation + 1,
                                                                                          jumpFatigue, jumpLastUpdate);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new CharacterSheetJump[]{
        new CharacterSheetJump(jumpActivation + 1, jumpFatigue, jumpLastUpdate), new CharacterSheetJump(jumpActivation,
                                                                                                        jumpFatigue + 1,
                                                                                                        jumpLastUpdate),
        new CharacterSheetJump(jumpActivation, jumpFatigue, jumpLastUpdate + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CharacterSheetJump::get);
  }
}
