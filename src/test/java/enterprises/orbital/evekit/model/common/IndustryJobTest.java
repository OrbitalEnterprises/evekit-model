package enterprises.orbital.evekit.model.common;

import java.math.BigDecimal;
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

public class IndustryJobTest extends AbstractModelTester<IndustryJob> {

  private final long                           jobID                = TestBase.getRandomInt(100000000);
  private final long                           installerID          = TestBase.getRandomInt(100000000);
  private final String                         installerName        = "test installer name";
  private final long                           facilityID           = TestBase.getRandomInt(100000000);
  private final int                            solarSystemID        = TestBase.getRandomInt(100000000);
  private final String                         solarSystemName      = "test solar system name";
  private final long                           stationID            = TestBase.getRandomInt(100000000);
  private final int                            activityID           = TestBase.getRandomInt(100000000);
  private final long                           blueprintID          = TestBase.getRandomInt(100000000);
  private final int                            blueprintTypeID      = TestBase.getRandomInt(100000000);
  private final String                         blueprintTypeName    = "test blueprint type name";
  private final long                           blueprintLocationID  = TestBase.getRandomInt(100000000);
  private final long                           outputLocationID     = TestBase.getRandomInt(100000000);
  private final int                            runs                 = TestBase.getRandomInt(100000000);
  private final BigDecimal                     cost                 = TestBase.getRandomBigDecimal(100000000);
  private final long                           teamID               = TestBase.getRandomInt(100000000);
  private final int                            licensedRuns         = TestBase.getRandomInt(100000000);
  private final double                         probability          = TestBase.getRandomDouble(1000);
  private final int                            productTypeID        = TestBase.getRandomInt(100000000);
  private final String                         productTypeName      = "test product type name";
  private final int                            status               = TestBase.getRandomInt(100000000);
  private final int                            timeInSeconds        = TestBase.getRandomInt(100000000);
  private final long                           startDate            = TestBase.getRandomInt(100000000);
  private final long                           endDate              = TestBase.getRandomInt(100000000);
  private final long                           pauseDate            = TestBase.getRandomInt(100000000);
  private final long                           completedDate        = TestBase.getRandomInt(100000000);
  private final long                           completedCharacterID = TestBase.getRandomInt(100000000);
  private final int                            successfulRuns       = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<IndustryJob> eol                  = new ClassUnderTestConstructor<IndustryJob>() {

                                                                      @Override
                                                                      public IndustryJob getCUT() {
                                                                        return new IndustryJob(
                                                                            jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName,
                                                                            stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
                                                                            blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns,
                                                                            probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
                                                                            endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
                                                                      }

                                                                    };

  final ClassUnderTestConstructor<IndustryJob> live                 = new ClassUnderTestConstructor<IndustryJob>() {
                                                                      @Override
                                                                      public IndustryJob getCUT() {
                                                                        return new IndustryJob(
                                                                            jobID, installerID + 1, installerName, facilityID, solarSystemID, solarSystemName,
                                                                            stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
                                                                            blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns,
                                                                            probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
                                                                            endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
                                                                      }

                                                                    };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<IndustryJob>() {

      @Override
      public IndustryJob[] getVariants() {
        return new IndustryJob[] {
            new IndustryJob(
                jobID + 1, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID + 1, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName + " 1", facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID + 1, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID + 1, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName + " 1", stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID + 1, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID + 1, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID + 1, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID + 1,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName + " 1", blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID + 1, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID + 1, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs + 1, cost, teamID, licensedRuns, probability, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost.add(BigDecimal.TEN), teamID, licensedRuns, probability, productTypeID,
                productTypeName, status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID + 1, licensedRuns, probability, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns + 1, probability, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability + 1, productTypeID, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID + 1, productTypeName,
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName + " 1",
                status, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName,
                status + 1, timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds + 1, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate + 1, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate + 1, pauseDate, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate + 1, completedDate, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate + 1, completedCharacterID, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID + 1, successfulRuns),
            new IndustryJob(
                jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
                blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
                timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns + 1),
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<IndustryJob>() {

      @Override
      public IndustryJob getModel(
                                  SynchronizedEveAccount account,
                                  long time) {
        return IndustryJob.get(account, time, jobID);
      }

    });
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - jobs for a different account
    // - jobs not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    IndustryJob existing;
    Map<Long, IndustryJob> listCheck = new HashMap<Long, IndustryJob>();

    existing = new IndustryJob(
        jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
        blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
        endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID, existing);

    existing = new IndustryJob(
        jobID + 10, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 10, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 10, existing);

    existing = new IndustryJob(
        jobID + 20, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 20, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 20, existing);

    existing = new IndustryJob(
        jobID + 30, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 30, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 30, existing);

    // Associated with different account
    existing = new IndustryJob(
        jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
        blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
        endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new IndustryJob(
        jobID + 5, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new IndustryJob(
        jobID + 3, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<IndustryJob> result = IndustryJob.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (IndustryJob next : result) {
      long jobID = next.getJobID();
      Assert.assertTrue(listCheck.containsKey(jobID));
      Assert.assertEquals(listCheck.get(jobID), next);
    }

    // Verify limited set returned
    result = IndustryJob.getAllForward(testAccount, 8888L, 2, startDate - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(jobID), result.get(0));
    Assert.assertEquals(listCheck.get(jobID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = IndustryJob.getAllForward(testAccount, 8888L, 100, startDate + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(jobID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(jobID + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - jobs for a different account
    // - jobs not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    IndustryJob existing;
    Map<Long, IndustryJob> listCheck = new HashMap<Long, IndustryJob>();

    existing = new IndustryJob(
        jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
        blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
        endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID, existing);

    existing = new IndustryJob(
        jobID + 10, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 10, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 10, existing);

    existing = new IndustryJob(
        jobID + 20, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 20, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 20, existing);

    existing = new IndustryJob(
        jobID + 30, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 30, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 30, existing);

    // Associated with different account
    existing = new IndustryJob(
        jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
        blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
        endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new IndustryJob(
        jobID + 5, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new IndustryJob(
        jobID + 3, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<IndustryJob> result = IndustryJob.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (IndustryJob next : result) {
      long jobID = next.getJobID();
      Assert.assertTrue(listCheck.containsKey(jobID));
      Assert.assertEquals(listCheck.get(jobID), next);
    }

    // Verify limited set returned
    result = IndustryJob.getAllBackward(testAccount, 8888L, 2, startDate + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(jobID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(jobID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = IndustryJob.getAllBackward(testAccount, 8888L, 100, startDate + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(jobID + 10), result.get(0));
    Assert.assertEquals(listCheck.get(jobID), result.get(1));

  }

  @Test
  public void testGetAllIncomplete() throws Exception {
    // Should exclude:
    // - jobs for a different account
    // - jobs not live at the given time
    // - complete jobs
    // Need to test:
    // - max results limitation
    // - continuation ID

    IndustryJob existing;
    Map<Long, IndustryJob> listCheck = new HashMap<Long, IndustryJob>();

    existing = new IndustryJob(
        jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
        blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
        endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID, existing);

    existing = new IndustryJob(
        jobID + 10, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 10, endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 10, existing);

    existing = new IndustryJob(
        jobID + 20, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 20, endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 20, existing);

    existing = new IndustryJob(
        jobID + 30, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate + 30, endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jobID + 30, existing);

    // Associated with different account
    existing = new IndustryJob(
        jobID, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID, blueprintTypeName,
        blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status, timeInSeconds, startDate,
        endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Complete
    existing = new IndustryJob(
        jobID + 40, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, completedDate + 1, completedCharacterID, successfulRuns);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new IndustryJob(
        jobID + 5, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new IndustryJob(
        jobID + 3, installerID, installerName, facilityID, solarSystemID, solarSystemName, stationID, activityID, blueprintID, blueprintTypeID,
        blueprintTypeName, blueprintLocationID, outputLocationID, runs, cost, teamID, licensedRuns, probability, productTypeID, productTypeName, status,
        timeInSeconds, startDate, endDate, pauseDate, 0, completedCharacterID, successfulRuns);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<IndustryJob> result = IndustryJob.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (IndustryJob next : result) {
      long jobID = next.getJobID();
      Assert.assertTrue(listCheck.containsKey(jobID));
      Assert.assertEquals(listCheck.get(jobID), next);
    }

    // Verify limited set returned
    result = IndustryJob.getAllForward(testAccount, 8888L, 2, startDate - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(jobID), result.get(0));
    Assert.assertEquals(listCheck.get(jobID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = IndustryJob.getAllForward(testAccount, 8888L, 100, startDate + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(jobID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(jobID + 30), result.get(1));

  }

}
