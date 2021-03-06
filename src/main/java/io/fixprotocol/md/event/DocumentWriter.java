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

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.fixprotocol.md.util.AssociativeSet;

public class DocumentWriter implements AutoCloseable {
  enum Alignment {
    CENTER, LEFT, RIGHT
  }

  private static final char[] CELL_PREFIX = "| ".toCharArray();
  private static final char[] FENCE = "```".toCharArray();
  private static final char[] HEADING_LEVELS = "######".toCharArray();
  private static final char[] HYPHENS = new char[128];
  private static final char[] SPACES = new char[128];

  private final Logger logger = LogManager.getLogger(getClass());
  private final Writer writer;
  private final DatatypeInference datatypes;

  public DocumentWriter(Writer writer) {
    this.writer = writer;
    Arrays.fill(SPACES, ' ');
    Arrays.fill(HYPHENS, '-');
    datatypes = new DatatypeInference();
  }

  public DocumentWriter(Writer writer, NumberFormat numberFormat, String trueValue,
      String falseValue) {
    this.writer = writer;
    Arrays.fill(SPACES, ' ');
    Arrays.fill(HYPHENS, '-');
    datatypes = new DatatypeInference(numberFormat, trueValue, falseValue);
  }

  @Override
  public void close() throws Exception {
    writer.close();
  }

  public void write(Context context) throws IOException {
    writer.write(HEADING_LEVELS, 0, context.getLevel());
    writer.write(" ");
    writer.write(String.join(" ", context.getKeys()));
    writer.write("\n\n");
  }

  public void write(Detail t) throws IOException {
    // TODO Auto-generated method stub

  }

  public void write(DetailTable detailTable) throws IOException {
    final List<? extends TableColumn> tableColumns = detailTable.getTableColumns();
    write(detailTable, tableColumns);
  }

  public void write(DetailTable detailTable, AssociativeSet headings) throws IOException {
    final List<? extends TableColumn> tableColumns = detailTable.getTableColumns();
    for (final TableColumn column : tableColumns) {
      final String key = column.getKey();
      if (key != null) {
        final String display = headings.get(key);
        if (display != null) {
          column.setHeading(display);
        }
      }
    }
    write(detailTable, tableColumns);
  }

  public void write(DetailTable detailTable, final List<? extends TableColumn> tableColumns)
      throws IOException {
    final Map<String, TableColumn> columnsByKey = new HashMap<>();
    tableColumns.forEach(tc -> columnsByKey.put(tc.getKey(), tc));
    for (final DetailProperties row : detailTable.rows()) {
      for (final Entry<String, String> p : row.getProperties()) {
        final TableColumn tc = columnsByKey.get(p.getKey());
        if (tc != null) {
          // Set spacing to the longest value in each column
          tc.updateWidth(p.getValue().length());
          // Infer common datatype
          final Class<?> datatype = datatypes.inferDatatype(p.getValue());
          tc.updateDatatype(datatype);
        }
      }
    }
    writeTableHeadings(tableColumns);
    writeTableDelimiters(tableColumns);
    for (final DetailProperties row : detailTable.rows()) {
      writeTableRow(tableColumns, row);
    }
    writer.write("\n");
  }

  public void write(Documentation documentation) throws IOException {
    final String text = documentation.getDocumentation();
    final String format = documentation.getFormat();
    if (text != null) {
      if (format.equals(Documentation.MARKDOWN)) {
        writer.write(text);
      } else {
        writer.write(FENCE);
        writer.write(format);
        writer.write("\n");
        writer.write(text);
        writer.write(FENCE);
      }
      writer.write("\n\n");
    }
  }

  public void write(Iterable<? extends Context> contextSupplier) throws IOException {
    final Consumer<? super Context> action = (Consumer<Context>) t -> {
      try {
        if (t instanceof Documentation) {
          write((Documentation) t);
        } else if (t instanceof DetailTable) {
          write((DetailTable) t);
        } else if (t instanceof Detail) {
          write((Detail) t);
        } else {
          write(t);
        }

      } catch (final IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    };
    contextSupplier.forEach(action);
  }

  private void writeCell(String value, int length) throws IOException {
    writer.write(CELL_PREFIX);
    writer.write(value);
    final int spaces = Math.min(length - value.length() + 1, SPACES.length - 1);
    // assert spaces >= 0;
    if (spaces > 0) {
      writer.write(SPACES, 0, spaces);
    }
  }

  private void writeTableDelimiters(List<? extends TableColumn> tableColumns) throws IOException {
    for (final TableColumn column : tableColumns) {
      final TableColumn.Alignment alignment = column.getAlignment();
      writer.write("|");
      if (alignment == TableColumn.Alignment.CENTER) {
        writer.write(':');
      }
      int hyphens = Math.min(column.getWidth() + 2, HYPHENS.length - 1);
      if (alignment == TableColumn.Alignment.CENTER) {
        hyphens -= 2;
      }
      if (alignment == TableColumn.Alignment.RIGHT) {
        hyphens--;
      }
      if (hyphens > 0) {
        writer.write(HYPHENS, 0, hyphens);
      }
      if (alignment != TableColumn.Alignment.LEFT) {
        writer.write(':');
      }
    }
    writer.write("|\n");
  }

  private void writeTableHeadings(List<? extends TableColumn> tableColumns) throws IOException {
    for (final TableColumn column : tableColumns) {
      writer.write(CELL_PREFIX);
      writer.write(column.getHeading());
      final int spaces =
          Math.min(column.getWidth() - column.getHeading().length() + 1, SPACES.length - 1);
      // assert spaces >= 0;
      if (spaces > 0) {
        writer.write(SPACES, 0, spaces);
      }
    }
    writer.write("|\n");
  }

  private void writeTableRow(List<? extends TableColumn> tableColumns, DetailProperties row)
      throws IOException {
    for (final TableColumn column : tableColumns) {
      final String key = column.getKey();
      String value = row.getProperty(key);
      if (value == null) {
        value = "";
      }
      writeCell(value, column.getWidth());

    }
    writer.write("|\n");
  }
}
