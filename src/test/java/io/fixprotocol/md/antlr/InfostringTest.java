package io.fixprotocol.md.antlr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.fixprotocol.md.antlr.InfostringParser.EndContext;
import io.fixprotocol.md.antlr.InfostringParser.ImportspecContext;
import io.fixprotocol.md.antlr.InfostringParser.LocationContext;
import io.fixprotocol.md.antlr.InfostringParser.PathContext;
import io.fixprotocol.md.antlr.InfostringParser.StartContext;

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
    validator = new GrammarValidator(out );
  }

  @Test
  void typeOnly() throws IOException {
    final String string = "xml";
    InfostringLexer lexer =
        new InfostringLexer(CharStreams.fromReader(new StringReader(string)));
    InfostringParser parser = new InfostringParser(new CommonTokenStream(lexer));   
    parser.addErrorListener(validator);
    io.fixprotocol.md.antlr.InfostringParser.InfostringContext ctx = parser.infostring();
    assertEquals(string, ctx.type().getText());
    assertTrue(validator.isValid);
  }
  
  @Test
  void importFile() throws IOException {
    final String string = "xml import myfile.xml";
    InfostringLexer lexer =
        new InfostringLexer(CharStreams.fromReader(new StringReader(string)));
    InfostringParser parser = new InfostringParser(new CommonTokenStream(lexer));   
    parser.addErrorListener(validator);
    io.fixprotocol.md.antlr.InfostringParser.InfostringContext ctx = parser.infostring();
    assertEquals("xml", ctx.type().getText());
    ImportspecContext importspec = ctx.importspec();
    final PathContext pathCtx = importspec.path();
    final String path = pathCtx.getText();
    assertEquals("myfile.xml", path);
    assertTrue(validator.isValid);
  }
  
  @Test
  void lineRange() throws IOException {
    final String string = "xml import myfile.xml 88 - 199";
    InfostringLexer lexer =
        new InfostringLexer(CharStreams.fromReader(new StringReader(string)));
    InfostringParser parser = new InfostringParser(new CommonTokenStream(lexer));   
    parser.addErrorListener(validator);
    io.fixprotocol.md.antlr.InfostringParser.InfostringContext ctx = parser.infostring();
    assertEquals("xml", ctx.type().getText());
    ImportspecContext importspec = ctx.importspec();
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
  }
  
  @Test
  void textRange() throws IOException {
    final String string = "xml import myfile.xml from \"foo\" to \"bar\"";
    InfostringLexer lexer =
        new InfostringLexer(CharStreams.fromReader(new StringReader(string)));
    InfostringParser parser = new InfostringParser(new CommonTokenStream(lexer));   
    parser.addErrorListener(validator);
    io.fixprotocol.md.antlr.InfostringParser.InfostringContext ctx = parser.infostring();
    assertEquals("xml", ctx.type().getText());
    ImportspecContext importspec = ctx.importspec();
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
  }
  
  @Disabled
  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/fileimport.txt"})
  void testRig(String fileName) throws Exception {
    String[] args = new String[] {"io.fixprotocol.md.antlr.Infostring", "document", "-gui", "-tree",
        "-tokens", fileName};
    TestRig testRig = new TestRig(args);
    testRig.process();
  }

}
