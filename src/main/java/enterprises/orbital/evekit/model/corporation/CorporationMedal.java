package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
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
    name = "evekit_data_corporation_medal",
    indexes = {
        @Index(
            name = "medalIDIndex",
            columnList = "medalID",
            unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "CorporationMedal.getByMedalID",
        query = "SELECT c FROM CorporationMedal c where c.owner = :owner and c.medalID = :medal and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CorporationMedal.getAll",
        query = "SELECT c FROM CorporationMedal c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hours
public class CorporationMedal extends CachedData {
  private static final Logger log  = Logger.getLogger(CorporationMedal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_MEDALS);
  private int                 medalID;
  @Lob
  @Column(
      length = 102400)
  private String              description;
  private String              title;
  private long                created;
  private long                creatorID;

  @SuppressWarnings("unused")
  private CorporationMedal() {}

  public CorporationMedal(int medalID, String description, String title, long created, long creatorID) {
    super();
    this.medalID = medalID;
    this.description = description;
    this.title = title;
    this.created = created;
    this.creatorID = creatorID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CorporationMedal)) return false;
    CorporationMedal other = (CorporationMedal) sup;
    return medalID == other.medalID && nullSafeObjectCompare(description, other.description) && nullSafeObjectCompare(title, other.title)
        && created == other.created && creatorID == other.creatorID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getMedalID() {
    return medalID;
  }

  public String getDescription() {
    return description;
  }

  public String getTitle() {
    return title;
  }

  public long getCreated() {
    return created;
  }

  public long getCreatorID() {
    return creatorID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (created ^ (created >>> 32));
    result = prime * result + (int) (creatorID ^ (creatorID >>> 32));
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + medalID;
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CorporationMedal other = (CorporationMedal) obj;
    if (created != other.created) return false;
    if (creatorID != other.creatorID) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (medalID != other.medalID) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CorporationMedal [medalID=" + medalID + ", description=" + description + ", title=" + title + ", created=" + created + ", creatorID=" + creatorID
        + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CorporationMedal get(
                                     final SynchronizedEveAccount owner,
                                     final long time,
                                     final int medalID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CorporationMedal>() {
        @Override
        public CorporationMedal run() throws Exception {
          TypedQuery<CorporationMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CorporationMedal.getByMedalID",
                                                                                                                           CorporationMedal.class);
          getter.setParameter("owner", owner);
          getter.setParameter("medal", medalID);
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

  public static List<CorporationMedal> getAll(
                                              final SynchronizedEveAccount owner,
                                              final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationMedal>>() {
        @Override
        public List<CorporationMedal> run() throws Exception {
          TypedQuery<CorporationMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CorporationMedal.getAll",
                                                                                                                           CorporationMedal.class);
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

  public static List<CorporationMedal> accessQuery(
                                                   final SynchronizedEveAccount owner,
                                                   final long contid,
                                                   final int maxresults,
                                                   final AttributeSelector at,
                                                   final AttributeSelector medalID,
                                                   final AttributeSelector description,
                                                   final AttributeSelector title,
                                                   final AttributeSelector created,
                                                   final AttributeSelector creatorID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationMedal>>() {
        @Override
        public List<CorporationMedal> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CorporationMedal c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
          AttributeSelector.addStringSelector(qs, "c", "description", description, p);
          AttributeSelector.addStringSelector(qs, "c", "title", title, p);
          AttributeSelector.addLongSelector(qs, "c", "created", created);
          AttributeSelector.addLongSelector(qs, "c", "creatorID", creatorID);
          // Set CID constraint
          qs.append(" and c.cid > ").append(contid);
          // Order by CID (asc)
          qs.append(" order by cid asc");
          // Return result
          TypedQuery<CorporationMedal> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CorporationMedal.class);
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
