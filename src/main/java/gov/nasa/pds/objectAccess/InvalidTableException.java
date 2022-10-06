// Copyright (c) 2020, California Institute of Technology.
// ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id: InvalidTableContentException.java $
//

package gov.nasa.pds.objectAccess;

/**
 * @author hyunlee
 * @version $Revision: 10821 $
 *
 */
public class InvalidTableException extends Exception {
  private static final long serialVersionUID = 1L;

  public InvalidTableException(String message) {
    super(message);
  }
}
