package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_structure_services",
    indexes = {
        @Index(
            name = "structureServiceIndex",
            columnList = "structureID, name"),
    })
@NamedQueries({
    @NamedQuery(
        name = "StructureService.get",
        query = "SELECT c FROM StructureService c where c.owner = :owner and c.structureID = :sid and c.name = :name and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class StructureService extends CachedData {
  private static final Logger log = Logger.getLogger(StructureService.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_STRUCTURES);

  private long structureID;
  private String name;
  private String state;

  @SuppressWarnings("unused")
  protected StructureService() {}

  public StructureService(long structureID, String name, String state) {
    this.structureID = structureID;
    this.name = name;
    this.state = state;
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
    if (!(sup instanceof StructureService)) return false;
    StructureService other = (StructureService) sup;
    return structureID == other.structureID &&
        nullSafeObjectCompare(name, other.name) &&
        nullSafeObjectCompare(state, other.state);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public long getStructureID() {
    return structureID;
  }

  public String getName() {
    return name;
  }

  public String getState() {
    return state;
  }

  @Override
  public String toString() {
    return "StructureServiceService{" +
        "structureID=" + structureID +
        ", name='" + name + '\'' +
        ", state='" + state + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StructureService that = (StructureService) o;
    return structureID == that.structureID &&
        Objects.equals(name, that.name) &&
        Objects.equals(state, that.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), structureID, name, state);
  }

  public static StructureService get(
      final SynchronizedEveAccount owner,
      final long time,
      final long structureID,
      final String name) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<StructureService> getter = EveKitUserAccountProvider.getFactory()
                                                                                                       .getEntityManager()
                                                                                                       .createNamedQuery(
                                                                                                    "StructureService.get",
                                                                                                    StructureService.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("sid", structureID);
                                        getter.setParameter("name", name);
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

  public static List<StructureService> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector structureID,
      final AttributeSelector name,
      final AttributeSelector state) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM StructureService c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addLongSelector(qs, "c", "structureID", structureID);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        AttributeSelector.addStringSelector(qs, "c", "name", state, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<StructureService> query = EveKitUserAccountProvider.getFactory()
                                                                                                      .getEntityManager()
                                                                                                      .createQuery(
                                                                                                   qs.toString(),
                                                                                                   StructureService.class);
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
