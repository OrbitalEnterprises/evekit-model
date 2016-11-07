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
  private static final Logger log                       = Logger.getLogger(RefData.class.getName());

  // Request expiry data
  private long                serverStatusExpiry        = -1;
  private long                callListExpiry            = -1;
  private long                allianceListExpiry        = -1;
  private long                conquerableStationsExpiry = -1;
  private long                errorListExpiry           = -1;
  private long                facWarStatsExpiry         = -1;
  private long                facWarTopStatsExpiry      = -1;
  private long                refTypeExpiry             = -1;
  private long                skillTreeExpiry           = -1;
  private long                facWarSystemsExpiry       = -1;
  private long                mapJumpExpiry             = -1;
  private long                mapKillExpiry             = -1;
  private long                sovereigntyExpiry         = -1;

  public long getServerStatusExpiry() {
    return serverStatusExpiry;
  }

  public void setServerStatusExpiry(
                                    long serverStatusExpiry) {
    this.serverStatusExpiry = serverStatusExpiry;
  }

  public long getCallListExpiry() {
    return callListExpiry;
  }

  public void setCallListExpiry(
                                long callListExpiry) {
    this.callListExpiry = callListExpiry;
  }

  public long getAllianceListExpiry() {
    return allianceListExpiry;
  }

  public void setAllianceListExpiry(
                                    long allianceListExpiry) {
    this.allianceListExpiry = allianceListExpiry;
  }

  public long getConquerableStationsExpiry() {
    return conquerableStationsExpiry;
  }

  public void setConquerableStationsExpiry(
                                           long conquerableStationsExpiry) {
    this.conquerableStationsExpiry = conquerableStationsExpiry;
  }

  public long getErrorListExpiry() {
    return errorListExpiry;
  }

  public void setErrorListExpiry(
                                 long errorListExpiry) {
    this.errorListExpiry = errorListExpiry;
  }

  public long getFacWarStatsExpiry() {
    return facWarStatsExpiry;
  }

  public void setFacWarStatsExpiry(
                                   long facWarStatsExpiry) {
    this.facWarStatsExpiry = facWarStatsExpiry;
  }

  public long getFacWarTopStatsExpiry() {
    return facWarTopStatsExpiry;
  }

  public void setFacWarTopStatsExpiry(
                                      long facWarTopStatsExpiry) {
    this.facWarTopStatsExpiry = facWarTopStatsExpiry;
  }

  public long getRefTypeExpiry() {
    return refTypeExpiry;
  }

  public void setRefTypeExpiry(
                               long refTypeExpiry) {
    this.refTypeExpiry = refTypeExpiry;
  }

  public long getSkillTreeExpiry() {
    return skillTreeExpiry;
  }

  public void setSkillTreeExpiry(
                                 long skillTreeExpiry) {
    this.skillTreeExpiry = skillTreeExpiry;
  }

  public long getFacWarSystemsExpiry() {
    return facWarSystemsExpiry;
  }

  public void setFacWarSystemsExpiry(
                                     long facWarSystemsExpiry) {
    this.facWarSystemsExpiry = facWarSystemsExpiry;
  }

  public long getMapJumpExpiry() {
    return mapJumpExpiry;
  }

  public void setMapJumpExpiry(
                               long mapJumpExpiry) {
    this.mapJumpExpiry = mapJumpExpiry;
  }

  public long getMapKillExpiry() {
    return mapKillExpiry;
  }

  public void setMapKillExpiry(
                               long mapKillExpiry) {
    this.mapKillExpiry = mapKillExpiry;
  }

  public long getSovereigntyExpiry() {
    return sovereigntyExpiry;
  }

  public void setSovereigntyExpiry(
                                   long sovereigntyExpiry) {
    this.sovereigntyExpiry = sovereigntyExpiry;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (allianceListExpiry ^ (allianceListExpiry >>> 32));
    result = prime * result + (int) (callListExpiry ^ (callListExpiry >>> 32));
    result = prime * result + (int) (conquerableStationsExpiry ^ (conquerableStationsExpiry >>> 32));
    result = prime * result + (int) (errorListExpiry ^ (errorListExpiry >>> 32));
    result = prime * result + (int) (facWarStatsExpiry ^ (facWarStatsExpiry >>> 32));
    result = prime * result + (int) (facWarSystemsExpiry ^ (facWarSystemsExpiry >>> 32));
    result = prime * result + (int) (facWarTopStatsExpiry ^ (facWarTopStatsExpiry >>> 32));
    result = prime * result + (int) (mapJumpExpiry ^ (mapJumpExpiry >>> 32));
    result = prime * result + (int) (mapKillExpiry ^ (mapKillExpiry >>> 32));
    result = prime * result + (int) (refTypeExpiry ^ (refTypeExpiry >>> 32));
    result = prime * result + (int) (serverStatusExpiry ^ (serverStatusExpiry >>> 32));
    result = prime * result + (int) (skillTreeExpiry ^ (skillTreeExpiry >>> 32));
    result = prime * result + (int) (sovereigntyExpiry ^ (sovereigntyExpiry >>> 32));
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    RefData other = (RefData) obj;
    if (allianceListExpiry != other.allianceListExpiry) return false;
    if (callListExpiry != other.callListExpiry) return false;
    if (conquerableStationsExpiry != other.conquerableStationsExpiry) return false;
    if (errorListExpiry != other.errorListExpiry) return false;
    if (facWarStatsExpiry != other.facWarStatsExpiry) return false;
    if (facWarSystemsExpiry != other.facWarSystemsExpiry) return false;
    if (facWarTopStatsExpiry != other.facWarTopStatsExpiry) return false;
    if (mapJumpExpiry != other.mapJumpExpiry) return false;
    if (mapKillExpiry != other.mapKillExpiry) return false;
    if (refTypeExpiry != other.refTypeExpiry) return false;
    if (serverStatusExpiry != other.serverStatusExpiry) return false;
    if (skillTreeExpiry != other.skillTreeExpiry) return false;
    if (sovereigntyExpiry != other.sovereigntyExpiry) return false;
    return true;
  }

  @Override
  public String toString() {
    return "RefData [serverStatusExpiry=" + serverStatusExpiry + ", callListExpiry=" + callListExpiry + ", allianceListExpiry=" + allianceListExpiry
        + ", conquerableStationsExpiry=" + conquerableStationsExpiry + ", errorListExpiry=" + errorListExpiry + ", facWarStatsExpiry=" + facWarStatsExpiry
        + ", facWarTopStatsExpiry=" + facWarTopStatsExpiry + ", refTypeExpiry=" + refTypeExpiry + ", skillTreeExpiry=" + skillTreeExpiry
        + ", facWarSystemsExpiry=" + facWarSystemsExpiry + ", mapJumpExpiry=" + mapJumpExpiry + ", mapKillExpiry=" + mapKillExpiry + ", sovereigntyExpiry="
        + sovereigntyExpiry + "]";
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
