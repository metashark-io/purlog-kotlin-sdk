# PurLog

<p align="center">
A remote logging SDK for Kotlin Multi Platform (Android, JVM, iOS, iPadOS, macOS, watchOS, tvOS).
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-0.9.0-blue" alt="Version">
  <img src="https://dl.circleci.com/status-badge/img/circleci/QHEuwkxDTekYMK98ity4TZ/3HAoqtaHTWTsXrqjRFLDV4/tree/main.svg?style=shield" alt="CircleCI">
</p>


## Installation

```groovy
commonMain.dependencies {
    implementation("io.metashark:purlog:0.9.0")
}
```

## Example

```kotlin
@Composable
@Preview
fun App() {
    MaterialTheme {
        val config = PurLogConfig.Builder()
            .setProject(
                projectId = "PROJECT_ID",
                projectJWT = "PROECT_JWT" // DO NOT HARCODE; should be passed in securely from server
            )
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            PurLog.initialize(config)
            PurLog.verbose("Test Verbose")
        }
    }
}
```

