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

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_character_skill",
    indexes = {
        @Index(
            name = "typeIDIndex",
            columnList = "typeID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "CharacterSkill.getByTypeID",
        query = "SELECT c FROM CharacterSkill c where c.owner = :owner and c.typeID = :type and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CharacterSkill.getAll",
        query = "SELECT c FROM CharacterSkill c where c.owner = :owner and c.typeID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.typeID asc"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterSkill extends CachedData {
  private static final Logger log                 = Logger.getLogger(CharacterSkill.class.getName());
  private static final byte[] MASK                = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  private static final int    DEFAULT_MAX_RESULTS = 1000;
  private int                 typeID;
  private int                 level;
  private int                 skillpoints;
  private boolean             published;

  @SuppressWarnings("unused")
  private CharacterSkill() {}

  public CharacterSkill(int typeID, int level, int skillpoints, boolean published) {
    super();
    this.typeID = typeID;
    this.level = level;
    this.skillpoints = skillpoints;
    this.published = published;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CharacterSkill)) return false;
    CharacterSkill other = (CharacterSkill) sup;
    return typeID == other.typeID && level == other.level && skillpoints == other.skillpoints && published == other.published;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getLevel() {
    return level;
  }

  public int getSkillpoints() {
    return skillpoints;
  }

  public boolean isPublished() {
    return published;
  }

  @Override
  public String toString() {
    return "CharacterSkill [typeID=" + typeID + ", level=" + level + ", skillpoints=" + skillpoints + ", published=" + published + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + level;
    result = prime * result + (published ? 1231 : 1237);
    result = prime * result + skillpoints;
    result = prime * result + typeID;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterSkill other = (CharacterSkill) obj;
    if (level != other.level) return false;
    if (published != other.published) return false;
    if (skillpoints != other.skillpoints) return false;
    if (typeID != other.typeID) return false;
    return true;
  }

  public static CharacterSkill get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final int typeID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterSkill>() {
        @Override
        public CharacterSkill run() throws Exception {
          TypedQuery<CharacterSkill> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSkill.getByTypeID",
                                                                                                                         CharacterSkill.class);
          getter.setParameter("owner", owner);
          getter.setParameter("type", typeID);
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

  public static List<CharacterSkill> getAll(
                                            final SynchronizedEveAccount owner,
                                            final long time,
                                            int maxresults,
                                            final int contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(CharacterSkill.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterSkill>>() {
        @Override
        public List<CharacterSkill> run() throws Exception {
          TypedQuery<CharacterSkill> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSkill.getAll",
                                                                                                                         CharacterSkill.class);
          getter.setParameter("owner", owner);
          getter.setParameter("contid", contid);
          getter.setParameter("point", time);
          getter.setMaxResults(maxr);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<CharacterSkill> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector typeID,
                                                 final AttributeSelector level,
                                                 final AttributeSelector skillpoints,
                                                 final AttributeSelector published) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CharacterSkill>>() {
        @Override
        public List<CharacterSkill> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CharacterSkill c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addIntSelector(qs, "c", "level", level);
          AttributeSelector.addIntSelector(qs, "c", "skillpoints", skillpoints);
          AttributeSelector.addBooleanSelector(qs, "c", "published", published);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CharacterSkill> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CharacterSkill.class);
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
