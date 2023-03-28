# Changelog

## [1.2.1] - March 27th 2023
- Fix: Adding newline to end of M-SEARCH message so that some devices do not ignore the search requests
- Fix: Properly updating discovered devices list when parsing ALIVE and UPDATE SSDP packets

## [1.1.1] - November 3rd 2022
- **New**: Optimized device list emissions to not emit every second due to periodic stale device check. Updates will now be sent only if a device comes online, gets updated, or goes offline.

## [1.0.1] - September 20th 2022
- **New**: Added [setRetryCount](lighthouse/src/main/java/com/ivanempire/lighthouse/LighthouseClient.kt#L40) method to allow for retry sending of M-SEARCH packets.
- Updated Kotlin to version 1.7.10.
- Updated AGP to version 7.2.2.

## [1.0.0] - August 12th 2022
- Formalized first release.

## [0.9.9] - August 11th 2022
- Initial release.
