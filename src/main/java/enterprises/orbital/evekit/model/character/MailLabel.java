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
    name = "evekit_data_mail_label",
    indexes = {
        @Index(
            name = "labelIDIndex",
            columnList = "labelID"),
    })
@NamedQueries({
    @NamedQuery(
        name = "MailLabel.get",
        query = "SELECT c FROM MailLabel c where c.owner = :owner and c.labelID = :lid and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class MailLabel extends CachedData {
  private static final Logger log = Logger.getLogger(MailLabel.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_MAIL);

  private int labelID;
  private int unreadCount;
  private String name;
  private String color;

  @SuppressWarnings("unused")
  protected MailLabel() {}

  public MailLabel(int labelID, int unreadCount, String name, String color) {
    this.labelID = labelID;
    this.unreadCount = unreadCount;
    this.name = name;
    this.color = color;
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
    if (!(sup instanceof MailLabel)) return false;
    MailLabel other = (MailLabel) sup;
    return labelID == other.labelID &&
        unreadCount == other.unreadCount &&
        nullSafeObjectCompare(name, other.name) &&
        nullSafeObjectCompare(color, other.color);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getLabelID() {
    return labelID;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MailLabel mailLabel = (MailLabel) o;
    return labelID == mailLabel.labelID &&
        unreadCount == mailLabel.unreadCount &&
        Objects.equals(name, mailLabel.name) &&
        Objects.equals(color, mailLabel.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), labelID, unreadCount, name, color);
  }

  @Override
  public String toString() {
    return "MailLabel{" +
        "labelID=" + labelID +
        ", unreadCount=" + unreadCount +
        ", name='" + name + '\'' +
        ", color='" + color + '\'' +
        '}';
  }

  public static MailLabel get(
      final SynchronizedEveAccount owner,
      final long time,
      final int labelID) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<MailLabel> getter = EveKitUserAccountProvider.getFactory()
                                                                                                .getEntityManager()
                                                                                                .createNamedQuery(
                                                                                                    "MailLabel.get",
                                                                                                    MailLabel.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("lid", labelID);
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

  public static List<MailLabel> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector labelID,
      final AttributeSelector unreadCount,
      final AttributeSelector name,
      final AttributeSelector color) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM MailLabel c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addIntSelector(qs, "c", "labelID", labelID);
                                        AttributeSelector.addIntSelector(qs, "c", "unreadCount", unreadCount);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        AttributeSelector.addStringSelector(qs, "c", "color", color, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<MailLabel> query = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createQuery(
                                                                                                   qs.toString(),
                                                                                                   MailLabel.class);
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
