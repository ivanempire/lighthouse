<div align="center">

<a href="">[![Maven Central](https://img.shields.io/maven-central/v/com.ivanempire/lighthouse)](https://search.maven.org/artifact/com.ivanempire/lighthouse)</a>
<a href="">![Build Status](https://github.com/ivanempire/lighthouse/actions/workflows/continuous-integration.yml/badge.svg)</a>
<a href="">![Issues](https://img.shields.io/github/issues/ivanempire/lighthouse)</a>
<a href="">![License](https://img.shields.io/github/license/ivanempire/lighthouse)</a>

</div>

![Lighthouse banner](banner.png)

Lighthouse is an open-source Android library that allows users to discover SSDP devices. It does this by listening to messages broadcast to the multicast group, as well as allowing users to send unicast and multicast M-SEARCH packets.

## Download
Lighthouse is published to `mavenCentral()`, so you can add it to your Android project like so:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ivanempire:lighthouse:1.0.0")
}
```

Alternatively, you can manually download the AAR artifact from the [releases](https://github.com/ivanempire/lighthouse/releases) page.

## Getting started
To get started with Lighthouse, simply build an instance of `LighthouseClient` and call the `discoverDevices()` suspending function:

```kotlin
val lighthouseClient = LighthouseClient.Builder(context).build()

suspend fun startDiscovery() {
    lighthouseClient.discoverDevices()
        .collect { deviceList: List<AbridgedMediaDevice> ->
            Log.d("MyTag", "Got an updated device list: $deviceList")
        }
}
```

### Data models
The main data class to work with is `AbridgedMediaDevice`, although others are present in the library - see the section about future work. The data class contains the following fields, with the specified defaults (if a particular device is not advertising said field):



### Search requests
By default, `discoverDevices()` will send a multicast messages with a search target of `ssdp:all`, see full construction [here](lighthouse/src/main/java/com/ivanempire/lighthouse/models/Constants.kt#L20). However, you can build your own search messages and pass them into the function as shown below:

```kotlin

```

## Notes on SSDP

## R8 / ProGuard
Lighthosue is fully compatible with the standard shrinking tools and does not require any additional rules. The only two dependencies are Kotlin Coroutines and AndroidX Core.

## Architecture

## License

    Copyright 2022 Lighthouse Contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
