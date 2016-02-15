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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_contract_bid", indexes = {
    @Index(name = "contractIDIndex", columnList = "contractID", unique = false), @Index(name = "bidIDIndex", columnList = "bidID", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "ContractBid.getByContractAndBidID",
        query = "SELECT c FROM ContractBid c where c.owner = :owner and c.contractID = :contract and c.bidID = :bid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ContractBid.getAll",
        query = "SELECT c FROM ContractBid c where c.owner = :owner and c.cid > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "ContractBid.getAllByContractID",
        query = "SELECT c FROM ContractBid c where c.owner = :owner and c.contractID = :contract and c.bidID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.bidID asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class ContractBid extends CachedData {
  private static final Logger log                 = Logger.getLogger(ContractBid.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                bidID;
  private long                contractID;
  private long                bidderID;
  private long                dateBid             = -1;
  @Column(precision = 19, scale = 2)
  private BigDecimal          amount;

  @SuppressWarnings("unused")
  private ContractBid() {}

  public ContractBid(long bidID, long contractID, long bidderID, long dateBid, BigDecimal amount) {
    super();
    this.bidID = bidID;
    this.contractID = contractID;
    this.bidderID = bidderID;
    this.dateBid = dateBid;
    this.amount = amount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof ContractBid)) return false;
    ContractBid other = (ContractBid) sup;
    return bidID == other.bidID && contractID == other.contractID && bidderID == other.bidderID && dateBid == other.dateBid
        && nullSafeObjectCompare(amount, other.amount);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getBidID() {
    return bidID;
  }

  public long getContractID() {
    return contractID;
  }

  public long getBidderID() {
    return bidderID;
  }

  public long getDateBid() {
    return dateBid;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((amount == null) ? 0 : amount.hashCode());
    result = prime * result + (int) (bidID ^ (bidID >>> 32));
    result = prime * result + (int) (bidderID ^ (bidderID >>> 32));
    result = prime * result + (int) (contractID ^ (contractID >>> 32));
    result = prime * result + (int) (dateBid ^ (dateBid >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ContractBid other = (ContractBid) obj;
    if (amount == null) {
      if (other.amount != null) return false;
    } else if (!amount.equals(other.amount)) return false;
    if (bidID != other.bidID) return false;
    if (bidderID != other.bidderID) return false;
    if (contractID != other.contractID) return false;
    if (dateBid != other.dateBid) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ContractBid [bidID=" + bidID + ", contractID=" + contractID + ", bidderID=" + bidderID + ", dateBid=" + dateBid + ", amount=" + amount + ", owner="
        + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Return contract bid with given properties, live at the given time. Returns null if no such contract bid exists.
   * 
   * @param owner
   *          contract bid owner
   * @param time
   *          time at which the contract must be live
   * @param contractID
   *          contract ID of bid
   * @param bidID
   *          bid ID of bid
   * @return contract bid with the given properties live at the given time, or null.
   */
  public static ContractBid get(final SynchronizedEveAccount owner, final long time, final long contractID, final long bidID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ContractBid>() {
        @Override
        public ContractBid run() throws Exception {
          TypedQuery<ContractBid> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContractBid.getByContractAndBidID",
                                                                                                                      ContractBid.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contract", contractID);
          getter.setParameter("bid", bidID);
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
   * List contract bids live at the given time with a sortKey (lexicographically) greater than contid.
   * 
   * @param owner
   *          contract bids owner
   * @param time
   *          time at which contract bids must be live
   * @param maxresults
   *          maximum number of contract bids to retrieve
   * @param contid
   *          sortKey (exclusive) from which to start returning results
   * @return a list of contract bids no longer than maxresults with sortKey (lexicographically) greater than contid
   */
  public static List<ContractBid> getAllBids(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(ContractBid.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContractBid>>() {
        @Override
        public List<ContractBid> run() throws Exception {
          TypedQuery<ContractBid> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContractBid.getAll", ContractBid.class);
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
   * List contract bids by contract ID live at the given time with a sortKey (lexicographically) greater than contid.
   * 
   * @param owner
   *          contract bids owner
   * @param time
   *          time at which contract bids must be live
   * @param contractID
   *          contract ID for all bids
   * @param maxresults
   *          maximum number of contract bids to retrieve
   * @param contid
   *          sortKey (exclusive) from which to start returning results
   * @return list of contract bids no longer than maxresults with the given contract ID and with sortKey (lexicographically) greater than contid
   */
  public static List<ContractBid> getAllBidsByContractID(
                                                         final SynchronizedEveAccount owner,
                                                         final long time,
                                                         final long contractID,
                                                         int maxresults,
                                                         final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(ContractBid.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContractBid>>() {
        @Override
        public List<ContractBid> run() throws Exception {
          TypedQuery<ContractBid> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContractBid.getAllByContractID",
                                                                                                                      ContractBid.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contract", contractID);
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

}
