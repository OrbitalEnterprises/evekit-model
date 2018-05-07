package enterprises.orbital.evekit.model.character;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
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
    name = "evekit_data_character_mining_ledger",
    indexes = {
        @Index(
            name = "miningLedgerIndex",
            columnList = "date,solarSystemID,typeID"),
    }
)
@NamedQueries({
    @NamedQuery(
        name = "MiningLedger.get",
        query = "SELECT c FROM MiningLedger c where c.owner = :owner and c.date = :dt and c.solarSystemID = :sid and c.typeID = :tid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MiningLedger extends CachedData {
  private static final Logger log = Logger.getLogger(MiningLedger.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER);

  private long date;
  private int solarSystemID;
  private int typeID;
  private long quantity;

  @Transient
  @ApiModelProperty(
      value = "date Date")
  @JsonProperty("dateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date dateDate;

  @SuppressWarnings("unused")
  protected MiningLedger() {}

  public MiningLedger(long date, int solarSystemID, int typeID, long quantity) {
    this.date = date;
    this.solarSystemID = solarSystemID;
    this.typeID = typeID;
    this.quantity = quantity;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    dateDate = assignDateField(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MiningLedger)) return false;
    MiningLedger other = (MiningLedger) sup;
    return date == other.date &&
        solarSystemID == other.solarSystemID &&
        typeID == other.typeID &&
        quantity == other.quantity;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getDate() {
    return date;
  }

  public int getSolarSystemID() {
    return solarSystemID;
  }

  public int getTypeID() {
    return typeID;
  }

  public long getQuantity() {
    return quantity;
  }

  @Override
  public String toString() {
    return "MiningLedger{" +
        "date=" + date +
        ", solarSystemID=" + solarSystemID +
        ", typeID=" + typeID +
        ", quantity=" + quantity +
        ", dateDate=" + dateDate +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MiningLedger that = (MiningLedger) o;
    return date == that.date &&
        solarSystemID == that.solarSystemID &&
        typeID == that.typeID &&
        quantity == that.quantity;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), date, solarSystemID, typeID, quantity);
  }

  public static MiningLedger get(
      final SynchronizedEveAccount owner,
      final long time,
      final long date,
      final int solarSystemID,
      final int typeID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MiningLedger> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "MiningLedger.get",
                                                                                                       MiningLedger.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("dt", date);
                                        getter.setParameter("sid", solarSystemID);
                                        getter.setParameter("tid", typeID);
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

  public static List<MiningLedger> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector date,
      final AttributeSelector solarSystemID,
      final AttributeSelector typeID,
      final AttributeSelector quantity) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MiningLedger c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addLongSelector(qs, "c", "date", date);
                                        AttributeSelector.addIntSelector(qs, "c", "solarSystemID", solarSystemID);
                                        AttributeSelector.addIntSelector(qs, "c", "typeID", typeID);
                                        AttributeSelector.addLongSelector(qs, "c", "quantity", quantity);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MiningLedger> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      MiningLedger.class);
                                        query.setParameter("owner", owner);
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
