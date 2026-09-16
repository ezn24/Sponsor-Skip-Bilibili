# Sponsor Skip for Bilibili — Project Context

Sponsor Skip for Bilibili is a native Android application written in Kotlin. It monitors media sessions from the official mainland and international Bilibili applications, resolves the current video to a BVID/CID, loads community-submitted segments from BilibiliSponsorBlock and seeks to the end of enabled segment categories.

## Runtime flow

1. `MediaNotificationService` considers media sessions from `tv.danmaku.bili` and `com.bilibili.app.in` only.
2. `BilibiliResolver` uses media metadata and Bilibili public APIs to resolve the BVID and, when required, CID.
3. The service requests segments from `https://www.bsbsb.top/api/skipSegments`.
4. Enabled and valid segments are filtered, merged and tracked against the active playback position.
5. `MediaController.TransportControls.seekTo()` skips a matching segment. Toasts and local statistics report loading and skip events.
6. Optional skip-count tracking posts the segment UUID to the BilibiliSponsorBlock viewed-segment endpoint.

## Supported applications

- Mainland Bilibili: `tv.danmaku.bili`
- International Bilibili: `com.bilibili.app.in`

Custom applications, YouTube scraping, SponsorBlock's YouTube API and Spotify/Spot SponsorBlock are intentionally not part of this variant.

## Important files

- `MediaNotificationService.kt`: session selection, segment loading and playback tracking.
- `BilibiliResolver.kt`: BVID/CID extraction and resolution.
- `SettingsManager.kt`: persisted settings and statistics.
- `MainActivity.kt`: primary UI, category settings and project links.
- `UpdateManager.kt`: GitHub release checking and installation.

The canonical project and release repository is `https://github.com/ezn24/Sponsor-Skip-Bilibili`.
