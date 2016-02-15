package enterprises.orbital.evekit.model.corporation;

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

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_customs_office", indexes = {
    @Index(name = "itemIDIndex", columnList = "itemID", unique = false),
})
@NamedQueries({
    @NamedQuery(
        name = "CustomsOffice.getByItemID",
        query = "SELECT c FROM CustomsOffice c where c.owner = :owner and c.itemID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "CustomsOffice.getAll",
        query = "SELECT c FROM CustomsOffice c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
// 2 hour cache time - API caches for 1 hour
public class CustomsOffice extends CachedData {
  private static final Logger log  = Logger.getLogger(CustomsOffice.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);
  private long                itemID;
  private int                 solarSystemID;
  private String              solarSystemName;
  private int                 reinforceHour;
  private boolean             allowAlliance;
  private boolean             allowStandings;
  private double              standingLevel;
  private double              taxRateAlliance;
  private double              taxRateCorp;
  private double              taxRateStandingHigh;
  private double              taxRateStandingGood;
  private double              taxRateStandingNeutral;
  private double              taxRateStandingBad;
  private double              taxRateStandingHorrible;

  @SuppressWarnings("unused")
  private CustomsOffice() {}

  public CustomsOffice(long itemID, int solarSystemID, String solarSystemName, int reinforceHour, boolean allowAlliance, boolean allowStandings,
                       double standingLevel, double taxRateAlliance, double taxRateCorp, double taxRateStandingHigh, double taxRateStandingGood,
                       double taxRateStandingNeutral, double taxRateStandingBad, double taxRateStandingHorrible) {
    super();
    this.itemID = itemID;
    this.solarSystemID = solarSystemID;
    this.solarSystemName = solarSystemName;
    this.reinforceHour = reinforceHour;
    this.allowAlliance = allowAlliance;
    this.allowStandings = allowStandings;
    this.standingLevel = standingLevel;
    this.taxRateAlliance = taxRateAlliance;
    this.taxRateCorp = taxRateCorp;
    this.taxRateStandingHigh = taxRateStandingHigh;
    this.taxRateStandingGood = taxRateStandingGood;
    this.taxRateStandingNeutral = taxRateStandingNeutral;
    this.taxRateStandingBad = taxRateStandingBad;
    this.taxRateStandingHorrible = taxRateStandingHorrible;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof CustomsOffice)) return false;
    CustomsOffice other = (CustomsOffice) sup;
    return itemID == other.itemID && solarSystemID == other.solarSystemID && nullSafeObjectCompare(solarSystemName, other.solarSystemName)
        && reinforceHour == other.reinforceHour && allowAlliance == other.allowAlliance && allowStandings == other.allowStandings
        && standingLevel == other.standingLevel && taxRateAlliance == other.taxRateAlliance && taxRateCorp == other.taxRateCorp
        && taxRateStandingHigh == other.taxRateStandingHigh && taxRateStandingGood == other.taxRateStandingGood
        && taxRateStandingNeutral == other.taxRateStandingNeutral && taxRateStandingBad == other.taxRateStandingBad
        && taxRateStandingHorrible == other.taxRateStandingHorrible;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getItemID() {
    return itemID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public String getSolarSystemName() {
    return solarSystemName;
  }

  public int getReinforceHour() {
    return reinforceHour;
  }

  public boolean isAllowAlliance() {
    return allowAlliance;
  }

  public boolean isAllowStandings() {
    return allowStandings;
  }

  public double getStandingLevel() {
    return standingLevel;
  }

  public double getTaxRateAlliance() {
    return taxRateAlliance;
  }

  public double getTaxRateCorp() {
    return taxRateCorp;
  }

  public double getTaxRateStandingHigh() {
    return taxRateStandingHigh;
  }

  public double getTaxRateStandingGood() {
    return taxRateStandingGood;
  }

  public double getTaxRateStandingNeutral() {
    return taxRateStandingNeutral;
  }

  public double getTaxRateStandingBad() {
    return taxRateStandingBad;
  }

  public double getTaxRateStandingHorrible() {
    return taxRateStandingHorrible;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (allowAlliance ? 1231 : 1237);
    result = prime * result + (allowStandings ? 1231 : 1237);
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + reinforceHour;
    result = prime * result + solarSystemID;
    result = prime * result + ((solarSystemName == null) ? 0 : solarSystemName.hashCode());
    long temp;
    temp = Double.doubleToLongBits(standingLevel);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateAlliance);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateCorp);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateStandingBad);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateStandingGood);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateStandingHigh);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateStandingHorrible);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(taxRateStandingNeutral);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CustomsOffice other = (CustomsOffice) obj;
    if (allowAlliance != other.allowAlliance) return false;
    if (allowStandings != other.allowStandings) return false;
    if (itemID != other.itemID) return false;
    if (reinforceHour != other.reinforceHour) return false;
    if (solarSystemID != other.solarSystemID) return false;
    if (solarSystemName == null) {
      if (other.solarSystemName != null) return false;
    } else if (!solarSystemName.equals(other.solarSystemName)) return false;
    if (Double.doubleToLongBits(standingLevel) != Double.doubleToLongBits(other.standingLevel)) return false;
    if (Double.doubleToLongBits(taxRateAlliance) != Double.doubleToLongBits(other.taxRateAlliance)) return false;
    if (Double.doubleToLongBits(taxRateCorp) != Double.doubleToLongBits(other.taxRateCorp)) return false;
    if (Double.doubleToLongBits(taxRateStandingBad) != Double.doubleToLongBits(other.taxRateStandingBad)) return false;
    if (Double.doubleToLongBits(taxRateStandingGood) != Double.doubleToLongBits(other.taxRateStandingGood)) return false;
    if (Double.doubleToLongBits(taxRateStandingHigh) != Double.doubleToLongBits(other.taxRateStandingHigh)) return false;
    if (Double.doubleToLongBits(taxRateStandingHorrible) != Double.doubleToLongBits(other.taxRateStandingHorrible)) return false;
    if (Double.doubleToLongBits(taxRateStandingNeutral) != Double.doubleToLongBits(other.taxRateStandingNeutral)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CustomsOffice [itemID=" + itemID + ", solarSystemID=" + solarSystemID + ", solarSystemName=" + solarSystemName + ", reinforceHour=" + reinforceHour
        + ", allowAlliance=" + allowAlliance + ", allowStandings=" + allowStandings + ", standingLevel=" + standingLevel + ", taxRateAlliance="
        + taxRateAlliance + ", taxRateCorp=" + taxRateCorp + ", taxRateStandingHigh=" + taxRateStandingHigh + ", taxRateStandingGood=" + taxRateStandingGood
        + ", taxRateStandingNeutral=" + taxRateStandingNeutral + ", taxRateStandingBad=" + taxRateStandingBad + ", taxRateStandingHorrible="
        + taxRateStandingHorrible + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CustomsOffice get(final SynchronizedEveAccount owner, final long time, final long itemID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CustomsOffice>() {
        @Override
        public CustomsOffice run() throws Exception {
          TypedQuery<CustomsOffice> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CustomsOffice.getByItemID",
                                                                                                                        CustomsOffice.class);
          getter.setParameter("owner", owner);
          getter.setParameter("item", itemID);
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

  public static List<CustomsOffice> getAll(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<CustomsOffice>>() {
        @Override
        public List<CustomsOffice> run() throws Exception {
          TypedQuery<CustomsOffice> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CustomsOffice.getAll",
                                                                                                                        CustomsOffice.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
