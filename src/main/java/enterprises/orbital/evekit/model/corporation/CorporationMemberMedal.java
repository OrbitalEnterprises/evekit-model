package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    name = "evekit_data_corporation_member_medal",
    indexes = {
        @Index(
            name = "medalIDIndex",
            columnList = "medalID",
            unique = false),
        @Index(
            name = "characterIDIndex",
            columnList = "characterID",
            unique = false),
        @Index(
            name = "issuedIndex",
            columnList = "issued",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "CorporationMemberMedal.getByMedalAndCharacterAndIssued",
        query = "SELECT c FROM CorporationMemberMedal c where c.owner = :owner and c.medalID = :medal and c.characterID = :char and c.issued = :issued and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CorporationMemberMedal.getAllForward",
        query = "SELECT c FROM CorporationMemberMedal c where c.owner = :owner and c.issued > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.issued asc"),
    @NamedQuery(
        name = "CorporationMemberMedal.getAllBackward",
        query = "SELECT c FROM CorporationMemberMedal c where c.owner = :owner and c.issued < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.issued desc"),
})
// 7 hour cache time - API caches for 6 hours
public class CorporationMemberMedal extends CachedData {
  private static final Logger log                 = Logger.getLogger(CorporationMemberMedal.class.getName());
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_MEDALS);
  private int                 medalID;
  private long                characterID;
  private long                issued              = -1;
  private long                issuerID;
  private String              reason;
  private String              status;
  @Transient
  @ApiModelProperty(
      value = "issued Date")
  @JsonProperty("issuedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                issuedDate;

  @SuppressWarnings("unused")
  private CorporationMemberMedal() {}

  public CorporationMemberMedal(int medalID, long characterID, long issued, long issuerID, String reason, String status) {
    super();
    this.medalID = medalID;
    this.characterID = characterID;
    this.issued = issued;
    this.issuerID = issuerID;
    this.reason = reason;
    this.status = status;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    issuedDate = assignDateField(issued);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CorporationMemberMedal)) return false;
    CorporationMemberMedal other = (CorporationMemberMedal) sup;
    return medalID == other.medalID && characterID == other.characterID && issued == other.issued && issuerID == other.issuerID
        && nullSafeObjectCompare(reason, other.reason) && nullSafeObjectCompare(status, other.status);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getMedalID() {
    return medalID;
  }

  public long getCharacterID() {
    return characterID;
  }

  public long getIssued() {
    return issued;
  }

  public long getIssuerID() {
    return issuerID;
  }

  public String getReason() {
    return reason;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + (int) (issued ^ (issued >>> 32));
    result = prime * result + (int) (issuerID ^ (issuerID >>> 32));
    result = prime * result + medalID;
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CorporationMemberMedal other = (CorporationMemberMedal) obj;
    if (characterID != other.characterID) return false;
    if (issued != other.issued) return false;
    if (issuerID != other.issuerID) return false;
    if (medalID != other.medalID) return false;
    if (reason == null) {
      if (other.reason != null) return false;
    } else if (!reason.equals(other.reason)) return false;
    if (status == null) {
      if (other.status != null) return false;
    } else if (!status.equals(other.status)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CorporationMemberMedal [medalID=" + medalID + ", characterID=" + characterID + ", issued=" + issued + ", issuerID=" + issuerID + ", reason="
        + reason + ", status=" + status + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CorporationMemberMedal get(
                                           final SynchronizedEveAccount owner,
                                           final long time,
                                           final int medalID,
                                           final long characterID,
                                           final long issued) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CorporationMemberMedal>() {
        @Override
        public CorporationMemberMedal run() throws Exception {
          TypedQuery<CorporationMemberMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CorporationMemberMedal.getByMedalAndCharacterAndIssued", CorporationMemberMedal.class);
          getter.setParameter("owner", owner);
          getter.setParameter("medal", medalID);
          getter.setParameter("char", characterID);
          getter.setParameter("issued", issued);
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

  public static List<CorporationMemberMedal> getAllForward(
                                                           final SynchronizedEveAccount owner,
                                                           final long time,
                                                           int maxresults,
                                                           final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(CorporationMemberMedal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationMemberMedal>>() {
        @Override
        public List<CorporationMemberMedal> run() throws Exception {
          TypedQuery<CorporationMemberMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CorporationMemberMedal.getAllForward", CorporationMemberMedal.class);
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

  public static List<CorporationMemberMedal> getAllBackward(
                                                            final SynchronizedEveAccount owner,
                                                            final long time,
                                                            int maxresults,
                                                            final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(CorporationMemberMedal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationMemberMedal>>() {
        @Override
        public List<CorporationMemberMedal> run() throws Exception {
          TypedQuery<CorporationMemberMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("CorporationMemberMedal.getAllBackward", CorporationMemberMedal.class);
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

  public static List<CorporationMemberMedal> accessQuery(
                                                         final SynchronizedEveAccount owner,
                                                         final long contid,
                                                         final int maxresults,
                                                         final boolean reverse,
                                                         final AttributeSelector at,
                                                         final AttributeSelector medalID,
                                                         final AttributeSelector characterID,
                                                         final AttributeSelector issued,
                                                         final AttributeSelector issuerID,
                                                         final AttributeSelector reason,
                                                         final AttributeSelector status) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationMemberMedal>>() {
        @Override
        public List<CorporationMemberMedal> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CorporationMemberMedal c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
          AttributeSelector.addLongSelector(qs, "c", "characterID", characterID);
          AttributeSelector.addLongSelector(qs, "c", "issued", issued);
          AttributeSelector.addLongSelector(qs, "c", "issuerID", issuerID);
          AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
          AttributeSelector.addStringSelector(qs, "c", "status", status, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CorporationMemberMedal> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                           CorporationMemberMedal.class);
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
