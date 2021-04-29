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

public interface MutableContext extends Context, MutableContextual {

  /**
   * Add a key to the Context
   *
   * @param key a key
   */
  void addKey(String key);

  /**
   * Add a pair of keys to the Context that may be interpreted as a key-value pair
   *
   * @param key a key to the Context
   * @param value a value associated with the key
   */
  default void addPair(String key, String value) {
    addKey(key);
    addKey(value);
  }

}
