package io.fixprotocol.md.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A specification of a file and optionally a range within the file
 * 
 * A portion of a file may be specified either as a range of line numbers or as text to search
 * within a file. Either way, a range specifies the start of the file portion and its end. If end is
 * not explicit, then EOF is assumed. If end is specified, then it must be in the same metric as the
 * start. This start and end must both be line numbers or both must be searches.
 * 
 * A begin search stops at the first instance of the text specified. It is recommended to search for
 * unique values. An end search matches the first instance of text after the the start search's
 * target. The end search need not be unique in the file.
 * 
 * If a range is not specified, then the whole file is assumed.
 * 
 * 
 * @author Don Mendelson
 *
 */
public class FileSpec {

  public static final int UNKNOWN_LINENUMBER = -1;

  private final Logger logger = LogManager.getLogger(getClass());

  private int endLine = UNKNOWN_LINENUMBER;
  private String endSearch;
  private String path;
  private int startLine = UNKNOWN_LINENUMBER;
  private String startSearch;
  private String type;

  public int getEndLinenumber() {
    return endLine;
  }

  public String getEndSearch() {
    return endSearch;
  }

  public String getPath() {
    return path;
  }

  public int getStartLinenumber() {
    return startLine;
  }

  public String getStartSearch() {
    return startSearch;
  }

  public String getType() {
    return type;
  }

  /**
   * Validates this FileSpec for internal consistency
   * @return returns {@code true} if this FileSpec is valid
   */
  public boolean isValid() {
    // start or end without a path is invalid
    if ((endLine != UNKNOWN_LINENUMBER || startLine != UNKNOWN_LINENUMBER || endSearch != null
        || startSearch != null) && path == null) {
      logger.error("Invalid file spec; start or end specified without file path");
      return false;
    }
    // can't have both linenumber and text to search
    if (endLine != UNKNOWN_LINENUMBER && endSearch != null) {
      logger.error("Invalid file spec; both start linenumber and start search specified");
      return false;
    }
    if (startLine != UNKNOWN_LINENUMBER && startSearch != null) {
      logger.error("Invalid file spec; both end linenumber and end search specified");
      return false;
    }
    return true;
  }

  public void setEndLinenumber(int endLine) {
    this.endLine = endLine;
  }

  public void setEndSearch(String endSearch) {
    this.endSearch = endSearch;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setStartLinenumber(int startLine) {
    this.startLine = startLine;
  }

  public void setStartSearch(String startSearch) {
    this.startSearch = startSearch;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "FileSpec [" + (type != null ? "type=" + type + ", " : "")
        + (path != null ? "path=" + path + ", " : "") + "startLine=" + startLine + ", "
        + (startSearch != null ? "startSearch=" + startSearch + ", " : "") + "endLine=" + endLine
        + ", " + (endSearch != null ? "endSearch=" + endSearch : "") + "]";
  }

}
