package io.fixprotocol.md.event;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatatypeInferenceTest {

  private DatatypeInference datatypes;

  @BeforeEach
  void setUp() throws Exception {
    datatypes = new DatatypeInference();
  }

  @Test
  void testGetDatatype() {
    assertEquals(Boolean.class, datatypes.inferDatatype("Y"));
    assertEquals(Character.class, datatypes.inferDatatype("c"));
    assertEquals(Number.class, datatypes.inferDatatype("987"));
    assertEquals(Number.class, datatypes.inferDatatype("-987.65"));
    assertEquals(String.class, datatypes.inferDatatype("widget99"));
    assertEquals(String.class, datatypes.inferDatatype(" "));
    assertEquals(String.class, datatypes.inferDatatype("-987.65z"));
  }

}
