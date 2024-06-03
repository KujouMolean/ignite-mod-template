rootProject.name = "ignite-template-build-logic"

dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.fabricmc.net/")
  }

  versionCatalogs {
    register("libs") {
      from(files("../gradle/libs.versions.toml")) // include from parent project
    }
  }
}
