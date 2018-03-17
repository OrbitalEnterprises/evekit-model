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

public class ChatChannelTest extends AbstractModelTester<ChatChannel> {
  private final int channelID = TestBase.getRandomInt(100000000);
  private final int ownerID = TestBase.getRandomInt(100000000);
  private final String displayName = "test display name";
  private final String comparisonKey = "test key value";
  private final boolean hasPassword = TestBase.getRandomBoolean();
  private final String motd = "test motd";

  final ClassUnderTestConstructor<ChatChannel> eol = () -> new ChatChannel(
      channelID, ownerID, displayName, comparisonKey, hasPassword, motd);

  final ClassUnderTestConstructor<ChatChannel> live = () -> new ChatChannel(
      channelID, ownerID, displayName, comparisonKey, hasPassword, motd);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new ChatChannel[]{
        new ChatChannel(channelID + 1, ownerID, displayName, comparisonKey, hasPassword, motd),
        new ChatChannel(channelID, ownerID + 1, displayName, comparisonKey, hasPassword, motd),
        new ChatChannel(channelID, ownerID, displayName + "1", comparisonKey, hasPassword, motd),
        new ChatChannel(channelID, ownerID, displayName, comparisonKey + "1", hasPassword, motd),
        new ChatChannel(channelID, ownerID, displayName, comparisonKey, !hasPassword, motd),
        new ChatChannel(channelID, ownerID, displayName, comparisonKey, hasPassword, motd + "1")
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, (account, time) -> ChatChannel.get(account, time, channelID));
  }

  @Test
  public void testGetAllChatChannels() throws Exception {
    // Should exclude:
    // - channels for a different account
    // - channels not live at the given time
    ChatChannel existing;
    Map<Integer, ChatChannel> listCheck = new HashMap<>();

    existing = new ChatChannel(channelID, ownerID, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID, existing);

    existing = new ChatChannel(channelID + 10, ownerID, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID + 10, existing);

    existing = new ChatChannel(channelID + 20, ownerID, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID + 20, existing);

    // Associated with different account
    existing = new ChatChannel(channelID, ownerID, displayName, comparisonKey, hasPassword, motd);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ChatChannel(channelID + 5, ownerID, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new ChatChannel(channelID + 3, ownerID, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<ChatChannel> result = CachedData.retrieveAll(8888L,
                                                      (contid, at) -> ChatChannel.accessQuery(testAccount, contid, 1000,
                                                                                              false, at,
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any(),
                                                                                              AttributeSelector.any()));
    Assert.assertEquals(3, result.size());
    for (ChatChannel next : result) {
      int channelID = next.getChannelID();
      Assert.assertTrue(listCheck.containsKey(channelID));
      Assert.assertEquals(listCheck.get(channelID), next);
    }
  }

}
