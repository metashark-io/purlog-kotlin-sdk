#  PurLog

<p align="center">
A remote logging SDK for Native Android and Kotlin Multi Platform (Android, iOS, iPadOS, macOS, watchOS, tvOS).
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-0.9.0-blue" alt="Version">
  <img src="https://dl.circleci.com/status-badge/img/circleci/QHEuwkxDTekYMK98ity4TZ/3HAoqtaHTWTsXrqjRFLDV4/tree/main.svg?style=shield" alt="CircleCI">
</p>

<p align="center">
<img width="569" alt="Screenshot 2024-11-04 at 11 36 16 PM" src="https://github.com/user-attachments/assets/cf13591e-6c0b-4205-b32d-5a6791cbe769">
</p>

##  Native Android Setup

### Installation

```groovy
dependencies {
    implementation("io.metashark:purlog:0.9.0")
}
```

### Permissions

```xml
 <uses-permission android:name="android.permission.INTERNET" />
```


### Native Android Example

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup PurLog
        val config = PurLogConfig.Builder()
            .setEnv(PurLogEnv.DEV)
            .setLevel(PurLogLevel.VERBOSE)
            .setProject(
                projectId = Secrets.projectId,
                projectJWT = Secrets.projectJWT // pass this in securely from your server. Do not hardcode it in your app
            )
            .setContext(applicationContext)
            .build()

        // Test Logs
        CoroutineScope(Dispatchers.Default).launch {
            PurLog.initialize(config)
            PurLog.verbose("Test Verbose", metadata =  mapOf("key1" to "value1"))
            PurLog.debug("Test DEBUG Log")
            PurLog.info("Test INFO Log")
            PurLog.warn("Test WARN Log")
            PurLog.error("Test ERROR Log")
            PurLog.fatal("Test FATAL Log")
        }

        // boilerplate UI
        enableEdgeToEdge()
        setContent {
            PurLogNativeAndroidSampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = "Hello World!",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
```

##  Kotlin MultiPlatform Setup

### Installation

```groovy
commonMain.dependencies {
    implementation("io.metashark:purlog:0.9.0")
}
```

### Permissions

```xml
 <uses-permission android:name="android.permission.INTERNET" />
```

### Kotlin MultiPlatform Example


```kotlin
@Composable
@Preview
fun App() {
    val context = getContext()
    MaterialTheme {
        val config = PurLogConfig.Builder()
            .setEnv(PurLogEnv.DEV)
            .setLevel(PurLogLevel.VERBOSE)
            .setProject(
                projectId = Secrets.projectId,
                projectJWT = Secrets.projectJWT // pass this in securely from your server. Do not hardcode it in your app
            )
            .setContext(context)
            .build()

        CoroutineScope(Dispatchers.Default).launch {
            PurLog.initialize(config)
            PurLog.verbose("Test Verbose", metadata =  mapOf("key1" to "value1"))
            PurLog.debug("Test DEBUG Log")
            PurLog.info("Test INFO Log")
            PurLog.warn("Test WARN Log")
            PurLog.error("Test ERROR Log")
            PurLog.fatal("Test FATAL Log")
        }
    }
}
```

Note: Android environments require passing in a `Context`. `setContext(context)` can be ommited if only iOS is targeted

#### commonMain

```kotlin
@Composable
expect fun getContext(): Any?
```

#### androidMain

```kotlin
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getContext(): Any? = LocalContext.current
```

#### iosMain

```kotlin
@Composable
actual fun getContext(): Any? = null
```
