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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_standing",
    indexes = {
        @Index(
            name = "standingEntityIndex",
            columnList = "standingEntity",
            unique = false),
        @Index(
            name = "fromIDIndex",
            columnList = "fromID",
            unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "Standing.getByStandingEntityAndFromID",
        query = "SELECT c FROM Standing c where c.owner = :owner and c.standingEntity = :entity and c.fromID = :fromid and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Standing.getAll",
        query = "SELECT c FROM Standing c where c.owner = :owner and c.cid > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "Standing.getAllByEntity",
        query = "SELECT c FROM Standing c where c.owner = :owner and c.cid > :contid and c.standingEntity = :entity and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 4 hour cache time - API caches for 3 hours
public class Standing extends CachedData {
  private static final Logger log                 = Logger.getLogger(Standing.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STANDINGS);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private String              standingEntity;
  private int                 fromID;
  private String              fromName;
  private double              standing;

  @SuppressWarnings("unused")
  private Standing() {}

  public Standing(String standingEntity, int fromID, String fromName, double standing) {
    super();
    this.standingEntity = standingEntity;
    this.fromID = fromID;
    this.fromName = fromName;
    this.standing = standing;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Standing)) return false;
    Standing other = (Standing) sup;
    return nullSafeObjectCompare(standingEntity, other.standingEntity) && fromID == other.fromID && nullSafeObjectCompare(fromName, other.fromName)
        && standing == other.standing;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getStandingEntity() {
    return standingEntity;
  }

  public int getFromID() {
    return fromID;
  }

  public String getFromName() {
    return fromName;
  }

  public double getStanding() {
    return standing;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + fromID;
    result = prime * result + ((fromName == null) ? 0 : fromName.hashCode());
    long temp;
    temp = Double.doubleToLongBits(standing);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((standingEntity == null) ? 0 : standingEntity.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Standing other = (Standing) obj;
    if (fromID != other.fromID) return false;
    if (fromName == null) {
      if (other.fromName != null) return false;
    } else if (!fromName.equals(other.fromName)) return false;
    if (Double.doubleToLongBits(standing) != Double.doubleToLongBits(other.standing)) return false;
    if (standingEntity == null) {
      if (other.standingEntity != null) return false;
    } else if (!standingEntity.equals(other.standingEntity)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Standing [standingEntity=" + standingEntity + ", fromID=" + fromID + ", fromName=" + fromName + ", standing=" + standing + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  /**
   * Retrieve standing with the given parameters live at the given time, or null.
   * 
   * @param owner
   *          standing owner
   * @param time
   *          time at which the standing must be live
   * @param standingEntity
   *          standing entity with which the standing is associated
   * @param fromID
   *          from ID with which the standing is associated
   * @return standing with the given parameters, live at the given time, or null
   */
  public static Standing get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final String standingEntity,
                             final int fromID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Standing>() {
        @Override
        public Standing run() throws Exception {
          TypedQuery<Standing> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Standing.getByStandingEntityAndFromID",
                                                                                                                   Standing.class);
          getter.setParameter("owner", owner);
          getter.setParameter("entity", standingEntity);
          getter.setParameter("fromid", fromID);
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
   * Retrieve list of standings live at the given time with sortKey (lexicographically) greater than "contid"
   * 
   * @param owner
   *          standings owner
   * @param time
   *          time at which standings must be live
   * @param maxresults
   *          maximum number of standings to return
   * @param contid
   *          sortKey (exclusive) after which results will be returned
   * @return list of standings live at the given time with sortKey (lexicographically) greater than "contid"
   */
  public static List<Standing> getAllStandings(
                                               final SynchronizedEveAccount owner,
                                               final long time,
                                               int maxresults,
                                               final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Standing.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Standing>>() {
        @Override
        public List<Standing> run() throws Exception {
          TypedQuery<Standing> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Standing.getAll", Standing.class);
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
   * Retrieve list of standings live at the given time, associated with the given standing entity, and with sortKey (lexicographically) greater than "contid"
   * 
   * @param owner
   *          standings owner
   * @param time
   *          time at which standings must be live
   * @param standingEntity
   *          standing entity with which standings must be associated
   * @param maxresults
   *          maximum number of standings to return
   * @param contid
   *          sortKey (exclusive) after which results will be returned
   * @return list of standings live at the given time, associated with the given standings entity, and with sortKey (lexicographicallY) greater than "contid"
   */
  public static List<Standing> getByEntity(
                                           final SynchronizedEveAccount owner,
                                           final long time,
                                           final String standingEntity,
                                           int maxresults,
                                           final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults,
                                                         (int) PersistentProperty.getLongPropertyWithFallback(
                                                                                                              OrbitalProperties.getPropertyName(Standing.class,
                                                                                                                                                "maxresults"),
                                                                                                              DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Standing>>() {
        @Override
        public List<Standing> run() throws Exception {
          TypedQuery<Standing> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Standing.getAllByEntity", Standing.class);
          getter.setParameter("owner", owner);
          getter.setParameter("entity", standingEntity);
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

  public static List<Standing> accessQuery(
                                           final SynchronizedEveAccount owner,
                                           final long contid,
                                           final int maxresults,
                                           final AttributeSelector at,
                                           final AttributeSelector standingEntity,
                                           final AttributeSelector fromID,
                                           final AttributeSelector fromName,
                                           final AttributeSelector standing) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Standing>>() {
        @Override
        public List<Standing> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Standing c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addStringSelector(qs, "c", "standingEntity", standingEntity, p);
          AttributeSelector.addIntSelector(qs, "c", "fromID", fromID);
          AttributeSelector.addStringSelector(qs, "c", "fromName", fromName, p);
          AttributeSelector.addDoubleSelector(qs, "c", "standing", standing);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<Standing> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Standing.class);
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
