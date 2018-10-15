package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_corporation_member_role_history",
    indexes = {
        @Index(
            name = "corporationMemberRoleHistoryCharacterIDIndex",
            columnList = "characterID"),
        @Index(
            name = "corporationMemberRoleHistoryChangeIndex",
            columnList = "changedAt"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MemberRoleHistory.get",
        query = "SELECT c FROM MemberRoleHistory c where c.owner = :owner and c.characterID = :character and c.changedAt = :changedat and c.issuerID = :issuer and c.roleType = :roletype and c.roleName = :rolename and c.old = :old and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MemberRoleHistory extends CachedData {
  private static final Logger log = Logger.getLogger(MemberRoleHistory.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MEMBER_SECURITY_LOG);

  private int characterID;
  private long changedAt;
  private int issuerID;
  private String roleType;
  private String roleName;
  private boolean old;

  @Transient
  @ApiModelProperty(
      value = "changedAt Date")
  @JsonProperty("changedAtDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date changedAtDate;

  @SuppressWarnings("unused")
  protected MemberRoleHistory() {}

  public MemberRoleHistory(int characterID, long changedAt, int issuerID, String roleType, String roleName,
                           boolean old) {
    this.characterID = characterID;
    this.changedAt = changedAt;
    this.issuerID = issuerID;
    this.roleType = roleType;
    this.roleName = roleName;
    this.old = old;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    changedAtDate = assignDateField(changedAt);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MemberRoleHistory)) return false;
    MemberRoleHistory other = (MemberRoleHistory) sup;
    return characterID == other.characterID &&
        changedAt == other.changedAt &&
        issuerID == other.issuerID &&
        nullSafeObjectCompare(roleType, other.roleType) &&
        nullSafeObjectCompare(roleName, other.roleName) &&
        old == other.old;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getCharacterID() {
    return characterID;
  }

  public long getChangedAt() {
    return changedAt;
  }

  public int getIssuerID() {
    return issuerID;
  }

  public String getRoleType() {
    return roleType;
  }

  public String getRoleName() {
    return roleName;
  }

  public boolean isOld() {
    return old;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MemberRoleHistory that = (MemberRoleHistory) o;
    return characterID == that.characterID &&
        changedAt == that.changedAt &&
        issuerID == that.issuerID &&
        old == that.old &&
        Objects.equals(roleType, that.roleType) &&
        Objects.equals(roleName, that.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characterID, changedAt, issuerID, roleType, roleName, old);
  }

  @Override
  public String toString() {
    return "MemberRoleHistory{" +
        "characterID=" + characterID +
        ", changedAt=" + changedAt +
        ", issuerID=" + issuerID +
        ", roleType='" + roleType + '\'' +
        ", roleName='" + roleName + '\'' +
        ", old=" + old +
        '}';
  }

  public static MemberRoleHistory get(
      final SynchronizedEveAccount owner,
      final long time,
      final int characterID,
      final long changedAt,
      final int issuerID,
      final String roleType,
      final String roleName,
      final boolean old) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MemberRoleHistory> getter = EveKitUserAccountProvider.getFactory()
                                                                                                        .getEntityManager()
                                                                                                        .createNamedQuery(
                                                                                                            "MemberRoleHistory.get",
                                                                                                            MemberRoleHistory.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("character", characterID);
                                        getter.setParameter("changedat", changedAt);
                                        getter.setParameter("issuer", issuerID);
                                        getter.setParameter("roletype", roleType);
                                        getter.setParameter("rolename", roleName);
                                        getter.setParameter("old", old);
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

  public static List<MemberRoleHistory> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector characterID,
      final AttributeSelector changedAt,
      final AttributeSelector issuerID,
      final AttributeSelector roleType,
      final AttributeSelector roleName,
      final AttributeSelector old) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MemberRoleHistory c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "characterID", characterID);
                                        AttributeSelector.addLongSelector(qs, "c", "changedAt", changedAt);
                                        AttributeSelector.addIntSelector(qs, "c", "issuerID", issuerID);
                                        AttributeSelector.addStringSelector(qs, "c", "roleType", roleType, p);
                                        AttributeSelector.addStringSelector(qs, "c", "roleName", roleName, p);
                                        AttributeSelector.addBooleanSelector(qs, "c", "old", old);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MemberRoleHistory> query = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createQuery(
                                                                                                           qs.toString(),
                                                                                                           MemberRoleHistory.class);
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
