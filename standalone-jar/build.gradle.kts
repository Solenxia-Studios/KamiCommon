plugins {
    // Unique plugins for this module
}

dependencies {
    api(project(":generic-jar")); implementation(project(":generic-jar"))
    api(project(":standalone-utils")); implementation(project(":standalone-utils"))

    // org.json (standalone-utils) and google gson needed for for jedis (in :generic-jar) to work properly
    api("com.google.code.gson:gson:2.11.0"); implementation("com.google.code.gson:gson:2.11.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven {
            credentials {
                username = System.getenv("LUXIOUS_NEXUS_USER")
                password = System.getenv("LUXIOUS_NEXUS_PASS")
            }
            // Select URL based on version (if it's a snapshot or not)
            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.luxiouslabs.net/repository/maven-snapshots/")
            }else {
                uri("https://repo.luxiouslabs.net/repository/maven-releases/")
            }
        }
    }
}

tasks {
    shadowJar {
        dependsOn(project(":generic-jar").tasks.shadowJar) // Gradle complained...
        dependsOn(project(":generic-utils").tasks.shadowJar) // Gradle complained...
    }
}