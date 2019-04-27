package enterprises.orbital.evekit.model.corporation;

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
    name = "evekit_data_division",
    indexes = {
        @Index(
            name = "walletIndex",
            columnList = "wallet"),
        @Index(
            name = "divisionIndex",
            columnList = "division"),
    })
@NamedQueries({
    @NamedQuery(
        name = "Division.getByWalletAndAccountKey",
        query = "SELECT c FROM Division c where c.owner = :owner and c.wallet = :wallet and c.division = :ack and c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class Division extends CachedData {
  private static final Logger log = Logger.getLogger(Division.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CORPORATION_SHEET);

  private boolean wallet;
  private int division;
  private String name;

  @SuppressWarnings("unused")
  protected Division() {}

  public Division(boolean wallet, int division, String name) {
    this.wallet = wallet;
    this.division = division;
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
    if (!(sup instanceof Division)) return false;
    Division other = (Division) sup;
    return wallet == other.wallet && division == other.division && nullSafeObjectCompare(name, other.name);
  }

  @Override
  public String dataHash() {
    return dataHashHelper(wallet, division, name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public boolean isWallet() {
    return wallet;
  }

  public int getDivision() {
    return division;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Division division1 = (Division) o;
    return wallet == division1.wallet &&
        division == division1.division &&
        Objects.equals(name, division1.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), wallet, division, name);
  }

  @Override
  public String toString() {
    return "Division{" +
        "wallet=" + wallet +
        ", division=" + division +
        ", name='" + name + '\'' +
        '}';
  }

  public static Division get(
      final SynchronizedEveAccount owner,
      final long time,
      final boolean wallet,
      final int division) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<Division> getter = EveKitUserAccountProvider.getFactory()
                                                                                               .getEntityManager()
                                                                                               .createNamedQuery(
                                                                                                   "Division.getByWalletAndAccountKey",
                                                                                                   Division.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("wallet", wallet);
                                        getter.setParameter("ack", division);
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

  public static List<Division> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector wallet,
      final AttributeSelector division,
      final AttributeSelector name) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM Division c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeParameters p = new AttributeParameters("att");
                                        AttributeSelector.addBooleanSelector(qs, "c", "wallet", wallet);
                                        AttributeSelector.addIntSelector(qs, "c", "division", division);
                                        AttributeSelector.addStringSelector(qs, "c", "name", name, p);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<Division> query = EveKitUserAccountProvider.getFactory()
                                                                                              .getEntityManager()
                                                                                              .createQuery(
                                                                                                  qs.toString(),
                                                                                                  Division.class);
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
