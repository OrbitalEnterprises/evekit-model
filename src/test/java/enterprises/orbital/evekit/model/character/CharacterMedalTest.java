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
import enterprises.orbital.evekit.model.character.CharacterMedal;

public class CharacterMedalTest extends AbstractModelTester<CharacterMedal> {
  final String                                    description   = "test description";
  final int                                       medalID       = TestBase.getRandomInt(100000000);
  final String                                    title         = "test title";
  final long                                      corporationID = TestBase.getRandomInt(100000000);
  final long                                      issued        = TestBase.getRandomInt(100000000);
  final long                                      issuerID      = TestBase.getRandomInt(100000000);
  final String                                    reason        = "test reason";
  final String                                    status        = "test status";

  final ClassUnderTestConstructor<CharacterMedal> eol           = new ClassUnderTestConstructor<CharacterMedal>() {

                                                                  @Override
                                                                  public CharacterMedal getCUT() {
                                                                    return new CharacterMedal(
                                                                        description, medalID, title, corporationID, issued, issuerID, reason, status);
                                                                  }

                                                                };

  final ClassUnderTestConstructor<CharacterMedal> live          = new ClassUnderTestConstructor<CharacterMedal>() {
                                                                  @Override
                                                                  public CharacterMedal getCUT() {
                                                                    return new CharacterMedal(
                                                                        description + " 2", medalID, title + " 2", corporationID + 1, issued, issuerID + 1,
                                                                        reason + " 2", status + " 2");
                                                                  }

                                                                };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<CharacterMedal>() {

      @Override
      public CharacterMedal[] getVariants() {
        return new CharacterMedal[] {
            new CharacterMedal(description + " 2", medalID, title, corporationID, issued, issuerID, reason, status),
            new CharacterMedal(description, medalID + 1, title, corporationID, issued, issuerID, reason, status),
            new CharacterMedal(description, medalID, title + " 2", corporationID, issued, issuerID, reason, status),
            new CharacterMedal(description, medalID, title, corporationID + 1, issued, issuerID, reason, status),
            new CharacterMedal(description, medalID, title, corporationID, issued + 1, issuerID, reason, status),
            new CharacterMedal(description, medalID, title, corporationID, issued, issuerID + 1, reason, status),
            new CharacterMedal(description, medalID, title, corporationID, issued, issuerID, reason + " 2", status),
            new CharacterMedal(description, medalID, title, corporationID, issued, issuerID, reason, status + " 2")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEDALS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<CharacterMedal>() {

      @Override
      public CharacterMedal getModel(SynchronizedEveAccount account, long time) {
        return CharacterMedal.get(account, time, medalID, issued);
      }

    });
  }

  @Test
  public void testGetAllMedals() throws Exception {
    // Should exclude:
    // - medals for a different account
    // - medals not live at the given time
    CharacterMedal existing;
    Map<Integer, Map<Long, CharacterMedal>> listCheck = new HashMap<Integer, Map<Long, CharacterMedal>>();

    existing = new CharacterMedal(description, medalID, title, corporationID, issued, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(medalID, new HashMap<Long, CharacterMedal>());
    listCheck.get(medalID).put(issued, existing);

    existing = new CharacterMedal(description, medalID + 1, title, corporationID, issued + 1, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(medalID + 1, new HashMap<Long, CharacterMedal>());
    listCheck.get(medalID + 1).put(issued + 1, existing);

    // Associated with different account
    existing = new CharacterMedal(description, medalID + 2, title, corporationID, issued + 2, issuerID, reason, status);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new CharacterMedal(description, medalID + 3, title, corporationID, issued + 3, issuerID, reason, status);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new CharacterMedal(description, medalID + 4, title, corporationID, issued + 3, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<CharacterMedal> result = CharacterMedal.getAllMedals(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterMedal next : result) {
      int medalID = next.getMedalID();
      long issued = next.getIssued();
      Assert.assertTrue(listCheck.containsKey(medalID));
      Assert.assertTrue(listCheck.get(medalID).containsKey(issued));
      Assert.assertEquals(listCheck.get(medalID).get(issued), next);
    }

  }

}
