package enterprises.orbital.evekit.model.eve;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeParameters;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_eve_error")
@NamedQueries({
    @NamedQuery(
        name = "ErrorType.get",
        query = "SELECT c FROM ErrorType c WHERE c.errorCode = :ecode AND c.lifeStart <= :point AND c.lifeEnd > :point"),
})
public class ErrorType extends RefCachedData {
  private static final Logger log = Logger.getLogger(ErrorType.class.getName());
  private int                 errorCode;
  @Lob
  @Column(
      length = 102400)
  private String              errorText;

  @SuppressWarnings("unused")
  private ErrorType() {}

  public ErrorType(int errorCode, String errorText) {
    super();
    this.errorCode = errorCode;
    this.errorText = errorText;
  }

  /**
   * Update transient date values for readability.
   */
  @Override
  public void prepareDates() {
    fixDates();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof ErrorType)) return false;
    ErrorType other = (ErrorType) sup;
    return errorCode == other.errorCode && nullSafeObjectCompare(errorText, other.errorText);
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorText() {
    return errorText;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + errorCode;
    result = prime * result + ((errorText == null) ? 0 : errorText.hashCode());
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ErrorType other = (ErrorType) obj;
    if (errorCode != other.errorCode) return false;
    if (errorText == null) {
      if (other.errorText != null) return false;
    } else if (!errorText.equals(other.errorText)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ErrorType [errorCode=" + errorCode + ", errorText=" + errorText + "]";
  }

  public static ErrorType get(
                              final long time,
                              final int errorCode) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<ErrorType>() {
        @Override
        public ErrorType run() throws Exception {
          TypedQuery<ErrorType> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("ErrorType.get", ErrorType.class);
          getter.setParameter("point", time);
          getter.setParameter("ecode", errorCode);
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

  public static List<ErrorType> accessQuery(
                                            final long contid,
                                            final int maxresults,
                                            final boolean reverse,
                                            final AttributeSelector at,
                                            final AttributeSelector errorCode,
                                            final AttributeSelector errorText) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<ErrorType>>() {
        @Override
        public List<ErrorType> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ErrorType c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeParameters p = new AttributeParameters("att");
          AttributeSelector.addIntSelector(qs, "c", "errorCode", errorCode);
          AttributeSelector.addStringSelector(qs, "c", "errorText", errorText, p);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<ErrorType> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), ErrorType.class);
          p.fillParams(query);
          query.setMaxResults(maxresults);
          return query.getResultList();
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return Collections.emptyList();
  }

}
