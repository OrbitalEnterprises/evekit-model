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
    name = "evekit_data_market_order",
    indexes = {
        @Index(
            name = "orderIDIndex",
            columnList = "orderID"),
        @Index(
            name = "issuedIndex",
            columnList = "issued"),
        @Index(
            name = "orderStateIndex",
            columnList = "orderState"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MarketOrder.getByOrderID",
        query = "SELECT c FROM MarketOrder c where c.owner = :owner and c.orderID = :orderid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "MarketOrder.getByIssuedForward",
        query = "SELECT c FROM MarketOrder c where c.owner = :owner and c.issued > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.issued asc"),
    @NamedQuery(
        name = "MarketOrder.getByIssuedBackward",
        query = "SELECT c FROM MarketOrder c where c.owner = :owner and c.issued < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.issued desc"),
    @NamedQuery(
        name = "MarketOrder.getAllActive",
        query = "SELECT c FROM MarketOrder c where c.owner = :owner and c.issued > :contid and c.issued > :bound and c.orderState = 'open' and c.lifeStart <= :point and c.lifeEnd > :point order by c.issued asc"),
})
public class MarketOrder extends CachedData {
  private static final Logger log = Logger.getLogger(MarketOrder.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MARKET_ORDERS);
  private static final int DEFAULT_MAX_RESULTS = 1000;
  private long orderID;
  private int walletDivision;
  private boolean bid;
  private long charID;
  private int duration;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal escrow;
  private long issued = -1;
  private int issuedBy = 0;
  private int minVolume;
  private String orderState;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal price;
  private String orderRange;
  private int typeID;
  private int volEntered;
  private int volRemaining;
  private int regionID;
  private long locationID;
  private boolean isCorp;

  @Transient
  @ApiModelProperty(
      value = "issued Date")
  @JsonProperty("issuedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date issuedDate;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* accountKey")
  @JsonProperty("accountKey")
  private int accountKey;

  @SuppressWarnings("unused")
  protected MarketOrder() {}

  public MarketOrder(long orderID, int walletDivision, boolean bid, long charID, int duration,
                     BigDecimal escrow, long issued, int issuedBy, int minVolume, String orderState, BigDecimal price,
                     String orderRange, int typeID, int volEntered, int volRemaining, int regionID, long locationID,
                     boolean isCorp) {
    this.orderID = orderID;
    this.walletDivision = walletDivision;
    this.bid = bid;
    this.charID = charID;
    this.duration = duration;
    this.escrow = escrow;
    this.issued = issued;
    this.issuedBy = issuedBy;
    this.minVolume = minVolume;
    this.orderState = orderState;
    this.price = price;
    this.orderRange = orderRange;
    this.typeID = typeID;
    this.volEntered = volEntered;
    this.volRemaining = volRemaining;
    this.regionID = regionID;
    this.locationID = locationID;
    this.isCorp = isCorp;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    issuedDate = assignDateField(issued);
    accountKey = walletDivision - 1 + 1000;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MarketOrder)) return false;
    MarketOrder other = (MarketOrder) sup;
    return orderID == other.orderID &&
        walletDivision == other.walletDivision &&
        bid == other.bid &&
        charID == other.charID &&
        duration == other.duration &&
        nullSafeObjectCompare(escrow, other.escrow) &&
        issued == other.issued &&
        issuedBy == other.issuedBy &&
        minVolume == other.minVolume &&
        nullSafeObjectCompare(orderState, other.orderState) &&
        nullSafeObjectCompare(price, other.price) &&
        nullSafeObjectCompare(orderRange, other.orderRange) &&
        typeID == other.typeID &&
        volEntered == other.volEntered &&
        volRemaining == other.volRemaining &&
        regionID == other.regionID &&
        locationID == other.locationID &&
        isCorp == other.isCorp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getOrderID() {
    return orderID;
  }

  public boolean isBid() {
    return bid;
  }

  public long getCharID() {
    return charID;
  }

  public int getDuration() {
    return duration;
  }

  public BigDecimal getEscrow() {
    return escrow;
  }

  public long getIssued() {
    return issued;
  }

  public int getIssuedBy() { return issuedBy; }

  public int getMinVolume() {
    return minVolume;
  }

  public String getOrderState() {
    return orderState;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getOrderRange() {
    return orderRange;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getVolEntered() {
    return volEntered;
  }

  public int getVolRemaining() {
    return volRemaining;
  }

  public int getWalletDivision() {
    return walletDivision;
  }

  public int getRegionID() {
    return regionID;
  }

  public long getLocationID() {
    return locationID;
  }

  public boolean isCorp() {
    return isCorp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MarketOrder that = (MarketOrder) o;
    return orderID == that.orderID &&
        walletDivision == that.walletDivision &&
        bid == that.bid &&
        charID == that.charID &&
        duration == that.duration &&
        issued == that.issued &&
        issuedBy == that.issuedBy &&
        minVolume == that.minVolume &&
        typeID == that.typeID &&
        volEntered == that.volEntered &&
        volRemaining == that.volRemaining &&
        regionID == that.regionID &&
        locationID == that.locationID &&
        isCorp == that.isCorp &&
        Objects.equals(escrow, that.escrow) &&
        Objects.equals(orderState, that.orderState) &&
        Objects.equals(price, that.price) &&
        Objects.equals(orderRange, that.orderRange);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), orderID, walletDivision, bid, charID, duration, escrow, issued, issuedBy,
                        minVolume, orderState, price, orderRange, typeID, volEntered, volRemaining, regionID,
                        locationID,
                        isCorp);
  }

  @Override
  public String toString() {
    return "MarketOrder{" +
        "orderID=" + orderID +
        ", walletDivision=" + walletDivision +
        ", bid=" + bid +
        ", charID=" + charID +
        ", duration=" + duration +
        ", escrow=" + escrow +
        ", issued=" + issued +
        ", issuedBy=" + issuedBy +
        ", minVolume=" + minVolume +
        ", orderState='" + orderState + '\'' +
        ", price=" + price +
        ", orderRange='" + orderRange + '\'' +
        ", typeID=" + typeID +
        ", volEntered=" + volEntered +
        ", volRemaining=" + volRemaining +
        ", regionID=" + regionID +
        ", locationID=" + locationID +
        ", isCorp=" + isCorp +
        ", issuedDate=" + issuedDate +
        '}';
  }

  /**
   * Retrieve market order with the given parameters live at the given time, or null if no such market order exists.
   *
   * @param owner   market order owner
   * @param time    time at which market order must be live
   * @param orderID market order ID
   * @return market order with the given parameters, live at the given time, or null
   */
  public static MarketOrder get(
      final SynchronizedEveAccount owner,
      final long time,
      final long orderID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "MarketOrder.getByOrderID",
                                                                                                      MarketOrder.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("orderid", orderID);
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
   * Retrieve list of market orders live at the given time with issued date greater than "contid".
   *
   * @param owner      market orders owner
   * @param time       time at which market orders must be live
   * @param maxresults maximum number of market orders to retrieve
   * @param contid     issued time (exclusive) after which market orders should be returned
   * @return list of market orders live at the given time with issued date greater than "contid"
   */
  public static List<MarketOrder> getAllForward(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MarketOrder.class, "maxresults"),
                                     DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "MarketOrder.getByIssuedForward",
                                                                                                      MarketOrder.class);
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
   * Retrieve list of market orders live at the given time with issued date less than "contid"
   *
   * @param owner      market orders owner
   * @param time       time at which market orders must be live
   * @param maxresults maximum number of market orders to retrieve
   * @param contid     issued date (exclusive) before which market orders should be retrieved
   * @return list of market orders live at the given time with issued date less than "contid"
   */
  public static List<MarketOrder> getAllBackward(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MarketOrder.class, "maxresults"),
                                     DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "MarketOrder.getByIssuedBackward",
                                                                                                      MarketOrder.class);
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
   * Retrieve list of open market orders live at the given time such that issued + duration &gt; reftime and issued date greater than "contid"
   *
   * @param owner         market orders owner
   * @param time          time at which market orders must be live
   * @param maxresults    maximum number of market orders to retrieve
   * @param contid        issued date (exclusive) after which market orders will be returned
   * @param reftime       reference time after which orders will expire
   * @param durationBound duration (in days) to add to issued date for expiry check purposes
   * @return list of open market orders live at the given time such that issued + duration &gt; reftime and issued date greater than "contid"
   */
  public static List<MarketOrder> getAllActive(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid,
      final long reftime,
      final int durationBound) throws IOException {
    final long bound = reftime - (durationBound * (24 * 60 * 60 * 1000L));
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MarketOrder.class, "maxresults"),
                                     DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "MarketOrder.getAllActive",
                                                                                                      MarketOrder.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("contid", contid);
                                        getter.setParameter("bound", bound);
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

  public static List<MarketOrder> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector orderID,
      final AttributeSelector walletDivision,
      final AttributeSelector bid,
      final AttributeSelector charID,
      final AttributeSelector duration,
      final AttributeSelector escrow,
      final AttributeSelector issued,
      final AttributeSelector issuedBy,
      final AttributeSelector minVolume,
      final AttributeSelector orderState,
      final AttributeSelector price,
      final AttributeSelector orderRange,
      final AttributeSelector typeID,
      final AttributeSelector volEntered,
      final AttributeSelector volRemaining,
      final AttributeSelector regionID,
      final AttributeSelector locationID,
      final AttributeSelector isCorp) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MarketOrder c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "orderID", orderID);
                                        AttributeSelector.addIntSelector(qs, "c", "accountKey", walletDivision);
                                        AttributeSelector.addBooleanSelector(qs, "c", "bid", bid);
                                        AttributeSelector.addLongSelector(qs, "c", "charID", charID);
                                        AttributeSelector.addIntSelector(qs, "c", "duration", duration);
                                        AttributeSelector.addDoubleSelector(qs, "c", "escrow", escrow);
                                        AttributeSelector.addLongSelector(qs, "c", "issued", issued);
                                        AttributeSelector.addIntSelector(qs, "c", "issuedBy", issuedBy);
                                        AttributeSelector.addIntSelector(qs, "c", "minVolume", minVolume);
                                        AttributeSelector.addStringSelector(qs, "c", "orderState", orderState, p);
                                        AttributeSelector.addDoubleSelector(qs, "c", "price", price);
                                        AttributeSelector.addStringSelector(qs, "c", "orderRange", orderRange, p);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "volEntered", volEntered);
                                        AttributeSelector.addIntSelector(qs, "c", "volRemaining", volRemaining);
                                        AttributeSelector.addIntSelector(qs, "c", "regionID", regionID);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addBooleanSelector(qs, "c", "isCorp", isCorp);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MarketOrder> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     MarketOrder.class);
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
