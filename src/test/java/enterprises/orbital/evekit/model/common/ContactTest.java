package enterprises.orbital.evekit.model.common;

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
import enterprises.orbital.evekit.model.common.Contact;

public class ContactTest extends AbstractModelTester<Contact> {
  final String                             list          = "test list";
  final int                                contactID     = TestBase.getRandomInt(100000000);
  final String                             contactName   = "test contact name";
  final double                             standing      = TestBase.getRandomDouble(100000000);
  final boolean                            inWatchlist   = false;
  final int                                contactTypeID = TestBase.getRandomInt(100000000);
  final long                               labelMask     = TestBase.getRandomInt(100000000);

  final ClassUnderTestConstructor<Contact> eol           = new ClassUnderTestConstructor<Contact>() {

                                                           @Override
                                                           public Contact getCUT() {
                                                             return new Contact(list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
                                                           }

                                                         };

  final ClassUnderTestConstructor<Contact> live          = new ClassUnderTestConstructor<Contact>() {
                                                           @Override
                                                           public Contact getCUT() {
                                                             return new Contact(
                                                                 list, contactID, contactName, standing + 1, contactTypeID, inWatchlist, labelMask);
                                                           }

                                                         };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Contact>() {

      @Override
      public Contact[] getVariants() {
        return new Contact[] {
            new Contact(list + " 1", contactID, contactName, standing, contactTypeID, inWatchlist, labelMask),
            new Contact(list, contactID + 1, contactName, standing, contactTypeID, inWatchlist, labelMask),
            new Contact(list, contactID, contactName + " 1", standing, contactTypeID, inWatchlist, labelMask),
            new Contact(list, contactID, contactName, standing + 1, contactTypeID, inWatchlist, labelMask),
            new Contact(list, contactID, contactName, standing, contactTypeID, !inWatchlist, labelMask),
            new Contact(list, contactID, contactName, standing, contactTypeID + 1, inWatchlist, labelMask),
            new Contact(list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask + 1)
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Contact>() {

      @Override
      public Contact getModel(SynchronizedEveAccount account, long time) {
        return Contact.get(account, time, list, contactID);
      }

    });
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
    Map<String, Map<Integer, Contact>> listCheck = new HashMap<String, Map<Integer, Contact>>();

    existing = new Contact(list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list, new HashMap<Integer, Contact>());
    listCheck.get(list).put(contactID, existing);

    existing = new Contact(list, contactID + 10, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list).put(contactID + 10, existing);

    existing = new Contact(list + "1", contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list + "1", new HashMap<Integer, Contact>());
    listCheck.get(list + "1").put(contactID, existing);

    existing = new Contact(list + "1", contactID + 10, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list + "1").put(contactID + 10, existing);

    // Associated with different account
    existing = new Contact(list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Contact(list, contactID + 5, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Contact(list, contactID + 3, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<Contact> result = Contact.getAllContacts(testAccount, 8888L, 10, -1);
    Assert.assertEquals(4, result.size());
    for (Contact next : result) {
      String list = next.getList();
      int contactID = next.getContactID();
      Assert.assertTrue(listCheck.containsKey(list));
      Assert.assertTrue(listCheck.get(list).containsKey(contactID));
      Assert.assertEquals(listCheck.get(list).get(contactID), next);
    }

    // Verify limited set returned
    long limit = listCheck.get(list).get(contactID).getCid();
    result = Contact.getAllContacts(testAccount, 8888L, 2, limit - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(list).get(contactID), result.get(0));
    Assert.assertEquals(listCheck.get(list).get(contactID + 10), result.get(1));

    // Verify continuation ID returns proper set
    limit = listCheck.get(list).get(contactID + 10).getCid();
    result = Contact.getAllContacts(testAccount, 8888L, 100, limit);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(list + "1").get(contactID), result.get(0));
    Assert.assertEquals(listCheck.get(list + "1").get(contactID + 10), result.get(1));

  }

  @Test
  public void testGetByList() throws Exception {
    // Should exclude:
    // - contacts for a different account
    // - contacts not live at the given time
    // - contacts for a different list
    // Need to test:
    // - max results limitation
    // - continuation ID
    Contact existing;
    Map<String, Map<Integer, Contact>> listCheck = new HashMap<String, Map<Integer, Contact>>();

    existing = new Contact(list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list, new HashMap<Integer, Contact>());
    listCheck.get(list).put(contactID, existing);

    existing = new Contact(list, contactID + 10, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list).put(contactID + 10, existing);

    existing = new Contact(list, contactID + 20, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list).put(contactID + 20, existing);

    existing = new Contact(list, contactID + 30, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list).put(contactID + 30, existing);

    // Associated with different account
    existing = new Contact(list, contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with different list
    existing = new Contact(list + "1", contactID, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Contact(list, contactID + 5, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Contact(list, contactID + 3, contactName, standing, contactTypeID, inWatchlist, labelMask);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contacts are returned
    List<Contact> result = Contact.getByList(testAccount, 8888L, list, 10, -1);
    Assert.assertEquals(4, result.size());
    for (Contact next : result) {
      String list = next.getList();
      int contactID = next.getContactID();
      Assert.assertTrue(listCheck.containsKey(list));
      Assert.assertTrue(listCheck.get(list).containsKey(contactID));
      Assert.assertEquals(listCheck.get(list).get(contactID), next);
    }

    // Verify limited set returned
    long limit = listCheck.get(list).get(contactID).getCid();
    result = Contact.getByList(testAccount, 8888L, list, 2, limit - 1);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(list).get(contactID), result.get(0));
    Assert.assertEquals(listCheck.get(list).get(contactID + 10), result.get(1));

    // Verify continuation ID returns proper set
    limit = listCheck.get(list).get(contactID + 10).getCid();
    result = Contact.getByList(testAccount, 8888L, list, 100, limit);
    Assert.assertEquals(2, result.size());
    Assert.assertEquals(listCheck.get(list).get(contactID + 20), result.get(0));
    Assert.assertEquals(listCheck.get(list).get(contactID + 30), result.get(1));

  }

}
