<div align="center">

<img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/bilibili/assets/graphics/icon.png" alt="Sponsor Skip for Bilibili 应用程序图标" width="200" />

# Sponsor Skip for Bilibili

### 基于 [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock) 与 [Sponsor-Skip](https://github.com/jaival-11/Sponsor-Skip) 开发的 Android 原生广告／赞助片段跳过工具

<br/>

[![最新版本](https://img.shields.io/github/v/release/ezn24/Sponsor-Skip-Bilibili?style=for-the-badge\&labelColor=0d1117)](https://github.com/ezn24/Sponsor-Skip-Bilibili/releases/latest)
[![许可证](https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge\&labelColor=0d1117\&color=EA7233)](https://github.com/ezn24/Sponsor-Skip-Bilibili/blob/main/LICENSE)

<br/>

[**下载**](#下載) · [**功能**](#功能) · [**问题反馈**](#問題回報與功能建議)

</div>

---

## 目录

  * **[功能](#功能)**: Sponsor Skip for Bilibili 可以做什么
    
  * **[运作方式](#運作方式)**: Sponsor Skip for Bilibili 的基本工作流程
    
  * **[下载](#下載)**: 取得最新版本
    
  * **[从源代码构建](#從原始碼建置)**: 构建环境与编译方式
    
  * **[贡献](#貢獻)**: 如何参与项目开发
    
  * **[问题反馈与功能建议](#問題回報與功能建議)**: 反馈错误或提出新功能
    
  * **[技术信息](#技術資訊)**: 使用技术与核心架构
    
  * **[致谢](#致謝)**: BilibiliSponsorBlock、SponsorBlock 与其他开源项目
    
  * **[隐私](#隱私權)**: 隐私政策
    
  * **[免责声明](#免責聲明)**: 第三方关系、责任与使用条款
    
  * **[许可条款](#授權條款)**: GNU General Public License v3.0

---

<h2><a id="screenshots"></a>屏幕截图</h2>

<img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/main/assets/screenshots/Screenshot1.jpg" alt="首页" width="30%" /> <img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/main/assets/screenshots/Screenshot2.jpg" alt="设置页面" width="30%" /> <img src="https://raw.githubusercontent.com/ezn24/Sponsor-Skip-Bilibili/refs/heads/main/assets/screenshots/Screenshot3.jpg" alt="其他设置" width="30%" />

---

<a id="功能"></a>

## 功能

* **Bilibili 支持：** 为 Android 上的 Bilibili 播放体验加入自动跳过功能，不需要修改 Bilibili APK。

* **基于 BilibiliSponsorBlock：** 使用由 [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock) 项目及其社区建立的片段数据。

* **自动跳过片段：** 播放视频时，自动取得已由社区标记的时间片段，并在播放进度进入指定片段时跳至片段结尾。

* **多种片段分类：** 可依照 BilibiliSponsorBlock 提供的片段分类，自定义需要跳过或忽略的内容，例如：

  * 广告
  * 无偿／自我推广
  * 柔性推广／品牌合作
  * 三连／订阅提醒
  * 精彩时刻／重点
  * 过场／开场动画
  * 鸣谢／结束画面
  * 回顾／概要
  * 离题闲聊／玩笑
  * 音乐：非音乐部分
  * 静音片段

* **节省时间统计：** 记录已跳过的片段数量以及累计节省的观看时间。

* **备份与恢复：** 备份及恢复应用程序偏好设置与统计数据。

* **最短片段长度：** 可设置片段最低长度，忽略过短的片段。

* **跳过偏移：** 可调整跳过时间点，使跳转稍微提前或延后，以适应不同设备的播放延迟。

* **快速开关：** 可快速启用或停用 Sponsor Skip for Bilibili 服务。

* **现代化界面：** 采用简洁的 Android Material Design 界面。

* **开源：** Sponsor Skip for Bilibili 以 GPL-3.0 许可发布，源代码公开于 GitHub。

---

<a id="運作方式"></a>

## 运作方式

**Sponsor Skip for Bilibili** 将 Android 的媒体控制能力与 [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock) 提供的片段数据结合，在不修改 Bilibili 应用程序本体的情况下，自动跳过已标记的视频片段。

基本流程如下：

1. **检测播放状态**

   Sponsor Skip for Bilibili 通过 Android 系统提供的媒体相关 API，检测目前正在播放的 Bilibili 内容以及播放进度。

2. **识别视频**

   应用程序根据目前取得的媒体信息，解析对应的 Bilibili 视频。

3. **取得片段数据**

   Sponsor Skip for Bilibili 向 BilibiliSponsorBlock 所使用的服务取得该视频已由社区提交的片段时间信息。

4. **监控播放进度**

   播放视频期间，Sponsor Skip for Bilibili 持续追踪目前的播放位置。

5. **自动跳过**

   当播放位置进入设置为自动跳过的片段时，Sponsor Skip for Bilibili 会通过 Android 媒体控制功能将播放位置移动至该片段结尾。

因此不需要修改 Bilibili APK，也不需要将 Sponsor Skip for Bilibili 直接注入 Bilibili 应用程序。

---

<a id="下載"></a>

## 下载

你可以从 GitHub Releases 下载最新版本：

<table>
  <tr>
    <th align="center">GitHub Releases</th>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/ezn24/Sponsor-Skip-Bilibili/releases/latest">
        <img src="https://raw.githubusercontent.com/NeoApplications/Neo-Backup/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png" alt="从 GitHub Releases 下载" height="70">
      </a>
    </td>
  </tr>
</table>

> 建议仅从本项目的 GitHub Releases 或其他明确列出的官方来源下载 APK。

---

<a id="從原始碼建置"></a>

## 从源代码构建

如果你希望自行编译 Sponsor Skip for Bilibili，可以使用项目内附的 Gradle Wrapper。

### 必要环境

* **Git**
* **JDK 17** 或更新版本
* **Android SDK**
* 已设置 `ANDROID_HOME`
* 已接受 Android SDK 许可条款

例如：

```bash
git clone https://github.com/ezn24/Sponsor-Skip-Bilibili.git
cd Sponsor-Skip-Bilibili
./gradlew assembleDebug
```

编译完成后，可在 Gradle 对应的输出目录找到 APK。

---

<a id="貢獻"></a>

## 贡献

欢迎参与 Sponsor Skip for Bilibili 的开发。

1. Fork 此 Repository。
2. 建立新的功能分支。

```bash
git checkout -b feature/AmazingFeature
```

3. 提交修改。

```bash
git commit -m "feat: add AmazingFeature"
```

4. 推送分支。

```bash
git push origin feature/AmazingFeature
```

5. 建立 Pull Request。

如果修改涉及 BilibiliSponsorBlock 的 API、片段分类或数据格式，建议同时参考：

* [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)
* [BilibiliSponsorBlock API 文档](https://github.com/hanydd/BilibiliSponsorBlock/wiki/API)

---

<a id="問題回報與功能建議"></a>

## 问题反馈与功能建议

### 发现问题？

请前往：

[GitHub Issues](https://github.com/ezn24/Sponsor-Skip-Bilibili/issues)

建立 Issue，并尽可能提供：

* Sponsor Skip for Bilibili 版本
* Android 版本
* Bilibili 版本
* 设备型号
* 问题重现步骤
* 预期结果
* 实际结果
* 必要的错误记录或截图

### 有功能建议？

同样可以通过：

[GitHub Issues](https://github.com/ezn24/Sponsor-Skip-Bilibili/issues)

提出你的想法。

---

<a id="技術資訊"></a>

## 技术信息

### 技术栈

![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?style=for-the-badge\&logo=kotlin\&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-0095D5?style=for-the-badge\&logo=kotlin\&logoColor=white)
![Material Design](https://img.shields.io/badge/Material_Design-757575?style=for-the-badge\&logo=materialdesign\&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge\&logo=Gradle\&logoColor=white)
![BilibiliSponsorBlock](https://img.shields.io/badge/BilibiliSponsorBlock-00A1D6?style=for-the-badge)

### 核心架构

Sponsor Skip for Bilibili 主要以 **Kotlin** 开发，利用 Android 系统提供的媒体功能，在不修改 Bilibili 本体的情况下控制目前正在播放的内容。

核心流程可以概括为：

* **媒体播放检测**

  取得目前 Android 系统中的媒体播放信息，判断 Bilibili 是否正在播放视频。

* **视频识别**

  根据媒体信息解析目前播放的 Bilibili 视频，以取得查询片段数据所需的视频识别信息。

* **BilibiliSponsorBlock API**

  取得由 BilibiliSponsorBlock 社区提交的片段时间与分类数据。

  API 文档：

  https://github.com/hanydd/BilibiliSponsorBlock/wiki/API

* **播放进度追踪**

  Sponsor Skip for Bilibili 在后台追踪目前视频播放位置，判断是否即将或已经进入需要跳过的片段。

* **Android MediaController**

  当播放位置进入符合设置的片段时，通过 Android 的媒体控制功能将播放器跳转至片段结束位置。

* **异步处理**

  网络请求、播放状态追踪以及其他后台工作通过 Kotlin Coroutines 执行，避免阻塞主 UI 线程。

---

## BilibiliSponsorBlock

Sponsor Skip for Bilibili 的 Bilibili 支持是基于：

**[hanydd/BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)**

BilibiliSponsorBlock 是一个移植自 SponsorBlock、专门针对 Bilibili 设计的开源项目。

它建立了一套由社区共同维护的视频片段数据，让用户可以标记并跳过：

* 广告
* 自我推广
* 品牌合作
* 三连提醒
* 开场／过场
* 结束画面
* 回顾
* 离题内容
* 其他可分类片段

BilibiliSponsorBlock 同时提供 API，使其他客户端及第三方项目可以使用这些社区建立的片段数据。

### BilibiliSponsorBlock API

正式服务：

```text
https://bsbsb.top/api/
```

API 文档：

https://github.com/hanydd/BilibiliSponsorBlock/wiki/API

### 数据库

BilibiliSponsorBlock 亦公开其片段数据供二次开发使用：

```text
https://download.bsbsb.top/database.zip
```

---

<a id="致謝"></a>

## 致谢

Sponsor Skip for Bilibili 的 Bilibili 功能建立在多个开源项目及社区成果之上。

### 1. Sponsor-Skip

**[Sponsor-Skip](https://github.com/jaival-11/Sponsor-Skip)**
由 [Jaival-11](https://github.com/jaival-11) 建立及维护

基于其项目并经过修改以适配 BiliBili，总体原理类似

### 2. BilibiliSponsorBlock

**[BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)**
由 [hanydd](https://github.com/hanydd) 建立及维护。

Sponsor Skip for Bilibili 的 Bilibili 片段数据、分类概念以及相关 API 支持基于 BilibiliSponsorBlock。

感谢 BilibiliSponsorBlock 的开发者以及所有协助提交、修正与审核视频片段的社区成员。

### 3. SponsorBlock

**[SponsorBlock](https://github.com/ajayyy/SponsorBlock)**
由 [Ajay Ramachandran](https://github.com/ajayyy) 建立。

BilibiliSponsorBlock 本身移植并改造自 SponsorBlock。SponsorBlock 建立了社区协作标记视频片段的核心概念及原始实现。

因此 Sponsor Skip for Bilibili 亦间接受益于 SponsorBlock 及其开源社区的工作。

### 4. Kotlin 与 Android

**[Kotlin](https://kotlinlang.org/)** 与 **[Android](https://developer.android.com/)**

提供 Sponsor Skip for Bilibili 所使用的编程语言、Android API、后台处理机制与应用程序架构。

### 5. Android Jetpack

**[Android Jetpack](https://developer.android.com/jetpack)**

提供 Android 应用程序生命周期、UI 与其他基础架构组件。

### 5. Material Design

**[Material Design](https://m3.material.io/)**

Sponsor Skip for Bilibili 的界面与部分图标使用 Material Design 系统。

---

<a id="隱私權"></a>

## 隐私

Sponsor Skip for Bilibili 不以收集用户数据为目的。

应用程序可能需要访问必要的 Android 系统媒体信息以及向 Bilibili、BilibiliSponsorBlock 或相关服务发出网络请求，以识别视频及取得片段数据。

完整内容请参阅：

[隐私政策](PRIVACY.md)

---

<a id="免責聲明"></a>

## 免责声明

### BilibiliSponsorBlock

Sponsor Skip for Bilibili 使用或兼容于由 BilibiliSponsorBlock 提供的片段数据及相关服务。

BilibiliSponsorBlock 是独立的第三方开源项目。

Sponsor Skip for Bilibili 与 BilibiliSponsorBlock 的原作者之间不存在官方从属关系，除非另有明确说明。

### 责任与担保

本程序为自由软件，你可以依照自由软件基金会发布的 **GNU General Public License** 条款重新分发及／或修改本程序；许可版本为 GPL 第 3 版或任何较新的版本。

本程序的发布目的是希望它能够有所帮助，但**不提供任何形式的担保**，包括但不限于适售性或特定用途适用性的默示担保。

详细内容请参阅 GNU General Public License。

### 用户责任与平台服务条款

Sponsor Skip for Bilibili 是一个本地自动化及媒体控制工具。

使用本软件即代表你理解并同意：

* 你有责任确认自己的使用方式符合 Bilibili 及其他第三方服务的使用条款。
* 第三方平台可能随时修改 API、播放器行为、服务条款或技术限制。
* Sponsor Skip for Bilibili 无法保证任何第三方服务永久兼容。
* 项目维护者不对因使用本软件造成的账号限制、服务中断、数据损失或其他损害负责。

---

<a id="授權條款"></a>

## 许可条款

Sponsor Skip for Bilibili 采用 [GNU General Public License v3.0](LICENSE) 授权。

本项目包含或衍生自其他 GPL 许可项目之概念或代码时，相关内容仍应遵循其原始许可条款与著作权声明。

特别感谢：

* [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)
* [SponsorBlock](https://github.com/ajayyy/SponsorBlock)
* 

---

<div align="center">

**基于 [BilibiliSponsorBlock](https://github.com/hanydd/BilibiliSponsorBlock)**

**由 [ezn24](https://github.com/ezn24) 维护**

</div>
