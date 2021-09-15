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
 */
package io.fixprotocol.md.antlr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import io.fixprotocol.md.antlr.MarkdownParser.BlockContext;
import io.fixprotocol.md.antlr.MarkdownParser.CellContext;
import io.fixprotocol.md.antlr.MarkdownParser.DocumentContext;
import io.fixprotocol.md.antlr.MarkdownParser.FencedcodeblockContext;
import io.fixprotocol.md.antlr.MarkdownParser.HeadingContext;
import io.fixprotocol.md.antlr.MarkdownParser.InfostringContext;
import io.fixprotocol.md.antlr.MarkdownParser.ParagraphContext;
import io.fixprotocol.md.antlr.MarkdownParser.ParagraphlineContext;
import io.fixprotocol.md.antlr.MarkdownParser.TableContext;
import io.fixprotocol.md.antlr.MarkdownParser.TableheadingContext;
import io.fixprotocol.md.antlr.MarkdownParser.TablerowContext;
import io.fixprotocol.md.antlr.MarkdownParser.TextlineContext;

class MdGrammarTest {

  @BeforeAll
  public static void setupOnce() {
    new File("target/test").mkdirs();
  }

  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/badmarkdown.md"})
  void invalid(String fileName) throws IOException {
    String outputFileName = getOutputFilename(fileName);
    try (PrintStream out = new PrintStream(new FileOutputStream(outputFileName))) {
      final GrammarValidator validator = new GrammarValidator(out);
      MarkdownLexer lexer =
          new MarkdownLexer(CharStreams.fromStream(new FileInputStream(fileName)));
      lexer.removeErrorListeners();
      lexer.addErrorListener(validator);
      MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));
      parser.removeErrorListeners();
      parser.addErrorListener(validator);
      parser.addErrorListener(validator);
      DocumentContext document = parser.document();
      assertFalse(validator.isValid);
    }
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/md2orchestra-proto.md"})
  void testRig(String fileName) throws Exception {
    String[] args = new String[] {"io.fixprotocol.md.antlr.Markdown", "document", "-tree",
        "-tokens", fileName};
    TestRig testRig = new TestRig(args);
    testRig.process();
  }

  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/md2orchestra-proto.md"})
  void valid(String fileName) throws IOException {
    String outputFileName = getOutputFilename(fileName);
    try (PrintStream out = new PrintStream(new FileOutputStream(outputFileName))) {
      final GrammarValidator validator = new GrammarValidator(out);
      MarkdownLexer lexer =
          new MarkdownLexer(CharStreams.fromStream(new FileInputStream(fileName)));
      lexer.removeErrorListeners();
      lexer.addErrorListener(validator);
      MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));     
      parser.removeErrorListeners();
      parser.addErrorListener(validator);

      DocumentContext document = parser.document();
      List<BlockContext> blocks = document.block();
      for (BlockContext block : blocks) {
        HeadingContext heading = block.heading();
        if (heading != null) {
          // String level = heading.headinglevel().getText();
          String textline = heading.HEADINGLINE().getText();
          out.format("Heading text: %s%n", textline);
        } else {
          ParagraphContext paragraph = block.paragraph();
          if (paragraph != null) {
            List<ParagraphlineContext> textlines = paragraph.paragraphline();
            String paragraphText = textlines.stream().map(p -> p.PARAGRAPHLINE().getText())
                .collect(Collectors.joining(" "));
            out.format("Paragraph text: %s%n", paragraphText);
          } else {
            FencedcodeblockContext fencedCodeBlock = block.fencedcodeblock();
            if (fencedCodeBlock != null) {
              InfostringContext infoString = fencedCodeBlock.infostring();
              out.format("Fenced code block infostring: %s%n", infoString.getText());
              List<TextlineContext> textlines = fencedCodeBlock.textline();
              String paragraphText = textlines.stream().map(t -> t.getText())
                  .collect(Collectors.joining("\n"));
              out.format("Fenced code block text: %s%n", paragraphText);
            } else {
              TableContext table = block.table();
              if (table != null) {
                TableheadingContext tableHeading = table.tableheading();
                TablerowContext headingRow = tableHeading.tablerow();
                List<CellContext> colHeadings = headingRow.cell();
                for (CellContext colHeading : colHeadings) {
                  out.format("Column heading: %s%n", colHeading.getText());
                }
                List<TablerowContext> rows = table.tablerow();
                for (TablerowContext row : rows) {
                  List<CellContext> cells = row.cell();
                  for (CellContext cell : cells) {
                    String celltext = cell.CELLTEXT().getText();
                    out.format("Cell: %s%n", celltext);
                  }
                }
              }
            }
          }
        }
      }
      //assertTrue(validator.isValid);
    }
  }

  private String getOutputFilename(String fileName) {
    Path inputPath = Path.of(fileName);
    String inputFileName = inputPath.getFileName().toString();
    String withoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
    String outputFileName = "target/test/" + withoutExtension + "-out.txt";
    return outputFileName;
  }
}
