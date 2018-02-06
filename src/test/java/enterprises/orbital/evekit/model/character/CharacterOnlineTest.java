package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

public class CharacterOnlineTest extends AbstractModelTester<CharacterOnline> {
  private final boolean online = TestBase.getRandomBoolean();
  private final long lastLogin = TestBase.getRandomInt(100000000);
  private final long lastLogout = TestBase.getRandomInt(100000000);
  private final int logins = TestBase.getRandomInt(10000);

  private final ClassUnderTestConstructor<CharacterOnline> eol = () -> new CharacterOnline(
      online, lastLogin, lastLogout, logins);
  private final ClassUnderTestConstructor<CharacterOnline> live = () -> new CharacterOnline(
      online, lastLogin + 1, lastLogout, logins);

  @Test
  public void testBasic() throws Exception {
    runBasicTests(eol, () -> new CharacterOnline[]{
        new CharacterOnline(!online, lastLogin, lastLogout, logins),
        new CharacterOnline(online, lastLogin + 1, lastLogout, logins),
        new CharacterOnline(online, lastLogin, lastLogout + 1, logins),
        new CharacterOnline(online, lastLogin, lastLogout, logins + 1)
    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_STATUS));
  }

  @Test
  public void testGetLifeline() throws Exception {
    runGetLifelineTest(eol, live, CharacterOnline::get);
  }

  @Test
  public void testGetLocation() throws Exception {
    // Should exclude:
    // - online for a different account
    // - online not live at the given time
    CharacterOnline existing, keyed;

    keyed = new CharacterOnline(online, lastLogin, lastLogout, logins);
    keyed.setup(testAccount, 7777L);
    keyed = CachedData.update(keyed);

    // Associated with different account
    existing = new CharacterOnline(online, lastLogin + 1, lastLogout, logins);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new CharacterOnline(online, lastLogin + 3, lastLogout, logins);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new CharacterOnline(online, lastLogin + 4, lastLogout, logins);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    CharacterOnline result = CharacterOnline.get(testAccount, 8888L);
    Assert.assertEquals(keyed, result);
  }

}
