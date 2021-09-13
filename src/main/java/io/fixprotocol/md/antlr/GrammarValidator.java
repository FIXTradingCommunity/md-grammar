package io.fixprotocol.md.antlr;

import java.io.PrintStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Grammar validator
 *
 * @author Don Mendelson
 *
 */
final class GrammarValidator extends BaseErrorListener {
  boolean isValid = true;
  private final PrintStream out;

  public GrammarValidator(PrintStream out) {
    super();
    this.out = out;
  }

  public boolean isValid() {
    return isValid;
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
      int charPositionInLine, String msg, RecognitionException e) {
    isValid = false;
    out.format("Failed to parse at line %d position %d due to %s", line, charPositionInLine, msg);
  }
}
