package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_character_sheet_attributes")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheetAttributes.get",
        query = "SELECT c FROM CharacterSheetAttributes c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterSheetAttributes extends CachedData {
  protected static final Logger log = Logger.getLogger(CharacterSheetAttributes.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int intelligence;
  private int memory;
  private int charisma;
  private int perception;
  private int willpower;
  private int bonusRemaps;
  private long lastRemapDate = -1;
  private long accruedRemapCooldownDate = -1;

  @Transient
  @ApiModelProperty(
      value = "lastRemapDate Date")
  @JsonProperty("lastRemapDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastRemapDateDate;

  @Transient
  @ApiModelProperty(
      value = "accruedRemapCooldownDate Date")
  @JsonProperty("accruedRemapCooldownDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date accruedRemapCooldownDateDate;

  @SuppressWarnings("unused")
  protected CharacterSheetAttributes() {}

  public CharacterSheetAttributes(int intelligence, int memory, int charisma, int perception, int willpower,
                                  int bonusRemaps, long lastRemapDate, long accruedRemapCooldownDate) {
    this.intelligence = intelligence;
    this.memory = memory;
    this.charisma = charisma;
    this.perception = perception;
    this.willpower = willpower;
    this.bonusRemaps = bonusRemaps;
    this.lastRemapDate = lastRemapDate;
    this.accruedRemapCooldownDate = accruedRemapCooldownDate;
  }

  /**
   * Update transient date values for readability.
   */
  @SuppressWarnings("Duplicates")
  @Override
  public void prepareTransient() {
    fixDates();
    lastRemapDateDate = assignDateField(lastRemapDate);
    accruedRemapCooldownDateDate = assignDateField(accruedRemapCooldownDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterSheetAttributes)) return false;
    CharacterSheetAttributes other = (CharacterSheetAttributes) sup;
    return intelligence == other.intelligence &&
        memory == other.memory &&
        charisma == other.charisma &&
        perception == other.perception &&
        willpower == other.willpower &&
        bonusRemaps == other.bonusRemaps &&
        lastRemapDate == other.lastRemapDate &&
        accruedRemapCooldownDate == other.accruedRemapCooldownDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getIntelligence() {
    return intelligence;
  }

  public int getMemory() {
    return memory;
  }

  public int getCharisma() {
    return charisma;
  }

  public int getPerception() {
    return perception;
  }

  public int getWillpower() {
    return willpower;
  }

  public int getBonusRemaps() {
    return bonusRemaps;
  }

  public long getLastRemapDate() {
    return lastRemapDate;
  }

  public long getAccruedRemapCooldownDate() {
    return accruedRemapCooldownDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterSheetAttributes that = (CharacterSheetAttributes) o;
    return intelligence == that.intelligence &&
        memory == that.memory &&
        charisma == that.charisma &&
        perception == that.perception &&
        willpower == that.willpower &&
        bonusRemaps == that.bonusRemaps &&
        lastRemapDate == that.lastRemapDate &&
        accruedRemapCooldownDate == that.accruedRemapCooldownDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), intelligence, memory, charisma, perception, willpower, bonusRemaps,
                        lastRemapDate, accruedRemapCooldownDate);
  }

  @Override
  public String toString() {
    return "CharacterSheetAttributes{" +
        "intelligence=" + intelligence +
        ", memory=" + memory +
        ", charisma=" + charisma +
        ", perception=" + perception +
        ", willpower=" + willpower +
        ", bonusRemaps=" + bonusRemaps +
        ", lastRemapDate=" + lastRemapDate +
        ", accruedRemapCooldownDate=" + accruedRemapCooldownDate +
        ", lastRemapDateDate=" + lastRemapDateDate +
        ", accruedRemapCooldownDateDate=" + accruedRemapCooldownDateDate +
        '}';
  }

  public static CharacterSheetAttributes get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterSheetAttributes> getter = EveKitUserAccountProvider.getFactory()
                                                                                                               .getEntityManager()
                                                                                                               .createNamedQuery(
                                                                                                                   "CharacterSheetAttributes.get",
                                                                                                                   CharacterSheetAttributes.class);
                                        getter.setParameter("owner", owner);
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

  public static List<CharacterSheetAttributes> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector intelligence,
      final AttributeSelector memory,
      final AttributeSelector charisma,
      final AttributeSelector perception,
      final AttributeSelector willpower,
      final AttributeSelector bonusRemaps,
      final AttributeSelector lastRemapDate,
      final AttributeSelector accruedRemapCooldownDate) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterSheetAttributes c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "intelligence", intelligence);
                                        AttributeSelector.addIntSelector(qs, "c", "memory", memory);
                                        AttributeSelector.addIntSelector(qs, "c", "charisma", charisma);
                                        AttributeSelector.addIntSelector(qs, "c", "perception", perception);
                                        AttributeSelector.addIntSelector(qs, "c", "willpower", willpower);
                                        AttributeSelector.addIntSelector(qs, "c", "bonusRemaps", bonusRemaps);
                                        AttributeSelector.addLongSelector(qs, "c", "lastRemapDate", lastRemapDate);
                                        AttributeSelector.addLongSelector(qs, "c", "accruedRemapCooldownDate",
                                                                          accruedRemapCooldownDate);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterSheetAttributes> query = EveKitUserAccountProvider.getFactory()
                                                                                                              .getEntityManager()
                                                                                                              .createQuery(
                                                                                                                  qs.toString(),
                                                                                                                  CharacterSheetAttributes.class);
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
