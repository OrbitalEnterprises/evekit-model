package enterprises.orbital.evekit.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.db.ConnectionFactory.RunInVoidTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;

@Entity
@Table(
    name = "evekit_ref_model_type_map")
public class RefModelTypeMap {
  private static final Logger log = Logger.getLogger(RefModelTypeMap.class.getName());
  @Id
  public long                 cid;
  public String               typeName;

  // No args constructor required for Hibernate
  @SuppressWarnings("unused")
  private RefModelTypeMap() {}

  public RefModelTypeMap(long cid, String typeName) {
    super();
    this.cid = cid;
    this.typeName = typeName;
  }

  public long getCid() {
    return cid;
  }

  public String getTypeName() {
    return typeName;
  }

  public static RefModelTypeMap update(
                                       final RefModelTypeMap data) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RefModelTypeMap>() {
        @Override
        public RefModelTypeMap run() throws Exception {
          return EveKitRefDataProvider.getFactory().getEntityManager().merge(data);
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static RefModelTypeMap retrieve(
                                         final long cid) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<RefModelTypeMap>() {
        @Override
        public RefModelTypeMap run() throws Exception {
          TypedQuery<RefModelTypeMap> getter = EveKitRefDataProvider.getFactory().getEntityManager()
              .createQuery("SELECT c FROM RefModelTypeMap c WHERE c.cid = :cid", RefModelTypeMap.class);
          getter.setParameter("cid", cid);
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

  public static String retrieveType(
                                    final long cid) {
    RefModelTypeMap result = retrieve(cid);
    return result != null ? result.getTypeName() : null;
  }

  public static void cleanup(
                             final long cid) {
    try {
      EveKitRefDataProvider.getFactory().runTransaction(new RunInVoidTransaction() {
        @Override
        public void run() throws Exception {
          RefModelTypeMap toRemove = retrieve(cid);
          if (toRemove != null) EveKitRefDataProvider.getFactory().getEntityManager().remove(toRemove);
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
  }

}
