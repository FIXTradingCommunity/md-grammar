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

import io.fixprotocol.md.event.MutableTableColumn;
import io.fixprotocol.md.util.StringUtil;

class TableColumnImpl implements MutableTableColumn {
  private final Alignment alignment;
  private String display = null;
  private final String key;
  private int length;

  public TableColumnImpl(String key) {
    this(key, 0, Alignment.LEFT);
  }

  public TableColumnImpl(String key, int length) {
    this(key, length, Alignment.LEFT);
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
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    return true;
  }

  @Override
  public Alignment getAlignment() {
    return alignment;
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

  @Override
  public void setHeading(String display) {
    this.display = display;
    updateWidth(display.length());
  }

  @Override
  public int updateWidth(int newLength) {
    this.length = Math.max(length, newLength);
    return length;
  }

}
