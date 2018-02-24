package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterSkillTest extends AbstractModelTester<CharacterSkill> {

  private final int typeID = TestBase.getRandomInt(100000000);
  private final int trainedSkillLevel = TestBase.getRandomInt(10);
  private final long skillpoints = TestBase.getRandomInt(100000000);
  private final int activeSkillLevel = TestBase.getRandomInt(10);

  final ClassUnderTestConstructor<CharacterSkill> eol = () -> new CharacterSkill(typeID, trainedSkillLevel, skillpoints,
                                                                                 activeSkillLevel);

  final ClassUnderTestConstructor<CharacterSkill> live = () -> new CharacterSkill(typeID, trainedSkillLevel,
                                                                                  skillpoints + 1, activeSkillLevel);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new CharacterSkill[]{
        new CharacterSkill(typeID + 1, trainedSkillLevel, skillpoints, activeSkillLevel),
        new CharacterSkill(typeID, trainedSkillLevel + 1, skillpoints, activeSkillLevel),
        new CharacterSkill(typeID, trainedSkillLevel, skillpoints + 1, activeSkillLevel),
        new CharacterSkill(typeID, trainedSkillLevel, skillpoints, activeSkillLevel + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterSkill.get(account, time, typeID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - skills for a different account
    // - skills not live at the given time

    CharacterSkill existing;
    Map<Integer, CharacterSkill> listCheck = new HashMap<>();

    existing = new CharacterSkill(typeID, trainedSkillLevel, skillpoints, activeSkillLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID, existing);

    existing = new CharacterSkill(typeID + 10, trainedSkillLevel + 10, skillpoints + 10, activeSkillLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 10, existing);

    existing = new CharacterSkill(typeID + 20, trainedSkillLevel + 20, skillpoints + 20, activeSkillLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 20, existing);

    existing = new CharacterSkill(typeID + 30, trainedSkillLevel + 30, skillpoints + 30, activeSkillLevel);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 30, existing);

    // Associated with different account
    existing = new CharacterSkill(typeID, trainedSkillLevel, skillpoints, activeSkillLevel);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterSkill(typeID + 5, trainedSkillLevel + 5, skillpoints + 5, activeSkillLevel);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterSkill(typeID + 3, trainedSkillLevel + 3, skillpoints + 3, activeSkillLevel);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all skills are returned
    List<CharacterSkill> result = CachedData.retrieveAll(8888L,
                                                         (contid, at) -> CharacterSkill.accessQuery(testAccount, contid,
                                                                                                    1000, false, at,
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterSkill next : result) {
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(typeID));
      Assert.assertEquals(listCheck.get(typeID), next);
    }
  }

}
