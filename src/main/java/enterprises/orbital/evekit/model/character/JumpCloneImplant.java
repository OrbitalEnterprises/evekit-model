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
    name = "evekit_data_jump_clone_implant",
    indexes = {
        @Index(
            name = "jumpCloneIDIndex",
            columnList = "jumpCloneID",
            unique = false),
        @Index(
            name = "typeIDIndex",
            columnList = "typeID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "JumpCloneImplant.getByCloneAndTypeID",
        query = "SELECT c FROM JumpCloneImplant c where c.owner = :owner and c.jumpCloneID = :clone and c.typeID = :type and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "JumpCloneImplant.getAll",
        query = "SELECT c FROM JumpCloneImplant c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class JumpCloneImplant extends CachedData {
  private static final Logger log  = Logger.getLogger(JumpCloneImplant.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  private int                 jumpCloneID;
  private int                 typeID;
  private String              typeName;

  @SuppressWarnings("unused")
  private JumpCloneImplant() {}

  public JumpCloneImplant(int jumpCloneID, int typeID, String typeName) {
    super();
    this.jumpCloneID = jumpCloneID;
    this.typeID = typeID;
    this.typeName = typeName;
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
    if (!(sup instanceof JumpCloneImplant)) return false;
    JumpCloneImplant other = (JumpCloneImplant) sup;
    return jumpCloneID == other.jumpCloneID && typeID == other.typeID && nullSafeObjectCompare(typeName, other.typeName);
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

  public String getTypeName() {
    return typeName;
  }

  @Override
  public String toString() {
    return "JumpCloneImplant [jumpCloneID=" + jumpCloneID + ", typeID=" + typeID + ", typeName=" + typeName + ", owner=" + owner + ", lifeStart=" + lifeStart
        + ", lifeEnd=" + lifeEnd + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + jumpCloneID;
    result = prime * result + typeID;
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    JumpCloneImplant other = (JumpCloneImplant) obj;
    if (jumpCloneID != other.jumpCloneID) return false;
    if (typeID != other.typeID) return false;
    if (typeName == null) {
      if (other.typeName != null) return false;
    } else if (!typeName.equals(other.typeName)) return false;
    return true;
  }

  public static JumpCloneImplant get(
                                     final SynchronizedEveAccount owner,
                                     final long time,
                                     final int jumpCloneID,
                                     final int typeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<JumpCloneImplant>() {
        @Override
        public JumpCloneImplant run() throws Exception {
          TypedQuery<JumpCloneImplant> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createNamedQuery("JumpCloneImplant.getByCloneAndTypeID", JumpCloneImplant.class);
          getter.setParameter("owner", owner);
          getter.setParameter("clone", jumpCloneID);
          getter.setParameter("type", typeID);
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

  public static List<JumpCloneImplant> getAll(
                                              final SynchronizedEveAccount owner,
                                              final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<JumpCloneImplant>>() {
        @Override
        public List<JumpCloneImplant> run() throws Exception {
          TypedQuery<JumpCloneImplant> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("JumpCloneImplant.getAll",
                                                                                                                           JumpCloneImplant.class);
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

  public static List<JumpCloneImplant> accessQuery(
                                                   final SynchronizedEveAccount owner,
                                                   final long contid,
                                                   final int maxresults,
                                                   final boolean reverse,
                                                   final AttributeSelector at,
                                                   final AttributeSelector jumpCloneID,
                                                   final AttributeSelector typeID,
                                                   final AttributeSelector typeName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<JumpCloneImplant>>() {
        @Override
        public List<JumpCloneImplant> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM JumpCloneImplant c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "jumpCloneID", jumpCloneID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addStringSelector(qs, "c", "typeName", typeName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<JumpCloneImplant> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), JumpCloneImplant.class);
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
