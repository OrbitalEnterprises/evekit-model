package enterprises.orbital.evekit.model.common;

import enterprises.orbital.evekit.TestBase;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AbstractModelTester;
import enterprises.orbital.evekit.model.CachedData;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarkTest extends AbstractModelTester<Bookmark> {
  final int                                 folderID          = TestBase.getRandomInt(100000000);
  final String                              folderName        = "test folder name";
  final long                                folderCreatorID   = TestBase.getRandomInt(100000000);
  final int                                 bookmarkID        = TestBase.getRandomInt(100000000);
  final long                                bookmarkCreatorID = TestBase.getRandomInt(100000000);
  final long                                created           = TestBase.getRandomInt(100000000);
  final long                                itemID            = TestBase.getRandomInt(100000000);
  final int                                 typeID            = TestBase.getRandomInt(100000000);
  final long                                locationID        = TestBase.getRandomInt(100000000);
  final double                              x                 = TestBase.getRandomDouble(100000000);
  final double                              y                 = TestBase.getRandomDouble(100000000);
  final double                              z                 = TestBase.getRandomDouble(100000000);
  final String                              memo              = "test memo";
  final String                              note              = "test note";

  final ClassUnderTestConstructor<Bookmark> eol               = new ClassUnderTestConstructor<Bookmark>() {

                                                                @Override
                                                                public Bookmark getCUT() {
                                                                  return new Bookmark(
                                                                      folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID,
                                                                      typeID, locationID, x, y, z, memo, note);
                                                                }

                                                              };

  final ClassUnderTestConstructor<Bookmark> live              = new ClassUnderTestConstructor<Bookmark>() {
                                                                @Override
                                                                public Bookmark getCUT() {
                                                                  return new Bookmark(
                                                                      folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID,
                                                                      typeID, locationID, x, y, z, memo, note);
                                                                }

                                                              };

  @Test
  public void testBasic() throws Exception {

    runBasicTests(eol, new CtorVariants<Bookmark>() {

      @Override
      public Bookmark[] getVariants() {
        return new Bookmark[] {
            new Bookmark(folderID + 1, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName + "1", folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID + 1, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID + 1, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID + 1, created, itemID, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created + 1, itemID, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID + 1, typeID, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID + 1, locationID, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID + 1, x, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x + 1, y, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y + 1, z, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z + 1, memo, note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo + "1", note),
            new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note + "1")
        };
      }

    }, AccountAccessMask.createMask(AccountAccessMask.ACCESS_BOOKMARKS));
  }

  @Test
  public void testGetLifeline() throws Exception {

    runGetLifelineTest(eol, live, new ModelRetriever<Bookmark>() {

      @Override
      public Bookmark getModel(SynchronizedEveAccount account, long time) {
        return Bookmark.get(account, time, folderID, bookmarkID);
      }

    });
  }

  @Test
  public void testGetAllBookmarks() throws Exception {
    // Should exclude:
    // - bookmarks for a different account
    // - bookmarks not live at the given time
    Bookmark existing;
    Map<Integer, Map<Integer, Bookmark>> listCheck = new HashMap<Integer, Map<Integer, Bookmark>>();

    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(folderID, new HashMap<Integer, Bookmark>());
    listCheck.get(folderID).put(bookmarkID, existing);

    existing = new Bookmark(
        folderID, folderName, folderCreatorID, bookmarkID + 10, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(folderID).put(bookmarkID + 10, existing);

    existing = new Bookmark(
        folderID + 10, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(folderID + 10, new HashMap<Integer, Bookmark>());
    listCheck.get(folderID + 10).put(bookmarkID, existing);

    existing = new Bookmark(
        folderID + 10, folderName, folderCreatorID, bookmarkID + 10, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(folderID + 10).put(bookmarkID + 10, existing);

    // Associated with different account
    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID + 5, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID + 3, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all bookmarks are returned
    List<Bookmark> result = Bookmark.getAllBookmarks(testAccount, 8888L);
    Assert.assertEquals(4, result.size());
    for (Bookmark next : result) {
      int folderID = next.getFolderID();
      int bookmarkID = next.getBookmarkID();
      Assert.assertTrue(listCheck.containsKey(folderID));
      Assert.assertTrue(listCheck.get(folderID).containsKey(bookmarkID));
      Assert.assertEquals(listCheck.get(folderID).get(bookmarkID), next);
    }
  }

  @Test
  public void testGetByFolderID() throws Exception {
    // Should exclude:
    // - bookmarks for a different account
    // - bookmarks not live at the given time
    // - bookmarks for a different list
    Bookmark existing;
    Map<Integer, Map<Integer, Bookmark>> listCheck = new HashMap<Integer, Map<Integer, Bookmark>>();

    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.put(folderID, new HashMap<Integer, Bookmark>());
    listCheck.get(folderID).put(bookmarkID, existing);

    existing = new Bookmark(
        folderID, folderName, folderCreatorID, bookmarkID + 10, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(folderID).put(bookmarkID + 10, existing);

    existing = new Bookmark(
        folderID, folderName, folderCreatorID, bookmarkID + 20, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(folderID).put(bookmarkID + 20, existing);

    existing = new Bookmark(
        folderID, folderName, folderCreatorID, bookmarkID + 30, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing = CachedData.update(existing);
    listCheck.get(folderID).put(bookmarkID + 30, existing);

    // Associated with different account
    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Associated with different folder
    existing = new Bookmark(
        folderID + 10, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(otherAccount, 7777L);
    CachedData.update(existing);

    // Not live at the given time
    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID + 5, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 9999L);
    CachedData.update(existing);

    // EOL before the given time
    existing = new Bookmark(folderID, folderName, folderCreatorID, bookmarkID + 3, bookmarkCreatorID, created, itemID, typeID, locationID, x, y, z, memo, note);
    existing.setup(testAccount, 7777L);
    existing.evolve(null, 7977L);
    CachedData.update(existing);

    // Verify all bookmarks are returned
    List<Bookmark> result = Bookmark.getByFolderID(testAccount, 8888L, folderID);
    Assert.assertEquals(4, result.size());
    for (Bookmark next : result) {
      int folderID = next.getFolderID();
      int bookmarkID = next.getBookmarkID();
      Assert.assertTrue(listCheck.containsKey(folderID));
      Assert.assertTrue(listCheck.get(folderID).containsKey(bookmarkID));
      Assert.assertEquals(listCheck.get(folderID).get(bookmarkID), next);
    }

  }

}
