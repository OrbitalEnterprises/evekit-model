package enterprises.orbital.evekit.model.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_account_status")
@NamedQueries({
    @NamedQuery(
        name = "AccountStatus.get",
        query = "SELECT c FROM AccountStatus c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class AccountStatus extends CachedData {
  private static final Logger log                    = Logger.getLogger(AccountStatus.class.getName());
  private static final byte[] MASK                   = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_STATUS);
  private long                paidUntil              = -1;
  private long                createDate             = -1;
  private long                logonCount;
  private long                logonMinutes;
  @ElementCollection(
      fetch = FetchType.EAGER)
  private List<Long>          multiCharacterTraining = new ArrayList<Long>();
  @Transient
  @ApiModelProperty(
      value = "paidUntil Date")
  @JsonProperty("paidUntilDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                paidUntilDate;
  @Transient
  @ApiModelProperty(
      value = "createDate Date")
  @JsonProperty("createDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                createDateDate;

  @SuppressWarnings("unused")
  private AccountStatus() {}

  public AccountStatus(long paidUntil, long createDate, long logonCount, long logonMinutes) {
    this.paidUntil = paidUntil;
    this.createDate = createDate;
    this.logonCount = logonCount;
    this.logonMinutes = logonMinutes;
    this.multiCharacterTraining = new ArrayList<Long>();
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    paidUntilDate = assignDateField(paidUntil);
    createDateDate = assignDateField(createDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof AccountStatus)) return false;
    AccountStatus other = (AccountStatus) sup;
    boolean listEquals = multiCharacterTraining.size() == other.multiCharacterTraining.size();
    if (!listEquals) return false;
    Set<Long> localMCT = new HashSet<Long>();
    Set<Long> otherMCT = new HashSet<Long>();
    localMCT.addAll(multiCharacterTraining);
    otherMCT.addAll(other.multiCharacterTraining);
    return paidUntil == other.paidUntil && createDate == other.createDate && logonCount == other.logonCount && logonMinutes == other.logonMinutes
        && localMCT.equals(otherMCT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getPaidUntil() {
    return paidUntil;
  }

  public long getCreateDate() {
    return createDate;
  }

  public long getLogonCount() {
    return logonCount;
  }

  public long getLogonMinutes() {
    return logonMinutes;
  }

  public List<Long> getMultiCharacterTraining() {
    return multiCharacterTraining;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (createDate ^ (createDate >>> 32));
    result = prime * result + (int) (logonCount ^ (logonCount >>> 32));
    result = prime * result + (int) (logonMinutes ^ (logonMinutes >>> 32));
    result = prime * result + ((multiCharacterTraining == null) ? 0 : multiCharacterTraining.hashCode());
    result = prime * result + (int) (paidUntil ^ (paidUntil >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AccountStatus other = (AccountStatus) obj;
    if (createDate != other.createDate) return false;
    if (logonCount != other.logonCount) return false;
    if (logonMinutes != other.logonMinutes) return false;
    if (multiCharacterTraining == null) {
      if (other.multiCharacterTraining != null) return false;
    } else if (!multiCharacterTraining.equals(other.multiCharacterTraining)) return false;
    if (paidUntil != other.paidUntil) return false;
    return true;
  }

  @Override
  public String toString() {
    return "AccountStatus [paidUntil=" + paidUntil + ", createDate=" + createDate + ", logonCount=" + logonCount + ", logonMinutes=" + logonMinutes
        + ", multiCharacterTraining=" + multiCharacterTraining + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve existing account status live at the given time. Returns null if no such account status exists.
   * 
   * @param owner
   *          account status owner
   * @param time
   *          time at which the account status should be live
   * @return an existing account status, or null
   */
  public static AccountStatus get(
                                  final SynchronizedEveAccount owner,
                                  final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<AccountStatus>() {
        @Override
        public AccountStatus run() throws Exception {
          TypedQuery<AccountStatus> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("AccountStatus.get",
                                                                                                                        AccountStatus.class);
          getter.setParameter("owner", owner);
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

  public static List<AccountStatus> accessQuery(
                                                final SynchronizedEveAccount owner,
                                                final long contid,
                                                final int maxresults,
                                                final boolean reverse,
                                                final AttributeSelector at,
                                                final AttributeSelector paidUntil,
                                                final AttributeSelector createDate,
                                                final AttributeSelector logonCount,
                                                final AttributeSelector logonMinutes,
                                                final AttributeSelector multiCharacterTraining) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<AccountStatus>>() {
        @Override
        public List<AccountStatus> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM AccountStatus c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "paidUntil", paidUntil);
          AttributeSelector.addLongSelector(qs, "c", "createDate", createDate);
          AttributeSelector.addLongSelector(qs, "c", "logonCount", logonCount);
          AttributeSelector.addLongSelector(qs, "c", "logonMinutes", logonMinutes);
          AttributeSelector.addSetLongSelector(qs, "c", "multiCharacterTraining", multiCharacterTraining);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<AccountStatus> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), AccountStatus.class);
          query.setParameter("owner", owner);
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
