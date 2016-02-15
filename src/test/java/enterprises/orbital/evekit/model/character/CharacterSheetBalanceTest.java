package enterprises.orbital.evekit.model.character;

import java.math.BigDecimal;

import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.character.CharacterSheetBalance;

public class CharacterSheetBalanceTest extends AbstractModelTester<CharacterSheetBalance> {
  final BigDecimal                                       balance = TestBase.getRandomBigDecimal(100000000);

  final ClassUnderTestConstructor<CharacterSheetBalance> eol     = new ClassUnderTestConstructor<CharacterSheetBalance>() {

                                                                   @Override
                                                                   public CharacterSheetBalance getCUT() {
                                                                     return new CharacterSheetBalance(balance);
                                                                   }

                                                                 };

  final ClassUnderTestConstructor<CharacterSheetBalance> live    = new ClassUnderTestConstructor<CharacterSheetBalance>() {
                                                                   @Override
                                                                   public CharacterSheetBalance getCUT() {
                                                                     return new CharacterSheetBalance(balance);
                                                                   }

                                                                 };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterSheetBalance>() {

      @Override
      public CharacterSheetBalance[] getVariants() {
        return new CharacterSheetBalance[] {
            new CharacterSheetBalance(balance.add(BigDecimal.TEN))
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterSheetBalance>() {

      @Override
      public CharacterSheetBalance getModel(SynchronizedEveAccount account, long time) {
        return CharacterSheetBalance.get(account, time);
      }

    });
  }
}
