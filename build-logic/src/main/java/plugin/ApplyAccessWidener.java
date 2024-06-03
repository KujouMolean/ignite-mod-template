package plugin;

import net.fabricmc.accesswidener.AccessWidener;
import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.fabricmc.accesswidener.AccessWidenerReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.zeroturnaround.zip.ZipUtil;
import org.zeroturnaround.zip.transform.ByteArrayZipEntryTransformer;
import org.zeroturnaround.zip.transform.ZipEntryTransformer;
import org.zeroturnaround.zip.transform.ZipEntryTransformerEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.zip.ZipEntry;

public class ApplyAccessWidener {
  private final AccessWidener accessWidener = new AccessWidener();

  public ApplyAccessWidener(File accessWidener) {
    AccessWidenerReader accessWidenerReader = new AccessWidenerReader(this.accessWidener);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(accessWidener));
      accessWidenerReader.read(reader);
      reader.close();
    } catch (IOException var15) {
      throw new RuntimeException("Failed to read project access widener file");
    }
  }

  public void apply(File file) {
    ZipUtil.transformEntries(file, this.getTransformers(this.accessWidener.getTargets()));
  }

  private ZipEntryTransformerEntry[] getTransformers(Set<String> classes) {
    return classes.stream().map((string) -> new ZipEntryTransformerEntry(string.replaceAll("\\.", "/") + ".class", this.getTransformer())).toArray(ZipEntryTransformerEntry[]::new);
  }

  private ZipEntryTransformer getTransformer() {
    return new ByteArrayZipEntryTransformer() {
      protected byte[] transform(ZipEntry zipEntry, byte[] input) {
        ClassReader reader = new ClassReader(input);
        ClassWriter writer = new ClassWriter(0);
        ClassVisitor classVisitor = AccessWidenerClassVisitor.createClassVisitor(589824, writer, ApplyAccessWidener.this.accessWidener);
        reader.accept(classVisitor, 0);
        return writer.toByteArray();
      }
    };
  }
}
