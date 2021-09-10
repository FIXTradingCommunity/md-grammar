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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.fixprotocol.md.antlr.MarkdownParser.BlockContext;
import io.fixprotocol.md.antlr.MarkdownParser.BlockquoteContext;
import io.fixprotocol.md.antlr.MarkdownParser.CellContext;
import io.fixprotocol.md.antlr.MarkdownParser.DocumentContext;
import io.fixprotocol.md.antlr.MarkdownParser.FencedcodeblockContext;
import io.fixprotocol.md.antlr.MarkdownParser.HeadingContext;
import io.fixprotocol.md.antlr.MarkdownParser.InfostringContext;
import io.fixprotocol.md.antlr.MarkdownParser.ListContext;
import io.fixprotocol.md.antlr.MarkdownParser.ListlineContext;
import io.fixprotocol.md.antlr.MarkdownParser.ParagraphContext;
import io.fixprotocol.md.antlr.MarkdownParser.ParagraphlineContext;
import io.fixprotocol.md.antlr.MarkdownParser.QuotelineContext;
import io.fixprotocol.md.antlr.MarkdownParser.TableContext;
import io.fixprotocol.md.antlr.MarkdownParser.TabledelimiterrowContext;
import io.fixprotocol.md.antlr.MarkdownParser.TableheadingContext;
import io.fixprotocol.md.antlr.MarkdownParser.TablerowContext;
import io.fixprotocol.md.event.ContextFactory;
import io.fixprotocol.md.event.Documentation;
import io.fixprotocol.md.event.GraphContext;
import io.fixprotocol.md.event.MutableContext;
import io.fixprotocol.md.event.MutableDetail;
import io.fixprotocol.md.event.MutableDetailProperties;
import io.fixprotocol.md.event.MutableDetailTable;
import io.fixprotocol.md.event.MutableDocumentContext;
import io.fixprotocol.md.event.MutableDocumentation;
import io.fixprotocol.md.event.MutableGraphContext;
import io.fixprotocol.md.util.FileSpec;

/**
 * Generates events for document consumers
 *
 * @author Don Mendelson
 *
 */
public class MarkdownEventSource implements MarkdownListener {

  private static final String CELL_NONTEXT = " |\t";
  private static final String WHITESPACE_REGEX = "[ \t]";
  
  private ContextFactory contextFactory = new ContextFactory();

  static String normalizeList(List<? extends ListlineContext> textlines) {
    return textlines.stream().map(p -> p.LISTLINE().getText()).collect(Collectors.joining("\n"));
  }

  static String normalizeParagraph(List<? extends ParagraphlineContext> textlines) {
    return textlines.stream().map(p -> p.PARAGRAPHLINE().getText())
        .collect(Collectors.joining(" "));
  }

  static String normalizeQuote(List<? extends QuotelineContext> textlines) {
    return textlines.stream().map(p -> p.QUOTELINE().getText()).collect(Collectors.joining("\n"));
  }

  static String trimCell(String text) {
    int beginIndex = 0;
    int endIndex = text.length();
    for (; beginIndex < endIndex
        && (CELL_NONTEXT.indexOf(text.charAt(beginIndex)) != -1); beginIndex++);
    for (; endIndex > beginIndex
        && (CELL_NONTEXT.indexOf(text.charAt(endIndex - 1)) != -1); endIndex--);
    return text.substring(beginIndex, endIndex);
  }

  private final Consumer<? super GraphContext> contextConsumer;
  private final Deque<MutableContext> contexts = new ArrayDeque<>();
  private boolean inTableHeading = false;
  private final List<String> lastBlocks = new ArrayList<>();
  private int lastColumnNo;
  private final List<String> lastRowValues = new ArrayList<>();
  private final List<String> lastTableHeadings = new ArrayList<>();
  private final Logger logger = LogManager.getLogger(getClass());

  public MarkdownEventSource(Consumer<? super GraphContext> contextConsumer) {
    this.contextConsumer = contextConsumer;
  }

  @Override
  public void enterBlock(BlockContext ctx) {
    // no action

  }

  @Override
  public void enterBlockquote(BlockquoteContext ctx) {
    // no action

  }

  @Override
  public void enterCell(CellContext ctx) {
    // no action

  }

  @Override
  public void enterDocument(DocumentContext ctx) {
    // no action

  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
    // no action

  }

  @Override
  public void enterFencedcodeblock(FencedcodeblockContext ctx) {
    // TODO Auto-generated method stub

  }

  @Override
  public void enterHeading(HeadingContext ctx) {
    supplyLastDocumentation();
    lastBlocks.clear();
  }

  @Override
  public void enterInfostring(InfostringContext ctx) {
    // TODO Auto-generated method stub

  }

  @Override
  public void enterList(ListContext ctx) {
    // no action

  }

  @Override
  public void enterListline(ListlineContext ctx) {
    // no action

  }

  @Override
  public void enterParagraph(ParagraphContext ctx) {

  }

  @Override
  public void enterParagraphline(ParagraphlineContext ctx) {
    // no action

  }

  @Override
  public void enterQuoteline(QuotelineContext ctx) {
    // no action

  }

  @Override
  public void enterTable(TableContext ctx) {
    supplyLastDocumentation();
    lastBlocks.clear();
  }

  @Override
  public void enterTabledelimiterrow(TabledelimiterrowContext ctx) {
    // no action

  }

  @Override
  public void enterTableheading(TableheadingContext ctx) {
    lastTableHeadings.clear();
    inTableHeading = true;
  }

  @Override
  public void enterTablerow(TablerowContext ctx) {
    lastColumnNo = 0;
    lastRowValues.clear();
  }

  @Override
  public void exitBlock(BlockContext ctx) {
    // no action

  }

  @Override
  public void exitBlockquote(BlockquoteContext ctx) {
    final List<QuotelineContext> textlines = ctx.quoteline();
    lastBlocks.add(normalizeQuote(textlines));
  }

  @Override
  public void exitCell(CellContext ctx) {
    final String cellText = trimCell(ctx.CELLTEXT().getText());
    if (inTableHeading) {
      lastTableHeadings.add(cellText);
    } else {
      lastRowValues.add(cellText);
    }
    lastColumnNo++;
  }

  @Override
  public void exitDocument(DocumentContext ctx) {
    supplyLastDocumentation();
  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
    // no action

  }

  @Override
  public void exitFencedcodeblock(FencedcodeblockContext ctx) {
    String format = Documentation.MARKDOWN;
    InfostringContext infostringCtx = ctx.infostring();
    if (infostringCtx != null) {
      String infostring = infostringCtx.PARAGRAPHLINE().getText();
      InfostringToFileSpec infostringToFileSpec = new InfostringToFileSpec();
      FileSpec spec = infostringToFileSpec.parse(infostring);
      if (spec != null) {
        if (spec.isValid()) {
          if (spec.getType() != null) {
            format = spec.getType();
          }
          String path = spec.getPath();
          if (path != null) {
            
          }
        }
      } else {
        logger.error("Infostring for fenced code block is invalid at line {} position {}",
            infostringCtx.start.getLine(), infostringCtx.start.getCharPositionInLine());
      }
    }
    List<ParagraphlineContext> lines = ctx.paragraphline();
    String text =
        lines.stream().map(p -> p.PARAGRAPHLINE().getText()).collect(Collectors.joining("\n"));

    final MutableDocumentation documentation = contextFactory.createDocumentation(text, format);
    updateParentContext(documentation);
    contextConsumer.accept(documentation);
  }

  @Override
  public void exitHeading(HeadingContext ctx) {
    final String headingLine = ctx.HEADINGLINE().getText();
    // Only a new heading changes the context
    // Heading level is length of first word formed with '#'
    final int headingLevel = headingLine.indexOf(" ");
    final String[] headingWords = headingLine.substring(headingLevel + 1).split(WHITESPACE_REGEX);
    final MutableContext context = contextFactory.createContext(headingWords, headingLevel);
    context.setLine(ctx.start.getLine());
    context.setCharPositionInLine(ctx.start.getCharPositionInLine());
    updateParentContext(context);

    contextConsumer.accept(context);
  }

  @Override
  public void exitInfostring(InfostringContext ctx) {
    // TODO Auto-generated method stub

  }

  @Override
  public void exitList(ListContext ctx) {
    final List<ListlineContext> textlines = ctx.listline();
    lastBlocks.add(normalizeList(textlines));
  }

  @Override
  public void exitListline(ListlineContext ctx) {
    // no action

  }

  @Override
  public void exitParagraph(ParagraphContext ctx) {
    final List<ParagraphlineContext> textlines = ctx.paragraphline();
    lastBlocks.add(normalizeParagraph(textlines));
  }

  @Override
  public void exitParagraphline(ParagraphlineContext ctx) {
    // no action

  }

  @Override
  public void exitQuoteline(QuotelineContext ctx) {
    // no action

  }

  @Override
  public void exitTable(TableContext ctx) {
    if (!inTableHeading) {
      final MutableDetailTable detailTable = contextFactory.createDetailTable();
      detailTable.setLine(ctx.start.getLine());
      detailTable.setCharPositionInLine(ctx.start.getCharPositionInLine());
      final List<TablerowContext> tablerows = ctx.tablerow();

      for (final TablerowContext tablerow : tablerows) {
        final MutableDetailProperties row = detailTable.newRow();
        if (row instanceof MutableDocumentContext) {
          MutableDocumentContext mutableRow = (MutableDocumentContext)row;
          mutableRow.setLine(tablerow.start.getLine());
          mutableRow.setCharPositionInLine(tablerow.start.getCharPositionInLine());
        }

        for (int i = 0; i < tablerow.cell().size() && i < lastTableHeadings.size(); i++) {
          final CellContext cell = tablerow.cell(i);
          if (cell != null) {
            row.addProperty(lastTableHeadings.get(i), cell.getText());
          } else {
            logger.error("MarkdownEventSource table cell missing in column {}", i);
          }
        }
      }
      updateParentContext(detailTable);
      if (contextConsumer != null) {
        contextConsumer.accept(detailTable);
      }
    }
  }

  @Override
  public void exitTabledelimiterrow(TabledelimiterrowContext ctx) {
    // no action

  }

  @Override
  public void exitTableheading(TableheadingContext ctx) {
    inTableHeading = false;
  }

  @Override
  public void exitTablerow(TablerowContext ctx) {
    if (!inTableHeading) {
      final MutableDetail detail = contextFactory.createDetail();
      detail.setLine(ctx.start.getLine());
      detail.setCharPositionInLine(ctx.start.getCharPositionInLine());
      for (int i = 0; i < lastColumnNo && i < lastTableHeadings.size(); i++) {
        final String value = lastRowValues.get(i);
        if (!value.isBlank()) {
          detail.addProperty(lastTableHeadings.get(i), value);
        }
      }
      updateParentContext(detail);
      if (contextConsumer != null) {
        contextConsumer.accept(detail);
      }
    }
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    // should error node be logged?

  }

  @Override
  public void visitTerminal(TerminalNode node) {
    // no action

  }

  void updateParentContext(final MutableContext context) {
    // Remove previous contexts at same or lower level
    contexts.removeIf(c -> context.getLevel() <= c.getLevel());
    final MutableContext lastContext = contexts.peekLast();

    // Add top level context or lower level than parent
    if (lastContext == null) {
      contexts.add(context);
    } else if (context.getLevel() > lastContext.getLevel()) {
      context.setParent(lastContext);
      contexts.add(context);
    }
  }

  void updateParentContext(final MutableGraphContext contextual) {
    final MutableContext lastContext = contexts.peekLast();
    contextual.setParent(lastContext);
  }

  private String normalizeBlocks() {
    return String.join("\n\n", lastBlocks);
  }

  private void supplyLastDocumentation() {
    if (!lastBlocks.isEmpty()) {
      final String paragraphs = normalizeBlocks();
      final MutableDocumentation documentation = contextFactory.createDocumentation(paragraphs);
      updateParentContext(documentation);
      contextConsumer.accept(documentation);
    }
  }

}
