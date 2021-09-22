/*
 * Copyright 2020 FIX Protocol Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package io.fixprotocol.md.event;


/**
 * Mutable attributes of a table column
 *
 * Attributes are mutable because a function that composes a table needs to adjust spacing, etc. for
 * best presentation.
 *
 * @author Don Mendelson
 *
 */
public interface TableColumn {

  enum Alignment {
    CENTER, LEFT, RIGHT
  }

  /**
   *
   * @return column alignment
   */
  Alignment getAlignment();

  /**
   * Displayed table heading
   *
   * @return displayable string
   */
  String getHeading();

  /**
   * Data key for values displayed in this column
   *
   * @return data key
   */
  String getKey();

  /**
   * Width of the column
   *
   * @return the column width
   */
  int getWidth();

  /**
   * Set the heading to display, may be different from its key
   *
   * @param display text to display
   */
  void setHeading(String display);

  /**
   * Update the inferred datatype of this column
   *
   * @param datatype a datatype to control formatting of data in the column
   */
  void updateDatatype(Class<?> datatype);


  /**
   * Update width allowed for column
   *
   * @param newWidth minimum space requested for this column
   * @return actual space allocated so far, may be larger than {@code newWidth}
   */
  int updateWidth(int newWidth);

}
