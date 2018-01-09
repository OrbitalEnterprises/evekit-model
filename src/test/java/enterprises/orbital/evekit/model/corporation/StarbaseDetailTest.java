package enterprises.orbital.evekit.model.corporation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;

public class StarbaseDetailTest extends AbstractModelTester<StarbaseDetail> {

  final long                                      itemID                   = TestBase.getRandomInt(100000000);
  final int                                       state                    = TestBase.getRandomInt(100000000);
  final long                                      stateTimestamp           = TestBase.getRandomInt(100000000);
  final long                                      onlineTimestamp          = TestBase.getRandomInt(100000000);
  final int                                       usageFlags               = TestBase.getRandomInt(100000000);
  final int                                       deployFlags              = TestBase.getRandomInt(100000000);
  final boolean                                   allowAllianceMembers     = true;
  final boolean                                   allowCorporationMembers  = true;
  final long                                      useStandingsFrom         = TestBase.getRandomInt(100000000);
  final boolean                                   onAggressionEnabled      = true;
  final int                                       onAggressionStanding     = TestBase.getRandomInt(100000000);
  final boolean                                   onCorporationWarEnabled  = true;
  final int                                       onCorporationWarStanding = TestBase.getRandomInt(100000000);
  final boolean                                   onStandingDropEnabled    = true;
  final int                                       onStandingDropStanding   = TestBase.getRandomInt(100000000);
  final boolean                                   onStatusDropEnabled      = true;
  final int                                       onStatusDropStanding     = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<StarbaseDetail> eol                      = new ClassUnderTestConstructor<StarbaseDetail>() {

                                                                             @Override
                                                                             public StarbaseDetail getCUT() {
                                                                               return new StarbaseDetail(
                                                                                   itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags,
                                                                                   allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                                                                                   onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled,
                                                                                   onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                                                                                   onStatusDropEnabled, onStatusDropStanding);
                                                                             }

                                                                           };

  final ClassUnderTestConstructor<StarbaseDetail> live                     = new ClassUnderTestConstructor<StarbaseDetail>() {
                                                                             @Override
                                                                             public StarbaseDetail getCUT() {
                                                                               return new StarbaseDetail(
                                                                                   itemID, state + 1, stateTimestamp, onlineTimestamp, usageFlags, deployFlags,
                                                                                   allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                                                                                   onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled,
                                                                                   onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                                                                                   onStatusDropEnabled, onStatusDropStanding);
                                                                             }

                                                                           };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<StarbaseDetail>() {

      @Override
      public StarbaseDetail[] getVariants() {
        return new StarbaseDetail[] {
            new StarbaseDetail(
                itemID + 1, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state + 1, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp + 1, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp + 1, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags + 1, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags + 1, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, !allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, !allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom + 1,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                !onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding + 1, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, !onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding + 1, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, !onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding + 1,
                onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                !onStatusDropEnabled, onStatusDropStanding),
            new StarbaseDetail(
                itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
                onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
                onStatusDropEnabled, onStatusDropStanding + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<StarbaseDetail>() {

      @Override
      public StarbaseDetail getModel(SynchronizedEveAccount account, long time) {
        return StarbaseDetail.get(account, time, itemID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - details for a different account
    // - details not live at the given time
    StarbaseDetail existing;
    Map<Long, StarbaseDetail> listCheck = new HashMap<Long, StarbaseDetail>();

    existing = new StarbaseDetail(
        itemID, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
        onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
        onStatusDropEnabled, onStatusDropStanding);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID, existing);

    existing = new StarbaseDetail(
        itemID + 1, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
        onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
        onStatusDropEnabled, onStatusDropStanding);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(itemID + 1, existing);

    // Associated with different account
    existing = new StarbaseDetail(
        itemID + 2, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
        onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
        onStatusDropEnabled, onStatusDropStanding);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new StarbaseDetail(
        itemID + 3, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
        onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
        onStatusDropEnabled, onStatusDropStanding);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new StarbaseDetail(
        itemID + 4, state, stateTimestamp, onlineTimestamp, usageFlags, deployFlags, allowAllianceMembers, allowCorporationMembers, useStandingsFrom,
        onAggressionEnabled, onAggressionStanding, onCorporationWarEnabled, onCorporationWarStanding, onStandingDropEnabled, onStandingDropStanding,
        onStatusDropEnabled, onStatusDropStanding);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<StarbaseDetail> result = StarbaseDetail.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (StarbaseDetail next : result) {
      long itemID = next.getItemID();
      Assert.assertTrue(listCheck.containsKey(itemID));
      Assert.assertEquals(listCheck.get(itemID), next);
    }

  }

}
