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
    name = "evekit_data_kill_item",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID",
            unique = false),
        @Index(
            name = "sequenceIndex",
            columnList = "sequence",
            unique = false),
        @Index(
            name = "containerSequenceIndex",
            columnList = "containerSequence",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "KillItem.getByKillIDAndSequence",
        query = "SELECT c FROM KillItem c where c.owner = :owner and c.killID = :killid and c.sequence = :seq and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "KillItem.getAllContained",
        query = "SELECT c FROM KillItem c where c.owner = :owner and c.killID = :killid and c.containerSequence = :cseq and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "KillItem.getAll",
        query = "SELECT c FROM KillItem c where c.owner = :owner and c.killID = :killid and c.sequence > :seq and c.lifeStart <= :point and c.lifeEnd > :point order by c.sequence asc"),
})
// 1 hour cache time - API caches for 30 minutes
public class KillItem extends CachedData {
  private static final Logger log                 = Logger.getLogger(KillItem.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  public static final int     TOP_LEVEL           = -1;
  private long                killID;
  private int                 typeID;
  private int                 flag;
  private int                 qtyDestroyed;
  private int                 qtyDropped;
  private boolean             singleton;
  // This field doesn't exist in the API download data. We introduce it to make it easier to deal with repeated elements in the kill item tree, which are
  // otherwise not unique.
  private int                 sequence;
  // This value gives the sequence number of the container for this kill item
  // and is -1 for top-level items
  private int                 containerSequence   = TOP_LEVEL;

  @SuppressWarnings("unused")
  protected KillItem() {}

  public KillItem(long killID, int typeID, int flag, int qtyDestroyed, int qtyDropped, boolean singleton, int sequence, int containerSequence) {
    this.killID = killID;
    this.typeID = typeID;
    this.flag = flag;
    this.qtyDestroyed = qtyDestroyed;
    this.qtyDropped = qtyDropped;
    this.singleton = singleton;
    this.sequence = sequence;
    this.containerSequence = containerSequence;
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
    if (!(sup instanceof KillItem)) return false;
    KillItem other = (KillItem) sup;
    return killID == other.killID && typeID == other.typeID && flag == other.flag && qtyDestroyed == other.qtyDestroyed && qtyDropped == other.qtyDropped
        && singleton == other.singleton && sequence == other.sequence && containerSequence == other.containerSequence;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getSequence() {
    return sequence;
  }

  public boolean isSingleton() {
    return singleton;
  }

  public long getKillID() {
    return killID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getFlag() {
    return flag;
  }

  public int getQtyDestroyed() {
    return qtyDestroyed;
  }

  public int getQtyDropped() {
    return qtyDropped;
  }

  public int getContainerSequence() {
    return containerSequence;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + containerSequence;
    result = prime * result + flag;
    result = prime * result + (int) (killID ^ (killID >>> 32));
    result = prime * result + qtyDestroyed;
    result = prime * result + qtyDropped;
    result = prime * result + sequence;
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
    KillItem other = (KillItem) obj;
    if (containerSequence != other.containerSequence) return false;
    if (flag != other.flag) return false;
    if (killID != other.killID) return false;
    if (qtyDestroyed != other.qtyDestroyed) return false;
    if (qtyDropped != other.qtyDropped) return false;
    if (sequence != other.sequence) return false;
    if (singleton != other.singleton) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  @Override
  public String toString() {
    return "KillItem [killID=" + killID + ", typeID=" + typeID + ", flag=" + flag + ", qtyDestroyed=" + qtyDestroyed + ", qtyDropped=" + qtyDropped
        + ", singleton=" + singleton + ", sequence=" + sequence + ", containerSequence=" + containerSequence + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve kill item with the given parameters live at the given time, or null if no such kill item exists.
   * 
   * @param owner
   *          kill item owner
   * @param time
   *          time at which the kill item must be live
   * @param killID
   *          kill ID for this kill item
   * @param sequence
   *          sequence number for this kill item
   * @return the kill item with the specified parameters live at the given time, or null.
   */
  public static KillItem get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final long killID,
                             final int sequence) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<KillItem>() {
        @Override
        public KillItem run() throws Exception {
          TypedQuery<KillItem> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("KillItem.getByKillIDAndSequence",
                                                                                                                   KillItem.class);
          getter.setParameter("owner", owner);
          getter.setParameter("killid", killID);
          getter.setParameter("seq", sequence);
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
   * Retrieve the list of kill items stored in the given container.
   * 
   * @param owner
   *          kill items owner
   * @param time
   *          time at which the kill items must be live
   * @param killID
   *          kill ID of kill these items are associated with
   * @param containerSequence
   *          sequence number of container holding the desired kill items
   * @return the list of kill items stored in the given container, live at the given time
   */
  public static List<KillItem> getContainedKillItems(
                                                     final SynchronizedEveAccount owner,
                                                     final long time,
                                                     final long killID,
                                                     final int containerSequence) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<KillItem>>() {
        @Override
        public List<KillItem> run() throws Exception {
          TypedQuery<KillItem> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("KillItem.getAllContained", KillItem.class);
          getter.setParameter("owner", owner);
          getter.setParameter("killid", killID);
          getter.setParameter("cseq", containerSequence);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  /**
   * Retrieve the list of kill items associated with the given kill ID and live at the given time.
   * 
   * @param owner
   *          kill items owner
   * @param time
   *          time at which the kill items must be live
   * @param killID
   *          kill ID to which these kill items are associated
   * @param maxresults
   *          maximum number of kill items to return
   * @param contid
   *          sequence number (exclusive) after which kill items will be returned
   * @return the list of kill items with the appropriate properties, live at the given time, with sequence number after "contid"
   */
  public static List<KillItem> getAllKillItems(
                                               final SynchronizedEveAccount owner,
                                               final long time,
                                               final long killID,
                                               int maxresults,
                                               final int contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(KillItem.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<KillItem>>() {
        @Override
        public List<KillItem> run() throws Exception {
          TypedQuery<KillItem> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("KillItem.getAll", KillItem.class);
          getter.setParameter("owner", owner);
          getter.setParameter("killid", killID);
          getter.setParameter("seq", contid);
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

  public static List<KillItem> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final boolean reverse,
                                           final AttributeSelector at,
                                           final AttributeSelector killID,
                                           final AttributeSelector typeID,
                                           final AttributeSelector flag,
                                           final AttributeSelector qtyDestroyed,
                                           final AttributeSelector qtyDropped,
                                           final AttributeSelector singleton,
                                           final AttributeSelector sequence,
                                           final AttributeSelector containerSequence) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<KillItem>>() {
        @Override
        public List<KillItem> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM KillItem c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "killID", killID);
          AttributeSelector.addLongSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addLongSelector(qs, "c", "flag", flag);
          AttributeSelector.addLongSelector(qs, "c", "qtyDestroyed", qtyDestroyed);
          AttributeSelector.addLongSelector(qs, "c", "qtyDropped", qtyDropped);
          AttributeSelector.addLongSelector(qs, "c", "singleton", singleton);
          AttributeSelector.addLongSelector(qs, "c", "sequence", sequence);
          AttributeSelector.addLongSelector(qs, "c", "containerSequence", containerSequence);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<KillItem> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), KillItem.class);
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
