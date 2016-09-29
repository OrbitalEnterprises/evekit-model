package enterprises.orbital.evekit.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.base.OrbitalProperties;
import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;

/**
 * Top level data object for reference data.
 * 
 * NOTE: Reference data is a special form of RefCachedData and always has a single life window. This is because the instance of RefData is solely used to track
 * synchronization expiry.
 */
@Entity
@Table(
    name = "evekit_ref_container")
@NamedQueries({
    @NamedQuery(
        name = "RefData.get",
        query = "SELECT c FROM RefData c"),
})
public class RefData extends RefCachedData {
  private static final Logger log                = Logger.getLogger(RefData.class.getName());

  // Request expiry data
  private long                serverStatusExpiry = -1;

  public long getServerStatusExpiry() {
    return serverStatusExpiry;
  }

  public void setServerStatusExpiry(
                                    long serverStatusExpiry) {
    this.serverStatusExpiry = serverStatusExpiry;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (serverStatusExpiry ^ (serverStatusExpiry >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    RefData other = (RefData) obj;
    if (serverStatusExpiry != other.serverStatusExpiry) return false;
    return true;
  }

  @Override
  public String toString() {
    return "RefData [serverStatusExpiry=" + serverStatusExpiry + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    throw new UnsupportedOperationException();
  }

  public static RefData getRefData() {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RefData>() {
        @Override
        public RefData run() throws Exception {
          TypedQuery<RefData> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("RefData.get", RefData.class);
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

  public static RefData getOrCreateRefData() {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RefData>() {
        @Override
        public RefData run() throws Exception {
          RefData existing = getRefData();
          if (existing == null) {
            existing = new RefData();
            existing.setup(OrbitalProperties.getCurrentTime());
            existing = EveKitRefDataProvider.getFactory().getEntityManager().merge(existing);
          }
          return existing;
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

}
