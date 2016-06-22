package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_shareholder",
    indexes = {
        @Index(
            name = "shareholderIDIndex",
            columnList = "shareholderID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "Shareholder.getByShareholderID",
        query = "SELECT c FROM Shareholder c where c.owner = :owner and c.shareholderID = :holder and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Shareholder.getAll",
        query = "SELECT c FROM Shareholder c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class Shareholder extends CachedData {
  private static final Logger log  = Logger.getLogger(Shareholder.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_SHAREHOLDERS);
  private long                shareholderID;
  private boolean             isCorporation;
  private long                shareholderCorporationID;
  private String              shareholderCorporationName;
  private String              shareholderName;
  private int                 shares;

  @SuppressWarnings("unused")
  private Shareholder() {}

  public Shareholder(long shareholderID, boolean isCorporation, long shareholderCorporationID, String shareholderCorporationName, String shareholderName,
                     int shares) {
    super();
    this.shareholderID = shareholderID;
    this.isCorporation = isCorporation;
    this.shareholderCorporationID = shareholderCorporationID;
    this.shareholderCorporationName = shareholderCorporationName;
    this.shareholderName = shareholderName;
    this.shares = shares;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof Shareholder)) return false;
    Shareholder other = (Shareholder) sup;
    return shareholderID == other.shareholderID && isCorporation == other.isCorporation && shareholderCorporationID == other.shareholderCorporationID
        && nullSafeObjectCompare(shareholderCorporationName, other.shareholderCorporationName) && nullSafeObjectCompare(shareholderName, other.shareholderName)
        && shares == other.shares;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getShareholderID() {
    return shareholderID;
  }

  public boolean isCorporation() {
    return isCorporation;
  }

  public long getShareholderCorporationID() {
    return shareholderCorporationID;
  }

  public String getShareholderCorporationName() {
    return shareholderCorporationName;
  }

  public String getShareholderName() {
    return shareholderName;
  }

  public int getShares() {
    return shares;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (isCorporation ? 1231 : 1237);
    result = prime * result + (int) (shareholderCorporationID ^ (shareholderCorporationID >>> 32));
    result = prime * result + ((shareholderCorporationName == null) ? 0 : shareholderCorporationName.hashCode());
    result = prime * result + (int) (shareholderID ^ (shareholderID >>> 32));
    result = prime * result + ((shareholderName == null) ? 0 : shareholderName.hashCode());
    result = prime * result + shares;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Shareholder other = (Shareholder) obj;
    if (isCorporation != other.isCorporation) return false;
    if (shareholderCorporationID != other.shareholderCorporationID) return false;
    if (shareholderCorporationName == null) {
      if (other.shareholderCorporationName != null) return false;
    } else if (!shareholderCorporationName.equals(other.shareholderCorporationName)) return false;
    if (shareholderID != other.shareholderID) return false;
    if (shareholderName == null) {
      if (other.shareholderName != null) return false;
    } else if (!shareholderName.equals(other.shareholderName)) return false;
    if (shares != other.shares) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Shareholder [shareholderID=" + shareholderID + ", isCorporation=" + isCorporation + ", shareholderCorporationID=" + shareholderCorporationID
        + ", shareholderCorporationName=" + shareholderCorporationName + ", shareholderName=" + shareholderName + ", shares=" + shares + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static Shareholder get(
                                final SynchronizedEveAccount owner,
                                final long time,
                                final long shareholderID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Shareholder>() {
        @Override
        public Shareholder run() throws Exception {
          TypedQuery<Shareholder> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Shareholder.getByShareholderID",
                                                                                                                      Shareholder.class);
          getter.setParameter("owner", owner);
          getter.setParameter("holder", shareholderID);
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

  public static List<Shareholder> getAll(
                                         final SynchronizedEveAccount owner,
                                         final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Shareholder>>() {
        @Override
        public List<Shareholder> run() throws Exception {
          TypedQuery<Shareholder> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Shareholder.getAll", Shareholder.class);
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

  public static List<Shareholder> accessQuery(
                                              final SynchronizedEveAccount owner,
                                              final long contid,
                                              final int maxresults,
                                              final boolean reverse,
                                              final AttributeSelector at,
                                              final AttributeSelector shareholderID,
                                              final AttributeSelector isCorporation,
                                              final AttributeSelector shareholderCorporationID,
                                              final AttributeSelector shareholderCorporationName,
                                              final AttributeSelector shareholderName,
                                              final AttributeSelector shares) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Shareholder>>() {
        @Override
        public List<Shareholder> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM Shareholder c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "shareholderID", shareholderID);
          AttributeSelector.addBooleanSelector(qs, "c", "isCorporation", isCorporation);
          AttributeSelector.addLongSelector(qs, "c", "shareholderCorporationID", shareholderCorporationID);
          AttributeSelector.addStringSelector(qs, "c", "shareholderCorporationName", shareholderCorporationName, p);
          AttributeSelector.addStringSelector(qs, "c", "shareholderName", shareholderName, p);
          AttributeSelector.addIntSelector(qs, "c", "shares", shares);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Shareholder> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Shareholder.class);
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
