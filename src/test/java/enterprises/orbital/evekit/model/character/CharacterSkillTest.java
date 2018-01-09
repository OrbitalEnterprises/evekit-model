package enterprises.orbital.evekit.model.character;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.character.CharacterSkill;

public class CharacterSkillTest extends AbstractModelTester<CharacterSkill> {

  final int                                       typeID      = TestBase.getRandomInt(100000000);
  final int                                       level       = TestBase.getRandomInt(100000000);
  final int                                       skillpoints = TestBase.getRandomInt(100000000);
  final boolean                                   published   = true;

  final ClassUnderTestConstructor<CharacterSkill> eol         = new ClassUnderTestConstructor<CharacterSkill>() {

                                                                @Override
                                                                public CharacterSkill getCUT() {
                                                                  return new CharacterSkill(typeID, level, skillpoints, published);
                                                                }

                                                              };

  final ClassUnderTestConstructor<CharacterSkill> live        = new ClassUnderTestConstructor<CharacterSkill>() {
                                                                @Override
                                                                public CharacterSkill getCUT() {
                                                                  return new CharacterSkill(typeID, level, skillpoints + 5, published);
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterSkill>() {

      @Override
      public CharacterSkill[] getVariants() {
        return new CharacterSkill[] {
            new CharacterSkill(typeID + 1, level, skillpoints, published), new CharacterSkill(typeID, level + 1, skillpoints, published),
            new CharacterSkill(typeID, level, skillpoints + 1, published), new CharacterSkill(typeID, level, skillpoints, !published)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterSkill>() {

      @Override
      public CharacterSkill getModel(SynchronizedEveAccount account, long time) {
        return CharacterSkill.get(account, time, typeID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - skills for a different account
    // - skills not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID

    CharacterSkill existing;
    Map<Integer, CharacterSkill> listCheck = new HashMap<Integer, CharacterSkill>();

    existing = new CharacterSkill(typeID, level, skillpoints, published);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID, existing);

    existing = new CharacterSkill(typeID + 10, level + 10, skillpoints + 10, published);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 10, existing);

    existing = new CharacterSkill(typeID + 20, level + 20, skillpoints + 20, published);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 20, existing);

    existing = new CharacterSkill(typeID + 30, level + 30, skillpoints + 30, published);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(typeID + 30, existing);

    // Associated with different account
    existing = new CharacterSkill(typeID, level, skillpoints, published);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterSkill(typeID + 5, level + 5, skillpoints + 5, published);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterSkill(typeID + 3, level + 3, skillpoints + 3, published);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all skillss are returned
    List<CharacterSkill> result = CharacterSkill.getAll(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterSkill next : result) {
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(typeID));
      Assert.assertEquals(listCheck.get(typeID), next);
    }

    // Verify limited set returned
    result = CharacterSkill.getAll(testAccount, 8888L, 2, typeID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(typeID), result.get(0));
    Assert.assertEquals(listCheck.get(typeID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = CharacterSkill.getAll(testAccount, 8888L, 100, typeID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(typeID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(typeID + 30), result.get(1));

  }

}
