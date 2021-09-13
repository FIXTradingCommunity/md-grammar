package io.fixprotocol.md.util;

import java.util.ArrayList;
import java.util.List;

public final class ListUtil {

  public static <T> void append(List<T> dest, List<T> source, int from, int toExclusive) {
    dest.addAll(source.subList(from, toExclusive));
    // System.out.format("Append %s => %s%n", source.subList(from, toExclusive), dest.toString());
  }

  public static <T> void insert(List<T> dest, int insertPos, List<T> source, int from,
      int toExclusive) {
    dest.addAll(insertPos, source.subList(from, toExclusive));
    // System.out.format("Insert %s at %d => %s%n", source.subList(from, toExclusive).toString(),
    // insertPos, dest.toString());
  }

  /**
   * Merges two lists while preserving order.
   *
   * Common sequences are identified in two lists and are output in a merged List in same order as
   * the originals. If a sequence of elements is found in the second List between two common
   * elements, it is inserted between them in the merged list. If a non-overlapping sequence is
   * found in the second List it is appended to the merged List. The method is non-destructive of
   * its arguments.
   *
   * It is assumed that the common elements of the two lists are in the same order, but they are
   * allowed to have intervening non-common elements.
   *
   * The only requirement for element type is that its {@code equals()} method is transitive and
   * consistent, as specified by {@code Object.equals()}.
   *
   * @param <T> element type
   * @param first a sequence of elements
   * @param second a sequence of elements
   * @return a merged list
   */
  public static <T> List<T> merge(List<T> first, List<T> second) {
    final List<T> merged = new ArrayList<>(first);
    int lastMatchPos = -1;
    for (int pos2 = 0; pos2 < second.size(); pos2++) {
      final int matchPos = merged.indexOf(second.get(pos2));
      if (matchPos != -1) {
        // System.out.format("matched at pos2=%d pos1=%d val=[%s]%n", pos2, matchPos,
        // second.get(pos2));
        insert(merged, matchPos, second, lastMatchPos + 1, pos2);
        lastMatchPos = pos2;
      } /*
         * else { System.out.format("not matched at pos2=%d val=[%s] lastMatchPos=%d%n", pos2,
         * second.get(pos2), lastMatchPos); }
         */
    }
    append(merged, second, lastMatchPos + 1, second.size());
    return merged;
  }


}

