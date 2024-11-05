import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish") version "0.29.0"
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
}

mavenPublishing {
    group = "io.metashark"
    version = "0.9.0"

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "purlog", version.toString())

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
