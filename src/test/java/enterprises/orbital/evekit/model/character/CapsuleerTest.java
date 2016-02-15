package enterprises.orbital.evekit.model.character;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.model.AbstractAccountBasedTest;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.character.Capsuleer;

public class CapsuleerTest extends AbstractAccountBasedTest {
  @Test
  public void blankTest() {
    Capsuleer cut1, cut2;
    cut1 = new Capsuleer();
    cut2 = new Capsuleer();
    Assert.assertEquals(cut1, cut2);
    Assert.assertEquals(cut1.hashCode(), cut2.hashCode());
    Assert.assertEquals(cut1.getAccountBalanceExpiry(), cut2.getAccountBalanceExpiry());
    Assert.assertEquals(cut1.getAssetListExpiry(), cut2.getAssetListExpiry());
    Assert.assertEquals(cut1.getCalendarEventAttendeesExpiry(), cut2.getCalendarEventAttendeesExpiry());
    Assert.assertEquals(cut1.getCharacterSheetExpiry(), cut2.getCharacterSheetExpiry());
    Assert.assertEquals(cut1.getContactListExpiry(), cut2.getContactListExpiry());
    Assert.assertEquals(cut1.getContactNotificationsExpiry(), cut2.getContactNotificationsExpiry());
    Assert.assertEquals(cut1.getFacWarStatsExpiry(), cut2.getFacWarStatsExpiry());
    Assert.assertEquals(cut1.getIndustryJobsExpiry(), cut2.getIndustryJobsExpiry());
    Assert.assertEquals(cut1.getKilllogExpiry(), cut2.getKilllogExpiry());
    Assert.assertEquals(cut1.getMailBodiesExpiry(), cut2.getMailBodiesExpiry());
    Assert.assertEquals(cut1.getMailingListsExpiry(), cut2.getMailingListsExpiry());
    Assert.assertEquals(cut1.getMailMessagesExpiry(), cut2.getMailMessagesExpiry());
    Assert.assertEquals(cut1.getMarketOrdersExpiry(), cut2.getMarketOrdersExpiry());
    Assert.assertEquals(cut1.getMedalsExpiry(), cut2.getMedalsExpiry());
    Assert.assertEquals(cut1.getNotificationsExpiry(), cut2.getNotificationsExpiry());
    Assert.assertEquals(cut1.getNotificationTextsExpiry(), cut2.getNotificationTextsExpiry());
    Assert.assertEquals(cut1.getResearchExpiry(), cut2.getResearchExpiry());
    Assert.assertEquals(cut1.getSkillInTrainingExpiry(), cut2.getSkillInTrainingExpiry());
    Assert.assertEquals(cut1.getSkillQueueExpiry(), cut2.getSkillQueueExpiry());
    Assert.assertEquals(cut1.getStandingsExpiry(), cut2.getStandingsExpiry());
    Assert.assertEquals(cut1.getUpcomingCalendarEventsExpiry(), cut2.getUpcomingCalendarEventsExpiry());
    Assert.assertEquals(cut1.getWalletJournalExpiry(), cut2.getWalletJournalExpiry());
    Assert.assertEquals(cut1.getWalletTransactionsExpiry(), cut2.getWalletTransactionsExpiry());
  }

  @Test
  public void testGetCapsuleer() throws IOException, ExecutionException {
    Capsuleer existing, result;

    // Populate an existing Capsuleer. Getter should return this.
    existing = new Capsuleer();
    existing.setup(testAccount, 1234L);
    existing.setCharacterName(testAccount.getEveCharacterName());
    existing.setCharacterID(testAccount.getEveCharacterID());
    existing = CachedData.updateData(existing);

    // Get the Capsuleer and check.
    result = Capsuleer.getCapsuleer(testAccount);
    Assert.assertEquals(existing, result);
    Assert.assertEquals(existing.hashCode(), result.hashCode());
  }

  @Test
  public void testGetCapsuleerMissing() throws IOException, ExecutionException {
    Capsuleer existing, result;

    // Populate an existing Capsuleer. Getter should NOT return this one.
    existing = new Capsuleer();
    existing.setup(otherAccount, 1234L);
    existing.setCharacterName(otherAccount.getEveCharacterName());
    existing.setCharacterID(otherAccount.getEveCharacterID());
    CachedData.updateData(existing);

    // Get the Capsuleer and check.
    result = Capsuleer.getCapsuleer(testAccount);
    Assert.assertNull(result);
  }

  @Test
  public void testGetOrCreateExisting() throws IOException, ExecutionException {
    Capsuleer existing, result;

    // Populate an existing Capsuleer. Getter should return this.
    existing = new Capsuleer();
    existing.setup(testAccount, 1234L);
    existing.setCharacterName(testAccount.getEveCharacterName());
    existing.setCharacterID(testAccount.getEveCharacterID());
    existing = CachedData.updateData(existing);

    // Get the Capsuleer and check.
    result = Capsuleer.getOrCreateCapsuleer(testAccount);
    Assert.assertNotSame(existing, result);
    Assert.assertEquals(existing, result);

  }

  @Test
  public void testGetOrCreateMissing() throws IOException, ExecutionException {
    Capsuleer other, result;

    // Populate an existing Capsuleer for a different account.
    other = new Capsuleer();
    other.setup(otherAccount, 1234L);
    other.setCharacterName(otherAccount.getEveCharacterName());
    other.setCharacterID(otherAccount.getEveCharacterID());
    other = CachedData.updateData(other);

    // Get the Capsuleer and check.
    result = Capsuleer.getOrCreateCapsuleer(testAccount);
    Assert.assertFalse(other.equals(result));

  }

  @Test
  public void testInsertFromDifferentUsers() throws IOException, ExecutionException {
    Capsuleer cap1, cap2;

    cap1 = Capsuleer.getOrCreateCapsuleer(testAccount);
    cap2 = Capsuleer.getOrCreateCapsuleer(testAccount2);

    Assert.assertFalse(cap1.equals(cap2));

  }

  @Test
  public void testUpdateCapsuleer() throws IOException, ExecutionException {
    Capsuleer existing;

    // Populate an existing Capsuleer for update.
    existing = new Capsuleer();
    existing.setup(testAccount, 1234L);
    existing.setCharacterName(testAccount.getEveCharacterName());
    existing.setCharacterID(testAccount.getEveCharacterID());
    existing = CachedData.updateData(existing);

    // Update the Capsuleer.
    existing.setAccountBalanceExpiry(OrbitalProperties.getCurrentTime());
    existing = CachedData.updateData(existing);

    Capsuleer result = Capsuleer.getCapsuleer(testAccount);
    Assert.assertEquals(existing, result);
  }
}
