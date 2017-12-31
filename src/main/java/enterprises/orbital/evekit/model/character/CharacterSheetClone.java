package enterprises.orbital.evekit.model.character;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_character_sheet_clone")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheetClone.get",
        query = "SELECT c FROM CharacterSheetClone c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterSheetClone extends CachedData {
  protected static final Logger log           = Logger.getLogger(CharacterSheetClone.class.getName());
  private static final byte[]   MASK          = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  // Stores just the cloneJumpDate part of the character sheet since this may change
  // frequently and we want to avoid having to evolve the entire character sheet.
  private long                  cloneJumpDate = -1;
  @Transient
  @ApiModelProperty(
      value = "cloneJumpDate Date")
  @JsonProperty("cloneJumpDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  cloneJumpDateDate;

  @SuppressWarnings("unused")
  protected CharacterSheetClone() {}

  public CharacterSheetClone(long cloneJumpDate) {
    this.cloneJumpDate = cloneJumpDate;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    cloneJumpDateDate = assignDateField(cloneJumpDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterSheetClone)) return false;
    CharacterSheetClone other = (CharacterSheetClone) sup;
    return cloneJumpDate == other.cloneJumpDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getCloneJumpDate() {
    return cloneJumpDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (cloneJumpDate ^ (cloneJumpDate >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterSheetClone other = (CharacterSheetClone) obj;
    if (cloneJumpDate != other.cloneJumpDate) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterSheetClone [cloneJumpDate=" + cloneJumpDate + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterSheetClone get(
                                        final SynchronizedEveAccount owner,
                                        final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterSheetClone>() {
        @Override
        public CharacterSheetClone run() throws Exception {
          TypedQuery<CharacterSheetClone> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSheetClone.get",
                                                                                                                              CharacterSheetClone.class);
          getter.setParameter("owner", owner);
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

  public static List<CharacterSheetClone> accessQuery(
                                                      final SynchronizedEveAccount owner,
                                                      final long contid,
                                                      final int maxresults,
                                                      final boolean reverse,
                                                      final AttributeSelector at,
                                                      final AttributeSelector cloneJumpDate) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterSheetClone>>() {
        @Override
        public List<CharacterSheetClone> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterSheetClone c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "cloneJumpDate", cloneJumpDate);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterSheetClone> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(),
                                                                                                                        CharacterSheetClone.class);
          query.setParameter("owner", owner);
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
