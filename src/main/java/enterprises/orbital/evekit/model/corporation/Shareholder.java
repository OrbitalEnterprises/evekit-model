package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_shareholder",
    indexes = {
        @Index(
            name = "shareholderIDIndex",
            columnList = "shareholderID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Shareholder.getByShareholderID",
        query = "SELECT c FROM Shareholder c where c.owner = :owner and c.shareholderID = :holder and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Shareholder extends CachedData {
  private static final Logger log = Logger.getLogger(Shareholder.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_SHAREHOLDERS);

  private int shareholderID;
  private String shareholderType;
  private long shares;

  @SuppressWarnings("unused")
  protected Shareholder() {}

  public Shareholder(int shareholderID, String shareholderType, long shares) {
    this.shareholderID = shareholderID;
    this.shareholderType = shareholderType;
    this.shares = shares;
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
    if (!(sup instanceof Shareholder)) return false;
    Shareholder other = (Shareholder) sup;
    return shareholderID == other.shareholderID
        && nullSafeObjectCompare(shareholderType, other.shareholderType)
        && shares == other.shares;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getShareholderID() {
    return shareholderID;
  }

  public String getShareholderType() {
    return shareholderType;
  }

  public long getShares() {
    return shares;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Shareholder that = (Shareholder) o;
    return shareholderID == that.shareholderID &&
        shares == that.shares &&
        Objects.equals(shareholderType, that.shareholderType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), shareholderID, shareholderType, shares);
  }

  @Override
  public String toString() {
    return "Shareholder{" +
        "shareholderID=" + shareholderID +
        ", shareholderType='" + shareholderType + '\'' +
        ", shares=" + shares +
        '}';
  }

  public static Shareholder get(
      final SynchronizedEveAccount owner,
      final long time,
      final int shareholderID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Shareholder> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "Shareholder.getByShareholderID",
                                                                                                      Shareholder.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("holder", shareholderID);
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

  public static List<Shareholder> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector shareholderID,
      final AttributeSelector shareholderType,
      final AttributeSelector shares) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Shareholder c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "shareholderID", shareholderID);
                                        AttributeSelector.addStringSelector(qs, "c", "shareholderType", shareholderType,
                                                                            p);
                                        AttributeSelector.addLongSelector(qs, "c", "shares", shares);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Shareholder> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     Shareholder.class);
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
