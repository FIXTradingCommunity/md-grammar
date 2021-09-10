package io.fixprotocol.md.antlr;

import java.io.IOException;
import java.io.StringReader;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.fixprotocol.md.antlr.InfostringParser.EndContext;
import io.fixprotocol.md.antlr.InfostringParser.ImportspecContext;
import io.fixprotocol.md.antlr.InfostringParser.LocationContext;
import io.fixprotocol.md.antlr.InfostringParser.PathContext;
import io.fixprotocol.md.antlr.InfostringParser.StartContext;
import io.fixprotocol.md.antlr.InfostringParser.TypeContext;
import io.fixprotocol.md.util.FileSpec;

public class InfostringToFileSpec {

  private static class SyntaxErrorListener extends BaseErrorListener {
    private final Logger logger = LogManager.getLogger(getClass());
    private int errors = 0;


    public int getErrors() {
      return errors;
    }

    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
        int charPositionInLine, String msg, RecognitionException e) {
      errors++;
      logError(line, charPositionInLine, msg);
    }

    void logError(int line, int charPositionInLine, String msg) {
      logger.error("Infostring parser failed at position {} due to {}", charPositionInLine, msg);
    }
  }

  /**
   * Parses an infostring and populates a new FileSpec
   * @param infostring string following the opening fence of a fenced code block
   * @return a new FileSpec, or {@code null} if parsing fails
   */
  public FileSpec parse(String infostring) {
    InfostringLexer lexer;
    try {
      lexer = new InfostringLexer(CharStreams.fromReader(new StringReader(infostring)));
    } catch (IOException e) {
      // would be internal error
      throw new RuntimeException(e);
    }
    InfostringParser parser = new InfostringParser(new CommonTokenStream(lexer));
    final SyntaxErrorListener errorListener = new SyntaxErrorListener();
    parser.addErrorListener(errorListener);
    if (errorListener.getErrors() > 0) {
      return null;
    } else {
      FileSpec spec = new FileSpec();
      io.fixprotocol.md.antlr.InfostringParser.InfostringContext ctx = parser.infostring();
      final TypeContext type = ctx.type();
      if (type != null) {
        spec.setType(type.getText());
      }
      ImportspecContext importspec = ctx.importspec();
      if (importspec != null) {
        final PathContext pathCtx = importspec.path();
        if (pathCtx != null) {
          final String path = pathCtx.getText();
          spec.setPath(path);
        }
        StartContext startCtx = importspec.start();
        if (startCtx != null) {
          LocationContext startLocation = startCtx.location();
          if (startLocation.LINENUMBER() != null) {
            spec.setStartLinenumber(Integer.parseInt(startLocation.LINENUMBER().getText()));
          }
          if (startLocation.STRING() != null) {
            spec.setStartSearch(startLocation.STRING().getText());
          }
        }
      }
      EndContext endCtx = importspec.end();
      if (endCtx != null) {
        LocationContext startLocation = endCtx.location();
        if (startLocation.LINENUMBER() != null) {
          spec.setEndLinenumber(Integer.parseInt(startLocation.LINENUMBER().getText()));
        }
        if (startLocation.STRING() != null) {
          spec.setEndSearch(startLocation.STRING().getText());
        }
      }
      return spec;
    }
  }

}

