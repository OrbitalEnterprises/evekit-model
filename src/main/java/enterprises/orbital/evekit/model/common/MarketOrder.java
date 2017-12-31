package enterprises.orbital.evekit.model.common;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_market_order",
    indexes = {
        @Index(
            name = "orderIDIndex",
            columnList = "orderID",
            unique = false),
        @Index(
            name = "issuedIndex",
            columnList = "issued",
            unique = false),
        @Index(
            name = "orderStateIndex",
            columnList = "orderState",
            unique = false),
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
        query = "SELECT c FROM MarketOrder c where c.owner = :owner and c.issued > :contid and c.issued > :bound and c.orderState = 0 and c.lifeStart <= :point and c.lifeEnd > :point order by c.issued asc"),
})
// 2 hour cache time - API caches for 1 hour
public class MarketOrder extends CachedData {
  private static final Logger log                 = Logger.getLogger(MarketOrder.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MARKET_ORDERS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                orderID;
  private int                 accountKey;
  private boolean             bid;
  private long                charID;
  private int                 duration;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          escrow;
  private long                issued              = -1;
  private int                 minVolume;
  private int                 orderState;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          price;
  private int                 orderRange;
  private long                stationID;
  private int                 typeID;
  private int                 volEntered;
  private int                 volRemaining;
  @Transient
  @ApiModelProperty(
      value = "issued Date")
  @JsonProperty("issuedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                issuedDate;

  @SuppressWarnings("unused")
  protected MarketOrder() {}

  public MarketOrder(long orderID, int accountKey, boolean bid, long charID, int duration, BigDecimal escrow, long issued, int minVolume, int orderState,
                     BigDecimal price, int orderRange, long stationID, int typeID, int volEntered, int volRemaining) {
    super();
    this.orderID = orderID;
    this.accountKey = accountKey;
    this.bid = bid;
    this.charID = charID;
    this.duration = duration;
    this.escrow = escrow;
    this.issued = issued;
    this.minVolume = minVolume;
    this.orderState = orderState;
    this.price = price;
    this.orderRange = orderRange;
    this.stationID = stationID;
    this.typeID = typeID;
    this.volEntered = volEntered;
    this.volRemaining = volRemaining;
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
    if (!(sup instanceof MarketOrder)) return false;
    MarketOrder other = (MarketOrder) sup;
    return orderID == other.orderID && accountKey == other.accountKey && bid == other.bid && charID == other.charID && duration == other.duration
        && nullSafeObjectCompare(escrow, other.escrow) && issued == other.issued && minVolume == other.minVolume && orderState == other.orderState
        && nullSafeObjectCompare(price, other.price) && orderRange == other.orderRange && stationID == other.stationID && typeID == other.typeID
        && volEntered == other.volEntered && volRemaining == other.volRemaining;
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

  public int getAccountKey() {
    return accountKey;
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

  public int getMinVolume() {
    return minVolume;
  }

  public int getOrderState() {
    return orderState;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getOrderRange() {
    return orderRange;
  }

  public long getStationID() {
    return stationID;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + accountKey;
    result = prime * result + (bid ? 1231 : 1237);
    result = prime * result + (int) (charID ^ (charID >>> 32));
    result = prime * result + duration;
    result = prime * result + ((escrow == null) ? 0 : escrow.hashCode());
    result = prime * result + (int) (issued ^ (issued >>> 32));
    result = prime * result + minVolume;
    result = prime * result + (int) (orderID ^ (orderID >>> 32));
    result = prime * result + orderRange;
    result = prime * result + orderState;
    result = prime * result + ((price == null) ? 0 : price.hashCode());
    result = prime * result + (int) (stationID ^ (stationID >>> 32));
    result = prime * result + typeID;
    result = prime * result + volEntered;
    result = prime * result + volRemaining;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MarketOrder other = (MarketOrder) obj;
    if (accountKey != other.accountKey) return false;
    if (bid != other.bid) return false;
    if (charID != other.charID) return false;
    if (duration != other.duration) return false;
    if (escrow == null) {
      if (other.escrow != null) return false;
    } else if (!escrow.equals(other.escrow)) return false;
    if (issued != other.issued) return false;
    if (minVolume != other.minVolume) return false;
    if (orderID != other.orderID) return false;
    if (orderRange != other.orderRange) return false;
    if (orderState != other.orderState) return false;
    if (price == null) {
      if (other.price != null) return false;
    } else if (!price.equals(other.price)) return false;
    if (stationID != other.stationID) return false;
    if (typeID != other.typeID) return false;
    if (volEntered != other.volEntered) return false;
    if (volRemaining != other.volRemaining) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MarketOrder [orderID=" + orderID + ", accountKey=" + accountKey + ", bid=" + bid + ", charID=" + charID + ", duration=" + duration + ", escrow="
        + escrow + ", issued=" + issued + ", minVolume=" + minVolume + ", orderState=" + orderState + ", price=" + price + ", orderRange=" + orderRange
        + ", stationID=" + stationID + ", typeID=" + typeID + ", volEntered=" + volEntered + ", volRemaining=" + volRemaining + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve market order with the given parameters live at the given time, or null if no such market order exists.
   * 
   * @param owner
   *          market order owner
   * @param time
   *          time at which market order must be live
   * @param orderID
   *          market order ID
   * @return market order with the given parameters, live at the given time, or null
   */
  public static MarketOrder get(
                                final SynchronizedEveAccount owner,
                                final long time,
                                final long orderID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<MarketOrder>() {
        @Override
        public MarketOrder run() throws Exception {
          TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MarketOrder.getByOrderID",
                                                                                                                      MarketOrder.class);
          getter.setParameter("owner", owner);
          getter.setParameter("orderid", orderID);
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
   * Retrieve list of market orders live at the given time with issued date greater than "contid".
   * 
   * @param owner
   *          market orders owner
   * @param time
   *          time at which market orders must be live
   * @param maxresults
   *          maximum number of market orders to retrieve
   * @param contid
   *          issued time (exclusive) after which market orders should be returned
   * @return list of market orders live at the given time with issued date greater than "contid"
   */
  public static List<MarketOrder> getAllForward(
                                                final SynchronizedEveAccount owner,
                                                final long time,
                                                int maxresults,
                                                final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MarketOrder.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MarketOrder>>() {
        @Override
        public List<MarketOrder> run() throws Exception {
          TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MarketOrder.getByIssuedForward",
                                                                                                                      MarketOrder.class);
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
   * Retrieve list of market orders live at the given time with issued date less than "contid"
   * 
   * @param owner
   *          market orders owner
   * @param time
   *          time at which market orders must be live
   * @param maxresults
   *          maximum number of market orders to retrieve
   * @param contid
   *          issued date (exclusive) before which market orders should be retrieved
   * @return list of market orders live at the given time with issued date less than "contid"
   */
  public static List<MarketOrder> getAllBackward(
                                                 final SynchronizedEveAccount owner,
                                                 final long time,
                                                 int maxresults,
                                                 final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MarketOrder.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MarketOrder>>() {
        @Override
        public List<MarketOrder> run() throws Exception {
          TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MarketOrder.getByIssuedBackward",
                                                                                                                      MarketOrder.class);
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
   * Retrieve list of open market orders live at the given time such that issued + duration &gt; reftime and issued date greater than "contid"
   * 
   * @param owner
   *          market orders owner
   * @param time
   *          time at which market orders must be live
   * @param maxresults
   *          maximum number of market orders to retrieve
   * @param contid
   *          issued date (exclusive) after which market orders will be returned
   * @param reftime
   *          reference time after which orders will expire
   * @param durationBound
   *          duration (in days) to add to issued date for expiry check purposes
   * @return list of open market orders live at the given time such that issued + duration &gt; reftime and issued date greater than "contid"
   */
  public static List<MarketOrder> getAllActive(
                                               final SynchronizedEveAccount owner,
                                               final long time,
                                               int maxresults,
                                               final long contid,
                                               final long reftime,
                                               final int durationBound) {
    final long bound = reftime - (durationBound * (24 * 60 * 60 * 1000L));
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MarketOrder.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MarketOrder>>() {
        @Override
        public List<MarketOrder> run() throws Exception {
          TypedQuery<MarketOrder> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MarketOrder.getAllActive",
                                                                                                                      MarketOrder.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("bound", bound);
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

  public static List<MarketOrder> accessQuery(
                                              final SynchronizedEveAccount owner,
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector orderID,
                                              final AttributeSelector accountKey,
                                              final AttributeSelector bid,
                                              final AttributeSelector charID,
                                              final AttributeSelector duration,
                                              final AttributeSelector escrow,
                                              final AttributeSelector issued,
                                              final AttributeSelector minVolume,
                                              final AttributeSelector orderState,
                                              final AttributeSelector price,
                                              final AttributeSelector orderRange,
                                              final AttributeSelector stationID,
                                              final AttributeSelector typeID,
                                              final AttributeSelector volEntered,
                                              final AttributeSelector volRemaining) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MarketOrder>>() {
        @Override
        public List<MarketOrder> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MarketOrder c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "orderID", orderID);
          AttributeSelector.addIntSelector(qs, "c", "accountKey", accountKey);
          AttributeSelector.addBooleanSelector(qs, "c", "bid", bid);
          AttributeSelector.addLongSelector(qs, "c", "charID", charID);
          AttributeSelector.addIntSelector(qs, "c", "duration", duration);
          AttributeSelector.addDoubleSelector(qs, "c", "escrow", escrow);
          AttributeSelector.addLongSelector(qs, "c", "issued", issued);
          AttributeSelector.addIntSelector(qs, "c", "minVolume", minVolume);
          AttributeSelector.addIntSelector(qs, "c", "orderState", orderState);
          AttributeSelector.addDoubleSelector(qs, "c", "price", price);
          AttributeSelector.addIntSelector(qs, "c", "orderRange", orderRange);
          AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addIntSelector(qs, "c", "volEntered", volEntered);
          AttributeSelector.addIntSelector(qs, "c", "volRemaining", volRemaining);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MarketOrder> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), MarketOrder.class);
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
