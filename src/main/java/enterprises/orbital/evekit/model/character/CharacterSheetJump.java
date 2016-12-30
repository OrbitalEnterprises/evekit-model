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
    name = "evekit_data_character_sheet_jump")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheetJump.get",
        query = "SELECT c FROM CharacterSheetJump c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterSheetJump extends CachedData {
  protected static final Logger log            = Logger.getLogger(CharacterSheetJump.class.getName());
  private static final byte[]   MASK           = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  // Stores just the jump timer part of the character sheet since this may
  // change frequently and we want to avoid having to evolve the entire character sheet.
  private long                  jumpActivation = -1;
  private long                  jumpFatigue    = -1;
  private long                  jumpLastUpdate = -1;
  @Transient
  @ApiModelProperty(
      value = "jumpActivation Date")
  @JsonProperty("jumpActivationDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  jumpActivationDate;
  @Transient
  @ApiModelProperty(
      value = "jumpFatigue Date")
  @JsonProperty("jumpFatigueDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  jumpFatigueDate;
  @Transient
  @ApiModelProperty(
      value = "jumpLastUpdate Date")
  @JsonProperty("jumpLastUpdateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  jumpLastUpdateDate;

  @SuppressWarnings("unused")
  private CharacterSheetJump() {}

  public CharacterSheetJump(long jumpActivation, long jumpFatigue, long jumpLastUpdate) {
    super();
    this.jumpActivation = jumpActivation;
    this.jumpFatigue = jumpFatigue;
    this.jumpLastUpdate = jumpLastUpdate;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    jumpActivationDate = assignDateField(jumpActivation);
    jumpFatigueDate = assignDateField(jumpFatigue);
    jumpLastUpdateDate = assignDateField(jumpLastUpdate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterSheetJump)) return false;
    CharacterSheetJump other = (CharacterSheetJump) sup;
    return jumpActivation == other.jumpActivation && jumpFatigue == other.jumpFatigue && jumpLastUpdate == other.jumpLastUpdate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getJumpActivation() {
    return jumpActivation;
  }

  public long getJumpFatigue() {
    return jumpFatigue;
  }

  public long getJumpLastUpdate() {
    return jumpLastUpdate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (jumpActivation ^ (jumpActivation >>> 32));
    result = prime * result + (int) (jumpFatigue ^ (jumpFatigue >>> 32));
    result = prime * result + (int) (jumpLastUpdate ^ (jumpLastUpdate >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterSheetJump other = (CharacterSheetJump) obj;
    if (jumpActivation != other.jumpActivation) return false;
    if (jumpFatigue != other.jumpFatigue) return false;
    if (jumpLastUpdate != other.jumpLastUpdate) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterSheetJump [jumpActivation=" + jumpActivation + ", jumpFatigue=" + jumpFatigue + ", jumpLastUpdate=" + jumpLastUpdate + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterSheetJump get(
                                       final SynchronizedEveAccount owner,
                                       final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterSheetJump>() {
        @Override
        public CharacterSheetJump run() throws Exception {
          TypedQuery<CharacterSheetJump> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSheetJump.get",
                                                                                                                             CharacterSheetJump.class);
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

  public static List<CharacterSheetJump> accessQuery(
                                                     final SynchronizedEveAccount owner,
                                                     final long contid,
                                                     final int maxresults,
                                                     final boolean reverse,
                                                     final AttributeSelector at,
                                                     final AttributeSelector jumpActivation,
                                                     final AttributeSelector jumpFatigue,
                                                     final AttributeSelector jumpLastUpdate) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterSheetJump>>() {
        @Override
        public List<CharacterSheetJump> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterSheetJump c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addLongSelector(qs, "c", "jumpActivation", jumpActivation);
          AttributeSelector.addLongSelector(qs, "c", "jumpFatigue", jumpFatigue);
          AttributeSelector.addLongSelector(qs, "c", "jumpLastUpdate", jumpLastUpdate);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterSheetJump> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterSheetJump.class);
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
