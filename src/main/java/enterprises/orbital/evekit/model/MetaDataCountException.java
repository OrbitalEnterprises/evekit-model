package enterprises.orbital.evekit.model;

/**
 * Thrown if an attempt is made to add a tag to CacheData which already has too many tags.
 */
public class MetaDataCountException extends Exception {
  private static final long serialVersionUID = 3333744574971433242L;

  public MetaDataCountException() {
    super();
  }

  public MetaDataCountException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public MetaDataCountException(String arg0) {
    super(arg0);
  }

  public MetaDataCountException(Throwable arg0) {
    super(arg0);
  }

}
