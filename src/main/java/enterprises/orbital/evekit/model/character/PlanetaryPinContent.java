package enterprises.orbital.evekit.model.character;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PlanetaryPinContent {
  private int typeID;
  private long amount;

  public PlanetaryPinContent() {}

  public PlanetaryPinContent(int typeID, long amount) {
    this.typeID = typeID;
    this.amount = amount;
  }

  public int getTypeID() {
    return typeID;
  }

  public void setTypeID(int typeID) {
    this.typeID = typeID;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlanetaryPinContent that = (PlanetaryPinContent) o;
    return typeID == that.typeID &&
        amount == that.amount;
  }

  @Override
  public int hashCode() {

    return Objects.hash(typeID, amount);
  }

  @Override
  public String toString() {
    return "PlanetaryPinContent{" +
        "typeID=" + typeID +
        ", amount=" + amount +
        '}';
  }
}
