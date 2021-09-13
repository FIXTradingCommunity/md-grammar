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

import java.util.List;

/**
 * A Context with a table of values
 *
 * @author Don Mendelson
 *
 */
public interface DetailTable extends GraphContext, DocumentContext {

  interface TableRow extends DetailProperties, DocumentContext {

  }

  /**
   *
   * @return an List of TableColumn that describes this table
   */
  List<? extends TableColumn> getTableColumns();

  /**
   * Supplies a Iterable of row values
   *
   * @return a Iterable of TableRow
   */
  Iterable<? extends TableRow> rows();
}
