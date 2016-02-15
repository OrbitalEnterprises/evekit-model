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
import enterprises.orbital.evekit.model.common.ContactLabel;

public class ContactLabelTest extends AbstractModelTester<ContactLabel> {
  final String                                  list    = "test list";
  final long                                    labelID = TestBase.getRandomInt(100000000);
  final String                                  name    = "test label name";

  final ClassUnderTestConstructor<ContactLabel> eol     = new ClassUnderTestConstructor<ContactLabel>() {

                                                          @Override
                                                          public ContactLabel getCUT() {
                                                            return new ContactLabel(list, labelID, name);
                                                          }

                                                        };

  final ClassUnderTestConstructor<ContactLabel> live    = new ClassUnderTestConstructor<ContactLabel>() {
                                                          @Override
                                                          public ContactLabel getCUT() {
                                                            return new ContactLabel(list, labelID, name + " 1");
                                                          }

                                                        };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ContactLabel>() {

      @Override
      public ContactLabel[] getVariants() {
        return new ContactLabel[] {
            new ContactLabel(list + " 1", labelID, name), new ContactLabel(list, labelID + 1, name), new ContactLabel(list, labelID, name + " 1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ContactLabel>() {

      @Override
      public ContactLabel getModel(SynchronizedEveAccount account, long time) {
        return ContactLabel.get(account, time, list, labelID);
      }

    });
  }

  @Test
  public void testGetAllContactLabels() throws Exception {
    // Should exclude:
    // - contact labels for a different account
    // - contact labels not live at the given time
    ContactLabel existing;
    Map<String, Map<Long, ContactLabel>> listCheck = new HashMap<String, Map<Long, ContactLabel>>();

    existing = new ContactLabel(list, labelID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(list, new HashMap<Long, ContactLabel>());
    listCheck.get(list).put(labelID, existing);

    existing = new ContactLabel(list, labelID + 10, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(list).put(labelID + 10, existing);

    existing = new ContactLabel(list + "1", labelID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(list + "1", new HashMap<Long, ContactLabel>());
    listCheck.get(list + "1").put(labelID, existing);

    existing = new ContactLabel(list + "1", labelID + 10, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(list + "1").put(labelID + 10, existing);

    // Associated with different account
    existing = new ContactLabel(list, labelID, name);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ContactLabel(list, labelID + 5, name);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ContactLabel(list, labelID + 3, name);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contact labels are returned
    List<ContactLabel> result = ContactLabel.getAllContactLabels(testAccount, 8888L);
    Assert.assertEquals(4, result.size());
    for (ContactLabel next : result) {
      String list = next.getList();
      long labelID = next.getLabelID();
      Assert.assertTrue(listCheck.containsKey(list));
      Assert.assertTrue(listCheck.get(list).containsKey(labelID));
      Assert.assertEquals(listCheck.get(list).get(labelID), next);
    }
  }

  @Test
  public void testGetByList() throws Exception {
    // Should exclude:
    // - contacts for a different account
    // - contacts not live at the given time
    // - contacts for a different list
    ContactLabel existing;
    Map<String, Map<Long, ContactLabel>> listCheck = new HashMap<String, Map<Long, ContactLabel>>();

    existing = new ContactLabel(list, labelID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(list, new HashMap<Long, ContactLabel>());
    listCheck.get(list).put(labelID, existing);

    existing = new ContactLabel(list, labelID + 10, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(list).put(labelID + 10, existing);

    existing = new ContactLabel(list, labelID + 20, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(list).put(labelID + 20, existing);

    existing = new ContactLabel(list, labelID + 30, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(list).put(labelID + 30, existing);

    // Associated with different account
    existing = new ContactLabel(list, labelID, name);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different list
    existing = new ContactLabel(list + "1", labelID, name);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ContactLabel(list, labelID + 5, name);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ContactLabel(list, labelID + 3, name);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all contact labels are returned
    List<ContactLabel> result = ContactLabel.getByList(testAccount, 8888L, list);
    Assert.assertEquals(4, result.size());
    for (ContactLabel next : result) {
      String list = next.getList();
      long labelID = next.getLabelID();
      Assert.assertTrue(listCheck.containsKey(list));
      Assert.assertTrue(listCheck.get(list).containsKey(labelID));
      Assert.assertEquals(listCheck.get(list).get(labelID), next);
    }
  }

}
