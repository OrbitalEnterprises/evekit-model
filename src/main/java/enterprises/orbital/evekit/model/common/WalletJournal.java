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
    name = "evekit_data_wallet_journal",
    indexes = {
        @Index(
            name = "divisionIndex",
            columnList = "division"),
        @Index(
            name = "refIDIndex",
            columnList = "refID"),
        @Index(
            name = "dateIndex",
            columnList = "date")
    })
@NamedQueries({
    @NamedQuery(
        name = "WalletJournal.getByRefIDAndDivision",
        query = "SELECT c FROM WalletJournal c where c.owner = :owner and c.division = :division and c.refID = :refid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "WalletJournal.getAllForward",
        query = "SELECT c FROM WalletJournal c where c.owner = :owner and c.date > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.date asc"),
    @NamedQuery(
        name = "WalletJournal.getAllBackward",
        query = "SELECT c FROM WalletJournal c where c.owner = :owner and c.date < :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.date desc"),
    @NamedQuery(
        name = "WalletJournal.getRangeAsc",
        query = "SELECT c FROM WalletJournal c where c.owner = :owner and c.date >= :mindate and c.date <= :maxdate and c.lifeStart <= :point and c.lifeEnd > :point order by c.date asc"),
    @NamedQuery(
        name = "WalletJournal.getRangeDesc",
        query = "SELECT c FROM WalletJournal c where c.owner = :owner and c.date >= :mindate and c.date <= :maxdate and c.lifeStart <= :point and c.lifeEnd > :point order by c.date desc"),
})
public class WalletJournal extends CachedData {
  private static final Logger log = Logger.getLogger(WalletJournal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_JOURNAL);

  private int division;
  private long refID;
  private long date = -1;
  private String refType;
  private int firstPartyID;
  private int secondPartyID;
  private String argName1;
  private long argID1;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal amount;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal balance;
  private String reason;
  private int taxReceiverID;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal taxAmount;
  private long contextID;
  private String contextType;
  private String description;

  // Transient fields

  @Transient
  @ApiModelProperty(
      value = "date Date")
  @JsonProperty("dateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateDate;

  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* accountKey")
  @JsonProperty("accountKey")
  private int accountKey;

  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* ownerID1")
  @JsonProperty("ownerID1")
  private long ownerID1;

  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* ownerID2")
  @JsonProperty("ownerID2")
  private long ownerID2;

  @SuppressWarnings("unused")
  protected WalletJournal() {}

  public WalletJournal(int division, long refID, long date, String refType, int firstPartyID, int secondPartyID,
                       String argName1, long argID1, BigDecimal amount, BigDecimal balance, String reason,
                       int taxReceiverID, BigDecimal taxAmount, long contextID, String contextType,
                       String description) {
    this.division = division;
    this.refID = refID;
    this.date = date;
    this.refType = refType;
    this.firstPartyID = firstPartyID;
    this.secondPartyID = secondPartyID;
    this.argName1 = argName1;
    this.argID1 = argID1;
    this.amount = amount;
    this.balance = balance;
    this.reason = reason;
    this.taxReceiverID = taxReceiverID;
    this.taxAmount = taxAmount;
    this.contextID = contextID;
    this.contextType = contextType;
    this.description = description;
  }

  /**
   * Update transient date values for readability.
   */
  @SuppressWarnings("deprecation")
  @Override
  public void prepareTransient() {
    fixDates();
    dateDate = assignDateField(date);
    accountKey = division - 1 + 1000;
    ownerID1 = firstPartyID;
    ownerID2 = secondPartyID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof WalletJournal)) return false;
    WalletJournal other = (WalletJournal) sup;

    return division == other.division &&
        refID == other.refID &&
        date == other.date &&
        nullSafeObjectCompare(refType, other.refType) &&
        firstPartyID == other.firstPartyID &&
        secondPartyID == other.secondPartyID &&
        nullSafeObjectCompare(argName1, other.argName1) &&
        argID1 == other.argID1 &&
        nullSafeObjectCompare(amount, other.amount) &&
        nullSafeObjectCompare(balance, other.balance) &&
        nullSafeObjectCompare(reason, other.reason) &&
        taxReceiverID == other.taxReceiverID &&
        nullSafeObjectCompare(taxAmount, other.taxAmount) &&
        contextID == other.contextID &&
        nullSafeObjectCompare(contextType, other.contextType) &&
        nullSafeObjectCompare(description, other.description);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(division, refID, date, refType, firstPartyID, secondPartyID, argName1, argID1, amount,
                          balance, reason, taxReceiverID, taxAmount, contextID, contextType, description);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getRefID() {
    return refID;
  }

  public long getDate() {
    return date;
  }

  public String getArgName1() {
    return argName1;
  }

  public long getArgID1() {
    return argID1;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public String getReason() {
    return reason;
  }

  public int getTaxReceiverID() {
    return taxReceiverID;
  }

  public BigDecimal getTaxAmount() {
    return taxAmount;
  }

  public int getDivision() {
    return division;
  }

  public String getRefType() {
    return refType;
  }

  public int getFirstPartyID() {
    return firstPartyID;
  }

  public int getSecondPartyID() {
    return secondPartyID;
  }

  public long getContextID() {
    return contextID;
  }

  public String getContextType() {
    return contextType;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WalletJournal that = (WalletJournal) o;
    return division == that.division &&
        refID == that.refID &&
        date == that.date &&
        firstPartyID == that.firstPartyID &&
        secondPartyID == that.secondPartyID &&
        argID1 == that.argID1 &&
        taxReceiverID == that.taxReceiverID &&
        contextID == that.contextID &&
        Objects.equals(refType, that.refType) &&
        Objects.equals(argName1, that.argName1) &&
        Objects.equals(amount, that.amount) &&
        Objects.equals(balance, that.balance) &&
        Objects.equals(reason, that.reason) &&
        Objects.equals(taxAmount, that.taxAmount) &&
        Objects.equals(contextType, that.contextType) &&
        Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), division, refID, date, refType, firstPartyID, secondPartyID, argName1, argID1,
                        amount, balance, reason, taxReceiverID, taxAmount, contextID, contextType, description);
  }

  @SuppressWarnings("deprecation")
  @Override
  public String toString() {
    return "WalletJournal{" +
        "division=" + division +
        ", refID=" + refID +
        ", date=" + date +
        ", refType='" + refType + '\'' +
        ", firstPartyID=" + firstPartyID +
        ", secondPartyID=" + secondPartyID +
        ", argName1='" + argName1 + '\'' +
        ", argID1=" + argID1 +
        ", amount=" + amount +
        ", balance=" + balance +
        ", reason='" + reason + '\'' +
        ", taxReceiverID=" + taxReceiverID +
        ", taxAmount=" + taxAmount +
        ", contextID=" + contextID +
        ", contextType='" + contextType + '\'' +
        ", description='" + description + '\'' +
        ", dateDate=" + dateDate +
        ", accountKey=" + accountKey +
        ", ownerID1=" + ownerID1 +
        ", ownerID2=" + ownerID2 +
        '}';
  }

  /**
   * Retrieve a wallet journal entry by key, live at the given time.
   *
   * @param owner    journal entry owner
   * @param time     time at which journal entry must be live
   * @param division division in which journal entry is recorded
   * @param refID    journal entry refID
   * @return an existing journal entry, or null if a live entry with the given attributes can not be found
   */
  public static WalletJournal get(
      final SynchronizedEveAccount owner,
      final long time,
      final int division,
      final long refID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(
                                                                                                        "WalletJournal.getByRefIDAndDivision",
                                                                                                        WalletJournal.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("division", division);
                                        getter.setParameter("refid", refID);
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

  public static List<WalletJournal> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector division,
      final AttributeSelector refID,
      final AttributeSelector date,
      final AttributeSelector refType,
      final AttributeSelector firstPartyID,
      final AttributeSelector secondPartyID,
      final AttributeSelector argName1,
      final AttributeSelector argID1,
      final AttributeSelector amount,
      final AttributeSelector balance,
      final AttributeSelector reason,
      final AttributeSelector taxReceiverID,
      final AttributeSelector taxAmount,
      final AttributeSelector contextID,
      final AttributeSelector contextType,
      final AttributeSelector description) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM WalletJournal c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "division", division);
                                        AttributeSelector.addLongSelector(qs, "c", "refID", refID);
                                        AttributeSelector.addLongSelector(qs, "c", "date", date);
                                        AttributeSelector.addStringSelector(qs, "c", "refType", refType, p);
                                        AttributeSelector.addIntSelector(qs, "c", "firstPartyID", firstPartyID);
                                        AttributeSelector.addIntSelector(qs, "c", "secondPartyID", secondPartyID);
                                        AttributeSelector.addStringSelector(qs, "c", "argName1", argName1, p);
                                        AttributeSelector.addLongSelector(qs, "c", "argID1", argID1);
                                        AttributeSelector.addDoubleSelector(qs, "c", "amount", amount);
                                        AttributeSelector.addDoubleSelector(qs, "c", "balance", balance);
                                        AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
                                        AttributeSelector.addIntSelector(qs, "c", "taxReceiverID", taxReceiverID);
                                        AttributeSelector.addDoubleSelector(qs, "c", "taxAmount", taxAmount);
                                        AttributeSelector.addLongSelector(qs, "c", "contextID", contextID);
                                        AttributeSelector.addStringSelector(qs, "c", "contextType", contextType, p);
                                        AttributeSelector.addStringSelector(qs, "c", "description", description, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<WalletJournal> query = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createQuery(
                                                                                                       qs.toString(),
                                                                                                       WalletJournal.class);
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
