package enterprises.orbital.evekit.model.common;

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
    name = "evekit_data_contact_label",
    indexes = {
        @Index(
            name = "listIndex",
            columnList = "list"),
        @Index(
            name = "labelIDIndex",
            columnList = "labelID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "ContactLabel.getByListAndLabelID",
        query = "SELECT c FROM ContactLabel c where c.owner = :owner and c.list = :list and c.labelID = :label and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class ContactLabel extends CachedData {
  private static final Logger log = Logger.getLogger(ContactLabel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CONTACT_LIST);

  private String list;
  private long labelID;
  private String name;

  @SuppressWarnings("unused")
  protected ContactLabel() {}

  public ContactLabel(String list, long labelID, String name) {
    super();
    this.list = list;
    this.labelID = labelID;
    this.name = name;
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
    if (!(sup instanceof ContactLabel)) return false;
    ContactLabel other = (ContactLabel) sup;
    return nullSafeObjectCompare(list, other.list) && labelID == other.labelID && nullSafeObjectCompare(name,
                                                                                                        other.name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getList() {
    return list;
  }

  public long getLabelID() {
    return labelID;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ContactLabel that = (ContactLabel) o;
    return labelID == that.labelID &&
        Objects.equals(list, that.list) &&
        Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), list, labelID, name);
  }

  @Override
  public String toString() {
    return "ContactLabel{" +
        "list='" + list + '\'' +
        ", labelID=" + labelID +
        ", name='" + name + '\'' +
        '}';
  }

  public static ContactLabel get(
      final SynchronizedEveAccount owner,
      final long time,
      final String list,
      final long labelID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<ContactLabel> getter = EveKitUserAccountProvider.getFactory()
                                                                                                   .getEntityManager()
                                                                                                   .createNamedQuery(
                                                                                                       "ContactLabel.getByListAndLabelID",
                                                                                                       ContactLabel.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("list", list);
                                        getter.setParameter("label", labelID);
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

  public static List<ContactLabel> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector list,
      final AttributeSelector labelID,
      final AttributeSelector name) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM ContactLabel c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addStringSelector(qs, "c", "list", list, p);
                                        AttributeSelector.addLongSelector(qs, "c", "labelID", labelID);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<ContactLabel> query = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createQuery(
                                                                                                      qs.toString(),
                                                                                                      ContactLabel.class);
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
