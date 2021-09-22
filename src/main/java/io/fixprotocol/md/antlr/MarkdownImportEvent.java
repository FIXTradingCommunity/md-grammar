package io.fixprotocol.md.antlr;

import org.antlr.v4.runtime.tree.TerminalNode;
import io.fixprotocol.md.antlr.MarkdownParser.EndContext;
import io.fixprotocol.md.antlr.MarkdownParser.ImportspecContext;
import io.fixprotocol.md.antlr.MarkdownParser.InfostringContext;
import io.fixprotocol.md.antlr.MarkdownParser.LocationContext;
import io.fixprotocol.md.antlr.MarkdownParser.PathContext;
import io.fixprotocol.md.antlr.MarkdownParser.StartContext;
import io.fixprotocol.md.util.FileSpec;

public final class MarkdownImportEvent {

  public static FileSpec infostringToFileSpec(InfostringContext infostring,
      ImportspecContext importspec) {
    final FileSpec spec = new FileSpec();
    final TerminalNode type = infostring.WORD();
    if (type != null) {
      spec.setType(type.getText());
    }
    if (importspec != null) {
      final PathContext pathCtx = importspec.path();
      if (pathCtx != null) {
        final String path = pathCtx.getText();
        spec.setPath(path);
      }
      final StartContext startCtx = importspec.start();
      if (startCtx != null) {
        final LocationContext startLocation = startCtx.location();
        if (startLocation.LINENUMBER() != null) {
          spec.setStartLinenumber(Integer.parseInt(startLocation.LINENUMBER().getText()));
        }
        if (startLocation.STRING() != null) {
          spec.setStartSearch(startLocation.STRING().getText().replaceAll("\"", ""));
        }
      }
      final EndContext endCtx = importspec.end();
      if (endCtx != null) {
        final LocationContext startLocation = endCtx.location();
        if (startLocation.LINENUMBER() != null) {
          spec.setEndLinenumber(Integer.parseInt(startLocation.LINENUMBER().getText()));
        }
        if (startLocation.STRING() != null) {
          spec.setEndSearch(startLocation.STRING().getText().replaceAll("\"", ""));
        }
      }
    }

    return spec;
  }

}
