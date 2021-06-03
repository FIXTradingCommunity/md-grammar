package io.fixprotocol.md.event;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MarkdownUtilTest {

  @Test
  void testPlainTextToMarkdown() {
    assertEquals(".+(\\\\s.+)*", MarkdownUtil.plainTextToMarkdown(".+(\\s.+)*"));
    assertEquals("[A-Za-z0-9](\\\\s[A-Za-z0-9])*", MarkdownUtil.plainTextToMarkdown("[A-Za-z0-9](\\s[A-Za-z0-9])*"));
    assertEquals("\\\\d{4}(0\\|1)\\\\d([0-3wW]\\\\d)?", MarkdownUtil.plainTextToMarkdown("\\d{4}(0|1)\\d([0-3wW]\\d)?"));
    assertEquals("character values (e.g.\\|18=2 A F\\| ).", MarkdownUtil.plainTextToMarkdown("character values (e.g.|18=2 A F| )."));
    assertEquals("line1\n\nline2", MarkdownUtil.plainTextToMarkdown("line1\nline2"));
  }
  
  @Test
  void plainTextToMarkdownLiteral() {
    assertEquals("`foo \bar`", MarkdownUtil.plainTextToMarkdownLiteral("foo \bar"));
  }
  
  @Test
  void markdownLiteralToPlainText() {
    assertEquals("foo \bar", MarkdownUtil.markdownLiteralToPlainText("`foo \bar`"));
  }

}
