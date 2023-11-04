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

group = "ru.oklookat"
version = "1.0.0"

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
    publications.create<MavenPublication>("images4kt") {
        groupId = "ru.oklookat.images4kt"
        artifactId = "images4kt"
        version = "1.0.0"
        pom.packaging = "jar"
        artifact("$buildDir/libs/${artifactId}-$version.jar")
        artifact("$buildDir/libs/${artifactId}-$version-sources.jar")
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


