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
    name = "evekit_data_character_opportunities",
    indexes = {
        @Index(
            name = "taskIDIndex",
            columnList = "taskID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Opportunity.get",
        query = "SELECT c FROM Opportunity c where c.owner = :owner and c.taskID = :tid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Opportunity extends CachedData {
  private static final Logger log = Logger.getLogger(Opportunity.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);

  private int taskID;
  private long completedAt;

  @Transient
  @ApiModelProperty(
      value = "completedAt Date")
  @JsonProperty("completedAtDate")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date completedAtDate;

  @SuppressWarnings("unused")
  protected Opportunity() {}

  public Opportunity(int taskID, long completedAt) {
    this.taskID = taskID;
    this.completedAt = completedAt;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    completedAtDate = assignDateField(completedAt);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof Opportunity)) return false;
    Opportunity other = (Opportunity) sup;
    return taskID == other.taskID &&
        completedAt == other.completedAt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getTaskID() {
    return taskID;
  }

  public long getCompletedAt() {
    return completedAt;
  }

  @Override
  public String toString() {
    return "Opportunity{" +
        "taskID=" + taskID +
        ", completedAt=" + completedAt +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Opportunity that = (Opportunity) o;
    return taskID == that.taskID &&
        completedAt == that.completedAt;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), taskID, completedAt);
  }

  public static Opportunity get(
      final SynchronizedEveAccount owner,
      final long time,
      final int taskID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Opportunity> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                  "Opportunity.get",
                                                                                                  Opportunity.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("tid", taskID);
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

  public static List<Opportunity> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector taskID,
      final AttributeSelector completedAt) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Opportunity c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "taskID", taskID);
                                        AttributeSelector.addIntSelector(qs, "c", "completedAt", completedAt);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Opportunity> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(qs.toString(),
                                                                                                          Opportunity.class);
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
