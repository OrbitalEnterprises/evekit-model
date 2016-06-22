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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_contract",
    indexes = {
        @Index(
            name = "contractIDIndex",
            columnList = "contractID",
            unique = false),
        @Index(
            name = "dateIssuedIndex",
            columnList = "dateIssued",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Contract.getByContractID",
        query = "SELECT c FROM Contract c where c.owner = :owner and c.contractID = :contract and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Contract.getAll",
        query = "SELECT c FROM Contract c where c.owner = :owner and c.contractID > :contract and c.lifeStart <= :point and c.lifeEnd > :point order by c.contractID asc"),
    @NamedQuery(
        name = "Contract.getAllItemRetrievable",
        query = "SELECT c FROM Contract c where c.owner = :owner and c.contractID > :contract and :threshold <= c.dateIssued + ((c.numDays + 7) * 24 * 3600) and c.lifeStart <= :point and c.lifeEnd > :point order by c.contractID asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class Contract extends CachedData {
  private static final Logger log                 = Logger.getLogger(Contract.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                contractID;
  private long                issuerID;
  private long                issuerCorpID;
  private long                assigneeID;
  private long                acceptorID;
  private int                 startStationID;
  private int                 endStationID;
  private String              type;
  private String              status;
  private String              title;
  private boolean             forCorp;
  private String              availability;
  private long                dateIssued          = -1;
  private long                dateExpired         = -1;
  private long                dateAccepted        = -1;
  private int                 numDays;
  private long                dateCompleted       = -1;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          price;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          reward;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          collateral;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          buyout;
  private long                volume;

  @SuppressWarnings("unused")
  private Contract() {}

  public Contract(long contractID, long issuerID, long issuerCorpID, long assigneeID, long acceptorID, int startStationID, int endStationID, String type,
                  String status, String title, boolean forCorp, String availability, long dateIssued, long dateExpired, long dateAccepted, int numDays,
                  long dateCompleted, BigDecimal price, BigDecimal reward, BigDecimal collateral, BigDecimal buyout, long volume) {
    super();
    this.contractID = contractID;
    this.issuerID = issuerID;
    this.issuerCorpID = issuerCorpID;
    this.assigneeID = assigneeID;
    this.acceptorID = acceptorID;
    this.startStationID = startStationID;
    this.endStationID = endStationID;
    this.type = type;
    this.status = status;
    this.title = title;
    this.forCorp = forCorp;
    this.availability = availability;
    this.dateIssued = dateIssued;
    this.dateExpired = dateExpired;
    this.dateAccepted = dateAccepted;
    this.numDays = numDays;
    this.dateCompleted = dateCompleted;
    this.price = price;
    this.reward = reward;
    this.collateral = collateral;
    this.buyout = buyout;
    this.volume = volume;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Contract)) return false;
    Contract other = (Contract) sup;
    return contractID == other.contractID && issuerID == other.issuerID && issuerCorpID == other.issuerCorpID && assigneeID == other.assigneeID
        && acceptorID == other.acceptorID && startStationID == other.startStationID && endStationID == other.endStationID
        && nullSafeObjectCompare(type, other.type) && nullSafeObjectCompare(status, other.status) && nullSafeObjectCompare(title, other.title)
        && forCorp == other.forCorp && nullSafeObjectCompare(availability, other.availability) && dateIssued == other.dateIssued
        && dateExpired == other.dateExpired && dateAccepted == other.dateAccepted && numDays == other.numDays && dateCompleted == other.dateCompleted
        && nullSafeObjectCompare(price, other.price) && nullSafeObjectCompare(reward, other.reward) && nullSafeObjectCompare(collateral, other.collateral)
        && nullSafeObjectCompare(buyout, other.buyout) && volume == other.volume;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getContractID() {
    return contractID;
  }

  public long getIssuerID() {
    return issuerID;
  }

  public long getIssuerCorpID() {
    return issuerCorpID;
  }

  public long getAssigneeID() {
    return assigneeID;
  }

  public long getAcceptorID() {
    return acceptorID;
  }

  public int getStartStationID() {
    return startStationID;
  }

  public int getEndStationID() {
    return endStationID;
  }

  public String getType() {
    return type;
  }

  public String getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public boolean isForCorp() {
    return forCorp;
  }

  public String getAvailability() {
    return availability;
  }

  public long getDateIssued() {
    return dateIssued;
  }

  public long getDateExpired() {
    return dateExpired;
  }

  public long getDateAccepted() {
    return dateAccepted;
  }

  public int getNumDays() {
    return numDays;
  }

  public long getDateCompleted() {
    return dateCompleted;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public BigDecimal getReward() {
    return reward;
  }

  public BigDecimal getCollateral() {
    return collateral;
  }

  public BigDecimal getBuyout() {
    return buyout;
  }

  public long getVolume() {
    return volume;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (acceptorID ^ (acceptorID >>> 32));
    result = prime * result + (int) (assigneeID ^ (assigneeID >>> 32));
    result = prime * result + ((availability == null) ? 0 : availability.hashCode());
    result = prime * result + ((buyout == null) ? 0 : buyout.hashCode());
    result = prime * result + ((collateral == null) ? 0 : collateral.hashCode());
    result = prime * result + (int) (contractID ^ (contractID >>> 32));
    result = prime * result + (int) (dateAccepted ^ (dateAccepted >>> 32));
    result = prime * result + (int) (dateCompleted ^ (dateCompleted >>> 32));
    result = prime * result + (int) (dateExpired ^ (dateExpired >>> 32));
    result = prime * result + (int) (dateIssued ^ (dateIssued >>> 32));
    result = prime * result + endStationID;
    result = prime * result + (forCorp ? 1231 : 1237);
    result = prime * result + (int) (issuerCorpID ^ (issuerCorpID >>> 32));
    result = prime * result + (int) (issuerID ^ (issuerID >>> 32));
    result = prime * result + numDays;
    result = prime * result + ((price == null) ? 0 : price.hashCode());
    result = prime * result + ((reward == null) ? 0 : reward.hashCode());
    result = prime * result + startStationID;
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + (int) (volume ^ (volume >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Contract other = (Contract) obj;
    if (acceptorID != other.acceptorID) return false;
    if (assigneeID != other.assigneeID) return false;
    if (availability == null) {
      if (other.availability != null) return false;
    } else if (!availability.equals(other.availability)) return false;
    if (buyout == null) {
      if (other.buyout != null) return false;
    } else if (!buyout.equals(other.buyout)) return false;
    if (collateral == null) {
      if (other.collateral != null) return false;
    } else if (!collateral.equals(other.collateral)) return false;
    if (contractID != other.contractID) return false;
    if (dateAccepted != other.dateAccepted) return false;
    if (dateCompleted != other.dateCompleted) return false;
    if (dateExpired != other.dateExpired) return false;
    if (dateIssued != other.dateIssued) return false;
    if (endStationID != other.endStationID) return false;
    if (forCorp != other.forCorp) return false;
    if (issuerCorpID != other.issuerCorpID) return false;
    if (issuerID != other.issuerID) return false;
    if (numDays != other.numDays) return false;
    if (price == null) {
      if (other.price != null) return false;
    } else if (!price.equals(other.price)) return false;
    if (reward == null) {
      if (other.reward != null) return false;
    } else if (!reward.equals(other.reward)) return false;
    if (startStationID != other.startStationID) return false;
    if (status == null) {
      if (other.status != null) return false;
    } else if (!status.equals(other.status)) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    if (type == null) {
      if (other.type != null) return false;
    } else if (!type.equals(other.type)) return false;
    if (volume != other.volume) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Contract [contractID=" + contractID + ", issuerID=" + issuerID + ", issuerCorpID=" + issuerCorpID + ", assigneeID=" + assigneeID + ", acceptorID="
        + acceptorID + ", startStationID=" + startStationID + ", endStationID=" + endStationID + ", type=" + type + ", status=" + status + ", title=" + title
        + ", forCorp=" + forCorp + ", availability=" + availability + ", dateIssued=" + dateIssued + ", dateExpired=" + dateExpired + ", dateAccepted="
        + dateAccepted + ", numDays=" + numDays + ", dateCompleted=" + dateCompleted + ", price=" + price + ", reward=" + reward + ", collateral=" + collateral
        + ", buyout=" + buyout + ", volume=" + volume + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Return contract with given ID, live at the given time. Returns null if no such contract exists.
   * 
   * @param owner
   *          contract owner
   * @param time
   *          time at which the contract must be live
   * @param contractID
   *          contract ID
   * @return contract with the given ID live at the given time, or null.
   */
  public static Contract get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final long contractID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Contract>() {
        @Override
        public Contract run() throws Exception {
          TypedQuery<Contract> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Contract.getByContractID", Contract.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contract", contractID);
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
   * List contracts live at the given time with contractID greater than contid
   * 
   * @param owner
   *          contracts owner
   * @param time
   *          time at which the contracts must be live
   * @param maxresults
   *          maximum number of contracts to retrieve
   * @param contid
   *          contractID (exclusive) from which to start returning results
   * @return a list of contracts no longer than maxresults with contractID greater than contid
   */
  public static List<Contract> getAllContracts(
                                               final SynchronizedEveAccount owner,
                                               final long time,
                                               int maxresults,
                                               final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Contract.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Contract>>() {
        @Override
        public List<Contract> run() throws Exception {
          TypedQuery<Contract> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Contract.getAll", Contract.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contract", contid);
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
   * Retrieve list of contracts for which it should still be possible to retrieve contract items.
   * 
   * @param owner
   *          contracts owner
   * @param time
   *          time at which contracts must be live
   * @param maxresults
   *          max number of contracts to return
   * @param contid
   *          contract ID (exclusive) from which to start returning results
   * @param threshold
   *          time which must be less than expiry date of a contract in order to be considered
   * @return a list of contracts, at most maxresults long, live at the given time, and meeting the specified threshold
   */
  public static List<Contract> getAllItemRetrievableContracts(
                                                              final SynchronizedEveAccount owner,
                                                              final long time,
                                                              int maxresults,
                                                              final long contid,
                                                              final long threshold) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Contract.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Contract>>() {
        @Override
        public List<Contract> run() throws Exception {
          TypedQuery<Contract> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Contract.getAllItemRetrievable",
                                                                                                                   Contract.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contract", contid);
          // Contract items are considered retrievable as long as the threshold is less
          // than 7 days after the day the contract was issued plus the length of the contract
          // in days. In other words:
          getter.setParameter("threshold", threshold);
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

  public static List<Contract> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector contractID,
                                           final AttributeSelector issuerID,
                                           final AttributeSelector issuerCorpID,
                                           final AttributeSelector assigneeID,
                                           final AttributeSelector acceptorID,
                                           final AttributeSelector startStationID,
                                           final AttributeSelector endStationID,
                                           final AttributeSelector type,
                                           final AttributeSelector status,
                                           final AttributeSelector title,
                                           final AttributeSelector forCorp,
                                           final AttributeSelector availability,
                                           final AttributeSelector dateIssued,
                                           final AttributeSelector dateExpired,
                                           final AttributeSelector dateAccepted,
                                           final AttributeSelector numDays,
                                           final AttributeSelector dateCompleted,
                                           final AttributeSelector price,
                                           final AttributeSelector reward,
                                           final AttributeSelector collateral,
                                           final AttributeSelector buyout,
                                           final AttributeSelector volume) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Contract>>() {
        @Override
        public List<Contract> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Contract c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "contractID", contractID);
          AttributeSelector.addLongSelector(qs, "c", "issuerID", issuerID);
          AttributeSelector.addLongSelector(qs, "c", "issuerCorpID", issuerCorpID);
          AttributeSelector.addLongSelector(qs, "c", "assigneeID", assigneeID);
          AttributeSelector.addLongSelector(qs, "c", "acceptorID", acceptorID);
          AttributeSelector.addIntSelector(qs, "c", "startStationID", startStationID);
          AttributeSelector.addIntSelector(qs, "c", "endStationID", endStationID);
          AttributeSelector.addStringSelector(qs, "c", "type", type, p);
          AttributeSelector.addStringSelector(qs, "c", "status", status, p);
          AttributeSelector.addStringSelector(qs, "c", "title", title, p);
          AttributeSelector.addBooleanSelector(qs, "c", "forCorp", forCorp);
          AttributeSelector.addStringSelector(qs, "c", "availability", availability, p);
          AttributeSelector.addLongSelector(qs, "c", "dateIssued", dateIssued);
          AttributeSelector.addLongSelector(qs, "c", "dateExpired", dateExpired);
          AttributeSelector.addLongSelector(qs, "c", "dateAccepted", dateAccepted);
          AttributeSelector.addIntSelector(qs, "c", "numDays", numDays);
          AttributeSelector.addLongSelector(qs, "c", "dateCompleted", dateCompleted);
          AttributeSelector.addDoubleSelector(qs, "c", "price", price);
          AttributeSelector.addDoubleSelector(qs, "c", "reward", reward);
          AttributeSelector.addDoubleSelector(qs, "c", "collateral", collateral);
          AttributeSelector.addDoubleSelector(qs, "c", "buyout", buyout);
          AttributeSelector.addLongSelector(qs, "c", "volume", volume);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Contract> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Contract.class);
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
