plugins {
    kotlin("jvm") version "1.9.24"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.spoiligaming"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.8.1")

    testImplementation(kotlin("test"))
}

javafx {
    version = "11"
    modules("javafx.controls", "javafx.graphics", "javafx.base")
}

tasks.test {
    useJUnitPlatform()
}

val mainFunctionLocation = "com.spoiligaming.loader.MainKt"

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("LoadingSimulator-$version.jar")
    manifest {
        attributes["Main-Class"] = mainFunctionLocation
    }
}

application {
    mainClass.set(mainFunctionLocation)
}

kotlin {
    jvmToolchain(11)
}