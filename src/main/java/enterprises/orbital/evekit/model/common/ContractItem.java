package enterprises.orbital.evekit.model.common;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_contract_item",
    indexes = {
        @Index(
            name = "contractIDIndex",
            columnList = "contractID",
            unique = false),
        @Index(
            name = "recordIDIndex",
            columnList = "recordID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "ContractItem.getByContractAndRecordID",
        query = "SELECT c FROM ContractItem c where c.owner = :owner and c.contractID = :contract and c.recordID = :record and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "ContractItem.getAllByContractID",
        query = "SELECT c FROM ContractItem c where c.owner = :owner and c.contractID = :contract and c.recordID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.recordID asc"),
})
// 1 hour cache time - API caches for 15 minutes
public class ContractItem extends CachedData {
  private static final Logger log                 = Logger.getLogger(ContractItem.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTRACTS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private long                contractID;
  private long                recordID;
  private int                 typeID;
  private long                quantity;
  private long                rawQuantity;
  private boolean             singleton;
  private boolean             included;

  @SuppressWarnings("unused")
  protected ContractItem() {}

  public ContractItem(long contractID, long recordID, int typeID, long quantity, long rawQuantity, boolean singleton, boolean included) {
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
  public void prepareDates() {
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

  public long getContractID() {
    return contractID;
  }

  public long getRecordID() {
    return recordID;
  }

  public int getTypeID() {
    return typeID;
  }

  public long getQuantity() {
    return quantity;
  }

  public long getRawQuantity() {
    return rawQuantity;
  }

  public boolean isSingleton() {
    return singleton;
  }

  public boolean isIncluded() {
    return included;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (contractID ^ (contractID >>> 32));
    result = prime * result + (included ? 1231 : 1237);
    result = prime * result + (int) (quantity ^ (quantity >>> 32));
    result = prime * result + (int) (rawQuantity ^ (rawQuantity >>> 32));
    result = prime * result + (int) (recordID ^ (recordID >>> 32));
    result = prime * result + (singleton ? 1231 : 1237);
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ContractItem other = (ContractItem) obj;
    if (contractID != other.contractID) return false;
    if (included != other.included) return false;
    if (quantity != other.quantity) return false;
    if (rawQuantity != other.rawQuantity) return false;
    if (recordID != other.recordID) return false;
    if (singleton != other.singleton) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ContractItem [contractID=" + contractID + ", recordID=" + recordID + ", typeID=" + typeID + ", quantity=" + quantity + ", rawQuantity="
        + rawQuantity + ", singleton=" + singleton + ", included=" + included + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve contract item with the given parameters live at the given time, or null if no such contract item exists.
   * 
   * @param owner
   *          contract item owner
   * @param time
   *          time at which contract item must be live
   * @param contractID
   *          contract ID of contract item
   * @param recordID
   *          record ID of contract item
   * @return the contract item with the specified parameters live at the give time, or null
   */
  public static ContractItem get(
                                 final SynchronizedEveAccount owner,
                                 final long time,
                                 final long contractID,
                                 final long recordID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ContractItem>() {
        @Override
        public ContractItem run() throws Exception {
          TypedQuery<ContractItem> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContractItem.getByContractAndRecordID",
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
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  /**
   * Retrieve a list of contract items for the given contract ID live at the given time.
   * 
   * @param owner
   *          contract items owner
   * @param time
   *          time at which the contract items must be live
   * @param contractID
   *          contract ID of contract items to retrieve
   * @param maxresults
   *          maximum number of contract items to retrieve
   * @param contid
   *          sortKey (exclusive) from which to start returning contract items
   * @return the list of contract items live at the given time which are (lexicographically) greater than the given contid
   */
  public static List<ContractItem> getAllContractItems(
                                                       final SynchronizedEveAccount owner,
                                                       final long time,
                                                       final long contractID,
                                                       int maxresults,
                                                       final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(ContractItem.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContractItem>>() {
        @Override
        public List<ContractItem> run() throws Exception {
          TypedQuery<ContractItem> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("ContractItem.getAllByContractID",
                                                                                                                       ContractItem.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contract", contractID);
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
                                               final AttributeSelector included) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<ContractItem>>() {
        @Override
        public List<ContractItem> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ContractItem c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "contractID", contractID);
          AttributeSelector.addLongSelector(qs, "c", "recordID", recordID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addLongSelector(qs, "c", "quantity", quantity);
          AttributeSelector.addLongSelector(qs, "c", "rawQuantity", rawQuantity);
          AttributeSelector.addBooleanSelector(qs, "c", "singleton", singleton);
          AttributeSelector.addBooleanSelector(qs, "c", "included", included);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<ContractItem> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), ContractItem.class);
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
