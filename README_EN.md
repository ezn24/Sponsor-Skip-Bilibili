<div align="center">

<img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/bilibili/assets/graphics/icon.png" alt="Sponsor Skip for Bilibili app icon" width="200" />

# Sponsor Skip for Bilibili

### A native Android tool for skipping ads and sponsored segments, based on [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock) and [Sponsor-Skip](https://github.com/jaival-11/Sponsor-Skip)

<br/>

[![Latest release](https://img.shields.io/github/v/release/ezn24/Sponsor-Skip-Bilibili?style=for-the-badge\&labelColor=0d1117)](https://github.com/ezn24/Sponsor-Skip-Bilibili/releases/latest)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge\&labelColor=0d1117\&color=EA7233)](https://github.com/ezn24/Sponsor-Skip-Bilibili/blob/main/LICENSE)

<br/>

[**Download**](#下載) · [**Features**](#功能) · [**Report an Issue**](#問題回報與功能建議)

</div>

---

## Table of Contents

  * **[Features](#功能)**: What Sponsor Skip for Bilibili can do
    
  * **[How It Works](#運作方式)**: The basic workflow of Sponsor Skip for Bilibili
    
  * **[Download](#下載)**: Get the latest version
    
  * **[Build from Source](#從原始碼建置)**: Build environment and compilation instructions
    
  * **[Contributing](#貢獻)**: How to contribute to the project
    
  * **[Issue Reports and Feature Requests](#問題回報與功能建議)**: Report bugs or suggest new features
    
  * **[Technical Information](#技術資訊)**: Technologies used and core architecture
    
  * **[Acknowledgments](#致謝)**: BilibiliSponsorBlock, SponsorBlock, and other open-source projects
    
  * **[Privacy](#隱私權)**: Privacy policy
    
  * **[Disclaimer](#免責聲明)**: Third-party affiliations, liability, and terms of use
    
  * **[License](#授權條款)**: GNU General Public License v3.0

---

<h2><a id="screenshots"></a>Screenshots</h2>

<img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/main/assets/screenshots/Screenshot1.jpg" alt="Home" width="30%" /> <img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/main/assets/screenshots/Screenshot2.jpg" alt="Settings" width="30%" /> <img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/main/assets/screenshots/Screenshot3.jpg" alt="Other settings" width="30%" />

---

<a id="功能"></a>

## Features

* **Bilibili support:** Adds automatic skipping to the Bilibili viewing experience on Android without modifying the Bilibili APK.

* **Based on BilibiliSponsorBlock:** Uses segment data created by the [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock) project and its community.

* **Automatic segment skipping:** Automatically retrieves community-marked segments while a video is playing and jumps to the end of a segment when playback enters it.

* **Multiple segment categories:** Customize which content to skip or ignore using the categories provided by BilibiliSponsorBlock, such as:

  * Ads
  * Unpaid promotion / self-promotion
  * Subtle promotion / brand partnerships
  * Like, coin, and favorite / subscription reminders
  * Highlights / key moments
  * Transitions / opening animations
  * Credits / end screens
  * Recaps / summaries
  * Off-topic chatter / jokes
  * Music: non-music sections
  * Silent segments

* **Time saved statistics:** Tracks the number of skipped segments and the total viewing time saved.

* **Backup and restore:** Back up and restore app preferences and statistics.

* **Minimum segment length:** Set a minimum segment length to ignore segments that are too short.

* **Skip offset:** Adjust the skip timing slightly earlier or later to account for playback delays on different devices.

* **Quick toggle:** Quickly enable or disable the Sponsor Skip for Bilibili service.

* **Modern interface:** Uses a clean Android Material Design interface.

* **Open source:** Sponsor Skip for Bilibili is released under GPL-3.0, with its source code available on GitHub.

---

<a id="運作方式"></a>

## How It Works

**Sponsor Skip for Bilibili** combines Android's media controls with segment data provided by [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock) to automatically skip marked video segments without modifying the Bilibili app itself.

The basic workflow is as follows:

1. **Detect playback status**

   Sponsor Skip for Bilibili uses Android's media-related APIs to detect the Bilibili content currently playing and its playback position.

2. **Identify the video**

   The app identifies the corresponding Bilibili video using the available media information.

3. **Retrieve segment data**

   Sponsor Skip for Bilibili retrieves community-submitted segment timestamps for the video from the service used by BilibiliSponsorBlock.

4. **Monitor playback progress**

   While the video is playing, Sponsor Skip for Bilibili continuously tracks the current playback position.

5. **Skip automatically**

   When playback enters a segment configured for automatic skipping, Sponsor Skip for Bilibili uses Android's media controls to move playback to the end of that segment.

This requires neither modifying the Bilibili APK nor injecting Sponsor Skip for Bilibili directly into the Bilibili app.

---

<a id="下載"></a>

## Download

You can download the latest version from GitHub Releases:

<table>
  <tr>
    <th align="center">GitHub Releases</th>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/ezn24/Sponsor-Skip-Bilibili/releases/latest">
        <img src="https://raw.githubusercontent.com/NeoApplications/Neo-Backup/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png" alt="Download from GitHub Releases" height="70">
      </a>
    </td>
  </tr>
</table>

> We recommend downloading APKs only from this project's GitHub Releases or other explicitly listed official sources.

---

<a id="從原始碼建置"></a>

## Build from Source

To compile Sponsor Skip for Bilibili yourself, use the Gradle Wrapper included in the project.

### Prerequisites

* **Git**
* **JDK 17** or later
* **Android SDK**
* `ANDROID_HOME` configured
* Android SDK license terms accepted

For example:

```bash
git clone https://github.com/ezn24/Sponsor-Skip-Bilibili.git
cd Sponsor-Skip-Bilibili
./gradlew assembleDebug
```

Once the build is complete, you can find the APK in the corresponding Gradle output directory.

---

<a id="貢獻"></a>

## Contributing

Contributions to Sponsor Skip for Bilibili are welcome.

1. Fork this repository.
2. Create a new feature branch.

```bash
git checkout -b feature/AmazingFeature
```

3. Commit your changes.

```bash
git commit -m "feat: add AmazingFeature"
```

4. Push the branch.

```bash
git push origin feature/AmazingFeature
```

5. Open a Pull Request.

If your changes involve the BilibiliSponsorBlock API, segment categories, or data formats, we recommend also consulting:

* [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)
* [BilibiliSponsorBlock API documentation](https://github.com/hanydd/BilibiliSponsorBlock/wiki/API)

---

<a id="問題回報與功能建議"></a>

## Issue Reports and Feature Requests

### Found a problem?

Please visit:

[GitHub Issues](https://github.com/ezn24/Sponsor-Skip-Bilibili/issues)

Open an issue and provide as much of the following information as possible:

* Sponsor Skip for Bilibili version
* Android version
* Bilibili version
* Device model
* Steps to reproduce the issue
* Expected result
* Actual result
* Relevant error logs or screenshots

### Have a feature request?

You can also use:

[GitHub Issues](https://github.com/ezn24/Sponsor-Skip-Bilibili/issues)

to share your ideas.

---

<a id="技術資訊"></a>

## Technical Information

### Technology Stack

![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?style=for-the-badge\&logo=kotlin\&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-0095D5?style=for-the-badge\&logo=kotlin\&logoColor=white)
![Material Design](https://img.shields.io/badge/Material_Design-757575?style=for-the-badge\&logo=materialdesign\&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge\&logo=Gradle\&logoColor=white)
![BilibiliSponsorBlock](https://img.shields.io/badge/BilibiliSponsorBlock-00A1D6?style=for-the-badge)

### Core Architecture

Sponsor Skip for Bilibili is developed primarily in **Kotlin** and uses Android's media capabilities to control the content currently playing without modifying Bilibili itself.

The core workflow can be summarized as follows:

* **Media playback detection**

  Retrieves current media playback information from Android to determine whether Bilibili is playing a video.

* **Video identification**

  Identifies the currently playing Bilibili video from media information to obtain the video identifier needed to query segment data.

* **BilibiliSponsorBlock API**

  Retrieves segment timestamps and category data submitted by the BilibiliSponsorBlock community.

  API documentation:

  https://github.com/hanydd/BilibiliSponsorBlock/wiki/API

* **Playback progress tracking**

  Sponsor Skip for Bilibili tracks the current video playback position in the background to determine whether playback is approaching or has entered a segment that should be skipped.

* **Android MediaController**

  When playback enters a segment that matches the configured settings, Android's media controls seek to the end of that segment.

* **Asynchronous processing**

  Network requests, playback status tracking, and other background tasks run through Kotlin Coroutines to avoid blocking the main UI thread.

---

## BilibiliSponsorBlock

Sponsor Skip for Bilibili's Bilibili support is based on:

**[hanydd/BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)**

BilibiliSponsorBlock is an open-source project ported from SponsorBlock and designed specifically for Bilibili.

It provides community-maintained video segment data, allowing users to mark and skip:

* Ads
* Self-promotion
* Brand partnerships
* Like, coin, and favorite reminders
* Intros / transitions
* End screens
* Recaps
* Off-topic content
* Other categorized segments

BilibiliSponsorBlock also provides an API that allows other clients and third-party projects to use this community-created segment data.

### BilibiliSponsorBlock API

Production service:

```text
https://bsbsb.top/api/
```

API documentation:

https://github.com/hanydd/BilibiliSponsorBlock/wiki/API

### Database

BilibiliSponsorBlock also makes its segment data publicly available for use in further development:

```text
https://download.bsbsb.top/database.zip
```

---

<a id="致謝"></a>

## Acknowledgments

Sponsor Skip for Bilibili's Bilibili functionality builds on the work of several open-source projects and communities.

### 1. Sponsor-Skip

**[Sponsor-Skip](https://github.com/jaival-11/Sponsor-Skip)**
Created and maintained by [Jaival-11](https://github.com/jaival-11)

Based on this project and modified to support BiliBili, with similar overall operating principles.

### 2. BilibiliSponsorBlock

**[BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)**
Created and maintained by [hanydd](https://github.com/hanydd).

Sponsor Skip for Bilibili's Bilibili segment data, category concepts, and related API support are based on BilibiliSponsorBlock.

Thanks to the BilibiliSponsorBlock developers and all community members who help submit, correct, and review video segments.

### 3. SponsorBlock

**[SponsorBlock](https://github.com/ajayyy/SponsorBlock)**
Created by [Ajay Ramachandran](https://github.com/ajayyy).

BilibiliSponsorBlock itself is ported and adapted from SponsorBlock. SponsorBlock established the core concept and original implementation of community-driven video segment marking.

Sponsor Skip for Bilibili therefore also benefits indirectly from the work of SponsorBlock and its open-source community.

### 4. Kotlin and Android

**[Kotlin](https://kotlinlang.org/)** and **[Android](https://developer.android.com/)**

Provide the programming language, Android APIs, background processing mechanisms, and app architecture used by Sponsor Skip for Bilibili.

### 5. Android Jetpack

**[Android Jetpack](https://developer.android.com/jetpack)**

Provides Android app lifecycle, UI, and other infrastructure components.

### 5. Material Design

**[Material Design](https://m3.material.io/)**

Sponsor Skip for Bilibili's interface and some of its icons use the Material Design system.

---

<a id="隱私權"></a>

## Privacy

Sponsor Skip for Bilibili is not intended to collect user data.

The app may need to access necessary Android system media information and make network requests to Bilibili, BilibiliSponsorBlock, or related services to identify videos and retrieve segment data.

For full details, see:

[Privacy Policy](PRIVACY.md)

---

<a id="免責聲明"></a>

## Disclaimer

### BilibiliSponsorBlock

Sponsor Skip for Bilibili uses or is compatible with segment data and related services provided by BilibiliSponsorBlock.

BilibiliSponsorBlock is an independent third-party open-source project.

Sponsor Skip for Bilibili has no official affiliation with the original authors of BilibiliSponsorBlock unless explicitly stated otherwise.

### Liability and Warranty

This program is free software: you may redistribute it and/or modify it under the terms of the **GNU General Public License** published by the Free Software Foundation, either version 3 of the License or any later version.

This program is distributed in the hope that it will be useful, but **without any warranty**, including but not limited to the implied warranties of merchantability or fitness for a particular purpose.

See the GNU General Public License for details.

### User Responsibilities and Platform Terms of Service

Sponsor Skip for Bilibili is a local automation and media control tool.

By using this software, you understand and agree that:

* You are responsible for ensuring that your use complies with the terms of service of Bilibili and other third-party services.
* Third-party platforms may change their APIs, player behavior, terms of service, or technical restrictions at any time.
* Sponsor Skip for Bilibili cannot guarantee permanent compatibility with any third-party service.
* The project maintainers are not responsible for account restrictions, service interruptions, data loss, or other damages resulting from the use of this software.

---

<a id="授權條款"></a>

## License

Sponsor Skip for Bilibili is licensed under the [GNU General Public License v3.0](LICENSE).

Where this project includes or derives from concepts or code from other GPL-licensed projects, the relevant content remains subject to its original license terms and copyright notices.

Special thanks to:

* [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)
* [SponsorBlock](https://github.com/ajayyy/SponsorBlock)
* 

---

<div align="center">

**Based on [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)**

**Maintained by [ezn24](https://github.com/ezn24)**

</div>
