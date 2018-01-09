package enterprises.orbital.evekit.model.corporation;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.model.AbstractAccountBasedTest;
import enterprises.orbital.evekit.model.CachedData;

public class CorporationTest extends AbstractAccountBasedTest {
  @Test
  public void blankTest() {
    Corporation cut1, cut2;
    cut1 = new Corporation();
    cut2 = new Corporation();
    Assert.assertEquals(cut1, cut2);
    Assert.assertEquals(cut1.hashCode(), cut2.hashCode());
    Assert.assertEquals(cut1.getAccountBalanceExpiry(), cut2.getAccountBalanceExpiry());
    Assert.assertEquals(cut1.getAssetListExpiry(), cut2.getAssetListExpiry());
    Assert.assertEquals(cut1.getContactListExpiry(), cut2.getContactListExpiry());
    Assert.assertEquals(cut1.getContainerLogExpiry(), cut2.getContainerLogExpiry());
    Assert.assertEquals(cut1.getCorporationSheetExpiry(), cut2.getCorporationSheetExpiry());
    Assert.assertEquals(cut1.getBlueprintsExpiry(), cut2.getBlueprintsExpiry());
    Assert.assertEquals(cut1.getContractsExpiry(), cut2.getContractsExpiry());
    Assert.assertEquals(cut1.getContractItemsExpiry(), cut2.getContractItemsExpiry());
    Assert.assertEquals(cut1.getContractBidsExpiry(), cut2.getContractBidsExpiry());
    Assert.assertEquals(cut1.getCustomsOfficeExpiry(), cut2.getCustomsOfficeExpiry());
    Assert.assertEquals(cut1.getFacilitiesExpiry(), cut2.getFacilitiesExpiry());
    Assert.assertEquals(cut1.getFacWarStatsExpiry(), cut2.getFacWarStatsExpiry());
    Assert.assertEquals(cut1.getIndustryJobsExpiry(), cut2.getIndustryJobsExpiry());
    Assert.assertEquals(cut1.getIndustryJobsHistoryExpiry(), cut2.getIndustryJobsHistoryExpiry());
    Assert.assertEquals(cut1.getKilllogExpiry(), cut2.getKilllogExpiry());
    Assert.assertEquals(cut1.getMarketOrdersExpiry(), cut2.getMarketOrdersExpiry());
    Assert.assertEquals(cut1.getMedalsExpiry(), cut2.getMedalsExpiry());
    Assert.assertEquals(cut1.getMemberMedalsExpiry(), cut2.getMemberMedalsExpiry());
    Assert.assertEquals(cut1.getMemberSecurityExpiry(), cut2.getMemberSecurityExpiry());
    Assert.assertEquals(cut1.getMemberSecurityLogExpiry(), cut2.getMemberSecurityLogExpiry());
    Assert.assertEquals(cut1.getMemberTrackingExpiry(), cut2.getMemberTrackingExpiry());
    Assert.assertEquals(cut1.getOutpostListExpiry(), cut2.getOutpostListExpiry());
    Assert.assertEquals(cut1.getOutpostServiceDetailExpiry(), cut2.getOutpostServiceDetailExpiry());
    Assert.assertEquals(cut1.getShareholdersExpiry(), cut2.getShareholdersExpiry());
    Assert.assertEquals(cut1.getStandingsExpiry(), cut2.getStandingsExpiry());
    Assert.assertEquals(cut1.getStarbaseDetailExpiry(), cut2.getStarbaseDetailExpiry());
    Assert.assertEquals(cut1.getStarbaseListExpiry(), cut2.getStarbaseListExpiry());
    Assert.assertEquals(cut1.getTitlesExpiry(), cut2.getTitlesExpiry());
    Assert.assertEquals(cut1.getWalletJournalExpiry(), cut2.getWalletJournalExpiry());
    Assert.assertEquals(cut1.getWalletTransactionsExpiry(), cut2.getWalletTransactionsExpiry());

  }

  @Test
  public void testGetCorporation() throws IOException, ExecutionException {
    Corporation existing, result;

    // Populate an existing Corporation . Getter should return this.
    existing = new Corporation();
    existing.setup(testAccount, 1234L);
    existing.setCorporationName(testAccount.getEveCorporationName());
    existing.setCorporationID(testAccount.getEveCorporationID());
    existing = CachedData.update(existing);

    // Get the Corporation and check.
    result = Corporation.getCorporation(testAccount);
    Assert.assertEquals(existing, result);
    Assert.assertEquals(existing.hashCode(), result.hashCode());
  }

  @Test
  public void testGetCorporationMissing() throws IOException, ExecutionException {
    Corporation existing, result;

    // Populate an existing Corporation. Getter should NOT return this one.
    existing = new Corporation();
    existing.setup(otherAccount, 1234L);
    existing.setCorporationName(otherAccount.getEveCorporationName());
    existing.setCorporationID(otherAccount.getEveCorporationID());
    existing = CachedData.update(existing);

    // Get the Corporation and check.
    result = Corporation.getCorporation(testAccount);
    Assert.assertNull(result);
  }

  @Test
  public void testGetOrCreateExisting() throws IOException, ExecutionException {
    Corporation existing, result;

    // Populate an existing Corporation. Getter should return this.
    existing = new Corporation();
    existing.setup(testAccount, 1234L);
    existing.setCorporationName(testAccount.getEveCorporationName());
    existing.setCorporationID(testAccount.getEveCorporationID());
    existing = CachedData.update(existing);

    // Get the Corporation and check.
    result = Corporation.getOrCreateCorporation(testAccount);
    Assert.assertNotSame(existing, result);
    Assert.assertEquals(existing, result);

  }

  @Test
  public void testGetOrCreateMissing() throws IOException, ExecutionException {
    Corporation other, result;

    // Populate an existing Corporation for a different account.
    other = new Corporation();
    other.setup(otherAccount, 1234L);
    other.setCorporationName(otherAccount.getEveCorporationName());
    other.setCorporationID(otherAccount.getEveCorporationID());
    other = CachedData.update(other);

    // Get the Corporation and check.
    result = Corporation.getOrCreateCorporation(testAccount);
    Assert.assertFalse(other.equals(result));

  }

  @Test
  public void testInsertFromDifferentUsers() throws IOException, ExecutionException {
    Corporation cap1, cap2;

    cap1 = Corporation.getOrCreateCorporation(testAccount);
    cap2 = Corporation.getOrCreateCorporation(testAccount2);

    Assert.assertFalse(cap1.equals(cap2));

  }

  @Test
  public void testUpdateCorporation() throws IOException, ExecutionException {
    Corporation existing;

    // Populate an existing Corporation for update.
    existing = new Corporation();
    existing.setup(testAccount, 1234L);
    existing.setCorporationName(testAccount.getEveCorporationName());
    existing.setCorporationID(testAccount.getEveCorporationID());
    existing = CachedData.update(existing);

    // Update the Corporation.
    existing.setAccountBalanceExpiry(OrbitalProperties.getCurrentTime());
    existing = CachedData.update(existing);

    Corporation result = Corporation.getCorporation(testAccount);
    Assert.assertEquals(existing, result);
  }
}
