package enterprises.orbital.evekit.model.character;

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

public class ChatChannelMemberTest extends AbstractModelTester<ChatChannelMember> {
  final private int channelID = TestBase.getRandomInt(100000000);
  final private String category = "allowed";
  final private int accessorID = TestBase.getRandomInt(100000000);
  final private String accessorType = "teset accessor";
  final private long untilWhen = TestBase.getRandomInt(100000000);
  final private String reason = "test reason";

  final ClassUnderTestConstructor<ChatChannelMember> eol = () -> new ChatChannelMember(
      channelID, category, accessorID, accessorType, untilWhen, reason);

  final ClassUnderTestConstructor<ChatChannelMember> live = () -> new ChatChannelMember(
      channelID, category, accessorID, accessorType, untilWhen, reason);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new ChatChannelMember[]{
        new ChatChannelMember(channelID + 1, category, accessorID, accessorType, untilWhen, reason),
        new ChatChannelMember(channelID, category + "1", accessorID, accessorType, untilWhen, reason),
        new ChatChannelMember(channelID, category, accessorID + 1, accessorType, untilWhen, reason),
        new ChatChannelMember(channelID, category, accessorID, accessorType + "1", untilWhen, reason),
        new ChatChannelMember(channelID, category, accessorID, accessorType, untilWhen + 1, reason),
        new ChatChannelMember(channelID, category, accessorID, accessorType, untilWhen, reason + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live,
                       (account, time) -> ChatChannelMember.get(account, time, channelID, category, accessorID));
  }

  @Test
  public void testGetAllChatChannelMembers() throws Exception {
    // Should exclude:
    // - members for a different account
    // - members not live at the given time
    ChatChannelMember existing;
    Map<Integer, Map<String, Map<Integer, ChatChannelMember>>> listCheck = new HashMap<>();

    existing = new ChatChannelMember(channelID, category, accessorID, accessorType, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID, new HashMap<>());
    listCheck.get(channelID)
             .put(category, new HashMap<>());
    listCheck.get(channelID)
             .get(category)
             .put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category + "10", accessorID, accessorType, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(channelID)
             .put(category + "10", new HashMap<>());
    listCheck.get(channelID)
             .get(category + "10")
             .put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 10, accessorType, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(channelID)
             .get(category)
             .put(accessorID + 10, existing);

    existing = new ChatChannelMember(channelID + 10, category, accessorID, accessorType, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID + 10, new HashMap<>());
    listCheck.get(channelID + 10)
             .put(category, new HashMap<>());
    listCheck.get(channelID + 10)
             .get(category)
             .put(accessorID, existing);

    // Associated with different account
    existing = new ChatChannelMember(channelID, category, accessorID, accessorType, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 5, accessorType, untilWhen, reason);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 3, accessorType, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all members are returned
    List<ChatChannelMember> result = CachedData.retrieveAll(8888L,
                                                            (contid, at) -> ChatChannelMember.accessQuery(testAccount,
                                                                                                          contid, 1000,
                                                                                                          false, at,
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any(),
                                                                                                          AttributeSelector.any()));
    Assert.assertEquals(4, result.size());
    for (ChatChannelMember next : result) {
      int channelID = next.getChannelID();
      String category = next.getCategory();
      int accessorID = next.getAccessorID();
      Assert.assertTrue(listCheck.containsKey(channelID));
      Assert.assertTrue(listCheck.get(channelID)
                                 .containsKey(category));
      Assert.assertTrue(listCheck.get(channelID)
                                 .get(category)
                                 .containsKey(accessorID));
      Assert.assertEquals(listCheck.get(channelID)
                                   .get(category)
                                   .get(accessorID), next);
    }
  }

}
