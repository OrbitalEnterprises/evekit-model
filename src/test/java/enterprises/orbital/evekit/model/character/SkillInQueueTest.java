package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import org.junit.Test;

public class SkillInQueueTest extends AbstractModelTester<SkillInQueue> {
  private final int endSP = TestBase.getRandomInt(100000000);
  private final long endTime = TestBase.getRandomInt(100000000);
  private final int level = TestBase.getRandomInt(100000000);
  private final int queuePosition = TestBase.getRandomInt(100000000);
  private final int startSP = TestBase.getRandomInt(100000000);
  private final long startTime = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int trainingStartSP = TestBase.getRandomInt(10000);

  final ClassUnderTestConstructor<SkillInQueue> eol = () -> new SkillInQueue(endSP, endTime, level, queuePosition,
                                                                             startSP, startTime, typeID,
                                                                             trainingStartSP);

  final ClassUnderTestConstructor<SkillInQueue> live = () -> new SkillInQueue(endSP + 1, endTime, level, queuePosition,
                                                                              startSP, startTime, typeID,
                                                                              trainingStartSP);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new SkillInQueue[]{
        new SkillInQueue(endSP + 1, endTime, level, queuePosition, startSP, startTime, typeID, trainingStartSP),
        new SkillInQueue(endSP, endTime + 1, level, queuePosition, startSP, startTime, typeID, trainingStartSP),
        new SkillInQueue(endSP, endTime, level + 1, queuePosition, startSP, startTime, typeID, trainingStartSP),
        new SkillInQueue(endSP, endTime, level, queuePosition + 1, startSP, startTime, typeID, trainingStartSP),
        new SkillInQueue(endSP, endTime, level, queuePosition, startSP + 1, startTime, typeID, trainingStartSP),
        new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime + 1, typeID, trainingStartSP),
        new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime, typeID + 1, trainingStartSP),
        new SkillInQueue(endSP, endTime, level, queuePosition, startSP, startTime, typeID, trainingStartSP + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_SKILL_QUEUE));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> SkillInQueue.get(account, time, queuePosition));
  }

}
