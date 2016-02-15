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
@Table(name = "evekit_data_wallet_transaction", indexes = {
    @Index(name = "transactionIDIndex", columnList = "transactionID", unique = false)
})
@NamedQueries({
    @NamedQuery(
        name = "WalletTransaction.getByTransactionID",
        query = "SELECT c FROM WalletTransaction c where c.owner = :owner and c.transactionID = :transid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "WalletTransaction.getAllForward",
        query = "SELECT c FROM WalletTransaction c where c.owner = :owner and c.date > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.date asc"),
    @NamedQuery(
        name = "WalletTransaction.getAllBackward",
        query = "SELECT c FROM WalletTransaction c where c.owner = :owner and c.date < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.date desc"),
    @NamedQuery(
        name = "WalletTransaction.getRangeAsc",
        query = "SELECT c FROM WalletTransaction c where c.owner = :owner and c.date >= :mindate and c.date <= :maxdate and c.lifeStart <= :point and c.lifeEnd > :point order by c.date asc"),
    @NamedQuery(
        name = "WalletTransaction.getRangeDesc",
        query = "SELECT c FROM WalletTransaction c where c.owner = :owner and c.date >= :mindate and c.date <= :maxdate and c.lifeStart <= :point and c.lifeEnd > :point order by c.date desc"),
})
// 1 hour cache time - API caches for 30 minutes
public class WalletTransaction extends CachedData {
  private static final Logger log                 = Logger.getLogger(WalletTransaction.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_TRANSACTIONS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private int                 accountKey;
  private long                transactionID;
  private long                date                = -1;
  private int                 quantity;
  private String              typeName;
  private int                 typeID;
  @Column(precision = 19, scale = 2)
  private BigDecimal          price;
  private long                clientID;
  private String              clientName;
  private int                 stationID;
  private String              stationName;
  private String              transactionType;
  private String              transactionFor;
  private long                journalTransactionID;

  @SuppressWarnings("unused")
  private WalletTransaction() {}

  public WalletTransaction(int accountKey, long transactionID, long date, int quantity, String typeName, int typeID, BigDecimal price, long clientID,
                           String clientName, int stationID, String stationName, String transactionType, String transactionFor, long journalTransactionID) {
    this.accountKey = accountKey;
    this.transactionID = transactionID;
    this.date = date;
    this.quantity = quantity;
    this.typeName = typeName;
    this.typeID = typeID;
    this.price = price;
    this.clientID = clientID;
    this.clientName = clientName;
    this.stationID = stationID;
    this.stationName = stationName;
    this.transactionType = transactionType;
    this.transactionFor = transactionFor;
    this.journalTransactionID = journalTransactionID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof WalletTransaction)) return false;
    WalletTransaction other = (WalletTransaction) sup;

    return accountKey == other.accountKey && transactionID == other.transactionID && date == other.date && quantity == other.quantity
        && nullSafeObjectCompare(typeName, other.typeName) && typeID == other.typeID && nullSafeObjectCompare(price, other.price) && clientID == other.clientID
        && nullSafeObjectCompare(clientName, other.clientName) && stationID == other.stationID && nullSafeObjectCompare(stationName, other.stationName)
        && nullSafeObjectCompare(transactionType, other.transactionType) && nullSafeObjectCompare(transactionFor, other.transactionFor)
        && journalTransactionID == other.journalTransactionID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getAccountKey() {
    return accountKey;
  }

  public long getTransactionID() {
    return transactionID;
  }

  public long getDate() {
    return date;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getTypeName() {
    return typeName;
  }

  public int getTypeID() {
    return typeID;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public long getClientID() {
    return clientID;
  }

  public String getClientName() {
    return clientName;
  }

  public int getStationID() {
    return stationID;
  }

  public String getStationName() {
    return stationName;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public String getTransactionFor() {
    return transactionFor;
  }

  public long getJournalTransactionID() {
    return journalTransactionID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + accountKey;
    result = prime * result + (int) (clientID ^ (clientID >>> 32));
    result = prime * result + ((clientName == null) ? 0 : clientName.hashCode());
    result = prime * result + (int) (date ^ (date >>> 32));
    result = prime * result + (int) (journalTransactionID ^ (journalTransactionID >>> 32));
    result = prime * result + ((price == null) ? 0 : price.hashCode());
    result = prime * result + quantity;
    result = prime * result + stationID;
    result = prime * result + ((stationName == null) ? 0 : stationName.hashCode());
    result = prime * result + ((transactionFor == null) ? 0 : transactionFor.hashCode());
    result = prime * result + (int) (transactionID ^ (transactionID >>> 32));
    result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
    result = prime * result + typeID;
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    WalletTransaction other = (WalletTransaction) obj;
    if (accountKey != other.accountKey) return false;
    if (clientID != other.clientID) return false;
    if (clientName == null) {
      if (other.clientName != null) return false;
    } else if (!clientName.equals(other.clientName)) return false;
    if (date != other.date) return false;
    if (journalTransactionID != other.journalTransactionID) return false;
    if (price == null) {
      if (other.price != null) return false;
    } else if (!price.equals(other.price)) return false;
    if (quantity != other.quantity) return false;
    if (stationID != other.stationID) return false;
    if (stationName == null) {
      if (other.stationName != null) return false;
    } else if (!stationName.equals(other.stationName)) return false;
    if (transactionFor == null) {
      if (other.transactionFor != null) return false;
    } else if (!transactionFor.equals(other.transactionFor)) return false;
    if (transactionID != other.transactionID) return false;
    if (transactionType == null) {
      if (other.transactionType != null) return false;
    } else if (!transactionType.equals(other.transactionType)) return false;
    if (typeID != other.typeID) return false;
    if (typeName == null) {
      if (other.typeName != null) return false;
    } else if (!typeName.equals(other.typeName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "WalletTransaction [accountKey=" + accountKey + ", transactionID=" + transactionID + ", date=" + date + ", quantity=" + quantity + ", typeName="
        + typeName + ", typeID=" + typeID + ", price=" + price + ", clientID=" + clientID + ", clientName=" + clientName + ", stationID=" + stationID
        + ", stationName=" + stationName + ", transactionType=" + transactionType + ", transactionFor=" + transactionFor + ", journalTransactionID="
        + journalTransactionID + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve a wallet transaction entry by key, live at the given time.
   * 
   * @param owner
   *          wallet transaction owner
   * @param time
   *          time when the wallet transaction should be live
   * @param transactionID
   *          wallet transaction ID
   * @return an existing wallet transaction, or null if a live entry with the given attributes can not be found
   */
  public static WalletTransaction get(final SynchronizedEveAccount owner, final long time, final long transactionID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<WalletTransaction>() {
        @Override
        public WalletTransaction run() throws Exception {
          TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("WalletTransaction.getByTransactionID", WalletTransaction.class);
          getter.setParameter("owner", owner);
          getter.setParameter("transid", transactionID);
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
   * Retrieve wallet transactions live at the given time with date greater than "contid".
   * 
   * @param owner
   *          wallet transactions owner
   * @param time
   *          time at which wallet transactions must be live
   * @param maxresults
   *          max wallet transactions to return
   * @param contid
   *          minimum date (exclusive) for returned wallet transactions
   * @return a list of wallet transactions live at the given time, no longer than maxresults, with date greater than contid, ordered increasing by date
   */
  public static List<WalletTransaction> getAllForward(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletTransaction.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletTransaction>>() {
        @Override
        public List<WalletTransaction> run() throws Exception {
          TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("WalletTransaction.getAllForward",
                                                                                                                            WalletTransaction.class);
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
   * Retrieve wallet transactions live at the given time with date less than "contid"
   * 
   * @param owner
   *          wallet transactions owner
   * @param time
   *          time at which wallet transactions must be live
   * @param maxresults
   *          max wallet transactions to return
   * @param contid
   *          maximum date (exclusive) for returned wallet transactions
   * @return a list of wallet transactions live at the given time, no longer than max results, with date less than contid, ordered decreasing by date
   */
  public static List<WalletTransaction> getAllBackward(final SynchronizedEveAccount owner, final long time, int maxresults, final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletTransaction.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletTransaction>>() {
        @Override
        public List<WalletTransaction> run() throws Exception {
          TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("WalletTransaction.getAllBackward",
                                                                                                                            WalletTransaction.class);
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
   * Retrieve wallet transactions live at the given time, with dates in the provided range.
   * 
   * @param owner
   *          wallet transactions owner
   * @param time
   *          time at which wallet transactions must be live
   * @param maxresults
   *          max wallet transactions to return
   * @param mindate
   *          lower bound (inclusive) for date range
   * @param maxdate
   *          upper bound (inclusive) for date range
   * @param ascending
   *          if true, return results in increasing order by date, otherwise results are ordered decreasing by date
   * @return a list of wallet transactions live at the given time, no longer than maxresults, with date in the provided range, ordered either increasing or
   *         decreasing depending on "ascending"
   */
  public static List<WalletTransaction> getRange(
                                                 final SynchronizedEveAccount owner,
                                                 final long time,
                                                 int maxresults,
                                                 final long mindate,
                                                 final long maxdate,
                                                 final boolean ascending) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletTransaction.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletTransaction>>() {
        @Override
        public List<WalletTransaction> run() throws Exception {
          TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery(ascending ? "WalletTransaction.getRangeAsc" : "WalletTransaction.getRangeDesc", WalletTransaction.class);
          getter.setParameter("owner", owner);
          getter.setParameter("mindate", mindate);
          getter.setParameter("maxdate", maxdate);
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
