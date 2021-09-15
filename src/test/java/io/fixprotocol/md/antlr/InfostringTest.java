package io.fixprotocol.md.antlr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;
import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.fixprotocol.md.antlr.MarkdownParser.BlockContext;
import io.fixprotocol.md.antlr.MarkdownParser.DocumentContext;
import io.fixprotocol.md.antlr.MarkdownParser.EndContext;
import io.fixprotocol.md.antlr.MarkdownParser.FencedcodeblockContext;
import io.fixprotocol.md.antlr.MarkdownParser.ImportspecContext;
import io.fixprotocol.md.antlr.MarkdownParser.LocationContext;
import io.fixprotocol.md.antlr.MarkdownParser.PathContext;
import io.fixprotocol.md.antlr.MarkdownParser.StartContext;

class InfostringTest {

  private static PrintStream out;
  private GrammarValidator validator;

  @BeforeAll
  static void setUpOnce() throws FileNotFoundException {
    new File("target/test").mkdirs();
    out = new PrintStream(new FileOutputStream("target/test/InfostringTest.txt"));
  }

  @AfterAll
  static void cleanUpOnce() throws FileNotFoundException {
    out.close();
  }

  @BeforeEach
  void setUp() throws Exception {
    validator = new GrammarValidator(out);
  }

  @Test
  void typeOnly() throws IOException {
    final String string = "```xml\n \n```\n";
    MarkdownLexer lexer = new MarkdownLexer(CharStreams.fromReader(new StringReader(string)));
    lexer.removeErrorListeners();
    lexer.addErrorListener(validator);
    MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(validator);
    final DocumentContext document = parser.document();
    List<BlockContext> blocks = document.block();
    for (BlockContext block : blocks) {
      FencedcodeblockContext fencedCodeBlock = block.fencedcodeblock();
      if (fencedCodeBlock != null) {
        io.fixprotocol.md.antlr.MarkdownParser.InfostringContext ctx = fencedCodeBlock.infostring();
        assertEquals("xml", ctx.WORD().getText());
        assertTrue(validator.isValid);
        return;
      }
    }
    fail("Fenced code block not found");
  }

  @Test
  void importFile() throws IOException {
    final String string = "```xml import myfile.xml\n```\n";
    MarkdownLexer lexer = new MarkdownLexer(CharStreams.fromReader(new StringReader(string)));
    lexer.removeErrorListeners();
    lexer.addErrorListener(validator);
    MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(validator);
    final DocumentContext document = parser.document();
    List<BlockContext> blocks = document.block();
    for (BlockContext block : blocks) {
      FencedcodeblockContext fencedCodeBlock = block.fencedcodeblock();
      if (fencedCodeBlock != null) {
        io.fixprotocol.md.antlr.MarkdownParser.InfostringContext ctx = fencedCodeBlock.infostring();
        assertEquals("xml", ctx.WORD().getText());
        ImportspecContext importspec = fencedCodeBlock.importspec();
        final PathContext pathCtx = importspec.path();
        final String path = pathCtx.getText();
        assertEquals("myfile.xml", path);
        assertTrue(validator.isValid);
        return;
      }
    }
    fail("Fenced code block not found");
  }

  @Test
  void lineRange() throws IOException {
    final String string = "```xml import myfile.xml 88 - 199\n```\n";
    MarkdownLexer lexer = new MarkdownLexer(CharStreams.fromReader(new StringReader(string)));
    lexer.removeErrorListeners();
    lexer.addErrorListener(validator);
    MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(validator);
    final DocumentContext document = parser.document();
    List<BlockContext> blocks = document.block();
    for (BlockContext block : blocks) {
      FencedcodeblockContext fencedCodeBlock = block.fencedcodeblock();
      if (fencedCodeBlock != null) {
        io.fixprotocol.md.antlr.MarkdownParser.InfostringContext ctx = fencedCodeBlock.infostring();
        assertEquals("xml", ctx.WORD().getText());
        ImportspecContext importspec = fencedCodeBlock.importspec();
        final PathContext pathCtx = importspec.path();
        final String path = pathCtx.getText();
        assertEquals("myfile.xml", path);
        StartContext startCtx = importspec.start();
        LocationContext startLocation = startCtx.location();
        assertEquals(88, Integer.parseInt(startLocation.LINENUMBER().getText()));
        EndContext endCtx = importspec.end();
        LocationContext endLocation = endCtx.location();
        assertEquals(199, Integer.parseInt(endLocation.LINENUMBER().getText()));
        assertTrue(validator.isValid);
        return;
      }
    }
    fail("Fenced code block not found");
  }

  @Test
  void textRange() throws IOException {
    final String string = "```xml import myfile.xml from \"foo\" to \"bar\"\n```\n";
    MarkdownLexer lexer = new MarkdownLexer(CharStreams.fromReader(new StringReader(string)));
    lexer.removeErrorListeners();
    lexer.addErrorListener(validator);
    MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(validator);
    final DocumentContext document = parser.document();
    List<BlockContext> blocks = document.block();
    for (BlockContext block : blocks) {
      FencedcodeblockContext fencedCodeBlock = block.fencedcodeblock();
      if (fencedCodeBlock != null) {
        io.fixprotocol.md.antlr.MarkdownParser.InfostringContext ctx = fencedCodeBlock.infostring();
        assertEquals("xml", ctx.WORD().getText());
        ImportspecContext importspec = fencedCodeBlock.importspec();
        final PathContext pathCtx = importspec.path();
        final String path = pathCtx.getText();
        assertEquals("myfile.xml", path);
        StartContext startCtx = importspec.start();
        LocationContext startLocation = startCtx.location();
        assertEquals("\"foo\"", startLocation.STRING().getText());
        EndContext endCtx = importspec.end();
        LocationContext endLocation = endCtx.location();
        assertEquals("\"bar\"", endLocation.STRING().getText());
        assertTrue(validator.isValid);
        return;
      }
    }
    fail("Fenced code block not found");
  }


  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/fileimport.md"})
  void testRig(String fileName) throws Exception {
    String[] args =
        new String[] {"io.fixprotocol.md.antlr.Markdown", "document", "-tree", "-tokens", fileName};
    TestRig testRig = new TestRig(args);
    testRig.process();
  }

}
