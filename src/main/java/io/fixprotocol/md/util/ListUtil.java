package io.fixprotocol.md.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class ListUtil {

  /**
   * Merges two lists while preserving order.
   * 
   * Common sequences are identified in two lists and are output in a merged List in same order as
   * the originals. If a sequence of elements is found in the second List between two common
   * elements, it is inserted between them in the merged list. If a non-overlapping sequence is
   * found in the second List it is appended to the merged List. The method is non-destructive of its
   * arguments.
   * 
   * It is assumed that the common elements of the two lists are in the same order, but they are allowed to 
   * have intervening non-common elements. Elements are not required to be unique.
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
    List<T> merged = new ArrayList<T>(first);
    
    int insertionPoint = merged.size();
    for (int i2=0; i2 < second.size(); i2++) {
      T e = second.get(i2);
      int foundPos = nextInstance(merged, insertionPoint == merged.size() ? 0 : insertionPoint, e);
      if (foundPos == -1) {
        if (insertionPoint >= merged.size()) {
          merged.add(e);
        } else {
          merged.add(insertionPoint, e);
        }
        insertionPoint++;
        
      } else {
        insertionPoint = foundPos+1;
      }
    }

    return merged;
  }

  private static <T> int nextInstance(List<T> list, int start, T e) {
    if (start >= list.size()) {
      return -1;
    }
    if (list.get(start).equals(e)) {
      return start;
    }
    ListIterator<T> iter = list.listIterator(start);
    while (iter.hasNext()) {
      int i = iter.nextIndex();
      if (list.get(i).equals(e)) {
        return i;
      }
      iter.next();
    }
    return -1;
  }
  
  

}
