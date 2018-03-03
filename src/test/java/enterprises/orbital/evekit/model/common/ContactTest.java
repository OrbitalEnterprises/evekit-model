package enterprises.orbital.evekit.model.common;

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

public class ContactTest extends AbstractModelTester<Contact> {
  final String list = TestBase.getRandomText(50);
  private final int contactID = TestBase.getRandomInt(100000000);
  private final float standing = TestBase.getRandomFloat(10);
  private final String contactType = TestBase.getRandomText(50);
  private final boolean inWatchlist = TestBase.getRandomBoolean();
  private final boolean isBlocked = TestBase.getRandomBoolean();
  private final long labelID = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Contact> eol = () -> new Contact(list, contactID, standing, contactType, inWatchlist,
                                                                   isBlocked, labelID);

  final ClassUnderTestConstructor<Contact> live = () -> new Contact(
      list, contactID, standing + 1, contactType, inWatchlist, isBlocked, labelID);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new Contact[]{
        new Contact(list + " 1", contactID, standing, contactType, inWatchlist, isBlocked, labelID),
        new Contact(list, contactID + 1, standing, contactType, inWatchlist, isBlocked, labelID),
        new Contact(list, contactID, standing + 1, contactType, inWatchlist, isBlocked, labelID),
        new Contact(list, contactID, standing, contactType + "1", inWatchlist, isBlocked, labelID),
        new Contact(list, contactID, standing, contactType, !inWatchlist, isBlocked, labelID),
        new Contact(list, contactID, standing, contactType, inWatchlist, !isBlocked, labelID),
        new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked, labelID + 1),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Contact.get(account, time, list, contactID));
  }

  @Test
  public void testGetAllContacts() throws Exception {
    // Should exclude:
    // - contacts for a different account
    // - contacts not live at the given time
    // Need to test:
    // - max results limitation
    // - continuation ID
    Contact existing;
    Map<String, Map<Integer, Contact>> listCheck = new HashMap<>();

    existing = new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list, new HashMap<>());
    listCheck.get(list)
             .put(contactID, existing);

    existing = new Contact(list, contactID + 10, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list)
             .put(contactID + 10, existing);

    existing = new Contact(list + "1", contactID, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list + "1", new HashMap<>());
    listCheck.get(list + "1")
             .put(contactID, existing);

    existing = new Contact(list + "1", contactID + 10, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list + "1")
             .put(contactID + 10, existing);

    // Associated with different account
    existing = new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Contact(list, contactID + 5, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Contact(list, contactID + 3, standing, contactType, inWatchlist, isBlocked, labelID);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<Contact> result = CachedData.retrieveAll(8888L,
                                                  (contid, at) -> Contact.accessQuery(testAccount, contid, 1000, false,
                                                                                      at,
                                                                                      AttributeSelector.any(),
                                                                                      AttributeSelector.any(),
                                                                                      AttributeSelector.any(),
                                                                                      AttributeSelector.any(),
                                                                                      AttributeSelector.any(),
                                                                                      AttributeSelector.any(),
                                                                                      AttributeSelector.any()));
    Assert.assertEquals(4, result.size());
    for (Contact next : result) {
      String list = next.getList();
      int contactID = next.getContactID();
      Assert.assertTrue(listCheck.containsKey(list));
      Assert.assertTrue(listCheck.get(list)
                                 .containsKey(contactID));
      Assert.assertEquals(listCheck.get(list)
                                   .get(contactID), next);
    }
  }

}
