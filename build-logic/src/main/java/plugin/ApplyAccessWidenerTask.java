package plugin;

import groovy.json.JsonSlurper;
import org.apache.groovy.json.internal.LazyMap;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ApplyAccessWidenerTask extends DefaultTask {
  @TaskAction
  public void remapAccessWidenerTask() throws IOException {
    File asFile = getProject().getLayout().getProjectDirectory().file("src/main/resources/ignite.mod.json").getAsFile();
    Object obj = new JsonSlurper().parse(Files.newInputStream(Paths.get(asFile.getPath())));
    if (!(obj instanceof LazyMap)) {
      throw new RuntimeException();
    }
    LazyMap jsonObject = (LazyMap) obj;
    if (!jsonObject.containsKey("wideners") || jsonObject.get("wideners") == null || ((List<?>) jsonObject.get("wideners")).isEmpty()) {
      throw new RuntimeException("no wideners.");
    }
    String awPath = ((List<String>) jsonObject.get("wideners")).get(0);
    ApplyAccessWidener applyAccessWidener = new ApplyAccessWidener(new File(asFile.getParent(), awPath));

    getProject().getDependencies();
    for (Configuration configuration : getProject().getConfigurations()) {
      if (!configuration.getName().equals("compileClasspath")) {
        continue;
      }
      try {
        for (ResolvedArtifact resolvedArtifact : configuration.getResolvedConfiguration().getResolvedArtifacts()) {
          if (resolvedArtifact.getName().endsWith("-server")) {
            applyAccessWidener.apply(resolvedArtifact.getFile());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
