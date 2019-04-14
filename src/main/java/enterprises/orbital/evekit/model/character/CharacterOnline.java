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
    name = "evekit_data_char_online"
)
@NamedQueries({
    @NamedQuery(
        name = "CharacterOnline.get",
        query = "SELECT c FROM CharacterOnline c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterOnline extends CachedData {
  private static final Logger log = Logger.getLogger(CharacterOnline.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_STATUS);
  private boolean online;
  private long lastLogin = -1;
  private long lastLogout = -1;
  private int logins;
  @Transient
  @ApiModelProperty(
      value = "lastLogin Date")
  @JsonProperty("lastLoginDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastLoginDate;
  @Transient
  @ApiModelProperty(
      value = "lastLogout Date")
  @JsonProperty("lastLogoutDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastLogoutDate;

  @SuppressWarnings("unused")
  protected CharacterOnline() {}

  public CharacterOnline(boolean online, long lastLogin, long lastLogout, int logins) {
    this.online = online;
    this.lastLogin = lastLogin;
    this.lastLogout = lastLogout;
    this.logins = logins;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    lastLoginDate = assignDateField(lastLogin);
    lastLogoutDate = assignDateField(lastLogout);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterOnline)) return false;
    CharacterOnline other = (CharacterOnline) sup;
    return online == other.online
        && lastLogin == other.lastLogin
        && lastLogout == other.lastLogout
        && logins == other.logins;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public boolean isOnline() {
    return online;
  }

  public long getLastLogin() {
    return lastLogin;
  }

  public long getLastLogout() {
    return lastLogout;
  }

  public int getLogins() {
    return logins;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterOnline that = (CharacterOnline) o;
    return online == that.online &&
        lastLogin == that.lastLogin &&
        lastLogout == that.lastLogout &&
        logins == that.logins;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), online, lastLogin, lastLogout, logins);
  }

  @Override
  public String toString() {
    return "CharacterOnline{" +
        "online=" + online +
        ", lastLogin=" + lastLogin +
        ", lastLogout=" + lastLogout +
        ", logins=" + logins +
        ", lastLoginDate=" + lastLoginDate +
        ", lastLogoutDate=" + lastLogoutDate +
        '}';
  }

  public static CharacterOnline get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterOnline> getter = EveKitUserAccountProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createNamedQuery(
                                                                                                          "CharacterOnline.get",
                                                                                                          CharacterOnline.class);
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

  public static List<CharacterOnline> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector online,
      final AttributeSelector lastLogin,
      final AttributeSelector lastLogout,
      final AttributeSelector logins) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterOnline c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addBooleanSelector(qs, "c", "online", online);
                                        AttributeSelector.addLongSelector(qs, "c", "lastLogin", lastLogin);
                                        AttributeSelector.addLongSelector(qs, "c", "lastLogout", lastLogout);
                                        AttributeSelector.addIntSelector(qs, "c", "logins", logins);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterOnline> query = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createQuery(
                                                                                                         qs.toString(),
                                                                                                         CharacterOnline.class);
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

  public static List<CharacterOnline> ekfQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final long timestamp) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterOnline c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addFirstLiveSelector(qs, "c", timestamp);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, false);
                                        // Return result
                                        TypedQuery<CharacterOnline> query = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createQuery(
                                                                                                         qs.toString(),
                                                                                                         CharacterOnline.class);
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
