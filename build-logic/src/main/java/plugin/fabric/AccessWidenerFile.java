/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package plugin.fabric;

import groovy.json.JsonSlurper;
import org.apache.groovy.json.internal.LazyMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AccessWidenerFile {
  public AccessWidenerFile(String path, String modId, byte[] content) {
    this.path = path;
    this.modId = modId;
    this.content = content;
  }

  private final String path;
  private final String modId;
  private final byte[] content;

  public byte[] content() {
    return content;
  }

  public String path() {
    return path;
  }

  /**
   * Reads the access-widener contained in a mod jar, or returns null if there is none.
   */
  public static AccessWidenerFile fromModJar(Path modJarPath) {
    byte[] modJsonBytes;

    try {
      modJsonBytes = ZipUtils.unpackNullable(modJarPath, "ignite.mod.json");
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read access-widener file from: " + modJarPath.toAbsolutePath(), e);
    }

    if (modJsonBytes == null) {
      return null;
    }

    Object obj = new JsonSlurper().parseText(new String(modJsonBytes, StandardCharsets.UTF_8));
    if (!(obj instanceof LazyMap)) {
      throw new RuntimeException();
    }
    LazyMap jsonObject = (LazyMap) obj;

    if (!jsonObject.containsKey("wideners") || jsonObject.get("wideners") == null || ((List<?>) jsonObject.get("wideners")).isEmpty()) {
      throw new RuntimeException();
    }

    String awPath = ((List<String>) jsonObject.get("wideners")).get(0);
    String modId = (String) jsonObject.get("id");

    byte[] content;
    try {
      content = ZipUtils.unpack(modJarPath, awPath);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not find access widener file (%s) defined in the fabric.mod.json file of %s".formatted(awPath, modJarPath.toAbsolutePath()), e);
    }

    return new AccessWidenerFile(awPath, modId, content);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(path, modId);
    result = 31 * result + Arrays.hashCode(content);
    return result;
  }
}
