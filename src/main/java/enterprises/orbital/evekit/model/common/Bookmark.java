package enterprises.orbital.evekit.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_bookmark",
    indexes = {
        @Index(
            name = "folderIDIndex",
            columnList = "folderID"),
        @Index(
            name = "bookmarkIDIndex",
            columnList = "bookmarkID")
    })
@NamedQueries({
    @NamedQuery(
        name = "Bookmark.getByFolderAndBookmarkID",
        query = "SELECT c FROM Bookmark c where c.owner = :owner and c.folderID = :folder and c.bookmarkID = :bookmark and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "Bookmark.getAll",
        query = "SELECT c FROM Bookmark c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
    @NamedQuery(
        name = "Bookmark.getAllByFolderID",
        query = "SELECT c FROM Bookmark c where c.owner = :owner and c.folderID = :folder and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
public class Bookmark extends CachedData {
  private static final Logger log = Logger.getLogger(Bookmark.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_BOOKMARKS);
  // Folder Info
  private int folderID;
  private String folderName;
  private int folderCreatorID;
  // Bookmark Info
  private int bookmarkID;
  private int bookmarkCreatorID;
  private long created = -1;
  private long itemID;
  private int typeID;
  private int locationID;
  private double x;
  private double y;
  private double z;
  @Lob
  @Column(
      length = 102400)
  private String memo;
  @Lob
  @Column(
      length = 102400)
  private String note;
  @Transient
  @ApiModelProperty(
      value = "created Date")
  @JsonProperty("createdDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date createdDate;

  @SuppressWarnings("unused")
  protected Bookmark() {}

  public Bookmark(int folderID, String folderName, int folderCreatorID, int bookmarkID, int bookmarkCreatorID,
                  long created, long itemID, int typeID,
                  int locationID, double x, double y, double z, String memo, String note) {
    this.folderID = folderID;
    this.folderName = folderName;
    this.folderCreatorID = folderCreatorID;
    this.bookmarkID = bookmarkID;
    this.bookmarkCreatorID = bookmarkCreatorID;
    this.created = created;
    this.itemID = itemID;
    this.typeID = typeID;
    this.locationID = locationID;
    this.x = x;
    this.y = y;
    this.z = z;
    this.memo = memo;
    this.note = note;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    createdDate = assignDateField(created);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof Bookmark)) return false;
    Bookmark other = (Bookmark) sup;
    return folderID == other.folderID && nullSafeObjectCompare(folderName,
                                                               other.folderName) && folderCreatorID == other.folderCreatorID
        && bookmarkID == other.bookmarkID && bookmarkCreatorID == other.bookmarkCreatorID && created == other.created && itemID == other.itemID
        && typeID == other.typeID && locationID == other.locationID &&
        doubleCompare(x, other.x, 0.000000001D) &&
        doubleCompare(y, other.y, 0.000000001D) &&
        doubleCompare(z, other.z, 0.000000001D) &&
        nullSafeObjectCompare(memo, other.memo)
        && nullSafeObjectCompare(note, other.note);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getFolderID() {
    return folderID;
  }

  public String getFolderName() {
    return folderName;
  }

  public int getFolderCreatorID() {
    return folderCreatorID;
  }

  public int getBookmarkID() {
    return bookmarkID;
  }

  public int getBookmarkCreatorID() {
    return bookmarkCreatorID;
  }

  public long getCreated() {
    return created;
  }

  public long getItemID() {
    return itemID;
  }

  public int getTypeID() {
    return typeID;
  }

  public int getLocationID() {
    return locationID;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public String getMemo() {
    return memo;
  }

  public String getNote() {
    return note;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Bookmark bookmark = (Bookmark) o;
    return folderID == bookmark.folderID &&
        folderCreatorID == bookmark.folderCreatorID &&
        bookmarkID == bookmark.bookmarkID &&
        bookmarkCreatorID == bookmark.bookmarkCreatorID &&
        created == bookmark.created &&
        itemID == bookmark.itemID &&
        typeID == bookmark.typeID &&
        locationID == bookmark.locationID &&
        Double.compare(bookmark.x, x) == 0 &&
        Double.compare(bookmark.y, y) == 0 &&
        Double.compare(bookmark.z, z) == 0 &&
        Objects.equals(folderName, bookmark.folderName) &&
        Objects.equals(memo, bookmark.memo) &&
        Objects.equals(note, bookmark.note);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), folderID, folderName, folderCreatorID, bookmarkID, bookmarkCreatorID, created,
                        itemID, typeID, locationID, x, y, z, memo, note);
  }

  @Override
  public String toString() {
    return "Bookmark{" +
        "folderID=" + folderID +
        ", folderName='" + folderName + '\'' +
        ", folderCreatorID=" + folderCreatorID +
        ", bookmarkID=" + bookmarkID +
        ", bookmarkCreatorID=" + bookmarkCreatorID +
        ", created=" + created +
        ", itemID=" + itemID +
        ", typeID=" + typeID +
        ", locationID=" + locationID +
        ", x=" + x +
        ", y=" + y +
        ", z=" + z +
        ", memo='" + memo + '\'' +
        ", note='" + note + '\'' +
        ", createdDate=" + createdDate +
        '}';
  }

  public static Bookmark get(
      final SynchronizedEveAccount owner,
      final long time,
      final int folderID,
      final int bookmarkID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Bookmark> getter = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery(
                                                                                                   "Bookmark.getByFolderAndBookmarkID",
                                                                                                   Bookmark.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("folder", folderID);
                                        getter.setParameter("bookmark", bookmarkID);
                                        getter.setParameter("point", time);
                                        try {
                                          return getter.getSingleResult();
                                        } catch (NoResultException e) {
                                          return null;
                                        }
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

  public static List<Bookmark> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector folderID,
      final AttributeSelector folderName,
      final AttributeSelector folderCreatorID,
      final AttributeSelector bookmarkID,
      final AttributeSelector bookmarkCreatorID,
      final AttributeSelector created,
      final AttributeSelector itemID,
      final AttributeSelector typeID,
      final AttributeSelector locationID,
      final AttributeSelector x,
      final AttributeSelector y,
      final AttributeSelector z,
      final AttributeSelector memo,
      final AttributeSelector note) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Bookmark c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "folderID", folderID);
                                        AttributeSelector.addStringSelector(qs, "c", "folderName", folderName, p);
                                        AttributeSelector.addIntSelector(qs, "c", "folderCreatorID", folderCreatorID);
                                        AttributeSelector.addIntSelector(qs, "c", "bookmarkID", bookmarkID);
                                        AttributeSelector.addIntSelector(qs, "c", "bookmarkCreatorID",
                                                                         bookmarkCreatorID);
                                        AttributeSelector.addLongSelector(qs, "c", "created", created);
                                        AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addIntSelector(qs, "c", "locationID", locationID);
                                        AttributeSelector.addDoubleSelector(qs, "c", "x", x);
                                        AttributeSelector.addDoubleSelector(qs, "c", "y", y);
                                        AttributeSelector.addDoubleSelector(qs, "c", "z", z);
                                        AttributeSelector.addStringSelector(qs, "c", "memo", memo, p);
                                        AttributeSelector.addStringSelector(qs, "c", "note", note, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Bookmark> query = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(
                                                                                                  qs.toString(),
                                                                                                  Bookmark.class);
                                        query.setParameter("owner", owner);
                                        p.fillParams(query);
                                        query.setMaxResults(maxresults);
                                        return query.getResultList();
                                      });
    } catch (Exception e) {
      if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
      log.log(Level.SEVERE, "query error", e);
      throw new IOException(e.getCause());
    }
  }

}
