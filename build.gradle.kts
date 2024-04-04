plugins { // needed for the subprojects section to work
    id("java")
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.5.12" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

ext {
    set("projectName", rootProject.name)
    set("lombokDep", "org.projectlombok:lombok:1.18.30")

    // reduced is just a re-zipped version of the original, without some conflicting libraries (gson)
    set("lowestSpigotDep", "net.techcable.tacospigot:server:1.8.8-R0.2-REDUCED")   // luxious nexus (public)
    set("latestSpigotDep", "org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")          // spigotmc nexus
}

allprojects {
    group = "com.kamikazejam"
    version = "3.0.0.0-SNAPSHOT"
    description = "KamikazeJAM's common library for Spigot and Standalone projects."

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://nexus.luxiouslabs.net/public")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://jitpack.io")
        gradlePluginPortal()
    }

    // We want UTF-8 for everything
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    dependencies {
        // junit
        testImplementation(platform("org.junit:junit-bom:5.10.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")

        // IntelliJ annotations
        api("org.jetbrains:annotations:24.1.0")
    }
}

// Disable root project build
tasks.jar.get().enabled = false

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}