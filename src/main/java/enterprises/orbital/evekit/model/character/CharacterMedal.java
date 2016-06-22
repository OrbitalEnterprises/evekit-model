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
    name = "evekit_data_character_medal",
    indexes = {
        @Index(
            name = "medalIDIndex",
            columnList = "medalID",
            unique = false),
        @Index(
            name = "issuedIndex",
            columnList = "issued",
            unique = false)
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterMedal.getbyMedalIDAndIssued",
        query = "SELECT c FROM CharacterMedal c where c.owner = :owner and c.medalID = :mid and c.issued = :issued and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterMedal.getAll",
        query = "SELECT c FROM CharacterMedal c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 7 hour cache time - API caches for 6 hours
public class CharacterMedal extends CachedData {
  private static final Logger log  = Logger.getLogger(CharacterMedal.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEDALS);
  private String              description;
  private int                 medalID;
  private String              title;
  private long                corporationID;
  private long                issued;
  private long                issuerID;
  private String              reason;
  private String              status;

  @SuppressWarnings("unused")
  private CharacterMedal() {}

  public CharacterMedal(String description, int medalID, String title, long corporationID, long issued, long issuerID, String reason, String status) {
    super();
    this.description = description;
    this.medalID = medalID;
    this.title = title;
    this.corporationID = corporationID;
    this.issued = issued;
    this.issuerID = issuerID;
    this.reason = reason;
    this.status = status;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterMedal)) return false;
    CharacterMedal other = (CharacterMedal) sup;
    return nullSafeObjectCompare(description, other.description) && medalID == other.medalID && nullSafeObjectCompare(title, other.title)
        && corporationID == other.corporationID && issued == other.issued && issuerID == other.issuerID && nullSafeObjectCompare(reason, other.reason)
        && nullSafeObjectCompare(status, other.status);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getDescription() {
    return description;
  }

  public int getMedalID() {
    return medalID;
  }

  public String getTitle() {
    return title;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public long getIssued() {
    return issued;
  }

  public long getIssuerID() {
    return issuerID;
  }

  public String getReason() {
    return reason;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (int) (issued ^ (issued >>> 32));
    result = prime * result + (int) (issuerID ^ (issuerID >>> 32));
    result = prime * result + medalID;
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterMedal other = (CharacterMedal) obj;
    if (corporationID != other.corporationID) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (issued != other.issued) return false;
    if (issuerID != other.issuerID) return false;
    if (medalID != other.medalID) return false;
    if (reason == null) {
      if (other.reason != null) return false;
    } else if (!reason.equals(other.reason)) return false;
    if (status == null) {
      if (other.status != null) return false;
    } else if (!status.equals(other.status)) return false;
    if (title == null) {
      if (other.title != null) return false;
    } else if (!title.equals(other.title)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterMedal [description=" + description + ", medalID=" + medalID + ", title=" + title + ", corporationID=" + corporationID + ", issued="
        + issued + ", issuerID=" + issuerID + ", reason=" + reason + ", status=" + status + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd="
        + lifeEnd + "]";
  }

  public static CharacterMedal get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final int medalID,
                                   final long issued) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterMedal>() {
        @Override
        public CharacterMedal run() throws Exception {
          TypedQuery<CharacterMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterMedal.getbyMedalIDAndIssued",
                                                                                                                         CharacterMedal.class);
          getter.setParameter("owner", owner);
          getter.setParameter("mid", medalID);
          getter.setParameter("issued", issued);
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

  public static List<CharacterMedal> getAllMedals(
                                                  final SynchronizedEveAccount owner,
                                                  final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterMedal>>() {
        @Override
        public List<CharacterMedal> run() throws Exception {
          TypedQuery<CharacterMedal> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterMedal.getAll",
                                                                                                                         CharacterMedal.class);
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

  public static List<CharacterMedal> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector description,
                                                 final AttributeSelector medalID,
                                                 final AttributeSelector title,
                                                 final AttributeSelector corporationID,
                                                 final AttributeSelector issued,
                                                 final AttributeSelector issuerID,
                                                 final AttributeSelector reason,
                                                 final AttributeSelector status) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterMedal>>() {
        @Override
        public List<CharacterMedal> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterMedal c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addStringSelector(qs, "c", "description", description, p);
          AttributeSelector.addIntSelector(qs, "c", "medalID", medalID);
          AttributeSelector.addStringSelector(qs, "c", "title", title, p);
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
          AttributeSelector.addLongSelector(qs, "c", "issued", issued);
          AttributeSelector.addLongSelector(qs, "c", "issuerID", issuerID);
          AttributeSelector.addStringSelector(qs, "c", "reason", reason, p);
          AttributeSelector.addStringSelector(qs, "c", "status", status, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterMedal> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterMedal.class);
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
