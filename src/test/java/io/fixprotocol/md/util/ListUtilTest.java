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
  void mergeNoOverlap2() {
    List<Integer> first = List.of(2, 3, 4, 5, 6);
    List<Integer> second = List.of(1, 2, 3, 6, 7);
    
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
  
  @Test
  void noInsert() {
    List<String> first = List.of("tag", "presence", "added", "addedep", "documentation", "updated", "updatedep");
    List<String> second = List.of("tag", "presence", "added", "updated", "updatedep", "documentation");

    List<String> merged = ListUtil.merge(first, second);
    assertEquals(List.of("tag", "presence", "added", "addedep", "documentation", "updated", "updatedep"), merged);
  }
  
  @Test
  void multiplePasses() {
    List<String> first = List.of("name", "added", "documentation");
    List<String> second = List.of("name", "added", "addedep", "documentation");
    
    List<String> merged1 = ListUtil.merge(first, second);
    assertEquals(List.of("name", "added", "addedep", "documentation"), merged1);
    List<String> third = List.of("name", "added", "updated", "updatedep", "documentation");
    List<String> merged2 = ListUtil.merge(merged1, third);
    assertEquals(List.of("name", "added", "addedep", "updated", "updatedep", "documentation"), merged2);
  }


}
