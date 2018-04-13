package enterprises.orbital.evekit.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
      allocationSize = 10,
      sequenceName = "cached_sequence")
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
  // Transient timestamp fields for better readability
  @Transient
  @ApiModelProperty(
      value = "lifeStart Date")
  @JsonProperty("lifeStartDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                     lifeStartDate;
  @Transient
  @ApiModelProperty(
      value = "lifeEnd Date")
  @JsonProperty("lifeEndDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                     lifeEndDate;
  // Object meta data - this will be serialized into storage
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Map<String, String>      metaData        = null;

  /**
   * Update transient values for readability.
   */
  public abstract void prepareTransient();

  protected Date assignDateField(
      long dateValue) {
    final long MAX_PARSEABLE_DATE = 253370782800000L;
    return dateValue < 0 ? null : new Date(Math.min(dateValue, MAX_PARSEABLE_DATE));
  }

  /**
   * Update transient date values. This method should normally be called by superclasses after updating their own date values.
   */
  protected void fixDates() {
    lifeStartDate = assignDateField(lifeStart);
    lifeEndDate = assignDateField(lifeEnd);
  }

  public static boolean nullSafeObjectCompare(
                                              Object a,
                                              Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Null safe collection compare.  Normally, the List collection types
   * implement a sensible equality check.  However, Hibernates PersistentBag class
   * relies on Object.equals which fails in most cases.  This function essentially
   * copies AbstractCollection.equals.
   *
   * @param a first list to compare
   * @param b second list to compare
   * @param <A> type of lists (must be identical)
   * @return true if the lists are identical, false otherwise
   */
  public static <A> boolean nullSafeListCompare(List<A> a, List<A> b) {
    if (a == b) return true;
    if (a == null || b == null) return false;
    if (a.size() != b.size()) return false;

    ListIterator<A> e1 = a.listIterator();
    ListIterator<A> e2 = b.listIterator();
    while (e1.hasNext() && e2.hasNext()) {
      A o1 = e1.next();
      A o2 = e2.next();
      if (!(o1==null ? o2==null : o1.equals(o2)))
        return false;
    }
    return true;
  }

  public static boolean floatCompare(float v1, float v2, float precision) {
    return Math.abs(v1 - v2) < precision;
  }

  public static boolean doubleCompare(double v1, double v2, double precision) {
    return Math.abs(v1 - v2) < precision;
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
                          String value)
    throws MetaDataLimitException, MetaDataCountException {
    if (key == null || key.length() == 0) throw new MetaDataLimitException("Key empty!");
    if (value == null) throw new MetaDataLimitException("Value null!");
    if (key.length() > 191) throw new MetaDataLimitException("Key too large!");
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

  @SuppressWarnings("Duplicates")
  protected static void setCIDOrdering(StringBuilder qs, long contid, boolean reverse) {
    if (reverse) {
      qs.append(" and c.cid < ")
        .append(contid);
      qs.append(" order by c.cid desc");
    } else {
      qs.append(" and c.cid > ")
        .append(contid);
      qs.append(" order by c.cid asc");
    }
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

  public static <A extends CachedData> A update(
      final A data) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                  .runTransaction(() -> {
                                    A result = EveKitUserAccountProvider.getFactory()
                                                                    .getEntityManager()
                                                                    .merge(data);
                                    // Ensure type map entry exists
                                    String typeName = data.getClass()
                                                          .getSimpleName();
                                    ModelTypeMap tn = new ModelTypeMap(result.getCid(), typeName);
                                    if (ModelTypeMap.update(tn) == null) return null;
                                    return result;
                                  });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
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

  //////////////////////////////////
  // Convenience functions for various types of batch processing
  //////////////////////////////////

  // Interface which forwards a call to the class specific query function to retrieve data
  public interface QueryCaller<A extends CachedData> {
    List<A> query(long contid, AttributeSelector at) throws IOException;
  }

  // Interface to receive any query exceptions generated from a stream constructed below.
  public interface StreamExceptionHandler {
    void handle(IOException e);
  }

  public static class SimpleStreamExceptionHandler implements StreamExceptionHandler {
    private List<IOException> caught = new ArrayList<>();

    public void handle(IOException e) {
      caught.add(e);
    }

    public boolean hit() {
      return !caught.isEmpty();
    }

    public List<IOException> get() {
      return caught;
    }

    public IOException getFirst() {
      return caught.get(0);
    }
  }

  /**
   * Return a stream over a collection of CachedData order by CachedData ID.
   * If a query error occurs during the stream, then the stream will be truncated at
   * the last CachedData item successfully returned by the stream.  Since the stream
   * interface does not provide an easy way to propagate such exceptions, this method
   * accepts an optional exception handler interface which will be invoked when a
   * query exception occurs.
   *
   * @param time the time at which objects in the stream should be live.
   * @param query a QueryCaller which will generate batches of elements as needed
   * @param ascending true if the stream should will be in ascending order, false otherwise.
   * @param exceptionHandler an optional interface which will be called if a query error occurs during the stream.
   * @param <A> subclass of CachedData which will be returned by the stream
   * @return a stream of CachedData objects.
   */
  public static <A extends CachedData> Stream<A> stream(long time, QueryCaller<A> query, boolean ascending, StreamExceptionHandler exceptionHandler) {
    return StreamSupport.stream(new Spliterator<A>() {
      final AttributeSelector ats = AttributeSelector.values(time);
      boolean done = false;
      long contid = ascending ? -1 : Long.MAX_VALUE;
      Deque<A> nextBatch = new ArrayDeque<>();

      private void attemptFill() {
        if (done || !nextBatch.isEmpty()) return;
        try {
          nextBatch.addAll(query.query(contid, ats));
          if (nextBatch.isEmpty()) {
            done = true;
          } else {
            contid = nextBatch.getLast()
                              .getCid();
          }
        } catch (IOException e) {
          log.log(Level.FINE, "Query error, truncating stream at last element", e);
          nextBatch.clear();
          done = true;
          if (exceptionHandler != null) exceptionHandler.handle(e);
        }
      }

      private boolean hasNext() {
        attemptFill();
        return !done;
      }

      @Override
      public boolean tryAdvance(Consumer<? super A> action) {
        if (hasNext()) {
          action.accept(nextBatch.remove());
          return true;
        }
        return false;
      }

      @Override
      public void forEachRemaining(Consumer<? super A> action) {
        while (hasNext())
          action.accept(nextBatch.remove());
      }

      @Override
      public Spliterator<A> trySplit() {
        return null;
      }

      @Override
      public long estimateSize() {
        return Long.MAX_VALUE;
      }

      @Override
      public long getExactSizeIfKnown() {
        return -1;
      }

      @Override
      public int characteristics() {
        return Spliterator.SORTED | Spliterator.IMMUTABLE | Spliterator.NONNULL;
      }

      @Override
      public boolean hasCharacteristics(int characteristics) {
        return ((Spliterator.SORTED | Spliterator.IMMUTABLE | Spliterator.NONNULL) & characteristics) > 0;
      }

      @Override
      public Comparator<? super A> getComparator() {
        return ascending ? Comparator.comparingLong(CachedData::getCid) : Comparator.comparingLong(CachedData::getCid).reversed();
      }
    }, false);
  }


  /**
   * Retrieve all data items of the specified type live at the specified time.
   * This function continues to accumulate results until a query returns no results.
   *
   * @param time  the "live" time for the retrieval.
   * @param query an interface which performs the type appropriate query call.
   * @param <A>   class of the object which will be returned.
   * @return the list of results.
   * @throws IOException on any DB error.
   */
  @SuppressWarnings("Duplicates")
  public static <A extends CachedData> List<A> retrieveAll(long time, QueryCaller<A> query) throws IOException {
    SimpleStreamExceptionHandler capture = new SimpleStreamExceptionHandler();
    List<A> collected = stream(time, query, true, capture).collect(Collectors.toList());
    if (capture.hit()) throw capture.getFirst();
    return collected;
  }

}
