package enterprises.orbital.evekit.model.common;

import java.math.BigDecimal;
import java.util.Collections;
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
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_account_balance",
    indexes = {
        @Index(
            name = "accountIDIndex",
            columnList = "accountID",
            unique = false),
        @Index(
            name = "accountKeyIndex",
            columnList = "accountKey",
            unique = false)
    })
@NamedQueries({
    @NamedQuery(
        name = "AccountBalance.getByAccountID",
        query = "SELECT c FROM AccountBalance c where c.owner = :owner and c.accountID = :aid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "AccountBalance.getByAccountKey",
        query = "SELECT c FROM AccountBalance c where c.owner = :owner and c.accountKey = :akey and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "AccountBalance.getAll",
        query = "SELECT c FROM AccountBalance c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class AccountBalance extends CachedData {
  private static final Logger log  = Logger.getLogger(AccountBalance.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
  private int                 accountID;
  private int                 accountKey;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          balance;

  @SuppressWarnings("unused")
  protected AccountBalance() {}

  public AccountBalance(int accountID, int accountKey, BigDecimal balance) {
    this.accountID = accountID;
    this.accountKey = accountKey;
    this.balance = balance;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof AccountBalance)) return false;
    AccountBalance other = (AccountBalance) sup;
    return accountID == other.accountID && accountKey == other.accountKey && nullSafeObjectCompare(balance, other.balance);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getAccountID() {
    return accountID;
  }

  public int getAccountKey() {
    return accountKey;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + accountID;
    result = prime * result + accountKey;
    result = prime * result + ((balance == null) ? 0 : balance.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AccountBalance other = (AccountBalance) obj;
    if (accountID != other.accountID) return false;
    if (accountKey != other.accountKey) return false;
    if (balance == null) {
      if (other.balance != null) return false;
    } else if (!balance.equals(other.balance)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "AccountBalance [accountID=" + accountID + ", accountKey=" + accountKey + ", balance=" + balance + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve existing account balance with the given id and which is live at the given time. Returns null if no such account balance exists.
   * 
   * @param owner
   *          account balance owner
   * @param time
   *          time at which the account balance should be live
   * @param aid
   *          account balance ID
   * @return an existing account balance, or null.
   */
  public static AccountBalance get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final int aid) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<AccountBalance>() {
        @Override
        public AccountBalance run() throws Exception {
          TypedQuery<AccountBalance> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("AccountBalance.getByAccountID",
                                                                                                                         AccountBalance.class);
          getter.setParameter("owner", owner);
          getter.setParameter("aid", aid);
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
   * Get an account balance by account key, live at the given time.
   * 
   * @param owner
   *          account balance owner
   * @param akey
   *          account key
   * @param time
   *          time at which the account balance should be live
   * @return an account balance with the given key, live at the given time, or null if no such account balance exists
   */
  public static AccountBalance getByKey(
                                        final SynchronizedEveAccount owner,
                                        final long time,
                                        final int akey) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<AccountBalance>() {
        @Override
        public AccountBalance run() throws Exception {
          TypedQuery<AccountBalance> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("AccountBalance.getByAccountKey",
                                                                                                                         AccountBalance.class);
          getter.setParameter("owner", owner);
          getter.setParameter("akey", akey);
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
   * Retrieve all account balances live at the given time.
   * 
   * @param owner
   *          account balances owner.
   * @param time
   *          time at which the account balances should be live
   * @return the list of account balances live at the given time
   */
  public static List<AccountBalance> getAll(
                                            final SynchronizedEveAccount owner,
                                            final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<AccountBalance>>() {
        @Override
        public List<AccountBalance> run() throws Exception {
          TypedQuery<AccountBalance> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("AccountBalance.getAll",
                                                                                                                         AccountBalance.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<AccountBalance> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector accountID,
                                                 final AttributeSelector accountKey) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<AccountBalance>>() {
        @Override
        public List<AccountBalance> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM AccountBalance c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "accountID", accountID);
          AttributeSelector.addIntSelector(qs, "c", "accountKey", accountKey);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<AccountBalance> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), AccountBalance.class);
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
