package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailLabelTest extends AbstractModelTester<MailLabel> {
  private final int labelID = TestBase.getRandomInt();
  private final int unreadCount = TestBase.getRandomInt();
  private final String name = TestBase.getRandomText(50);
  private final String color = TestBase.getRandomText(50);

  final ClassUnderTestConstructor<MailLabel> eol = () -> new MailLabel(labelID, unreadCount, name, color);

  final ClassUnderTestConstructor<MailLabel> live = () -> new MailLabel(labelID, unreadCount + 1, name, color);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new MailLabel[]{
        new MailLabel(labelID + 1, unreadCount, name, color),
        new MailLabel(labelID, unreadCount + 1, name, color),
        new MailLabel(labelID, unreadCount, name + "1", color),
        new MailLabel(labelID, unreadCount, name, color + "1"),
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> MailLabel.get(account, time, labelID));
  }

  @Test
  public void testGetAllLabels() throws Exception {
    // Should exclude:
    // - lists for a different account
    // - lists not live at the given time
    MailLabel existing;
    Map<Integer, MailLabel> listCheck = new HashMap<>();

    existing = new MailLabel(1234, 5555, "name", "color");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(1234, existing);

    existing = new MailLabel(8213, 5555, "name", "color");
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(8213, existing);

    // Associated with different account
    existing = new MailLabel(5678, 5555, "name", "color");
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new MailLabel(9721, 5555, "name", "color");
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new MailLabel(2714, 5555, "name", "color");
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    List<MailLabel> result = CachedData.retrieveAll(8888L, new CachedData.QueryCaller<MailLabel>() {
      @Override
      public List<MailLabel> query(long contid, AttributeSelector at) throws IOException {
        return MailLabel.accessQuery(testAccount, contid, 1000, false, at,
                                     AttributeSelector.any(), AttributeSelector.any(),
                                     AttributeSelector.any(), AttributeSelector.any());
      }
    });
    Assert.assertEquals(listCheck.size(), result.size());
    for (MailLabel next : result) {
      int labelID = next.getLabelID();
      Assert.assertTrue(listCheck.containsKey(labelID));
      Assert.assertEquals(listCheck.get(labelID), next);
    }
  }

}
