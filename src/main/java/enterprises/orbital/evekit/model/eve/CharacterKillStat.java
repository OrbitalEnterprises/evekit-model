package enterprises.orbital.evekit.model.eve;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_eve_charkillstat")
@NamedQueries({
    @NamedQuery(
        name = "CharacterKillStat.get",
        query = "SELECT c FROM CharacterKillStat c WHERE c.characterID = :charid AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class CharacterKillStat extends RefCachedData {
  private static final Logger log = Logger.getLogger(CharacterKillStat.class.getName());
  private long                characterID;
  private String              characterName;
  private int                 kills;

  @SuppressWarnings("unused")
  private CharacterKillStat() {}

  public CharacterKillStat(long characterID, String characterName, int kills) {
    super();
    this.characterID = characterID;
    this.characterName = characterName;
    this.kills = kills;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof CharacterKillStat)) return false;
    CharacterKillStat other = (CharacterKillStat) sup;
    return characterID == other.characterID && nullSafeObjectCompare(characterName, other.characterName) && kills == other.kills;
  }

  public long getCharacterID() {
    return characterID;
  }

  public String getCharacterName() {
    return characterName;
  }

  public int getKills() {
    return kills;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + ((characterName == null) ? 0 : characterName.hashCode());
    result = prime * result + kills;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterKillStat other = (CharacterKillStat) obj;
    if (characterID != other.characterID) return false;
    if (characterName == null) {
      if (other.characterName != null) return false;
    } else if (!characterName.equals(other.characterName)) return false;
    if (kills != other.kills) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterKillStat [characterID=" + characterID + ", characterName=" + characterName + ", kills=" + kills + "]";
  }

  public static CharacterKillStat get(
                                      final long time,
                                      final long characterID) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<CharacterKillStat>() {
        @Override
        public CharacterKillStat run() throws Exception {
          TypedQuery<CharacterKillStat> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("CharacterKillStat.get",
                                                                                                                        CharacterKillStat.class);
          getter.setParameter("point", time);
          getter.setParameter("charid", characterID);
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

  public static List<CharacterKillStat> accessQuery(
                                                    final long contid,
                                                    final int maxresults,
                                                    final boolean reverse,
                                                    final AttributeSelector at,
                                                    final AttributeSelector characterID,
                                                    final AttributeSelector characterName,
                                                    final AttributeSelector kills) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterKillStat>>() {
        @Override
        public List<CharacterKillStat> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterKillStat c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "characterID", characterID);
          AttributeSelector.addStringSelector(qs, "c", "charactrName", characterName, p);
          AttributeSelector.addLongSelector(qs, "c", "kills", kills);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterKillStat> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterKillStat.class);
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
