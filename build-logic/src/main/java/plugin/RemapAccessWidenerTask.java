package plugin;

import io.papermc.paperweight.tasks.RemapJar;
import net.fabricmc.accesswidener.AccessWidenerReader;
import net.fabricmc.accesswidener.AccessWidenerRemapper;
import net.fabricmc.accesswidener.AccessWidenerWriter;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.TaskAction;
import plugin.fabric.AccessWidenerFile;
import plugin.fabric.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class RemapAccessWidenerTask extends DefaultTask {
  @TaskAction
  public void remapAccessWidenerTask() throws IOException {
    Project project = getProject();
    File inputJar = ((RemapJar) getProject().getTasks().getByName("reobfJar")).getOutputJar().get().getAsFile();
    Path[] paths = ((RemapJar) getProject().getTasks().getByName("reobfJar")).getRemapClasspath().getFiles().stream().map(File::toPath).toArray(Path[]::new);
    AccessWidenerFile accessWidenerFile = AccessWidenerFile.fromModJar(inputJar.toPath());
    if (accessWidenerFile == null) {
      throw new RuntimeException("Please run reobfJar Task!");
    }
    byte[] input = accessWidenerFile.content();
    String from = "mojang+yarn";
    String to = "spigot";
    RemapJar remapJar = (RemapJar) project.getTasks().findByName("reobfJar");
    assert remapJar != null;
    RegularFileProperty mappingsFile = remapJar.getMappingsFile();
    TinyRemapper tinyRemapper = TinyRemapper.newRemapper().withMappings(TinyUtils.createTinyMappingProvider(mappingsFile.get().getAsFile().toPath(), from, to)).build();
    tinyRemapper.readInputs(inputJar.toPath());
    tinyRemapper.readClassPath(paths);
    int version = AccessWidenerReader.readVersion(input);
    AccessWidenerWriter writer = new AccessWidenerWriter(version);
    AccessWidenerRemapper remapper = new AccessWidenerRemapper(writer, tinyRemapper.getEnvironment().getRemapper(), from, to);
    AccessWidenerReader reader = new AccessWidenerReader(remapper);
    reader.read(input);
    byte[] output = writer.write();
    ZipUtils.replace(inputJar.toPath(), accessWidenerFile.path(), output);
  }
}
