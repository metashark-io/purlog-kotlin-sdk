import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`

plugins {
    `maven-publish`
    signing
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        groupId = "io.metashark"
        artifactId = "purlog"
        version = "0.9.0"
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })

        // Provide artifacts information required by Maven Central
        pom {
            name.set("PurLog")
            description.set("A remote logging SDK for Native Android and Kotlin Multi Platform (Android, iOS, iPadOS, macOS, watchOS, tvOS).")
            url.set("https://github.com/metashark-io/purlog-kotlin-sdk")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("MetaShark")
                    name.set("MetaShark")
                    organization.set("MetaShark")
                    organizationUrl.set("https://metashark.io")
                }
            }
            scm {
                url.set("https://github.com/metashark-io/purlog-kotlin-sdk")
                connection.set("scm:git:git://github.com/metashark-io/purlog-kotlin-sdk.git")
                developerConnection.set("scm:git:ssh://git@github.com/metashark-io/purlog-kotlin-sdk.git")
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}