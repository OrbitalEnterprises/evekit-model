package enterprises.orbital.evekit.model.character;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.character.CharacterSheetJump;

public class CharacterSheetJumpTest extends AbstractModelTester<CharacterSheetJump> {
  final long                                          jumpActivation = TestBase.getRandomInt(100000000);
  final long                                          jumpFatigue    = TestBase.getRandomInt(100000000);
  final long                                          jumpLastUpdate = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterSheetJump> eol            = new ClassUnderTestConstructor<CharacterSheetJump>() {

                                                                       @Override
                                                                       public CharacterSheetJump getCUT() {
                                                                         return new CharacterSheetJump(jumpActivation, jumpFatigue, jumpLastUpdate);
                                                                       }

                                                                     };

  final ClassUnderTestConstructor<CharacterSheetJump> live           = new ClassUnderTestConstructor<CharacterSheetJump>() {
                                                                       @Override
                                                                       public CharacterSheetJump getCUT() {
                                                                         return new CharacterSheetJump(jumpActivation, jumpFatigue, jumpLastUpdate);
                                                                       }

                                                                     };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterSheetJump>() {

      @Override
      public CharacterSheetJump[] getVariants() {
        return new CharacterSheetJump[] {
            new CharacterSheetJump(jumpActivation + 1, jumpFatigue, jumpLastUpdate), new CharacterSheetJump(jumpActivation, jumpFatigue + 1, jumpLastUpdate),
            new CharacterSheetJump(jumpActivation, jumpFatigue, jumpLastUpdate + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterSheetJump>() {

      @Override
      public CharacterSheetJump getModel(SynchronizedEveAccount account, long time) {
        return CharacterSheetJump.get(account, time);
      }

    });
  }
}
