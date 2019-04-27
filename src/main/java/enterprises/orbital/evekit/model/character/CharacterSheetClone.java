package enterprises.orbital.evekit.model.character;

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
    name = "evekit_data_character_sheet_clone")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheetClone.get",
        query = "SELECT c FROM CharacterSheetClone c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class CharacterSheetClone extends CachedData {
  protected static final Logger log = Logger.getLogger(CharacterSheetClone.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private long cloneJumpDate = -1;
  private long homeStationID;
  private String homeStationType;
  private long lastStationChangeDate = -1;

  @Transient
  @ApiModelProperty(
      value = "cloneJumpDate Date")
  @JsonProperty("cloneJumpDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date cloneJumpDateDate;

  @Transient
  @ApiModelProperty(
      value = "lastStationChangeDate Date")
  @JsonProperty("lastStationChangeDateDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date lastStationChangeDateDate;

  @SuppressWarnings("unused")
  protected CharacterSheetClone() {}

  public CharacterSheetClone(long cloneJumpDate, long homeStationID, String homeStationType,
                             long lastStationChangeDate) {
    this.cloneJumpDate = cloneJumpDate;
    this.homeStationID = homeStationID;
    this.homeStationType = homeStationType;
    this.lastStationChangeDate = lastStationChangeDate;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    cloneJumpDateDate = assignDateField(cloneJumpDate);
    lastStationChangeDateDate = assignDateField(lastStationChangeDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof CharacterSheetClone)) return false;
    CharacterSheetClone other = (CharacterSheetClone) sup;
    return cloneJumpDate == other.cloneJumpDate &&
        homeStationID == other.homeStationID &&
        nullSafeObjectCompare(homeStationType, other.homeStationType) &&
        lastStationChangeDate == other.lastStationChangeDate;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(cloneJumpDate, homeStationID, homeStationType, lastStationChangeDate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getCloneJumpDate() {
    return cloneJumpDate;
  }

  public long getHomeStationID() {
    return homeStationID;
  }

  public String getHomeStationType() {
    return homeStationType;
  }

  public long getLastStationChangeDate() {
    return lastStationChangeDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacterSheetClone that = (CharacterSheetClone) o;
    return cloneJumpDate == that.cloneJumpDate &&
        homeStationID == that.homeStationID &&
        lastStationChangeDate == that.lastStationChangeDate &&
        Objects.equals(homeStationType, that.homeStationType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), cloneJumpDate, homeStationID, homeStationType, lastStationChangeDate);
  }

  @Override
  public String toString() {
    return "CharacterSheetClone{" +
        "cloneJumpDate=" + cloneJumpDate +
        ", homeStationID=" + homeStationID +
        ", homeStationType='" + homeStationType + '\'' +
        ", lastStationChangeDate=" + lastStationChangeDate +
        ", cloneJumpDateDate=" + cloneJumpDateDate +
        ", lastStationChangeDateDate=" + lastStationChangeDateDate +
        '}';
  }

  public static CharacterSheetClone get(
      final SynchronizedEveAccount owner,
      final long time) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<CharacterSheetClone> getter = EveKitUserAccountProvider.getFactory()
                                                                                                          .getEntityManager()
                                                                                                          .createNamedQuery(
                                                                                                              "CharacterSheetClone.get",
                                                                                                              CharacterSheetClone.class);
                                        getter.setParameter("owner", owner);
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

  public static List<CharacterSheetClone> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector cloneJumpDate,
      final AttributeSelector homeStationID,
      final AttributeSelector homeStationType,
      final AttributeSelector lastStationChangeDate) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM CharacterSheetClone c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "cloneJumpDate", cloneJumpDate);
                                        AttributeSelector.addLongSelector(qs, "c", "homeStationID", homeStationID);
                                        AttributeSelector.addStringSelector(qs, "c", "homeStationType", homeStationType,
                                                                            p);
                                        AttributeSelector.addLongSelector(qs, "c", "lastStationChangeDate",
                                                                          lastStationChangeDate);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<CharacterSheetClone> query = EveKitUserAccountProvider.getFactory()
                                                                                                         .getEntityManager()
                                                                                                         .createQuery(
                                                                                                             qs.toString(),
                                                                                                             CharacterSheetClone.class);
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
