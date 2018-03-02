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
    name = "evekit_data_mailing_list",
    indexes = {
        @Index(
            name = "listIDIndex",
            columnList = "listID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MailingList.getByListID",
        query = "SELECT c FROM MailingList c where c.owner = :owner and c.listID = :list and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MailingList extends CachedData {
  private static final Logger log = Logger.getLogger(MailingList.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAILING_LISTS);

  private String displayName;
  private int listID;

  @SuppressWarnings("unused")
  protected MailingList() {}

  public MailingList(String displayName, int listID) {
    super();
    this.displayName = displayName;
    this.listID = listID;
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
    if (!(sup instanceof MailingList)) return false;
    MailingList other = (MailingList) sup;
    return nullSafeObjectCompare(displayName, other.displayName) && listID == other.listID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getListID() {
    return listID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MailingList that = (MailingList) o;
    return listID == that.listID &&
        Objects.equals(displayName, that.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), displayName, listID);
  }

  @Override
  public String toString() {
    return "MailingList{" +
        "displayName='" + displayName + '\'' +
        ", listID=" + listID +
        '}';
  }

  public static MailingList get(
      final SynchronizedEveAccount owner,
      final long time,
      final int listID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MailingList> getter = EveKitUserAccountProvider.getFactory()
                                                                                                  .getEntityManager()
                                                                                                  .createNamedQuery(
                                                                                                      "MailingList.getByListID",
                                                                                                      MailingList.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("list", listID);
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

  public static List<MailingList> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector displayName,
      final AttributeSelector listID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MailingList c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addStringSelector(qs, "c", "displayName", displayName, p);
                                        AttributeSelector.addIntSelector(qs, "c", "listID", listID);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MailingList> query = EveKitUserAccountProvider.getFactory()
                                                                                                 .getEntityManager()
                                                                                                 .createQuery(
                                                                                                     qs.toString(),
                                                                                                     MailingList.class);
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
