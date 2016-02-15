package enterprises.orbital.evekit.model.character;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.AccountAccessMask;
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;
import enterprises.orbital.evekit.account.SynchronizedEveAccount;
import enterprises.orbital.evekit.model.CachedData;

@Entity
@Table(name = "evekit_data_character_sheet_balance")
@NamedQueries({
    @NamedQuery(
        name = "CharacterSheetBalance.get",
        query = "SELECT c FROM CharacterSheetBalance c where c.owner = :owner and c.lifeStart <= :point and c.lifeEnd > :point"),
})
// 2 hour cache time - API caches for 1 hour
public class CharacterSheetBalance extends CachedData {
  protected static final Logger log  = Logger.getLogger(CharacterSheetBalance.class.getName());
  private static final byte[]   MASK = AccountAccessMask.createMask(AccountAccessMask.ACCESS_CHARACTER_SHEET);
  // Stores just the balance part of the character sheet since this changes very frequently
  // and we want to avoid having to evolve the entire character sheet.
  @Column(precision = 19, scale = 2)
  private BigDecimal            balance;

  @SuppressWarnings("unused")
  private CharacterSheetBalance() {}

  public CharacterSheetBalance(BigDecimal balance) {
    super();
    this.balance = balance;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(CachedData sup) {
    if (!(sup instanceof CharacterSheetBalance)) return false;
    CharacterSheetBalance other = (CharacterSheetBalance) sup;
    return nullSafeObjectCompare(balance, other.balance);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getMask() {
    return MASK;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((balance == null) ? 0 : balance.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    CharacterSheetBalance other = (CharacterSheetBalance) obj;
    if (balance == null) {
      if (other.balance != null) return false;
    } else if (!balance.equals(other.balance)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "CharacterSheetBalance [balance=" + balance + ", owner=" + owner + ", lifeStart=" + lifeStart + ", lifeEnd=" + lifeEnd + "]";
  }

  public static CharacterSheetBalance get(final SynchronizedEveAccount owner, final long time) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<CharacterSheetBalance>() {
        @Override
        public CharacterSheetBalance run() throws Exception {
          TypedQuery<CharacterSheetBalance> getter = EveKitUserAccountProvider.getFactory().getEntityManager().createNamedQuery("CharacterSheetBalance.get",
                                                                                                                                CharacterSheetBalance.class);
          getter.setParameter("owner", owner);
          getter.setParameter("point", time);
          try {
            return getter.getSingleResult();
          } catch (NoResultException e) {
            return null;
          }
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }
}
