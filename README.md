<div align="center">

<a href="">[![Maven Central](https://img.shields.io/maven-central/v/com.ivanempire/lighthouse)](https://search.maven.org/artifact/com.ivanempire/lighthouse)</a>
<a href="">![Build Status](https://github.com/ivanempire/lighthouse/actions/workflows/continuous-integration.yml/badge.svg)</a>
<a href="">![Issues](https://img.shields.io/github/issues/ivanempire/lighthouse)</a>
<a href="">![License](https://img.shields.io/github/license/ivanempire/lighthouse)</a>

</div>

![Lighthouse banner](banner.png)

Lighthouse is an open-source Android library which facilitates the discovery of SSDP devices found on your network. It does this by supporting the sending of unicast and multicast search messages, as well as listening to all packets that may be transmitted to the multicast group. Lighthouse is built in accordance with the [UPnP Device Architecture 2.0](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf).

Features at a glance:

- **State management**: Lighthouse tracks the state of all devices found on your network. The moment a device (or any of its embedded components) changes, the list gets updated and emitted to the consumer.
- **Search models**: Lighthouse exposes models for sending unicast and multicast M-SEARCH messages to the multicast group. No more tedious string concatenations: it does that for you!
- **Modern stack**: Lighthouse is written using Kotlin and leverages Kotlin Coroutines. It is also grass-fed. 

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

Alternatively, if you're not about that life, feel free to either clone this repository directly or download the source code from the [releases](https://github.com/ivanempire/lighthouse/releases) page.

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

## Searching for devices
There are two types of search messages (also known as M-SEARCH) one may send to a multicast group: unicast and multicast. A unicast message. On the other hand, a multicast message is like an all-points bulletin: every single device that matches the search criteria will respond. These are known as search requests in Lighthouse.

For example, the default search request, found [here](lighthouse/src/main/java/com/ivanempire/lighthouse/models/Constants.kt#L33), is a multicast message with a search target of `ssdp:all`. Lighthouse sets this up as an implicit argument to `discoverDevices()`. The corresponding string is:

```
M-SEARCH * HTTP/1.1
HOST:239.255.255.250:1900
MAN:"ssdp:discover"
MX:1
ST:ssdp:all
CPFN.UPNP.ORG:LighthouseClient
CPUUID.UPNP.ORG:747f550a-8dec-33a1-8470-e314bf440695
```

If you are feeling adventurous, however, you can create your own search requests and pass them into the dicover method:

```kotlin
val multicastSearchRequest = MulticastSearchRequest(
    hostname = Constants.DEFAULT_MEDIA_HOST,
    mx = 1,
    searchTarget = "mySearchTarget"
)
lighthouseClient.discoverDevices(multicastSearchRequest).collect { ... }

// Alternatively, here's unicast
val unicastSearchRequest = UnicastSearchRequest(
    hostname = DEFAULT_MEDIA_HOST,
    searchTarget = "ssdp:all",
)
lighthouseClient.discoverDevices(unicastSearchRequest).collect { ... }
```

Checkout the documentation for [`MulticastSearchRequest.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/search/MulticastSearchRequest.kt) and [`UnicastSearchRequest.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/search/UnicastSearchRequest.kt) to see which parameters are required and what they mean. Some basic network-specific constants are exposed in [`Constants.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/Constants.kt).

## Debugging
### Exception handling
Kotlin Coroutines are, for the most part, absolutely fantastic for asynchronous programming. However, one area of increased complexity is exception handling. It's worth pointing out that [`RealSocketListener.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/socket/RealSocketListener.kt) may throw exceptions: IOException or SocketException, depending on what goes wrong when setting up the MulticastSocket. As a result, it's a good idea to wrap the Lighthouse call in either a [runCatching](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/run-catching.html) lambda, or use a [SupervisorJob](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-supervisor-job.html). For example:

```kotlin
class MyViewModel: ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val myScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    
    myScope.launch {
        lighthouseClient.discoverDevices()
            .collect { backingDeviceList.value = it }
    }
}
```
An easy litmus test is to call this method while your device is in airplane mode - if the exception is not propagated to the very top, then you're all set!

### R8 / ProGuard
Lighthosue is fully compatible with the standard shrinking tools and does not require any additional rules. The only two dependencies are Kotlin Coroutines and AndroidX Core.

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
