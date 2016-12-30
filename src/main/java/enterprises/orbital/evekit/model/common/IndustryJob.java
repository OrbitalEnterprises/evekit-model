package enterprises.orbital.evekit.model.common;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_industry_job",
    indexes = {
        @Index(
            name = "jobIDIndex",
            columnList = "jobID",
            unique = false),
        @Index(
            name = "startDateIndex",
            columnList = "startDate",
            unique = false),
        @Index(
            name = "completedDateIndex",
            columnList = "completedDate",
            unique = false),
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
    @NamedQuery(
        name = "IndustryJob.getIncomplete",
        query = "SELECT c FROM IndustryJob c where c.owner = :owner and c.completedDate = -1 and c.startDate > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.startDate asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class IndustryJob extends CachedData {
  private static final Logger log                 = Logger.getLogger(IndustryJob.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_INDUSTRY_JOBS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                jobID;
  private long                installerID;
  private String              installerName;
  private long                facilityID;
  private int                 solarSystemID;
  private String              solarSystemName;
  private long                stationID;
  private int                 activityID;
  private long                blueprintID;
  private int                 blueprintTypeID;
  private String              blueprintTypeName;
  private long                blueprintLocationID;
  private long                outputLocationID;
  private int                 runs;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          cost;
  private long                teamID;
  private int                 licensedRuns;
  private double              probability;
  private int                 productTypeID;
  private String              productTypeName;
  private int                 status;
  private int                 timeInSeconds;
  private long                startDate           = -1;
  private long                endDate             = -1;
  private long                pauseDate           = -1;
  private long                completedDate       = -1;
  private long                completedCharacterID;
  private int                 successfulRuns;
  @Transient
  @ApiModelProperty(
      value = "startDate Date")
  @JsonProperty("startDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                startDateDate;
  @Transient
  @ApiModelProperty(
      value = "endDate Date")
  @JsonProperty("endDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                endDateDate;
  @Transient
  @ApiModelProperty(
      value = "pauseDate Date")
  @JsonProperty("pauseDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                pauseDateDate;
  @Transient
  @ApiModelProperty(
      value = "completedDate Date")
  @JsonProperty("completedDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                completedDateDate;

  @SuppressWarnings("unused")
  private IndustryJob() {}

  public IndustryJob(long jobID, long installerID, String installerName, long facilityID, int solarSystemID, String solarSystemName, long stationID,
                     int activityID, long blueprintID, int blueprintTypeID, String blueprintTypeName, long blueprintLocationID, long outputLocationID, int runs,
                     BigDecimal cost, long teamID, int licensedRuns, double probability, int productTypeID, String productTypeName, int status,
                     int timeInSeconds, long startDate, long endDate, long pauseDate, long completedDate, long completedCharacterID, int successfulRuns) {
    super();
    this.jobID = jobID;
    this.installerID = installerID;
    this.installerName = installerName;
    this.facilityID = facilityID;
    this.solarSystemID = solarSystemID;
    this.solarSystemName = solarSystemName;
    this.stationID = stationID;
    this.activityID = activityID;
    this.blueprintID = blueprintID;
    this.blueprintTypeID = blueprintTypeID;
    this.blueprintTypeName = blueprintTypeName;
    this.blueprintLocationID = blueprintLocationID;
    this.outputLocationID = outputLocationID;
    this.runs = runs;
    this.cost = cost;
    this.teamID = teamID;
    this.licensedRuns = licensedRuns;
    this.probability = probability;
    this.productTypeID = productTypeID;
    this.productTypeName = productTypeName;
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
  @Override
  public void prepareDates() {
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
    return jobID == other.jobID && installerID == other.installerID && nullSafeObjectCompare(installerName, other.installerName)
        && facilityID == other.facilityID && solarSystemID == other.solarSystemID && nullSafeObjectCompare(solarSystemName, other.solarSystemName)
        && stationID == other.stationID && activityID == other.activityID && blueprintID == other.blueprintID && blueprintTypeID == other.blueprintTypeID
        && nullSafeObjectCompare(blueprintTypeName, other.blueprintTypeName) && blueprintLocationID == other.blueprintLocationID
        && outputLocationID == other.outputLocationID && runs == other.runs && nullSafeObjectCompare(cost, other.cost) && teamID == other.teamID
        && licensedRuns == other.licensedRuns && probability == other.probability && productTypeID == other.productTypeID
        && nullSafeObjectCompare(productTypeName, other.productTypeName) && status == other.status && timeInSeconds == other.timeInSeconds
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

  public long getJobID() {
    return jobID;
  }

  public long getInstallerID() {
    return installerID;
  }

  public String getInstallerName() {
    return installerName;
  }

  public long getFacilityID() {
    return facilityID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getSolarSystemName() {
    return solarSystemName;
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

  public String getBlueprintTypeName() {
    return blueprintTypeName;
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

  public long getTeamID() {
    return teamID;
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

  public String getProductTypeName() {
    return productTypeName;
  }

  public int getStatus() {
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

  public long getCompletedCharacterID() {
    return completedCharacterID;
  }

  public int getSuccessfulRuns() {
    return successfulRuns;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + activityID;
    result = prime * result + (int) (blueprintID ^ (blueprintID >>> 32));
    result = prime * result + (int) (blueprintLocationID ^ (blueprintLocationID >>> 32));
    result = prime * result + blueprintTypeID;
    result = prime * result + ((blueprintTypeName == null) ? 0 : blueprintTypeName.hashCode());
    result = prime * result + (int) (completedCharacterID ^ (completedCharacterID >>> 32));
    result = prime * result + (int) (completedDate ^ (completedDate >>> 32));
    result = prime * result + ((cost == null) ? 0 : cost.hashCode());
    result = prime * result + (int) (endDate ^ (endDate >>> 32));
    result = prime * result + (int) (facilityID ^ (facilityID >>> 32));
    result = prime * result + (int) (installerID ^ (installerID >>> 32));
    result = prime * result + ((installerName == null) ? 0 : installerName.hashCode());
    result = prime * result + (int) (jobID ^ (jobID >>> 32));
    result = prime * result + licensedRuns;
    result = prime * result + (int) (outputLocationID ^ (outputLocationID >>> 32));
    result = prime * result + (int) (pauseDate ^ (pauseDate >>> 32));
    long temp;
    temp = Double.doubleToLongBits(probability);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + productTypeID;
    result = prime * result + ((productTypeName == null) ? 0 : productTypeName.hashCode());
    result = prime * result + runs;
    result = prime * result + solarSystemID;
    result = prime * result + ((solarSystemName == null) ? 0 : solarSystemName.hashCode());
    result = prime * result + (int) (startDate ^ (startDate >>> 32));
    result = prime * result + (int) (stationID ^ (stationID >>> 32));
    result = prime * result + status;
    result = prime * result + successfulRuns;
    result = prime * result + (int) (teamID ^ (teamID >>> 32));
    result = prime * result + timeInSeconds;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    IndustryJob other = (IndustryJob) obj;
    if (activityID != other.activityID) return false;
    if (blueprintID != other.blueprintID) return false;
    if (blueprintLocationID != other.blueprintLocationID) return false;
    if (blueprintTypeID != other.blueprintTypeID) return false;
    if (blueprintTypeName == null) {
      if (other.blueprintTypeName != null) return false;
    } else if (!blueprintTypeName.equals(other.blueprintTypeName)) return false;
    if (completedCharacterID != other.completedCharacterID) return false;
    if (completedDate != other.completedDate) return false;
    if (cost == null) {
      if (other.cost != null) return false;
    } else if (!cost.equals(other.cost)) return false;
    if (endDate != other.endDate) return false;
    if (facilityID != other.facilityID) return false;
    if (installerID != other.installerID) return false;
    if (installerName == null) {
      if (other.installerName != null) return false;
    } else if (!installerName.equals(other.installerName)) return false;
    if (jobID != other.jobID) return false;
    if (licensedRuns != other.licensedRuns) return false;
    if (outputLocationID != other.outputLocationID) return false;
    if (pauseDate != other.pauseDate) return false;
    if (Double.doubleToLongBits(probability) != Double.doubleToLongBits(other.probability)) return false;
    if (productTypeID != other.productTypeID) return false;
    if (productTypeName == null) {
      if (other.productTypeName != null) return false;
    } else if (!productTypeName.equals(other.productTypeName)) return false;
    if (runs != other.runs) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (solarSystemName == null) {
      if (other.solarSystemName != null) return false;
    } else if (!solarSystemName.equals(other.solarSystemName)) return false;
    if (startDate != other.startDate) return false;
    if (stationID != other.stationID) return false;
    if (status != other.status) return false;
    if (successfulRuns != other.successfulRuns) return false;
    if (teamID != other.teamID) return false;
    if (timeInSeconds != other.timeInSeconds) return false;
    return true;
  }

  @Override
  public String toString() {
    return "IndustryJob [jobID=" + jobID + ", installerID=" + installerID + ", installerName=" + installerName + ", facilityID=" + facilityID
        + ", solarSystemID=" + solarSystemID + ", solarSystemName=" + solarSystemName + ", stationID=" + stationID + ", activityID=" + activityID
        + ", blueprintID=" + blueprintID + ", blueprintTypeID=" + blueprintTypeID + ", blueprintTypeName=" + blueprintTypeName + ", blueprintLocationID="
        + blueprintLocationID + ", outputLocationID=" + outputLocationID + ", runs=" + runs + ", cost=" + cost + ", teamID=" + teamID + ", licensedRuns="
        + licensedRuns + ", probability=" + probability + ", productTypeID=" + productTypeID + ", productTypeName=" + productTypeName + ", status=" + status
        + ", timeInSeconds=" + timeInSeconds + ", startDate=" + startDate + ", endDate=" + endDate + ", pauseDate=" + pauseDate + ", completedDate="
        + completedDate + ", completedCharacterID=" + completedCharacterID + ", successfulRuns=" + successfulRuns + ", owner=" + owner + ", lifeStart="
        + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve industry job with the given job ID live at the given time, or null if no such job exists.
   * 
   * @param owner
   *          industry job owner
   * @param time
   *          time at which job must be live
   * @param jobID
   *          job ID
   * @return industry job with the given ID live at the given time, or null
   */
  public static IndustryJob get(
                                final SynchronizedEveAccount owner,
                                final long time,
                                final long jobID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<IndustryJob>() {
        @Override
        public IndustryJob run() throws Exception {
          TypedQuery<IndustryJob> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("IndustryJob.getByJobID",
                                                                                                                      IndustryJob.class);
          getter.setParameter("owner", owner);
          getter.setParameter("jobid", jobID);
          getter.setParameter("point", time);
          try {
            return getter.getSingleResult();
          } catch (NoResultException e) {
            return null;
          }
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  /**
   * List industry jobs live at the given time with startDate greater than "contid".
   * 
   * @param owner
   *          industry jobs owner
   * @param time
   *          time at which industry jobs must be live
   * @param maxresults
   *          maximum number of industry jobs to retrieve
   * @param contid
   *          startDate (exclusive) from which to start returning industry jobs
   * @return list of industry jobs live at the given time with startDate greater than "contid"
   */
  public static List<IndustryJob> getAllForward(
                                                final SynchronizedEveAccount owner,
                                                final long time,
                                                int maxresults,
                                                final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(IndustryJob.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<IndustryJob>>() {
        @Override
        public List<IndustryJob> run() throws Exception {
          TypedQuery<IndustryJob> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("IndustryJob.getByStartDateForward",
                                                                                                                      IndustryJob.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  /**
   * List industry jobs live at the given time with startDate less than "contid".
   * 
   * @param owner
   *          industry jobs owner
   * @param time
   *          time at which industry jobs must be live
   * @param maxresults
   *          maximum number of industry jobs to retrieve
   * @param contid
   *          startDate (exclusive) before which industry jobs will be returned
   * @return list of industry jobs live at the given time with startDate less than "contid"
   */
  public static List<IndustryJob> getAllBackward(
                                                 final SynchronizedEveAccount owner,
                                                 final long time,
                                                 int maxresults,
                                                 final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(IndustryJob.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<IndustryJob>>() {
        @Override
        public List<IndustryJob> run() throws Exception {
          TypedQuery<IndustryJob> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("IndustryJob.getByStartDateBackward",
                                                                                                                      IndustryJob.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  /**
   * List incomplete industry jobs live at the given time with startDate greater than "contid".
   * 
   * @param owner
   *          industry jobs owner
   * @param time
   *          time at which industry jobs must be live
   * @param maxresults
   *          maximum number of industry jobs to retrieve
   * @param contid
   *          startDate (exclusive) after which industry jobs will be returned
   * @return list of incomplete industry jobs live at the given time with startDate greater than "contid"
   */
  public static List<IndustryJob> getAllIncomplete(
                                                   final SynchronizedEveAccount owner,
                                                   final long time,
                                                   int maxresults,
                                                   final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(IndustryJob.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<IndustryJob>>() {
        @Override
        public List<IndustryJob> run() throws Exception {
          TypedQuery<IndustryJob> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("IndustryJob.getIncomplete",
                                                                                                                      IndustryJob.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<IndustryJob> accessQuery(
                                              final SynchronizedEveAccount owner,
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector jobID,
                                              final AttributeSelector installerID,
                                              final AttributeSelector installerName,
                                              final AttributeSelector facilityID,
                                              final AttributeSelector solarSystemID,
                                              final AttributeSelector solarSystemName,
                                              final AttributeSelector stationID,
                                              final AttributeSelector activityID,
                                              final AttributeSelector blueprintID,
                                              final AttributeSelector blueprintTypeID,
                                              final AttributeSelector blueprintTypeName,
                                              final AttributeSelector blueprintLocationID,
                                              final AttributeSelector outputLocationID,
                                              final AttributeSelector runs,
                                              final AttributeSelector cost,
                                              final AttributeSelector teamID,
                                              final AttributeSelector licensedRuns,
                                              final AttributeSelector probability,
                                              final AttributeSelector productTypeID,
                                              final AttributeSelector productTypeName,
                                              final AttributeSelector status,
                                              final AttributeSelector timeInSeconds,
                                              final AttributeSelector startDate,
                                              final AttributeSelector endDate,
                                              final AttributeSelector pauseDate,
                                              final AttributeSelector completedDate,
                                              final AttributeSelector completedCharacterID,
                                              final AttributeSelector successfulRuns) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<IndustryJob>>() {
        @Override
        public List<IndustryJob> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM IndustryJob c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "jobID", jobID);
          AttributeSelector.addLongSelector(qs, "c", "installerID", installerID);
          AttributeSelector.addStringSelector(qs, "c", "installerName", installerName, p);
          AttributeSelector.addLongSelector(qs, "c", "facilityID", facilityID);
          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
          AttributeSelector.addStringSelector(qs, "c", "solarSystemName", solarSystemName, p);
          AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
          AttributeSelector.addIntSelector(qs, "c", "activityID", activityID);
          AttributeSelector.addLongSelector(qs, "c", "blueprintID", blueprintID);
          AttributeSelector.addIntSelector(qs, "c", "blueprintTypeID", blueprintTypeID);
          AttributeSelector.addStringSelector(qs, "c", "blueprintTypeName", blueprintTypeName, p);
          AttributeSelector.addLongSelector(qs, "c", "blueprintLocationID", blueprintLocationID);
          AttributeSelector.addLongSelector(qs, "c", "outputLocationID", outputLocationID);
          AttributeSelector.addIntSelector(qs, "c", "runs", runs);
          AttributeSelector.addDoubleSelector(qs, "c", "cost", cost);
          AttributeSelector.addLongSelector(qs, "c", "teamID", teamID);
          AttributeSelector.addIntSelector(qs, "c", "licensedRuns", licensedRuns);
          AttributeSelector.addDoubleSelector(qs, "c", "probability", probability);
          AttributeSelector.addIntSelector(qs, "c", "productTypeID", productTypeID);
          AttributeSelector.addStringSelector(qs, "c", "productTypeName", productTypeName, p);
          AttributeSelector.addIntSelector(qs, "c", "status", status);
          AttributeSelector.addIntSelector(qs, "c", "timeInSeconds", timeInSeconds);
          AttributeSelector.addLongSelector(qs, "c", "startDate", startDate);
          AttributeSelector.addLongSelector(qs, "c", "endDate", endDate);
          AttributeSelector.addLongSelector(qs, "c", "pauseDate", pauseDate);
          AttributeSelector.addLongSelector(qs, "c", "completedDate", completedDate);
          AttributeSelector.addLongSelector(qs, "c", "completedCharacterID", completedCharacterID);
          AttributeSelector.addIntSelector(qs, "c", "successfulRuns", successfulRuns);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<IndustryJob> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), IndustryJob.class);
          query.setParameter("owner", owner);
          p.fillParams(query);
          query.setMaxResults(maxresults);
          return query.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
