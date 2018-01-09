package enterprises.orbital.evekit.model.corporation;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_corporation_sheet")
@NamedQueries({
    @NamedQuery(
        name = "CorporationSheet.get",
        query = "SELECT c FROM CorporationSheet c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class CorporationSheet extends CachedData {
  private static final Logger log  = Logger.getLogger(CorporationSheet.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET);
  private long                allianceID;
  private String              allianceName;
  private long                ceoID;
  private String              ceoName;
  private long                corporationID;
  private String              corporationName;
  @Lob
  @Column(
      length = 102400)
  private String              description;
  private int                 logoColor1;
  private int                 logoColor2;
  private int                 logoColor3;
  private int                 logoGraphicID;
  private int                 logoShape1;
  private int                 logoShape2;
  private int                 logoShape3;
  private int                 memberCount;
  private int                 memberLimit;
  private int                 shares;
  private long                stationID;
  private String              stationName;
  private double              taxRate;
  private String              ticker;
  private String              url;

  @SuppressWarnings("unused")
  protected CorporationSheet() {}

  public CorporationSheet(long allianceID, String allianceName, long ceoID, String ceoName, long corporationID, String corporationName, String description,
                          int logoColor1, int logoColor2, int logoColor3, int logoGraphicID, int logoShape1, int logoShape2, int logoShape3, int memberCount,
                          int memberLimit, int shares, long stationID, String stationName, double taxRate, String ticker, String url) {
    super();
    this.allianceID = allianceID;
    this.allianceName = allianceName;
    this.ceoID = ceoID;
    this.ceoName = ceoName;
    this.corporationID = corporationID;
    this.corporationName = corporationName;
    this.description = description;
    this.logoColor1 = logoColor1;
    this.logoColor2 = logoColor2;
    this.logoColor3 = logoColor3;
    this.logoGraphicID = logoGraphicID;
    this.logoShape1 = logoShape1;
    this.logoShape2 = logoShape2;
    this.logoShape3 = logoShape3;
    this.memberCount = memberCount;
    this.memberLimit = memberLimit;
    this.shares = shares;
    this.stationID = stationID;
    this.stationName = stationName;
    this.taxRate = taxRate;
    this.ticker = ticker;
    this.url = url;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    if (!(sup instanceof CorporationSheet)) return false;
    CorporationSheet other = (CorporationSheet) sup;
    return allianceID == other.allianceID && nullSafeObjectCompare(allianceName, other.allianceName) && ceoID == other.ceoID
        && nullSafeObjectCompare(ceoName, other.ceoName) && corporationID == other.corporationID
        && nullSafeObjectCompare(corporationName, other.corporationName) && nullSafeObjectCompare(description, other.description)
        && logoColor1 == other.logoColor1 && logoColor2 == other.logoColor2 && logoColor3 == other.logoColor3 && logoGraphicID == other.logoGraphicID
        && logoShape1 == other.logoShape1 && logoShape2 == other.logoShape2 && logoShape3 == other.logoShape3 && memberCount == other.memberCount
        && memberLimit == other.memberLimit && shares == other.shares && stationID == other.stationID && nullSafeObjectCompare(stationName, other.stationName)
        && taxRate == other.taxRate && nullSafeObjectCompare(ticker, other.ticker) && nullSafeObjectCompare(url, other.url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getAllianceID() {
    return allianceID;
  }

  public String getAllianceName() {
    return allianceName;
  }

  public long getCeoID() {
    return ceoID;
  }

  public String getCeoName() {
    return ceoName;
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

  public int getLogoColor1() {
    return logoColor1;
  }

  public int getLogoColor2() {
    return logoColor2;
  }

  public int getLogoColor3() {
    return logoColor3;
  }

  public int getLogoGraphicID() {
    return logoGraphicID;
  }

  public int getLogoShape1() {
    return logoShape1;
  }

  public int getLogoShape2() {
    return logoShape2;
  }

  public int getLogoShape3() {
    return logoShape3;
  }

  public int getMemberCount() {
    return memberCount;
  }

  public int getMemberLimit() {
    return memberLimit;
  }

  public int getShares() {
    return shares;
  }

  public long getStationID() {
    return stationID;
  }

  public String getStationName() {
    return stationName;
  }

  public double getTaxRate() {
    return taxRate;
  }

  public String getTicker() {
    return ticker;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceID ^ (allianceID >>> 32));
    result = prime * result + ((allianceName == null) ? 0 : allianceName.hashCode());
    result = prime * result + (int) (ceoID ^ (ceoID >>> 32));
    result = prime * result + ((ceoName == null) ? 0 : ceoName.hashCode());
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((corporationName == null) ? 0 : corporationName.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + logoColor1;
    result = prime * result + logoColor2;
    result = prime * result + logoColor3;
    result = prime * result + logoGraphicID;
    result = prime * result + logoShape1;
    result = prime * result + logoShape2;
    result = prime * result + logoShape3;
    result = prime * result + memberCount;
    result = prime * result + memberLimit;
    result = prime * result + shares;
    result = prime * result + (int) (stationID ^ (stationID >>> 32));
    result = prime * result + ((stationName == null) ? 0 : stationName.hashCode());
    long temp;
    temp = Double.doubleToLongBits(taxRate);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CorporationSheet other = (CorporationSheet) obj;
    if (allianceID != other.allianceID) return false;
    if (allianceName == null) {
      if (other.allianceName != null) return false;
    } else if (!allianceName.equals(other.allianceName)) return false;
    if (ceoID != other.ceoID) return false;
    if (ceoName == null) {
      if (other.ceoName != null) return false;
    } else if (!ceoName.equals(other.ceoName)) return false;
    if (corporationID != other.corporationID) return false;
    if (corporationName == null) {
      if (other.corporationName != null) return false;
    } else if (!corporationName.equals(other.corporationName)) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (logoColor1 != other.logoColor1) return false;
    if (logoColor2 != other.logoColor2) return false;
    if (logoColor3 != other.logoColor3) return false;
    if (logoGraphicID != other.logoGraphicID) return false;
    if (logoShape1 != other.logoShape1) return false;
    if (logoShape2 != other.logoShape2) return false;
    if (logoShape3 != other.logoShape3) return false;
    if (memberCount != other.memberCount) return false;
    if (memberLimit != other.memberLimit) return false;
    if (shares != other.shares) return false;
    if (stationID != other.stationID) return false;
    if (stationName == null) {
      if (other.stationName != null) return false;
    } else if (!stationName.equals(other.stationName)) return false;
    if (Double.doubleToLongBits(taxRate) != Double.doubleToLongBits(other.taxRate)) return false;
    if (ticker == null) {
      if (other.ticker != null) return false;
    } else if (!ticker.equals(other.ticker)) return false;
    if (url == null) {
      if (other.url != null) return false;
    } else if (!url.equals(other.url)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CorporationSheet [allianceID=" + allianceID + ", allianceName=" + allianceName + ", ceoID=" + ceoID + ", ceoName=" + ceoName + ", corporationID="
        + corporationID + ", corporationName=" + corporationName + ", description=" + description + ", logoColor1=" + logoColor1 + ", logoColor2=" + logoColor2
        + ", logoColor3=" + logoColor3 + ", logoGraphicID=" + logoGraphicID + ", logoShape1=" + logoShape1 + ", logoShape2=" + logoShape2 + ", logoShape3="
        + logoShape3 + ", memberCount=" + memberCount + ", memberLimit=" + memberLimit + ", shares=" + shares + ", stationID=" + stationID + ", stationName="
        + stationName + ", taxRate=" + taxRate + ", ticker=" + ticker + ", url=" + url + "]";
  }

  public static CorporationSheet get(
                                     final SynchronizedEveAccount owner,
                                     final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CorporationSheet>() {
        @Override
        public CorporationSheet run() throws Exception {
          TypedQuery<CorporationSheet> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CorporationSheet.get",
                                                                                                                           CorporationSheet.class);
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

  public static List<CorporationSheet> accessQuery(
                                                   final SynchronizedEveAccount owner,
                                                   final long contid,
                                                   final int maxresults,
                                                   final boolean reverse,
                                                   final AttributeSelector at,
                                                   final AttributeSelector allianceID,
                                                   final AttributeSelector allianceName,
                                                   final AttributeSelector ceoID,
                                                   final AttributeSelector ceoName,
                                                   final AttributeSelector corporationID,
                                                   final AttributeSelector corporationName,
                                                   final AttributeSelector description,
                                                   final AttributeSelector logoColor1,
                                                   final AttributeSelector logoColor2,
                                                   final AttributeSelector logoColor3,
                                                   final AttributeSelector logoGraphicID,
                                                   final AttributeSelector logoShape1,
                                                   final AttributeSelector logoShape2,
                                                   final AttributeSelector logoShape3,
                                                   final AttributeSelector memberCount,
                                                   final AttributeSelector memberLimit,
                                                   final AttributeSelector shares,
                                                   final AttributeSelector stationID,
                                                   final AttributeSelector stationName,
                                                   final AttributeSelector taxRate,
                                                   final AttributeSelector ticker,
                                                   final AttributeSelector url) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CorporationSheet>>() {
        @Override
        public List<CorporationSheet> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM CorporationSheet c WHERE ");
          // Constrain to specified owner
          qs.append("c.owner = :owner");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addLongSelector(qs, "c", "allianceID", allianceID);
          AttributeSelector.addStringSelector(qs, "c", "allianceName", allianceName, p);
          AttributeSelector.addLongSelector(qs, "c", "ceoID", ceoID);
          AttributeSelector.addStringSelector(qs, "c", "ceoName", ceoName, p);
          AttributeSelector.addLongSelector(qs, "c", "corporationID", corporationID);
          AttributeSelector.addStringSelector(qs, "c", "corporationName", corporationName, p);
          AttributeSelector.addStringSelector(qs, "c", "description", description, p);
          AttributeSelector.addIntSelector(qs, "c", "logoColor1", logoColor1);
          AttributeSelector.addIntSelector(qs, "c", "logoColor2", logoColor2);
          AttributeSelector.addIntSelector(qs, "c", "logoColor3", logoColor3);
          AttributeSelector.addIntSelector(qs, "c", "logoGraphicID", logoGraphicID);
          AttributeSelector.addIntSelector(qs, "c", "logoShape1", logoShape1);
          AttributeSelector.addIntSelector(qs, "c", "logoShape2", logoShape2);
          AttributeSelector.addIntSelector(qs, "c", "logoShape3", logoShape3);
          AttributeSelector.addIntSelector(qs, "c", "memberCount", memberCount);
          AttributeSelector.addIntSelector(qs, "c", "memberLimit", memberLimit);
          AttributeSelector.addIntSelector(qs, "c", "shares", shares);
          AttributeSelector.addLongSelector(qs, "c", "stationID", stationID);
          AttributeSelector.addStringSelector(qs, "c", "stationName", stationName, p);
          AttributeSelector.addDoubleSelector(qs, "c", "taxRate", taxRate);
          AttributeSelector.addStringSelector(qs, "c", "ticker", ticker, p);
          AttributeSelector.addStringSelector(qs, "c", "url", url, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<CorporationSheet> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), CorporationSheet.class);
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
