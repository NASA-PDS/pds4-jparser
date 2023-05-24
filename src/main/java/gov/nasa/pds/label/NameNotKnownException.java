package gov.nasa.pds.label;

public class NameNotKnownException extends IllegalArgumentException {
  private static final long serialVersionUID = -2657183691342441420L;
  public NameNotKnownException() {
    super();
  }
  public NameNotKnownException(String s) {
    super(s);
  }
  public NameNotKnownException(Throwable cause) {
    super(cause);
  }
  public NameNotKnownException(String message, Throwable cause) {
    super(message, cause);
  }
}
