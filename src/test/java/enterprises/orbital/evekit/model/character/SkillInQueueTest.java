package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillInQueueTest extends AbstractModelTester<SkillInQueue> {
  final int                                     endSP         = TestBase.getRandomInt(100000000);
  final long                                    endTime       = TestBase.getRandomInt(100000000);
  final int                                     level         = TestBase.getRandomInt(100000000);
  final int                                     queuePosition = TestBase.getRandomInt(100000000);
  final int                                     startSP       = TestBase.getRandomInt(100000000);
  final long                                    startTime     = TestBase.getRandomInt(100000000);
  final int                                     typeID        = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<SkillInQueue> eol           = new ClassUnderTestConstructor<SkillInQueue>() {

                                                                @Override
                                                                public SkillInQueue getCUT() {
                                                                  return new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime, typeID);
                                                                }

                                                              };

  final ClassUnderTestConstructor<SkillInQueue> live          = new ClassUnderTestConstructor<SkillInQueue>() {
                                                                @Override
                                                                public SkillInQueue getCUT() {
                                                                  return new SkillInQueue(endSP + 1, endTime, level, queuePosition, startSP, startTime, typeID);
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<SkillInQueue>() {

      @Override
      public SkillInQueue[] getVariants() {
        return new SkillInQueue[] {
            new SkillInQueue(endSP + 1, endTime, level, queuePosition, startSP, startTime, typeID),
            new SkillInQueue(endSP, endTime + 1, level, queuePosition, startSP, startTime, typeID),
            new SkillInQueue(endSP, endTime, level + 1, queuePosition, startSP, startTime, typeID),
            new SkillInQueue(endSP, endTime, level, queuePosition + 1, startSP, startTime, typeID),
            new SkillInQueue(endSP, endTime, level, queuePosition, startSP + 1, startTime, typeID),
            new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime + 1, typeID),
            new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime, typeID + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_SKILL_QUEUE));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<SkillInQueue>() {

      @Override
      public SkillInQueue getModel(SynchronizedEveAccount account, long time) {
        return SkillInQueue.get(account, time, queuePosition);
      }

    });
  }

  @Test
  public void testGetAtOrAfterPosition() throws Exception {
    // Should exclude:
    // - skills for a different account
    // - skills not live at the given time
    // Need to test:
    // - excludes skills before the specified position
    SkillInQueue existing;
    Map<Integer, SkillInQueue> listCheck = new HashMap<Integer, SkillInQueue>();

    existing = new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(queuePosition, existing);

    existing = new SkillInQueue(endSP, endTime, level, queuePosition + 1, startSP, startTime, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(queuePosition + 1, existing);

    existing = new SkillInQueue(endSP, endTime, level, queuePosition + 2, startSP, startTime, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(queuePosition + 2, existing);

    existing = new SkillInQueue(endSP, endTime, level, queuePosition + 3, startSP, startTime, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(queuePosition + 3, existing);

    // Associated with different account
    existing = new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime, typeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new SkillInQueue(endSP, endTime, level, queuePosition + 4, startSP, startTime, typeID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new SkillInQueue(endSP, endTime, level, queuePosition + 5, startSP, startTime, typeID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify proper part of skill queue is returned
    List<SkillInQueue> result = SkillInQueue.getAtOrAfterPosition(testAccount, 8888L, queuePosition + 2);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(queuePosition + 2), result.get(0));
    Assert.assertEquals(listCheck.get(queuePosition + 3), result.get(1));
  }

}
