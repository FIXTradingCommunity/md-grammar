package io.fixprotocol.md.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileImport {

  public static class Imported implements AutoCloseable {
    final MappedByteBuffer buffer;
    final FileChannel channel;

    public Imported(MappedByteBuffer buffer, FileChannel channel) {
      this.buffer = buffer;
      this.channel = channel;
    }

    @Override
    public void close() throws Exception {
      channel.close();
    }

    public MappedByteBuffer getBuffer() {
      return buffer;
    }

    public FileChannel getChannel() {
      return channel;
    }
  }

  /**
   * Extracts a String from a buffer
   *
   * @param buffer a buffer containing text
   * @return text with UTF-8 encoding, or an empty string if the buffer is empty
   */
  public static String bufferToText(final ByteBuffer buffer) {
    if (buffer.hasRemaining()) {
      final byte[] data = new byte[buffer.remaining()];
      buffer.get(data);
      return new String(data, StandardCharsets.UTF_8);
    } else {
      return "";
    }
  }

  private final Logger logger = LogManager.getLogger(getClass());

  /**
   * Import text from a file
   *
   * The file to import must be a text file encoded as UTF-8.
   *
   * If no start target is provided in the FileSpec, or the start search is not matched, then the
   * file is read from its beginning.
   *
   * If no end target is provided in the FileSpec, or the end line number is greater than the number
   * of lines in the file, or the end search is not matched, then the file is read until EOF.
   *
   * @param baseDir base directory for resolving file path
   * @param spec specification of a file or file portion. FileSpec should be validated for internal
   *        consistency by invoking {@link FileSpec#isValid()}.
   * @return a buffer containing imported text
   * @throws IOException if the specified file cannot be opened or read
   * @throws InvalidPathException if the path string cannot be converted to a Path.
   */
  public Imported importFromFile(Path baseDir, FileSpec spec) throws IOException {
    final Path filePath = baseDir.resolve(spec.getPath());
    final File file = filePath.toFile();
    long startPosition = 0;
    final long length = file.length();

    RandomAccessFile randomAccessFile;
    try {
      randomAccessFile = new RandomAccessFile(file, "r");

      try {
        final FileChannel channel = randomAccessFile.getChannel();
        long position = -1;
        final int startLine = spec.getStartLinenumber();
        if (startLine != FileSpec.UNKNOWN_LINENUMBER) {
          position = findLinenumber(randomAccessFile, startLine, startPosition);
        } else {
          final String startSearch = spec.getStartSearch();
          if (startSearch != null) {
            position = findTextStart(randomAccessFile, startSearch, startPosition);
          }
        }
        if (position != -1) {
          startPosition = position;
        } else {
          startPosition = 0;
        }

        long endPosition = length;
        position = -1;
        final int endLine = spec.getEndLinenumber();
        if (endLine != FileSpec.UNKNOWN_LINENUMBER) {
          position = findLinenumber(randomAccessFile, endLine - startLine + 2, startPosition);
        } else {
          final String endSearch = spec.getEndSearch();
          if (endSearch != null) {
            position = findTextEnd(randomAccessFile, endSearch, startPosition);
          }
        }
        if (position != -1) {
          endPosition = position;
        }

        final MappedByteBuffer buffer =
            channel.map(MapMode.READ_ONLY, startPosition, endPosition - startPosition);
        return new Imported(buffer, channel);
      } catch (final IOException e) {
        logger.error(e);
        throw e;
      }
    } catch (final FileNotFoundException e) {
      logger.error(e);
      throw e;
    }
  }

  private long findLinenumber(RandomAccessFile randomAccessFile, int lineToSeek, long startPosition)
      throws IOException {
    randomAccessFile.seek(startPosition);
    long position = 0;
    for (int i = 0; i < lineToSeek; i++) {
      position = randomAccessFile.getFilePointer();
      if (randomAccessFile.readLine() == null) {
        return -1;
      }
    }
    return position;
  }

  private long findTextEnd(RandomAccessFile randomAccessFile, String searchText, long startPosition)
      throws IOException {
    randomAccessFile.seek(startPosition);
    String lineText = "";
    do {
      // position of start of line
      final long position = randomAccessFile.getFilePointer();
      lineText = randomAccessFile.readLine();
      if (lineText != null && lineText.contains(searchText)) {
        return position + lineText.length();
      }
    } while (lineText != null);
    return -1;
  }

  private long findTextStart(RandomAccessFile randomAccessFile, String searchText,
      long startPosition) throws IOException {
    randomAccessFile.seek(startPosition);
    String lineText = "";
    do {
      // position of start of line
      final long position = randomAccessFile.getFilePointer();
      lineText = randomAccessFile.readLine();
      if (lineText != null && lineText.contains(searchText)) {
        return position;
      }
    } while (lineText != null);
    return -1;
  }
}
