package io.fixprotocol.md.util;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileImportTest {

  private FileImport fileImport;
  private FileSpec fileSpec;

  @BeforeEach
  void setUp() throws Exception {
    fileImport = new FileImport();
    fileSpec = new FileSpec();
  }
  
  @Test
  void importWholeFile() throws IOException {
    fileSpec.setPath("md2orchestra-proto.md");
    Path baseDir = Path.of("src", "test", "resources");
    String text = fileImport.importTextFromFile(baseDir , fileSpec);
    assertTrue(text.startsWith("# Rules of Engagement"));
    assertTrue(text.contains("ThrottleInst"));
  }

  @Test
  void importByLines() throws IOException {
    fileSpec.setPath("md2orchestra-proto.md");
    fileSpec.setStartLinenumber(63);
    fileSpec.setEndLinenumber(72);   
    Path baseDir = Path.of("src", "test", "resources");
    String text = fileImport.importTextFromFile(baseDir , fileSpec);
    assertTrue(text.startsWith("### Codeset Sides"));
    assertTrue(text.contains("multileg instruments)"));
  }
  
  @Test
  void importByStartLineOnly() throws IOException {
    fileSpec.setPath("md2orchestra-proto.md");
    fileSpec.setStartLinenumber(63);  
    Path baseDir = Path.of("src", "test", "resources");
    String text = fileImport.importTextFromFile(baseDir , fileSpec);
    assertTrue(text.startsWith("### Codeset Sides"));
    assertTrue(text.contains("ThrottleInst"));
  }
  
  @Test
  void importBySearch() throws IOException {
    fileSpec.setPath("md2orchestra-proto.md");
    fileSpec.setStartSearch("### Codeset Sides");
    fileSpec.setEndSearch("multileg instruments)");   
    Path baseDir = Path.of("src", "test", "resources");
    String text = fileImport.importTextFromFile(baseDir , fileSpec);
    assertTrue(text.startsWith("### Codeset Sides"));
    assertTrue(text.contains("multileg instruments)"));
  }
  
  @Test
  void importByStartSearchOnly() throws IOException {
    fileSpec.setPath("md2orchestra-proto.md");
    fileSpec.setStartSearch("### Codeset Sides"); 
    Path baseDir = Path.of("src", "test", "resources");
    String text = fileImport.importTextFromFile(baseDir , fileSpec);
    assertTrue(text.startsWith("### Codeset Sides"));
    assertTrue(text.contains("ThrottleInst"));
  }

}
