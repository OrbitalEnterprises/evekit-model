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
    name = "evekit_data_corporation_sheet")
@NamedQueries({
    @NamedQuery(
        name = "CorporationSheet.get",
        query = "SELECT c FROM CorporationSheet c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CorporationSheet extends CachedData {
  private static final Logger log = Logger.getLogger(CorporationSheet.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET);

  private int allianceID;
  private int ceoID;
  private long corporationID;
  private String corporationName;
  @Lob
  @Column(
      length = 102400)
  private String description;
  private int memberCount;
  private long shares;
  private int stationID;
  private float taxRate;
  private String ticker;
  private String url;
  private long dateFounded;
  private int creatorID;
  private int factionID;
  private String px64x64;
  private String px128x128;
  private String px256x256;

  @Transient
  @ApiModelProperty(
      value = "dateFounded Date")
  @JsonProperty("dateFoundedDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateFoundedDate;


  @SuppressWarnings("unused")
  protected CorporationSheet() {}

  public CorporationSheet(int allianceID, int ceoID, long corporationID, String corporationName,
                          String description, int memberCount, long shares, int stationID, float taxRate,
                          String ticker, String url, long dateFounded, int creatorID, int factionID,
                          String px64x64, String px128x128, String px256x256) {
    this.allianceID = allianceID;
    this.ceoID = ceoID;
    this.corporationID = corporationID;
    this.corporationName = corporationName;
    this.description = description;
    this.memberCount = memberCount;
    this.shares = shares;
    this.stationID = stationID;
    this.taxRate = taxRate;
    this.ticker = ticker;
    this.url = url;
    this.dateFounded = dateFounded;
    this.creatorID = creatorID;
    this.factionID = factionID;
    this.px64x64 = px64x64;
    this.px128x128 = px128x128;
    this.px256x256 = px256x256;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    dateFoundedDate = assignDateField(dateFounded);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CorporationSheet)) return false;
    CorporationSheet other = (CorporationSheet) sup;
    return allianceID == other.allianceID && ceoID == other.ceoID
        && corporationID == other.corporationID
        && nullSafeObjectCompare(corporationName, other.corporationName) && nullSafeObjectCompare(description,
                                                                                                  other.description)
        && memberCount == other.memberCount && shares == other.shares && stationID == other.stationID
        && floatCompare(taxRate, other.taxRate, 0.00001F)
        && nullSafeObjectCompare(ticker, other.ticker)
        && nullSafeObjectCompare(url, other.url)
        && dateFounded == other.dateFounded
        && creatorID == other.creatorID
        && factionID == other.factionID
        && nullSafeObjectCompare(px64x64, other.px64x64)
        && nullSafeObjectCompare(px128x128, other.px128x128)
        && nullSafeObjectCompare(px256x256, other.px256x256);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getAllianceID() {
    return allianceID;
  }

  public int getCeoID() {
    return ceoID;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public String getCorporationName() {
    return corporationName;
  }

  public String getDescription() {
    return description;
  }

  public int getMemberCount() {
    return memberCount;
  }

  public long getShares() {
    return shares;
  }

  public int getStationID() {
    return stationID;
  }

  public float getTaxRate() {
    return taxRate;
  }

  public String getTicker() {
    return ticker;
  }

  public String getUrl() {
    return url;
  }

  public long getDateFounded() {
    return dateFounded;
  }

  public int getCreatorID() {
    return creatorID;
  }

  public int getFactionID() {
    return factionID;
  }

  public String getPx64x64() {
    return px64x64;
  }

  public String getPx128x128() {
    return px128x128;
  }

  public String getPx256x256() {
    return px256x256;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CorporationSheet that = (CorporationSheet) o;
    return allianceID == that.allianceID &&
        ceoID == that.ceoID &&
        corporationID == that.corporationID &&
        memberCount == that.memberCount &&
        shares == that.shares &&
        stationID == that.stationID &&
        Float.compare(that.taxRate, taxRate) == 0 &&
        dateFounded == that.dateFounded &&
        creatorID == that.creatorID &&
        factionID == that.factionID &&
        Objects.equals(corporationName, that.corporationName) &&
        Objects.equals(description, that.description) &&
        Objects.equals(ticker, that.ticker) &&
        Objects.equals(url, that.url) &&
        Objects.equals(px64x64, that.px64x64) &&
        Objects.equals(px128x128, that.px128x128) &&
        Objects.equals(px256x256, that.px256x256);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), allianceID, ceoID, corporationID, corporationName, description, memberCount,
                        shares, stationID, taxRate, ticker, url, dateFounded, creatorID, factionID, px64x64, px128x128,
                        px256x256);
  }

  @Override
  public String toString() {
    return "CorporationSheet{" +
        "allianceID=" + allianceID +
        ", ceoID=" + ceoID +
        ", corporationID=" + corporationID +
        ", corporationName='" + corporationName + '\'' +
        ", description='" + description + '\'' +
        ", memberCount=" + memberCount +
        ", shares=" + shares +
        ", stationID=" + stationID +
        ", taxRate=" + taxRate +
        ", ticker='" + ticker + '\'' +
        ", url='" + url + '\'' +
        ", dateFounded=" + dateFounded +
        ", creatorID=" + creatorID +
        ", factionID=" + factionID +
        ", px64x64='" + px64x64 + '\'' +
        ", px128x128='" + px128x128 + '\'' +
        ", px256x256='" + px256x256 + '\'' +
        ", dateFoundedDate=" + dateFoundedDate +
        '}';
  }

  public static CorporationSheet get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CorporationSheet> getter = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createNamedQuery(
                                                                                                           "CorporationSheet.get",
                                                                                                           CorporationSheet.class);
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

  public static List<CorporationSheet> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector allianceID,
      final AttributeSelector ceoID,
      final AttributeSelector corporationID,
      final AttributeSelector corporationName,
      final AttributeSelector description,
      final AttributeSelector memberCount,
      final AttributeSelector shares,
      final AttributeSelector stationID,
      final AttributeSelector taxRate,
      final AttributeSelector ticker,
      final AttributeSelector url,
      final AttributeSelector dateFounded,
      final AttributeSelector creatorID,
      final AttributeSelector factionID,
      final AttributeSelector px64x64,
      final AttributeSelector px128x128,
      final AttributeSelector px256x256) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CorporationSheet c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "allianceID", allianceID);
                                        AttributeSelector.addIntSelector(qs, "c", "ceoID", ceoID);
                                        AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
                                        AttributeSelector.addStringSelector(qs, "c", "corporationName", corporationName,
                                                                            p);
                                        AttributeSelector.addStringSelector(qs, "c", "description", description, p);
                                        AttributeSelector.addIntSelector(qs, "c", "memberCount", memberCount);
                                        AttributeSelector.addLongSelector(qs, "c", "shares", shares);
                                        AttributeSelector.addIntSelector(qs, "c", "stationID", stationID);
                                        AttributeSelector.addFloatSelector(qs, "c", "taxRate", taxRate);
                                        AttributeSelector.addStringSelector(qs, "c", "ticker", ticker, p);
                                        AttributeSelector.addStringSelector(qs, "c", "url", url, p);
                                        AttributeSelector.addLongSelector(qs, "c", "dateFounded", dateFounded);
                                        AttributeSelector.addIntSelector(qs, "c", "creatorID", creatorID);
                                        AttributeSelector.addIntSelector(qs, "c", "factionID", factionID);
                                        AttributeSelector.addStringSelector(qs, "c", "px64x64", px64x64, p);
                                        AttributeSelector.addStringSelector(qs, "c", "px128x128", px128x128, p);
                                        AttributeSelector.addStringSelector(qs, "c", "px256x256", px256x256, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CorporationSheet> query = EveKitUserAccountProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createQuery(
                                                                                                          qs.toString(),
                                                                                                          CorporationSheet.class);
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
