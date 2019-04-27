package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_jump_clone_implant",
    indexes = {
        @Index(
            name = "jumpCloneIDIndex",
            columnList = "jumpCloneID"),
        @Index(
            name = "typeIDIndex",
            columnList = "typeID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "JumpCloneImplant.getByCloneAndTypeID",
        query = "SELECT c FROM JumpCloneImplant c where c.owner = :owner and c.jumpCloneID = :clone and c.typeID = :type and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "JumpCloneImplant.getAll",
        query = "SELECT c FROM JumpCloneImplant c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
public class JumpCloneImplant extends CachedData {
  private static final Logger log  = Logger.getLogger(JumpCloneImplant.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int                 jumpCloneID;
  private int                 typeID;

  @SuppressWarnings("unused")
  protected JumpCloneImplant() {}

  public JumpCloneImplant(int jumpCloneID, int typeID) {
    super();
    this.jumpCloneID = jumpCloneID;
    this.typeID = typeID;
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
    if (!(sup instanceof JumpCloneImplant)) return false;
    JumpCloneImplant other = (JumpCloneImplant) sup;
    return jumpCloneID == other.jumpCloneID && typeID == other.typeID;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(jumpCloneID, typeID);
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

  @Override
  public String toString() {
    return "JumpCloneImplant{" +
        "jumpCloneID=" + jumpCloneID +
        ", typeID=" + typeID +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JumpCloneImplant that = (JumpCloneImplant) o;
    return jumpCloneID == that.jumpCloneID &&
        typeID == that.typeID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), jumpCloneID, typeID);
  }

  public static JumpCloneImplant get(
                                     final SynchronizedEveAccount owner,
                                     final long time,
                                     final int jumpCloneID,
                                     final int typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(() -> {
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
      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<JumpCloneImplant> accessQuery(
                                                   final SynchronizedEveAccount owner,
                                                   final long contid,
                                                   final int maxresults,
                                                   final boolean reverse,
                                                   final AttributeSelector at,
                                                   final AttributeSelector jumpCloneID,
                                                   final AttributeSelector typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(() -> {
        StringBuilder qs = new StringBuilder();
        qs.append("SELECT c FROM JumpCloneImplant c WHERE ");
        // Constrain to specified owner
        qs.append("c.owner = :owner");
        // Constrain lifeline
        AttributeSelector.addLifelineSelector(qs, "c", at);
        // Constrain attributes
        AttributeSelector.addIntSelector(qs, "c", "jumpCloneID", jumpCloneID);
        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
        // Set CID constraint and ordering
        setCIDOrdering(qs, contid, reverse);
        // Return result
        TypedQuery<JumpCloneImplant> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), JumpCloneImplant.class);
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
