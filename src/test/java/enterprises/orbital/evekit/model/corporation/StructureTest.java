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

public class StructureTest extends AbstractModelTester<Structure> {

  private final long structureID = TestBase.getRandomLong();
  private final int corporationID = TestBase.getRandomInt();
  private final long fuelExpires = TestBase.getRandomLong();
  private final long nextReinforceApply = TestBase.getRandomLong();
  private final int nextReinforceHour = TestBase.getRandomInt();
  private final int nextReinforceWeekday = TestBase.getRandomInt();
  private final int profileID = TestBase.getRandomInt();
  private final int reinforceHour = TestBase.getRandomInt();
  private final int reinforceWeekday = TestBase.getRandomInt();
  private final String state = TestBase.getRandomText(50);
  private final long stateTimerEnd = TestBase.getRandomLong();
  private final long stateTimerStart = TestBase.getRandomLong();
  private final int systemID = TestBase.getRandomInt();
  private final int typeID = TestBase.getRandomInt();
  private final long unanchorsAt = TestBase.getRandomLong();

  final ClassUnderTestConstructor<Structure> eol = () -> new Structure(structureID, corporationID, fuelExpires,
                                                                       nextReinforceApply, nextReinforceHour,
                                                                       nextReinforceWeekday, profileID, reinforceHour,
                                                                       reinforceWeekday, state, stateTimerEnd,
                                                                       stateTimerStart, systemID, typeID, unanchorsAt);

  final ClassUnderTestConstructor<Structure> live = () -> new Structure(structureID, corporationID + 1, fuelExpires,
                                                                        nextReinforceApply, nextReinforceHour,
                                                                        nextReinforceWeekday, profileID, reinforceHour,
                                                                        reinforceWeekday, state, stateTimerEnd,
                                                                        stateTimerStart, systemID, typeID, unanchorsAt);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Structure[]{
        new Structure(structureID + 1, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID + 1, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires + 1, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply + 1, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour + 1,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday + 1, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID + 1, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour + 1, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday + 1, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state + "1", stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd + 1,
                      stateTimerStart, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart + 1, systemID, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID + 1, typeID, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID + 1, unanchorsAt),
        new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                      nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                      stateTimerStart, systemID, typeID, unanchorsAt + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STRUCTURES));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Structure.get(account, time, structureID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - structures for a different account
    // - structures not live at the given time
    Structure existing;
    Map<Long, Structure> listCheck = new HashMap<>();

    existing = new Structure(structureID, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                             nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                             stateTimerStart, systemID, typeID, unanchorsAt);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(structureID, existing);

    existing = new Structure(structureID + 1, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                             nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                             stateTimerStart, systemID, typeID, unanchorsAt);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(structureID + 1, existing);

    // Associated with different account
    existing = new Structure(structureID + 2, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                             nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                             stateTimerStart, systemID, typeID, unanchorsAt);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Structure(structureID + 3, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                             nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                             stateTimerStart, systemID, typeID, unanchorsAt);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Structure(structureID + 4, corporationID, fuelExpires, nextReinforceApply, nextReinforceHour,
                             nextReinforceWeekday, profileID, reinforceHour, reinforceWeekday, state, stateTimerEnd,
                             stateTimerStart, systemID, typeID, unanchorsAt);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<Structure> result = CachedData.retrieveAll(8888L,
                                                    (contid, at) -> Structure.accessQuery(testAccount, contid, 1000,
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
                                                                                          AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (Structure next : result) {
      long structureID = next.getStructureID();
      Assert.assertTrue(listCheck.containsKey(structureID));
      Assert.assertEquals(listCheck.get(structureID), next);
    }

  }

}
