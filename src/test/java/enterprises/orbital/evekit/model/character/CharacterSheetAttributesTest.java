package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class CharacterSheetAttributesTest extends AbstractModelTester<CharacterSheetAttributes> {
  private final int intelligence = TestBase.getRandomInt();
  private final int memory = TestBase.getRandomInt();
  private final int charisma = TestBase.getRandomInt();
  private final int perception = TestBase.getRandomInt();
  private final int willpower = TestBase.getRandomInt();
  private final int bonusRemaps = TestBase.getRandomInt();
  private final long lastRemapDate = TestBase.getRandomLong();
  private final long accruedRemapCooldownDate = TestBase.getRandomLong();

  final ClassUnderTestConstructor<CharacterSheetAttributes> eol = () -> new CharacterSheetAttributes(intelligence,
                                                                                                     memory,
                                                                                                     charisma,
                                                                                                     perception,
                                                                                                     willpower,
                                                                                                     bonusRemaps,
                                                                                                     lastRemapDate,
                                                                                                     accruedRemapCooldownDate);

  final ClassUnderTestConstructor<CharacterSheetAttributes> live = () -> new CharacterSheetAttributes(intelligence + 1,
                                                                                                      memory,
                                                                                                      charisma,
                                                                                                      perception,
                                                                                                      willpower,
                                                                                                      bonusRemaps,
                                                                                                      lastRemapDate,
                                                                                                      accruedRemapCooldownDate);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterSheetAttributes[]{
        new CharacterSheetAttributes(intelligence + 1, memory, charisma, perception, willpower, bonusRemaps,
                                     lastRemapDate, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory + 1, charisma, perception, willpower, bonusRemaps,
                                     lastRemapDate, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory, charisma + 1, perception, willpower, bonusRemaps,
                                     lastRemapDate, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory, charisma, perception + 1, willpower, bonusRemaps,
                                     lastRemapDate, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory, charisma, perception, willpower + 1, bonusRemaps,
                                     lastRemapDate, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory, charisma, perception, willpower, bonusRemaps + 1,
                                     lastRemapDate, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory, charisma, perception, willpower, bonusRemaps,
                                     lastRemapDate + 1, accruedRemapCooldownDate),
        new CharacterSheetAttributes(intelligence, memory, charisma, perception, willpower, bonusRemaps, lastRemapDate,
                                     accruedRemapCooldownDate + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));

  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterSheetAttributes.get(account, time));
  }
}
