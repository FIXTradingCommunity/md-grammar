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

import java.util.Objects;

public final class MarkdownUtil {

  public static final char BACKTICK_CHARACTER = '`';
  public static final char ESCAPE_CHARACTER = '\\';
  public static final char LINEFEED_CHARACTER = '\n';
  public static final String MARKDOWN_MEDIA_TYPE = "text/markdown";
  public static final String MARKDOWN_PARAGRAPH_BREAK = "\n\n";
  public static final char PIPE_CHARACTER = '|';

  /**
   * Translates a markdown literal surrounded by single backtick characters to plain text.
   *
   * Any characters prior to the first backtick or after the last one are ignored. If backticks are
   * not present, the whole string is passed through.
   *
   * @param literal a markdown literal
   * @return unescaped plain text
   * @throws NullPointerException if {@code literal} is null
   */
  public static String markdownLiteralToPlainText(String literal) {
    final int first = Objects.requireNonNull(literal).indexOf(BACKTICK_CHARACTER);
    final int last = literal.lastIndexOf(BACKTICK_CHARACTER);
    return literal.substring(first == -1 ? 0 : first + 1, last == -1 ? literal.length() : last);
  }

  /**
   * Translates plaintext to markdown
   *
   * A standard markdown paragraph break is used.
   *
   * @param text plaintext
   * @return a markdown string
   * @see #plainTextToMarkdown(String, String)
   */
  public static String plainTextToMarkdown(String text) {
    return plainTextToMarkdown(text, MARKDOWN_PARAGRAPH_BREAK);
  }

  /**
   * Translates plaintext to markdown
   *
   * <ul>
   * <li>Leading and trailing whitespace is removed</li>
   * <li>Escape these characters: pipe '|'</li>
   * <li>Convert linefeed to specified paragraph break token</li>
   * <li>Pass through XML/HTML entity references</li>
   * </ul>
   *
   * @param text plaintext
   * @param markdownParagraphBreak token to use for paragraph break in markdown
   * @return a markdown string
   */
  public static String plainTextToMarkdown(String text, String markdownParagraphBreak) {
    final StringBuilder sb = new StringBuilder(text.length());
    final String stripped = text.strip();
    boolean escaped = false;
    for (int i = 0; i < stripped.length(); i++) {
      final char c = stripped.charAt(i);
      switch (c) {
        case PIPE_CHARACTER:
          if (!escaped) {
            sb.append(ESCAPE_CHARACTER);
            escaped = false;
          }
          sb.append(c);
          break;
        case LINEFEED_CHARACTER:
          sb.append(markdownParagraphBreak);
          break;
        case ESCAPE_CHARACTER:
          if (escaped) {
            sb.append(ESCAPE_CHARACTER);
            sb.append(ESCAPE_CHARACTER);
            escaped = false;
          } else {
            escaped = true;
          }
          break;
        default:
          if (escaped) {
            sb.append(ESCAPE_CHARACTER);
            sb.append(ESCAPE_CHARACTER);
            escaped = false;
          }
          sb.append(c);
      }

    }
    return sb.toString();
  }

  /**
   * Text is surrounded by backtick characters
   *
   * Special characters within the literal are not escaped.
   *
   * @param text plain text
   * @return a markdown literal
   */
  public static String plainTextToMarkdownLiteral(String text) {
    return BACKTICK_CHARACTER + text + BACKTICK_CHARACTER;
  }

  /**
   * Trims leading and trailing whitespace or pipe characters, leaving just the text within a
   * markdown table cell.
   *
   * @param str string to strip
   * @return a string without leading or trailing whitespace, or {@code null} if the parameter is
   *         null
   */
  public static String stripCell(String str) {
    if (str == null) {
      return null;
    }
    final int strLen = str.length();
    int end = strLen - 1;
    int begin = 0;

    while ((begin < strLen)
        && (str.charAt(begin) == ' ' || str.charAt(begin) == '\t' || str.charAt(begin) == '|')) {
      begin++;
    }
    while ((begin < end) && (str.charAt(end) == ' ' || str.charAt(end) == '\t')) {
      end--;
    }
    return ((begin > 0) || (end < strLen)) ? str.substring(begin, end + 1) : str;
  }
}
