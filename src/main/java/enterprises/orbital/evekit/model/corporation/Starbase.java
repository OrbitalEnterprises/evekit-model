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
    name = "evekit_data_starbase",
    indexes = {
        @Index(
            name = "starbaseIDIndex",
            columnList = "starbaseID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Starbase.getByItemID",
        query = "SELECT c FROM Starbase c where c.owner = :owner and c.starbaseID = :sb and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Starbase extends CachedData {
  private static final Logger log = Logger.getLogger(Starbase.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST);

  private long starbaseID;
  private int typeID;
  private int systemID;
  private int moonID;
  private String state;
  private long unanchorAt = -1;
  private long reinforcedUntil = -1;
  private long onlinedSince = -1;
  private String fuelBayView;
  private String fuelBayTake;
  private String anchor;
  private String unanchor;
  private String online;
  private String offline;
  private boolean allowCorporationMembers;
  private boolean allowAllianceMembers;
  private boolean useAllianceStandings;
  private float attackStandingThreshold;
  private float attackSecurityStatusThreshold;
  private boolean attackIfOtherSecurityStatusDropping;
  private boolean attackIfAtWar;

  @Transient
  @ApiModelProperty(
      value = "unanchorAt Date")
  @JsonProperty("unanchorAtDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date unanchorAtDate;

  @Transient
  @ApiModelProperty(
      value = "reinforcedUntil Date")
  @JsonProperty("reinforcedUntilDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date reinforcedUntilDate;

  @Transient
  @ApiModelProperty(
      value = "onlinedSince Date")
  @JsonProperty("onlinedSinceDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date onlinedSinceDate;

  @SuppressWarnings("unused")
  protected Starbase() {}

  public Starbase(long starbaseID, int typeID, int systemID, int moonID, String state, long unanchorAt,
                  long reinforcedUntil, long onlinedSince, String fuelBayView, String fuelBayTake,
                  String anchor, String unanchor, String online, String offline, boolean allowCorporationMembers,
                  boolean allowAllianceMembers, boolean useAllianceStandings, float attackStandingThreshold,
                  float attackSecurityStatusThreshold, boolean attackIfOtherSecurityStatusDropping,
                  boolean attackIfAtWar) {
    this.starbaseID = starbaseID;
    this.typeID = typeID;
    this.systemID = systemID;
    this.moonID = moonID;
    this.state = state;
    this.unanchorAt = unanchorAt;
    this.reinforcedUntil = reinforcedUntil;
    this.onlinedSince = onlinedSince;
    this.fuelBayView = fuelBayView;
    this.fuelBayTake = fuelBayTake;
    this.anchor = anchor;
    this.unanchor = unanchor;
    this.online = online;
    this.offline = offline;
    this.allowCorporationMembers = allowCorporationMembers;
    this.allowAllianceMembers = allowAllianceMembers;
    this.useAllianceStandings = useAllianceStandings;
    this.attackStandingThreshold = attackStandingThreshold;
    this.attackSecurityStatusThreshold = attackSecurityStatusThreshold;
    this.attackIfOtherSecurityStatusDropping = attackIfOtherSecurityStatusDropping;
    this.attackIfAtWar = attackIfAtWar;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    unanchorAtDate = assignDateField(unanchorAt);
    reinforcedUntilDate = assignDateField(reinforcedUntil);
    onlinedSinceDate = assignDateField(onlinedSince);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof Starbase)) return false;
    Starbase other = (Starbase) sup;
    return starbaseID == other.starbaseID &&
        typeID == other.typeID &&
        systemID == other.systemID &&
        moonID == other.moonID &&
        nullSafeObjectCompare(state, other.state) &&
        unanchorAt == other.unanchorAt &&
        reinforcedUntil == other.reinforcedUntil &&
        onlinedSince == other.onlinedSince &&
        nullSafeObjectCompare(fuelBayView, other.fuelBayView) &&
        nullSafeObjectCompare(fuelBayTake, other.fuelBayTake) &&
        nullSafeObjectCompare(anchor, other.anchor) &&
        nullSafeObjectCompare(unanchor, other.unanchor) &&
        nullSafeObjectCompare(online, other.online) &&
        nullSafeObjectCompare(offline, other.offline) &&
        allowCorporationMembers == other.allowCorporationMembers &&
        allowAllianceMembers == other.allowAllianceMembers &&
        useAllianceStandings == other.useAllianceStandings &&
        floatCompare(attackStandingThreshold, other.attackStandingThreshold,0.00001F) &&
        floatCompare(attackSecurityStatusThreshold, other.attackSecurityStatusThreshold,0.00001F) &&
        attackIfOtherSecurityStatusDropping == other.attackIfOtherSecurityStatusDropping &&
        attackIfAtWar == other.attackIfAtWar;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getStarbaseID() {
    return starbaseID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getSystemID() {
    return systemID;
  }

  public int getMoonID() {
    return moonID;
  }

  public String getState() {
    return state;
  }

  public long getUnanchorAt() {
    return unanchorAt;
  }

  public long getReinforcedUntil() {
    return reinforcedUntil;
  }

  public long getOnlinedSince() {
    return onlinedSince;
  }

  public String getFuelBayView() {
    return fuelBayView;
  }

  public String getFuelBayTake() {
    return fuelBayTake;
  }

  public String getAnchor() {
    return anchor;
  }

  public String getUnanchor() {
    return unanchor;
  }

  public String getOnline() {
    return online;
  }

  public String getOffline() {
    return offline;
  }

  public boolean isAllowCorporationMembers() {
    return allowCorporationMembers;
  }

  public boolean isAllowAllianceMembers() {
    return allowAllianceMembers;
  }

  public boolean isUseAllianceStandings() {
    return useAllianceStandings;
  }

  public float getAttackStandingThreshold() {
    return attackStandingThreshold;
  }

  public float getAttackSecurityStatusThreshold() {
    return attackSecurityStatusThreshold;
  }

  public boolean isAttackIfOtherSecurityStatusDropping() {
    return attackIfOtherSecurityStatusDropping;
  }

  public boolean isAttackIfAtWar() {
    return attackIfAtWar;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Starbase starbase = (Starbase) o;
    return starbaseID == starbase.starbaseID &&
        typeID == starbase.typeID &&
        systemID == starbase.systemID &&
        moonID == starbase.moonID &&
        unanchorAt == starbase.unanchorAt &&
        reinforcedUntil == starbase.reinforcedUntil &&
        onlinedSince == starbase.onlinedSince &&
        allowCorporationMembers == starbase.allowCorporationMembers &&
        allowAllianceMembers == starbase.allowAllianceMembers &&
        useAllianceStandings == starbase.useAllianceStandings &&
        Float.compare(starbase.attackStandingThreshold, attackStandingThreshold) == 0 &&
        Float.compare(starbase.attackSecurityStatusThreshold, attackSecurityStatusThreshold) == 0 &&
        attackIfOtherSecurityStatusDropping == starbase.attackIfOtherSecurityStatusDropping &&
        attackIfAtWar == starbase.attackIfAtWar &&
        Objects.equals(state, starbase.state) &&
        Objects.equals(fuelBayView, starbase.fuelBayView) &&
        Objects.equals(fuelBayTake, starbase.fuelBayTake) &&
        Objects.equals(anchor, starbase.anchor) &&
        Objects.equals(unanchor, starbase.unanchor) &&
        Objects.equals(online, starbase.online) &&
        Objects.equals(offline, starbase.offline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil,
                        onlinedSince, fuelBayView, fuelBayTake, anchor, unanchor, online, offline,
                        allowCorporationMembers, allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                        attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar);
  }

  @Override
  public String toString() {
    return "Starbase{" +
        "starbaseID=" + starbaseID +
        ", typeID=" + typeID +
        ", systemID=" + systemID +
        ", moonID=" + moonID +
        ", state='" + state + '\'' +
        ", unanchorAt=" + unanchorAt +
        ", reinforcedUntil=" + reinforcedUntil +
        ", onlinedSince=" + onlinedSince +
        ", fuelBayView='" + fuelBayView + '\'' +
        ", fuelBayTake='" + fuelBayTake + '\'' +
        ", anchor='" + anchor + '\'' +
        ", unanchor='" + unanchor + '\'' +
        ", online='" + online + '\'' +
        ", offline='" + offline + '\'' +
        ", allowCorporationMembers=" + allowCorporationMembers +
        ", allowAllianceMembers=" + allowAllianceMembers +
        ", useAllianceStandings=" + useAllianceStandings +
        ", attackStandingThreshold=" + attackStandingThreshold +
        ", attackSecurityStatusThreshold=" + attackSecurityStatusThreshold +
        ", attackIfOtherSecurityStatusDropping=" + attackIfOtherSecurityStatusDropping +
        ", attackIfAtWar=" + attackIfAtWar +
        ", unanchorAtDate=" + unanchorAtDate +
        ", reinforcedUntilDate=" + reinforcedUntilDate +
        ", onlinedSinceDate=" + onlinedSinceDate +
        '}';
  }

  public static Starbase get(
      final SynchronizedEveAccount owner,
      final long time,
      final long starbaseID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Starbase> getter = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery(
                                                                                                   "Starbase.getByItemID",
                                                                                                   Starbase.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("sb", starbaseID);
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

  public static List<Starbase> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector starbaseID,
      final AttributeSelector typeID,
      final AttributeSelector systemID,
      final AttributeSelector moonID,
      final AttributeSelector state,
      final AttributeSelector unanchorAt,
      final AttributeSelector reinforcedUntil,
      final AttributeSelector onlinedSince,
      final AttributeSelector fuelBayView,
      final AttributeSelector fuelBayTake,
      final AttributeSelector anchor,
      final AttributeSelector unanchor,
      final AttributeSelector online,
      final AttributeSelector offline,
      final AttributeSelector allowCorporationMembers,
      final AttributeSelector allowAllianceMembers,
      final AttributeSelector useAllianceStandings,
      final AttributeSelector attackStandingThreshold,
      final AttributeSelector attackSecurityStatusThreshold,
      final AttributeSelector attackIfOtherSecurityStatusDropping,
      final AttributeSelector attackIfAtWar) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Starbase c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "starbaseID", starbaseID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "systemID", systemID);
                                        AttributeSelector.addIntSelector(qs, "c", "moonID", moonID);
                                        AttributeSelector.addStringSelector(qs, "c", "state", state, p);
                                        AttributeSelector.addLongSelector(qs, "c", "unanchorAt", unanchorAt);
                                        AttributeSelector.addLongSelector(qs, "c", "reinforcedUntil", reinforcedUntil);
                                        AttributeSelector.addLongSelector(qs, "c", "onlinedSince", onlinedSince);
                                        AttributeSelector.addStringSelector(qs, "c", "fuelBayView", fuelBayView, p);
                                        AttributeSelector.addStringSelector(qs, "c", "fuelBayTake", fuelBayTake, p);
                                        AttributeSelector.addStringSelector(qs, "c", "anchor", anchor, p);
                                        AttributeSelector.addStringSelector(qs, "c", "unanchor", unanchor, p);
                                        AttributeSelector.addStringSelector(qs, "c", "online", online, p);
                                        AttributeSelector.addStringSelector(qs, "c", "offline", offline, p);
                                        AttributeSelector.addBooleanSelector(qs, "c", "allowCorporationMembers",
                                                                             allowCorporationMembers);
                                        AttributeSelector.addBooleanSelector(qs, "c", "allowAllianceMembers",
                                                                             allowAllianceMembers);
                                        AttributeSelector.addBooleanSelector(qs, "c", "useAllianceStandings",
                                                                             useAllianceStandings);
                                        AttributeSelector.addFloatSelector(qs, "c", "attackStandingThreshold",
                                                                           attackStandingThreshold);
                                        AttributeSelector.addFloatSelector(qs, "c", "attackSecurityStatusThreshold",
                                                                           attackSecurityStatusThreshold);
                                        AttributeSelector.addBooleanSelector(qs, "c",
                                                                             "attackIfOtherSecurityStatusDropping",
                                                                             attackIfOtherSecurityStatusDropping);
                                        AttributeSelector.addBooleanSelector(qs, "c", "attackIfAtWar", attackIfAtWar);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Starbase> query = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(
                                                                                                  qs.toString(),
                                                                                                  Starbase.class);
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
