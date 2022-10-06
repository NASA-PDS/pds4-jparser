// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.label;

/**
 * Defines the various display directions that can be used by axes within arrays and images.
 */
public enum DisplayDirection {

  /** Display is right-to-left. */
  LEFT("Left"),

  RIGHT_TO_LEFT("Right to Left"),

  /** Display is left-to-right. */
  RIGHT("Right"),

  LEFT_TO_RIGHT("Left to Right"),

  /** Display is bottom-to-top. */
  UP("Up"),

  BOTTOM_TO_TOP("Bottom to Top"),

  /** Display is top-to-bottom. */
  DOWN("Down"),

  TOP_TO_BOTTOM("Top to Bottom");

  private String elementValue;

  private DisplayDirection(String elementValue) {
    this.elementValue = elementValue;
  }

  /**
   * Looks up a display direction based on the value within the metadata.
   *
   * @param value the metadata value
   * @return the display direciton corresponding to the metadata value, or null if not found
   */
  public static DisplayDirection getDirectionFromValue(String value) {
    for (DisplayDirection dir : DisplayDirection.values()) {
      if (dir.elementValue.equalsIgnoreCase(value)) {
        return dir;
      }
    }

    return null;
  }

}
