package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_contract_item",
    indexes = {
        @Index(
            name = "contractIDIndex",
            columnList = "contractID"),
        @Index(
            name = "recordIDIndex",
            columnList = "recordID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "ContractItem.getByContractAndRecordID",
        query = "SELECT c FROM ContractItem c where c.owner = :owner and c.contractID = :contract and c.recordID = :record and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ContractItem.getAllByContractID",
        query = "SELECT c FROM ContractItem c where c.owner = :owner and c.contractID = :contract and c.recordID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.recordID asc"),
})
public class ContractItem extends CachedData {
  private static final Logger log = Logger.getLogger(ContractItem.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS);
  private int contractID;
  private long recordID;
  private int typeID;
  private int quantity;
  private int rawQuantity;
  private boolean singleton;
  private boolean included;

  @SuppressWarnings("unused")
  protected ContractItem() {}

  public ContractItem(int contractID, long recordID, int typeID, int quantity, int rawQuantity, boolean singleton,
                      boolean included) {
    super();
    this.contractID = contractID;
    this.recordID = recordID;
    this.typeID = typeID;
    this.quantity = quantity;
    this.rawQuantity = rawQuantity;
    this.singleton = singleton;
    this.included = included;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof ContractItem)) return false;
    ContractItem other = (ContractItem) sup;
    return contractID == other.contractID && recordID == other.recordID && typeID == other.typeID && quantity == other.quantity
        && rawQuantity == other.rawQuantity && singleton == other.singleton && included == other.included;
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

  public long getRecordID() {
    return recordID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getQuantity() {
    return quantity;
  }

  public int getRawQuantity() {
    return rawQuantity;
  }

  public boolean isSingleton() {
    return singleton;
  }

  public boolean isIncluded() {
    return included;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ContractItem that = (ContractItem) o;
    return contractID == that.contractID &&
        recordID == that.recordID &&
        typeID == that.typeID &&
        quantity == that.quantity &&
        rawQuantity == that.rawQuantity &&
        singleton == that.singleton &&
        included == that.included;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), contractID, recordID, typeID, quantity, rawQuantity, singleton, included);
  }

  @Override
  public String toString() {
    return "ContractItem{" +
        "contractID=" + contractID +
        ", recordID=" + recordID +
        ", typeID=" + typeID +
        ", quantity=" + quantity +
        ", rawQuantity=" + rawQuantity +
        ", singleton=" + singleton +
        ", included=" + included +
        '}';
  }

  /**
   * Retrieve contract item with the given parameters live at the given time, or null if no such contract item exists.
   *
   * @param owner      contract item owner
   * @param time       time at which contract item must be live
   * @param contractID contract ID of contract item
   * @param recordID   record ID of contract item
   * @return the contract item with the specified parameters live at the give time, or null
   */
  public static ContractItem get(
      final SynchronizedEveAccount owner,
      final long time,
      final int contractID,
      final long recordID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ContractItem> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "ContractItem.getByContractAndRecordID",
                                                                                                       ContractItem.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("contract", contractID);
                                        getter.setParameter("record", recordID);
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

  public static List<ContractItem> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector contractID,
      final AttributeSelector recordID,
      final AttributeSelector typeID,
      final AttributeSelector quantity,
      final AttributeSelector rawQuantity,
      final AttributeSelector singleton,
      final AttributeSelector included) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ContractItem c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "contractID", contractID);
                                        AttributeSelector.addLongSelector(qs, "c", "recordID", recordID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
                                        AttributeSelector.addIntSelector(qs, "c", "rawQuantity", rawQuantity);
                                        AttributeSelector.addBooleanSelector(qs, "c", "singleton", singleton);
                                        AttributeSelector.addBooleanSelector(qs, "c", "included", included);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ContractItem> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      ContractItem.class);
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
