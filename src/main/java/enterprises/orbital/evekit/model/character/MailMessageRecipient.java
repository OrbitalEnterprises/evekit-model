package enterprises.orbital.evekit.model.character;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class MailMessageRecipient {
  private String recipientType;
  private int recipientID;

  public MailMessageRecipient() {}

  public MailMessageRecipient(String recipientType, int recipientID) {
    this.recipientType = recipientType;
    this.recipientID = recipientID;
  }

  public String getRecipientType() {
    return recipientType;
  }

  public int getRecipientID() {
    return recipientID;
  }

  public void setRecipientType(String recipientType) {
    this.recipientType = recipientType;
  }

  public void setRecipientID(int recipientID) {
    this.recipientID = recipientID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MailMessageRecipient that = (MailMessageRecipient) o;
    return recipientID == that.recipientID &&
        Objects.equals(recipientType, that.recipientType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipientType, recipientID);
  }
}
