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
import enterprises.orbital.evekit.model.character.CharacterTitle;

public class CharacterTitleTest extends AbstractModelTester<CharacterTitle> {
  final long                                      titleID   = TestBase.getRandomInt(100000000);
  final String                                    titleName = "test title";

  final ClassUnderTestConstructor<CharacterTitle> eol       = new ClassUnderTestConstructor<CharacterTitle>() {

                                                              @Override
                                                              public CharacterTitle getCUT() {
                                                                return new CharacterTitle(titleID, titleName);
                                                              }

                                                            };

  final ClassUnderTestConstructor<CharacterTitle> live      = new ClassUnderTestConstructor<CharacterTitle>() {
                                                              @Override
                                                              public CharacterTitle getCUT() {
                                                                return new CharacterTitle(titleID, titleName + " 2");
                                                              }

                                                            };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterTitle>() {

      @Override
      public CharacterTitle[] getVariants() {
        return new CharacterTitle[] {
            new CharacterTitle(titleID + 1, titleName), new CharacterTitle(titleID, titleName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterTitle>() {

      @Override
      public CharacterTitle getModel(SynchronizedEveAccount account, long time) {
        return CharacterTitle.get(account, time, titleID);
      }

    });
  }

  @Test
  public void testGetAllTitles() throws Exception {
    // Should exclude:
    // - titles for a different account
    // - titles not live at the given time
    CharacterTitle existing;
    Map<Long, CharacterTitle> listCheck = new HashMap<Long, CharacterTitle>();

    existing = new CharacterTitle(titleID, titleName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(titleID, existing);

    existing = new CharacterTitle(titleID + 10, titleName + " 1");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(titleID + 10, existing);

    // Associated with different account
    existing = new CharacterTitle(titleID, titleName);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterTitle(titleID + 3, titleName + " 3");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterTitle(titleID + 4, titleName + " 4");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CharacterTitle> result = CharacterTitle.getAllTitles(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterTitle next : result) {
      long titleID = next.getTitleID();
      Assert.assertTrue(listCheck.containsKey(titleID));
      Assert.assertEquals(listCheck.get(titleID), next);
    }

  }

}
