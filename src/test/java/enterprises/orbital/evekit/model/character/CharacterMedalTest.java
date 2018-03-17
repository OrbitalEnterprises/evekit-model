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

public class CharacterMedalTest extends AbstractModelTester<CharacterMedal> {
  private final String description = "test description";
  private final int medalID = TestBase.getRandomInt(100000000);
  private final String title = "test title";
  private final int corporationID = TestBase.getRandomInt(100000000);
  private final long issued = TestBase.getRandomInt(100000000);
  private final int issuerID = TestBase.getRandomInt(100000000);
  private final String reason = "test reason";
  private final String status = "test status";

  final ClassUnderTestConstructor<CharacterMedal> eol = () -> new CharacterMedal(
      description, medalID, title, corporationID, issued, issuerID, reason, status);

  final ClassUnderTestConstructor<CharacterMedal> live = () -> new CharacterMedal(
      description + " 2", medalID, title + " 2", corporationID + 1, issued, issuerID + 1,
      reason + " 2", status + " 2");

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterMedal[]{
        new CharacterMedal(description + " 2", medalID, title, corporationID, issued, issuerID, reason, status),
        new CharacterMedal(description, medalID + 1, title, corporationID, issued, issuerID, reason, status),
        new CharacterMedal(description, medalID, title + " 2", corporationID, issued, issuerID, reason, status),
        new CharacterMedal(description, medalID, title, corporationID + 1, issued, issuerID, reason, status),
        new CharacterMedal(description, medalID, title, corporationID, issued + 1, issuerID, reason, status),
        new CharacterMedal(description, medalID, title, corporationID, issued, issuerID + 1, reason, status),
        new CharacterMedal(description, medalID, title, corporationID, issued, issuerID, reason + " 2", status),
        new CharacterMedal(description, medalID, title, corporationID, issued, issuerID, reason, status + " 2")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEDALS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> CharacterMedal.get(account, time, medalID, issued));
  }

  @Test
  public void testGetAllMedals() throws Exception {
    // Should exclude:
    // - medals for a different account
    // - medals not live at the given time
    CharacterMedal existing;
    Map<Integer, Map<Long, CharacterMedal>> listCheck = new HashMap<>();

    existing = new CharacterMedal(description, medalID, title, corporationID, issued, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(medalID, new HashMap<>());
    listCheck.get(medalID)
             .put(issued, existing);

    existing = new CharacterMedal(description, medalID + 1, title, corporationID, issued + 1, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(medalID + 1, new HashMap<>());
    listCheck.get(medalID + 1)
             .put(issued + 1, existing);

    // Associated with different account
    existing = new CharacterMedal(description, medalID + 2, title, corporationID, issued + 2, issuerID, reason, status);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterMedal(description, medalID + 3, title, corporationID, issued + 3, issuerID, reason, status);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterMedal(description, medalID + 4, title, corporationID, issued + 3, issuerID, reason, status);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<CharacterMedal> result = CachedData.retrieveAll(8888L,
                                                         (contid, at) -> CharacterMedal.accessQuery(testAccount, contid,
                                                                                                    1000, false, at,
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any(),
                                                                                                    AttributeSelector.any()));

    Assert.assertEquals(listCheck.size(), result.size());
    for (CharacterMedal next : result) {
      int medalID = next.getMedalID();
      long issued = next.getIssued();
      Assert.assertTrue(listCheck.containsKey(medalID));
      Assert.assertTrue(listCheck.get(medalID)
                                 .containsKey(issued));
      Assert.assertEquals(listCheck.get(medalID)
                                   .get(issued), next);
    }

  }

}
