package enterprises.orbital.evekit.model.corporation;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StructureServiceTest extends AbstractModelTester<StructureService> {
  private final long structureID = TestBase.getRandomLong();
  private final String name = TestBase.getRandomText(50);
  private final String state = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<StructureService> eol = () -> new StructureService(structureID, name, state);

  final ClassUnderTestConstructor<StructureService> live = () -> new StructureService(structureID, name, state + "1");

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new StructureService[]{
        new StructureService(structureID + 1, name, state),
        new StructureService(structureID, name + "1", state),
        new StructureService(structureID, name, state + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_STRUCTURES));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> StructureService.get(account, time, structureID, name));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - services for a different account
    // - services not live at the given time
    StructureService existing;
    List<StructureService> listCheck = new ArrayList<>();

    existing = new StructureService(structureID, name, state);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    existing = new StructureService(structureID + 1, name, state);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.add(existing);

    // Associated with different account
    existing = new StructureService(structureID + 2, name, state);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new StructureService(structureID + 3, name, state);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new StructureService(structureID + 4, name, state);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<StructureService> result = CachedData.retrieveAll(8888L,
                                                           (contid, at) -> StructureService.accessQuery(testAccount,
                                                                                                        contid, 1000,
                                                                                                        false, at,
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (int i = 0; i < listCheck.size(); i++) {
      Assert.assertEquals(listCheck.get(i), result.get(i));
    }

  }

}
