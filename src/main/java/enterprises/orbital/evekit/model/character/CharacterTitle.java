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
    name = "evekit_data_character_title",
    indexes = {
        @Index(
            name = "titleIDIndex",
            columnList = "titleID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterTitle.getByTitleID",
        query = "SELECT c FROM CharacterTitle c where c.owner = :owner and c.titleID = :title and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterTitle.getAll",
        query = "SELECT c FROM CharacterTitle c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterTitle extends CachedData {
  private static final Logger log  = Logger.getLogger(CharacterTitle.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  private long                titleID;
  private String              titleName;

  @SuppressWarnings("unused")
  private CharacterTitle() {}

  public CharacterTitle(long titleID, String titleName) {
    super();
    this.titleID = titleID;
    this.titleName = titleName;
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
    if (!(sup instanceof CharacterTitle)) return false;
    CharacterTitle other = (CharacterTitle) sup;
    return titleID == other.titleID && nullSafeObjectCompare(titleName, other.titleName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getTitleID() {
    return titleID;
  }

  public String getTitleName() {
    return titleName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (titleID ^ (titleID >>> 32));
    result = prime * result + ((titleName == null) ? 0 : titleName.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterTitle other = (CharacterTitle) obj;
    if (titleID != other.titleID) return false;
    if (titleName == null) {
      if (other.titleName != null) return false;
    } else if (!titleName.equals(other.titleName)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterTitle [titleID=" + titleID + ", titleName=" + titleName + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterTitle get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final long titleID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterTitle>() {
        @Override
        public CharacterTitle run() throws Exception {
          TypedQuery<CharacterTitle> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterTitle.getByTitleID",
                                                                                                                         CharacterTitle.class);
          getter.setParameter("owner", owner);
          getter.setParameter("title", titleID);
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

  public static List<CharacterTitle> getAllTitles(
                                                  final SynchronizedEveAccount owner,
                                                  final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterTitle>>() {
        @Override
        public List<CharacterTitle> run() throws Exception {
          TypedQuery<CharacterTitle> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterTitle.getAll",
                                                                                                                         CharacterTitle.class);
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

  public static List<CharacterTitle> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector titleID,
                                                 final AttributeSelector titleName) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterTitle>>() {
        @Override
        public List<CharacterTitle> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterTitle c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "titleID", titleID);
          AttributeSelector.addStringSelector(qs, "c", "titleName", titleName, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterTitle> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterTitle.class);
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
