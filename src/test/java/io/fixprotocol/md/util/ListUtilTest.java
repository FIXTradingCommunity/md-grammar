package io.fixprotocol.md.util;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListUtilTest {

  @Test
  void mergeOverlap() {
    List<Integer> first = List.of(1, 2, 3, 6, 7);
    List<Integer> second = List.of(2, 3, 4, 5, 6);
    List<Integer> merged = ListUtil.merge(first, second);
    assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), merged);
  }
  
  @Test
  void mergeNoOverlap() {
    List<Integer> first = List.of(1, 2, 3, 6, 7);
    List<Integer> second = List.of(8, 9);
    List<Integer> merged = ListUtil.merge(first, second);
    assertEquals(List.of(1, 2, 3, 6, 7, 8, 9), merged);
  }

}
