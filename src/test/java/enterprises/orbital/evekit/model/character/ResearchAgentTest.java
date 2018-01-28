package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

public class ResearchAgentTest extends AbstractModelTester<ResearchAgent> {
  private final int agentID = TestBase.getRandomInt(100000000);
  private final float pointsPerDay = TestBase.getRandomFloat(10000);
  private final float remainderPoints = TestBase.getRandomFloat(10000);
  private final long researchStartDate = TestBase.getRandomInt(100000000);
  private final int skillTypeID = TestBase.getRandomInt(100000000);

  private final ClassUnderTestConstructor<ResearchAgent> eol = () -> new ResearchAgent(
      agentID, pointsPerDay, remainderPoints, researchStartDate,
      skillTypeID);
  private final ClassUnderTestConstructor<ResearchAgent> live = () -> new ResearchAgent(
      agentID, pointsPerDay, remainderPoints, researchStartDate,
      skillTypeID + 1);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new ResearchAgent[]{
        new ResearchAgent(agentID + 1, pointsPerDay, remainderPoints, researchStartDate, skillTypeID),
        new ResearchAgent(agentID, pointsPerDay + 1, remainderPoints, researchStartDate, skillTypeID),
        new ResearchAgent(agentID, pointsPerDay, remainderPoints + 1, researchStartDate, skillTypeID),
        new ResearchAgent(agentID, pointsPerDay, remainderPoints, researchStartDate + 1, skillTypeID),
        new ResearchAgent(agentID, pointsPerDay, remainderPoints, researchStartDate, skillTypeID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_RESEARCH));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> ResearchAgent.get(account, time, agentID));
  }

  @Test
  public void testGetAllAgents() throws Exception {
    // Should exclude:
    // - agents for a different account
    // - agents with different agent ID
    // - agents not live at the given time
    ResearchAgent existing, keyed;

    keyed = new ResearchAgent(agentID, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    keyed.setup(testAccount, 7777L);
    keyed = CachedData.update(keyed);

    // Different agent ID
    existing = new ResearchAgent(agentID + 10, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = new ResearchAgent(agentID, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ResearchAgent(agentID + 3, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ResearchAgent(agentID + 4, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    ResearchAgent result = ResearchAgent.get(testAccount, 8888L, agentID);
    Assert.assertEquals(keyed, result);
  }

}
