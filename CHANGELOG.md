# Changelog

## [2.1.0] - November 7th 2023
- **New**: Added support for consumers to implement their own logging mechanism via [LighthouseLogger](lighthouse/src/main/java/com/ivanempire/lighthouse/LighthouseLogger.kt)
- Bumped AGP version to 8.1.2
- Bumped Kotlin version to 1.8.10
- Bumped AndroidX version to 1.12.0
- Bumped Maven publish plugin to 0.25.2
- Bumped Kotlin Coroutines version to 1.7.3

## [2.0.0] - May 10th 2023
- **Breaking**: `AbridgedMediaDevice.uuid` field type has changed from `UUID` to string in order to support UPnP1.0 advertisements
- **New**: Default `NOT_AVAILABLE_LOCATION` value changed from `URL("http://0.0.0.0/")` to `URL("http://0.0.0.0/")`
- **New**: `DatagramPacketTransformer` no longer filters out packets with an empty `LOCATION` header
- **New**: App module added for a bare-bones usage demonstration
- `bootId` has been decoupled from the USN field
- Gradle build files have been rewritten in Kotlin

## [1.2.1] - March 27th 2023
- Fix: Adding newline to end of M-SEARCH message so that some devices do not ignore the search requests
- Fix: Properly updating discovered devices list when parsing ALIVE and UPDATE SSDP packets

## [1.1.1] - November 3rd 2022
- **New**: Optimized device list emissions to not emit every second due to periodic stale device check. Updates will now be sent only if a device comes online, gets updated, or goes offline.

## [1.0.1] - September 20th 2022
- **New**: Added [setRetryCount](lighthouse/src/main/java/com/ivanempire/lighthouse/LighthouseClient.kt#L35) method to allow for retry sending of M-SEARCH packets.
- Updated Kotlin to version 1.7.10.
- Updated AGP to version 7.2.2.

## [1.0.0] - August 12th 2022
- Formalized first release.

## [0.9.9] - August 11th 2022
- Initial release.
