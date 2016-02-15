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
import enterprises.orbital.evekit.model.character.JumpClone;

public class JumpCloneTest extends AbstractModelTester<JumpClone> {

  final int                                  jumpCloneID = TestBase.getRandomInt(100000000);
  final int                                  typeID      = TestBase.getRandomInt(100000000);
  final long                                 locationID  = TestBase.getRandomInt(100000000);
  final String                               cloneName   = "test clone";

  final ClassUnderTestConstructor<JumpClone> eol         = new ClassUnderTestConstructor<JumpClone>() {

                                                           @Override
                                                           public JumpClone getCUT() {
                                                             return new JumpClone(jumpCloneID, typeID, locationID, cloneName);
                                                           }

                                                         };

  final ClassUnderTestConstructor<JumpClone> live        = new ClassUnderTestConstructor<JumpClone>() {
                                                           @Override
                                                           public JumpClone getCUT() {
                                                             return new JumpClone(jumpCloneID, typeID + 1, locationID + 1, cloneName + " 1");
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<JumpClone>() {

      @Override
      public JumpClone[] getVariants() {
        return new JumpClone[] {
            new JumpClone(jumpCloneID + 1, typeID, locationID, cloneName), new JumpClone(jumpCloneID, typeID + 1, locationID, cloneName),
            new JumpClone(jumpCloneID, typeID, locationID + 1, cloneName), new JumpClone(jumpCloneID, typeID, locationID, cloneName + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<JumpClone>() {

      @Override
      public JumpClone getModel(SynchronizedEveAccount account, long time) {
        return JumpClone.get(account, time, jumpCloneID);
      }

    });
  }

  @Test
  public void testGetAll() throws Exception {
    // Should exclude:
    // - clones for a different account
    // - clones not live at the given time
    JumpClone existing;
    Map<Integer, JumpClone> listCheck = new HashMap<Integer, JumpClone>();

    existing = new JumpClone(jumpCloneID, typeID, locationID, cloneName);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(jumpCloneID, existing);

    existing = new JumpClone(jumpCloneID + 10, typeID + 10, locationID + 10, cloneName + " 10");
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(jumpCloneID + 10, existing);

    // Associated with different account
    existing = new JumpClone(jumpCloneID, typeID, locationID, cloneName);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new JumpClone(jumpCloneID + 3, typeID + 3, locationID + 3, cloneName + " 3");
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new JumpClone(jumpCloneID + 4, typeID + 4, locationID + 4, cloneName + " 4");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<JumpClone> result = JumpClone.getAll(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (JumpClone next : result) {
      int jumpCloneID = next.getJumpCloneID();
      Assert.assertTrue(listCheck.containsKey(jumpCloneID));
      Assert.assertEquals(listCheck.get(jumpCloneID), next);
    }

  }

}
