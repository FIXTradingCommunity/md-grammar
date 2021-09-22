module md.grammar {
  exports io.fixprotocol.md.app;

  opens io.fixprotocol.md.app;

  exports io.fixprotocol.md.event;

  opens io.fixprotocol.md.event;

  exports io.fixprotocol.md.util;

  opens io.fixprotocol.md.util;

  requires org.apache.logging.log4j;
  requires org.antlr.antlr4.runtime;
  requires antlr4;
  requires commons.cli;
}
