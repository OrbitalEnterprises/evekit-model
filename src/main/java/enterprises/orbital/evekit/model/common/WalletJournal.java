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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_wallet_journal",
    indexes = {
        @Index(
            name = "accountKeyIndex",
            columnList = "accountKey",
            unique = false),
        @Index(
            name = "refIDIndex",
            columnList = "refID",
            unique = false),
        @Index(
            name = "dateIndex",
            columnList = "date",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "WalletJournal.getByRefIDAndAccountKey",
        query = "SELECT c FROM WalletJournal c where c.owner = :owner and c.accountKey = :accountkey and c.refID = :refid and c.lifeStart <= :point and c.lifeEnd > :point"),
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
// 1 hour cache time - API caches for 30 minutes
public class WalletJournal extends CachedData {
  private static final Logger log                 = Logger.getLogger(WalletJournal.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_JOURNAL);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private int                 accountKey;
  private long                refID;
  private long                date                = -1;
  private int                 refTypeID;
  private String              ownerName1;
  private long                ownerID1;
  private String              ownerName2;
  private long                ownerID2;
  private String              argName1;
  private long                argID1;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          amount;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          balance;
  private String              reason;
  private long                taxReceiverID;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal          taxAmount;
  private int                 owner1TypeID;
  private int                 owner2TypeID;
  @Transient
  @ApiModelProperty(
      value = "date Date")
  @JsonProperty("dateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                dateDate;

  @SuppressWarnings("unused")
  protected WalletJournal() {}

  public WalletJournal(int accountKey, long refID, long date, int refTypeID, String ownerName1, long ownerID1, String ownerName2, long ownerID2,
                       String argName1, long argID1, BigDecimal amount, BigDecimal balance, String reason, long taxReceiverID, BigDecimal taxAmount,
                       int owner1TypeID, int owner2TypeID) {
    this.accountKey = accountKey;
    this.refID = refID;
    this.date = date;
    this.refTypeID = refTypeID;
    this.ownerName1 = ownerName1;
    this.ownerID1 = ownerID1;
    this.ownerName2 = ownerName2;
    this.ownerID2 = ownerID2;
    this.argName1 = argName1;
    this.argID1 = argID1;
    this.amount = amount;
    this.balance = balance;
    this.reason = reason;
    this.taxReceiverID = taxReceiverID;
    this.taxAmount = taxAmount;
    this.owner1TypeID = owner1TypeID;
    this.owner2TypeID = owner2TypeID;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    dateDate = assignDateField(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof WalletJournal)) return false;
    WalletJournal other = (WalletJournal) sup;

    return accountKey == other.accountKey && refID == other.refID && date == other.date && refTypeID == other.refTypeID
        && nullSafeObjectCompare(ownerName1, other.ownerName1) && ownerID1 == other.ownerID1 && nullSafeObjectCompare(ownerName2, other.ownerName2)
        && ownerID2 == other.ownerID2 && nullSafeObjectCompare(argName1, other.argName1) && argID1 == other.argID1
        && nullSafeObjectCompare(amount, other.amount) && nullSafeObjectCompare(balance, other.balance) && nullSafeObjectCompare(reason, other.reason)
        && taxReceiverID == other.taxReceiverID && nullSafeObjectCompare(taxAmount, other.taxAmount) && owner1TypeID == other.owner1TypeID
        && owner2TypeID == other.owner2TypeID;
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

  public long getRefID() {
    return refID;
  }

  public long getDate() {
    return date;
  }

  public int getRefTypeID() {
    return refTypeID;
  }

  public String getOwnerName1() {
    return ownerName1;
  }

  public long getOwnerID1() {
    return ownerID1;
  }

  public String getOwnerName2() {
    return ownerName2;
  }

  public long getOwnerID2() {
    return ownerID2;
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

  public long getTaxReceiverID() {
    return taxReceiverID;
  }

  public BigDecimal getTaxAmount() {
    return taxAmount;
  }

  public int getOwner1TypeID() {
    return owner1TypeID;
  }

  public int getOwner2TypeID() {
    return owner2TypeID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + accountKey;
    result = prime * result + ((amount == null) ? 0 : amount.hashCode());
    result = prime * result + (int) (argID1 ^ (argID1 >>> 32));
    result = prime * result + ((argName1 == null) ? 0 : argName1.hashCode());
    result = prime * result + ((balance == null) ? 0 : balance.hashCode());
    result = prime * result + (int) (date ^ (date >>> 32));
    result = prime * result + owner1TypeID;
    result = prime * result + owner2TypeID;
    result = prime * result + (int) (ownerID1 ^ (ownerID1 >>> 32));
    result = prime * result + (int) (ownerID2 ^ (ownerID2 >>> 32));
    result = prime * result + ((ownerName1 == null) ? 0 : ownerName1.hashCode());
    result = prime * result + ((ownerName2 == null) ? 0 : ownerName2.hashCode());
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + (int) (refID ^ (refID >>> 32));
    result = prime * result + refTypeID;
    result = prime * result + ((taxAmount == null) ? 0 : taxAmount.hashCode());
    result = prime * result + (int) (taxReceiverID ^ (taxReceiverID >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    WalletJournal other = (WalletJournal) obj;
    if (accountKey != other.accountKey) return false;
    if (amount == null) {
      if (other.amount != null) return false;
    } else if (!amount.equals(other.amount)) return false;
    if (argID1 != other.argID1) return false;
    if (argName1 == null) {
      if (other.argName1 != null) return false;
    } else if (!argName1.equals(other.argName1)) return false;
    if (balance == null) {
      if (other.balance != null) return false;
    } else if (!balance.equals(other.balance)) return false;
    if (date != other.date) return false;
    if (owner1TypeID != other.owner1TypeID) return false;
    if (owner2TypeID != other.owner2TypeID) return false;
    if (ownerID1 != other.ownerID1) return false;
    if (ownerID2 != other.ownerID2) return false;
    if (ownerName1 == null) {
      if (other.ownerName1 != null) return false;
    } else if (!ownerName1.equals(other.ownerName1)) return false;
    if (ownerName2 == null) {
      if (other.ownerName2 != null) return false;
    } else if (!ownerName2.equals(other.ownerName2)) return false;
    if (reason == null) {
      if (other.reason != null) return false;
    } else if (!reason.equals(other.reason)) return false;
    if (refID != other.refID) return false;
    if (refTypeID != other.refTypeID) return false;
    if (taxAmount == null) {
      if (other.taxAmount != null) return false;
    } else if (!taxAmount.equals(other.taxAmount)) return false;
    if (taxReceiverID != other.taxReceiverID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "WalletJournal [accountKey=" + accountKey + ", refID=" + refID + ", date=" + date + ", refTypeID=" + refTypeID + ", ownerName1=" + ownerName1
        + ", ownerID1=" + ownerID1 + ", ownerName2=" + ownerName2 + ", ownerID2=" + ownerID2 + ", argName1=" + argName1 + ", argID1=" + argID1 + ", amount="
        + amount + ", balance=" + balance + ", reason=" + reason + ", taxReceiverID=" + taxReceiverID + ", taxAmount=" + taxAmount + ", owner1TypeID="
        + owner1TypeID + ", owner2TypeID=" + owner2TypeID + "]";
  }

  /**
   * Retrieve a wallet journal entry by key, live at the given time.
   * 
   * @param owner
   *          journal entry owner
   * @param time
   *          time at which journal entry must be live
   * @param accountKey
   *          key of account in which journal entry is recorded
   * @param refID
   *          journal entry refID
   * @return an existing journal entry, or null if a live entry with the given attributes can not be found
   */
  public static WalletJournal get(
                                  final SynchronizedEveAccount owner,
                                  final long time,
                                  final int accountKey,
                                  final long refID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<WalletJournal>() {
        @Override
        public WalletJournal run() throws Exception {
          TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("WalletJournal.getByRefIDAndAccountKey",
                                                                                                                        WalletJournal.class);
          getter.setParameter("owner", owner);
          getter.setParameter("accountkey", accountKey);
          getter.setParameter("refid", refID);
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
   * Retrieve wallet journal entries live at the given time with date greater than "contid".
   * 
   * @param owner
   *          journal entries owner
   * @param time
   *          time at which journal entries must be live
   * @param maxresults
   *          max journal entries to return
   * @param contid
   *          minimum date (exclusive) for returned journal entries
   * @return a list of journal entries live at the given time, no longer than maxresults, with date greater than contid, ordered increasing by date
   */
  public static List<WalletJournal> getAllForward(
                                                  final SynchronizedEveAccount owner,
                                                  final long time,
                                                  int maxresults,
                                                  final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletJournal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletJournal>>() {
        @Override
        public List<WalletJournal> run() throws Exception {
          TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("WalletJournal.getAllForward",
                                                                                                                        WalletJournal.class);
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
   * Retrieve wallet journal entries live at the given time with date less than "contid"
   * 
   * @param owner
   *          journal entries owner
   * @param time
   *          time at which journal entries must be live
   * @param maxresults
   *          max journal entries to return
   * @param contid
   *          maximum date (exclusive) for returned journal entries
   * @return a list of journal entries live at the given time, no longer than maxresults, with date less than contid, ordered decreasing by date
   */
  public static List<WalletJournal> getAllBackward(
                                                   final SynchronizedEveAccount owner,
                                                   final long time,
                                                   int maxresults,
                                                   final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletJournal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletJournal>>() {
        @Override
        public List<WalletJournal> run() throws Exception {
          TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("WalletJournal.getAllBackward",
                                                                                                                        WalletJournal.class);
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
   * Retrieve wallet journal entries live at the given time, with dates in the provided range.
   * 
   * @param owner
   *          journal entries owner
   * @param time
   *          time at which journal entries must be live
   * @param maxresults
   *          max journal entries to return
   * @param mindate
   *          lower bound (inclusive) for date range
   * @param maxdate
   *          upper bound (inclusive) for date range
   * @param ascending
   *          if true, return results in increasing order by date, otherwise results are ordered decreasing by date
   * @return a list of journal entries live at the given time, no longer than maxresults, with date in the provided range, ordered either increasing or
   *         decreasing depending on "ascending"
   */
  public static List<WalletJournal> getRange(
                                             final SynchronizedEveAccount owner,
                                             final long time,
                                             int maxresults,
                                             final long mindate,
                                             final long maxdate,
                                             final boolean ascending) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletJournal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletJournal>>() {
        @Override
        public List<WalletJournal> run() throws Exception {
          TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery(ascending ? "WalletJournal.getRangeAsc" : "WalletJournal.getRangeDesc", WalletJournal.class);
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

  public static List<WalletJournal> accessQuery(
                                                final SynchronizedEveAccount owner,
                                                final long contid,
                                                final int maxresults,
                                                final boolean reverse,
                                                final AttributeSelector at,
                                                final AttributeSelector accountKey,
                                                final AttributeSelector refID,
                                                final AttributeSelector date,
                                                final AttributeSelector refTypeID,
                                                final AttributeSelector ownerName1,
                                                final AttributeSelector ownerID1,
                                                final AttributeSelector ownerName2,
                                                final AttributeSelector ownerID2,
                                                final AttributeSelector argName1,
                                                final AttributeSelector argID1,
                                                final AttributeSelector amount,
                                                final AttributeSelector balance,
                                                final AttributeSelector reason,
                                                final AttributeSelector taxReceiverID,
                                                final AttributeSelector taxAmount,
                                                final AttributeSelector owner1TypeID,
                                                final AttributeSelector owner2TypeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<WalletJournal>>() {
        @Override
        public List<WalletJournal> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM WalletJournal c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "accountKey", accountKey);
          AttributeSelector.addLongSelector(qs, "c", "refID", refID);
          AttributeSelector.addLongSelector(qs, "c", "date", date);
          AttributeSelector.addIntSelector(qs, "c", "refTypeID", refTypeID);
          AttributeSelector.addStringSelector(qs, "c", "ownerName1", ownerName1, p);
          AttributeSelector.addLongSelector(qs, "c", "ownerID1", ownerID1);
          AttributeSelector.addStringSelector(qs, "c", "ownerName2", ownerName2, p);
          AttributeSelector.addLongSelector(qs, "c", "ownerID2", ownerID2);
          AttributeSelector.addStringSelector(qs, "c", "argName1", argName1, p);
          AttributeSelector.addLongSelector(qs, "c", "argID1", argID1);
          AttributeSelector.addDoubleSelector(qs, "c", "amount", amount);
          AttributeSelector.addDoubleSelector(qs, "c", "balance", balance);
          AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
          AttributeSelector.addLongSelector(qs, "c", "taxReceiverID", taxReceiverID);
          AttributeSelector.addDoubleSelector(qs, "c", "taxAmount", taxAmount);
          AttributeSelector.addIntSelector(qs, "c", "owner1TypeID", owner1TypeID);
          AttributeSelector.addIntSelector(qs, "c", "owner2TypeID", owner2TypeID);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<WalletJournal> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), WalletJournal.class);
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
