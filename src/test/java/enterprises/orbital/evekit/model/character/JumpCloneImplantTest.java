package enterprises.orbital.evekit.model.character;

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
import enterprises.orbital.evekit.model.character.JumpCloneImplant;

public class JumpCloneImplantTest extends AbstractModelTester<JumpCloneImplant> {
  final int                                         jumpCloneID = TestBase.getRandomInt(100000000);
  final int                                         typeID      = TestBase.getRandomInt(100000000);
  final String                                      typeName    = "test implant";

  final ClassUnderTestConstructor<JumpCloneImplant> eol         = new ClassUnderTestConstructor<JumpCloneImplant>() {

                                                                  @Override
                                                                  public JumpCloneImplant getCUT() {
                                                                    return new JumpCloneImplant(jumpCloneID, typeID, typeName);
                                                                  }

                                                                };

  final ClassUnderTestConstructor<JumpCloneImplant> live        = new ClassUnderTestConstructor<JumpCloneImplant>() {
                                                                  @Override
                                                                  public JumpCloneImplant getCUT() {
                                                                    return new JumpCloneImplant(jumpCloneID, typeID, typeName + " 2");
                                                                  }

                                                                };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<JumpCloneImplant>() {

      @Override
      public JumpCloneImplant[] getVariants() {
        return new JumpCloneImplant[] {
            new JumpCloneImplant(jumpCloneID + 1, typeID, typeName), new JumpCloneImplant(jumpCloneID, typeID + 1, typeName),
            new JumpCloneImplant(jumpCloneID, typeID, typeName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<JumpCloneImplant>() {

      @Override
      public JumpCloneImplant getModel(SynchronizedEveAccount account, long time) {
        return JumpCloneImplant.get(account, time, jumpCloneID, typeID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - implants for a different account
    // - implants not live at the given time
    JumpCloneImplant existing;
    Map<Integer, Map<Integer, JumpCloneImplant>> listCheck = new HashMap<Integer, Map<Integer, JumpCloneImplant>>();

    existing = new JumpCloneImplant(jumpCloneID, typeID, typeName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jumpCloneID, new HashMap<Integer, JumpCloneImplant>());
    listCheck.get(jumpCloneID).put(typeID, existing);

    existing = new JumpCloneImplant(jumpCloneID + 10, typeID + 10, typeName + " 1");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(jumpCloneID + 10, new HashMap<Integer, JumpCloneImplant>());
    listCheck.get(jumpCloneID + 10).put(typeID + 10, existing);

    // Associated with different account
    existing = new JumpCloneImplant(jumpCloneID, typeID, typeName);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new JumpCloneImplant(jumpCloneID + 3, typeID + 3, typeName + " 3");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new JumpCloneImplant(jumpCloneID + 4, typeID + 4, typeName + " 4");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<JumpCloneImplant> result = JumpCloneImplant.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (JumpCloneImplant next : result) {
      int jumpCloneID = next.getJumpCloneID();
      int typeID = next.getTypeID();
      Assert.assertTrue(listCheck.containsKey(jumpCloneID));
      Assert.assertTrue(listCheck.get(jumpCloneID).containsKey(typeID));
      Assert.assertEquals(listCheck.get(jumpCloneID).get(typeID), next);
    }

  }

}
