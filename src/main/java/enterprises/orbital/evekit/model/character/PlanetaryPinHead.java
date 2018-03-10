package enterprises.orbital.evekit.model.character;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PlanetaryPinHead {
  private int headID;
  private float latitude;
  private float longitude;

  public PlanetaryPinHead() {}

  public PlanetaryPinHead(int headID, float latitude, float longitude) {
    this.headID = headID;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public int getHeadID() {
    return headID;
  }

  public void setHeadID(int headID) {
    this.headID = headID;
  }

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlanetaryPinHead that = (PlanetaryPinHead) o;
    return headID == that.headID &&
        Float.compare(that.latitude, latitude) == 0 &&
        Float.compare(that.longitude, longitude) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(headID, latitude, longitude);
  }

  @Override
  public String toString() {
    return "PlanetaryPinHead{" +
        "headID=" + headID +
        ", latitude=" + latitude +
        ", longitude=" + longitude +
        '}';
  }
}
