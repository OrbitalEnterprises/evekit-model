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

public class ChatChannelMemberTest extends AbstractModelTester<ChatChannelMember> {
  final private long                                 channelID    = TestBase.getRandomInt(100000000);
  final private String                               category     = "allowed";
  final private long                                 accessorID   = TestBase.getRandomInt(100000000);
  final private String                               accessorName = "teset accessor";
  final private long                                 untilWhen    = TestBase.getRandomInt(100000000);
  final private String                               reason       = "test reason";

  final ClassUnderTestConstructor<ChatChannelMember> eol          = new ClassUnderTestConstructor<ChatChannelMember>() {

                                                                    @Override
                                                                    public ChatChannelMember getCUT() {
                                                                      return new ChatChannelMember(
                                                                          channelID, category, accessorID, accessorName, untilWhen, reason);
                                                                    }

                                                                  };

  final ClassUnderTestConstructor<ChatChannelMember> live         = new ClassUnderTestConstructor<ChatChannelMember>() {
                                                                    @Override
                                                                    public ChatChannelMember getCUT() {
                                                                      return new ChatChannelMember(
                                                                          channelID, category, accessorID, accessorName, untilWhen, reason);
                                                                    }

                                                                  };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ChatChannelMember>() {

      @Override
      public ChatChannelMember[] getVariants() {
        return new ChatChannelMember[] {
            new ChatChannelMember(channelID + 1, category, accessorID, accessorName, untilWhen, reason),
            new ChatChannelMember(channelID, category + "1", accessorID, accessorName, untilWhen, reason),
            new ChatChannelMember(channelID, category, accessorID + 1, accessorName, untilWhen, reason),
            new ChatChannelMember(channelID, category, accessorID, accessorName + "1", untilWhen, reason),
            new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen + 1, reason),
            new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason + "1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ChatChannelMember>() {

      @Override
      public ChatChannelMember getModel(
                                        SynchronizedEveAccount account,
                                        long time) {
        return ChatChannelMember.get(account, time, channelID, category, accessorID);
      }

    });
  }

  @Test
  public void testGetAllChatChannelMembers() throws Exception {
    // Should exclude:
    // - members for a different account
    // - members not live at the given time
    ChatChannelMember existing;
    Map<Long, Map<String, Map<Long, ChatChannelMember>>> listCheck = new HashMap<Long, Map<String, Map<Long, ChatChannelMember>>>();

    existing = new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(channelID, new HashMap<String, Map<Long, ChatChannelMember>>());
    listCheck.get(channelID).put(category, new HashMap<Long, ChatChannelMember>());
    listCheck.get(channelID).get(category).put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category + "10", accessorID, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).put(category + "10", new HashMap<Long, ChatChannelMember>());
    listCheck.get(channelID).get(category + "10").put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 10, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).get(category).put(accessorID + 10, existing);

    existing = new ChatChannelMember(channelID + 10, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(channelID + 10, new HashMap<String, Map<Long, ChatChannelMember>>());
    listCheck.get(channelID + 10).put(category, new HashMap<Long, ChatChannelMember>());
    listCheck.get(channelID + 10).get(category).put(accessorID, existing);

    // Associated with different account
    existing = new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 5, accessorName, untilWhen, reason);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 3, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all members are returned
    List<ChatChannelMember> result = ChatChannelMember.getAllChatChannelMembers(testAccount, 8888L);
    Assert.assertEquals(4, result.size());
    for (ChatChannelMember next : result) {
      long channelID = next.getChannelID();
      String category = next.getCategory();
      long accessorID = next.getAccessorID();
      Assert.assertTrue(listCheck.containsKey(channelID));
      Assert.assertTrue(listCheck.get(channelID).containsKey(category));
      Assert.assertTrue(listCheck.get(channelID).get(category).containsKey(accessorID));
      Assert.assertEquals(listCheck.get(channelID).get(category).get(accessorID), next);
    }
  }

  @Test
  public void testGetAllChatChannelMembersByChannelID() throws Exception {
    // Should exclude:
    // - members for a different channel
    // - members for a different account
    // - members not live at the given time
    ChatChannelMember existing;
    Map<Long, Map<String, Map<Long, ChatChannelMember>>> listCheck = new HashMap<Long, Map<String, Map<Long, ChatChannelMember>>>();

    existing = new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(channelID, new HashMap<String, Map<Long, ChatChannelMember>>());
    listCheck.get(channelID).put(category, new HashMap<Long, ChatChannelMember>());
    listCheck.get(channelID).get(category).put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category + "10", accessorID, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).put(category + "10", new HashMap<Long, ChatChannelMember>());
    listCheck.get(channelID).get(category + "10").put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 10, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).get(category).put(accessorID + 10, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 20, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).get(category).put(accessorID + 20, existing);

    // Associated with different channel
    existing = new ChatChannelMember(channelID + 10, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 5, accessorName, untilWhen, reason);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 3, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all members are returned
    List<ChatChannelMember> result = ChatChannelMember.getByChannelID(testAccount, 8888L, channelID);
    Assert.assertEquals(4, result.size());
    for (ChatChannelMember next : result) {
      long channelID = next.getChannelID();
      String category = next.getCategory();
      long accessorID = next.getAccessorID();
      Assert.assertTrue(listCheck.containsKey(channelID));
      Assert.assertTrue(listCheck.get(channelID).containsKey(category));
      Assert.assertTrue(listCheck.get(channelID).get(category).containsKey(accessorID));
      Assert.assertEquals(listCheck.get(channelID).get(category).get(accessorID), next);
    }
  }

  @Test
  public void testGetAllChatChannelMembersByChannelIDAndCategory() throws Exception {
    // Should exclude:
    // - members for a different channel
    // - members for a different category
    // - members for a different category and channel
    // - members for a different account
    // - members not live at the given time
    ChatChannelMember existing;
    Map<Long, Map<String, Map<Long, ChatChannelMember>>> listCheck = new HashMap<Long, Map<String, Map<Long, ChatChannelMember>>>();

    existing = new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.put(channelID, new HashMap<String, Map<Long, ChatChannelMember>>());
    listCheck.get(channelID).put(category, new HashMap<Long, ChatChannelMember>());
    listCheck.get(channelID).get(category).put(accessorID, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 10, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).get(category).put(accessorID + 10, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 20, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).get(category).put(accessorID + 20, existing);

    existing = new ChatChannelMember(channelID, category, accessorID + 30, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing = CachedData.updateData(existing);
    listCheck.get(channelID).get(category).put(accessorID + 30, existing);

    // Associated with different channel
    existing = new ChatChannelMember(channelID + 10, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different category
    existing = new ChatChannelMember(channelID, category + "10", accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different channel and category
    existing = new ChatChannelMember(channelID + 10, category + "10", accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Associated with different account
    existing = new ChatChannelMember(channelID, category, accessorID, accessorName, untilWhen, reason);
    existing.setup(otherAccount, 7777L);
    CachedData.updateData(existing);

    // Not live at the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 5, accessorName, untilWhen, reason);
    existing.setup(testAccount, 9999L);
    CachedData.updateData(existing);

    // EOL before the given time
    existing = new ChatChannelMember(channelID, category, accessorID + 3, accessorName, untilWhen, reason);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.updateData(existing);

    // Verify all members are returned
    List<ChatChannelMember> result = ChatChannelMember.getByChannelIDAndCategory(testAccount, 8888L, channelID, category);
    Assert.assertEquals(4, result.size());
    for (ChatChannelMember next : result) {
      long channelID = next.getChannelID();
      String category = next.getCategory();
      long accessorID = next.getAccessorID();
      Assert.assertTrue(listCheck.containsKey(channelID));
      Assert.assertTrue(listCheck.get(channelID).containsKey(category));
      Assert.assertTrue(listCheck.get(channelID).get(category).containsKey(accessorID));
      Assert.assertEquals(listCheck.get(channelID).get(category).get(accessorID), next);
    }
  }

}
