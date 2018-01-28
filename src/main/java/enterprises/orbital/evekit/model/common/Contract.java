package enterprises.orbital.evekit.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    name = "evekit_data_contract",
    indexes = {
        @Index(
            name = "contractIDIndex",
            columnList = "contractID"),
        @Index(
            name = "dateIssuedIndex",
            columnList = "dateIssued"),
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
  private static final Logger log = Logger.getLogger(Contract.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS);
  private int contractID;
  private int issuerID;
  private int issuerCorpID;
  private int assigneeID;
  private int acceptorID;
  private long startStationID;
  private long endStationID;
  private String type;
  private String status;
  private String title;
  private boolean forCorp;
  private String availability;
  private long dateIssued = -1;
  private long dateExpired = -1;
  private long dateAccepted = -1;
  private int numDays;
  private long dateCompleted = -1;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal price;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal reward;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal collateral;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal buyout;
  private double volume;
  @Transient
  @ApiModelProperty(
      value = "dateIssued Date")
  @JsonProperty("dateIssuedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateIssuedDate;
  @Transient
  @ApiModelProperty(
      value = "dateExpired Date")
  @JsonProperty("dateExpiredDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateExpiredDate;
  @Transient
  @ApiModelProperty(
      value = "dateAccepted Date")
  @JsonProperty("dateAcceptedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateAcceptedDate;
  @Transient
  @ApiModelProperty(
      value = "dateCompleted Date")
  @JsonProperty("dateCompletedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateCompletedDate;

  @SuppressWarnings("unused")
  protected Contract() {}

  public Contract(int contractID, int issuerID, int issuerCorpID, int assigneeID, int acceptorID,
                  long startStationID, long endStationID, String type,
                  String status, String title, boolean forCorp, String availability, long dateIssued, long dateExpired,
                  long dateAccepted, int numDays,
                  long dateCompleted, BigDecimal price, BigDecimal reward, BigDecimal collateral, BigDecimal buyout,
                  double volume) {
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
   * Update transient date values for readability.
   */
  @SuppressWarnings("Duplicates")
  @Override
  public void prepareTransient() {
    fixDates();
    dateIssuedDate = assignDateField(dateIssued);
    dateExpiredDate = assignDateField(dateExpired);
    dateAcceptedDate = assignDateField(dateAccepted);
    dateCompletedDate = assignDateField(dateCompleted);
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
        && nullSafeObjectCompare(type, other.type) && nullSafeObjectCompare(status,
                                                                            other.status) && nullSafeObjectCompare(
        title, other.title)
        && forCorp == other.forCorp && nullSafeObjectCompare(availability,
                                                             other.availability) && dateIssued == other.dateIssued
        && dateExpired == other.dateExpired && dateAccepted == other.dateAccepted && numDays == other.numDays && dateCompleted == other.dateCompleted
        && nullSafeObjectCompare(price, other.price) && nullSafeObjectCompare(reward,
                                                                              other.reward) && nullSafeObjectCompare(
        collateral, other.collateral)
        && nullSafeObjectCompare(buyout, other.buyout) && volume == other.volume;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getContractID() {
    return contractID;
  }

  public int getIssuerID() {
    return issuerID;
  }

  public int getIssuerCorpID() {
    return issuerCorpID;
  }

  public int getAssigneeID() {
    return assigneeID;
  }

  public int getAcceptorID() {
    return acceptorID;
  }

  public long getStartStationID() {
    return startStationID;
  }

  public long getEndStationID() {
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

  public double getVolume() {
    return volume;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Contract contract = (Contract) o;
    return contractID == contract.contractID &&
        issuerID == contract.issuerID &&
        issuerCorpID == contract.issuerCorpID &&
        assigneeID == contract.assigneeID &&
        acceptorID == contract.acceptorID &&
        startStationID == contract.startStationID &&
        endStationID == contract.endStationID &&
        forCorp == contract.forCorp &&
        dateIssued == contract.dateIssued &&
        dateExpired == contract.dateExpired &&
        dateAccepted == contract.dateAccepted &&
        numDays == contract.numDays &&
        dateCompleted == contract.dateCompleted &&
        Double.compare(contract.volume, volume) == 0 &&
        Objects.equals(type, contract.type) &&
        Objects.equals(status, contract.status) &&
        Objects.equals(title, contract.title) &&
        Objects.equals(availability, contract.availability) &&
        Objects.equals(price, contract.price) &&
        Objects.equals(reward, contract.reward) &&
        Objects.equals(collateral, contract.collateral) &&
        Objects.equals(buyout, contract.buyout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), contractID, issuerID, issuerCorpID, assigneeID, acceptorID, startStationID,
                        endStationID, type, status, title, forCorp, availability, dateIssued, dateExpired, dateAccepted,
                        numDays, dateCompleted, price, reward, collateral, buyout, volume);
  }

  @Override
  public String toString() {
    return "Contract{" +
        "contractID=" + contractID +
        ", issuerID=" + issuerID +
        ", issuerCorpID=" + issuerCorpID +
        ", assigneeID=" + assigneeID +
        ", acceptorID=" + acceptorID +
        ", startStationID=" + startStationID +
        ", endStationID=" + endStationID +
        ", type='" + type + '\'' +
        ", status='" + status + '\'' +
        ", title='" + title + '\'' +
        ", forCorp=" + forCorp +
        ", availability='" + availability + '\'' +
        ", dateIssued=" + dateIssued +
        ", dateExpired=" + dateExpired +
        ", dateAccepted=" + dateAccepted +
        ", numDays=" + numDays +
        ", dateCompleted=" + dateCompleted +
        ", price=" + price +
        ", reward=" + reward +
        ", collateral=" + collateral +
        ", buyout=" + buyout +
        ", volume=" + volume +
        ", dateIssuedDate=" + dateIssuedDate +
        ", dateExpiredDate=" + dateExpiredDate +
        ", dateAcceptedDate=" + dateAcceptedDate +
        ", dateCompletedDate=" + dateCompletedDate +
        '}';
  }

  /**
   * Return contract with given ID, live at the given time. Returns null if no such contract exists.
   *
   * @param owner      contract owner
   * @param time       time at which the contract must be live
   * @param contractID contract ID
   * @return contract with the given ID live at the given time, or null.
   */
  public static Contract get(
      final SynchronizedEveAccount owner,
      final long time,
      final int contractID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Contract> getter = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery(
                                                                                                   "Contract.getByContractID",
                                                                                                   Contract.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("contract", contractID);
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
      final AttributeSelector volume) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Contract c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "contractID", contractID);
                                        AttributeSelector.addIntSelector(qs, "c", "issuerID", issuerID);
                                        AttributeSelector.addIntSelector(qs, "c", "issuerCorpID", issuerCorpID);
                                        AttributeSelector.addIntSelector(qs, "c", "assigneeID", assigneeID);
                                        AttributeSelector.addIntSelector(qs, "c", "acceptorID", acceptorID);
                                        AttributeSelector.addLongSelector(qs, "c", "startStationID", startStationID);
                                        AttributeSelector.addLongSelector(qs, "c", "endStationID", endStationID);
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
                                        AttributeSelector.addDoubleSelector(qs, "c", "volume", volume);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Contract> query = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(
                                                                                                  qs.toString(),
                                                                                                  Contract.class);
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
