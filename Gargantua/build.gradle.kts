plugins {
    id("java")
    id("application")
}

group = "com.deinname"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.4.1"

dependencies {
    // LWJGL Core
    implementation("org.lwjgl:lwjgl:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-opengl:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-glfw:${lwjglVersion}")
    implementation("org.lwjgl:lwjgl-stb:${lwjglVersion}")

    // Natives für Windows
    runtimeOnly("org.lwjgl:lwjgl:${lwjglVersion}:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl:${lwjglVersion}:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw:${lwjglVersion}:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-stb:${lwjglVersion}:natives-windows")
}

application {
    mainClass.set("Main")   // Wir erstellen später die Klasse Main.java
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec> {
    // Damit LWJGL die natives findet
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}