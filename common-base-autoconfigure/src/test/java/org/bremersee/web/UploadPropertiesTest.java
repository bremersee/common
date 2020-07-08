/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.web;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * The upload properties test.
 *
 * @author Christian Bremer
 */
class UploadPropertiesTest {

  /**
   * Equals and to string.
   */
  @Test
  void equalsAndToString() {
    UploadProperties a = new UploadProperties();
    UploadProperties b = new UploadProperties();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertEquals(a.toString(), b.toString());
  }

  /**
   * Gets tmp dir.
   */
  @Test
  void getTmpDir() {
    UploadProperties properties = new UploadProperties();
    File value = new File(System.getProperty("java.io.tmpdir"));
    properties.setTmpDir(value);
    assertEquals(value, properties.getTmpDir());
  }
}