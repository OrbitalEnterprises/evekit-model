package enterprises.orbital.evekit.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NoResultException;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Common abstract class for all reference model objects (this is data available from the XML API which does not require a key and access code). Every
 * synchronized reference object extends this class. Synchronized objects are immutable except for "life end" which is updated when an object no longer
 * represents the latest value of the source.
 */
@Entity
@Inheritance(
    strategy = InheritanceType.JOINED)
@Table(
    name = "evekit_ref_cached_data",
    indexes = {
        @Index(
            name = "lifeStartIndex",
            columnList = "lifeStart",
            unique = false),
        @Index(
            name = "lifeEndIndex",
            columnList = "lifeEnd",
            unique = false)
    })
@ApiModel(
    description = "Reference model data common properties")
public abstract class RefCachedData {
  private static final Logger log           = Logger.getLogger(RefCachedData.class.getName());
  // Unique cached data element ID
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "ek_ref_seq")
  @SequenceGenerator(
      name = "ek_ref_seq",
      initialValue = 100000,
      allocationSize = 10)
  @ApiModelProperty(
      value = "Unique ID")
  private long                cid;
  // Version and access mask. These are constants after creation.
  // Version 1 - pre Wayback Machine types
  // Version 2 - Introduction of wayback machine
  @ApiModelProperty(
      value = "EveKit release version")
  private short               eveKitVersion = 2;
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
  // Place in object lifeline:
  // [ key.lifeStart, lifeEnd]
  @ApiModelProperty(
      value = "Model lifeline start (milliseconds UTC)")
  protected long              lifeStart;
  @ApiModelProperty(
      value = "Model lifeline end (milliseconds UTC), MAX_LONG for live data")
  protected long              lifeEnd;

  public static boolean nullSafeObjectCompare(
                                              Object a,
                                              Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Initialize this CachedData object.
   * 
   * @param start
   *          start time of the life line of this object
   */
  public final void setup(
                          long start) {
    this.lifeStart = start;
    this.lifeEnd = Long.MAX_VALUE;
  }

  /**
   * Duplicate the cached data headers of this object onto the target object.
   * 
   * @param target
   *          the target object to be modified
   */
  public final void dup(
                        RefCachedData target) {
    target.eveKitVersion = eveKitVersion;
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
                           RefCachedData other,
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
   * Determine whether this entity is equivalent to another entity. Entities only compare their non-CachedData fields for equivalence.
   * 
   * @param other
   *          the entity to compare against.
   * @return true if the entities are equivalent except possibly for their CachedData headers, false otherwise.
   */
  public abstract boolean equivalent(
                                     RefCachedData other);

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
    result = prime * result + (int) (cid ^ (cid >>> 32));
    result = prime * result + eveKitVersion;
    result = prime * result + (int) (lifeEnd ^ (lifeEnd >>> 32));
    result = prime * result + (int) (lifeStart ^ (lifeStart >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RefCachedData other = (RefCachedData) obj;
    if (cid != other.cid) return false;
    if (eveKitVersion != other.eveKitVersion) return false;
    if (lifeEnd != other.lifeEnd) return false;
    if (lifeStart != other.lifeStart) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PublicCachedData [cid=" + cid + ", eveKitVersion=" + eveKitVersion + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static RefCachedData get(
                                  final long cid,
                                  final String tableName) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RefCachedData>() {
        @Override
        public RefCachedData run() throws Exception {
          TypedQuery<RefCachedData> getter = EveKitRefDataProvider.getFactory().getEntityManager()
              .createQuery("SELECT c FROM " + tableName + " c WHERE c.cid = :cid", RefCachedData.class);
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

  public static RefCachedData get(
                                  final long cid) {
    String type = RefModelTypeMap.retrieveType(cid);
    if (type == null) return null;
    return RefCachedData.get(cid, type);
  }

  public static <A extends RefCachedData> A updateData(
                                                       final A data) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<A>() {
        @Override
        public A run() throws Exception {
          A result = EveKitRefDataProvider.getFactory().getEntityManager().merge(data);
          // Ensure type map entry exists
          String typeName = data.getClass().getSimpleName();
          RefModelTypeMap tn = new RefModelTypeMap(result.getCid(), typeName);
          if (RefModelTypeMap.update(tn) == null) return null;
          return result;
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

}
