package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_corporation_mining_extraction",
    indexes = {
        @Index(
            name = "miningExtractionIndex",
            columnList = "moonID,structureID,extractionStartTime"),
    }
)
@NamedQueries({
    @NamedQuery(
        name = "MiningExtraction.get",
        query = "SELECT c FROM MiningExtraction c where c.owner = :owner and c.moonID = :mid and c.structureID = :sid and c.extractionStartTime = :est and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MiningExtraction extends CachedData {
  private static final Logger log = Logger.getLogger(MiningExtraction.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MINING_LEDGER);

  private int moonID;
  private long structureID;
  private long extractionStartTime;
  private long chunkArrivalTime;
  private long naturalDecayTime;

  @Transient
  @ApiModelProperty(
      value = "extractionStartTime Date")
  @JsonProperty("extractionStartTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date extractionStartTimeDate;

  @Transient
  @ApiModelProperty(
      value = "chunkArrivalTime Date")
  @JsonProperty("chunkArrivalTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date chunkArrivalTimeDate;

  @Transient
  @ApiModelProperty(
      value = "naturalDecayTime Date")
  @JsonProperty("naturalDecayTimeDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date naturalDecayTimeDate;

  @SuppressWarnings("unused")
  protected MiningExtraction() {}

  public MiningExtraction(int moonID, long structureID, long extractionStartTime, long chunkArrivalTime,
                          long naturalDecayTime) {
    this.moonID = moonID;
    this.structureID = structureID;
    this.extractionStartTime = extractionStartTime;
    this.chunkArrivalTime = chunkArrivalTime;
    this.naturalDecayTime = naturalDecayTime;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    extractionStartTimeDate = assignDateField(extractionStartTime);
    chunkArrivalTimeDate = assignDateField(chunkArrivalTime);
    naturalDecayTimeDate = assignDateField(naturalDecayTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof MiningExtraction)) return false;
    MiningExtraction other = (MiningExtraction) sup;
    return moonID == other.moonID &&
        structureID == other.structureID &&
        extractionStartTime == other.extractionStartTime &&
        chunkArrivalTime == other.chunkArrivalTime &&
        naturalDecayTime == other.naturalDecayTime;
  }

  @Override
  public String dataHash() {
    return dataHashHelper(moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getMoonID() {
    return moonID;
  }

  public long getStructureID() {
    return structureID;
  }

  public long getExtractionStartTime() {
    return extractionStartTime;
  }

  public long getChunkArrivalTime() {
    return chunkArrivalTime;
  }

  public long getNaturalDecayTime() {
    return naturalDecayTime;
  }

  @Override
  public String toString() {
    return "MiningExtraction{" +
        "moonID=" + moonID +
        ", structureID=" + structureID +
        ", extractionStartTime=" + extractionStartTime +
        ", chunkArrivalTime=" + chunkArrivalTime +
        ", naturalDecayTime=" + naturalDecayTime +
        ", extractionStartTimeDate=" + extractionStartTimeDate +
        ", chunkArrivalTimeDate=" + chunkArrivalTimeDate +
        ", naturalDecayTimeDate=" + naturalDecayTimeDate +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MiningExtraction that = (MiningExtraction) o;
    return moonID == that.moonID &&
        structureID == that.structureID &&
        extractionStartTime == that.extractionStartTime &&
        chunkArrivalTime == that.chunkArrivalTime &&
        naturalDecayTime == that.naturalDecayTime;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), moonID, structureID, extractionStartTime, chunkArrivalTime, naturalDecayTime);
  }

  public static MiningExtraction get(
      final SynchronizedEveAccount owner,
      final long time,
      final int moonID,
      final long structureID,
      final long extractionStartTime) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MiningExtraction> getter = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createNamedQuery(
                                                                                                           "MiningExtraction.get",
                                                                                                           MiningExtraction.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("mid", moonID);
                                        getter.setParameter("sid", structureID);
                                        getter.setParameter("est", extractionStartTime);
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

  public static List<MiningExtraction> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector moonID,
      final AttributeSelector structureID,
      final AttributeSelector extractionStartTime,
      final AttributeSelector chunkArrivalTime,
      final AttributeSelector naturalDecayTime) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MiningExtraction c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "moonID", moonID);
                                        AttributeSelector.addLongSelector(qs, "c", "structureID", structureID);
                                        AttributeSelector.addLongSelector(qs, "c", "extractionStartTime",
                                                                          extractionStartTime);
                                        AttributeSelector.addLongSelector(qs, "c", "chunkArrivalTime",
                                                                          chunkArrivalTime);
                                        AttributeSelector.addLongSelector(qs, "c", "naturalDecayTime",
                                                                          naturalDecayTime);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MiningExtraction> query = EveKitUserAccountProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createQuery(
                                                                                                          qs.toString(),
                                                                                                          MiningExtraction.class);
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
