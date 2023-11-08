<div align="center">

<a href="">[![Maven Central](https://img.shields.io/maven-central/v/com.ivanempire/lighthouse?color=informational)](https://search.maven.org/artifact/com.ivanempire/lighthouse)</a>
<a href="">![Build Status](https://github.com/ivanempire/lighthouse/actions/workflows/continuous-integration.yml/badge.svg)</a>
<a href="">![Issues](https://img.shields.io/github/issues/ivanempire/lighthouse)</a>
<a href="">![License](https://img.shields.io/github/license/ivanempire/lighthouse)</a>

</div>
<div align="center">
<picture>
    <source media="(prefers-color-scheme: dark)" srcset="banner-dark.png" width="800px">
    <source media="(prefers-color-scheme: light)" srcset="banner-light.png" width="800px">
    <img src="banner-light.png" alt="Lighthouse banner" width="800px" />
</picture>
</div>

Lighthouse is an open-source Android library which facilitates the discovery of SSDP devices
connected to your network. It does this by supporting the sending of unicast and multicast search
messages, as well as listening to all packets that may be sent to the multicast group. Lighthouse
is built in accordance with the [UPnP Device Architecture 2.0](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf) specification.

Features at a glance:
 
- **State management**: Lighthouse tracks the state of all devices found on your network. The moment a device (or any of its embedded components) changes, the list gets updated and emitted to the consumer.
- **Search models**: Lighthouse exposes models for sending unicast and multicast M-SEARCH messages to the multicast group. No more tedious string concatenations: it does that for you!
- **Modern stack**: Lighthouse is written using Kotlin and leverages Kotlin Coroutines.

## Download
Lighthouse is published to `mavenCentral()`, so you can add it to your Android project like so:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.ivanempire:lighthouse:2.1.0")
}
```

Alternatively, if you're not about that life, feel free to either clone this repository directly or
download the source code from the [releases](https://github.com/ivanempire/lighthouse/releases) page.

## Getting started
To get started with Lighthouse, simply build an instance of `LighthouseClient` and call the `discoverDevices()` suspending function:

```kotlin
val lighthouseClient = LighthouseClient
    .Builder(context)
    .setLogger(object : LighthouseLogger() {...}) // Optional: Setup a custom logging system
    .setRetryCount(3) // Optional: Retry sending packets 3 times (4 packets will be sent in total)
    .build()

suspend fun startDiscovery() {
    lighthouseClient.discoverDevices()
        .collect { deviceList: List<AbridgedMediaDevice> ->
            Log.d("MyTag", "Got an updated device list: $deviceList")
        }
}
```

## Searching for devices
There are two types of search messages (also known as M-SEARCH) one may send to a multicast group:
unicast and multicast. A unicast message is one that is sent to a specific device on the network
since the search criteria is rather narrow. A multicast message, on the other hand, is like an
all-points bulletin: every single device that matches the more-generic search criteria will respond.

For example, the default search request, found [here](lighthouse/src/main/java/com/ivanempire/lighthouse/models/Constants.kt#L33),
is a multicast message with a search target of `ssdp:all`. Lighthouse sets this up as an implicit argument to `discoverDevices()`. The corresponding string is:

```
M-SEARCH * HTTP/1.1
HOST:239.255.255.250:1900
MAN:"ssdp:discover"
MX:1
ST:ssdp:all
CPFN.UPNP.ORG:LighthouseClient
CPUUID.UPNP.ORG:747f550a-8dec-33a1-8470-e314bf440695
```

If you are feeling adventurous, however, you can create your own search requests and pass them into
the discover method:

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

Checkout the documentation for [`MulticastSearchRequest.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/search/MulticastSearchRequest.kt)
and [`UnicastSearchRequest.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/search/UnicastSearchRequest.kt)
to see which parameters are required and what they mean. Some basic network-specific constants are
exposed in [`Constants.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/Constants.kt).

## Debugging
### SSDP
Unfortunately, SSDP is not an enforced protocol. As a result, every single manufacturer may add or
drop headers from the network packets, save for the official HTTP start line. To simplify things
somewhat, Lighthouse follows the [UPnP Device Architecture 2.0](https://openconnectivity.org/upnp-specs/UPnP-arch-DeviceArchitecture-v2.0-20200417.pdf)
specification as closely as it can. However, if you are seeing some missing information, or if the
device list is not behaving as expected, here are some debugging notes to consider:

- **Defaults**: [`Constants.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/Constants.kt) contains all of the default values that Lighthouse will use if a device is not advertising a specific field in an SSDP packet. For example, if an `ssdp:alive` packet does not contain a `bootId`, the parser will set a default value of `-1`. Similarly, `cache-control` defaults to 1800 seconds (30 minutes), and missing `location` URLs default to `0.0.0.0`. [`MediaDeviceServer.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/devices/MediaDeviceServer.kt) gets a special shoutout due the finicky format required by UPnP. All fields default to `N/A`, and the error in parsing will be logged as a warning.
- **UUIDs**: Lighthouse groups devices and their embedded components (embedded devices and services) by the advertised UUID. However, some devices advertise each embedded component with a different UUID - usually off by 1 or 2. This is not something that's easy to fix, however, file a bug if you're seeing this and maybe we can discuss a solution! On the other hand, if a device is not advertising a UUID (or is advertising an invalid one), a zeroed-out value will be assigned to the packet. Said packets will be parsed as usual, but will show up under the zeroed-out UUID in the final device list.
- **Special headers**: If you're looking for manufacturer-specific data, then checkout [`AbridgedMediaDevice.extraHeaders`](lighthouse/src/main/java/com/ivanempire/lighthouse/models/devices/MediaDevice.kt#L38) - they will be located there. Similarly, official headers parsed outside of their proper packets will also be found there. For example, an `ssdp:update` packet does not typically contain a `cache-control` field. However, if your devices add it to said packet, the parser will simply put it into `extraHeaders`.

### Custom logging
By default, Lighthouse does not log anything. This makes it just a little bit difficult to figure
out what's going on during the device discovery process. However, the `setLogger()` method allows
one to specify their own logging system that will be used by Lighthouse. The demo module implements
a custom logger [here](demo/src/main/java/com/ivanempire/lighthouse/demo/MainActivity.kt#L56) as an example, and the
source abstract class is [`LighthouseLogger.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/LighthouseLogger.kt).
To re-iterate the comments in the code, the provided methods are:

- `logStateMessage()` - logs main state updates (the resulting device list), useful for inspecting why a certain device is or isn't showing up.
- `logStatusMessage()` - logs general status updates, useful for seeing general setup/teardown information.
- `logPacketMessage()` - most verbose method, probably shouldn't be used in production - logs and all incoming packets + the results of them getting parsed.
- `logErrorMessage()` - logs any and all error messages, if you're seeing issues starting discovery/packets not coming in - implement this.

### Exception handling
Kotlin Coroutines are, for the most part, absolutely fantastic for asynchronous programming.
However, one area of increased complexity is exception handling. It's worth pointing out that
[`RealSocketListener.kt`](lighthouse/src/main/java/com/ivanempire/lighthouse/socket/RealSocketListener.kt)
may throw exceptions: IOException or SocketException, depending on what goes wrong when setting up the
MulticastSocket. As a result, it's a good idea to wrap the Lighthouse discovery call in either a
[runCatching](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/run-catching.html) lambda, or
use a [SupervisorJob](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-supervisor-job.html).
For example:

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

An easy litmus test is to call this method while your device is in airplane mode - if the exception
is not propagated to the very top, then you're all set!

### R8 / ProGuard
Lighthouse is fully compatible with the standard shrinking tools and does not require any additional
rules. The only two dependencies are Kotlin Coroutines and AndroidX Core.

## Upcoming work
In no particular order, here's a list of incoming features and bug fixes:

- **Better diffing**: Lighthouse could be smarter in the way it updates the device list. Currently, each instance of an `AbridgedMediaDevice` is replaced in the list. A patch for this may come from the consumer side - implement a custom `DiffUtil` class. However, it would be preferable for Lighthouse to handle this logic via partial updates, or a built-in differ.
- **Unicast bug**: Discovered rather late in the game, but Lighthouse will show you all devices on the network, even if you're only looking for a specific one. Look, we're just THAT good. The packet parsing mechanism should take into account the search request and filter out anything that isn't relevant to the sent out M-SEARCH request. 
- **Multihome support**: An SSDP-capable device is able to advertise over all connected sockets. Similarly, it is possible for Lighthouse to be set up in such a way that it listens for multicast messages across all sockets on the device it's running on.

## License

    Copyright 2023 Lighthouse Contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Credits
Thank you to [Tyler Nickerson](https://dribbble.com/TylerNickerson) and [Yulia Biziaeva](https://www.behance.net/busyjey) for their combined efforts on the Lighthouse logo.
