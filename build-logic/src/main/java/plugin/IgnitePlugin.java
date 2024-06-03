package plugin;

import io.papermc.paperweight.tasks.RemapJar;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class IgnitePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("remapAccessWidener", RemapAccessWidenerTask.class).setGroup("ignite");
        project.getTasks().create("applyAccessWidener", ApplyAccessWidenerTask.class).setGroup("ignite");
        RemapJar jarTask = (RemapJar) project.getTasks().getByName("reobfJar");
        jarTask.finalizedBy("remapAccessWidener");
    }
}
