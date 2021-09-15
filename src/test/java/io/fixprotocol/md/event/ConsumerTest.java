/*
 * Copyright 2020 FIX Protocol Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package io.fixprotocol.md.event;

import java.io.*;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ConsumerTest {

  private static PrintStream out;

  @BeforeAll
  static void setUpOnce() throws FileNotFoundException {
    new File("target/test").mkdirs();
    out = new PrintStream(new FileOutputStream("target/test/ConsumerTest.txt"));
  }

  @AfterAll
  static void cleanUpOnce() throws FileNotFoundException {
    out.close();
  }

  @ParameterizedTest
  @ValueSource(strings = {"src/test/resources/md2orchestra-proto.md"})
  void consume(String fileName) throws IOException {
    Consumer<GraphContext> contextConsumer = new Consumer<>() {

      @Override
      public void accept(GraphContext contextual) {
        Context parent = contextual.getParent();
        if (parent != null) {
          final String[] parentKeys = parent.getKeys();
          out.format("Parent context=%s level=%d%n",
              parentKeys.length > 0 ? parentKeys[0] : "None", parent.getLevel());
        }
        if (contextual instanceof Detail) {
          Detail detail = (Detail) contextual;
          detail.getProperties().forEach(property -> out.format("Property key=%s value=%s%n",
              property.getKey(), property.getValue()));
        } else if (contextual instanceof Documentation) {
          Documentation documentation = (Documentation) contextual;       
          out.format("Documentation %s format %s%n", documentation.getDocumentation(),
              documentation.getFormat());
        } else if (contextual instanceof Context) {
          Context context = (Context) contextual;
          final String[] keys = context.getKeys();
          out.format("Context=%s level=%d%n", keys.length > 0 ? keys[0] : "None",
              context.getLevel());
        }

      }
    };

    InputStream inputStream = new FileInputStream(fileName);
    DocumentParser parser = new DocumentParser();
    parser.parse(inputStream, contextConsumer);
  }

}
