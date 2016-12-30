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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_eve_alliance_member")
@NamedQueries({
    @NamedQuery(
        name = "AllianceMemberCorporation.get",
        query = "SELECT c FROM AllianceMemberCorporation c WHERE c.allianceID = :allianceid AND c.corporationID = :corpid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class AllianceMemberCorporation extends RefCachedData {
  private static final Logger log = Logger.getLogger(AllianceMemberCorporation.class.getName());
  private long                allianceID;
  private long                corporationID;
  private long                startDate;
  @Transient
  @ApiModelProperty(
      value = "startDate Date")
  @JsonProperty("startDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                startDateDate;

  @SuppressWarnings("unused")
  private AllianceMemberCorporation() {}

  public AllianceMemberCorporation(long allianceID, long corporationID, long startDate) {
    super();
    this.allianceID = allianceID;
    this.corporationID = corporationID;
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
    if (!(sup instanceof AllianceMemberCorporation)) return false;
    AllianceMemberCorporation other = (AllianceMemberCorporation) sup;
    return allianceID == other.allianceID && corporationID == other.corporationID && startDate == other.startDate;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public long getStartDate() {
    return startDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + (int) (startDate ^ (startDate >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AllianceMemberCorporation other = (AllianceMemberCorporation) obj;
    if (allianceID != other.allianceID) return false;
    if (corporationID != other.corporationID) return false;
    if (startDate != other.startDate) return false;
    return true;
  }

  @Override
  public String toString() {
    return "AllianceMemberCorporation [allianceID=" + allianceID + ", corporationID=" + corporationID + ", startDate=" + startDate + "]";
  }

  public static AllianceMemberCorporation get(
                                              final long time,
                                              final long allianceID,
                                              final long corporationID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<AllianceMemberCorporation>() {
        @Override
        public AllianceMemberCorporation run() throws Exception {
          TypedQuery<AllianceMemberCorporation> getter = EveKitRefDataProvider.getFactory().getEntityManager()
              .createNamedQuery("AllianceMemberCorporation.get", AllianceMemberCorporation.class);
          getter.setParameter("point", time);
          getter.setParameter("allianceid", allianceID);
          getter.setParameter("corpid", corporationID);
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

  public static List<AllianceMemberCorporation> accessQuery(
                                                            final long contid,
                                                            final int maxresults,
                                                            final boolean reverse,
                                                            final AttributeSelector at,
                                                            final AttributeSelector allianceID,
                                                            final AttributeSelector corporationID,
                                                            final AttributeSelector startDate) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<AllianceMemberCorporation>>() {
        @Override
        public List<AllianceMemberCorporation> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM AllianceMemberCorporation c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
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
          TypedQuery<AllianceMemberCorporation> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                          AllianceMemberCorporation.class);
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
