package io.fixprotocol.md.event;



public interface MutableDocumentContext {

  void setCharPositionInLine(int charPositionInLine);

  void setEndOffset(int startOffset);

  void setLine(int line);

  void setStartOffset(int startOffset);

}
