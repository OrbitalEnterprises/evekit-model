package enterprises.orbital.evekit.model.eve;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import enterprises.orbital.evekit.model.RefCachedData;

@MappedSuperclass
public abstract class AbstractKillStat extends RefCachedData {
  @Enumerated(EnumType.STRING)
  protected StatAttribute attribute;
  protected int           kills;

  public AbstractKillStat(StatAttribute attribute, int kills) {
    super();
    this.attribute = attribute;
    this.kills = kills;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
                            RefCachedData sup) {
    if (!(sup instanceof AbstractKillStat)) return false;
    AbstractKillStat other = (AbstractKillStat) sup;
    return attribute == other.attribute && kills == other.kills;
  }

  public StatAttribute getAttribute() {
    return attribute;
  }

  public int getKills() {
    return kills;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
    result = prime * result + kills;
    return result;
  }

  @Override
  public boolean equals(
                        Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractKillStat other = (AbstractKillStat) obj;
    if (attribute != other.attribute) return false;
    if (kills != other.kills) return false;
    return true;
  }

  @Override
  public String toString() {
    return "AbstractKillStat [attribute=" + attribute + ", kills=" + kills + "]";
  }

}
