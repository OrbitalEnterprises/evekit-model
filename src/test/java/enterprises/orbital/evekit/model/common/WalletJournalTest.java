package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletJournalTest extends AbstractModelTester<WalletJournal> {

  private final int division = TestBase.getRandomInt(100000000);
  private final long refID = TestBase.getRandomInt(100000000);
  private final long date = TestBase.getRandomInt(100000000);
  private final String refType = TestBase.getRandomText(50);
  private final int firstPartyID = TestBase.getRandomInt();
  private final String firstPartyType = TestBase.getRandomText(50);
  private final int secondPartyID = TestBase.getRandomInt();
  private final String secondPartyType = TestBase.getRandomText(50);
  private final String argName1 = "test arg name 1";
  private final long argID1 = TestBase.getRandomInt(100000000);
  private final BigDecimal amount = TestBase.getRandomBigDecimal(100000000);
  private final BigDecimal balance = TestBase.getRandomBigDecimal(100000000);
  private final String reason = "test reason";
  private final int taxReceiverID = TestBase.getRandomInt(100000000);
  private final BigDecimal taxAmount = TestBase.getRandomBigDecimal(100000000);
  private final long locationID = TestBase.getRandomLong();
  private final long transactionID = TestBase.getRandomLong();
  private final String npcName = TestBase.getRandomText(50);
  private final int npcID = TestBase.getRandomInt();
  private final int destroyedShipTypeID = TestBase.getRandomInt();
  private final int characterID = TestBase.getRandomInt();
  private final int corporationID = TestBase.getRandomInt();
  private final int allianceID = TestBase.getRandomInt();
  private final int jobID = TestBase.getRandomInt();
  private final int contractID = TestBase.getRandomInt();
  private final int systemID = TestBase.getRandomInt();
  private final int planetID = TestBase.getRandomInt();

  final ClassUnderTestConstructor<WalletJournal> eol = () -> new WalletJournal(division, refID, date, refType,
                                                                               firstPartyID, firstPartyType,
                                                                               secondPartyID, secondPartyType, argName1,
                                                                               argID1, amount, balance, reason,
                                                                               taxReceiverID, taxAmount, locationID,
                                                                               transactionID, npcName, npcID,
                                                                               destroyedShipTypeID, characterID,
                                                                               corporationID, allianceID, jobID,
                                                                               contractID, systemID, planetID);

  final ClassUnderTestConstructor<WalletJournal> live = () -> new WalletJournal(division, refID, date, refType,
                                                                                firstPartyID + 1, firstPartyType,
                                                                                secondPartyID, secondPartyType,
                                                                                argName1, argID1, amount, balance,
                                                                                reason, taxReceiverID, taxAmount,
                                                                                locationID, transactionID, npcName,
                                                                                npcID, destroyedShipTypeID, characterID,
                                                                                corporationID, allianceID, jobID,
                                                                                contractID, systemID, planetID);

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, () -> new WalletJournal[]{
            new WalletJournal(division + 1, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID + 1, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date + 1, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType + "1", firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID + 1, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType + "1", secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID + 1,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType + "1", argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1 + "1", argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1 + 1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount.add(BigDecimal.ONE), balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance.add(BigDecimal.ONE), reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason + "1", taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID + 1, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount.add(BigDecimal.ONE),
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID + 1, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID + 1, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName + "1", npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID + 1, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID + 1, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID + 1,
                              corporationID, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID + 1, allianceID, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID + 1, jobID, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID + 1, contractID, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID + 1, systemID, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID + 1, planetID),
            new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                              secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                              locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                              corporationID, allianceID, jobID, contractID, systemID, planetID + 1)
        }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_WALLET_JOURNAL));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> WalletJournal.get(account, time, division, refID));
  }

  @Test
  public void testGetAllForward() throws Exception {
    // Should exclude:
    // - journal entries for a different account
    // - journal entries not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    WalletJournal existing;
    Map<Long, WalletJournal> listCheck = new HashMap<>();

    existing = new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(division, refID + 10, date + 10, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(division, refID + 20, date + 20, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(division, refID + 30, date + 30, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(division, refID + 5, date + 5, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(division, refID + 3, date + 3, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobsare returned
    List<WalletJournal> result = WalletJournal.getAllForward(testAccount, 8888L, 10, 0);
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletJournal next : result) {
      long refID = next.getRefID();
      Assert.assertTrue(listCheck.containsKey(refID));
      Assert.assertEquals(listCheck.get(refID), next);
    }

    // Verify limited set returned
    result = WalletJournal.getAllForward(testAccount, 8888L, 2, date - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletJournal.getAllForward(testAccount, 8888L, 100, date + 10);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 30), result.get(1));

  }

  @Test
  public void testGetAllBackward() throws Exception {
    // Should exclude:
    // - journal entries for a different account
    // - journal entries not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    WalletJournal existing;
    Map<Long, WalletJournal> listCheck = new HashMap<>();

    existing = new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(division, refID + 10, date + 10, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(division, refID + 20, date + 20, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(division, refID + 30, date + 30, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(division, refID + 5, date + 5, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(division, refID + 3, date + 3, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all jobs are returned
    List<WalletJournal> result = WalletJournal.getAllBackward(testAccount, 8888L, 10, Long.MAX_VALUE);
    Assert.assertEquals(listCheck.size(), result.size());
    for (WalletJournal next : result) {
      long refID = next.getRefID();
      Assert.assertTrue(listCheck.containsKey(refID));
      Assert.assertEquals(listCheck.get(refID), next);
    }

    // Verify limited set returned
    result = WalletJournal.getAllBackward(testAccount, 8888L, 2, date + 30 + 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));

    // Verify continuation ID returns proper set
    result = WalletJournal.getAllBackward(testAccount, 8888L, 100, date + 20);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 10), result.get(0));
    Assert.assertEquals(listCheck.get(refID), result.get(1));
  }

  @Test
  public void testGetRange() throws Exception {
    // Should exclude:
    // - journal entries for a different account
    // - journal entries not live at the given time
    // - journal entries out of the specified range
    // Need to test:
    // - max results limitation
    WalletJournal existing;
    Map<Long, WalletJournal> listCheck = new HashMap<>();

    existing = new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID, existing);

    existing = new WalletJournal(division, refID + 10, date + 10, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 10, existing);

    existing = new WalletJournal(division, refID + 20, date + 20, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 20, existing);

    existing = new WalletJournal(division, refID + 30, date + 30, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(refID + 30, existing);

    // Associated with different account
    existing = new WalletJournal(division, refID, date, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Out of the specified range
    existing = new WalletJournal(division, refID - 10, date - 10, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);
    existing = new WalletJournal(division, refID + 40, date + 40, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new WalletJournal(division, refID + 5, date + 5, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new WalletJournal(division, refID + 3, date + 3, refType, firstPartyID, firstPartyType, secondPartyID,
                                 secondPartyType, argName1, argID1, amount, balance, reason, taxReceiverID, taxAmount,
                                 locationID, transactionID, npcName, npcID, destroyedShipTypeID, characterID,
                                 corporationID, allianceID, jobID, contractID, systemID, planetID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all entries are returned in ascending order
    List<WalletJournal> result = WalletJournal.getRange(testAccount, 8888L, 10, date, date + 30, true);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(2));
    Assert.assertEquals(listCheck.get(refID + 30), result.get(3));

    // Verify all entries are returned in descending order
    result = WalletJournal.getRange(testAccount, 8888L, 10, date, date + 30, false);
    Assert.assertEquals(4, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(2));
    Assert.assertEquals(listCheck.get(refID), result.get(3));

    // Verify limited set returned ascending
    result = WalletJournal.getRange(testAccount, 8888L, 2, date, date + 30, true);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 10), result.get(1));

    // Verify limited set returned descending
    result = WalletJournal.getRange(testAccount, 8888L, 2, date, date + 30, false);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(refID + 30), result.get(0));
    Assert.assertEquals(listCheck.get(refID + 20), result.get(1));

  }

}
