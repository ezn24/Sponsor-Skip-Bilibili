# Privacy Policy

Last updated: 16 September 2026

Sponsor Skip for Bilibili processes media information locally so it can identify the currently playing Bilibili video and skip community-submitted segments. The app does not require an account and does not operate an analytics or advertising service.

## Information the app accesses

When notification-listener access is enabled, the app reads active media-session metadata from these packages only:

- `tv.danmaku.bili`
- `com.bilibili.app.in`

The metadata may include the video title, media ID, playback duration, playback position and playback state. It is used in memory to resolve the video's BVID/CID and control playback. Debug logs are stored locally only when the user enables logging.

## Network requests

The app may connect directly to:

- Bilibili public APIs to resolve video metadata such as BVID and CID.
- `https://www.bsbsb.top`, the BilibiliSponsorBlock service, to download matching skip segments and, when skip-count tracking is enabled, report that a segment was viewed/skipped.
- GitHub's API and release pages under `ezn24/Sponsor-Skip-Bilibili` to check for and download app updates when update checking is enabled.

These services receive the network information normally exposed by an HTTPS request, including the device's IP address. Segment requests may include a BVID/CID. Skip-count reporting sends the segment UUID. The app does not send Bilibili login credentials, cookies, account identifiers or notification contents to the developer.

## Storage and sharing

Settings, statistics and optional debug logs are stored locally on the device. A settings backup is created only when the user explicitly exports one. The developer does not receive or sell this local data.

The app does not use third-party advertising or analytics SDKs. Data sent to Bilibili, BilibiliSponsorBlock or GitHub is handled under those services' respective policies.

## Permissions

- Notification access: discover Bilibili media sessions and their playback metadata.
- Internet: resolve video IDs, obtain skip segments and check for updates.
- Notifications and foreground service: optionally keep media monitoring active.
- Install packages: install an update only after the user chooses to do so.
- Battery-optimization exemption: optional reliability improvement requested by the user.

## Independence

Sponsor Skip for Bilibili is an independent open-source project. It is not affiliated with, endorsed by or sponsored by Bilibili, BilibiliSponsorBlock, SponsorBlock or their maintainers.

## Contact

Questions and privacy requests can be submitted through the [project issue tracker](https://github.com/ezn24/Sponsor-Skip-Bilibili/issues).
