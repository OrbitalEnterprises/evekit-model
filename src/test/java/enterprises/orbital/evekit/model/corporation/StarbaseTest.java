package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarbaseTest extends AbstractModelTester<Starbase> {

  private final long starbaseID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);
  private final int systemID = TestBase.getRandomInt(100000000);
  private final int moonID = TestBase.getRandomInt(100000000);
  private final String state = TestBase.getRandomText(50);
  private final long unanchorAt = TestBase.getRandomLong();
  private final long reinforcedUntil = TestBase.getRandomLong();
  private final long onlinedSince = TestBase.getRandomLong();
  private final String fuelBayView = TestBase.getRandomText(50);
  private final String fuelBayTake = TestBase.getRandomText(50);
  private final String anchor = TestBase.getRandomText(50);
  private final String unanchor = TestBase.getRandomText(50);
  private final String online = TestBase.getRandomText(50);
  private final String offline = TestBase.getRandomText(50);
  private final boolean allowCorporationMembers = TestBase.getRandomBoolean();
  private final boolean allowAllianceMembers = TestBase.getRandomBoolean();
  private final boolean useAllianceStandings = TestBase.getRandomBoolean();
  private final float attackStandingThreshold = TestBase.getRandomFloat(10);
  private final float attackSecurityStatusThreshold = TestBase.getRandomFloat(10);
  private final boolean attackIfOtherSecurityStatusDropping = TestBase.getRandomBoolean();
  private final boolean attackIfAtWar = TestBase.getRandomBoolean();

  final ClassUnderTestConstructor<Starbase> eol = () -> new Starbase(starbaseID, typeID, systemID, moonID, state,
                                                                     unanchorAt, reinforcedUntil, onlinedSince,
                                                                     fuelBayView, fuelBayTake, anchor, unanchor, online,
                                                                     offline, allowCorporationMembers,
                                                                     allowAllianceMembers, useAllianceStandings,
                                                                     attackStandingThreshold,
                                                                     attackSecurityStatusThreshold,
                                                                     attackIfOtherSecurityStatusDropping,
                                                                     attackIfAtWar);

  final ClassUnderTestConstructor<Starbase> live = () -> new Starbase(starbaseID, typeID, systemID + 1, moonID, state,
                                                                      unanchorAt, reinforcedUntil, onlinedSince,
                                                                      fuelBayView, fuelBayTake, anchor, unanchor,
                                                                      online, offline, allowCorporationMembers,
                                                                      allowAllianceMembers, useAllianceStandings,
                                                                      attackStandingThreshold,
                                                                      attackSecurityStatusThreshold,
                                                                      attackIfOtherSecurityStatusDropping,
                                                                      attackIfAtWar);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Starbase[]{
        new Starbase(starbaseID + 1, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID + 1, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID + 1, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID + 1, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state + "1", unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt + 1, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil + 1, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince + 1,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView + "1", fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake + "1", anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor + "1", unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor + "1", online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online + "1", offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline + "1", allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, !allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     !allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                     attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, !useAllianceStandings, attackStandingThreshold,
                     attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold + 1,
                     attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                     attackSecurityStatusThreshold + 1, attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     !attackIfOtherSecurityStatusDropping, attackIfAtWar),
        new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                     fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                     allowAllianceMembers, useAllianceStandings, attackStandingThreshold, attackSecurityStatusThreshold,
                     attackIfOtherSecurityStatusDropping, !attackIfAtWar)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STARBASE_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Starbase.get(account, time, starbaseID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - bases for a different account
    // - bases not live at the given time
    Starbase existing;
    Map<Long, Starbase> listCheck = new HashMap<>();

    existing = new Starbase(starbaseID, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                            fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                            allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                            attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(starbaseID, existing);

    existing = new Starbase(starbaseID + 1, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                            fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                            allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                            attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(starbaseID + 1, existing);

    // Associated with different account
    existing = new Starbase(starbaseID + 2, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                            fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                            allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                            attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Starbase(starbaseID + 3, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                            fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                            allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                            attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Starbase(starbaseID + 4, typeID, systemID, moonID, state, unanchorAt, reinforcedUntil, onlinedSince,
                            fuelBayView, fuelBayTake, anchor, unanchor, online, offline, allowCorporationMembers,
                            allowAllianceMembers, useAllianceStandings, attackStandingThreshold,
                            attackSecurityStatusThreshold, attackIfOtherSecurityStatusDropping, attackIfAtWar);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Starbase> result = CachedData.retrieveAll(8888L,
                                                   (contid, at) -> Starbase.accessQuery(testAccount, contid, 1000,
                                                                                        false, at,
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any(),
                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Starbase next : result) {
      long starbaseID = next.getStarbaseID();
      Assert.assertTrue(listCheck.containsKey(starbaseID));
      Assert.assertEquals(listCheck.get(starbaseID), next);
    }

  }

}
