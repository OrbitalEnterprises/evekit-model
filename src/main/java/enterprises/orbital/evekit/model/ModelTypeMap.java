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
import enterprises.orbital.evekit.account.EveKitUserAccountProvider;

@Entity
@Table(
    name = "evekit_model_type_map")
public class ModelTypeMap {
  private static final Logger log = Logger.getLogger(ModelTypeMap.class.getName());
  @Id
  public long                 cid;
  public String               typeName;

  // No args constructor required for Hibernate
  @SuppressWarnings("unused")
  private ModelTypeMap() {}

  public ModelTypeMap(long cid, String typeName) {
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

  public static ModelTypeMap update(
                                    final ModelTypeMap data) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ModelTypeMap>() {
        @Override
        public ModelTypeMap run() throws Exception {
          return EveKitUserAccountProvider.getFactory().getEntityManager().merge(data);
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
    return null;
  }

  public static ModelTypeMap retrieve(
                                      final long cid) {
    try {
      return EveKitUserAccountProvider.getFactory().runTransaction(new RunInTransaction<ModelTypeMap>() {
        @Override
        public ModelTypeMap run() throws Exception {
          TypedQuery<ModelTypeMap> getter = EveKitUserAccountProvider.getFactory().getEntityManager()
              .createQuery("SELECT c FROM ModelTypeMap c WHERE c.cid = :cid", ModelTypeMap.class);
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
    ModelTypeMap result = retrieve(cid);
    return result != null ? result.getTypeName() : null;
  }

  public static void cleanup(
                             final long cid) {
    try {
      EveKitUserAccountProvider.getFactory().runTransaction(new RunInVoidTransaction() {
        @Override
        public void run() throws Exception {
          ModelTypeMap toRemove = retrieve(cid);
          if (toRemove != null) EveKitUserAccountProvider.getFactory().getEntityManager().remove(toRemove);
        }
      });
    } catch (Exception e) {
      log.log(Level.SEVERE, "query error", e);
    }
  }

}
