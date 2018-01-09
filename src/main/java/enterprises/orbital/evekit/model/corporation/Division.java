package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_division",
    indexes = {
        @Index(
            name = "walletIndex",
            columnList = "wallet",
            unique = false),
        @Index(
            name = "accountKeyIndex",
            columnList = "accountKey",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Division.getByWalletAndAccountKey",
        query = "SELECT c FROM Division c where c.owner = :owner and c.wallet = :wallet and c.accountKey = :ack and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Division.getAll",
        query = "SELECT c FROM Division c where c.owner = :owner and c.wallet = :wallet and c.lifeStart <= :point and c.lifeEnd > :point order by c.accountKey asc"),
})
// 2 hour cache time - API caches for 1 hour
public class Division extends CachedData {
  private static final Logger log  = Logger.getLogger(Division.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET);
  private boolean             wallet;
  private int                 accountKey;
  private String              description;

  @SuppressWarnings("unused")
  protected Division() {}

  public Division(boolean wallet, int accountKey, String description) {
    super();
    this.wallet = wallet;
    this.accountKey = accountKey;
    this.description = description;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Division)) return false;
    Division other = (Division) sup;
    return wallet == other.wallet && accountKey == other.accountKey && nullSafeObjectCompare(description, other.description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public boolean isWallet() {
    return wallet;
  }

  public int getAccountKey() {
    return accountKey;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + accountKey;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (wallet ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Division other = (Division) obj;
    if (accountKey != other.accountKey) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (wallet != other.wallet) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Division [wallet=" + wallet + ", accountKey=" + accountKey + ", description=" + description + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  public static Division get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final boolean wallet,
                             final int accountKey) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Division>() {
        @Override
        public Division run() throws Exception {
          TypedQuery<Division> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Division.getByWalletAndAccountKey",
                                                                                                                   Division.class);
          getter.setParameter("owner", owner);
          getter.setParameter("wallet", wallet);
          getter.setParameter("ack", accountKey);
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

  public static List<Division> getAllByType(
                                            final SynchronizedEveAccount owner,
                                            final long time,
                                            final boolean wallet) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Division>>() {
        @Override
        public List<Division> run() throws Exception {
          TypedQuery<Division> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Division.getAll", Division.class);
          getter.setParameter("owner", owner);
          getter.setParameter("wallet", wallet);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<Division> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector wallet,
                                           final AttributeSelector accountKey,
                                           final AttributeSelector description) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Division>>() {
        @Override
        public List<Division> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Division c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addBooleanSelector(qs, "c", "wallet", wallet);
          AttributeSelector.addIntSelector(qs, "c", "accountKey", accountKey);
          AttributeSelector.addStringSelector(qs, "c", "description", description, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Division> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Division.class);
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
