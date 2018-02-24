package enterprises.orbital.evekit.model.character;

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

public class JumpCloneTest extends AbstractModelTester<JumpClone> {

  private final int jumpCloneID = TestBase.getRandomInt(100000000);
  private final long locationID = TestBase.getRandomInt(100000000);
  private final String cloneName = "test clone";
  private final String locationType = "test type";

  final ClassUnderTestConstructor<JumpClone> eol = () -> new JumpClone(jumpCloneID, locationID, cloneName,
                                                                       locationType);

  final ClassUnderTestConstructor<JumpClone> live = () -> new JumpClone(jumpCloneID, locationID + 1, cloneName + " 1",
                                                                        locationType);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new JumpClone[]{
        new JumpClone(jumpCloneID + 1, locationID, cloneName, locationType),
        new JumpClone(jumpCloneID, locationID + 1, cloneName, locationType),
        new JumpClone(jumpCloneID, locationID, cloneName + " 1", locationType),
        new JumpClone(jumpCloneID, locationID, cloneName, locationType + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> JumpClone.get(account, time, jumpCloneID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - clones for a different account
    // - clones not live at the given time
    JumpClone existing;
    Map<Integer, JumpClone> listCheck = new HashMap<>();

    existing = new JumpClone(jumpCloneID, locationID, cloneName, locationType);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jumpCloneID, existing);

    existing = new JumpClone(jumpCloneID + 10, locationID + 10, cloneName + " 10", locationType + "10");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jumpCloneID + 10, existing);

    // Associated with different account
    existing = new JumpClone(jumpCloneID, locationID, cloneName, locationType);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new JumpClone(jumpCloneID + 3, locationID + 3, cloneName + " 3", locationType + "3");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new JumpClone(jumpCloneID + 4, locationID + 4, cloneName + " 4", locationType + "4");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<JumpClone> result = CachedData.retrieveAll(8888L,
                                                    (contid, at) -> JumpClone.accessQuery(testAccount, contid, 1000,
                                                                                          false, at,
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any(),
                                                                                          AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (JumpClone next : result) {
      int jumpCloneID = next.getJumpCloneID();
      Assert.assertTrue(listCheck.containsKey(jumpCloneID));
      Assert.assertEquals(listCheck.get(jumpCloneID), next);
    }

  }

}
