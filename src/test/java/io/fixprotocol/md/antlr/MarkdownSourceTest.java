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
package io.fixprotocol.md.antlr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

// black box testing for now
class MarkdownSourceTest {

  @Test
  void testTrimCell() {
    String text = "| Cross (orders where counterparty is an exchange, valid for all messages *except* IOIs) ";
    String trimmed = MarkdownEventSource.trimCell(text);
    assertEquals('C', trimmed.charAt(0));
    assertEquals(')', trimmed.charAt(trimmed.length()-1));
  }
  
  @Test
  void tokenizeHeading() {
    String[] tokens = MarkdownEventSource.tokenizeHeading("### Actor \"Trading Adapter\"", 3);
    assertEquals(2, tokens.length);
    assertEquals("Actor", tokens[0]);
    assertEquals("Trading Adapter", tokens[1]);
  }

}
