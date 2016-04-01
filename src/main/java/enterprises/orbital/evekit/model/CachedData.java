package enterprises.orbital.evekit.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Common abstract class for all model objects. Every synchronized object extends this class. Synchronized objects are immutable with one minor exception. The
 * only value that should ever be changed is the lifeEnd field when an object is evolved to a new version.
 */
@Entity
@Inheritance(
    strategy = InheritanceType.JOINED)
@Table(
    name = "evekit_cached_data",
    indexes = {
        @Index(
            name = "accountIndex",
            columnList = "aid",
            unique = false),
        @Index(
            name = "lifeStartIndex",
            columnList = "lifeStart",
            unique = false),
        @Index(
            name = "lifeEndIndex",
            columnList = "lifeEnd",
            unique = false)
})
@JsonIgnoreProperties({
    "owner", "accessMask", "metaData", "mask", "allMetaData"
})
@ApiModel(
    description = "Model data common properties")
public abstract class CachedData {
  private static final Logger      log             = Logger.getLogger(CachedData.class.getName());
  // Limit on number of meta-data tags allowed on a single cached item.
  // This is limited to avoid excessive caching of data by third party
  // sites
  public static final int          META_DATA_LIMIT = 10;
  // Unique cached data element ID
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "ek_seq")
  @SequenceGenerator(
      name = "ek_seq",
      initialValue = 100000,
      allocationSize = 10)
  @ApiModelProperty(
      value = "Unique ID")
  private long                     cid;
  // Account which owns this data
  @ManyToOne
  @JoinColumn(
      name = "aid",
      referencedColumnName = "aid")
  protected SynchronizedEveAccount owner;
  // Version and access mask. These are constants after creation.
  // Version 1 - pre Wayback Machine types
  // Version 2 - Introduction of wayback machine
  @ApiModelProperty(
      value = "EveKit release version")
  private short                    eveKitVersion   = 2;
  private byte[]                   accessMask;
  // About Object Lifelines:
  //
  // Every cached data object (with a few minor exceptions) has a time when it was created and a time when it was
  // deleted. If "start" is the time when an object was created, and "end" is the time when an object was deleted
  // then the interval [start, end) is the "life window" in which the object is "visible". Objects are never actually
  // deleted from storage. Instead, they are simply not visible outside their life window.
  //
  // An object is considered immutable within its life window. If it becomes necessary to change the state of an object,
  // then the object is copied so that the original's life window terminates at the time of the copy, and the life window
  // of the copy starts at the time of the copy. This rule is not strongly enforced. Instead, it is up to the subclass
  // to determine when it is appropriate to copy an object and make a new window.
  //
  // Object mutability is relaxed for meta-data. That is, meta-data may be changed for a given object at any time.
  // Meta-data is also copied when an object is evolved. This means that meta-data can never really be referenced
  // at a target time. Instead, the underlying object at the given time must be identified first, then the meta data
  // for that object can be inspected.
  //
  // Place in object lifeline:
  // [ key.lifeStart, lifeEnd]
  @ApiModelProperty(
      value = "Model lifeline start (milliseconds UTC)")
  protected long                   lifeStart;
  @ApiModelProperty(
      value = "Model lifeline end (milliseconds UTC), MAX_LONG for live data")
  protected long                   lifeEnd;
  // Object meta data - this will be serialized into storage
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Map<String, String>      metaData        = null;

  public static boolean nullSafeObjectCompare(
                                              Object a,
                                              Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Initialize this CachedData object.
   * 
   * @param owner
   *          the owner of this object
   * @param start
   *          start time of the life line of this object
   */
  public final void setup(
                          SynchronizedEveAccount owner,
                          long start) {
    this.owner = owner;
    this.lifeStart = start;
    this.accessMask = getMask();
    this.lifeEnd = Long.MAX_VALUE;
  }

  /**
   * Duplicate the cached data headers of this object onto the target object.
   * 
   * @param target
   *          the target object to be modified
   */
  public final void dup(
                        CachedData target) {
    target.owner = this.owner;
    target.eveKitVersion = eveKitVersion;
    target.metaData = null;
    if (metaData != null) {
      target.ensureMetaData();
      for (Entry<String, String> entry : getAllMetaData()) {
        target.metaData.put(entry.getKey(), entry.getValue());
      }
    }
    target.accessMask = this.accessMask.clone();
    target.lifeStart = this.lifeStart;
    target.lifeEnd = this.lifeEnd;
  }

  /**
   * End of life the current Entity (lifeEnd = time), and configure the other entity to be the next generation of the current entity (lifeStart = time, lifeEnd
   * = Long.MAX_LONG). Also initializes the CachedData fields of the other entity.
   * 
   * @param other
   *          object we're evolving to
   * @param time
   *          the time which marks the start of the evolution
   */
  public final void evolve(
                           CachedData other,
                           long time) {
    setLifeEnd(time);
    if (other != null) {
      dup(other);
      other.lifeStart = time;
      other.setLifeEnd(Long.MAX_VALUE);
    }
  }

  // Required subclass methods

  /**
   * Get the access mask for this cached data object.
   * 
   * @return data access mask.
   */
  public abstract byte[] getMask();

  /**
   * Determine whether this entity is equivalent to another entity. Entities only compare their non-CachedData fields for equivalence.
   * 
   * @param other
   *          the entity to compare against.
   * @return true if the entities are equivalent except possibly for their CachedData headers, false otherwise.
   */
  public abstract boolean equivalent(
                                     CachedData other);

  // Meta-data functions

  private void ensureMetaData() {
    if (metaData == null) metaData = new HashMap<String, String>();
  }

  public String getMetaData(
                            String key) {
    synchronized (this) {
      if (metaData == null) return null;
      return metaData.get(key);
    }
  }

  public Set<Entry<String, String>> getAllMetaData() {
    synchronized (this) {
      if (metaData == null) return Collections.emptySet();
      return metaData.entrySet();
    }
  }

  public void setMetaData(
                          String key,
                          String value) throws MetaDataLimitException, MetaDataCountException {
    if (key == null || key.length() == 0) throw new MetaDataLimitException("Key empty!");
    if (value == null) throw new MetaDataLimitException("Value null!");
    if (key.length() > 255) throw new MetaDataLimitException("Key too large!");
    if (value.length() > 255) throw new MetaDataLimitException("Value too large!");
    synchronized (this) {
      ensureMetaData();
      if (metaData.size() >= META_DATA_LIMIT && !metaData.containsKey(key)) throw new MetaDataCountException("CachedData target has reached MetaData limit!");
      metaData.put(key, value);
    }
  }

  public void deleteMetaData(
                             String key) {
    synchronized (this) {
      if (metaData != null) metaData.remove(key);
    }
  }

  public boolean hasMetaData() {
    synchronized (this) {
      return metaData != null && metaData.size() > 0;
    }
  }

  public boolean containsMetaData(
                                  String key) {
    synchronized (this) {
      return hasMetaData() && metaData.containsKey(key);
    }
  }

  public SynchronizedEveAccount getOwner() {
    return owner;
  }

  public long getCid() {
    return cid;
  }

  public short getEveKitVersion() {
    return eveKitVersion;
  }

  public void setEveKitVersion(
                               short eveKitVersion) {
    this.eveKitVersion = eveKitVersion;
  }

  public byte[] getAccessMask() {
    return accessMask;
  }

  public void setAccessMask(
                            byte[] access) {
    this.accessMask = access;
  }

  public void setLifeStart(
                           long lifeStart) {
    this.lifeStart = lifeStart;
  }

  public long getLifeStart() {
    return lifeStart;
  }

  public long getLifeEnd() {
    return lifeEnd;
  }

  public void setLifeEnd(
                         long lifeEnd) {
    this.lifeEnd = lifeEnd;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(accessMask);
    result = prime * result + (int) (cid ^ (cid >>> 32));
    result = prime * result + eveKitVersion;
    result = prime * result + (int) (lifeEnd ^ (lifeEnd >>> 32));
    result = prime * result + (int) (lifeStart ^ (lifeStart >>> 32));
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CachedData other = (CachedData) obj;
    if (!Arrays.equals(accessMask, other.accessMask)) return false;
    if (cid != other.cid) return false;
    if (eveKitVersion != other.eveKitVersion) return false;
    if (lifeEnd != other.lifeEnd) return false;
    if (lifeStart != other.lifeStart) return false;
    if (owner == null) {
      if (other.owner != null) return false;
    } else if (!owner.equals(other.owner)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CachedData [cid=" + cid + ", owner=" + owner + ", eveKitVersion=" + eveKitVersion + ", accessMask=" + Arrays.toString(accessMask) + ", lifeStart="
        + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CachedData get(
                               final long cid,
                               final String tableName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CachedData>() {
        @Override
        public CachedData run() throws Exception {
          TypedQuery<CachedData> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createQuery("SELECT c FROM " + tableName + " c WHERE c.cid = :cid", CachedData.class);
          getter.setParameter("cid", cid);
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

  public static CachedData get(
                               final long cid) {
    String type = ModelTypeMap.retrieveType(cid);
    if (type == null) return null;
    return CachedData.get(cid, type);
  }

  public static <A extends CachedData> A updateData(
                                                    final A data) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<A>() {
        @Override
        public A run() throws Exception {
          A result = EveKitUserAccountProvider.getFactory().getEntityManager().merge(data);
          // Ensure type map entry exists
          String typeName = data.getClass().getSimpleName();
          ModelTypeMap tn = new ModelTypeMap(result.getCid(), typeName);
          if (ModelTypeMap.update(tn) == null) return null;
          return result;
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static void cleanup(
                             final SynchronizedEveAccount toRemove,
                             final String tableName) {
    // Removes all CachedData which refers to this SynchronizedEveAccount. Very dangerous operation. Use with care. We don't use a bulk delete here because we
    // need cascading deletes on element collections and the only way to do this (easily) is to let the entity manager handle the removal.
    long removeCount = 0;
    try {
      long lastRemoved = 0;
      do {
        lastRemoved = EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Long>() {
          @Override
          public Long run() throws Exception {
            long removed = 0;
            TypedQuery<CachedData> query = EveKitUserAccountProvider.getFactory().getEntityManager()
                .createQuery("SELECT c FROM " + tableName + " c where c.owner = :owner", CachedData.class);
            query.setParameter("owner", toRemove);
            query.setMaxResults(1000);
            for (CachedData next : query.getResultList()) {
              EveKitUserAccountProvider.getFactory().getEntityManager().remove(next);
              ModelTypeMap.cleanup(next.getCid());
              removed++;
            }
            return removed;
          }
        });
        removeCount += lastRemoved;
      } while (lastRemoved > 0);
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    log.info("Removed " + removeCount + " entities from " + toRemove);
  }
}
