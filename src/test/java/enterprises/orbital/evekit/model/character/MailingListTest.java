package enterprises.orbital.evekit.model.character;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.character.MailingList;

public class MailingListTest extends AbstractModelTester<MailingList> {
  final String                                 listName = "test list";
  final long                                   listID   = 1234L;

  final ClassUnderTestConstructor<MailingList> eol      = new ClassUnderTestConstructor<MailingList>() {

                                                          @Override
                                                          public MailingList getCUT() {
                                                            return new MailingList(listName, listID);
                                                          }

                                                        };

  final ClassUnderTestConstructor<MailingList> live     = new ClassUnderTestConstructor<MailingList>() {
                                                          @Override
                                                          public MailingList getCUT() {
                                                            return new MailingList("test list 2", listID);
                                                          }

                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<MailingList>() {

      @Override
      public MailingList[] getVariants() {
        return new MailingList[] {
            new MailingList(listName + "1", listID), new MailingList(listName, listID + 1)
        };

      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAILING_LISTS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<MailingList>() {

      @Override
      public MailingList getModel(SynchronizedEveAccount account, long time) {
        return MailingList.get(account, time, listID);
      }

    });
  }

  @Test
  public void testGetAllListIDs() throws Exception {
    // Should exclude:
    // - lists for a different account
    // - lists not live at the given time
    MailingList existing;
    Set<Long> listIDs = new HashSet<Long>();
    listIDs.add(1234L);
    listIDs.add(8213L);

    existing = new MailingList("test list", 1234L);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    existing = new MailingList("test list", 8213L);
    existing.setup(testAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new MailingList("test list", 5678L);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new MailingList("test list", 9721L);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new MailingList("test list", 2714L);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<Long> result = MailingList.getAllListIDs(testAccount, 8888L);
    Assert.assertEquals(listIDs.size(), result.size());
    for (long i : result) {
      Assert.assertTrue(listIDs.contains(i));
    }
  }

  @Test
  public void testGetAllLists() throws Exception {
    // Should exclude:
    // - lists for a different account
    // - lists not live at the given time
    MailingList existing;
    Map<Long, MailingList> listCheck = new HashMap<Long, MailingList>();

    existing = new MailingList("test list", 1234L);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(1234L, existing);

    existing = new MailingList("test list", 8213L);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(8213L, existing);

    // Associated with different account
    existing = new MailingList("test list", 5678L);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new MailingList("test list", 9721L);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new MailingList("test list", 2714L);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    List<MailingList> result = MailingList.getAllLists(testAccount, 8888L);
    Assert.assertEquals(listCheck.size(), result.size());
    for (MailingList next : result) {
      long listID = next.getListID();
      Assert.assertTrue(listCheck.containsKey(listID));
      Assert.assertEquals(listCheck.get(listID), next);
    }
  }

}
