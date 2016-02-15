package enterprises.orbital.evekit.model.character;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.character.CharacterSheetClone;

public class CharacterSheetCloneTest extends AbstractModelTester<CharacterSheetClone> {
  final long                                           cloneJumpDate = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<CharacterSheetClone> eol           = new ClassUnderTestConstructor<CharacterSheetClone>() {

                                                                       @Override
                                                                       public CharacterSheetClone getCUT() {
                                                                         return new CharacterSheetClone(cloneJumpDate);
                                                                       }

                                                                     };

  final ClassUnderTestConstructor<CharacterSheetClone> live          = new ClassUnderTestConstructor<CharacterSheetClone>() {
                                                                       @Override
                                                                       public CharacterSheetClone getCUT() {
                                                                         return new CharacterSheetClone(cloneJumpDate);
                                                                       }

                                                                     };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterSheetClone>() {

      @Override
      public CharacterSheetClone[] getVariants() {
        return new CharacterSheetClone[] {
            new CharacterSheetClone(cloneJumpDate + 10)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterSheetClone>() {

      @Override
      public CharacterSheetClone getModel(SynchronizedEveAccount account, long time) {
        return CharacterSheetClone.get(account, time);
      }

    });
  }
}
