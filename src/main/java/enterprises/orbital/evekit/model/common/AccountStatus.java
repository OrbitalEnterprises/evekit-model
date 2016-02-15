package enterprises.orbital.evekit.model.common;

import java.util.ArrayList;
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
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_account_status")
@NamedQueries({
    @NamedQuery(name = "AccountStatus.get", query = "SELECT c FROM AccountStatus c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class AccountStatus extends CachedData {
  private static final Logger log                    = Logger.getLogger(AccountStatus.class.getName());
  private static final byte[] MASK                   = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_STATUS);
  private long                paidUntil;
  private long                createDate;
  private int                 logonCount;
  private int                 logonMinutes;
  @ElementCollection(fetch = FetchType.EAGER)
  private List<Long>          multiCharacterTraining = new ArrayList<Long>();

  @SuppressWarnings("unused")
  private AccountStatus() {}

  public AccountStatus(long paidUntil, long createDate, int logonCount, int logonMinutes) {
    this.paidUntil = paidUntil;
    this.createDate = createDate;
    this.logonCount = logonCount;
    this.logonMinutes = logonMinutes;
    this.multiCharacterTraining = new ArrayList<Long>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
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

  public int getLogonCount() {
    return logonCount;
  }

  public int getLogonMinutes() {
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
    result = prime * result + logonCount;
    result = prime * result + logonMinutes;
    result = prime * result + ((multiCharacterTraining == null) ? 0 : multiCharacterTraining.hashCode());
    result = prime * result + (int) (paidUntil ^ (paidUntil >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
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
  public static AccountStatus get(final SynchronizedEveAccount owner, final long time) {
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
}
