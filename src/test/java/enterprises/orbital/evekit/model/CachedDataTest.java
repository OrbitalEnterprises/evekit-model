package enterprises.orbital.evekit.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import javax.persistence.TypedQuery;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccessKeyCreationException;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.AccountCreationException;
import enterprises.orbital.evekit.account.EveKitUserAccount;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.character.CalendarEventAttendee;
import enterprises.orbital.evekit.model.character.Capsuleer;
import enterprises.orbital.evekit.model.character.CharacterContactNotification;
import enterprises.orbital.evekit.model.character.CharacterMailMessage;
import enterprises.orbital.evekit.model.character.CharacterMailMessageBody;
import enterprises.orbital.evekit.model.character.CharacterMedal;
import enterprises.orbital.evekit.model.character.CharacterNotification;
import enterprises.orbital.evekit.model.character.CharacterNotificationBody;
import enterprises.orbital.evekit.model.character.CharacterRole;
import enterprises.orbital.evekit.model.character.CharacterSheet;
import enterprises.orbital.evekit.model.character.CharacterSheetBalance;
import enterprises.orbital.evekit.model.character.CharacterSheetClone;
import enterprises.orbital.evekit.model.character.CharacterSheetJump;
import enterprises.orbital.evekit.model.character.CharacterSkill;
import enterprises.orbital.evekit.model.character.CharacterSkillInTraining;
import enterprises.orbital.evekit.model.character.CharacterTitle;
import enterprises.orbital.evekit.model.character.ChatChannel;
import enterprises.orbital.evekit.model.character.ChatChannelMember;
import enterprises.orbital.evekit.model.character.Implant;
import enterprises.orbital.evekit.model.character.JumpClone;
import enterprises.orbital.evekit.model.character.JumpCloneImplant;
import enterprises.orbital.evekit.model.character.MailingList;
import enterprises.orbital.evekit.model.character.PlanetaryColony;
import enterprises.orbital.evekit.model.character.PlanetaryLink;
import enterprises.orbital.evekit.model.character.PlanetaryPin;
import enterprises.orbital.evekit.model.character.PlanetaryRoute;
import enterprises.orbital.evekit.model.character.ResearchAgent;
import enterprises.orbital.evekit.model.character.SkillInQueue;
import enterprises.orbital.evekit.model.character.UpcomingCalendarEvent;
import enterprises.orbital.evekit.model.common.AccountBalance;
import enterprises.orbital.evekit.model.common.Asset;
import enterprises.orbital.evekit.model.common.Blueprint;
import enterprises.orbital.evekit.model.common.Contact;
import enterprises.orbital.evekit.model.common.ContactLabel;
import enterprises.orbital.evekit.model.common.Contract;
import enterprises.orbital.evekit.model.common.ContractBid;
import enterprises.orbital.evekit.model.common.ContractItem;
import enterprises.orbital.evekit.model.common.FacWarStats;
import enterprises.orbital.evekit.model.common.IndustryJob;
import enterprises.orbital.evekit.model.common.Kill;
import enterprises.orbital.evekit.model.common.KillAttacker;
import enterprises.orbital.evekit.model.common.KillItem;
import enterprises.orbital.evekit.model.common.KillVictim;
import enterprises.orbital.evekit.model.common.Location;
import enterprises.orbital.evekit.model.common.MarketOrder;
import enterprises.orbital.evekit.model.common.Standing;
import enterprises.orbital.evekit.model.common.WalletJournal;
import enterprises.orbital.evekit.model.common.WalletTransaction;
import enterprises.orbital.evekit.model.corporation.ContainerLog;
import enterprises.orbital.evekit.model.corporation.Corporation;
import enterprises.orbital.evekit.model.corporation.CorporationMedal;
import enterprises.orbital.evekit.model.corporation.CorporationMemberMedal;
import enterprises.orbital.evekit.model.corporation.CorporationSheet;
import enterprises.orbital.evekit.model.corporation.CorporationTitle;
import enterprises.orbital.evekit.model.corporation.CustomsOffice;
import enterprises.orbital.evekit.model.corporation.Division;
import enterprises.orbital.evekit.model.corporation.Facility;
import enterprises.orbital.evekit.model.corporation.Fuel;
import enterprises.orbital.evekit.model.corporation.MemberSecurity;
import enterprises.orbital.evekit.model.corporation.MemberSecurityLog;
import enterprises.orbital.evekit.model.corporation.MemberTracking;
import enterprises.orbital.evekit.model.corporation.Role;
import enterprises.orbital.evekit.model.corporation.SecurityRole;
import enterprises.orbital.evekit.model.corporation.SecurityTitle;
import enterprises.orbital.evekit.model.corporation.Shareholder;
import enterprises.orbital.evekit.model.corporation.Starbase;
import enterprises.orbital.evekit.model.corporation.StarbaseDetail;

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
    throws IOException, AccountCreationException, MetaDataLimitException, MetaDataCountException, AccessKeyCreationException, ExecutionException {
    if (Boolean.valueOf(System.getProperty("enterprises.orbtial.evekit.model.unittest.skipbig", "false"))) { return; }

    // Setup account
    EveKitUserAccount userAccount = EveKitUserAccount.createNewUserAccount(true, true);
    SynchronizedEveAccount testAccount = SynchronizedEveAccount.createSynchronizedEveAccount(userAccount, "testaccount", true, true);
    long testTime = OrbitalProperties.getCurrentTime();
    // Now create at least one of every model element attached to this account.
    int count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new Implant(TestBase.getUniqueRandomInteger(), TestBase.getRandomText(20));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Implants");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new JumpClone(TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Jump Clones");
    count = TestBase.getRandomInt(5) + 1;
    for (int i = 0; i < count; i++) {
      CachedData next = new JumpCloneImplant(TestBase.getUniqueRandomInteger(), TestBase.getRandomInt(), TestBase.getRandomText(50));
      for (int j = 0; j < 1; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Jump Clone Implants");
    count = TestBase.getRandomInt(1000) + 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CalendarEventAttendee(TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CalendarEventAttendees");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = Capsuleer.getOrCreateCapsuleer(testAccount);
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next = CachedData.update(next);
    }
    System.out.println("Created Capsuleers");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterContactNotification(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterContactNotifications");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CharacterMailMessage next = new CharacterMailMessage(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomBoolean(), TestBase.getRandomInt());
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getToCharacterID().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getToListID().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterMailMessages");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterMailMessageBody(TestBase.getRandomLong(), TestBase.getRandomBoolean(), TestBase.getRandomText(1000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterMailMessageBodies");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterMedal(
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterMedals");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterNotification(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterNotifications");
    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterNotificationBody(
          TestBase.getRandomLong(), TestBase.getRandomBoolean(), TestBase.getRandomText(1000), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterNotificationBodies");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterRole(TestBase.getRandomText(20), TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterRoles");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheet(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterSheets");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetBalance(TestBase.getRandomBigDecimal(100000000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterSheetBalance");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetJump(TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterSheetJump");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSheetClone(TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterSheetClone");
    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSkill(TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterSkills");
    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterSkillInTraining(
          TestBase.getRandomBoolean(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterSkillInTraining");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CharacterTitle(TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CharacterTitles");
    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ChatChannel(
          TestBase.getUniqueRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomBoolean(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ChatChannels");
    count = TestBase.getRandomInt(50) + 50;
    for (int i = 0; i < count; i++) {
      String[] cats = new String[] {
          "allowed", "blocked", "muted", "operators"
      };
      int sel = TestBase.getRandomInt(5);
      int category = TestBase.getRandomInt(4);
      CachedData next = new ChatChannelMember(
          TestBase.getUniqueRandomLong(), cats[category], TestBase.getUniqueRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ChatChannelMembers");
    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MailingList(TestBase.getRandomText(50), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created MailingLists");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryColony(
          TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created PlanetaryColonies");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryPin(
          TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomDouble(50), TestBase.getRandomDouble(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created PlanetaryPins");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryLink(
          TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created PlanetaryLinks");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new PlanetaryRoute(
          TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created PlanetaryRoutes");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ResearchAgent(
          TestBase.getRandomInt(), TestBase.getRandomDouble(1000), TestBase.getRandomDouble(1000), TestBase.getRandomLong(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ResearchData");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new SkillInQueue(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), i, TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created SkillInQueue");
    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new UpcomingCalendarEvent(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomBoolean(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
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
      next = CachedData.update(next);
    }
    System.out.println("Created AccountBalance");

    // Create assets with multiple levels of containment. This is necessary to test proper bottom up deleting of assets.
    count = TestBase.getRandomInt(1000) + 200;
    int contained = TestBase.getRandomInt(100) + 5;
    Queue<Asset> assetParents = new LinkedList<Asset>();
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Asset(
          TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), TestBase.getRandomInt(), Asset.TOP_LEVEL);
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      if (i < contained) {
        assetParents.add((Asset) next);
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    int maxContained = TestBase.getRandomInt(1000) + 200;
    int currentContained = 0;
    while (!assetParents.isEmpty() && currentContained < maxContained) {
      Asset parent = assetParents.poll();
      count = TestBase.getRandomInt(5);
      for (int j = 0; j < count; j++) {
        Asset child = new Asset(
            TestBase.getUniqueRandomLong(), TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
            TestBase.getRandomBoolean(), TestBase.getRandomInt(), parent.getItemID());
        assetParents.add(child);
        currentContained++;
        int sel = TestBase.getRandomInt(5);
        for (int k = 0; k < sel; k++) {
          child.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
        }
        child.setup(testAccount, testTime);
        child = CachedData.update(child);
      }
    }
    System.out.println("Created Assets");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Contact(
          TestBase.getRandomText(30), TestBase.getRandomInt(), TestBase.getRandomText(30), TestBase.getRandomDouble(100000000), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Contacts");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContactLabel(TestBase.getRandomText(30), TestBase.getRandomLong(), TestBase.getRandomText(30));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ContactLabels");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Blueprint(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Blueprints");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Location(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomDouble(10000), TestBase.getRandomDouble(10000),
          TestBase.getRandomDouble(10000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Locations");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContractItem(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ContractItems");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Contract(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomBoolean(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomBigDecimal(100000000),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Contracts");

    count = TestBase.getRandomInt(200) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContractBid(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomBigDecimal(100000000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ContractBids");

    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new FacWarStats(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created FacWarStats");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new IndustryJob(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomBigDecimal(100000000),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomDouble(100000000), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created IndustryJobsV2");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Kill(TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Kills");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new KillAttacker(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomDouble(10), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomBoolean());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created KillAttackers");

    // Create kill items with multiple levels of containment. This is necessary to test proper bottom up deleting of kill items.
    count = TestBase.getRandomInt(3000) + 500;
    contained = TestBase.getRandomInt(300) + 150;
    Queue<KillItem> killItemParents = new LinkedList<KillItem>();
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new KillItem(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), i, KillItem.TOP_LEVEL);
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      if (i < contained) {
        killItemParents.add((KillItem) next);
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    maxContained = TestBase.getRandomInt(1000) + 500;
    currentContained = 0;
    while (!killItemParents.isEmpty() && currentContained < maxContained) {
      KillItem parent = killItemParents.poll();
      count = TestBase.getRandomInt(5);
      for (int j = 0; j < count; j++) {
        KillItem child = new KillItem(
            TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
            TestBase.getRandomBoolean(), j, parent.getSequence());
        killItemParents.add(child);
        currentContained++;
        int sel = TestBase.getRandomInt(5);
        for (int k = 0; k < sel; k++) {
          child.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
        }
        child.setup(testAccount, testTime);
        child = CachedData.update(child);
      }
    }
    System.out.println("Created KillItems");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new KillVictim(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created KillVictims");

    count = TestBase.getRandomInt(200) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Location(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomDouble(1000), TestBase.getRandomDouble(1000), TestBase.getRandomDouble(1000));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Locations");

    count = TestBase.getRandomInt(200) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MarketOrder(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomBoolean(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created MarketOrders");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Standing(TestBase.getRandomText(30), TestBase.getRandomInt(), TestBase.getRandomText(30), TestBase.getRandomDouble(10));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Standings");

    count = TestBase.getRandomInt(2000) + 2000;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new WalletJournal(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomBigDecimal(100000000), TestBase.getRandomLong(), TestBase.getRandomLong(),
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created WalletJournals");

    count = TestBase.getRandomInt(2000) + 2000;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new WalletTransaction(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomBigDecimal(100000000), TestBase.getRandomInt(), TestBase.getRandomLong(),
          TestBase.getRandomBoolean(), TestBase.getRandomBoolean(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created WalletTransactions");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new ContainerLog(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created ContainerLogs");

    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = Corporation.getOrCreateCorporation(testAccount);
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Corporations");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CorporationMedal(
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CorporationMedals");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Facility(
          TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomDouble(10));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Facilities");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CustomsOffice(
          TestBase.getUniqueRandomLong(), TestBase.getRandomInt(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomBoolean(),
          TestBase.getRandomBoolean(), TestBase.getRandomDouble(10), TestBase.getRandomDouble(10), TestBase.getRandomDouble(10), TestBase.getRandomDouble(10),
          TestBase.getRandomDouble(10), TestBase.getRandomDouble(10), TestBase.getRandomDouble(10), TestBase.getRandomDouble(10));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Customs Offices");

    count = TestBase.getRandomInt(500) + 100;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CorporationMemberMedal(
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CorporationMemberMedals");

    count = 1;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new CorporationSheet(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomText(50), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomDouble(100000000), TestBase.getRandomText(50),
          TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CorporationSheets");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CorporationTitle next = new CorporationTitle(TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRoles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRolesAtBase().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRolesAtHQ().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRolesAtOther().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRoles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRolesAtBase().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRolesAtHQ().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRolesAtOther().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created CorporationTitles");

    count = TestBase.getRandomInt(10) + 5;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Division(TestBase.getRandomBoolean(), TestBase.getRandomInt(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
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
      next = CachedData.update(next);
    }
    System.out.println("Created Fuels");

    count = TestBase.getRandomInt(500) + 100;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      MemberSecurity next = new MemberSecurity(TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRoles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRolesAtBase().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRolesAtHQ().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getGrantableRolesAtOther().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRoles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRolesAtBase().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRolesAtHQ().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getRolesAtOther().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getTitles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created MemberSecuritys");

    count = TestBase.getRandomInt(2000) + 500;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      MemberSecurityLog next = new MemberSecurityLog(
          TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomText(50));
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getNewRoles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < TestBase.getRandomInt(5) + 2; j++) {
        next.getOldRoles().add(TestBase.getRandomLong());
      }
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created MemberSecurityLogs");

    count = TestBase.getRandomInt(500) + 100;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new MemberTracking(
          TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50),
          TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomLong(),
          TestBase.getRandomText(50), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created MemberTrackings");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Role(TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Roles");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new SecurityRole(TestBase.getRandomLong(), TestBase.getRandomText(50));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created SecurityRoles");

    count = TestBase.getRandomInt(100) + 20;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new SecurityTitle(TestBase.getRandomLong(), TestBase.getRandomText(100));
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created SecurityTitles");

    count = TestBase.getRandomInt(1000) + 200;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Shareholder(
          TestBase.getRandomLong(), TestBase.getRandomBoolean(), TestBase.getRandomLong(), TestBase.getRandomText(50), TestBase.getRandomText(50),
          TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Shareholders");

    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new Starbase(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created Starbases");

    count = TestBase.getRandomInt(100) + 50;
    for (int i = 0; i < count; i++) {
      int sel = TestBase.getRandomInt(5);
      CachedData next = new StarbaseDetail(
          TestBase.getRandomLong(), TestBase.getRandomInt(), TestBase.getRandomLong(), TestBase.getRandomLong(), TestBase.getRandomInt(),
          TestBase.getRandomInt(), TestBase.getRandomBoolean(), TestBase.getRandomBoolean(), TestBase.getRandomLong(), TestBase.getRandomBoolean(),
          TestBase.getRandomInt(), TestBase.getRandomBoolean(), TestBase.getRandomInt(), TestBase.getRandomBoolean(), TestBase.getRandomInt(),
          TestBase.getRandomBoolean(), TestBase.getRandomInt());
      for (int j = 0; j < sel; j++) {
        next.setMetaData(TestBase.getRandomText(30), TestBase.getRandomText(30));
      }
      next.setup(testAccount, testTime);
      next = CachedData.update(next);
    }
    System.out.println("Created StarbaseDetails");

    // Pre-clean count
    final SynchronizedEveAccount verify = testAccount;
    long remaining = EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Long>() {
      @Override
      public Long run() throws Exception {
        TypedQuery<Long> query = EveKitUserAccountProvider.getFactory().getEntityManager()
            .createQuery("SELECT count(c) FROM CachedData c where c.owner = :owner", Long.class);
        query.setParameter("owner", verify);
        return query.getSingleResult();
      }
    });
    System.out.println("Number of elements before delete: " + remaining);

    // Delete all the elements we control
    System.out.println("Account deleted, starting removal.");
    CachedData.cleanup(testAccount, "CalendarEventAttendee");
    CachedData.cleanup(testAccount, "Capsuleer");
    CachedData.cleanup(testAccount, "CharacterContactNotification");
    CachedData.cleanup(testAccount, "CharacterMailMessage");
    CachedData.cleanup(testAccount, "CharacterMailMessageBody");
    CachedData.cleanup(testAccount, "CharacterMedal");
    CachedData.cleanup(testAccount, "CharacterNotification");
    CachedData.cleanup(testAccount, "CharacterNotificationBody");
    CachedData.cleanup(testAccount, "CharacterRole");
    CachedData.cleanup(testAccount, "CharacterSheet");
    CachedData.cleanup(testAccount, "CharacterSheetBalance");
    CachedData.cleanup(testAccount, "CharacterSheetClone");
    CachedData.cleanup(testAccount, "CharacterSheetJump");
    CachedData.cleanup(testAccount, "CharacterSkill");
    CachedData.cleanup(testAccount, "CharacterSkillInTraining");
    CachedData.cleanup(testAccount, "CharacterTitle");
    CachedData.cleanup(testAccount, "ChatChannel");
    CachedData.cleanup(testAccount, "ChatChannelMember");
    CachedData.cleanup(testAccount, "Implant");
    CachedData.cleanup(testAccount, "JumpClone");
    CachedData.cleanup(testAccount, "JumpCloneImplant");
    CachedData.cleanup(testAccount, "MailingList");
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
    CachedData.cleanup(testAccount, "CustomsOffice");
    CachedData.cleanup(testAccount, "Division");
    CachedData.cleanup(testAccount, "Facility");
    CachedData.cleanup(testAccount, "Fuel");
    CachedData.cleanup(testAccount, "MemberSecurity");
    CachedData.cleanup(testAccount, "MemberSecurityLog");
    CachedData.cleanup(testAccount, "MemberTracking");
    CachedData.cleanup(testAccount, "Outpost");
    CachedData.cleanup(testAccount, "OutpostServiceDetail");
    CachedData.cleanup(testAccount, "Role");
    CachedData.cleanup(testAccount, "SecurityRole");
    CachedData.cleanup(testAccount, "SecurityTitle");
    CachedData.cleanup(testAccount, "Shareholder");
    CachedData.cleanup(testAccount, "Starbase");
    CachedData.cleanup(testAccount, "StarbaseDetail");

    // Verify all elements have been deleted.
    System.out.println("Verifying delete worked properly.");
    remaining = EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Long>() {
      @Override
      public Long run() throws Exception {
        TypedQuery<Long> query = EveKitUserAccountProvider.getFactory().getEntityManager()
            .createQuery("SELECT count(c) FROM CachedData c where c.owner = :owner", Long.class);
        query.setParameter("owner", verify);
        return query.getSingleResult();
      }
    });
    Assert.assertEquals(0, remaining);
  }

}
