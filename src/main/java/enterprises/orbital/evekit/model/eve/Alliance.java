package enterprises.orbital.evekit.model.eve;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_eve_alliance")
@NamedQueries({
    @NamedQuery(
        name = "Alliance.get",
        query = "SELECT c FROM Alliance c WHERE c.allianceID = :allianceid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class Alliance extends RefCachedData {
  private static final Logger log = Logger.getLogger(Alliance.class.getName());
  private long                allianceID;
  private long                executorCorpID;
  private int                 memberCount;
  private String              name;
  private String              shortName;
  private long                startDate;
  @Transient
  @ApiModelProperty(
      value = "startDate Date")
  @JsonProperty("startDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
  private Date                startDateDate;

  @SuppressWarnings("unused")
  private Alliance() {}

  public Alliance(long allianceID, long executorCorpID, int memberCount, String name, String shortName, long startDate) {
    super();
    this.allianceID = allianceID;
    this.executorCorpID = executorCorpID;
    this.memberCount = memberCount;
    this.name = name;
    this.shortName = shortName;
    this.startDate = startDate;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    startDateDate = assignDateField(startDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof Alliance)) return false;
    Alliance other = (Alliance) sup;
    return allianceID == other.allianceID && executorCorpID == other.executorCorpID && memberCount == other.memberCount
        && nullSafeObjectCompare(name, other.name) && nullSafeObjectCompare(shortName, other.shortName) && startDate == other.startDate;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public long getExecutorCorpID() {
    return executorCorpID;
  }

  public int getMemberCount() {
    return memberCount;
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    return shortName;
  }

  public long getStartDate() {
    return startDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + (int) (executorCorpID ^ (executorCorpID >>> 32));
    result = prime * result + memberCount;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
    result = prime * result + (int) (startDate ^ (startDate >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Alliance other = (Alliance) obj;
    if (allianceID != other.allianceID) return false;
    if (executorCorpID != other.executorCorpID) return false;
    if (memberCount != other.memberCount) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (shortName == null) {
      if (other.shortName != null) return false;
    } else if (!shortName.equals(other.shortName)) return false;
    if (startDate != other.startDate) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Alliance [allianceID=" + allianceID + ", executorCorpID=" + executorCorpID + ", memberCount=" + memberCount + ", name=" + name + ", shortName="
        + shortName + ", startDate=" + startDate + "]";
  }

  public static Alliance get(
                             final long time,
                             final long allianceID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<Alliance>() {
        @Override
        public Alliance run() throws Exception {
          TypedQuery<Alliance> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("Alliance.get", Alliance.class);
          getter.setParameter("point", time);
          getter.setParameter("allianceid", allianceID);
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

  public static List<Alliance> accessQuery(
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector allianceID,
                                           final AttributeSelector executorCorpID,
                                           final AttributeSelector memberCount,
                                           final AttributeSelector name,
                                           final AttributeSelector shortName,
                                           final AttributeSelector startDate) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<Alliance>>() {
        @Override
        public List<Alliance> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Alliance c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addLongSelector(qs, "c", "executorCorpID", executorCorpID);
          AttributeSelector.addIntSelector(qs, "c", "memberCount", memberCount);
          AttributeSelector.addStringSelector(qs, "c", "name", name, p);
          AttributeSelector.addStringSelector(qs, "c", "shortName", shortName, p);
          AttributeSelector.addLongSelector(qs, "c", "startDate", startDate);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Alliance> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), Alliance.class);
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
