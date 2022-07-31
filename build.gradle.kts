import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"
    `java-library`
    `maven-publish`
}

val githubUser: String by project
val githubPassword: String by project

repositories {
    jcenter()
    mavenCentral()
}

version = properties["version"] ?: "local"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.bazaarvoice.jackson:rison:2.9.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.slf4j:slf4j-api:1.7.30")
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
