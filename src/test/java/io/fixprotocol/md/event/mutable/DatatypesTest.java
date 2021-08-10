package io.fixprotocol.md.event.mutable;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatatypesTest {

  private Datatypes datatypes;

  @BeforeEach
  void setUp() throws Exception {
    datatypes = new Datatypes();
  }

  @Test
  void testGetDatatype() {
    assertEquals(Boolean.class, datatypes.getDatatype("Y"));
    assertEquals(Character.class, datatypes.getDatatype("c"));
    assertEquals(Number.class, datatypes.getDatatype("987"));
    assertEquals(Number.class, datatypes.getDatatype("-987.65"));
    assertEquals(String.class, datatypes.getDatatype("widget99"));
    assertEquals(String.class, datatypes.getDatatype(" "));
    assertEquals(String.class, datatypes.getDatatype("-987.65z"));
  }

}
