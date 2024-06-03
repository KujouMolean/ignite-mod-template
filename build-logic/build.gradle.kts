plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
}

dependencies {
  implementation(libs.build.paperweight)
  implementation(libs.build.shadow)
  implementation(libs.build.spotless)
  implementation(libs.build.accesswidener)
  implementation(libs.build.tiny.remapper)
  implementation(libs.build.zt.zip)
}

dependencies {
  compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
  target {
    compilations.configureEach {
      kotlinOptions {
        jvmTarget = "11"
      }
    }
  }
}
gradlePlugin {
  plugins {
    create("IgnitePlugin") {
      id = "plugin.IgnitePlugin"
      implementationClass = "plugin.IgnitePlugin"
    }
  }
}
