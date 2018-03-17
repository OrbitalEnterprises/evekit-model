package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CharacterMedalGraphicTest extends AbstractModelTester<CharacterMedalGraphic> {
  private final int medalID = TestBase.getRandomInt(100000000);
  private final long issued = TestBase.getRandomInt(100000000);
  private final int part = TestBase.getRandomInt();
  private final int layer = TestBase.getRandomInt();
  private final String graphic = "test graphic";
  private final int color = TestBase.getRandomInt();

  final ClassUnderTestConstructor<CharacterMedalGraphic> eol = () -> new CharacterMedalGraphic(
      medalID, issued, part, layer, graphic, color);

  final ClassUnderTestConstructor<CharacterMedalGraphic> live = () -> new CharacterMedalGraphic(
      medalID, issued, part, layer, graphic + "1", color + 1);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterMedalGraphic[]{
        new CharacterMedalGraphic(medalID + 1, issued, part, layer, graphic, color),
        new CharacterMedalGraphic(medalID, issued + 1, part, layer, graphic, color),
        new CharacterMedalGraphic(medalID, issued, part + 1, layer, graphic, color),
        new CharacterMedalGraphic(medalID, issued, part, layer + 1, graphic, color),
        new CharacterMedalGraphic(medalID, issued, part, layer, graphic + "1", color),
        new CharacterMedalGraphic(medalID, issued, part, layer, graphic, color + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEDALS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live,
                       (account, time) -> CharacterMedalGraphic.get(account, time, medalID, issued, part, layer));
  }

  @Test
  public void testGetAllMedals() throws Exception {
    // Should exclude:
    // - medals for a different account
    // - medals not live at the given time
    CharacterMedalGraphic existing;
    List<CharacterMedalGraphic> listCheck = new ArrayList<>();

    existing = new CharacterMedalGraphic(medalID, issued, part, layer, graphic, color);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    existing = new CharacterMedalGraphic(medalID + 1, issued + 1, part, layer, graphic, color);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    // Associated with different account
    existing = new CharacterMedalGraphic(medalID + 2, issued + 2, part, layer, graphic, color);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterMedalGraphic(medalID + 3, issued + 3, part, layer, graphic, color);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterMedalGraphic(medalID + 4, issued + 3, part, layer, graphic, color);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CharacterMedalGraphic> result = CachedData.retrieveAll(8888L,
                                                                (contid, at) -> CharacterMedalGraphic.accessQuery(
                                                                    testAccount, contid, 1000, false, at,
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any(),
                                                                    AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (int i = 0; i < result.size(); i++) {
      CharacterMedalGraphic check = listCheck.get(i);
      CharacterMedalGraphic stored = result.get(i);
      Assert.assertEquals(check, stored);
    }

  }

}
