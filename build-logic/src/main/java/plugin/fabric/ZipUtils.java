
/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021-2022 FabricMC
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

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ZipUtils {
  public static void replace(Path zip, String path, byte[] bytes) throws IOException {
    try (FileSystemUtil.Delegate fs = FileSystemUtil.getJarFileSystem(zip, true)) {
      Path fsPath = fs.get().getPath(path);

      if (Files.exists(fsPath)) {
        Files.write(fsPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      } else {
        throw new NoSuchFileException(fsPath.toString());
      }
    }
  }

  public static byte @Nullable [] unpackNullable(Path zip, String path) throws IOException {
    try {
      return unpack(zip, path);
    } catch (NoSuchFileException e) {
      return null;
    }
  }

  public static byte[] unpack(Path zip, String path) throws IOException {
    try (FileSystemUtil.Delegate fs = FileSystemUtil.getJarFileSystem(zip, false)) {
      return fs.readAllBytes(path);
    }
  }
}
