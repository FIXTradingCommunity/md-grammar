package io.fixprotocol.md.app;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Merge2MarkdownTest {

  @BeforeAll
  static void setUpOnce() {
    new File("target/test").mkdirs();
  }

  @Test
  void testMergeFileInputStreamOutputStreamPath() throws IOException {
    Merge2Markdown merge2Markdown = Merge2Markdown.builder().inputFile("src/test/resources/documentwithimport.md")
        .outputFile("target/test/Merge2Markdown.md").importDir("src/test/resources").build();
    merge2Markdown.merge();
  }

}
