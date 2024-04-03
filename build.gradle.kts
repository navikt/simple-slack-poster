import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    `java-library`
    `maven-publish`
}

group = "no.nav.slackposter"
version = properties["version"] ?: "local-build"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.bazaarvoice.jackson:rison:2.9.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("org.slf4j:slf4j-api:2.0.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions{
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/simple-slack-poster")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("simple-slack-poster")
                description.set("Simple Slack poster")
                url.set("https://github.com/navikt/simple-slack-poster")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/navikt/simple-slack-poster.git")
                    developerConnection.set("scm:git:https://github.com/navikt/simple-slack-poster.git")
                    url.set("https://github.com/navikt/simple-slack-poster")
                }
            }
            from(components["java"])
        }
    }
}