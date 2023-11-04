import java.net.URI

plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

var sGroupId = "ru.oklookat"
val sArtifactId = "images4kt"
val sVersion = "1.0.0"

group = sGroupId
version = sVersion

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

java {
    withSourcesJar()
}

tasks.jar {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name,
            "Implementation-Version" to project.version))
    }
}

configure<PublishingExtension> {
    publications.create<MavenPublication>(sArtifactId) {
        groupId = sGroupId
        artifactId = sArtifactId
        version = sVersion
        pom.packaging = "jar"
        artifact("$buildDir/libs/${artifactId}-$version.jar") { classifier = "jar" }
        artifact("$buildDir/libs/${artifactId}-$version-sources.jar") { classifier = "sources" }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/oklookat/images4kt")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}


