package enterprises.orbital.evekit.model.character;

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

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_jump_clone",
    indexes = {
        @Index(
            name = "jumpCloneIDIndex",
            columnList = "jumpCloneID",
            unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "JumpClone.getByCloneID",
        query = "SELECT c FROM JumpClone c where c.owner = :owner and c.jumpCloneID = :clone and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "JumpClone.getAll",
        query = "SELECT c FROM JumpClone c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class JumpClone extends CachedData {
  private static final Logger log  = Logger.getLogger(JumpClone.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  private int                 jumpCloneID;
  private int                 typeID;
  private long                locationID;
  private String              cloneName;

  @SuppressWarnings("unused")
  private JumpClone() {}

  public JumpClone(int jumpCloneID, int typeID, long locationID, String cloneName) {
    super();
    this.jumpCloneID = jumpCloneID;
    this.typeID = typeID;
    this.locationID = locationID;
    this.cloneName = cloneName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof JumpClone)) return false;
    JumpClone other = (JumpClone) sup;
    return jumpCloneID == other.jumpCloneID && typeID == other.typeID && locationID == other.locationID && nullSafeObjectCompare(cloneName, other.cloneName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getJumpCloneID() {
    return jumpCloneID;
  }

  public long getLocationID() {
    return locationID;
  }

  public String getCloneName() {
    return cloneName;
  }

  @Override
  public String toString() {
    return "JumpClone [jumpCloneID=" + jumpCloneID + ", typeID=" + typeID + ", locationID=" + locationID + ", cloneName=" + cloneName + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((cloneName == null) ? 0 : cloneName.hashCode());
    result = prime * result + jumpCloneID;
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    JumpClone other = (JumpClone) obj;
    if (cloneName == null) {
      if (other.cloneName != null) return false;
    } else if (!cloneName.equals(other.cloneName)) return false;
    if (jumpCloneID != other.jumpCloneID) return false;
    if (locationID != other.locationID) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  public static JumpClone get(
                              final SynchronizedEveAccount owner,
                              final long time,
                              final int jumpCloneID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<JumpClone>() {
        @Override
        public JumpClone run() throws Exception {
          TypedQuery<JumpClone> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("JumpClone.getByCloneID", JumpClone.class);
          getter.setParameter("owner", owner);
          getter.setParameter("clone", jumpCloneID);
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

  public static List<JumpClone> getAll(
                                       final SynchronizedEveAccount owner,
                                       final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<JumpClone>>() {
        @Override
        public List<JumpClone> run() throws Exception {
          TypedQuery<JumpClone> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("JumpClone.getAll", JumpClone.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<JumpClone> accessQuery(
                                            final SynchronizedEveAccount owner,
                                            final long contid,
                                            final int maxresults,
                                            final AttributeSelector at,
                                            final AttributeSelector jumpCloneID,
                                            final AttributeSelector typeID,
                                            final AttributeSelector locationID,
                                            final AttributeSelector cloneName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<JumpClone>>() {
        @Override
        public List<JumpClone> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM JumpClone c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "jumpCloneID", jumpCloneID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
          AttributeSelector.addStringSelector(qs, "c", "cloneName", cloneName, p);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<JumpClone> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), JumpClone.class);
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
