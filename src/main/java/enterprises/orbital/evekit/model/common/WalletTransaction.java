package enterprises.orbital.evekit.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_wallet_transaction",
    indexes = {
        @Index(
            name = "divisionIndex",
            columnList = "division"),
        @Index(
            name = "transactionIDIndex",
            columnList = "transactionID")
    })
@NamedQueries({
    @NamedQuery(
        name = "WalletTransaction.getByTransactionIDAndDivision",
        query = "SELECT c FROM WalletTransaction c where c.owner = :owner and c.division = :division and c.transactionID = :transid and c.lifeStart <= :point and c.lifeEnd > :point"),
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
public class WalletTransaction extends CachedData {
  private static final Logger log = Logger.getLogger(WalletTransaction.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_TRANSACTIONS);
  private static final int DEFAULT_MAX_RESULTS = 1000;
  private int division;
  private long transactionID;
  private long date = -1;
  private int quantity;
  private int typeID;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal price;
  private int clientID;
  private long locationID;
  private boolean isBuy;
  private boolean isPersonal;
  private long journalTransactionID;

  // Transient fields

  @Transient
  @ApiModelProperty(
      value = "date Date")
  @JsonProperty("dateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateDate;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* accountKey")
  @JsonProperty("accountKey")
  private int accountKey;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* stationID")
  @JsonProperty("stationID")
  private long stationID;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* transactionType")
  @JsonProperty("transactionType")
  private String transactionType;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* transactionFor")
  @JsonProperty("transactionFor")
  private String transactionFor;

  @SuppressWarnings("unused")
  protected WalletTransaction() {}

  public WalletTransaction(int division, long transactionID, long date, int quantity, int typeID,
                           BigDecimal price, int clientID, long locationID, boolean isBuy, boolean isPersonal,
                           long journalTransactionID) {
    this.division = division;
    this.transactionID = transactionID;
    this.date = date;
    this.quantity = quantity;
    this.typeID = typeID;
    this.price = price;
    this.clientID = clientID;
    this.locationID = locationID;
    this.isBuy = isBuy;
    this.isPersonal = isPersonal;
    this.journalTransactionID = journalTransactionID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    dateDate = assignDateField(date);
    accountKey = division - 1 + 1000;
    stationID = locationID;
    transactionType = isBuy ? "buy" : "sell";
    transactionFor = isPersonal ? "personal" : "corporation";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof WalletTransaction)) return false;
    WalletTransaction other = (WalletTransaction) sup;

    return division == other.division && transactionID == other.transactionID && date == other.date && quantity == other.quantity
        && typeID == other.typeID && nullSafeObjectCompare(price, other.price) && clientID == other.clientID
        && locationID == other.locationID && isBuy == other.isBuy && isPersonal == other.isPersonal
        && journalTransactionID == other.journalTransactionID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(division, transactionID, date, quantity, typeID, price, clientID, locationID, isBuy,
                          isPersonal, journalTransactionID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
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

  public int getTypeID() {
    return typeID;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getClientID() {
    return clientID;
  }

  public long getJournalTransactionID() {
    return journalTransactionID;
  }

  public int getDivision() {
    return division;
  }

  public long getLocationID() {
    return locationID;
  }

  public boolean isBuy() {
    return isBuy;
  }

  public boolean isPersonal() {
    return isPersonal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WalletTransaction that = (WalletTransaction) o;
    return division == that.division &&
        transactionID == that.transactionID &&
        date == that.date &&
        quantity == that.quantity &&
        typeID == that.typeID &&
        clientID == that.clientID &&
        locationID == that.locationID &&
        isBuy == that.isBuy &&
        isPersonal == that.isPersonal &&
        journalTransactionID == that.journalTransactionID &&
        Objects.equals(price, that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), division, transactionID, date, quantity, typeID, price, clientID, locationID,
                        isBuy, isPersonal, journalTransactionID);
  }

  @Override
  public String toString() {
    return "WalletTransaction{" +
        "division=" + division +
        ", transactionID=" + transactionID +
        ", date=" + date +
        ", quantity=" + quantity +
        ", typeID=" + typeID +
        ", price=" + price +
        ", clientID=" + clientID +
        ", locationID=" + locationID +
        ", isBuy=" + isBuy +
        ", isPersonal=" + isPersonal +
        ", journalTransactionID=" + journalTransactionID +
        '}';
  }

  /**
   * Retrieve a wallet transaction entry by key, live at the given time.
   *
   * @param owner         wallet transaction owner
   * @param time          time when the wallet transaction should be live
   * @param division      division in which the transaction is associated with
   * @param transactionID wallet transaction ID
   * @return an existing wallet transaction, or null if a live entry with the given attributes can not be found
   */
  public static WalletTransaction get(
      final SynchronizedEveAccount owner,
      final long time,
      final int division,
      final long transactionID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "WalletTransaction.getByTransactionIDAndDivision",
                                                                                                            WalletTransaction.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("transid", transactionID);
                                        getter.setParameter("division", division);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  /**
   * Retrieve wallet transactions live at the given time with date greater than "contid".
   *
   * @param owner      wallet transactions owner
   * @param time       time at which wallet transactions must be live
   * @param maxresults max wallet transactions to return
   * @param contid     minimum date (exclusive) for returned wallet transactions
   * @return a list of wallet transactions live at the given time, no longer than maxresults, with date greater than contid, ordered increasing by date
   */
  public static List<WalletTransaction> getAllForward(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletTransaction.class, "maxresults"),
                                     DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "WalletTransaction.getAllForward",
                                                                                                            WalletTransaction.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("contid", contid);
                                        getter.setParameter("point", time);
                                        getter.setMaxResults(maxr);
                                        return getter.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  /**
   * Retrieve wallet transactions live at the given time with date less than "contid"
   *
   * @param owner      wallet transactions owner
   * @param time       time at which wallet transactions must be live
   * @param maxresults max wallet transactions to return
   * @param contid     maximum date (exclusive) for returned wallet transactions
   * @return a list of wallet transactions live at the given time, no longer than max results, with date less than contid, ordered decreasing by date
   */
  public static List<WalletTransaction> getAllBackward(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletTransaction.class, "maxresults"),
                                     DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "WalletTransaction.getAllBackward",
                                                                                                            WalletTransaction.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("contid", contid);
                                        getter.setParameter("point", time);
                                        getter.setMaxResults(maxr);
                                        return getter.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  /**
   * Retrieve wallet transactions live at the given time, with dates in the provided range.
   *
   * @param owner      wallet transactions owner
   * @param time       time at which wallet transactions must be live
   * @param maxresults max wallet transactions to return
   * @param mindate    lower bound (inclusive) for date range
   * @param maxdate    upper bound (inclusive) for date range
   * @param ascending  if true, return results in increasing order by date, otherwise results are ordered decreasing by date
   * @return a list of wallet transactions live at the given time, no longer than maxresults, with date in the provided range, ordered either increasing or
   * decreasing depending on "ascending"
   */
  public static List<WalletTransaction> getRange(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long mindate,
      final long maxdate,
      final boolean ascending) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletTransaction.class, "maxresults"),
                                     DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletTransaction> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            ascending ? "WalletTransaction.getRangeAsc" : "WalletTransaction.getRangeDesc",
                                                                                                            WalletTransaction.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("mindate", mindate);
                                        getter.setParameter("maxdate", maxdate);
                                        getter.setParameter("point", time);
                                        getter.setMaxResults(maxr);
                                        return getter.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<WalletTransaction> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector division,
      final AttributeSelector transactionID,
      final AttributeSelector date,
      final AttributeSelector quantity,
      final AttributeSelector typeID,
      final AttributeSelector price,
      final AttributeSelector clientID,
      final AttributeSelector locationID,
      final AttributeSelector isBuy,
      final AttributeSelector isPersonal,
      final AttributeSelector journalTransactionID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM WalletTransaction c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "division", division);
                                        AttributeSelector.addLongSelector(qs, "c", "transactionID", transactionID);
                                        AttributeSelector.addLongSelector(qs, "c", "date", date);
                                        AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addDoubleSelector(qs, "c", "price", price);
                                        AttributeSelector.addLongSelector(qs, "c", "clientID", clientID);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isBuy", isBuy);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isPersonal", isPersonal);
                                        AttributeSelector.addLongSelector(qs, "c", "journalTransactionID",
                                                                          journalTransactionID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<WalletTransaction> query = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(
                                                                                                           qs.toString(),
                                                                                                           WalletTransaction.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
