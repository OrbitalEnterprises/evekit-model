package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndustryJobTest extends AbstractModelTester<IndustryJob> {

  private final int jobID = TestBase.getRandomInt(100000000);
  private final int installerID = TestBase.getRandomInt(100000000);
  private final long facilityID = TestBase.getRandomInt(100000000);
  private final long stationID = TestBase.getRandomInt(100000000);
  private final int activityID = TestBase.getRandomInt(100000000);
  private final long blueprintID = TestBase.getRandomInt(100000000);
  private final int blueprintTypeID = TestBase.getRandomInt(100000000);
  private final long blueprintLocationID = TestBase.getRandomInt(100000000);
  private final long outputLocationID = TestBase.getRandomInt(100000000);
  private final int runs = TestBase.getRandomInt(100000000);
  private final BigDecimal cost = TestBase.getRandomBigDecimal(100000000);
  private final int licensedRuns = TestBase.getRandomInt(100000000);
  private final float probability = (float) TestBase.getRandomDouble(1000);
  private final int productTypeID = TestBase.getRandomInt(100000000);
  private final String status = TestBase.getRandomText(50);
  private final int timeInSeconds = TestBase.getRandomInt(100000000);
  private final long startDate = TestBase.getRandomInt(100000000);
  private final long endDate = TestBase.getRandomInt(100000000);
  private final long pauseDate = TestBase.getRandomInt(100000000);
  private final long completedDate = TestBase.getRandomInt(100000000);
  private final int completedCharacterID = TestBase.getRandomInt(100000000);
  private final int successfulRuns = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<IndustryJob> eol = () -> new IndustryJob(
      jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
      blueprintLocationID, outputLocationID, runs, cost, licensedRuns,
      probability, productTypeID, status, timeInSeconds, startDate,
      endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);

  final ClassUnderTestConstructor<IndustryJob> live = () -> new IndustryJob(
      jobID, installerID + 1, facilityID,
      stationID, activityID, blueprintID, blueprintTypeID,
      blueprintLocationID, outputLocationID, runs, cost, licensedRuns,
      probability, productTypeID, status, timeInSeconds, startDate,
      endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new IndustryJob[]{
        new IndustryJob(
            jobID + 1, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID + 1, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID + 1, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID + 1, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID + 1, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID + 1, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID + 1,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID + 1, outputLocationID, runs, cost, licensedRuns, probability, productTypeID,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID + 1, runs, cost, licensedRuns, probability, productTypeID,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs + 1, cost, licensedRuns, probability, productTypeID,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost.add(BigDecimal.TEN), licensedRuns, probability,
            productTypeID,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns + 1, probability, productTypeID,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability + 1, productTypeID,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID + 1,
            status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID,
            status + 1, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID,
            successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds + 1, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate + 1, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate + 1, pauseDate, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate + 1, completedDate, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate + 1, completedCharacterID, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID + 1, successfulRuns),
        new IndustryJob(
            jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
            blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
            timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> IndustryJob.get(account, time, jobID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - jobs for a different account
    // - jobs not live at the given time
    IndustryJob existing;
    Map<Integer, IndustryJob> listCheck = new HashMap<>();

    existing = new IndustryJob(
        jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate,
        endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID, existing);

    existing = new IndustryJob(
        jobID + 10, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate + 10, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 10, existing);

    existing = new IndustryJob(
        jobID + 20, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate + 20, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 20, existing);

    existing = new IndustryJob(
        jobID + 30, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate + 30, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 30, existing);

    // Associated with different account
    existing = new IndustryJob(
        jobID, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate,
        endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new IndustryJob(
        jobID + 5, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new IndustryJob(
        jobID + 3, installerID, facilityID, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability, productTypeID, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobs are returned
    List<IndustryJob> result = CachedData.retrieveAll(8888L, (long contid, AttributeSelector at) ->
        IndustryJob.accessQuery(testAccount, contid, 1000, false, at, AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any(), AttributeSelector.any(),
                                AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (IndustryJob next : result) {
      int jobID = next.getJobID();
      Assert.assertTrue(listCheck.containsKey(jobID));
      Assert.assertEquals(listCheck.get(jobID), next);
    }
  }


}
