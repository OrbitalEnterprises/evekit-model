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

public class ChatChannelTest extends AbstractModelTester<ChatChannel> {
  final long                                   channelID     = TestBase.getRandomInt(100000000);
  final long                                   ownerID       = TestBase.getRandomInt(100000000);
  final String                                 ownerName     = "test owner name";
  final String                                 displayName   = "test display name";
  final String                                 comparisonKey = "test key value";
  final boolean                                hasPassword   = TestBase.getRandomBoolean();
  final String                                 motd          = "test motd";

  final ClassUnderTestConstructor<ChatChannel> eol           = new ClassUnderTestConstructor<ChatChannel>() {

                                                               @Override
                                                               public ChatChannel getCUT() {
                                                                 return new ChatChannel(
                                                                     channelID, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
                                                               }

                                                             };

  final ClassUnderTestConstructor<ChatChannel> live          = new ClassUnderTestConstructor<ChatChannel>() {
                                                               @Override
                                                               public ChatChannel getCUT() {
                                                                 return new ChatChannel(
                                                                     channelID, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
                                                               }

                                                             };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<ChatChannel>() {

      @Override
      public ChatChannel[] getVariants() {
        return new ChatChannel[] {
            new ChatChannel(channelID + 1, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd),
            new ChatChannel(channelID, ownerID + 1, ownerName, displayName, comparisonKey, hasPassword, motd),
            new ChatChannel(channelID, ownerID, ownerName + "1", displayName, comparisonKey, hasPassword, motd),
            new ChatChannel(channelID, ownerID, ownerName, displayName + "1", comparisonKey, hasPassword, motd),
            new ChatChannel(channelID, ownerID, ownerName, displayName, comparisonKey + "1", hasPassword, motd),
            new ChatChannel(channelID, ownerID, ownerName, displayName, comparisonKey, !hasPassword, motd),
            new ChatChannel(channelID, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd + "1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHAT_CHANNELS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<ChatChannel>() {

      @Override
      public ChatChannel getModel(
                                  SynchronizedEveAccount account,
                                  long time) {
        return ChatChannel.get(account, time, channelID);
      }

    });
  }

  @Test
  public void testGetAllChatChannels() throws Exception {
    // Should exclude:
    // - channels for a different account
    // - channels not live at the given time
    ChatChannel existing;
    Map<Long, ChatChannel> listCheck = new HashMap<Long, ChatChannel>();

    existing = new ChatChannel(channelID, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID, existing);

    existing = new ChatChannel(channelID + 10, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID + 10, existing);

    existing = new ChatChannel(channelID + 20, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(channelID + 20, existing);

    // Associated with different account
    existing = new ChatChannel(channelID, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new ChatChannel(channelID + 5, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 9999L);
    existing = CachedData.update(existing);

    // EOL before the given time
    existing = new ChatChannel(channelID + 3, ownerID, ownerName, displayName, comparisonKey, hasPassword, motd);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all channels are returned
    List<ChatChannel> result = ChatChannel.getAllChatChannels(testAccount, 8888L);
    Assert.assertEquals(3, result.size());
    for (ChatChannel next : result) {
      long channelID = next.getChannelID();
      Assert.assertTrue(listCheck.containsKey(channelID));
      Assert.assertEquals(listCheck.get(channelID), next);
    }
  }

}
