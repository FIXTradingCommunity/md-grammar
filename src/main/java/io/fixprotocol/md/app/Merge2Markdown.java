package io.fixprotocol.md.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.fixprotocol.md.antlr.MarkdownImportEvent;
import io.fixprotocol.md.antlr.MarkdownLexer;
import io.fixprotocol.md.antlr.MarkdownParser;
import io.fixprotocol.md.antlr.MarkdownParser.DocumentContext;
import io.fixprotocol.md.antlr.MarkdownParser.ImportspecContext;
import io.fixprotocol.md.antlr.MarkdownParser.InfostringContext;
import io.fixprotocol.md.antlr.MarkdownParserBaseListener;
import io.fixprotocol.md.antlr.MarkdownParserListener;
import io.fixprotocol.md.util.FileImport;
import io.fixprotocol.md.util.FileImport.Imported;
import io.fixprotocol.md.util.FileSpec;

/**
 * Refreshes the contents of fenced code blocks that have an import spec (a Markdown extension)
 * 
 * The original infostring as a class of data for block contents is still supported. Optionally, it may be extended
 * with an "import" clause followed by a filename to import an entire file. If only a portion of a
 * file is desired, it can either be specified as a range of line numbers or by the user of a search
 * strings for the start and end of the file portion.
 * 
 * Syntax summary:
 * 
 * <pre>
 import &lt;filename&gt; 
 import &lt;filename&gt; &lt;beginlineno&gt; - &lt;endlineno&gt; 
 import &lt;filename&gt; from "beginstring" to "endstring"
 * </pre>
 * <ul>
 * <li>"import", "from" and "to" are literal keywords. A hyphen "-" may be used as a synonym for
 * "to". The "from" keyword is optional.</li>
 * <li>Line number ranges are inclusive.</li>
 * <li>Search strings are delimited by double quote (") while line numbers consist of digits without
 * a delimiter.</li>
 * <li>The end search string matches the first instance of the string after the begin search is
 * matched.</li>
 * <li>If no end line number or end search string is given, then the remainder of the file is
 * included.</li>
 * </ul>
 * 
 * @author Don Mendelson
 * @see <a href="https://github.github.com/gfm/#fenced-code-blocks">Fenced code blocks</a>
 */
public class Merge2Markdown {

  public static class Builder {
    public String importDir;
    public String inputFilename;
    public String outputFilename;

    public Merge2Markdown build() {
      return new Merge2Markdown(this);
    }

    public Builder importDir(final String importDir) {
      this.importDir = importDir;
      return this;
    }

    public Builder inputFile(final String inputFilename) {
      this.inputFilename = inputFilename;
      return this;
    }

    public Builder outputFile(final String outputFilename) {
      this.outputFilename = outputFilename;
      return this;
    }
  }

  private class MarkdownListener extends MarkdownParserBaseListener {
    private final Path baseDir;
    private final FileImport fileImport = new FileImport();
    private final FileChannel inChannel;
    private final Logger logger = LogManager.getLogger(getClass());
    private final WritableByteChannel outChannel;
    private int readOffset = 0;

    public MarkdownListener(final FileInputStream inputStream, OutputStream outputStream,
        Path baseDir) {
      super();
      this.inChannel = inputStream.getChannel();
      this.outChannel = Channels.newChannel(outputStream);
      this.baseDir = baseDir;
    }

    @Override
    public void exitDocument(MarkdownParser.DocumentContext ctx) {
      try {
        transfer(readOffset, ctx.stop.getStopIndex());
      } catch (final IOException e) {
        logger.fatal("Failed to write remainder of document at offset {}", readOffset, e);
      }
    }

    @Override
    public void exitFencedcodeblock(MarkdownParser.FencedcodeblockContext ctx) {
      final ImportspecContext importspecCtx = ctx.importspec();
      // If no import, then ignore this block so it will be copied literally later
      if (importspecCtx != null) {
        final InfostringContext infostringCtx = ctx.infostring();
        final FileSpec spec =
            MarkdownImportEvent.infostringToFileSpec(infostringCtx, importspecCtx);

        final int startOffset = ctx.start.getStartIndex();
        try {
          // write unwritten portion of file prior to this block
          transfer(readOffset, startOffset);
          // write the open fence, infostring, importspec but not contents of block
          transfer(readOffset, ctx.FENCED_NEWLINE().getSymbol().getStopIndex());
          // write file import

          if (spec != null) {
            if (spec.isValid()) {
              /*
               * if (spec.getType() != null) { String format = spec.getType(); }
               */
              final String path = spec.getPath();
              if (path != null) {
                try {
                  final Imported imported = fileImport.importFromFile(baseDir, spec);
                  final MappedByteBuffer buffer = imported.getBuffer();
                  outChannel.write(buffer);
                } catch (final IOException e) {
                  logger.error(
                      "Failed to import file specified by infostring for fenced code block is invalid at line {} position {}",
                      infostringCtx.start.getLine(), infostringCtx.start.getCharPositionInLine(),
                      e);
                }
              }
            }

            // write last text newline and close fence next
            // textline could end in CRLF or LF, but LF alone is valid markdown
            final ByteBuffer lfBuffer = ByteBuffer.wrap("\n".getBytes());
            outChannel.write(lfBuffer);
            readOffset = ctx.CLOSE_FENCE().getSymbol().getStartIndex();
          } else {
            logger.error("Infostring for fenced code block is invalid at line {} position {}",
                infostringCtx.start.getLine(), infostringCtx.start.getCharPositionInLine());
          }
        } catch (final IOException e) {
          logger.fatal("Failed to write document at offset {}", readOffset, e);
        }
      }
    }

    private void transfer(int startPosition, int endPosition) throws IOException {
      final MappedByteBuffer buffer =
          inChannel.map(MapMode.READ_ONLY, startPosition, endPosition - startPosition);
      outChannel.write(buffer);
      readOffset = endPosition;
    }

  }

  private static class SyntaxErrorListener extends BaseErrorListener {
    private int errors = 0;
    private final Logger logger = LogManager.getLogger(getClass());

    public int getErrors() {
      return errors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
        int charPositionInLine, String msg, RecognitionException e) {
      errors++;
      logError(line, charPositionInLine, msg);
    }

    void logError(int line, int charPositionInLine, String msg) {
      logger.error("Markdown parser failed at line {} position {} due to {}", line,
          charPositionInLine, msg);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Runs Merge2Markdown
   * 
   * @param args command line arguments
   * 
   * <pre>
   usage: Merge2Markdown [options] &lt;input-file&gt;
   -?,--help           display usage
   -d,--import &lt;arg&gt;   directory for file import
   -o,--output &lt;arg&gt;   path of output Markdown file (required)
   * </pre>
   */
  public static void main(String[] args) {
    final Merge2Markdown merge2Markdown;
    try {
      merge2Markdown = Merge2Markdown.parseArgs(args).build();
      merge2Markdown.merge();
    } catch (final Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  static Builder parseArgs(final String[] args) throws ParseException {
    final Options options = new Options();
    options.addOption(Option.builder("o").desc("path of output Markdown file (required)")
        .longOpt("output").numberOfArgs(1).required().build());
    options.addOption(Option.builder("d").desc("directory for file import").longOpt("import")
        .numberOfArgs(1).build());
    options.addOption(
        Option.builder("?").numberOfArgs(0).desc("display usage").longOpt("help").build());

    final DefaultParser parser = new DefaultParser();
    final CommandLine cmd;

    final Builder builder = new Builder();

    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("?")) {
        showHelp(options);
        System.exit(1);
      }

      final List<String> argList = cmd.getArgList();
      if (!argList.isEmpty()) {
        builder.inputFilename = argList.get(0);
      } else {

      }
      if (cmd.hasOption("o")) {
        builder.outputFilename = cmd.getOptionValue("o");
      }

      if (cmd.hasOption("d")) {
        builder.importDir = cmd.getOptionValue("d");
      }
      return builder;
    } catch (final ParseException e) {
      showHelp(options);
      throw e;
    }
  }

  private static void showHelp(final Options options) {
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("Merge2Markdown [options] <input-file", options);
  }

  private final Logger logger = LogManager.getLogger(getClass());

  private final String importDir;
  private final String inputFilename;
  private final String outputFilename;

  private Merge2Markdown(Builder builder) {
    this.importDir = builder.importDir;
    this.inputFilename = builder.inputFilename;
    this.outputFilename = builder.outputFilename;
  }

  public void merge() throws IOException {
    try (FileInputStream inputStream = new FileInputStream(inputFilename);
        FileOutputStream outputStream = new FileOutputStream(outputFilename)) {
      // input is opened twice because the lexer closes the file
      parse(inputFilename, new MarkdownListener(inputStream, outputStream, Path.of(importDir)));
    }
  }

  /**
   * Parse a Markdown document
   *
   * @param inputFilename input file as markdown. Text is assumed to encoded as UTF-8.
   * @param markdownListener handles Markdown events
   * @return {@code true} if the document is fully parsed without errors
   * @throws IOException if the document cannot be read
   */
  private boolean parse(String inputFilename, MarkdownParserListener markdownListener)
      throws IOException {
    Objects.requireNonNull(inputFilename, "Missing inputFilename");
    Objects.requireNonNull(markdownListener, "Missing markdownListener");
    final SyntaxErrorListener errorListener = new SyntaxErrorListener();
    // this reads the entire input and closes the stream
    final CharStream charStream = CharStreams.fromFileName(inputFilename);
    final MarkdownLexer lexer = new MarkdownLexer(charStream);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);
    final MarkdownParser parser = new MarkdownParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    final ParseTreeWalker walker = new ParseTreeWalker();
    final DocumentContext documentContext = parser.document();
    walker.walk(markdownListener, documentContext);

    final int errors = errorListener.getErrors();
    return (errors == 0);
  }
}
