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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import io.fixprotocol.md.event.Context;
import io.fixprotocol.md.event.DetailProperties;
import io.fixprotocol.md.event.MarkdownUtil;
import io.fixprotocol.md.event.MutableDetailProperties;
import io.fixprotocol.md.event.MutableDetailTable;
import io.fixprotocol.md.event.MutableDocumentContext;
import io.fixprotocol.md.event.TableColumn;
import io.fixprotocol.md.util.ListUtil;

public class DetailTableImpl implements MutableDetailTable {

  private class TableRowImpl implements MutableDetailProperties, TableRow, MutableDocumentContext {
    private int charPositionInLine;
    private int endOffset = UNKNOWN_POSITION;
    private int line;
    private final Map<String, String> properties = new LinkedHashMap<>();
    private int startOffset = UNKNOWN_POSITION;

    @Override
    public void addIntProperty(String key, int value) {
      addProperty(key, Integer.toString(value));
    }

    @Override
    public void addProperty(String key, String value) {
      if (value != null) {
        final String trimmed = MarkdownUtil.stripCell(value);
        if (!trimmed.isEmpty()) {
          properties.put(Objects.requireNonNull(key, "Missing property key").toLowerCase(),
              trimmed);
        }
      }
    }

    @Override
    public int getCharPositionInLine() {
      // if row position unknown, get position of enclosing table
      return charPositionInLine != UNKNOWN_POSITION ? charPositionInLine
          : DetailTableImpl.this.getCharPositionInLine();
    }

    @Override
    public int getEndOffset() {
      return endOffset;
    }

    @Override
    public Integer getIntProperty(String key) {
      final String property = getProperty(key);
      if (property != null) {
        try {
          return Integer.valueOf(property);
        } catch (final NumberFormatException e) {
          return null;
        }
      } else
        return null;
    }

    @Override
    public int getLine() {
      // if row position unknown, get position of enclosing table
      return line != UNKNOWN_POSITION ? line : DetailTableImpl.this.getLine();
    }

    @Override
    public Collection<Entry<String, String>> getProperties() {
      return Collections.unmodifiableSet(properties.entrySet());
    }

    @Override
    public String getProperty(String key) {
      return properties.get(Objects.requireNonNull(key, "Missing property key").toLowerCase());
    }

    @Override
    public int getStartOffset() {
      return startOffset;
    }



    @Override
    public void setCharPositionInLine(int charPositionInLine) {
      this.charPositionInLine = charPositionInLine;
    }

    @Override
    public void setEndOffset(int endOffset) {
      this.endOffset = endOffset;
    }

    @Override
    public void setLine(int line) {
      this.line = line;
    }

    @Override
    public void setStartOffset(int startOffset) {
      this.startOffset = startOffset;
    }

    @Override
    public String toString() {
      return "TableRowImpl [properties=" + properties + "]";
    }
  }

  private int charPositionInLine;
  private int endOffset = UNKNOWN_POSITION;
  private int line;
  private Context parent;
  private final List<TableRow> propertiesList = new ArrayList<>();
  private int startOffset = UNKNOWN_POSITION;

  @Override
  public DetailProperties addProperties(DetailProperties detailProperties) {
    final TableRow clone = clone(detailProperties);
    propertiesList.add(clone);
    return detailProperties;
  }

  @Override
  public int getCharPositionInLine() {
    return charPositionInLine;
  }

  @Override
  public int getEndOffset() {
    return endOffset;
  }

  @Override
  public int getLine() {
    return line;
  }


  @Override
  public Context getParent() {
    return parent;
  }

  @Override
  public int getStartOffset() {
    return startOffset;
  }

  @Override
  public List<? extends TableColumn> getTableColumns() {
    List<TableColumn> columns = new ArrayList<>();

    for (final TableRow r : rows()) {
      final List<TableColumn> rowColumns = new ArrayList<>();
      r.getProperties().forEach(p -> {
        final String key = p.getKey();
        final TableColumnImpl column =
            new TableColumnImpl(key, Math.max(key.length(), p.getValue().length()));
        rowColumns.add(column);
      });
      columns = ListUtil.merge(columns, rowColumns);
    }

    return columns;
  }

  @Override
  public TableRowImpl newRow() {
    final TableRowImpl row = new TableRowImpl();
    propertiesList.add(row);
    return row;
  }

  @Override
  public Iterable<TableRow> rows() {
    return propertiesList;

  }

  @Override
  public void setCharPositionInLine(int charPositionInLine) {
    this.charPositionInLine = charPositionInLine;
  }

  @Override
  public void setEndOffset(int endOffset) {
    this.endOffset = endOffset;
  }

  @Override
  public void setLine(int line) {
    this.line = line;
  }

  @Override
  public void setParent(Context parent) {
    this.parent = parent;
  }

  @Override
  public void setStartOffset(int startOffset) {
    this.startOffset = startOffset;
  }

  private TableRow clone(DetailProperties detailProperties) {
    if (detailProperties instanceof TableRow) {
      return (TableRow) detailProperties;
    } else {
      final TableRowImpl row = new TableRowImpl();
      detailProperties.getProperties().forEach(e -> row.properties.put(e.getKey(), e.getValue()));
      return row;
    }
  }

}
