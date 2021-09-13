package io.fixprotocol.md.event;


/**
 * Context within a document
 *
 * @author Don Mendelson
 *
 */
public interface DocumentContext {

  /**
   * Unknown line number or character position in a document
   */
  int UNKNOWN_POSITION = -1;

  /**
   * Character position in a line
   *
   * @return line position or {@link #UNKNOWN_POSITION}
   */
  int getCharPositionInLine();

  /**
   * Line number in a document
   *
   * @return line number or {@link #UNKNOWN_POSITION}
   */
  int getLine();

}
