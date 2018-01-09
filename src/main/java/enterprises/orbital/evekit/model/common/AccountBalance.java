package enterprises.orbital.evekit.model.common;

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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Table(
    name = "evekit_data_account_balance",
    indexes = {
        @Index(
            name = "accountDivisionIndex",
            columnList = "division")
    })
@NamedQueries({
    @NamedQuery(
        name = "AccountBalance.getByDivision",
        query = "SELECT c FROM AccountBalance c where c.owner = :owner and c.division = :div and c.lifeStart <= :point and c.lifeEnd > :point"),
    @NamedQuery(
        name = "AccountBalance.getAll",
        query = "SELECT c FROM AccountBalance c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point order by c.cid asc"),
})
public class AccountBalance extends CachedData {
  private static final Logger log = Logger.getLogger(AccountBalance.class.getName());
  private static final byte[] MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_ACCOUNT_BALANCE);
  private int division;
  @Column(
      precision = 19,
      scale = 2)
  private BigDecimal balance;

  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* accountID")
  @JsonProperty("accountID")
  private int accountID;
  @Deprecated
  @Transient
  @ApiModelProperty(value = "*DEPRECATED* accountKey")
  @JsonProperty("accountKey")
  private int accountKey;

  @SuppressWarnings("unused")
  protected AccountBalance() {}

  public AccountBalance(int division, BigDecimal balance) {
    this.division = division;
    this.balance = balance;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareTransient() {
    fixDates();
    accountID = 0;
    accountKey = division - 1 + 1000;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      CachedData sup) {
    if (!(sup instanceof AccountBalance)) return false;
    AccountBalance other = (AccountBalance) sup;
    return division == other.division && nullSafeObjectCompare(balance, other.balance);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public int getDivision() {
    return division;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AccountBalance that = (AccountBalance) o;
    return division == that.division &&
        Objects.equals(balance, that.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), division, balance);
  }

  @Override
  public String toString() {
    return "AccountBalance{" +
        "division=" + division +
        ", balance=" + balance +
        '}';
  }

  /**
   * Retrieve existing account balance with the given division and which is live at the given time. Returns null if no such account balance exists.
   *
   * @param owner    account balance owner
   * @param time     time at which the account balance should be live
   * @param division account division
   * @return an existing account balance, or null.
   */
  public static AccountBalance get(
      final SynchronizedEveAccount owner,
      final long time,
      final int division) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        TypedQuery<AccountBalance> getter = EveKitUserAccountProvider.getFactory()
                                                                                                     .getEntityManager()
                                                                                                     .createNamedQuery("AccountBalance.getByDivision",
                                                                                                                       AccountBalance.class);
                                        getter.setParameter("owner", owner);
                                        getter.setParameter("div", division);
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

  public static List<AccountBalance> accessQuery(
      final SynchronizedEveAccount owner,
      final long contid,
      final int maxresults,
      final boolean reverse,
      final AttributeSelector at,
      final AttributeSelector division,
      final AttributeSelector balance) throws IOException {
    try {
      return EveKitUserAccountProvider.getFactory()
                                      .runTransaction(() -> {
                                        StringBuilder qs = new StringBuilder();
                                        qs.append("SELECT c FROM AccountBalance c WHERE ");
                                        // Constrain to specified owner
                                        qs.append("c.owner = :owner");
                                        // Constrain lifeline
                                        AttributeSelector.addLifelineSelector(qs, "c", at);
                                        // Constrain attributes
                                        AttributeSelector.addIntSelector(qs, "c", "division", division);
                                        AttributeSelector.addDoubleSelector(qs, "c", "balance", balance);
                                        // Set CID constraint and ordering
                                        setCIDOrdering(qs, contid, reverse);
                                        // Return result
                                        TypedQuery<AccountBalance> query = EveKitUserAccountProvider.getFactory()
                                                                                                    .getEntityManager()
                                                                                                    .createQuery(qs.toString(), AccountBalance.class);
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
