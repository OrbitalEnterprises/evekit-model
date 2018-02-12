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
    name = "evekit_data_kill_item",
    indexes = {
        @Index(
            name = "killIDIndex",
            columnList = "killID"),
        @Index(
            name = "sequenceIndex",
            columnList = "sequence"),
        @Index(
            name = "containerSequenceIndex",
            columnList = "containerSequence"),
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
public class KillItem extends CachedData {
  private static final Logger log = Logger.getLogger(KillItem.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_KILL_LOG);
  public static final int TOP_LEVEL = -1;
  private int killID;
  private int typeID;
  private int flag;
  private long qtyDestroyed;
  private long qtyDropped;
  private int singleton;
  // This field doesn't exist in the API download data. We introduce it to make it easier to deal with repeated elements in the kill item tree, which are
  // otherwise not unique.
  private int sequence;
  // This value gives the sequence number of the container for this kill item
  // and is -1 for top-level items
  private int containerSequence = TOP_LEVEL;

  @SuppressWarnings("unused")
  protected KillItem() {}

  public KillItem(int killID, int typeID, int flag, long qtyDestroyed, long qtyDropped, int singleton, int sequence,
                  int containerSequence) {
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

  public int getSingleton() {
    return singleton;
  }

  public int getKillID() {
    return killID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getFlag() {
    return flag;
  }

  public long getQtyDestroyed() {
    return qtyDestroyed;
  }

  public long getQtyDropped() {
    return qtyDropped;
  }

  public int getContainerSequence() {
    return containerSequence;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    KillItem killItem = (KillItem) o;
    return killID == killItem.killID &&
        typeID == killItem.typeID &&
        flag == killItem.flag &&
        qtyDestroyed == killItem.qtyDestroyed &&
        qtyDropped == killItem.qtyDropped &&
        singleton == killItem.singleton &&
        sequence == killItem.sequence &&
        containerSequence == killItem.containerSequence;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), killID, typeID, flag, qtyDestroyed, qtyDropped, singleton, sequence,
                        containerSequence);
  }

  @Override
  public String toString() {
    return "KillItem{" +
        "killID=" + killID +
        ", typeID=" + typeID +
        ", flag=" + flag +
        ", qtyDestroyed=" + qtyDestroyed +
        ", qtyDropped=" + qtyDropped +
        ", singleton=" + singleton +
        ", sequence=" + sequence +
        ", containerSequence=" + containerSequence +
        '}';
  }

  /**
   * Retrieve kill item with the given parameters live at the given time, or null if no such kill item exists.
   *
   * @param owner    kill item owner
   * @param time     time at which the kill item must be live
   * @param killID   kill ID for this kill item
   * @param sequence sequence number for this kill item
   * @return the kill item with the specified parameters live at the given time, or null.
   */
  public static KillItem get(
      final SynchronizedEveAccount owner,
      final long time,
      final int killID,
      final int sequence) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<KillItem> getter = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery(
                                                                                                   "KillItem.getByKillIDAndSequence",
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
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
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
      final AttributeSelector containerSequence) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM KillItem c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "killID", killID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "flag", flag);
                                        AttributeSelector.addLongSelector(qs, "c", "qtyDestroyed", qtyDestroyed);
                                        AttributeSelector.addLongSelector(qs, "c", "qtyDropped", qtyDropped);
                                        AttributeSelector.addIntSelector(qs, "c", "singleton", singleton);
                                        AttributeSelector.addIntSelector(qs, "c", "sequence", sequence);
                                        AttributeSelector.addIntSelector(qs, "c", "containerSequence",
                                                                         containerSequence);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<KillItem> query = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(
                                                                                                  qs.toString(),
                                                                                                  KillItem.class);
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
