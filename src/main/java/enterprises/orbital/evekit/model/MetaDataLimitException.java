package enterprises.orbital.evekit.model;

/**
 * Thrown if an attempt is made to add a tag where the key or value do not adhere to size requirements.
 */
public class MetaDataLimitException extends Exception {
  private static final long serialVersionUID = 3333744574971433242L;

  public MetaDataLimitException() {
    super();
  }

  public MetaDataLimitException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public MetaDataLimitException(String arg0) {
    super(arg0);
  }

  public MetaDataLimitException(Throwable arg0) {
    super(arg0);
  }

}
