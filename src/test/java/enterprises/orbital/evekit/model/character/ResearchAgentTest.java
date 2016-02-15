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
import enterprises.orbital.evekit.model.character.ResearchAgent;

public class ResearchAgentTest extends AbstractModelTester<ResearchAgent> {
  final int                                      agentID           = TestBase.getRandomInt(100000000);
  final double                                   currentPoints     = TestBase.getRandomDouble(10000);
  final double                                   pointsPerDay      = TestBase.getRandomDouble(10000);
  final double                                   remainderPoints   = TestBase.getRandomDouble(10000);
  final long                                     researchStartDate = TestBase.getRandomInt(100000000);
  final int                                      skillTypeID       = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<ResearchAgent> eol               = new ClassUnderTestConstructor<ResearchAgent>() {

                                                                     @Override
                                                                     public ResearchAgent getCUT() {
                                                                       return new ResearchAgent(
                                                                           agentID, currentPoints, pointsPerDay, remainderPoints, researchStartDate,
                                                                           skillTypeID);
                                                                     }

                                                                   };

  final ClassUnderTestConstructor<ResearchAgent> live              = new ClassUnderTestConstructor<ResearchAgent>() {
                                                                     @Override
                                                                     public ResearchAgent getCUT() {
                                                                       return new ResearchAgent(
                                                                           agentID, currentPoints + 1, pointsPerDay, remainderPoints, researchStartDate,
                                                                           skillTypeID);
                                                                     }

                                                                   };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ResearchAgent>() {

      @Override
      public ResearchAgent[] getVariants() {
        return new ResearchAgent[] {
            new ResearchAgent(agentID + 1, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID),
            new ResearchAgent(agentID, currentPoints + 1, pointsPerDay, remainderPoints, researchStartDate, skillTypeID),
            new ResearchAgent(agentID, currentPoints, pointsPerDay + 1, remainderPoints, researchStartDate, skillTypeID),
            new ResearchAgent(agentID, currentPoints, pointsPerDay, remainderPoints + 1, researchStartDate, skillTypeID),
            new ResearchAgent(agentID, currentPoints, pointsPerDay, remainderPoints, researchStartDate + 1, skillTypeID),
            new ResearchAgent(agentID, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_RESEARCH));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ResearchAgent>() {

      @Override
      public ResearchAgent getModel(SynchronizedEveAccount account, long time) {
        return ResearchAgent.get(account, time, agentID);
      }

    });
  }

  @Test
  public void testGetAllAgents() throws Exception {
    // Should exclude:
    // - agents for a different account
    // - agents not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    ResearchAgent existing;
    Map<Integer, ResearchAgent> listCheck = new HashMap<Integer, ResearchAgent>();

    existing = new ResearchAgent(agentID, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(agentID, existing);

    existing = new ResearchAgent(agentID + 10, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(agentID + 10, existing);

    existing = new ResearchAgent(agentID + 20, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(agentID + 20, existing);

    existing = new ResearchAgent(agentID + 30, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(agentID + 30, existing);

    // Associated with different account
    existing = new ResearchAgent(agentID, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ResearchAgent(agentID + 3, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ResearchAgent(agentID + 4, currentPoints, pointsPerDay, remainderPoints, researchStartDate, skillTypeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all agents are returned
    List<ResearchAgent> result = ResearchAgent.getAllAgents(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (ResearchAgent next : result) {
      int agentID = next.getAgentID();
      Assert.assertTrue(listCheck.containsKey(agentID));
      Assert.assertEquals(listCheck.get(agentID), next);
    }

    // Verify limited set returned
    result = ResearchAgent.getAllAgents(testAccount, 8888L, 2, agentID - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(agentID), result.get(0));
    Assert.assertEquals(listCheck.get(agentID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = ResearchAgent.getAllAgents(testAccount, 8888L, 100, agentID + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(agentID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(agentID + 30), result.get(1));
  }

}
