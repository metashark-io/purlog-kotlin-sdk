# PurLog

<p align="center">
A remote logging SDK for Kotlin Multi Platform (Android, JVM, iOS, iPadOS, macOS, watchOS, tvOS).
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-0.9.0-blue" alt="Version">
  <img src="https://dl.circleci.com/status-badge/img/circleci/QHEuwkxDTekYMK98ity4TZ/3HAoqtaHTWTsXrqjRFLDV4/tree/main.svg?style=shield" alt="CircleCI">
</p>


<p align="center">
<img width="500" alt="Screenshot 2024-09-21 at 6 20 45 AM" src="https://github.com/user-attachments/assets/dd0728a4-7331-4bcd-860f-434250b2ce3c">
</p>



## Installation

### CocoaPods

```ruby
pod 'PurLog', '~> 0.1.1'
```


### Swift Package Manager

In Xcode, go to File > Add Packages.

```
https://github.com/metashark-io/purlog-swift-sdk.git
```

## Usage


```swift
import PurLog
```


### Initialization


```swift
let config = PurLogConfig.Builder()
            .setLevel(.DEBUG) // (optional) defaults to .VERBOSE 
            .setEnv(.PROD) // (optional) defaults to .DEV
            .setProject(projectId: projectId, projectJWT: jwt) // (optional) configures remote logging so you can view logs on the PurLog web app
            .build()

Task {
    let initializationResult = await PurLog.shared.initialize(config: config)
}
```

Note: to use `.setProject()`, you must configure a [PurLog](https://app.purlog.io) project


### Logging


After a successfull initialization, you'll be able to invoke logs

```swift
PurLog.shared.log("Test DEBUG Log", level: .DEBUG)
```

You can also include `metadata` if needed:

```swift
PurLog.shared.log("Test DEBUG Log", metadata: ["key": "value"] level: .DEBUG)
```


### Example


```swift
import PurLog
import SwiftUI

@main
struct PurLogSampleApp: App {
    
    init() {
        let projectId = "YOUR_PURLOG_PROJECT_ID"
        let jwt = Secrets.projectJWT // The project JWT shouldn't be hardcoded in your project. It should typically be securely passed down from your server environment
        
        let config = PurLogConfig.Builder()
            .setProject(projectId: projectId, projectJWT: jwt)
            .build()
        
        Task {
            _ = await PurLog.shared.initialize(config: config)
            PurLog.shared.log("Test VERBOSE Log", metadata: ["key": "value"], level: .VERBOSE)
            PurLog.shared.log("Test DEBUG Log", level: .DEBUG)
            PurLog.shared.log("Test INFO Log", level: .INFO)
            PurLog.shared.log("Test WARN Log", level: .WARN)
            PurLog.shared.log("Test ERROR Log", level: .ERROR)
            PurLog.shared.log("Test FATAL Log", level: .FATAL)
        }
    }
    
    var body: some Scene {
        WindowGroup {}
    }
}
```
