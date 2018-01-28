package enterprises.orbital.evekit.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_industry_job",
    indexes = {
        @Index(
            name = "jobIDIndex",
            columnList = "jobID"),
        @Index(
            name = "startDateIndex",
            columnList = "startDate"),
        @Index(
            name = "completedDateIndex",
            columnList = "completedDate"),
    })
@NamedQueries({
    @NamedQuery(
        name = "IndustryJob.getByJobID",
        query = "SELECT c FROM IndustryJob c where c.owner = :owner and c.jobID = :jobid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "IndustryJob.getByStartDateForward",
        query = "SELECT c FROM IndustryJob c where c.owner = :owner and c.startDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.startDate asc"),
    @NamedQuery(
        name = "IndustryJob.getByStartDateBackward",
        query = "SELECT c FROM IndustryJob c where c.owner = :owner and c.startDate < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.startDate desc"),
})
// 1 hour cache time - API caches for 15 minutes
public class IndustryJob extends CachedData {
  private static final Logger log = Logger.getLogger(IndustryJob.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS);
  private int jobID;
  private int installerID;
  private long facilityID;
  private long stationID;
  private int activityID;
  private long blueprintID;
  private int blueprintTypeID;
  private long blueprintLocationID;
  private long outputLocationID;
  private int runs;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal cost;
  private int licensedRuns;
  private float probability;
  private int productTypeID;
  private String status;
  private int timeInSeconds;
  private long startDate = -1;
  private long endDate = -1;
  private long pauseDate = -1;
  private long completedDate = -1;
  private int completedCharacterID;
  private int successfulRuns;
  @Transient
  @ApiModelProperty(
      value = "startDate Date")
  @JsonProperty("startDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date startDateDate;
  @Transient
  @ApiModelProperty(
      value = "endDate Date")
  @JsonProperty("endDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date endDateDate;
  @Transient
  @ApiModelProperty(
      value = "pauseDate Date")
  @JsonProperty("pauseDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date pauseDateDate;
  @Transient
  @ApiModelProperty(
      value = "completedDate Date")
  @JsonProperty("completedDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date completedDateDate;

  @SuppressWarnings("unused")
  protected IndustryJob() {}

  public IndustryJob(int jobID, int installerID, long facilityID, long stationID,
                     int activityID, long blueprintID, int blueprintTypeID, long blueprintLocationID,
                     long outputLocationID, int runs,
                     BigDecimal cost, int licensedRuns, float probability, int productTypeID, String status,
                     int timeInSeconds, long startDate, long endDate, long pauseDate, long completedDate,
                     int completedCharacterID, int successfulRuns) {
    super();
    this.jobID = jobID;
    this.installerID = installerID;
    this.facilityID = facilityID;
    this.stationID = stationID;
    this.activityID = activityID;
    this.blueprintID = blueprintID;
    this.blueprintTypeID = blueprintTypeID;
    this.blueprintLocationID = blueprintLocationID;
    this.outputLocationID = outputLocationID;
    this.runs = runs;
    this.cost = cost;
    this.licensedRuns = licensedRuns;
    this.probability = probability;
    this.productTypeID = productTypeID;
    this.status = status;
    this.timeInSeconds = timeInSeconds;
    this.startDate = startDate;
    this.endDate = endDate;
    this.pauseDate = pauseDate;
    this.completedDate = completedDate;
    this.completedCharacterID = completedCharacterID;
    this.successfulRuns = successfulRuns;
  }

  /**
   * Update transient date values for readability.
   */
  @SuppressWarnings("Duplicates")
  @Override
  public void prepareTransient() {
    fixDates();
    startDateDate = assignDateField(startDate);
    endDateDate = assignDateField(endDate);
    pauseDateDate = assignDateField(pauseDate);
    completedDateDate = assignDateField(completedDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof IndustryJob)) return false;
    IndustryJob other = (IndustryJob) sup;
    return jobID == other.jobID && installerID == other.installerID && facilityID == other.facilityID
        && stationID == other.stationID && activityID == other.activityID && blueprintID == other.blueprintID
        && blueprintTypeID == other.blueprintTypeID && blueprintLocationID == other.blueprintLocationID
        && outputLocationID == other.outputLocationID && runs == other.runs && nullSafeObjectCompare(cost, other.cost)
        && licensedRuns == other.licensedRuns && probability == other.probability && productTypeID == other.productTypeID
        && nullSafeObjectCompare(status, other.status) && timeInSeconds == other.timeInSeconds
        && startDate == other.startDate && endDate == other.endDate && pauseDate == other.pauseDate && completedDate == other.completedDate
        && completedCharacterID == other.completedCharacterID && successfulRuns == other.successfulRuns;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getJobID() {
    return jobID;
  }

  public int getInstallerID() {
    return installerID;
  }

  public long getFacilityID() {
    return facilityID;
  }

  public long getStationID() {
    return stationID;
  }

  public int getActivityID() {
    return activityID;
  }

  public long getBlueprintID() {
    return blueprintID;
  }

  public int getBlueprintTypeID() {
    return blueprintTypeID;
  }

  public long getBlueprintLocationID() {
    return blueprintLocationID;
  }

  public long getOutputLocationID() {
    return outputLocationID;
  }

  public int getRuns() {
    return runs;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public int getLicensedRuns() {
    return licensedRuns;
  }

  public double getProbability() {
    return probability;
  }

  public int getProductTypeID() {
    return productTypeID;
  }

  public String getStatus() {
    return status;
  }

  public int getTimeInSeconds() {
    return timeInSeconds;
  }

  public long getStartDate() {
    return startDate;
  }

  public long getEndDate() {
    return endDate;
  }

  public long getPauseDate() {
    return pauseDate;
  }

  public long getCompletedDate() {
    return completedDate;
  }

  public int getCompletedCharacterID() {
    return completedCharacterID;
  }

  public int getSuccessfulRuns() {
    return successfulRuns;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    IndustryJob that = (IndustryJob) o;
    return jobID == that.jobID &&
        installerID == that.installerID &&
        facilityID == that.facilityID &&
        stationID == that.stationID &&
        activityID == that.activityID &&
        blueprintID == that.blueprintID &&
        blueprintTypeID == that.blueprintTypeID &&
        blueprintLocationID == that.blueprintLocationID &&
        outputLocationID == that.outputLocationID &&
        runs == that.runs &&
        licensedRuns == that.licensedRuns &&
        Double.compare(that.probability, probability) == 0 &&
        productTypeID == that.productTypeID &&
        timeInSeconds == that.timeInSeconds &&
        startDate == that.startDate &&
        endDate == that.endDate &&
        pauseDate == that.pauseDate &&
        completedDate == that.completedDate &&
        completedCharacterID == that.completedCharacterID &&
        successfulRuns == that.successfulRuns &&
        Objects.equals(cost, that.cost) &&
        Objects.equals(status, that.status) &&
        Objects.equals(startDateDate, that.startDateDate) &&
        Objects.equals(endDateDate, that.endDateDate) &&
        Objects.equals(pauseDateDate, that.pauseDateDate) &&
        Objects.equals(completedDateDate, that.completedDateDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), jobID, installerID, facilityID, stationID, activityID, blueprintID,
                        blueprintTypeID, blueprintLocationID, outputLocationID, runs, cost, licensedRuns, probability,
                        productTypeID, status, timeInSeconds, startDate, endDate, pauseDate, completedDate,
                        completedCharacterID, successfulRuns, startDateDate, endDateDate, pauseDateDate,
                        completedDateDate);
  }

  @Override
  public String toString() {
    return "IndustryJob{" +
        "jobID=" + jobID +
        ", installerID=" + installerID +
        ", facilityID=" + facilityID +
        ", stationID=" + stationID +
        ", activityID=" + activityID +
        ", blueprintID=" + blueprintID +
        ", blueprintTypeID=" + blueprintTypeID +
        ", blueprintLocationID=" + blueprintLocationID +
        ", outputLocationID=" + outputLocationID +
        ", runs=" + runs +
        ", cost=" + cost +
        ", licensedRuns=" + licensedRuns +
        ", probability=" + probability +
        ", productTypeID=" + productTypeID +
        ", status='" + status + '\'' +
        ", timeInSeconds=" + timeInSeconds +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", pauseDate=" + pauseDate +
        ", completedDate=" + completedDate +
        ", completedCharacterID=" + completedCharacterID +
        ", successfulRuns=" + successfulRuns +
        ", startDateDate=" + startDateDate +
        ", endDateDate=" + endDateDate +
        ", pauseDateDate=" + pauseDateDate +
        ", completedDateDate=" + completedDateDate +
        '}';
  }

  /**
   * Retrieve industry job with the given job ID live at the given time, or null if no such job exists.
   *
   * @param owner industry job owner
   * @param time  time at which job must be live
   * @param jobID job ID
   * @return industry job with the given ID live at the given time, or null
   */
  public static IndustryJob get(
      final SynchronizedEveAccount owner,
      final long time,
      final int jobID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<IndustryJob> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "IndustryJob.getByJobID",
                                                                                                      IndustryJob.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("jobid", jobID);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<IndustryJob> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector jobID,
      final AttributeSelector installerID,
      final AttributeSelector facilityID,
      final AttributeSelector stationID,
      final AttributeSelector activityID,
      final AttributeSelector blueprintID,
      final AttributeSelector blueprintTypeID,
      final AttributeSelector blueprintLocationID,
      final AttributeSelector outputLocationID,
      final AttributeSelector runs,
      final AttributeSelector cost,
      final AttributeSelector licensedRuns,
      final AttributeSelector probability,
      final AttributeSelector productTypeID,
      final AttributeSelector status,
      final AttributeSelector timeInSeconds,
      final AttributeSelector startDate,
      final AttributeSelector endDate,
      final AttributeSelector pauseDate,
      final AttributeSelector completedDate,
      final AttributeSelector completedCharacterID,
      final AttributeSelector successfulRuns) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM IndustryJob c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "jobID", jobID);
                                        AttributeSelector.addIntSelector(qs, "c", "installerID", installerID);
                                        AttributeSelector.addLongSelector(qs, "c", "facilityID", facilityID);
                                        AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
                                        AttributeSelector.addIntSelector(qs, "c", "activityID", activityID);
                                        AttributeSelector.addLongSelector(qs, "c", "blueprintID", blueprintID);
                                        AttributeSelector.addIntSelector(qs, "c", "blueprintTypeID", blueprintTypeID);
                                        AttributeSelector.addLongSelector(qs, "c", "blueprintLocationID",
                                                                          blueprintLocationID);
                                        AttributeSelector.addLongSelector(qs, "c", "outputLocationID",
                                                                          outputLocationID);
                                        AttributeSelector.addIntSelector(qs, "c", "runs", runs);
                                        AttributeSelector.addDoubleSelector(qs, "c", "cost", cost);
                                        AttributeSelector.addIntSelector(qs, "c", "licensedRuns", licensedRuns);
                                        AttributeSelector.addDoubleSelector(qs, "c", "probability", probability);
                                        AttributeSelector.addIntSelector(qs, "c", "productTypeID", productTypeID);
                                        AttributeSelector.addIntSelector(qs, "c", "status", status);
                                        AttributeSelector.addIntSelector(qs, "c", "timeInSeconds", timeInSeconds);
                                        AttributeSelector.addLongSelector(qs, "c", "startDate", startDate);
                                        AttributeSelector.addLongSelector(qs, "c", "endDate", endDate);
                                        AttributeSelector.addLongSelector(qs, "c", "pauseDate", pauseDate);
                                        AttributeSelector.addLongSelector(qs, "c", "completedDate", completedDate);
                                        AttributeSelector.addIntSelector(qs, "c", "completedCharacterID",
                                                                          completedCharacterID);
                                        AttributeSelector.addIntSelector(qs, "c", "successfulRuns", successfulRuns);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<IndustryJob> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     IndustryJob.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
