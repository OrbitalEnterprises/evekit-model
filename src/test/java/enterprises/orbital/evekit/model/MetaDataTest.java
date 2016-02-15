package enterprises.orbital.evekit.model;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.base.PersistentProperty;
import enterprises.orbital.db.DBPropertyProvider;
import enterprises.orbital.evekit.account.AccountCreationException;
import enterprises.orbital.evekit.account.EveKitUserAccount;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;
import enterprises.orbital.evekit.model.MetaDataCountException;
import enterprises.orbital.evekit.model.MetaDataLimitException;
import enterprises.orbital.evekit.model.common.Kill;

public class MetaDataTest {

  public EveKitUserAccount      userAccount;
  public SynchronizedEveAccount testAccount;

  @Before
  public void setUp() throws AccountCreationException, IOException {
    OrbitalProperties.addPropertyFile("ModelTest.properties");
    PersistentProperty.setProvider(new DBPropertyProvider(OrbitalProperties.getGlobalProperty(EveKitUserAccountProvider.USER_ACCOUNT_PU_PROP)));
    userAccount = EveKitUserAccount.createNewUserAccount(true, true);
    testAccount = SynchronizedEveAccount.createSynchronizedEveAccount(userAccount, "testaccount", true, true, 1234, "abcd", 5678, "charname", 8765, "corpname");
  }

  @After
  public void tearDown() {}

  protected CachedData makeTestTarget() {
    CachedData targetData = new Kill(1111L, 2222L, 1234, 5678L);
    targetData.setup(testAccount, 1235L);
    return CachedData.updateData(targetData);
  }

  @Test
  public void testGetTagPresent() throws MetaDataLimitException, MetaDataCountException {
    CachedData targetData = makeTestTarget();

    // Manually add tag
    targetData.setMetaData("testkey", "testvalue");
    targetData = CachedData.updateData(targetData);

    // Now pull tag out
    CachedData copy = CachedData.get(targetData.getCid());
    Assert.assertNotNull(copy);
    Assert.assertEquals("testvalue", copy.getMetaData("testkey"));
  }

  @Test
  public void testGetTagMissing() throws MetaDataLimitException, MetaDataCountException {
    final CachedData targetData = makeTestTarget();

    // Manually add tag
    targetData.setMetaData("testkey", "testvalue");
    CachedData.updateData(targetData);

    // Now pull tag out
    Assert.assertNull(targetData.getMetaData("testkeymissing"));
  }

  @Test
  public void testAddTag() throws MetaDataCountException, MetaDataLimitException {

    // Create test target
    CachedData targetData = makeTestTarget();

    // Add a tag
    targetData.setMetaData("testkey", "testvalue");
    targetData = CachedData.updateData(targetData);

    // Check against target fetched from DB
    CachedData copy = CachedData.get(targetData.getCid());
    Assert.assertNotNull(copy);
    Assert.assertEquals("testvalue", copy.getMetaData("testkey"));
  }

  @Test(expected = MetaDataLimitException.class)
  public void testAddTagKeyTooBig() throws MetaDataCountException, MetaDataLimitException {
    final CachedData targetData = makeTestTarget();
    String bigKey = String.format("%256d", 0);
    targetData.setMetaData(bigKey, "anothervalue");
  }

  @Test(expected = MetaDataLimitException.class)
  public void testAddTagKeyNull() throws MetaDataCountException, MetaDataLimitException {
    final CachedData targetData = makeTestTarget();
    targetData.setMetaData(null, "anothervalue");
  }

  @Test(expected = MetaDataLimitException.class)
  public void testAddTagKeyEmpty() throws MetaDataCountException, MetaDataLimitException {
    final CachedData targetData = makeTestTarget();
    targetData.setMetaData("", "anothervalue");
  }

  @Test(expected = MetaDataLimitException.class)
  public void testAddTagValueTooBig() throws MetaDataCountException, MetaDataLimitException {
    final CachedData targetData = makeTestTarget();
    String bigValue = String.format("%256d", 0);
    targetData.setMetaData("testkey", bigValue);
  }

  @Test(expected = MetaDataLimitException.class)
  public void testAddTagValueNull() throws MetaDataCountException, MetaDataLimitException {
    final CachedData targetData = makeTestTarget();
    targetData.setMetaData("testkey", null);
  }

  @Test(expected = MetaDataCountException.class)
  public void testAddTagMetaDataLimit() throws MetaDataCountException, MetaDataLimitException {
    final CachedData targetData = makeTestTarget();
    for (int i = 0; i < CachedData.META_DATA_LIMIT; i++) {
      targetData.setMetaData("key" + i, "value" + i);
    }
    targetData.setMetaData("testkey", "anothervalue");
  }

  @Test
  public void testDeleteTag() throws MetaDataCountException, MetaDataLimitException {
    CachedData targetData = makeTestTarget();
    targetData.setMetaData("testkey", "testvalue");
    targetData = CachedData.updateData(targetData);
    targetData.deleteMetaData("testkey");
    targetData = CachedData.updateData(targetData);
    Assert.assertNull(targetData.getMetaData("testkey"));
  }

  @Test
  public void testDeleteTagMissing() throws MetaDataCountException, MetaDataLimitException {
    CachedData targetData = makeTestTarget();
    targetData.setMetaData("testkey", "testvalue");
    targetData = CachedData.updateData(targetData);
    targetData.deleteMetaData("anotherkey");
    targetData = CachedData.updateData(targetData);
    Assert.assertNotNull(targetData.getMetaData("testkey"));
  }

  @Test
  public void testUpdateTagExisting() throws MetaDataCountException, MetaDataLimitException {
    CachedData targetData = makeTestTarget();
    targetData.setMetaData("testkey", "testvalue");
    targetData = CachedData.updateData(targetData);
    targetData.setMetaData("testkey", "newvalue");
    targetData = CachedData.updateData(targetData);
    CachedData copy = CachedData.get(targetData.getCid());
    Assert.assertNotNull(copy);
    Assert.assertEquals(targetData, copy);
  }

}
