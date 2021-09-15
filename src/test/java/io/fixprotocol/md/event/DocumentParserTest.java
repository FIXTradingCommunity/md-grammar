package io.fixprotocol.md.event;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentParserTest {
  private DocumentParser parser;
  private static PrintStream out;
  
  @BeforeAll
  static void setUpOnce() throws FileNotFoundException {
    new File("target/test").mkdirs();
    out = new PrintStream(new FileOutputStream("target/test/DocumentParserTest.txt"));
  }

  @BeforeEach
  void setUp() throws Exception {
    parser = new DocumentParser();
  }

  @Test
  void fileImport() throws IOException {
   
    
    Consumer<GraphContext> contextConsumer = new Consumer<>() {

      @Override
      public void accept(GraphContext graphContext) {
        if (graphContext instanceof Documentation) {
          final Documentation documentation = (Documentation) graphContext;
          String text = documentation.getDocumentation();
          out.println(text);
        }
      }
    };
    
    FileInputStream inputStream = new FileInputStream("src/test/resources/documentwithimport.md");
    assertTrue(parser.parse(inputStream, contextConsumer, null, Path.of("src", "test", "resources")));
  }

}
