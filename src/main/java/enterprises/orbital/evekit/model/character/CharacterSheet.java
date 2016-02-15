package enterprises.orbital.evekit.model.character;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_character_sheet")
@NamedQueries({
    @NamedQuery(name = "CharacterSheet.get", query = "SELECT c FROM CharacterSheet c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
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
  private String                bloodline;
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
  private int                   freeSkillPoints;
  private long                  remoteStationDate = -1;

  @SuppressWarnings("unused")
  private CharacterSheet() {}

  public CharacterSheet(long characterID, String name, long corporationID, String corporationName, String race, long doB, String bloodline, String ancestry,
                        String gender, String allianceName, long allianceID, String factionName, long factionID, int intelligence, int memory, int charisma,
                        int perception, int willpower, long homeStationID, long lastRespecDate, long lastTimedRespec, int freeRespecs, int freeSkillPoints,
                        long remoteStationDate) {
    super();
    this.characterID = characterID;
    this.name = name;
    this.corporationID = corporationID;
    this.corporationName = corporationName;
    this.race = race;
    this.doB = doB;
    this.bloodline = bloodline;
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
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof CharacterSheet)) return false;
    CharacterSheet other = (CharacterSheet) sup;
    return characterID == other.characterID && nullSafeObjectCompare(name, other.name) && corporationID == other.corporationID
        && nullSafeObjectCompare(corporationName, other.corporationName) && nullSafeObjectCompare(race, other.race) && doB == other.doB
        && nullSafeObjectCompare(bloodline, other.bloodline) && nullSafeObjectCompare(ancestry, other.ancestry) && nullSafeObjectCompare(gender, other.gender)
        && nullSafeObjectCompare(allianceName, other.allianceName) && allianceID == other.allianceID && nullSafeObjectCompare(factionName, other.factionName)
        && factionID == other.factionID && intelligence == other.intelligence && memory == other.memory && charisma == other.charisma
        && perception == other.perception && willpower == other.willpower && homeStationID == other.homeStationID && lastRespecDate == other.lastRespecDate
        && lastTimedRespec == other.lastTimedRespec && freeRespecs == other.freeRespecs && freeSkillPoints == other.freeSkillPoints
        && remoteStationDate == other.remoteStationDate;
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

  public int getFreeSkillPoints() {
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

  public String getBloodline() {
    return bloodline;
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
    result = prime * result + ((bloodline == null) ? 0 : bloodline.hashCode());
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + charisma;
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((corporationName == null) ? 0 : corporationName.hashCode());
    result = prime * result + (int) (doB ^ (doB >>> 32));
    result = prime * result + (int) (factionID ^ (factionID >>> 32));
    result = prime * result + ((factionName == null) ? 0 : factionName.hashCode());
    result = prime * result + freeRespecs;
    result = prime * result + freeSkillPoints;
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
  public boolean equals(Object obj) {
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
    if (bloodline == null) {
      if (other.bloodline != null) return false;
    } else if (!bloodline.equals(other.bloodline)) return false;
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
        + ", race=" + race + ", doB=" + doB + ", bloodline=" + bloodline + ", ancestry=" + ancestry + ", gender=" + gender + ", allianceName=" + allianceName
        + ", allianceID=" + allianceID + ", factionName=" + factionName + ", factionID=" + factionID + ", intelligence=" + intelligence + ", memory=" + memory
        + ", charisma=" + charisma + ", perception=" + perception + ", willpower=" + willpower + ", homeStationID=" + homeStationID + ", lastRespecDate="
        + lastRespecDate + ", lastTimedRespec=" + lastTimedRespec + ", freeRespecs=" + freeRespecs + ", freeSkillPoints=" + freeSkillPoints
        + ", remoteStationDate=" + remoteStationDate + "]";
  }

  public static CharacterSheet get(final SynchronizedEveAccount owner, final long time) {
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
}
