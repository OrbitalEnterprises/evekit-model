package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ContactTest extends AbstractModelTester<Contact> {
  final String list = TestBase.getRandomText(50);
  private final int contactID = TestBase.getRandomInt(100000000);
  private final float standing = TestBase.getRandomFloat(10);
  private final String contactType = TestBase.getRandomText(50);
  private final boolean inWatchlist = TestBase.getRandomBoolean();
  private final boolean isBlocked = TestBase.getRandomBoolean();
  private final Set<Long> labels = new HashSet<>();

  public ContactTest() {
    int numLabels = TestBase.getRandomInt(5) + 10;
    for (int i = 0; i < numLabels; i++) {
      labels.add(TestBase.getUniqueRandomLong());
    }
  }

  final ClassUnderTestConstructor<Contact> eol = () -> new Contact(list, contactID, standing, contactType, inWatchlist,
                                                                   isBlocked, labels);

  final ClassUnderTestConstructor<Contact> live = () -> new Contact(
      list, contactID, standing + 1, contactType, inWatchlist, isBlocked, labels);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> {
      Set<Long> labelCopy = new HashSet<>(labels);
      labelCopy.add(TestBase.getUniqueRandomLong());
      return new Contact[]{
          new Contact(list + " 1", contactID, standing, contactType, inWatchlist, isBlocked, labels),
          new Contact(list, contactID + 1, standing, contactType, inWatchlist, isBlocked, labels),
          new Contact(list, contactID, standing + 1, contactType, inWatchlist, isBlocked, labels),
          new Contact(list, contactID, standing, contactType + "1", inWatchlist, isBlocked, labels),
          new Contact(list, contactID, standing, contactType, !inWatchlist, isBlocked, labels),
          new Contact(list, contactID, standing, contactType, inWatchlist, !isBlocked, labels),
          new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked, labelCopy),
      };
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> Contact.get(account, time, list, contactID));
  }

  @Test
  public void testSelectByLabel() throws Exception {
    // Should exclude:
    // - messages for a different account
    // - messages not live at the given time
    // Need to test:
    // - messages with a specific label
    // - messages without a specific label

    Contact existing, sample;

    Long[] lbls = new Long[]{1L, 2L, 3L};

    existing = new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked,
                           new HashSet<>(Arrays.asList(lbls)));
    existing.setup(testAccount, 7777L);
    sample = CachedData.update(existing);

    // Different label set
    existing = new Contact(list, contactID + 10, standing, contactType, inWatchlist, isBlocked, new HashSet<>(Arrays.asList(
        4L, 5L, 6L)));
    existing.setup(testAccount, 7777L);
    CachedData.update(existing);

    // Associated with different account
    existing = new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked,
                           new HashSet<>(Arrays.asList(lbls)));
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Contact(list, contactID + 5, standing, contactType, inWatchlist, isBlocked,
                           new HashSet<>(Arrays.asList(lbls)));
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Contact(list, contactID + 3, standing, contactType, inWatchlist, isBlocked,
                           new HashSet<>(Arrays.asList(lbls)));
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify contact with requested label is selected
    List<Contact> check = CachedData.retrieveAll(8888L,
                                                 (contid, at) -> Contact.accessQuery(
                                                     testAccount,
                                                     contid,
                                                     1000,
                                                     false,
                                                     at,
                                                     AttributeSelector.any(),
                                                     AttributeSelector.values(contactID),
                                                     AttributeSelector.any(),
                                                     AttributeSelector.any(),
                                                     AttributeSelector.any(),
                                                     AttributeSelector.any(),
                                                     AttributeSelector.values(2, 3, 4, 5)));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no contacts match
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> Contact.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.any(),
                                       AttributeSelector.values(contactID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.values(5, 6)));
    Assert.assertEquals(0, check.size());

    // Verify contact with requested label is selected by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> Contact.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.any(),
                                       AttributeSelector.values(contactID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(0, 10)));
    Assert.assertEquals(1, check.size());
    Assert.assertEquals(sample, check.get(0));

    // Verify no contacts match by range
    check = CachedData.retrieveAll(8888L,
                                   (contid, at) -> Contact.accessQuery(
                                       testAccount,
                                       contid,
                                       1000,
                                       false,
                                       at,
                                       AttributeSelector.any(),
                                       AttributeSelector.values(contactID),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.any(),
                                       AttributeSelector.range(10, 100)));
    Assert.assertEquals(0, check.size());

  }

  @Test
  public void testGetAllContacts() throws Exception {
    // Should exclude:
    // - contacts for a different account
    // - contacts not live at the given time
    Contact existing;
    Map<String, Map<Integer, Contact>> listCheck = new HashMap<>();

    existing = new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked, labels);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list, new HashMap<>());
    listCheck.get(list)
             .put(contactID, existing);

    existing = new Contact(list, contactID + 10, standing, contactType, inWatchlist, isBlocked, labels);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list)
             .put(contactID + 10, existing);

    existing = new Contact(list + "1", contactID, standing, contactType, inWatchlist, isBlocked, labels);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list + "1", new HashMap<>());
    listCheck.get(list + "1")
             .put(contactID, existing);

    existing = new Contact(list + "1", contactID + 10, standing, contactType, inWatchlist, isBlocked, labels);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list + "1")
             .put(contactID + 10, existing);

    // Associated with different account
    existing = new Contact(list, contactID, standing, contactType, inWatchlist, isBlocked, labels);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Contact(list, contactID + 5, standing, contactType, inWatchlist, isBlocked, labels);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Contact(list, contactID + 3, standing, contactType, inWatchlist, isBlocked, labels);
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
