package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
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
    name = "evekit_data_character_sheet")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheet.get",
        query = "SELECT c FROM CharacterSheet c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterSheet extends CachedData {
  protected static final Logger log = Logger.getLogger(CharacterSheet.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private long characterID;
  private String name;
  private int corporationID;
  private int raceID;
  private long doB = -1;
  private int bloodlineID;
  private int ancestryID;
  private String gender;
  private int allianceID;
  private int factionID;
  @Lob
  @Column(
      length = 102400)
  private String description;
  private float securityStatus;
  @Lob
  @Column(
      length = 1000)
  private String title;

  @Transient
  @ApiModelProperty(
      value = "doB Date")
  @JsonProperty("doBDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date doBDate;

  @SuppressWarnings("unused")
  protected CharacterSheet() {}

  public CharacterSheet(long characterID, String name, int corporationID, int raceID, long doB, int bloodlineID,
                        int ancestryID, String gender, int allianceID, int factionID, String description,
                        float securityStatus, String title) {
    this.characterID = characterID;
    this.name = name;
    this.corporationID = corporationID;
    this.raceID = raceID;
    this.doB = doB;
    this.bloodlineID = bloodlineID;
    this.ancestryID = ancestryID;
    this.gender = gender;
    this.allianceID = allianceID;
    this.factionID = factionID;
    this.description = description;
    this.securityStatus = securityStatus;
    this.title = title;
  }

  /**
   * Update transient date values for readability.
   */
  @SuppressWarnings("Duplicates")
  @Override
  public void prepareTransient() {
    fixDates();
    doBDate = assignDateField(doB);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterSheet)) return false;
    CharacterSheet other = (CharacterSheet) sup;
    return characterID == other.characterID &&
        nullSafeObjectCompare(name, other.name) &&
        corporationID == other.corporationID &&
        raceID == other.raceID &&
        doB == other.doB &&
        bloodlineID == other.bloodlineID &&
        ancestryID == other.ancestryID &&
        nullSafeObjectCompare(gender, other.gender) &&
        allianceID == other.allianceID &&
        factionID == other.factionID &&
        nullSafeObjectCompare(description, other.description) &&
        floatCompare(securityStatus, other.securityStatus, 0.00001F) &&
        nullSafeObjectCompare(title, other.title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getCharacterID() {
    return characterID;
  }

  public String getName() {
    return name;
  }

  public int getFactionID() {
    return factionID;
  }

  public int getCorporationID() {
    return corporationID;
  }

  public long getDoB() {
    return doB;
  }

  public int getBloodlineID() {
    return bloodlineID;
  }

  public int getAncestryID() {
    return ancestryID;
  }

  public String getGender() {
    return gender;
  }

  public int getAllianceID() {
    return allianceID;
  }

  public int getRaceID() {
    return raceID;
  }

  public String getDescription() {
    return description;
  }

  public float getSecurityStatus() {
    return securityStatus;
  }

  public String getTitle() { return title; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterSheet that = (CharacterSheet) o;
    return characterID == that.characterID &&
        corporationID == that.corporationID &&
        raceID == that.raceID &&
        doB == that.doB &&
        bloodlineID == that.bloodlineID &&
        ancestryID == that.ancestryID &&
        allianceID == that.allianceID &&
        factionID == that.factionID &&
        Float.compare(that.securityStatus, securityStatus) == 0 &&
        Objects.equals(name, that.name) &&
        Objects.equals(gender, that.gender) &&
        Objects.equals(description, that.description) &&
        Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID, name, corporationID, raceID, doB, bloodlineID, ancestryID,
                        gender, allianceID, factionID, description, securityStatus, title);
  }

  @Override
  public String toString() {
    return "CharacterSheet{" +
        "characterID=" + characterID +
        ", name='" + name + '\'' +
        ", corporationID=" + corporationID +
        ", raceID=" + raceID +
        ", doB=" + doB +
        ", bloodlineID=" + bloodlineID +
        ", ancestryID=" + ancestryID +
        ", gender='" + gender + '\'' +
        ", allianceID=" + allianceID +
        ", factionID=" + factionID +
        ", description='" + description + '\'' +
        ", securityStatus=" + securityStatus +
        ", title='" + title + '\'' +
        ", doBDate=" + doBDate +
        '}';
  }

  public static CharacterSheet get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterSheet> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery(
                                                                                                         "CharacterSheet.get",
                                                                                                         CharacterSheet.class);
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

  public static List<CharacterSheet> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector characterID,
      final AttributeSelector name,
      final AttributeSelector corporationID,
      final AttributeSelector raceID,
      final AttributeSelector doB,
      final AttributeSelector bloodlineID,
      final AttributeSelector ancestryID,
      final AttributeSelector gender,
      final AttributeSelector allianceID,
      final AttributeSelector factionID,
      final AttributeSelector description,
      final AttributeSelector securityStatus,
      final AttributeSelector title) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterSheet c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        AttributeSelector.addIntSelector(qs, "c", "corporationID", corporationID);
                                        AttributeSelector.addIntSelector(qs, "c", "raceID", raceID);
                                        AttributeSelector.addLongSelector(qs, "c", "doB", doB);
                                        AttributeSelector.addIntSelector(qs, "c", "bloodlineID", bloodlineID);
                                        AttributeSelector.addIntSelector(qs, "c", "ancestryID", ancestryID);
                                        AttributeSelector.addStringSelector(qs, "c", "gender", gender, p);
                                        AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                        AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                        AttributeSelector.addStringSelector(qs, "c", "description", description, p);
                                        AttributeSelector.addFloatSelector(qs, "c", "securityStatus", securityStatus);
                                        AttributeSelector.addStringSelector(qs, "c", "title", title, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterSheet> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(
                                                                                                        qs.toString(),
                                                                                                        CharacterSheet.class);
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
