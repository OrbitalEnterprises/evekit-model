package enterprises.orbital.evekit.model.character;

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

/**
 * Top level data object for character data.
 * 
 * NOTE: Capsuleers are a special form of CachedData and always have a single life window. This is because Capsuleers are solely used to track synchronization
 * expiry.
 */
@Entity
@Table(
    name = "evekit_data_capsuleer")
@NamedQueries({
    @NamedQuery(
        name = "Capsuleer.get",
        query = "SELECT c FROM Capsuleer c where c.owner = :owner"),
})
public class Capsuleer extends CachedData {
  private static final Logger log                          = Logger.getLogger(Capsuleer.class.getName());

  private String              characterName;
  private long                characterID;

  // Request expiry data
  private long                accountStatusExpiry          = -1;
  private long                accountBalanceExpiry         = -1;
  private long                assetListExpiry              = -1;
  private long                calendarEventAttendeesExpiry = -1;
  private long                characterSheetExpiry         = -1;
  private long                partialCharacterSheetExpiry  = -1;
  private long                chatChannelsExpiry           = -1;
  private long                contactListExpiry            = -1;
  private long                contactNotificationsExpiry   = -1;
  private long                blueprintsExpiry             = -1;
  private long                bookmarksExpiry              = -1;
  private long                contractsExpiry              = -1;
  private long                contractItemsExpiry          = -1;
  private long                contractBidsExpiry           = -1;
  private long                facWarStatsExpiry            = -1;
  private long                industryJobsExpiry           = -1;
  private long                industryJobsHistoryExpiry    = -1;
  private long                killlogExpiry                = -1;
  private long                locationsExpiry              = -1;
  private long                mailBodiesExpiry             = -1;
  private long                mailingListsExpiry           = -1;
  private long                mailMessagesExpiry           = -1;
  private long                marketOrdersExpiry           = -1;
  private long                medalsExpiry                 = -1;
  private long                notificationsExpiry          = -1;
  private long                notificationTextsExpiry      = -1;
  private long                planetaryColoniesExpiry      = -1;
  private long                researchExpiry               = -1;
  private long                skillInTrainingExpiry        = -1;
  private long                skillQueueExpiry             = -1;
  private long                skillsExpiry                 = -1;
  private long                standingsExpiry              = -1;
  private long                upcomingCalendarEventsExpiry = -1;
  private long                walletJournalExpiry          = -1;
  private long                walletTransactionsExpiry     = -1;

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    // NOP
  }

  public String getCharacterName() {
    return characterName;
  }

  public void setCharacterName(
                               String characterName) {
    this.characterName = characterName;
  }

  public long getCharacterID() {
    return characterID;
  }

  public void setCharacterID(
                             long characterID) {
    this.characterID = characterID;
  }

  public long getAccountStatusExpiry() {
    return accountStatusExpiry;
  }

  public void setAccountStatusExpiry(
                                     long accountStatusExpiry) {
    this.accountStatusExpiry = accountStatusExpiry;
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

  public long getCalendarEventAttendeesExpiry() {
    return calendarEventAttendeesExpiry;
  }

  public void setCalendarEventAttendeesExpiry(
                                              long calendarEventAttendeesExpiry) {
    this.calendarEventAttendeesExpiry = calendarEventAttendeesExpiry;
  }

  public long getCharacterSheetExpiry() {
    return characterSheetExpiry;
  }

  public void setCharacterSheetExpiry(
                                      long characterSheetExpiry) {
    this.characterSheetExpiry = characterSheetExpiry;
  }

  public long getPartialCharacterSheetExpiry() {
    return partialCharacterSheetExpiry;
  }

  public void setPartialCharacterSheetExpiry(
                                             long partialCharacterSheetExpiry) {
    this.partialCharacterSheetExpiry = partialCharacterSheetExpiry;
  }

  public long getChatChannelsExpiry() {
    return chatChannelsExpiry;
  }

  public void setChatChannelsExpiry(
                                    long chatChannelsExpiry) {
    this.chatChannelsExpiry = chatChannelsExpiry;
  }

  public long getContactListExpiry() {
    return contactListExpiry;
  }

  public void setContactListExpiry(
                                   long contactListExpiry) {
    this.contactListExpiry = contactListExpiry;
  }

  public long getContactNotificationsExpiry() {
    return contactNotificationsExpiry;
  }

  public void setContactNotificationsExpiry(
                                            long contactNotificationsExpiry) {
    this.contactNotificationsExpiry = contactNotificationsExpiry;
  }

  public long getBlueprintsExpiry() {
    return blueprintsExpiry;
  }

  public void setBlueprintsExpiry(
                                  long blueprintExpiry) {
    this.blueprintsExpiry = blueprintExpiry;
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
                                 long contractExpiry) {
    this.contractsExpiry = contractExpiry;
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

  public long getMailBodiesExpiry() {
    return mailBodiesExpiry;
  }

  public void setMailBodiesExpiry(
                                  long mailBodiesExpiry) {
    this.mailBodiesExpiry = mailBodiesExpiry;
  }

  public long getMailingListsExpiry() {
    return mailingListsExpiry;
  }

  public void setMailingListsExpiry(
                                    long mailingListsExpiry) {
    this.mailingListsExpiry = mailingListsExpiry;
  }

  public long getMailMessagesExpiry() {
    return mailMessagesExpiry;
  }

  public void setMailMessagesExpiry(
                                    long mailMessagesExpiry) {
    this.mailMessagesExpiry = mailMessagesExpiry;
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

  public long getNotificationsExpiry() {
    return notificationsExpiry;
  }

  public void setNotificationsExpiry(
                                     long notificationsExpiry) {
    this.notificationsExpiry = notificationsExpiry;
  }

  public long getNotificationTextsExpiry() {
    return notificationTextsExpiry;
  }

  public void setNotificationTextsExpiry(
                                         long notificationTextsExpiry) {
    this.notificationTextsExpiry = notificationTextsExpiry;
  }

  public long getPlanetaryColoniesExpiry() {
    return planetaryColoniesExpiry;
  }

  public void setPlanetaryColoniesExpiry(
                                         long planetaryColoniesExpiry) {
    this.planetaryColoniesExpiry = planetaryColoniesExpiry;
  }

  public long getResearchExpiry() {
    return researchExpiry;
  }

  public void setResearchExpiry(
                                long researchExpiry) {
    this.researchExpiry = researchExpiry;
  }

  public long getSkillInTrainingExpiry() {
    return skillInTrainingExpiry;
  }

  public void setSkillInTrainingExpiry(
                                       long skillInTrainingExpiry) {
    this.skillInTrainingExpiry = skillInTrainingExpiry;
  }

  public long getSkillQueueExpiry() {
    return skillQueueExpiry;
  }

  public void setSkillQueueExpiry(
                                  long skillQueueExpiry) {
    this.skillQueueExpiry = skillQueueExpiry;
  }

  public long getSkillsExpiry() {
    return skillsExpiry;
  }

  public void setSkillsExpiry(
                              long skillsExpiry) {
    this.skillsExpiry = skillsExpiry;
  }

  public long getStandingsExpiry() {
    return standingsExpiry;
  }

  public void setStandingsExpiry(
                                 long standingsExpiry) {
    this.standingsExpiry = standingsExpiry;
  }

  public long getUpcomingCalendarEventsExpiry() {
    return upcomingCalendarEventsExpiry;
  }

  public void setUpcomingCalendarEventsExpiry(
                                              long upcomingCalendarEventsExpiry) {
    this.upcomingCalendarEventsExpiry = upcomingCalendarEventsExpiry;
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
    result = prime * result + (int) (accountStatusExpiry ^ (accountStatusExpiry >>> 32));
    result = prime * result + (int) (assetListExpiry ^ (assetListExpiry >>> 32));
    result = prime * result + (int) (blueprintsExpiry ^ (blueprintsExpiry >>> 32));
    result = prime * result + (int) (bookmarksExpiry ^ (bookmarksExpiry >>> 32));
    result = prime * result + (int) (calendarEventAttendeesExpiry ^ (calendarEventAttendeesExpiry >>> 32));
    result = prime * result + (int) (characterID ^ (characterID >>> 32));
    result = prime * result + ((characterName == null) ? 0 : characterName.hashCode());
    result = prime * result + (int) (characterSheetExpiry ^ (characterSheetExpiry >>> 32));
    result = prime * result + (int) (chatChannelsExpiry ^ (chatChannelsExpiry >>> 32));
    result = prime * result + (int) (contactListExpiry ^ (contactListExpiry >>> 32));
    result = prime * result + (int) (contactNotificationsExpiry ^ (contactNotificationsExpiry >>> 32));
    result = prime * result + (int) (contractBidsExpiry ^ (contractBidsExpiry >>> 32));
    result = prime * result + (int) (contractItemsExpiry ^ (contractItemsExpiry >>> 32));
    result = prime * result + (int) (contractsExpiry ^ (contractsExpiry >>> 32));
    result = prime * result + (int) (facWarStatsExpiry ^ (facWarStatsExpiry >>> 32));
    result = prime * result + (int) (industryJobsExpiry ^ (industryJobsExpiry >>> 32));
    result = prime * result + (int) (industryJobsHistoryExpiry ^ (industryJobsHistoryExpiry >>> 32));
    result = prime * result + (int) (killlogExpiry ^ (killlogExpiry >>> 32));
    result = prime * result + (int) (locationsExpiry ^ (locationsExpiry >>> 32));
    result = prime * result + (int) (mailBodiesExpiry ^ (mailBodiesExpiry >>> 32));
    result = prime * result + (int) (mailMessagesExpiry ^ (mailMessagesExpiry >>> 32));
    result = prime * result + (int) (mailingListsExpiry ^ (mailingListsExpiry >>> 32));
    result = prime * result + (int) (marketOrdersExpiry ^ (marketOrdersExpiry >>> 32));
    result = prime * result + (int) (medalsExpiry ^ (medalsExpiry >>> 32));
    result = prime * result + (int) (notificationTextsExpiry ^ (notificationTextsExpiry >>> 32));
    result = prime * result + (int) (notificationsExpiry ^ (notificationsExpiry >>> 32));
    result = prime * result + (int) (partialCharacterSheetExpiry ^ (partialCharacterSheetExpiry >>> 32));
    result = prime * result + (int) (planetaryColoniesExpiry ^ (planetaryColoniesExpiry >>> 32));
    result = prime * result + (int) (researchExpiry ^ (researchExpiry >>> 32));
    result = prime * result + (int) (skillInTrainingExpiry ^ (skillInTrainingExpiry >>> 32));
    result = prime * result + (int) (skillQueueExpiry ^ (skillQueueExpiry >>> 32));
    result = prime * result + (int) (skillsExpiry ^ (skillsExpiry >>> 32));
    result = prime * result + (int) (standingsExpiry ^ (standingsExpiry >>> 32));
    result = prime * result + (int) (upcomingCalendarEventsExpiry ^ (upcomingCalendarEventsExpiry >>> 32));
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
    Capsuleer other = (Capsuleer) obj;
    if (accountBalanceExpiry != other.accountBalanceExpiry) return false;
    if (accountStatusExpiry != other.accountStatusExpiry) return false;
    if (assetListExpiry != other.assetListExpiry) return false;
    if (blueprintsExpiry != other.blueprintsExpiry) return false;
    if (bookmarksExpiry != other.bookmarksExpiry) return false;
    if (calendarEventAttendeesExpiry != other.calendarEventAttendeesExpiry) return false;
    if (characterID != other.characterID) return false;
    if (characterName == null) {
      if (other.characterName != null) return false;
    } else if (!characterName.equals(other.characterName)) return false;
    if (characterSheetExpiry != other.characterSheetExpiry) return false;
    if (chatChannelsExpiry != other.chatChannelsExpiry) return false;
    if (contactListExpiry != other.contactListExpiry) return false;
    if (contactNotificationsExpiry != other.contactNotificationsExpiry) return false;
    if (contractBidsExpiry != other.contractBidsExpiry) return false;
    if (contractItemsExpiry != other.contractItemsExpiry) return false;
    if (contractsExpiry != other.contractsExpiry) return false;
    if (facWarStatsExpiry != other.facWarStatsExpiry) return false;
    if (industryJobsExpiry != other.industryJobsExpiry) return false;
    if (industryJobsHistoryExpiry != other.industryJobsHistoryExpiry) return false;
    if (killlogExpiry != other.killlogExpiry) return false;
    if (locationsExpiry != other.locationsExpiry) return false;
    if (mailBodiesExpiry != other.mailBodiesExpiry) return false;
    if (mailMessagesExpiry != other.mailMessagesExpiry) return false;
    if (mailingListsExpiry != other.mailingListsExpiry) return false;
    if (marketOrdersExpiry != other.marketOrdersExpiry) return false;
    if (medalsExpiry != other.medalsExpiry) return false;
    if (notificationTextsExpiry != other.notificationTextsExpiry) return false;
    if (notificationsExpiry != other.notificationsExpiry) return false;
    if (partialCharacterSheetExpiry != other.partialCharacterSheetExpiry) return false;
    if (planetaryColoniesExpiry != other.planetaryColoniesExpiry) return false;
    if (researchExpiry != other.researchExpiry) return false;
    if (skillInTrainingExpiry != other.skillInTrainingExpiry) return false;
    if (skillQueueExpiry != other.skillQueueExpiry) return false;
    if (skillsExpiry != other.skillsExpiry) return false;
    if (standingsExpiry != other.standingsExpiry) return false;
    if (upcomingCalendarEventsExpiry != other.upcomingCalendarEventsExpiry) return false;
    if (walletJournalExpiry != other.walletJournalExpiry) return false;
    if (walletTransactionsExpiry != other.walletTransactionsExpiry) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Capsuleer [characterName=" + characterName + ", characterID=" + characterID + ", accountStatusExpiry=" + accountStatusExpiry
        + ", accountBalanceExpiry=" + accountBalanceExpiry + ", assetListExpiry=" + assetListExpiry + ", calendarEventAttendeesExpiry="
        + calendarEventAttendeesExpiry + ", characterSheetExpiry=" + characterSheetExpiry + ", partialCharacterSheetExpiry=" + partialCharacterSheetExpiry
        + ", chatChannelsExpiry=" + chatChannelsExpiry + ", contactListExpiry=" + contactListExpiry + ", contactNotificationsExpiry="
        + contactNotificationsExpiry + ", blueprintsExpiry=" + blueprintsExpiry + ", bookmarksExpiry=" + bookmarksExpiry + ", contractsExpiry="
        + contractsExpiry + ", contractItemsExpiry=" + contractItemsExpiry + ", contractBidsExpiry=" + contractBidsExpiry + ", facWarStatsExpiry="
        + facWarStatsExpiry + ", industryJobsExpiry=" + industryJobsExpiry + ", industryJobsHistoryExpiry=" + industryJobsHistoryExpiry + ", killlogExpiry="
        + killlogExpiry + ", locationsExpiry=" + locationsExpiry + ", mailBodiesExpiry=" + mailBodiesExpiry + ", mailingListsExpiry=" + mailingListsExpiry
        + ", mailMessagesExpiry=" + mailMessagesExpiry + ", marketOrdersExpiry=" + marketOrdersExpiry + ", medalsExpiry=" + medalsExpiry
        + ", notificationsExpiry=" + notificationsExpiry + ", notificationTextsExpiry=" + notificationTextsExpiry + ", planetaryColoniesExpiry="
        + planetaryColoniesExpiry + ", researchExpiry=" + researchExpiry + ", skillInTrainingExpiry=" + skillInTrainingExpiry + ", skillQueueExpiry="
        + skillQueueExpiry + ", skillsExpiry=" + skillsExpiry + ", standingsExpiry=" + standingsExpiry + ", upcomingCalendarEventsExpiry="
        + upcomingCalendarEventsExpiry + ", walletJournalExpiry=" + walletJournalExpiry + ", walletTransactionsExpiry=" + walletTransactionsExpiry + "]";
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

  public static Capsuleer getCapsuleer(
                                       final SynchronizedEveAccount parent) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Capsuleer>() {
        @Override
        public Capsuleer run() throws Exception {
          TypedQuery<Capsuleer> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Capsuleer.get", Capsuleer.class);
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

  public static Capsuleer getOrCreateCapsuleer(
                                               final SynchronizedEveAccount owner) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Capsuleer>() {
        @Override
        public Capsuleer run() throws Exception {
          Capsuleer existing = getCapsuleer(owner);
          if (existing == null) {
            existing = new Capsuleer();
            existing.setup(owner, OrbitalProperties.getCurrentTime());
            existing.setCharacterName(owner.getEveCharacterName());
            existing.setCharacterID(owner.getEveCharacterID());
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
