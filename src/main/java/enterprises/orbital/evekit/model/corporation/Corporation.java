package enterprises.orbital.evekit.model.corporation;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(
    name = "evekit_data_corporation")
@NamedQueries({
    @NamedQuery(
        name = "Corporation.get",
        query = "SELECT c FROM Corporation c where c.owner = :owner"),
})
public class Corporation extends CachedData {
  private static final Logger log                        = Logger.getLogger(Corporation.class.getName());

  private String              corporationName;
  private long                corporationID;

  // Request expiry data
  private long                accountBalanceExpiry       = -1;
  private long                assetListExpiry            = -1;
  private long                contactListExpiry          = -1;
  private long                containerLogExpiry         = -1;
  private long                corporationSheetExpiry     = -1;
  private long                blueprintsExpiry           = -1;
  private long                bookmarksExpiry            = -1;
  private long                contractsExpiry            = -1;
  private long                contractItemsExpiry        = -1;
  private long                contractBidsExpiry         = -1;
  private long                customsOfficeExpiry        = -1;
  private long                facilitiesExpiry           = -1;
  private long                facWarStatsExpiry          = -1;
  private long                industryJobsExpiry         = -1;
  private long                industryJobsHistoryExpiry  = -1;
  private long                killlogExpiry              = -1;
  private long                locationsExpiry            = -1;
  private long                marketOrdersExpiry         = -1;
  private long                medalsExpiry               = -1;
  private long                memberMedalsExpiry         = -1;
  private long                memberSecurityExpiry       = -1;
  private long                memberSecurityLogExpiry    = -1;
  private long                memberTrackingExpiry       = -1;
  private long                outpostListExpiry          = -1;
  private long                outpostServiceDetailExpiry = -1;
  private long                shareholdersExpiry         = -1;
  private long                standingsExpiry            = -1;
  private long                starbaseDetailExpiry       = -1;
  private long                starbaseListExpiry         = -1;
  private long                titlesExpiry               = -1;
  private long                walletJournalExpiry        = -1;
  private long                walletTransactionsExpiry   = -1;

  public String getCorporationName() {
    return corporationName;
  }

  public void setCorporationName(
                                 String corporationName) {
    this.corporationName = corporationName;
  }

  public long getCorporationID() {
    return corporationID;
  }

  public void setCorporationID(
                               long corporationID) {
    this.corporationID = corporationID;
  }

  public long getAccountBalanceExpiry() {
    return accountBalanceExpiry;
  }

  public void setAccountBalanceExpiry(
                                      long accountBalanceExpiry) {
    this.accountBalanceExpiry = accountBalanceExpiry;
  }

  public long getAssetListExpiry() {
    return assetListExpiry;
  }

  public void setAssetListExpiry(
                                 long assetListExpiry) {
    this.assetListExpiry = assetListExpiry;
  }

  public long getContactListExpiry() {
    return contactListExpiry;
  }

  public void setContactListExpiry(
                                   long contactListExpiry) {
    this.contactListExpiry = contactListExpiry;
  }

  public long getContainerLogExpiry() {
    return containerLogExpiry;
  }

  public void setContainerLogExpiry(
                                    long containerLogExpiry) {
    this.containerLogExpiry = containerLogExpiry;
  }

  public long getCorporationSheetExpiry() {
    return corporationSheetExpiry;
  }

  public void setCorporationSheetExpiry(
                                        long corporationSheetExpiry) {
    this.corporationSheetExpiry = corporationSheetExpiry;
  }

  public long getCustomsOfficeExpiry() {
    return customsOfficeExpiry;
  }

  public void setCustomsOfficeExpiry(
                                     long customsOfficeExpiry) {
    this.customsOfficeExpiry = customsOfficeExpiry;
  }

  public long getBlueprintsExpiry() {
    return blueprintsExpiry;
  }

  public void setBlueprintsExpiry(
                                  long blueprintsExpiry) {
    this.blueprintsExpiry = blueprintsExpiry;
  }

  public long getBookmarksExpiry() {
    return bookmarksExpiry;
  }

  public void setBookmarksExpiry(
                                 long bookmarksExpiry) {
    this.bookmarksExpiry = bookmarksExpiry;
  }

  public long getContractsExpiry() {
    return contractsExpiry;
  }

  public void setContractsExpiry(
                                 long contractsExpiry) {
    this.contractsExpiry = contractsExpiry;
  }

  public long getContractItemsExpiry() {
    return contractItemsExpiry;
  }

  public void setContractItemsExpiry(
                                     long contractItemsExpiry) {
    this.contractItemsExpiry = contractItemsExpiry;
  }

  public long getContractBidsExpiry() {
    return contractBidsExpiry;
  }

  public void setContractBidsExpiry(
                                    long contractBidsExpiry) {
    this.contractBidsExpiry = contractBidsExpiry;
  }

  public long getFacilitiesExpiry() {
    return facilitiesExpiry;
  }

  public void setFacilitiesExpiry(
                                  long facilitiesExpiry) {
    this.facilitiesExpiry = facilitiesExpiry;
  }

  public long getFacWarStatsExpiry() {
    return facWarStatsExpiry;
  }

  public void setFacWarStatsExpiry(
                                   long facWarStatsExpiry) {
    this.facWarStatsExpiry = facWarStatsExpiry;
  }

  public long getIndustryJobsExpiry() {
    return industryJobsExpiry;
  }

  public void setIndustryJobsExpiry(
                                    long industryJobsExpiry) {
    this.industryJobsExpiry = industryJobsExpiry;
  }

  public long getIndustryJobsHistoryExpiry() {
    return industryJobsHistoryExpiry;
  }

  public void setIndustryJobsHistoryExpiry(
                                           long industryJobsHistoryExpiry) {
    this.industryJobsHistoryExpiry = industryJobsHistoryExpiry;
  }

  public long getKilllogExpiry() {
    return killlogExpiry;
  }

  public void setKilllogExpiry(
                               long killlogExpiry) {
    this.killlogExpiry = killlogExpiry;
  }

  public long getLocationsExpiry() {
    return locationsExpiry;
  }

  public void setLocationsExpiry(
                                 long locationsExpiry) {
    this.locationsExpiry = locationsExpiry;
  }

  public long getMarketOrdersExpiry() {
    return marketOrdersExpiry;
  }

  public void setMarketOrdersExpiry(
                                    long marketOrdersExpiry) {
    this.marketOrdersExpiry = marketOrdersExpiry;
  }

  public long getMedalsExpiry() {
    return medalsExpiry;
  }

  public void setMedalsExpiry(
                              long medalsExpiry) {
    this.medalsExpiry = medalsExpiry;
  }

  public long getMemberMedalsExpiry() {
    return memberMedalsExpiry;
  }

  public void setMemberMedalsExpiry(
                                    long memberMedalsExpiry) {
    this.memberMedalsExpiry = memberMedalsExpiry;
  }

  public long getMemberSecurityExpiry() {
    return memberSecurityExpiry;
  }

  public void setMemberSecurityExpiry(
                                      long memberSecurityExpiry) {
    this.memberSecurityExpiry = memberSecurityExpiry;
  }

  public long getMemberSecurityLogExpiry() {
    return memberSecurityLogExpiry;
  }

  public void setMemberSecurityLogExpiry(
                                         long memberSecurityLogExpiry) {
    this.memberSecurityLogExpiry = memberSecurityLogExpiry;
  }

  public long getMemberTrackingExpiry() {
    return memberTrackingExpiry;
  }

  public void setMemberTrackingExpiry(
                                      long memberTrackingExpiry) {
    this.memberTrackingExpiry = memberTrackingExpiry;
  }

  public long getOutpostListExpiry() {
    return outpostListExpiry;
  }

  public void setOutpostListExpiry(
                                   long outpostListExpiry) {
    this.outpostListExpiry = outpostListExpiry;
  }

  public long getOutpostServiceDetailExpiry() {
    return outpostServiceDetailExpiry;
  }

  public void setOutpostServiceDetailExpiry(
                                            long outpostServiceDetailExpiry) {
    this.outpostServiceDetailExpiry = outpostServiceDetailExpiry;
  }

  public long getShareholdersExpiry() {
    return shareholdersExpiry;
  }

  public void setShareholdersExpiry(
                                    long shareholdersExpiry) {
    this.shareholdersExpiry = shareholdersExpiry;
  }

  public long getStandingsExpiry() {
    return standingsExpiry;
  }

  public void setStandingsExpiry(
                                 long standingsExpiry) {
    this.standingsExpiry = standingsExpiry;
  }

  public long getStarbaseDetailExpiry() {
    return starbaseDetailExpiry;
  }

  public void setStarbaseDetailExpiry(
                                      long starbaseDetailExpiry) {
    this.starbaseDetailExpiry = starbaseDetailExpiry;
  }

  public long getStarbaseListExpiry() {
    return starbaseListExpiry;
  }

  public void setStarbaseListExpiry(
                                    long starbaseListExpiry) {
    this.starbaseListExpiry = starbaseListExpiry;
  }

  public long getTitlesExpiry() {
    return titlesExpiry;
  }

  public void setTitlesExpiry(
                              long titlesExpiry) {
    this.titlesExpiry = titlesExpiry;
  }

  public long getWalletJournalExpiry() {
    return walletJournalExpiry;
  }

  public void setWalletJournalExpiry(
                                     long walletJournalExpiry) {
    this.walletJournalExpiry = walletJournalExpiry;
  }

  public long getWalletTransactionsExpiry() {
    return walletTransactionsExpiry;
  }

  public void setWalletTransactionsExpiry(
                                          long walletTransactionsExpiry) {
    this.walletTransactionsExpiry = walletTransactionsExpiry;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (accountBalanceExpiry ^ (accountBalanceExpiry >>> 32));
    result = prime * result + (int) (assetListExpiry ^ (assetListExpiry >>> 32));
    result = prime * result + (int) (blueprintsExpiry ^ (blueprintsExpiry >>> 32));
    result = prime * result + (int) (bookmarksExpiry ^ (bookmarksExpiry >>> 32));
    result = prime * result + (int) (contactListExpiry ^ (contactListExpiry >>> 32));
    result = prime * result + (int) (containerLogExpiry ^ (containerLogExpiry >>> 32));
    result = prime * result + (int) (contractBidsExpiry ^ (contractBidsExpiry >>> 32));
    result = prime * result + (int) (contractItemsExpiry ^ (contractItemsExpiry >>> 32));
    result = prime * result + (int) (contractsExpiry ^ (contractsExpiry >>> 32));
    result = prime * result + (int) (corporationID ^ (corporationID >>> 32));
    result = prime * result + ((corporationName == null) ? 0 : corporationName.hashCode());
    result = prime * result + (int) (corporationSheetExpiry ^ (corporationSheetExpiry >>> 32));
    result = prime * result + (int) (customsOfficeExpiry ^ (customsOfficeExpiry >>> 32));
    result = prime * result + (int) (facWarStatsExpiry ^ (facWarStatsExpiry >>> 32));
    result = prime * result + (int) (facilitiesExpiry ^ (facilitiesExpiry >>> 32));
    result = prime * result + (int) (industryJobsExpiry ^ (industryJobsExpiry >>> 32));
    result = prime * result + (int) (industryJobsHistoryExpiry ^ (industryJobsHistoryExpiry >>> 32));
    result = prime * result + (int) (killlogExpiry ^ (killlogExpiry >>> 32));
    result = prime * result + (int) (locationsExpiry ^ (locationsExpiry >>> 32));
    result = prime * result + (int) (marketOrdersExpiry ^ (marketOrdersExpiry >>> 32));
    result = prime * result + (int) (medalsExpiry ^ (medalsExpiry >>> 32));
    result = prime * result + (int) (memberMedalsExpiry ^ (memberMedalsExpiry >>> 32));
    result = prime * result + (int) (memberSecurityExpiry ^ (memberSecurityExpiry >>> 32));
    result = prime * result + (int) (memberSecurityLogExpiry ^ (memberSecurityLogExpiry >>> 32));
    result = prime * result + (int) (memberTrackingExpiry ^ (memberTrackingExpiry >>> 32));
    result = prime * result + (int) (outpostListExpiry ^ (outpostListExpiry >>> 32));
    result = prime * result + (int) (outpostServiceDetailExpiry ^ (outpostServiceDetailExpiry >>> 32));
    result = prime * result + (int) (shareholdersExpiry ^ (shareholdersExpiry >>> 32));
    result = prime * result + (int) (standingsExpiry ^ (standingsExpiry >>> 32));
    result = prime * result + (int) (starbaseDetailExpiry ^ (starbaseDetailExpiry >>> 32));
    result = prime * result + (int) (starbaseListExpiry ^ (starbaseListExpiry >>> 32));
    result = prime * result + (int) (titlesExpiry ^ (titlesExpiry >>> 32));
    result = prime * result + (int) (walletJournalExpiry ^ (walletJournalExpiry >>> 32));
    result = prime * result + (int) (walletTransactionsExpiry ^ (walletTransactionsExpiry >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Corporation other = (Corporation) obj;
    if (accountBalanceExpiry != other.accountBalanceExpiry) return false;
    if (assetListExpiry != other.assetListExpiry) return false;
    if (blueprintsExpiry != other.blueprintsExpiry) return false;
    if (bookmarksExpiry != other.bookmarksExpiry) return false;
    if (contactListExpiry != other.contactListExpiry) return false;
    if (containerLogExpiry != other.containerLogExpiry) return false;
    if (contractBidsExpiry != other.contractBidsExpiry) return false;
    if (contractItemsExpiry != other.contractItemsExpiry) return false;
    if (contractsExpiry != other.contractsExpiry) return false;
    if (corporationID != other.corporationID) return false;
    if (corporationName == null) {
      if (other.corporationName != null) return false;
    } else if (!corporationName.equals(other.corporationName)) return false;
    if (corporationSheetExpiry != other.corporationSheetExpiry) return false;
    if (customsOfficeExpiry != other.customsOfficeExpiry) return false;
    if (facWarStatsExpiry != other.facWarStatsExpiry) return false;
    if (facilitiesExpiry != other.facilitiesExpiry) return false;
    if (industryJobsExpiry != other.industryJobsExpiry) return false;
    if (industryJobsHistoryExpiry != other.industryJobsHistoryExpiry) return false;
    if (killlogExpiry != other.killlogExpiry) return false;
    if (locationsExpiry != other.locationsExpiry) return false;
    if (marketOrdersExpiry != other.marketOrdersExpiry) return false;
    if (medalsExpiry != other.medalsExpiry) return false;
    if (memberMedalsExpiry != other.memberMedalsExpiry) return false;
    if (memberSecurityExpiry != other.memberSecurityExpiry) return false;
    if (memberSecurityLogExpiry != other.memberSecurityLogExpiry) return false;
    if (memberTrackingExpiry != other.memberTrackingExpiry) return false;
    if (outpostListExpiry != other.outpostListExpiry) return false;
    if (outpostServiceDetailExpiry != other.outpostServiceDetailExpiry) return false;
    if (shareholdersExpiry != other.shareholdersExpiry) return false;
    if (standingsExpiry != other.standingsExpiry) return false;
    if (starbaseDetailExpiry != other.starbaseDetailExpiry) return false;
    if (starbaseListExpiry != other.starbaseListExpiry) return false;
    if (titlesExpiry != other.titlesExpiry) return false;
    if (walletJournalExpiry != other.walletJournalExpiry) return false;
    if (walletTransactionsExpiry != other.walletTransactionsExpiry) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Corporation [corporationName=" + corporationName + ", corporationID=" + corporationID + ", accountBalanceExpiry=" + accountBalanceExpiry
        + ", assetListExpiry=" + assetListExpiry + ", contactListExpiry=" + contactListExpiry + ", containerLogExpiry=" + containerLogExpiry
        + ", corporationSheetExpiry=" + corporationSheetExpiry + ", blueprintsExpiry=" + blueprintsExpiry + ", bookmarksExpiry=" + bookmarksExpiry
        + ", contractsExpiry=" + contractsExpiry + ", contractItemsExpiry=" + contractItemsExpiry + ", contractBidsExpiry=" + contractBidsExpiry
        + ", customsOfficeExpiry=" + customsOfficeExpiry + ", facilitiesExpiry=" + facilitiesExpiry + ", facWarStatsExpiry=" + facWarStatsExpiry
        + ", industryJobsExpiry=" + industryJobsExpiry + ", industryJobsHistoryExpiry=" + industryJobsHistoryExpiry + ", killlogExpiry=" + killlogExpiry
        + ", locationsExpiry=" + locationsExpiry + ", marketOrdersExpiry=" + marketOrdersExpiry + ", medalsExpiry=" + medalsExpiry + ", memberMedalsExpiry="
        + memberMedalsExpiry + ", memberSecurityExpiry=" + memberSecurityExpiry + ", memberSecurityLogExpiry=" + memberSecurityLogExpiry
        + ", memberTrackingExpiry=" + memberTrackingExpiry + ", outpostListExpiry=" + outpostListExpiry + ", outpostServiceDetailExpiry="
        + outpostServiceDetailExpiry + ", shareholdersExpiry=" + shareholdersExpiry + ", standingsExpiry=" + standingsExpiry + ", starbaseDetailExpiry="
        + starbaseDetailExpiry + ", starbaseListExpiry=" + starbaseListExpiry + ", titlesExpiry=" + titlesExpiry + ", walletJournalExpiry="
        + walletJournalExpiry + ", walletTransactionsExpiry=" + walletTransactionsExpiry + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            CachedData sup) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return null;
  }

  public static Corporation getCorporation(
                                           final SynchronizedEveAccount parent) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Corporation>() {
        @Override
        public Corporation run() throws Exception {
          TypedQuery<Corporation> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Corporation.get", Corporation.class);
          getter.setParameter("owner", parent);
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

  public static Corporation getOrCreateCorporation(
                                                   final SynchronizedEveAccount owner) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Corporation>() {
        @Override
        public Corporation run() throws Exception {
          Corporation existing = getCorporation(owner);
          if (existing == null) {
            existing = new Corporation();
            existing.setup(owner, OrbitalProperties.getCurrentTime());
            existing.setCorporationName(owner.getEveCorporationName());
            existing.setCorporationID(owner.getEveCorporationID());
            existing = EveKitUserAccountProvider.getFactory().getEntityManager().merge(existing);
          }
          return existing;
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

}
