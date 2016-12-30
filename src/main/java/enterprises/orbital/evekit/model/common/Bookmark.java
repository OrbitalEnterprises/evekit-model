package enterprises.orbital.evekit.model.common;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(
    name = "evekit_data_bookmark",
    indexes = {
        @Index(
            name = "folderIDIndex",
            columnList = "folderID",
            unique = false),
        @Index(
            name = "bookmarkIDIndex",
            columnList = "bookmarkID",
            unique = false)
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
// 2 hour cache time - API caches for 1 hour
public class Bookmark extends CachedData {
  private static final Logger log     = Logger.getLogger(Bookmark.class.getName());
  private static final byte[] MASK    = AccountAccessMask.createMask(AccountAccessMask.ACCESS_BOOKMARKS);
  // Folder Info
  private int                 folderID;
  private String              folderName;
  private long                folderCreatorID;
  // Bookmark Info
  private int                 bookmarkID;
  private long                bookmarkCreatorID;
  private long                created = -1;
  private long                itemID;
  private int                 typeID;
  private long                locationID;
  private double              x;
  private double              y;
  private double              z;
  private String              memo;
  private String              note;
  @Transient
  @ApiModelProperty(
      value = "created Date")
  @JsonProperty("createdDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date                createdDate;

  @SuppressWarnings("unused")
  private Bookmark() {}

  public Bookmark(int folderID, String folderName, long folderCreatorID, int bookmarkID, long bookmarkCreatorID, long created, long itemID, int typeID,
                  long locationID, double x, double y, double z, String memo, String note) {
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
  public void prepareDates() {
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
    return folderID == other.folderID && nullSafeObjectCompare(folderName, other.folderName) && folderCreatorID == other.folderCreatorID
        && bookmarkID == other.bookmarkID && bookmarkCreatorID == other.bookmarkCreatorID && created == other.created && itemID == other.itemID
        && typeID == other.typeID && locationID == other.locationID && x == other.x && y == other.y && z == other.z && nullSafeObjectCompare(memo, other.memo)
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

  public long getFolderCreatorID() {
    return folderCreatorID;
  }

  public int getBookmarkID() {
    return bookmarkID;
  }

  public long getBookmarkCreatorID() {
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

  public long getLocationID() {
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (bookmarkCreatorID ^ (bookmarkCreatorID >>> 32));
    result = prime * result + bookmarkID;
    result = prime * result + (int) (created ^ (created >>> 32));
    result = prime * result + (int) (folderCreatorID ^ (folderCreatorID >>> 32));
    result = prime * result + folderID;
    result = prime * result + ((folderName == null) ? 0 : folderName.hashCode());
    result = prime * result + (int) (itemID ^ (itemID >>> 32));
    result = prime * result + (int) (locationID ^ (locationID >>> 32));
    result = prime * result + ((memo == null) ? 0 : memo.hashCode());
    result = prime * result + ((note == null) ? 0 : note.hashCode());
    result = prime * result + typeID;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Bookmark other = (Bookmark) obj;
    if (bookmarkCreatorID != other.bookmarkCreatorID) return false;
    if (bookmarkID != other.bookmarkID) return false;
    if (created != other.created) return false;
    if (folderCreatorID != other.folderCreatorID) return false;
    if (folderID != other.folderID) return false;
    if (folderName == null) {
      if (other.folderName != null) return false;
    } else if (!folderName.equals(other.folderName)) return false;
    if (itemID != other.itemID) return false;
    if (locationID != other.locationID) return false;
    if (memo == null) {
      if (other.memo != null) return false;
    } else if (!memo.equals(other.memo)) return false;
    if (note == null) {
      if (other.note != null) return false;
    } else if (!note.equals(other.note)) return false;
    if (typeID != other.typeID) return false;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
    if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Bookmark [folderID=" + folderID + ", folderName=" + folderName + ", folderCreatorID=" + folderCreatorID + ", bookmarkID=" + bookmarkID
        + ", bookmarkCreatorID=" + bookmarkCreatorID + ", created=" + created + ", itemID=" + itemID + ", typeID=" + typeID + ", locationID=" + locationID
        + ", x=" + x + ", y=" + y + ", z=" + z + ", memo=" + memo + ", note=" + note + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd
        + "]";
  }

  public static Bookmark get(
                             final SynchronizedEveAccount owner,
                             final long time,
                             final int folderID,
                             final int bookmarkID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<Bookmark>() {
        @Override
        public Bookmark run() throws Exception {
          TypedQuery<Bookmark> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Bookmark.getByFolderAndBookmarkID",
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
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static List<Bookmark> getAllBookmarks(
                                               final SynchronizedEveAccount owner,
                                               final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Bookmark>>() {
        @Override
        public List<Bookmark> run() throws Exception {
          TypedQuery<Bookmark> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Bookmark.getAll", Bookmark.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

  public static List<Bookmark> getByFolderID(
                                             final SynchronizedEveAccount owner,
                                             final long time,
                                             final int folderID) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Bookmark>>() {
        @Override
        public List<Bookmark> run() throws Exception {
          TypedQuery<Bookmark> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("Bookmark.getAllByFolderID", Bookmark.class);
          getter.setParameter("owner", owner);
          getter.setParameter("folder", folderID);
          getter.setParameter("point", time);
          return getter.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
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
                                           final AttributeSelector note) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<List<Bookmark>>() {
        @Override
        public List<Bookmark> run() throws Exception {
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
          AttributeSelector.addLongSelector(qs, "c", "folderCreatorID", folderCreatorID);
          AttributeSelector.addIntSelector(qs, "c", "bookmarkID", bookmarkID);
          AttributeSelector.addLongSelector(qs, "c", "bookmarkCreatorID", bookmarkCreatorID);
          AttributeSelector.addLongSelector(qs, "c", "created", created);
          AttributeSelector.addLongSelector(qs, "c", "itemID", itemID);
          AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
          AttributeSelector.addLongSelector(qs, "c", "locationID", locationID);
          AttributeSelector.addDoubleSelector(qs, "c", "x", x);
          AttributeSelector.addDoubleSelector(qs, "c", "y", y);
          AttributeSelector.addDoubleSelector(qs, "c", "z", z);
          AttributeSelector.addStringSelector(qs, "c", "memo", memo, p);
          AttributeSelector.addStringSelector(qs, "c", "note", note, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<Bookmark> query = EveKitUserAccountProvider.getFactory().getEntityManager().createQuery(qs.toString(), Bookmark.class);
          query.setParameter("owner", owner);
          p.fillParams(query);
          query.setMaxResults(maxresults);
          return query.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
