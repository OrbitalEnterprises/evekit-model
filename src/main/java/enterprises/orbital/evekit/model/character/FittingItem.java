package enterprises.orbital.evekit.model.character;

import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.CachedData;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_character_fitting_item",
    indexes = {
        @Index(
            name = "parentFittingIDIndex",
            columnList = "fittingID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "FittingItem.get",
        query = "SELECT c FROM FittingItem c where c.owner = :owner and c.fittingID = :fid and c.typeID = :tid and c.flag = :flag and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class FittingItem extends CachedData {
  private static final Logger log = Logger.getLogger(FittingItem.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_FITTINGS);

  private int fittingID;
  private int typeID;
  private String flag;
  private int quantity;

  @SuppressWarnings("unused")
  protected FittingItem() {}

  public FittingItem(int fittingID, int typeID, String flag, int quantity) {
    this.fittingID = fittingID;
    this.typeID = typeID;
    this.flag = flag;
    this.quantity = quantity;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof FittingItem)) return false;
    FittingItem other = (FittingItem) sup;
    return fittingID == other.fittingID &&
        typeID == other.typeID &&
        flag.equals(other.flag) &&
        quantity == other.quantity;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getFittingID() {
    return fittingID;
  }

  public int getTypeID() {
    return typeID;
  }

  public String getFlag() {
    return flag;
  }

  public int getQuantity() {
    return quantity;
  }

  @Override
  public String toString() {
    return "FittingItem{" +
        "fittingID=" + fittingID +
        ", typeID=" + typeID +
        ", flag=" + flag +
        ", quantity=" + quantity +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FittingItem that = (FittingItem) o;
    return fittingID == that.fittingID &&
        typeID == that.typeID &&
        quantity == that.quantity &&
        flag.equals(that.flag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), fittingID, typeID, flag, quantity);
  }

  public static FittingItem get(
      final SynchronizedEveAccount owner,
      final long time,
      final int fittingID,
      final int typeID,
      final String flag) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<FittingItem> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "FittingItem.get",
                                                                                                      FittingItem.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("fid", fittingID);
                                        getter.setParameter("tid", typeID);
                                        getter.setParameter("flag", flag);
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

  public static List<FittingItem> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector fittingID,
      final AttributeSelector typeID,
      final AttributeSelector flag,
      final AttributeSelector quantity) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM FittingItem c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "fittingID", fittingID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addStringSelector(qs, "c", "flag", flag, p);
                                        AttributeSelector.addIntSelector(qs, "c", "quantity", quantity);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<FittingItem> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     FittingItem.class);
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
