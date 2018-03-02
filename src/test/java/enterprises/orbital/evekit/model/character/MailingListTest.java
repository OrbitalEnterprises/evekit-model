package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailingListTest extends AbstractModelTester<MailingList> {
  private final String listName = "test list";
  private final int listID = 1234;

  final ClassUnderTestConstructor<MailingList> eol = () -> new MailingList(listName, listID);

  final ClassUnderTestConstructor<MailingList> live = () -> new MailingList("test list 2", listID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MailingList[]{
        new MailingList(listName + "1", listID),
        new MailingList(listName, listID + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAILING_LISTS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MailingList.get(account, time, listID));
  }

  @Test
  public void testGetAllLists() throws Exception {
    // Should exclude:
    // - lists for a different account
    // - lists not live at the given time
    MailingList existing;
    Map<Integer, MailingList> listCheck = new HashMap<>();

    existing = new MailingList("test list", 1234);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(1234, existing);

    existing = new MailingList("test list", 8213);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(8213, existing);

    // Associated with different account
    existing = new MailingList("test list", 5678);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MailingList("test list", 9721);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MailingList("test list", 2714);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<MailingList> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> MailingList.accessQuery(testAccount, contid, 1000,
                                                                                              false, at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(listCheck.size(), result.size());
    for (MailingList next : result) {
      int listID = next.getListID();
      Assert.assertTrue(listCheck.containsKey(listID));
      Assert.assertEquals(listCheck.get(listID), next);
    }
  }

}
