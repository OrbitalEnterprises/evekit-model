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

public class ContactLabelTest extends AbstractModelTester<ContactLabel> {
  final String list = "test list";
  private final long labelID = TestBase.getRandomInt(100000000);
  private final String name = "test label name";

  final ClassUnderTestConstructor<ContactLabel> eol = () -> new ContactLabel(list, labelID, name);

  final ClassUnderTestConstructor<ContactLabel> live = () -> new ContactLabel(list, labelID, name + " 1");

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new ContactLabel[]{
        new ContactLabel(list + " 1", labelID, name), new ContactLabel(list, labelID + 1, name), new ContactLabel(list,
                                                                                                                  labelID,
                                                                                                                  name + " 1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> ContactLabel.get(account, time, list, labelID));
  }

  @Test
  public void testGetAllContactLabels() throws Exception {
    // Should exclude:
    // - contact labels for a different account
    // - contact labels not live at the given time
    ContactLabel existing;
    Map<String, Map<Long, ContactLabel>> listCheck = new HashMap<>();

    existing = new ContactLabel(list, labelID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list, new HashMap<>());
    listCheck.get(list)
             .put(labelID, existing);

    existing = new ContactLabel(list, labelID + 10, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list)
             .put(labelID + 10, existing);

    existing = new ContactLabel(list + "1", labelID, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(list + "1", new HashMap<>());
    listCheck.get(list + "1")
             .put(labelID, existing);

    existing = new ContactLabel(list + "1", labelID + 10, name);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(list + "1")
             .put(labelID + 10, existing);

    // Associated with different account
    existing = new ContactLabel(list, labelID, name);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ContactLabel(list, labelID + 5, name);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ContactLabel(list, labelID + 3, name);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all contact labels are returned
    List<ContactLabel> result = CachedData.retrieveAll(8888L,
                                                       (contid, at) -> ContactLabel.accessQuery(testAccount, contid,
                                                                                                1000, false, at,
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any(),
                                                                                                AttributeSelector.any()));
    Assert.assertEquals(4, result.size());
    for (ContactLabel next : result) {
      String list = next.getList();
      long labelID = next.getLabelID();
      Assert.assertTrue(listCheck.containsKey(list));
      Assert.assertTrue(listCheck.get(list)
                                 .containsKey(labelID));
      Assert.assertEquals(listCheck.get(list)
                                   .get(labelID), next);
    }
  }

}
