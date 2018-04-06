package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_customs_office",
    indexes = {
        @Index(
            name = "officeIDIndex",
            columnList = "officeID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "CustomsOffice.getByItemID",
        query = "SELECT c FROM CustomsOffice c where c.owner = :owner and c.officeID = :item and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CustomsOffice extends CachedData {
  private static final Logger log = Logger.getLogger(CustomsOffice.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ASSETS);

  private long officeID;
  private int solarSystemID;
  private int reinforceExitStart;
  private int reinforceExitEnd;
  private boolean allowAlliance;
  private boolean allowStandings;
  private String standingLevel;
  private float taxRateAlliance;
  private float taxRateCorp;
  private float taxRateStandingExcellent;
  private float taxRateStandingGood;
  private float taxRateStandingNeutral;
  private float taxRateStandingBad;
  private float taxRateStandingTerrible;

  @SuppressWarnings("unused")
  protected CustomsOffice() {}

  public CustomsOffice(long officeID, int solarSystemID, int reinforceExitStart, int reinforceExitEnd,
                       boolean allowAlliance, boolean allowStandings, String standingLevel, float taxRateAlliance,
                       float taxRateCorp, float taxRateStandingExcellent, float taxRateStandingGood,
                       float taxRateStandingNeutral, float taxRateStandingBad, float taxRateStandingTerrible) {
    this.officeID = officeID;
    this.solarSystemID = solarSystemID;
    this.reinforceExitStart = reinforceExitStart;
    this.reinforceExitEnd = reinforceExitEnd;
    this.allowAlliance = allowAlliance;
    this.allowStandings = allowStandings;
    this.standingLevel = standingLevel;
    this.taxRateAlliance = taxRateAlliance;
    this.taxRateCorp = taxRateCorp;
    this.taxRateStandingExcellent = taxRateStandingExcellent;
    this.taxRateStandingGood = taxRateStandingGood;
    this.taxRateStandingNeutral = taxRateStandingNeutral;
    this.taxRateStandingBad = taxRateStandingBad;
    this.taxRateStandingTerrible = taxRateStandingTerrible;
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
    if (!(sup instanceof CustomsOffice)) return false;
    CustomsOffice other = (CustomsOffice) sup;
    return officeID == other.officeID
        && solarSystemID == other.solarSystemID
        && reinforceExitStart == other.reinforceExitStart
        && reinforceExitEnd == other.reinforceExitEnd
        && allowAlliance == other.allowAlliance
        && allowStandings == other.allowStandings
        && nullSafeObjectCompare(standingLevel, other.standingLevel)
        && Float.compare(taxRateAlliance, other.taxRateAlliance) == 0
        && Float.compare(taxRateCorp, other.taxRateCorp) == 0
        && Float.compare(taxRateStandingExcellent, other.taxRateStandingExcellent) == 0
        && Float.compare(taxRateStandingGood, other.taxRateStandingGood) == 0
        && Float.compare(taxRateStandingNeutral, other.taxRateStandingNeutral) == 0
        && Float.compare(taxRateStandingBad, other.taxRateStandingBad) == 0
        && Float.compare(taxRateStandingTerrible, other.taxRateStandingTerrible) == 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getOfficeID() {
    return officeID;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public int getReinforceExitStart() {
    return reinforceExitStart;
  }

  public int getReinforceExitEnd() {
    return reinforceExitEnd;
  }

  public boolean isAllowAlliance() {
    return allowAlliance;
  }

  public boolean isAllowStandings() {
    return allowStandings;
  }

  public String getStandingLevel() {
    return standingLevel;
  }

  public float getTaxRateAlliance() {
    return taxRateAlliance;
  }

  public float getTaxRateCorp() {
    return taxRateCorp;
  }

  public float getTaxRateStandingExcellent() {
    return taxRateStandingExcellent;
  }

  public float getTaxRateStandingGood() {
    return taxRateStandingGood;
  }

  public float getTaxRateStandingNeutral() {
    return taxRateStandingNeutral;
  }

  public float getTaxRateStandingBad() {
    return taxRateStandingBad;
  }

  public float getTaxRateStandingTerrible() {
    return taxRateStandingTerrible;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CustomsOffice that = (CustomsOffice) o;
    return officeID == that.officeID &&
        solarSystemID == that.solarSystemID &&
        reinforceExitStart == that.reinforceExitStart &&
        reinforceExitEnd == that.reinforceExitEnd &&
        allowAlliance == that.allowAlliance &&
        allowStandings == that.allowStandings &&
        Float.compare(that.taxRateAlliance, taxRateAlliance) == 0 &&
        Float.compare(that.taxRateCorp, taxRateCorp) == 0 &&
        Float.compare(that.taxRateStandingExcellent, taxRateStandingExcellent) == 0 &&
        Float.compare(that.taxRateStandingGood, taxRateStandingGood) == 0 &&
        Float.compare(that.taxRateStandingNeutral, taxRateStandingNeutral) == 0 &&
        Float.compare(that.taxRateStandingBad, taxRateStandingBad) == 0 &&
        Float.compare(that.taxRateStandingTerrible, taxRateStandingTerrible) == 0 &&
        Objects.equals(standingLevel, that.standingLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), officeID, solarSystemID, reinforceExitStart, reinforceExitEnd, allowAlliance,
                        allowStandings, standingLevel, taxRateAlliance, taxRateCorp, taxRateStandingExcellent,
                        taxRateStandingGood, taxRateStandingNeutral, taxRateStandingBad, taxRateStandingTerrible);
  }

  @Override
  public String toString() {
    return "CustomsOffice{" +
        "officeID=" + officeID +
        ", solarSystemID=" + solarSystemID +
        ", reinforceExitStart=" + reinforceExitStart +
        ", reinforceExitEnd=" + reinforceExitEnd +
        ", allowAlliance=" + allowAlliance +
        ", allowStandings=" + allowStandings +
        ", standingLevel='" + standingLevel + '\'' +
        ", taxRateAlliance=" + taxRateAlliance +
        ", taxRateCorp=" + taxRateCorp +
        ", taxRateStandingExcellent=" + taxRateStandingExcellent +
        ", taxRateStandingGood=" + taxRateStandingGood +
        ", taxRateStandingNeutral=" + taxRateStandingNeutral +
        ", taxRateStandingBad=" + taxRateStandingBad +
        ", taxRateStandingTerrible=" + taxRateStandingTerrible +
        '}';
  }

  public static CustomsOffice get(
      final SynchronizedEveAccount owner,
      final long time,
      final long officeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CustomsOffice> getter = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createNamedQuery(
                                                                                                        "CustomsOffice.getByItemID",
                                                                                                        CustomsOffice.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("item", officeID);
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

  public static List<CustomsOffice> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector officeID,
      final AttributeSelector solarSystemID,
      final AttributeSelector reinforceExitStart,
      final AttributeSelector reinforceExitEnd,
      final AttributeSelector allowAlliance,
      final AttributeSelector allowStandings,
      final AttributeSelector standingLevel,
      final AttributeSelector taxRateAlliance,
      final AttributeSelector taxRateCorp,
      final AttributeSelector taxRateStandingExcellent,
      final AttributeSelector taxRateStandingGood,
      final AttributeSelector taxRateStandingNeutral,
      final AttributeSelector taxRateStandingBad,
      final AttributeSelector taxRateStandingTerrible) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(new RunInTransaction<List<CustomsOffice>>() {
                                        @Override
                                        public List<CustomsOffice> run() throws Exception {
                                          StringBuilder qs = new StringBuilder();
                                          qs.append("SELECT c FROM CustomsOffice c WHERE ");
                                          // Constrain to specified owner
                                          qs.append("c.owner = :owner");
                                          // Constrain lifeline
                                          AttributeSelector.addLifelineSelector(qs, "c", at);
                                          // Constrain attributes
                                          AttributeParameters p = new AttributeParameters("att");
                                          AttributeSelector.addLongSelector(qs, "c", "officeID", officeID);
                                          AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                          AttributeSelector.addIntSelector(qs, "c", "reinforceExitStart",
                                                                           reinforceExitStart);
                                          AttributeSelector.addIntSelector(qs, "c", "reinforceExitEnd",
                                                                           reinforceExitEnd);
                                          AttributeSelector.addBooleanSelector(qs, "c", "allowAlliance", allowAlliance);
                                          AttributeSelector.addBooleanSelector(qs, "c", "allowStandings",
                                                                               allowStandings);
                                          AttributeSelector.addStringSelector(qs, "c", "standingLevel", standingLevel,
                                                                              p);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateAlliance",
                                                                             taxRateAlliance);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateCorp", taxRateCorp);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateStandingExcellent",
                                                                             taxRateStandingExcellent);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateStandingGood",
                                                                             taxRateStandingGood);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateStandingNeutral",
                                                                             taxRateStandingNeutral);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateStandingBad",
                                                                             taxRateStandingBad);
                                          AttributeSelector.addFloatSelector(qs, "c", "taxRateStandingTerrible",
                                                                             taxRateStandingTerrible);
                                          // Set CID constraint and ordering
                                          setCIDOrdering(qs, contid, reverse);
                                          // Return result
                                          TypedQuery<CustomsOffice> query = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createQuery(
                                                                                                         qs.toString(),
                                                                                                         CustomsOffice.class);
                                          query.setParameter("owner", owner);
                                          p.fillParams(query);
                                          query.setMaxResults(maxresults);
                                          return query.getResultList();
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
