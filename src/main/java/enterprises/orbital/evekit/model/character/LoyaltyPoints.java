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
    name = "evekit_data_character_loyalty_points",
    indexes = {
        @Index(
            name = "corporationIDIndex",
            columnList = "corporationID"),
    }
)
@NamedQueries({
    @NamedQuery(
        name = "LoyaltyPoints.get",
        query = "SELECT c FROM LoyaltyPoints c where c.owner = :owner and c.corporationID = :cid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class LoyaltyPoints extends CachedData {
  private static final Logger log = Logger.getLogger(LoyaltyPoints.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int corporationID;
  private int loyaltyPoints;

  @SuppressWarnings("unused")
  protected LoyaltyPoints() {}

  public LoyaltyPoints(int corporationID, int loyaltyPoints) {
    this.corporationID = corporationID;
    this.loyaltyPoints = loyaltyPoints;
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
    if (!(sup instanceof LoyaltyPoints)) return false;
    LoyaltyPoints other = (LoyaltyPoints) sup;
    return corporationID == other.corporationID &&
        loyaltyPoints == other.loyaltyPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getCorporationID() {
    return corporationID;
  }

  public int getLoyaltyPoints() {
    return loyaltyPoints;
  }

  @Override
  public String toString() {
    return "LoyaltyPoints{" +
        "corporationID=" + corporationID +
        ", loyaltyPoints=" + loyaltyPoints +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoyaltyPoints that = (LoyaltyPoints) o;
    return corporationID == that.corporationID &&
        loyaltyPoints == that.loyaltyPoints;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), corporationID, loyaltyPoints);
  }

  public static LoyaltyPoints get(
      final SynchronizedEveAccount owner,
      final long time,
      final int corporationID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<LoyaltyPoints> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(
                                                                                                        "LoyaltyPoints.get",
                                                                                                        LoyaltyPoints.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("cid", corporationID);
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

  public static List<LoyaltyPoints> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector corporationID,
      final AttributeSelector loyaltyPoints) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM LoyaltyPoints c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                        AttributeSelector.addIntSelector(qs, "c", "loyaltyPoints", loyaltyPoints);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<LoyaltyPoints> query = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createQuery(
                                                                                                       qs.toString(),
                                                                                                       LoyaltyPoints.class);
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
