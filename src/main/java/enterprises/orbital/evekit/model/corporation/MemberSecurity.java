package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_member_security",
    indexes = {
        @Index(
            name = "characterIDIndex",
            columnList = "characterID",
            unique = false),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberSecurity.getByCharacterID",
        query = "SELECT c FROM MemberSecurity c where c.owner = :owner and c.characterID = :char and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "MemberSecurity.getAll",
        query = "SELECT c FROM MemberSecurity c where c.owner = :owner and c.characterID > :contid and c.lifeStart <= :point and c.lifeEnd > :point order by c.characterID asc"),
})
// 2 hour cache time - API caches for 1 hour
public class MemberSecurity extends CachedData {
  private static final Logger log                   = Logger.getLogger(MemberSecurity.class.getName());
  private static final byte[] MASK                  = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY);
  private static final int    DEFAULT_MAX_RESULTS   = 1000;
  private long                characterID;
  private String              name;
  // Role collections map role ID, title collection maps title ID
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           grantableRoles        = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           grantableRolesAtBase  = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           grantableRolesAtHQ    = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           grantableRolesAtOther = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           roles                 = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           rolesAtBase           = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           rolesAtHQ             = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           rolesAtOther          = new HashSet<Long>();
  @ElementCollection(
      fetch = FetchType.EAGER)
  private Set<Long>           titles                = new HashSet<Long>();

  @SuppressWarnings("unused")
  private MemberSecurity() {}

  public MemberSecurity(long characterID, String name) {
    super();
    this.characterID = characterID;
    this.name = name;
    this.grantableRoles = new HashSet<Long>();
    this.grantableRolesAtBase = new HashSet<Long>();
    this.grantableRolesAtHQ = new HashSet<Long>();
    this.grantableRolesAtOther = new HashSet<Long>();
    this.roles = new HashSet<Long>();
    this.rolesAtBase = new HashSet<Long>();
    this.rolesAtHQ = new HashSet<Long>();
    this.rolesAtOther = new HashSet<Long>();
    this.titles = new HashSet<Long>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof MemberSecurity)) return false;
    MemberSecurity other = (MemberSecurity) sup;
    return characterID == other.characterID && nullSafeObjectCompare(name, other.name) && nullSafeObjectCompare(grantableRoles, other.grantableRoles)
        && nullSafeObjectCompare(grantableRolesAtBase, other.grantableRolesAtBase) && nullSafeObjectCompare(grantableRolesAtHQ, other.grantableRolesAtHQ)
        && nullSafeObjectCompare(grantableRolesAtOther, other.grantableRolesAtOther) && nullSafeObjectCompare(roles, other.roles)
        && nullSafeObjectCompare(rolesAtBase, other.rolesAtBase) && nullSafeObjectCompare(rolesAtHQ, other.rolesAtHQ)
        && nullSafeObjectCompare(rolesAtOther, other.rolesAtOther) && nullSafeObjectCompare(titles, other.titles);
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

  public Set<Long> getGrantableRoles() {
    return grantableRoles;
  }

  public Set<Long> getGrantableRolesAtBase() {
    return grantableRolesAtBase;
  }

  public Set<Long> getGrantableRolesAtHQ() {
    return grantableRolesAtHQ;
  }

  public Set<Long> getGrantableRolesAtOther() {
    return grantableRolesAtOther;
  }

  public Set<Long> getRoles() {
    return roles;
  }

  public Set<Long> getRolesAtBase() {
    return rolesAtBase;
  }

  public Set<Long> getRolesAtHQ() {
    return rolesAtHQ;
  }

  public Set<Long> getRolesAtOther() {
    return rolesAtOther;
  }

  public Set<Long> getTitles() {
    return titles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + ((grantableRoles == null) ? 0 : grantableRoles.hashCode());
    result = prime * result + ((grantableRolesAtBase == null) ? 0 : grantableRolesAtBase.hashCode());
    result = prime * result + ((grantableRolesAtHQ == null) ? 0 : grantableRolesAtHQ.hashCode());
    result = prime * result + ((grantableRolesAtOther == null) ? 0 : grantableRolesAtOther.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((roles == null) ? 0 : roles.hashCode());
    result = prime * result + ((rolesAtBase == null) ? 0 : rolesAtBase.hashCode());
    result = prime * result + ((rolesAtHQ == null) ? 0 : rolesAtHQ.hashCode());
    result = prime * result + ((rolesAtOther == null) ? 0 : rolesAtOther.hashCode());
    result = prime * result + ((titles == null) ? 0 : titles.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MemberSecurity other = (MemberSecurity) obj;
    if (characterID != other.characterID) return false;
    if (grantableRoles == null) {
      if (other.grantableRoles != null) return false;
    } else if (!grantableRoles.equals(other.grantableRoles)) return false;
    if (grantableRolesAtBase == null) {
      if (other.grantableRolesAtBase != null) return false;
    } else if (!grantableRolesAtBase.equals(other.grantableRolesAtBase)) return false;
    if (grantableRolesAtHQ == null) {
      if (other.grantableRolesAtHQ != null) return false;
    } else if (!grantableRolesAtHQ.equals(other.grantableRolesAtHQ)) return false;
    if (grantableRolesAtOther == null) {
      if (other.grantableRolesAtOther != null) return false;
    } else if (!grantableRolesAtOther.equals(other.grantableRolesAtOther)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (roles == null) {
      if (other.roles != null) return false;
    } else if (!roles.equals(other.roles)) return false;
    if (rolesAtBase == null) {
      if (other.rolesAtBase != null) return false;
    } else if (!rolesAtBase.equals(other.rolesAtBase)) return false;
    if (rolesAtHQ == null) {
      if (other.rolesAtHQ != null) return false;
    } else if (!rolesAtHQ.equals(other.rolesAtHQ)) return false;
    if (rolesAtOther == null) {
      if (other.rolesAtOther != null) return false;
    } else if (!rolesAtOther.equals(other.rolesAtOther)) return false;
    if (titles == null) {
      if (other.titles != null) return false;
    } else if (!titles.equals(other.titles)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "MemberSecurity [characterID=" + characterID + ", name=" + name + ", grantableRoles=" + grantableRoles + ", grantableRolesAtBase="
        + grantableRolesAtBase + ", grantableRolesAtHQ=" + grantableRolesAtHQ + ", grantableRolesAtOther=" + grantableRolesAtOther + ", roles=" + roles
        + ", rolesAtBase=" + rolesAtBase + ", rolesAtHQ=" + rolesAtHQ + ", rolesAtOther=" + rolesAtOther + ", titles=" + titles + ", owner=" + owner
        + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static MemberSecurity get(
                                   final SynchronizedEveAccount owner,
                                   final long time,
                                   final long characterID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<MemberSecurity>() {
        @Override
        public MemberSecurity run() throws Exception {
          TypedQuery<MemberSecurity> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberSecurity.getByCharacterID",
                                                                                                                         MemberSecurity.class);
          getter.setParameter("owner", owner);
          getter.setParameter("char", characterID);
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

  public static List<MemberSecurity> getAll(
                                            final SynchronizedEveAccount owner,
                                            final long time,
                                            int maxresults,
                                            final long contid) {
    final int maxr = OrbitalProperties.getNonzeroLimited(maxresults, (int) PersistentProperty
        .getLongPropertyWithFallback(OrbitalProperties.getPropertyName(MemberSecurity.class, "maxresults"), DEFAULT_MAX_RESULTS));
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberSecurity>>() {
        @Override
        public List<MemberSecurity> run() throws Exception {
          TypedQuery<MemberSecurity> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("MemberSecurity.getAll",
                                                                                                                         MemberSecurity.class);
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

  public static List<MemberSecurity> accessQuery(
                                                 final SynchronizedEveAccount owner,
                                                 final long contid,
                                                 final int maxresults,
                                                 final boolean reverse,
                                                 final AttributeSelector at,
                                                 final AttributeSelector characterID,
                                                 final AttributeSelector name,
                                                 final AttributeSelector grantableRoles,
                                                 final AttributeSelector grantableRolesAtBase,
                                                 final AttributeSelector grantableRolesAtHQ,
                                                 final AttributeSelector grantableRolesAtOther,
                                                 final AttributeSelector roles,
                                                 final AttributeSelector rolesAtBase,
                                                 final AttributeSelector rolesAtHQ,
                                                 final AttributeSelector rolesAtOther,
                                                 final AttributeSelector titles) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<MemberSecurity>>() {
        @Override
        public List<MemberSecurity> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM MemberSecurity c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "characterID", characterID);
          AttributeSelector.addStringSelector(qs, "c", "name", name, p);
          AttributeSelector.addSetLongSelector(qs, "c", "grantableRoles", grantableRoles);
          AttributeSelector.addSetLongSelector(qs, "c", "grantableRolesAtBase", grantableRolesAtBase);
          AttributeSelector.addSetLongSelector(qs, "c", "grantableRolesAtHQ", grantableRolesAtHQ);
          AttributeSelector.addSetLongSelector(qs, "c", "grantableRolesAtOther", grantableRolesAtOther);
          AttributeSelector.addSetLongSelector(qs, "c", "roles", roles);
          AttributeSelector.addSetLongSelector(qs, "c", "rolesAtBase", rolesAtBase);
          AttributeSelector.addSetLongSelector(qs, "c", "rolesAtHQ", rolesAtHQ);
          AttributeSelector.addSetLongSelector(qs, "c", "rolesAtOther", rolesAtOther);
          AttributeSelector.addSetLongSelector(qs, "c", "titles", titles);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<MemberSecurity> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), MemberSecurity.class);
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
