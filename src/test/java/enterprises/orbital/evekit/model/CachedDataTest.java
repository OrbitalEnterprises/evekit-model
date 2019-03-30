package enterprises.orbital.evekit.model;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.*;
import enterprises.orbital.evekit.model.character.*;
import enterprises.orbital.evekit.model.common.*;
import enterprises.orbital.evekit.model.corporation.*;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class CachedDataTest extends AbstractAccountBasedTest {

  // CachedData test
  // 1) setup sets fields properly
  // 2) Dup copies proper fields
  // 3) Evolve EOLs target and copies proper fields to copy

  @Test
  public void testSetupFn() {
    AccountBalance cut = new AccountBalance(25, BigDecimal.TEN);
    byte[] tstMsk = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    long start = 8675L;
    cut.setup(testAccount, start);
    Assert.assertEquals(testAccount, cut.getOwner());
    Assert.assertTrue(Arrays.equals(tstMsk, cut.getAccessMask()));
    Assert.assertEquals(start, cut.getLifeStart());
    Assert.assertEquals(Long.MAX_VALUE, cut.getLifeEnd());
  }

  @Test
  public void testDupFn() {
    AccountBalance cut = new AccountBalance(25, BigDecimal.TEN);
    byte[] tstMsk = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    long start = 8675L;
    cut.setup(testAccount, start);

    AccountBalance dup = new AccountBalance(85, BigDecimal.ONE);
    byte[] tstMsk2 = AccountAccessMask.createMask(AccountAccessMask.ACCESS_BLUEPRINTS);
    long start2 = 777L;
    dup.setup(testAccount, start2);
    dup.setAccessMask(tstMsk2);

    cut.dup(dup);

    Assert.assertEquals(testAccount, dup.getOwner());
    Assert.assertTrue(Arrays.equals(tstMsk, dup.getAccessMask()));
    Assert.assertEquals(start, dup.getLifeStart());
    Assert.assertEquals(Long.MAX_VALUE, dup.getLifeEnd());
  }

  @Test
  public void testEvolveFn() {
    AccountBalance cut = new AccountBalance(25, BigDecimal.TEN);
    byte[] tstMsk = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
    long start = 8675L;
    cut.setup(testAccount, start);

    AccountBalance dup = new AccountBalance(85, BigDecimal.ONE);
    byte[] tstMsk2 = AccountAccessMask.createMask(AccountAccessMask.ACCESS_BLUEPRINTS);
    long start2 = 777L;
    dup.setup(testAccount, start2);
    dup.setAccessMask(tstMsk2);

    cut.evolve(dup, 77777L);

    Assert.assertEquals(start, cut.getLifeStart());
    Assert.assertEquals(77777L, cut.getLifeEnd());
    Assert.assertEquals(testAccount, dup.getOwner());
    Assert.assertTrue(Arrays.equals(tstMsk, dup.getAccessMask()));
    Assert.assertEquals(77777L, dup.getLifeStart());
    Assert.assertEquals(Long.MAX_VALUE, dup.getLifeEnd());
  }

  @Test
  public void testRemoveAccount()
      throws IOException, AccountCreationException, MetaDataLimitException, MetaDataCountException, ExecutionException {
    if (Boolean.valueOf(System.getProperty("enterprises.orbtial.evekit.model.unittest.skipbig", "false"))) { return; }

    // Setup account
    EveKitUserAccount userAccount = EveKitUserAccount.createNewUserAccount(true, true);
    SynchronizedEveAccount testAccount = SynchronizedEveAccount.createSynchronizedEveAccount(userAccount, "testaccount",
                                                                                             true);
    long testTime = OrbitalProperties.getCurrentTime();
    // Now create at least one of every model element attached to this account.
    int count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new Implant(TestBase.getUniqueRandomInteger());
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Implants");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new CharacterFleet(TestBase.getUniqueRandomLong(),
                                           TestBase.getRandomText(50),
                                           TestBase.getRandomLong(),
                                           TestBase.getRandomLong());
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterFleets");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new FleetInfo(TestBase.getUniqueRandomLong(),
                                      TestBase.getRandomBoolean(),
                                      TestBase.getRandomBoolean(),
                                      TestBase.getRandomBoolean(),
                                      TestBase.getRandomText(1000));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Fleet Info");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new FleetMember(TestBase.getUniqueRandomLong(),
                                        TestBase.getUniqueRandomInteger(),
                                        TestBase.getRandomLong(),
                                        TestBase.getRandomText(50),
                                        TestBase.getRandomText(50),
                                        TestBase.getRandomInt(),
                                        TestBase.getRandomInt(),
                                        TestBase.getRandomLong(),
                                        TestBase.getRandomLong(),
                                        TestBase.getRandomBoolean(),
                                        TestBase.getRandomLong());
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Fleet Members");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new FleetWing(TestBase.getUniqueRandomLong(),
                                      TestBase.getUniqueRandomLong(),
                                      TestBase.getRandomText(50));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Fleet Wings");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new FleetSquad(TestBase.getUniqueRandomLong(),
                                       TestBase.getUniqueRandomLong(),
                                       TestBase.getUniqueRandomLong(),
                                       TestBase.getRandomText(50));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Fleet Squads");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new Opportunity(TestBase.getUniqueRandomInteger(),
                                        TestBase.getRandomLong());
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Opportunities");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new Structure(TestBase.getUniqueRandomLong(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomLong(),
                                      TestBase.getRandomLong(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomText(50),
                                      TestBase.getRandomLong(),
                                      TestBase.getRandomLong(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomInt(),
                                      TestBase.getRandomLong());
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Structures");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new StructureService(TestBase.getUniqueRandomLong(),
                                             TestBase.getRandomText(50),
                                             TestBase.getRandomText(50));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Structure Services");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new JumpClone(TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(),
                                      TestBase.getRandomText(50), TestBase.getRandomText(50));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Jump Clones");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new JumpCloneImplant(TestBase.getUniqueRandomInteger(), TestBase.getRandomInt());
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Jump Clone Implants");
    count = TestBase.getRandomInt(1000) + 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CalendarEventAttendee(TestBase.getRandomInt(), TestBase.getRandomInt(),
                                                  TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CalendarEventAttendees");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterContactNotification(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomFloat(10), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterContactNotifications");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      String[] rts = new String[]{"alliance", "character", "corporation", "mailing_list"};
      int sel = TestBase.getRandomInt(5);
      CharacterMailMessage next = new CharacterMailMessage(
          TestBase.getRandomLong(),
          TestBase.getRandomInt(),
          TestBase.getRandomLong(),
          TestBase.getRandomText(50),
          TestBase.getRandomBoolean(),
          new HashSet<>(),
          new HashSet<>(),
          TestBase.getRandomText(1000));
      for (int j = 0; j < TestBase.getRandomInt(5) + 5; j++) {
        next.getLabels()
            .add(TestBase.getUniqueRandomInteger());
      }
      for (int j = 0; j < TestBase.getRandomInt(10) + 10; j++) {
        next.getRecipients()
            .add(new MailMessageRecipient(rts[TestBase.getRandomInt(rts.length)],
                                          TestBase.getRandomInt()));
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterMailMessages");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterMedal(
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterMedals");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterMedalGraphic(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterMedalGraphics");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterNotification(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomBoolean(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterNotifications");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterRole(TestBase.getRandomText(20), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterRoles");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheet(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomFloat(10));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterSheets");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterLocation(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterLocations");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetAttributes(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterSheetAttributes");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetSkillPoints(
          TestBase.getRandomLong(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterSheetSkillPoints");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterShip(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterShips");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterOnline(
          TestBase.getRandomBoolean(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterOnlines");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetJump(TestBase.getRandomLong(), TestBase.getRandomLong(),
                                               TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterSheetJump");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetClone(TestBase.getRandomLong(), TestBase.getRandomLong(),
                                                TestBase.getRandomText(50), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterSheetClone");
    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSkill(TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
                                           TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterSkills");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterTitle(TestBase.getRandomInt(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CharacterTitles");
    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ChatChannel(
          TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomText(50),
          TestBase.getRandomBoolean(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ChatChannels");
    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      String[] cats = new String[]{
          "allowed", "blocked", "muted", "operators"
      };
      int sel = TestBase.getRandomInt(5);
      int category = TestBase.getRandomInt(4);
      CachedData next = new ChatChannelMember(
          TestBase.getUniqueRandomInteger(), cats[category], TestBase.getUniqueRandomInteger(),
          TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ChatChannelMembers");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new LoyaltyPoints(
          TestBase.getUniqueRandomInteger(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created LoyaltyPoints");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MiningLedger(
          TestBase.getUniqueRandomLong(),
          TestBase.getRandomInt(),
          TestBase.getRandomInt(),
          TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MiningLedgers");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MiningExtraction(
          TestBase.getUniqueRandomInteger(),
          TestBase.getRandomLong(),
          TestBase.getRandomLong(),
          TestBase.getRandomLong(),
          TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MiningExtractions");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MiningObserver(
          TestBase.getUniqueRandomLong(),
          TestBase.getRandomText(50),
          TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MiningObservers");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MiningObservation(
          TestBase.getUniqueRandomLong(),
          TestBase.getRandomInt(),
          TestBase.getRandomInt(),
          TestBase.getRandomInt(),
          TestBase.getRandomLong(),
          TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MiningObservations");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Fitting(
          TestBase.getUniqueRandomInteger(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Fittings");

    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new FittingItem(
          TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created FittingItems");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MailingList(TestBase.getRandomText(50), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MailingLists");
    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MailLabel(TestBase.getRandomInt(), TestBase.getRandomInt(),
                                      TestBase.getRandomText(50), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MailLabels");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryColony(
          TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created PlanetaryColonies");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryPin(
          TestBase.getUniqueRandomInteger(), TestBase.getUniqueRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomFloat(50), TestBase.getRandomFloat(50),
          TestBase.getRandomFloat(50), new HashSet<>(), new HashSet<>());
      for (int j = 0; j < TestBase.getRandomInt(5) + 5; j++) {
        ((PlanetaryPin) next).getHeads()
                             .add(new PlanetaryPinHead(TestBase.getUniqueRandomInteger(),
                                                       TestBase.getRandomFloat(100),
                                                       TestBase.getRandomFloat(100)));
      }
      for (int j = 0; j < TestBase.getRandomInt(10) + 10; j++) {
        ((PlanetaryPin) next).getContents()
                             .add(new PlanetaryPinContent(TestBase.getUniqueRandomInteger(),
                                                          TestBase.getRandomLong()));
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created PlanetaryPins");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryLink(
          TestBase.getUniqueRandomInteger(), TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created PlanetaryLinks");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryRoute(
          TestBase.getUniqueRandomInteger(), TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(),
          TestBase.getUniqueRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomFloat(100), new ArrayList<>());
      for (int j = 0; j < TestBase.getRandomInt(5) + 5; j++) {
        ((PlanetaryRoute) next).getWaypoints()
                               .add(TestBase.getUniqueRandomLong());
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created PlanetaryRoutes");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ResearchAgent(
          TestBase.getRandomInt(), TestBase.getRandomFloat(1000), TestBase.getRandomFloat(1000),
          TestBase.getRandomLong(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ResearchData");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new SkillInQueue(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), i, TestBase.getRandomInt(),
          TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created SkillInQueue");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new UpcomingCalendarEvent(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created UpcomingCalendarEvent");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new AccountBalance(TestBase.getRandomInt(), TestBase.getRandomBigDecimal(100000000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created AccountBalance");

    // Create assets with multiple levels of containment. This is necessary to test proper bottom up deleting of assets.
    count = TestBase.getRandomInt(1000) + 200;
    int contained = TestBase.getRandomInt(100) + 5;
    Queue<Asset> assetParents = new LinkedList<>();
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Asset(
          TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), TestBase.getRandomText(50), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      if (i < contained) {
        assetParents.add((Asset) next);
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    int maxContained = TestBase.getRandomInt(1000) + 200;
    int currentContained = 0;
    while (!assetParents.isEmpty() && currentContained < maxContained) {
      Asset parent = assetParents.poll();
      count = TestBase.getRandomInt(5);
      for (int j = 0; j < count; j++) {
        assert parent != null;
        Asset child = new Asset(
            TestBase.getUniqueRandomLong(), parent.getItemID(), TestBase.getRandomText(50),
            TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(),
            TestBase.getRandomBoolean(), TestBase.getRandomText(50), TestBase.getRandomBoolean());
        assetParents.add(child);
        currentContained++;
        int sel = TestBase.getRandomInt(5);
        for (int k = 0; k < sel; k++) {
          child.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
        }
        child.setup(testAccount, testTime);
        CachedData.update(child);
      }
    }
    System.out.println("Created Assets");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      Contact next = new Contact(
          TestBase.getRandomText(30), TestBase.getRandomInt(), TestBase.getRandomFloat(100000000),
          TestBase.getRandomText(50), TestBase.getRandomBoolean(), TestBase.getRandomBoolean(),
          new HashSet<>());
      for (int j = 0; j < TestBase.getRandomInt(5) + 5; j++) {
        next.getLabels()
            .add(TestBase.getUniqueRandomLong());
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Contacts");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContactLabel(TestBase.getRandomText(30), TestBase.getRandomLong(),
                                         TestBase.getRandomText(30));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ContactLabels");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Blueprint(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Blueprints");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Location(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomDouble(10000),
          TestBase.getRandomDouble(10000),
          TestBase.getRandomDouble(10000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Locations");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContractItem(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ContractItems");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Contract(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomText(50),
          TestBase.getRandomBoolean(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomBigDecimal(100000000),
          TestBase.getRandomBigDecimal(100000000),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Contracts");

    count = TestBase.getRandomInt(200) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContractBid(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomBigDecimal(100000000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ContractBids");

    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new FacWarStats(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created FacWarStats");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new IndustryJob(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomBigDecimal(100000000),
          TestBase.getRandomInt(), (float) TestBase.getRandomDouble(1), TestBase.getRandomInt(),
          TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created IndustryJobsV2");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Kill(TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
                                 TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Kills");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new KillAttacker(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomFloat(10), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created KillAttackers");

    // Create kill items with multiple levels of containment. This is necessary to test proper bottom up deleting of kill items.
    count = TestBase.getRandomInt(3000) + 500;
    contained = TestBase.getRandomInt(300) + 150;
    Queue<KillItem> killItemParents = new LinkedList<>();
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new KillItem(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomLong(),
          TestBase.getRandomInt(), i, KillItem.TOP_LEVEL);
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      if (i < contained) {
        killItemParents.add((KillItem) next);
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    maxContained = TestBase.getRandomInt(1000) + 500;
    currentContained = 0;
    while (!killItemParents.isEmpty() && currentContained < maxContained) {
      KillItem parent = killItemParents.poll();
      count = TestBase.getRandomInt(5);
      for (int j = 0; j < count; j++) {
        assert parent != null;
        KillItem child = new KillItem(
            TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(), TestBase.getRandomInt(),
            TestBase.getRandomLong(), TestBase.getRandomLong(),
            TestBase.getRandomInt(), j, parent.getSequence());
        killItemParents.add(child);
        currentContained++;
        int sel = TestBase.getRandomInt(5);
        for (int k = 0; k < sel; k++) {
          child.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
        }
        child.setup(testAccount, testTime);
        CachedData.update(child);
      }
    }
    System.out.println("Created KillItems");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new KillVictim(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomDouble(10000),
          TestBase.getRandomDouble(10000), TestBase.getRandomDouble(10000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created KillVictims");

    count = TestBase.getRandomInt(200) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Location(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomDouble(1000),
          TestBase.getRandomDouble(1000), TestBase.getRandomDouble(1000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Locations");

    count = TestBase.getRandomInt(200) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MarketOrder(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomBoolean(), TestBase.getRandomLong(),
          TestBase.getRandomInt(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MarketOrders");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Standing(TestBase.getRandomText(30), TestBase.getRandomInt(), TestBase.getRandomFloat(10));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Standings");

    count = TestBase.getRandomInt(2000) + 2000;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new WalletJournal(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomText(50),
          TestBase.getRandomInt(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created WalletJournals");

    count = TestBase.getRandomInt(2000) + 2000;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new WalletTransaction(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomInt(),
          TestBase.getRandomLong(),
          TestBase.getRandomBoolean(), TestBase.getRandomBoolean(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created WalletTransactions");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContainerLog(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created ContainerLogs");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CorporationMedal(
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CorporationMedals");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Facility(
          TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Facilities");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CustomsOffice(
          TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(),
          TestBase.getRandomBoolean(), TestBase.getRandomText(50), TestBase.getRandomFloat(10),
          TestBase.getRandomFloat(10), TestBase.getRandomFloat(10),
          TestBase.getRandomFloat(10), TestBase.getRandomFloat(10), TestBase.getRandomFloat(10),
          TestBase.getRandomFloat(10));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Customs Offices");

    count = TestBase.getRandomInt(500) + 100;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CorporationMemberMedal(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomText(50),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CorporationMemberMedals");

    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CorporationSheet(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomFloat(10), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CorporationSheets");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CorporationTitle next = new CorporationTitle(TestBase.getRandomInt(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CorporationTitles");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CorporationTitleRole next = new CorporationTitleRole(TestBase.getRandomInt(), TestBase.getRandomText(50),
                                                           TestBase.getRandomBoolean(), TestBase.getRandomBoolean(),
                                                           TestBase.getRandomBoolean(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created CorporationTitleRoles");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Division(TestBase.getRandomBoolean(), TestBase.getRandomInt(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Divisions");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Fuel(TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Fuels");

    count = TestBase.getRandomInt(10) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Member(TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Memberss");

    count = TestBase.getRandomInt(10) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MemberLimit(TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MemberLimits");

    count = TestBase.getRandomInt(10) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MemberTitle(TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MemberTitles");

    count = TestBase.getRandomInt(500) + 100;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      MemberRole next = new MemberRole(TestBase.getRandomInt(), TestBase.getRandomText(50),
                                       TestBase.getRandomBoolean(), TestBase.getRandomBoolean(),
                                       TestBase.getRandomBoolean(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MemberRoles");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      MemberRoleHistory next = new MemberRoleHistory(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MemberRoleHistorys");

    count = TestBase.getRandomInt(500) + 100;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MemberTracking(
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created MemberTrackings");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Shareholder(
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Shareholders");

    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Starbase(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomBoolean(),
          TestBase.getRandomBoolean(), TestBase.getRandomBoolean(), TestBase.getRandomFloat(10),
          TestBase.getRandomFloat(10), TestBase.getRandomBoolean(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      CachedData.update(next);
    }
    System.out.println("Created Starbases");

    // Pre-clean count
    final SynchronizedEveAccount verify = testAccount;
    long remaining = EveKitUserAccountProvider.getFactory()
                                              .runTransaction(() -> {
                                                TypedQuery<Long> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      "SELECT count(c) FROM CachedData c where c.owner = :owner",
                                                                                                      Long.class);
                                                query.setParameter("owner", verify);
                                                return query.getSingleResult();
                                              });
    System.out.println("Number of elements before delete: " + remaining);

    // Delete all the elements we control
    System.out.println("Account deleted, starting removal.");
    CachedData.cleanup(testAccount, "CalendarEventAttendee");
    CachedData.cleanup(testAccount, "Capsuleer");
    CachedData.cleanup(testAccount, "CharacterContactNotification");
    CachedData.cleanup(testAccount, "CharacterFleet");
    CachedData.cleanup(testAccount, "CharacterMailMessage");
    CachedData.cleanup(testAccount, "CharacterMedal");
    CachedData.cleanup(testAccount, "CharacterMedalGraphic");
    CachedData.cleanup(testAccount, "CharacterNotification");
    CachedData.cleanup(testAccount, "CharacterRole");
    CachedData.cleanup(testAccount, "CharacterLocation");
    CachedData.cleanup(testAccount, "CharacterSheetAttributes");
    CachedData.cleanup(testAccount, "CharacterSheetSkillPoints");
    CachedData.cleanup(testAccount, "CharacterShip");
    CachedData.cleanup(testAccount, "CharacterOnline");
    CachedData.cleanup(testAccount, "CharacterSheet");
    CachedData.cleanup(testAccount, "CharacterSheetClone");
    CachedData.cleanup(testAccount, "CharacterSheetJump");
    CachedData.cleanup(testAccount, "CharacterSkill");
    CachedData.cleanup(testAccount, "CharacterTitle");
    CachedData.cleanup(testAccount, "ChatChannel");
    CachedData.cleanup(testAccount, "ChatChannelMember");
    CachedData.cleanup(testAccount, "Fitting");
    CachedData.cleanup(testAccount, "FittingItem");
    CachedData.cleanup(testAccount, "FleetInfo");
    CachedData.cleanup(testAccount, "FleetMember");
    CachedData.cleanup(testAccount, "FleetSquad");
    CachedData.cleanup(testAccount, "FleetWing");
    CachedData.cleanup(testAccount, "Implant");
    CachedData.cleanup(testAccount, "JumpClone");
    CachedData.cleanup(testAccount, "JumpCloneImplant");
    CachedData.cleanup(testAccount, "LoyaltyPoints");
    CachedData.cleanup(testAccount, "MailingList");
    CachedData.cleanup(testAccount, "MailLabel");
    CachedData.cleanup(testAccount, "MiningLedger");
    CachedData.cleanup(testAccount, "Opportunity");
    CachedData.cleanup(testAccount, "PlanetaryColony");
    CachedData.cleanup(testAccount, "PlanetaryLink");
    CachedData.cleanup(testAccount, "PlanetaryPin");
    CachedData.cleanup(testAccount, "PlanetaryRoute");
    CachedData.cleanup(testAccount, "ResearchAgent");
    CachedData.cleanup(testAccount, "SkillInQueue");
    CachedData.cleanup(testAccount, "UpcomingCalendarEvent");
    CachedData.cleanup(testAccount, "AccountBalance");
    CachedData.cleanup(testAccount, "Asset");
    CachedData.cleanup(testAccount, "Blueprint");
    CachedData.cleanup(testAccount, "Bookmark");
    CachedData.cleanup(testAccount, "Contact");
    CachedData.cleanup(testAccount, "ContactLabel");
    CachedData.cleanup(testAccount, "Contract");
    CachedData.cleanup(testAccount, "ContractBid");
    CachedData.cleanup(testAccount, "ContractItem");
    CachedData.cleanup(testAccount, "FacWarStats");
    CachedData.cleanup(testAccount, "IndustryJob");
    CachedData.cleanup(testAccount, "Kill");
    CachedData.cleanup(testAccount, "KillAttacker");
    CachedData.cleanup(testAccount, "KillItem");
    CachedData.cleanup(testAccount, "KillVictim");
    CachedData.cleanup(testAccount, "Location");
    CachedData.cleanup(testAccount, "MarketOrder");
    CachedData.cleanup(testAccount, "Standing");
    CachedData.cleanup(testAccount, "WalletJournal");
    CachedData.cleanup(testAccount, "WalletTransaction");
    CachedData.cleanup(testAccount, "ContainerLog");
    CachedData.cleanup(testAccount, "Corporation");
    CachedData.cleanup(testAccount, "CorporationMedal");
    CachedData.cleanup(testAccount, "CorporationMemberMedal");
    CachedData.cleanup(testAccount, "CorporationSheet");
    CachedData.cleanup(testAccount, "CorporationTitle");
    CachedData.cleanup(testAccount, "CorporationTitleRole");
    CachedData.cleanup(testAccount, "CustomsOffice");
    CachedData.cleanup(testAccount, "Division");
    CachedData.cleanup(testAccount, "Facility");
    CachedData.cleanup(testAccount, "Fuel");
    CachedData.cleanup(testAccount, "Member");
    CachedData.cleanup(testAccount, "MemberLimit");
    CachedData.cleanup(testAccount, "MemberTitle");
    CachedData.cleanup(testAccount, "MemberRole");
    CachedData.cleanup(testAccount, "MemberRoleHistory");
    CachedData.cleanup(testAccount, "MemberTracking");
    CachedData.cleanup(testAccount, "MiningExtraction");
    CachedData.cleanup(testAccount, "MiningObserver");
    CachedData.cleanup(testAccount, "MiningObservation");
    CachedData.cleanup(testAccount, "Shareholder");
    CachedData.cleanup(testAccount, "Starbase");
    CachedData.cleanup(testAccount, "Structure");
    CachedData.cleanup(testAccount, "StructureService");

    // Verify all elements have been deleted.
    System.out.println("Verifying delete worked properly.");
    remaining = EveKitUserAccountProvider.getFactory()
                                         .runTransaction(() -> {
                                           TypedQuery<Long> query = EveKitUserAccountProvider.getFactory()
                                                                                             .getEntityManager()
                                                                                             .createQuery(
                                                                                                 "SELECT count(c) FROM CachedData c where c.owner = :owner",
                                                                                                 Long.class);
                                           query.setParameter("owner", verify);
                                           return query.getSingleResult();
                                         });
    Assert.assertEquals(0, remaining);
  }

}
