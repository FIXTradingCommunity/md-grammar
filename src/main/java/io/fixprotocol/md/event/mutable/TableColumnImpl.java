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
package io.fixprotocol.md.event.mutable;

import io.fixprotocol.md.event.TableColumn;
import io.fixprotocol.md.util.StringUtil;

class TableColumnImpl implements TableColumn {
  private Alignment alignment = null;
  private Class<?> datatype = null;
  private String display = null;
  private final String key;
  private int length;
  
  public TableColumnImpl(String key) {
    this(key, 0, null);
  }

  public TableColumnImpl(String key, int length) {
    this(key, length, null);
  }

  public TableColumnImpl(String key, int length, Alignment alignment) {
    this.key = key;
    this.length = length;
    this.alignment = alignment;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TableColumnImpl other = (TableColumnImpl) obj;
    if (key == null) {
      return other.key == null;
    } else return key.equals(other.key);
  }

  /**
   * If Alignment is explicitly set, return that value. Otherwise, infer it based
   * on datatype.
   */
  @Override
  public Alignment getAlignment() {
    if (alignment != null) {
      return alignment;
    } else if (datatype != null) {
      if (Number.class.isAssignableFrom(datatype)) {
        return Alignment.RIGHT;
      } else if (Boolean.class.isAssignableFrom(datatype)
          || Character.class.isAssignableFrom(datatype)) {
        return Alignment.CENTER;
      }
    }
    return Alignment.LEFT;
  }

  public Class<?> getDatatype() {
    return datatype;
  }

  @Override
  public String getHeading() {
    if (display == null) {
      display = StringUtil.convertToTitleCase(key);
    }
    return display;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public int getWidth() {
    return length;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  /**
   * Set the datatype of a column for display characteristics
   * 
   * @param datatype class of the data in this column
   */
  public void setDatatype(Class<?> datatype) {
    this.datatype = datatype;
  }

  @Override
  public void setHeading(String display) {
    this.display = display;
    updateWidth(display.length());
  } 

  @Override
  public String toString() {
    return "TableColumnImpl [key=" + key + ", display=" + display + ", length=" + length
        + ", alignment=" + alignment + "]";
  }

  @Override
  public int updateWidth(int newLength) {
    this.length = Math.max(length, newLength);
    return length;
  }

  /**
   * Compares datatype to existing datatype. If datatypes are mixed, then default to String.
   */
  @Override
  public void updateDatatype(Class<?> datatype) {
    if (this.datatype == String.class) {
      // don't override default
    } else if(datatype != null && this.datatype != null && datatype != this.datatype) {
      // set default if mixed
      this.datatype = String.class;
    } else {
      this.datatype = datatype;
    }
  }

}
