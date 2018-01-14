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
// 1 hour cache time - API caches for 30 minutes
public class WalletJournal extends CachedData {
  private static final Logger log = Logger.getLogger(WalletJournal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_JOURNAL);
  private static final int DEFAULT_MAX_RESULTS = 1000;
  private int division;
  private long refID;
  private long date = -1;
  private String refType;
  private int firstPartyID;
  private String firstPartyType;
  private int secondPartyID;
  private String secondPartyType;
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
  private long locationID;
  private long transactionID;
  private String npcName;
  private int npcID;
  private int destroyedShipTypeID;
  private int characterID;
  private int corporationID;
  private int allianceID;
  private int jobID;
  private int contractID;
  private int systemID;
  private int planetID;

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
  @ApiModelProperty(value = "*DEPRECATED* ownerID1")
  @JsonProperty("ownerID1")
  private long ownerID1;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* ownerID2")
  @JsonProperty("ownerID2")
  private long ownerID2;

  @SuppressWarnings("unused")
  protected WalletJournal() {}

  public WalletJournal(int division, long refID, long date, String refType, int firstPartyID,
                       String firstPartyType, int secondPartyID, String secondPartyType, String argName1, long argID1,
                       BigDecimal amount, BigDecimal balance, String reason, int taxReceiverID,
                       BigDecimal taxAmount, long locationID, long transactionID, String npcName, int npcID,
                       int destroyedShipTypeID, int characterID, int corporationID, int allianceID, int jobID,
                       int contractID, int systemID, int planetID) {
    this.division = division;
    this.refID = refID;
    this.date = date;
    this.refType = refType;
    this.firstPartyID = firstPartyID;
    this.firstPartyType = firstPartyType;
    this.secondPartyID = secondPartyID;
    this.secondPartyType = secondPartyType;
    this.argName1 = argName1;
    this.argID1 = argID1;
    this.amount = amount;
    this.balance = balance;
    this.reason = reason;
    this.taxReceiverID = taxReceiverID;
    this.taxAmount = taxAmount;
    this.locationID = locationID;
    this.transactionID = transactionID;
    this.npcName = npcName;
    this.npcID = npcID;
    this.destroyedShipTypeID = destroyedShipTypeID;
    this.characterID = characterID;
    this.corporationID = corporationID;
    this.allianceID = allianceID;
    this.jobID = jobID;
    this.contractID = contractID;
    this.systemID = systemID;
    this.planetID = planetID;
  }

  /**
   * Update transient date values for readability.
   */
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
        nullSafeObjectCompare(firstPartyType, other.firstPartyType) &&
        secondPartyID == other.secondPartyID &&
        nullSafeObjectCompare(secondPartyType, other.secondPartyType) &&
        nullSafeObjectCompare(argName1, other.argName1) &&
        argID1 == other.argID1 &&
        nullSafeObjectCompare(amount, other.amount) &&
        nullSafeObjectCompare(balance, other.balance) &&
        nullSafeObjectCompare(reason, other.reason) &&
        taxReceiverID == other.taxReceiverID &&
        nullSafeObjectCompare(taxAmount, other.taxAmount) &&
        locationID == other.locationID &&
        transactionID == other.transactionID &&
        nullSafeObjectCompare(npcName, other.npcName) &&
        npcID == other.npcID &&
        destroyedShipTypeID == other.destroyedShipTypeID &&
        characterID == other.characterID &&
        corporationID == other.corporationID &&
        allianceID == other.allianceID &&
        jobID == other.jobID &&
        contractID == other.contractID &&
        systemID == other.systemID &&
        planetID == other.planetID;
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

  public String getFirstPartyType() {
    return firstPartyType;
  }

  public int getSecondPartyID() {
    return secondPartyID;
  }

  public String getSecondPartyType() {
    return secondPartyType;
  }

  public long getLocationID() {
    return locationID;
  }

  public long getTransactionID() {
    return transactionID;
  }

  public String getNpcName() {
    return npcName;
  }

  public int getNpcID() {
    return npcID;
  }

  public int getDestroyedShipTypeID() {
    return destroyedShipTypeID;
  }

  public int getCharacterID() {
    return characterID;
  }

  public int getCorporationID() {
    return corporationID;
  }

  public int getAllianceID() {
    return allianceID;
  }

  public int getJobID() {
    return jobID;
  }

  public int getContractID() {
    return contractID;
  }

  public int getSystemID() {
    return systemID;
  }

  public int getPlanetID() {
    return planetID;
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
        locationID == that.locationID &&
        transactionID == that.transactionID &&
        npcID == that.npcID &&
        destroyedShipTypeID == that.destroyedShipTypeID &&
        characterID == that.characterID &&
        corporationID == that.corporationID &&
        allianceID == that.allianceID &&
        jobID == that.jobID &&
        contractID == that.contractID &&
        systemID == that.systemID &&
        planetID == that.planetID &&
        Objects.equals(refType, that.refType) &&
        Objects.equals(firstPartyType, that.firstPartyType) &&
        Objects.equals(secondPartyType, that.secondPartyType) &&
        Objects.equals(argName1, that.argName1) &&
        Objects.equals(amount, that.amount) &&
        Objects.equals(balance, that.balance) &&
        Objects.equals(reason, that.reason) &&
        Objects.equals(taxAmount, that.taxAmount) &&
        Objects.equals(npcName, that.npcName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID, secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount, locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID, corporationID, allianceID, jobID, contractID, systemID, planetID);
  }

  @Override
  public String toString() {
    return "WalletJournal{" +
        "division=" + division +
        ", refID=" + refID +
        ", date=" + date +
        ", refType='" + refType + '\'' +
        ", firstPartyID=" + firstPartyID +
        ", firstPartyType='" + firstPartyType + '\'' +
        ", secondPartyID=" + secondPartyID +
        ", secondPartyType='" + secondPartyType + '\'' +
        ", argName1='" + argName1 + '\'' +
        ", argID1=" + argID1 +
        ", amount=" + amount +
        ", balance=" + balance +
        ", reason='" + reason + '\'' +
        ", taxReceiverID=" + taxReceiverID +
        ", taxAmount=" + taxAmount +
        ", locationID=" + locationID +
        ", transactionID=" + transactionID +
        ", npcName='" + npcName + '\'' +
        ", npcID=" + npcID +
        ", destroyedShipTypeID=" + destroyedShipTypeID +
        ", characterID=" + characterID +
        ", corporationID=" + corporationID +
        ", allianceID=" + allianceID +
        ", jobID=" + jobID +
        ", contractID=" + contractID +
        ", systemID=" + systemID +
        ", planetID=" + planetID +
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
                                                                                                    .createNamedQuery("WalletJournal.getByRefIDAndDivision",
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

  /**
   * Retrieve wallet journal entries live at the given time with date greater than "contid".
   *
   * @param owner      journal entries owner
   * @param time       time at which journal entries must be live
   * @param maxresults max journal entries to return
   * @param contid     minimum date (exclusive) for returned journal entries
   * @return a list of journal entries live at the given time, no longer than maxresults, with date greater than contid, ordered increasing by date
   */
  public static List<WalletJournal> getAllForward(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletJournal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery("WalletJournal.getAllForward",
                                                                                                                      WalletJournal.class);
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
   * Retrieve wallet journal entries live at the given time with date less than "contid"
   *
   * @param owner      journal entries owner
   * @param time       time at which journal entries must be live
   * @param maxresults max journal entries to return
   * @param contid     maximum date (exclusive) for returned journal entries
   * @return a list of journal entries live at the given time, no longer than maxresults, with date less than contid, ordered decreasing by date
   */
  public static List<WalletJournal> getAllBackward(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long contid) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletJournal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery("WalletJournal.getAllBackward",
                                                                                                                      WalletJournal.class);
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
   * Retrieve wallet journal entries live at the given time, with dates in the provided range.
   *
   * @param owner      journal entries owner
   * @param time       time at which journal entries must be live
   * @param maxresults max journal entries to return
   * @param mindate    lower bound (inclusive) for date range
   * @param maxdate    upper bound (inclusive) for date range
   * @param ascending  if true, return results in increasing order by date, otherwise results are ordered decreasing by date
   * @return a list of journal entries live at the given time, no longer than maxresults, with date in the provided range, ordered either increasing or
   * decreasing depending on "ascending"
   */
  public static List<WalletJournal> getRange(
      final SynchronizedEveAccount owner,
      final long time,
      int maxresults,
      final long mindate,
      final long maxdate,
      final boolean ascending) throws IOException {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(WalletJournal.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<WalletJournal> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(ascending ? "WalletJournal.getRangeAsc" : "WalletJournal.getRangeDesc", WalletJournal.class);
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
      final AttributeSelector firstPartyType,
      final AttributeSelector secondPartyID,
      final AttributeSelector secondPartyType,
      final AttributeSelector argName1,
      final AttributeSelector argID1,
      final AttributeSelector amount,
      final AttributeSelector balance,
      final AttributeSelector reason,
      final AttributeSelector taxReceiverID,
      final AttributeSelector taxAmount,
      final AttributeSelector locationID,
      final AttributeSelector transactionID,
      final AttributeSelector npcName,
      final AttributeSelector npcID,
      final AttributeSelector destroyedShipTypeID,
      final AttributeSelector characterID,
      final AttributeSelector corporationID,
      final AttributeSelector allianceID,
      final AttributeSelector jobID,
      final AttributeSelector contractID,
      final AttributeSelector systemID,
      final AttributeSelector planetID) throws IOException {
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
                                        AttributeSelector.addStringSelector(qs, "c", "firstPartyType", firstPartyType, p);
                                        AttributeSelector.addIntSelector(qs, "c", "secondPartyID", secondPartyID);
                                        AttributeSelector.addStringSelector(qs, "c", "secondPartyType", secondPartyType, p);
                                        AttributeSelector.addStringSelector(qs, "c", "argName1", argName1, p);
                                        AttributeSelector.addLongSelector(qs, "c", "argID1", argID1);
                                        AttributeSelector.addDoubleSelector(qs, "c", "amount", amount);
                                        AttributeSelector.addDoubleSelector(qs, "c", "balance", balance);
                                        AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
                                        AttributeSelector.addIntSelector(qs, "c", "taxReceiverID", taxReceiverID);
                                        AttributeSelector.addDoubleSelector(qs, "c", "taxAmount", taxAmount);
                                        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addLongSelector(qs, "c", "transactionID", transactionID);
                                        AttributeSelector.addStringSelector(qs, "c", "npcName", npcName, p);
                                        AttributeSelector.addIntSelector(qs, "c", "npcID", npcID);
                                        AttributeSelector.addIntSelector(qs, "c", "destroyedShipTypeID", destroyedShipTypeID);
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                        AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                        AttributeSelector.addIntSelector(qs, "c", "jobID", jobID);
                                        AttributeSelector.addIntSelector(qs, "c", "contractID", contractID);
                                        AttributeSelector.addIntSelector(qs, "c", "systemID", systemID);
                                        AttributeSelector.addIntSelector(qs, "c", "planetID", planetID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<WalletJournal> query = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createQuery(qs.toString(), WalletJournal.class);
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
