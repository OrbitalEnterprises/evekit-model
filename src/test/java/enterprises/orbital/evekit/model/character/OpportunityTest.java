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

public class OpportunityTest extends AbstractModelTester<Opportunity> {
  private final int taskID = TestBase.getRandomInt();
  private final long completedAt = TestBase.getRandomLong();

  final ClassUnderTestConstructor<Opportunity> eol = () -> new Opportunity(taskID, completedAt);

  final ClassUnderTestConstructor<Opportunity> live = () -> new Opportunity(taskID, completedAt + 1);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new Opportunity[]{
        new Opportunity(taskID + 1, completedAt),
        new Opportunity(taskID, completedAt + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Opportunity.get(account, time, taskID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - opportunities for a different account
    // - opportunities not live at the given time
    Opportunity existing;
    Map<Integer, Opportunity> listCheck = new HashMap<>();

    existing = new Opportunity(taskID, completedAt);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(taskID, existing);

    existing = new Opportunity(taskID + 10, completedAt);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(taskID + 10, existing);

    // Associated with different account
    existing = new Opportunity(taskID, completedAt);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Opportunity(taskID + 3, completedAt);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Opportunity(taskID + 4, completedAt);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Opportunity> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> Opportunity.accessQuery(testAccount, contid, 1000,
                                                                                              false,
                                                                                              at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Opportunity next : result) {
      int taskID = next.getTaskID();
      Assert.assertTrue(listCheck.containsKey(taskID));
      Assert.assertEquals(listCheck.get(taskID), next);
    }

  }

}
