import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    `java-library`
    `maven-publish`
}

val githubUser: String by project
val githubPassword: String by project

repositories {
    mavenCentral()
}

version = properties["version"] ?: "local"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.bazaarvoice.jackson:rison:2.9.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    implementation("org.slf4j:slf4j-api:2.0.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions{
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
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
                description.set("Library for posting messages to Slack")
                url.set("https://github.com/navikt/simple-slack-poster")
                groupId = "no.nav.slackposter"
                artifactId = "simple-slack-poster"
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
