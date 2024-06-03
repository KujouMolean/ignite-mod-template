plugins {
  id("mod.base-conventions")
  id("plugin.IgnitePlugin")
}

dependencies {
  remapper("net.fabricmc:tiny-remapper:0.10.1:fat")

  compileOnly(libs.ignite)
  compileOnly(libs.mixin)
  compileOnly(libs.mixinExtras)

  paperweight.paperDevBundle(libs.versions.paper)
}

tasks.named("compileJava") {
  dependsOn(tasks.named("applyAccessWidener"))
}
