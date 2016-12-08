package enterprises.orbital.evekit.model.server;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import enterprises.orbital.db.ConnectionFactory.RunInTransaction;
import enterprises.orbital.evekit.account.EveKitRefDataProvider;
import enterprises.orbital.evekit.model.AttributeSelector;
import enterprises.orbital.evekit.model.RefCachedData;

@Entity
@Table(
    name = "evekit_server_server_status")
@NamedQueries({
    @NamedQuery(
        name = "ServerStatus.get",
        query = "SELECT c FROM ServerStatus c where c.lifeStart <= :point and c.lifeEnd > :point"),
})
public class ServerStatus extends RefCachedData {
  private static final Logger log = Logger.getLogger(ServerStatus.class.getName());
  private int                 onlinePlayers;
  private boolean             serverOpen;

  @SuppressWarnings("unused")
  private ServerStatus() {}

  public ServerStatus(int onlinePlayers, boolean serverOpen) {
    super();
    this.onlinePlayers = onlinePlayers;
    this.serverOpen = serverOpen;
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
    if (!(sup instanceof ServerStatus)) return false;
    ServerStatus other = (ServerStatus) sup;
    return onlinePlayers == other.onlinePlayers && serverOpen == other.serverOpen;
  }

  public int getOnlinePlayers() {
    return onlinePlayers;
  }

  public boolean isServerOpen() {
    return serverOpen;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + onlinePlayers;
    result = prime * result + (serverOpen ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ServerStatus other = (ServerStatus) obj;
    if (onlinePlayers != other.onlinePlayers) return false;
    if (serverOpen != other.serverOpen) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ServerStatus [onlinePlayers=" + onlinePlayers + ", serverOpen=" + serverOpen + "]";
  }

  public static ServerStatus get(
                                 final long time) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<ServerStatus>() {
        @Override
        public ServerStatus run() throws Exception {
          TypedQuery<ServerStatus> getter = EveKitRefDataProvider.getFactory().getEntityManager().createNamedQuery("ServerStatus.get", ServerStatus.class);
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

  public static List<ServerStatus> accessQuery(
                                               final long contid,
                                               final int maxresults,
                                               final boolean reverse,
                                               final AttributeSelector at,
                                               final AttributeSelector onlinePlayers,
                                               final AttributeSelector serverOpen) {
    try {
      return EveKitRefDataProvider.getFactory().runTransaction(new RunInTransaction<List<ServerStatus>>() {
        @Override
        public List<ServerStatus> run() throws Exception {
          StringBuilder qs = new StringBuilder();
          qs.append("SELECT c FROM ServerStatus c WHERE 1=1");
          // Constrain lifeline
          AttributeSelector.addLifelineSelector(qs, "c", at);
          // Constrain attributes
          AttributeSelector.addIntSelector(qs, "c", "onlinePlayers", onlinePlayers);
          AttributeSelector.addBooleanSelector(qs, "c", "serverOpen", serverOpen);
          // Set CID constraint and ordering
          if (reverse) {
            qs.append(" and c.cid < ").append(contid);
            qs.append(" order by cid desc");
          } else {
            qs.append(" and c.cid > ").append(contid);
            qs.append(" order by cid asc");
          }
          // Return result
          TypedQuery<ServerStatus> query = EveKitRefDataProvider.getFactory().getEntityManager().createQuery(qs.toString(), ServerStatus.class);
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
