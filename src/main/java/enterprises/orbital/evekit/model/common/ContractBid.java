package enterprises.orbital.evekit.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
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
    name = "evekit_data_contract_bid",
    indexes = {
        @Index(
            name = "contractIDIndex",
            columnList = "contractID"),
        @Index(
            name = "bidIDIndex",
            columnList = "bidID"),
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
public class ContractBid extends CachedData {
  private static final Logger log = Logger.getLogger(ContractBid.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS);
  private int bidID;
  private int contractID;
  private int bidderID;
  private long dateBid = -1;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal amount;
  @Transient
  @ApiModelProperty(
      value = "dateBid Date")
  @JsonProperty("dateBidDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateBidDate;

  @SuppressWarnings("unused")
  protected ContractBid() {}

  public ContractBid(int bidID, int contractID, int bidderID, long dateBid, BigDecimal amount) {
    super();
    this.bidID = bidID;
    this.contractID = contractID;
    this.bidderID = bidderID;
    this.dateBid = dateBid;
    this.amount = amount;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    dateBidDate = assignDateField(dateBid);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
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

  public int getBidID() {
    return bidID;
  }

  public int getContractID() {
    return contractID;
  }

  public int getBidderID() {
    return bidderID;
  }

  public long getDateBid() {
    return dateBid;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ContractBid that = (ContractBid) o;
    return bidID == that.bidID &&
        contractID == that.contractID &&
        bidderID == that.bidderID &&
        dateBid == that.dateBid &&
        Objects.equals(amount, that.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), bidID, contractID, bidderID, dateBid, amount);
  }

  @Override
  public String toString() {
    return "ContractBid{" +
        "bidID=" + bidID +
        ", contractID=" + contractID +
        ", bidderID=" + bidderID +
        ", dateBid=" + dateBid +
        ", amount=" + amount +
        ", dateBidDate=" + dateBidDate +
        '}';
  }

  /**
   * Return contract bid with given properties, live at the given time. Returns null if no such contract bid exists.
   *
   * @param owner      contract bid owner
   * @param time       time at which the contract must be live
   * @param contractID contract ID of bid
   * @param bidID      bid ID of bid
   * @return contract bid with the given properties live at the given time, or null.
   */
  public static ContractBid get(
      final SynchronizedEveAccount owner,
      final long time,
      final int contractID,
      final int bidID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ContractBid> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "ContractBid.getByContractAndBidID",
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
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<ContractBid> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector bidID,
      final AttributeSelector contractID,
      final AttributeSelector bidderID,
      final AttributeSelector dateBid,
      final AttributeSelector amount) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ContractBid c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "bidID", bidID);
                                        AttributeSelector.addIntSelector(qs, "c", "contractID", contractID);
                                        AttributeSelector.addIntSelector(qs, "c", "bidderID", bidderID);
                                        AttributeSelector.addLongSelector(qs, "c", "dateBid", dateBid);
                                        AttributeSelector.addDoubleSelector(qs, "c", "amount", amount);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ContractBid> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     ContractBid.class);
                                        query.setParameter("owner", owner);
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
