# Repository guidance

- This repository is the Bilibili-only variant of Sponsor Skip.
- Supported media packages are `tv.danmaku.bili` and `com.bilibili.app.in`.
- Do not reintroduce YouTube title scraping, Spotify support, custom target applications or `sponsor.ajay.app` calls.
- Use BilibiliSponsorBlock (`www.bsbsb.top`) as the segment source and preserve BVID/CID filtering.
- Keep the application ID and namespace under `io.github.ezn24.sponsorskip.bilibili`.
- Preserve upstream copyright and GPL attribution when modifying derived files.
- Keep English, Simplified Chinese, Traditional Chinese (Hong Kong) and Traditional Chinese (Taiwan) resources in sync.
- Run `gradlew.bat assembleDebug` after Kotlin, manifest or resource changes.
