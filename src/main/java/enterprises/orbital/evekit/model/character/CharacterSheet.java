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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_character_sheet")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheet.get",
        query = "SELECT c FROM CharacterSheet c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterSheet extends CachedData {
  protected static final Logger log               = Logger.getLogger(CharacterSheet.class.getName());
  private static final byte[]   MASK              = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  // The following character sheet features have been re-factored into separate
  // classes because they change frequently enough that it would be expensive to
  // duplicate the entire character sheet each time:
  //
  // @Serialize
  // private BigDecimal balance; <-- now in CharacterSheetBalance
  //
  // private long jumpActivation; <-- these three now in CharacterSheetJump
  // private long jumpFatigue;
  // private long jumpLastUpdate;
  //
  // private long cloneJumpDate; <-- now in CharacterSheetClone
  //
  private long                  characterID;
  private String                name;
  private long                  corporationID;
  private String                corporationName;
  private String                race;
  private long                  doB               = -1;
  private int                   bloodlineID;
  private String                bloodline;
  private int                   ancestryID;
  private String                ancestry;
  private String                gender;
  private String                allianceName;
  private long                  allianceID;
  private String                factionName;
  private long                  factionID;
  private int                   intelligence;
  private int                   memory;
  private int                   charisma;
  private int                   perception;
  private int                   willpower;
  private long                  homeStationID;
  private long                  lastRespecDate    = -1;
  private long                  lastTimedRespec   = -1;
  private int                   freeRespecs;
  private long                  freeSkillPoints;
  private long                  remoteStationDate = -1;
  @Transient
  @ApiModelProperty(
      value = "doB Date")
  @JsonProperty("doBDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  doBDate;
  @Transient
  @ApiModelProperty(
      value = "lastRespecDate Date")
  @JsonProperty("lastRespecDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  lastRespecDateDate;
  @Transient
  @ApiModelProperty(
      value = "lastTimedRespec Date")
  @JsonProperty("lastTimedRespecDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  lastTimedRespecDate;
  @Transient
  @ApiModelProperty(
      value = "remoteStationDate Date")
  @JsonProperty("remoteStationDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                  remoteStationDateDate;

  @SuppressWarnings("unused")
  protected CharacterSheet() {}

  public CharacterSheet(long characterID, String name, long corporationID, String corporationName, String race, long doB, int bloodlineID, String bloodline,
                        int ancestryID, String ancestry, String gender, String allianceName, long allianceID, String factionName, long factionID,
                        int intelligence, int memory, int charisma, int perception, int willpower, long homeStationID, long lastRespecDate,
                        long lastTimedRespec, int freeRespecs, long freeSkillPoints, long remoteStationDate) {
    super();
    this.characterID = characterID;
    this.name = name;
    this.corporationID = corporationID;
    this.corporationName = corporationName;
    this.race = race;
    this.doB = doB;
    this.bloodlineID = bloodlineID;
    this.bloodline = bloodline;
    this.ancestryID = ancestryID;
    this.ancestry = ancestry;
    this.gender = gender;
    this.allianceName = allianceName;
    this.allianceID = allianceID;
    this.factionName = factionName;
    this.factionID = factionID;
    this.intelligence = intelligence;
    this.memory = memory;
    this.charisma = charisma;
    this.perception = perception;
    this.willpower = willpower;
    this.homeStationID = homeStationID;
    this.lastRespecDate = lastRespecDate;
    this.lastTimedRespec = lastTimedRespec;
    this.freeRespecs = freeRespecs;
    this.freeSkillPoints = freeSkillPoints;
    this.remoteStationDate = remoteStationDate;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
    doBDate = assignDateField(doB);
    lastRespecDateDate = assignDateField(lastRespecDate);
    lastTimedRespecDate = assignDateField(lastTimedRespec);
    remoteStationDateDate = assignDateField(remoteStationDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterSheet)) return false;
    CharacterSheet other = (CharacterSheet) sup;
    return characterID == other.characterID && nullSafeObjectCompare(name, other.name) && corporationID == other.corporationID
        && nullSafeObjectCompare(corporationName, other.corporationName) && nullSafeObjectCompare(race, other.race) && doB == other.doB
        && bloodlineID == other.bloodlineID && nullSafeObjectCompare(bloodline, other.bloodline) && ancestryID == other.ancestryID
        && nullSafeObjectCompare(ancestry, other.ancestry) && nullSafeObjectCompare(gender, other.gender)
        && nullSafeObjectCompare(allianceName, other.allianceName) && allianceID == other.allianceID && nullSafeObjectCompare(factionName, other.factionName)
        && factionID == other.factionID && intelligence == other.intelligence && memory == other.memory && charisma == other.charisma
        && perception == other.perception && willpower == other.willpower && homeStationID == other.homeStationID && lastRespecDate == other.lastRespecDate
        && lastTimedRespec == other.lastTimedRespec && freeRespecs == other.freeRespecs && freeSkillPoints == other.freeSkillPoints
        && remoteStationDate == other.remoteStationDate;
  }

  /**
   * A specialized version of the equivalence check used when processing a Clones synchronization event which can only change certain fields of the character
   * sheet.
   * 
   * @param sup
   *          the cached data object to check
   * @return true if the argument is equivalent to this object, false otherwise.
   */
  public boolean equivalentClonesCheck(
                                       CachedData sup) {
    if (!(sup instanceof CharacterSheet)) return false;
    CharacterSheet other = (CharacterSheet) sup;
    return nullSafeObjectCompare(race, other.race) && doB == other.doB && bloodlineID == other.bloodlineID && nullSafeObjectCompare(bloodline, other.bloodline)
        && ancestryID == other.ancestryID && nullSafeObjectCompare(ancestry, other.ancestry) && nullSafeObjectCompare(gender, other.gender)
        && intelligence == other.intelligence && memory == other.memory && charisma == other.charisma && perception == other.perception
        && willpower == other.willpower && lastRespecDate == other.lastRespecDate && lastTimedRespec == other.lastTimedRespec
        && freeRespecs == other.freeRespecs && remoteStationDate == other.remoteStationDate;
  }

  /**
   * A specialized version of the equivalence check used when processing a Skills synchronization event which can only change the free skill points of a
   * character sheet.
   * 
   * @param sup
   *          the cached data object to check
   * @return true if the argument is equivalent to this object, false otherwise.
   */
  public boolean equivalentSkillsCheck(
                                       CachedData sup) {
    if (!(sup instanceof CharacterSheet)) return false;
    CharacterSheet other = (CharacterSheet) sup;
    return freeSkillPoints == other.freeSkillPoints;
  }

  /**
   * Copy all fields from src except for those fields which can be changed by a Clones synchronization event.
   * 
   * @param src
   *          the source from which to copy
   */
  public void copyForClones(
                            CharacterSheet src) {
    characterID = src.getCharacterID();
    name = src.getName();
    corporationID = src.getCorporationID();
    corporationName = src.getCorporationName();
    allianceName = src.getAllianceName();
    allianceID = src.getAllianceID();
    factionName = src.getFactionName();
    factionID = src.getFactionID();
    homeStationID = src.getHomeStationID();
    freeSkillPoints = src.getFreeSkillPoints();
  }

  /**
   * Copy all fields from src except for those fields which can be changed by a Skills synchronization event.
   * 
   * @param src
   *          the source from which to copy
   */
  public void copyForSkills(
                            CharacterSheet src) {
    characterID = src.getCharacterID();
    name = src.getName();
    corporationID = src.getCorporationID();
    corporationName = src.getCorporationName();
    race = src.getRace();
    doB = src.getDoB();
    bloodlineID = src.getBloodlineID();
    bloodline = src.getBloodline();
    ancestryID = src.getAncestryID();
    ancestry = src.getAncestry();
    gender = src.getGender();
    allianceName = src.getAllianceName();
    allianceID = src.getAllianceID();
    factionName = src.getFactionName();
    factionID = src.getFactionID();
    intelligence = src.getIntelligence();
    memory = src.getMemory();
    charisma = src.getCharisma();
    perception = src.getPerception();
    willpower = src.getWillpower();
    homeStationID = src.getHomeStationID();
    lastRespecDate = src.getLastRespecDate();
    lastTimedRespec = src.getLastTimedRespec();
    freeRespecs = src.getFreeRespecs();
    remoteStationDate = src.getRemoteStationDate();
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

  public String getFactionName() {
    return factionName;
  }

  public long getFactionID() {
    return factionID;
  }

  public long getHomeStationID() {
    return homeStationID;
  }

  public long getLastRespecDate() {
    return lastRespecDate;
  }

  public long getLastTimedRespec() {
    return lastTimedRespec;
  }

  public int getFreeRespecs() {
    return freeRespecs;
  }

  public long getFreeSkillPoints() {
    return freeSkillPoints;
  }

  public long getRemoteStationDate() {
    return remoteStationDate;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public String getCorporationName() {
    return corporationName;
  }

  public String getRace() {
    return race;
  }

  public long getDoB() {
    return doB;
  }

  public int getBloodlineID() {
    return bloodlineID;
  }

  public String getBloodline() {
    return bloodline;
  }

  public int getAncestryID() {
    return ancestryID;
  }

  public void setAncestryID(
                            int ancestryID) {
    this.ancestryID = ancestryID;
  }

  public String getAncestry() {
    return ancestry;
  }

  public String getGender() {
    return gender;
  }

  public String getAllianceName() {
    return allianceName;
  }

  public long getAllianceID() {
    return allianceID;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + ((allianceName == null) ? 0 : allianceName.hashCode());
    result = prime * result + ((ancestry == null) ? 0 : ancestry.hashCode());
    result = prime * result + ancestryID;
    result = prime * result + ((bloodline == null) ? 0 : bloodline.hashCode());
    result = prime * result + bloodlineID;
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + charisma;
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((corporationName == null) ? 0 : corporationName.hashCode());
    result = prime * result + (int) (doB ^ (doB >>> 32));
    result = prime * result + (int) (factionID ^ (factionID >>> 32));
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    result = prime * result + freeRespecs;
    result = prime * result + (int) (freeSkillPoints ^ (freeSkillPoints >>> 32));
    result = prime * result + ((gender == null) ? 0 : gender.hashCode());
    result = prime * result + (int) (homeStationID ^ (homeStationID >>> 32));
    result = prime * result + intelligence;
    result = prime * result + (int) (lastRespecDate ^ (lastRespecDate >>> 32));
    result = prime * result + (int) (lastTimedRespec ^ (lastTimedRespec >>> 32));
    result = prime * result + memory;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + perception;
    result = prime * result + ((race == null) ? 0 : race.hashCode());
    result = prime * result + (int) (remoteStationDate ^ (remoteStationDate >>> 32));
    result = prime * result + willpower;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterSheet other = (CharacterSheet) obj;
    if (allianceID != other.allianceID) return false;
    if (allianceName == null) {
      if (other.allianceName != null) return false;
    } else if (!allianceName.equals(other.allianceName)) return false;
    if (ancestry == null) {
      if (other.ancestry != null) return false;
    } else if (!ancestry.equals(other.ancestry)) return false;
    if (ancestryID != other.ancestryID) return false;
    if (bloodline == null) {
      if (other.bloodline != null) return false;
    } else if (!bloodline.equals(other.bloodline)) return false;
    if (bloodlineID != other.bloodlineID) return false;
    if (characterID != other.characterID) return false;
    if (charisma != other.charisma) return false;
    if (corporationID != other.corporationID) return false;
    if (corporationName == null) {
      if (other.corporationName != null) return false;
    } else if (!corporationName.equals(other.corporationName)) return false;
    if (doB != other.doB) return false;
    if (factionID != other.factionID) return false;
    if (factionName == null) {
      if (other.factionName != null) return false;
    } else if (!factionName.equals(other.factionName)) return false;
    if (freeRespecs != other.freeRespecs) return false;
    if (freeSkillPoints != other.freeSkillPoints) return false;
    if (gender == null) {
      if (other.gender != null) return false;
    } else if (!gender.equals(other.gender)) return false;
    if (homeStationID != other.homeStationID) return false;
    if (intelligence != other.intelligence) return false;
    if (lastRespecDate != other.lastRespecDate) return false;
    if (lastTimedRespec != other.lastTimedRespec) return false;
    if (memory != other.memory) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (perception != other.perception) return false;
    if (race == null) {
      if (other.race != null) return false;
    } else if (!race.equals(other.race)) return false;
    if (remoteStationDate != other.remoteStationDate) return false;
    if (willpower != other.willpower) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterSheet [characterID=" + characterID + ", name=" + name + ", corporationID=" + corporationID + ", corporationName=" + corporationName
        + ", race=" + race + ", doB=" + doB + ", bloodlineID=" + bloodlineID + ", bloodline=" + bloodline + ", ancestryID=" + ancestryID + ", ancestry="
        + ancestry + ", gender=" + gender + ", allianceName=" + allianceName + ", allianceID=" + allianceID + ", factionName=" + factionName + ", factionID="
        + factionID + ", intelligence=" + intelligence + ", memory=" + memory + ", charisma=" + charisma + ", perception=" + perception + ", willpower="
        + willpower + ", homeStationID=" + homeStationID + ", lastRespecDate=" + lastRespecDate + ", lastTimedRespec=" + lastTimedRespec + ", freeRespecs="
        + freeRespecs + ", freeSkillPoints=" + freeSkillPoints + ", remoteStationDate=" + remoteStationDate + "]";
  }

  public static CharacterSheet get(
                                   final SynchronizedEveAccount owner,
                                   final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterSheet>() {
        @Override
        public CharacterSheet run() throws Exception {
          TypedQuery<CharacterSheet> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSheet.get",
                                                                                                                         CharacterSheet.class);
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

  public static List<CharacterSheet> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector characterID,
                                                 final AttributeSelector name,
                                                 final AttributeSelector corporationID,
                                                 final AttributeSelector corporationName,
                                                 final AttributeSelector race,
                                                 final AttributeSelector doB,
                                                 final AttributeSelector bloodlineID,
                                                 final AttributeSelector bloodline,
                                                 final AttributeSelector ancestryID,
                                                 final AttributeSelector ancestry,
                                                 final AttributeSelector gender,
                                                 final AttributeSelector allianceName,
                                                 final AttributeSelector allianceID,
                                                 final AttributeSelector factionName,
                                                 final AttributeSelector factionID,
                                                 final AttributeSelector intelligence,
                                                 final AttributeSelector memory,
                                                 final AttributeSelector charisma,
                                                 final AttributeSelector perception,
                                                 final AttributeSelector willpower,
                                                 final AttributeSelector homeStationID,
                                                 final AttributeSelector lastRespecDate,
                                                 final AttributeSelector lastTimedRespec,
                                                 final AttributeSelector freeRespecs,
                                                 final AttributeSelector freeSkillPoints,
                                                 final AttributeSelector remoteStationDate) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterSheet>>() {
        @Override
        public List<CharacterSheet> run() throws Exception {
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
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
          AttributeSelector.addStringSelector(qs, "c", "corporationName", corporationName, p);
          AttributeSelector.addStringSelector(qs, "c", "race", race, p);
          AttributeSelector.addLongSelector(qs, "c", "doB", doB);
          AttributeSelector.addIntSelector(qs, "c", "bloodlineID", bloodlineID);
          AttributeSelector.addStringSelector(qs, "c", "bloodline", bloodline, p);
          AttributeSelector.addIntSelector(qs, "c", "ancestryID", ancestryID);
          AttributeSelector.addStringSelector(qs, "c", "ancestry", ancestry, p);
          AttributeSelector.addStringSelector(qs, "c", "gender", gender, p);
          AttributeSelector.addStringSelector(qs, "c", "allianceName", allianceName, p);
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addStringSelector(qs, "c", "factionName", factionName, p);
          AttributeSelector.addLongSelector(qs, "c", "factionID", factionID);
          AttributeSelector.addIntSelector(qs, "c", "intelligence", intelligence);
          AttributeSelector.addIntSelector(qs, "c", "memory", memory);
          AttributeSelector.addIntSelector(qs, "c", "charisma", charisma);
          AttributeSelector.addIntSelector(qs, "c", "perception", perception);
          AttributeSelector.addIntSelector(qs, "c", "willpower", willpower);
          AttributeSelector.addLongSelector(qs, "c", "homeStationID", homeStationID);
          AttributeSelector.addLongSelector(qs, "c", "lastRespecDate", lastRespecDate);
          AttributeSelector.addLongSelector(qs, "c", "lastTimedRespec", lastTimedRespec);
          AttributeSelector.addIntSelector(qs, "c", "freeRespecs", freeRespecs);
          AttributeSelector.addLongSelector(qs, "c", "freeSkillPoints", freeSkillPoints);
          AttributeSelector.addLongSelector(qs, "c", "remoteStationDate", remoteStationDate);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterSheet> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterSheet.class);
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
