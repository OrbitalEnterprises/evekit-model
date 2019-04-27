package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
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
    name = "evekit_data_jump_clone",
    indexes = {
        @Index(
            name = "jumpCloneIDIndex",
            columnList = "jumpCloneID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "JumpClone.getByCloneID",
        query = "SELECT c FROM JumpClone c where c.owner = :owner and c.jumpCloneID = :clone and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "JumpClone.getAll",
        query = "SELECT c FROM JumpClone c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
public class JumpClone extends CachedData {
  private static final Logger log  = Logger.getLogger(JumpClone.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int                 jumpCloneID;
  private long                locationID;
  private String              cloneName;
  private String locationType;

  @SuppressWarnings("unused")
  protected JumpClone() {}

  public JumpClone(int jumpCloneID, long locationID, String cloneName, String locationType) {
    this.jumpCloneID = jumpCloneID;
    this.locationID = locationID;
    this.cloneName = cloneName;
    this.locationType = locationType;
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
    if (!(sup instanceof JumpClone)) return false;
    JumpClone other = (JumpClone) sup;
    return jumpCloneID == other.jumpCloneID &&
        locationID == other.locationID &&
        nullSafeObjectCompare(cloneName, other.cloneName) &&
        nullSafeObjectCompare(locationType, other.locationType);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(jumpCloneID, locationID, cloneName, locationType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
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

  public String getLocationType() {
    return locationType;
  }

  @Override
  public String toString() {
    return "JumpClone{" +
        "jumpCloneID=" + jumpCloneID +
        ", locationID=" + locationID +
        ", cloneName='" + cloneName + '\'' +
        ", locationType='" + locationType + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JumpClone jumpClone = (JumpClone) o;
    return jumpCloneID == jumpClone.jumpCloneID &&
        locationID == jumpClone.locationID &&
        Objects.equals(cloneName, jumpClone.cloneName) &&
        Objects.equals(locationType, jumpClone.locationType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), jumpCloneID, locationID, cloneName, locationType);
  }

  public static JumpClone get(
                              final SynchronizedEveAccount owner,
                              final long time,
                              final int jumpCloneID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(() -> {
        TypedQuery<JumpClone> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("JumpClone.getByCloneID", JumpClone.class);
        getter.setParameter("owner", owner);
        getter.setParameter("clone", jumpCloneID);
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

  public static List<JumpClone> accessQuery(
                                            final SynchronizedEveAccount owner,
                                            final long contid,
                                            final int maxresults,
                                            final boolean reverse,
                                            final AttributeSelector at,
                                            final AttributeSelector jumpCloneID,
                                            final AttributeSelector locationID,
                                            final AttributeSelector cloneName,
                                            final AttributeSelector locationType) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(() -> {
        StringBuilder qs = new StringBuilder();
        qs.append("SELECT c FROM JumpClone c WHERE ");
        // Constrain to specified owner
        qs.append("c.owner = :owner");
        // Constrain lifeline
        AttributeSelector.addLifelineSelector(qs, "c", at);
        // Constrain attributes
        AttributeParameters p = new AttributeParameters("att");
        AttributeSelector.addIntSelector(qs, "c", "jumpCloneID", jumpCloneID);
        AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
        AttributeSelector.addStringSelector(qs, "c", "cloneName", cloneName, p);
        AttributeSelector.addStringSelector(qs, "c", "locationType", locationType, p);
        // Set CID constraint and ordering
        setCIDOrdering(qs, contid, reverse);
        // Return result
        TypedQuery<JumpClone> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), JumpClone.class);
        query.setParameter("owner", owner);
        p.fillParams(query);
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
