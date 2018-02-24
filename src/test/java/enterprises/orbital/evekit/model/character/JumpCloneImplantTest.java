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

public class JumpCloneImplantTest extends AbstractModelTester<JumpCloneImplant> {
  private final int jumpCloneID = TestBase.getRandomInt(100000000);
  private final int typeID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<JumpCloneImplant> eol = () -> new JumpCloneImplant(jumpCloneID, typeID);

  final ClassUnderTestConstructor<JumpCloneImplant> live = () -> new JumpCloneImplant(jumpCloneID, typeID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new JumpCloneImplant[]{
        new JumpCloneImplant(jumpCloneID + 1, typeID),
        new JumpCloneImplant(jumpCloneID, typeID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> JumpCloneImplant.get(account, time, jumpCloneID, typeID));
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - implants for a different account
    // - implants not live at the given time
    JumpCloneImplant existing;
    Map<Integer, Map<Integer, JumpCloneImplant>> listCheck = new HashMap<>();

    existing = new JumpCloneImplant(jumpCloneID, typeID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jumpCloneID, new HashMap<>());
    listCheck.get(jumpCloneID)
             .put(typeID, existing);

    existing = new JumpCloneImplant(jumpCloneID + 10, typeID + 10);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jumpCloneID + 10, new HashMap<>());
    listCheck.get(jumpCloneID + 10)
             .put(typeID + 10, existing);

    // Associated with different account
    existing = new JumpCloneImplant(jumpCloneID, typeID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new JumpCloneImplant(jumpCloneID + 3, typeID + 3);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new JumpCloneImplant(jumpCloneID + 4, typeID + 4);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<JumpCloneImplant> result = CachedData.retrieveAll(8888L,
                                                           (contid, at) -> JumpCloneImplant.accessQuery(testAccount,
                                                                                                        contid, 1000,
                                                                                                        false, at,
                                                                                                        AttributeSelector.any(),
                                                                                                        AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (JumpCloneImplant next : result) {
      int jumpCloneID = next.getJumpCloneID();
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(jumpCloneID));
      Assert.assertTrue(listCheck.get(jumpCloneID)
                                 .containsKey(typeID));
      Assert.assertEquals(listCheck.get(jumpCloneID)
                                   .get(typeID), next);
    }

  }

}
