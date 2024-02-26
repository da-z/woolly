plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ing.llamaz"
version = "0.5.7"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.sashirestela:simple-openai:2.0.0") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
    }
    implementation("com.fasterxml.jackson.core:jackson-core:2.14.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.3")

    testImplementation("org.hamcrest:hamcrest-all:1.3")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
    }

    shadowJar {
        mergeServiceFiles()
        manifest {
            attributes(
                    "Implementation-Title" to "Woolly",
                    "Implementation-Version" to version,
            )
        }
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
