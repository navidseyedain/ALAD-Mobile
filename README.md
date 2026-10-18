<div align="center">
  🌐 <strong>Read in English</strong> | <a href="README-fa.md">خواندن به زبان فارسی</a>
</div>

<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="ALAD Logo" width="120" />

<h1>🎙️ ALAD — AI Live Audio Dubbing (Mobile)</h1>

<p><strong>Real-time AI voice dubbing for any Android app, powered by Google's Gemini 3.5 Live Translate.</strong></p>

<p>
  <a href="https://github.com/navidseyedain/ALAD-Mobile/stargazers"><img src="https://img.shields.io/github/stars/navidseyedain/ALAD-Mobile?style=for-the-badge&color=FFD700" alt="Stars"></a>
  <a href="https://github.com/navidseyedain/ALAD-Mobile/releases/latest"><img src="https://img.shields.io/github/v/release/navidseyedain/ALAD-Mobile?style=for-the-badge&color=blue" alt="Latest Release"></a>
  <a href="https://developer.android.com/"><img src="https://img.shields.io/badge/Android-10%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://aistudio.google.com/"><img src="https://img.shields.io/badge/Powered%20By-Gemini%203.5%20Live-00C896?style=for-the-badge&logo=google&logoColor=white" alt="Gemini"></a>
  <a href="https://github.com/navidseyedain/ALAD-Mobile?tab=readme-ov-file#%EF%B8%8F-78-supported-languages"><img src="https://img.shields.io/badge/Languages-78-blueviolet?style=for-the-badge" alt="78 Languages"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License"></a>
</p>

<p>
  <b>Watch any foreign video or listen to any podcast. Hear it in your native language — instantly.</b><br/>
  No subscriptions. No accounts. 100% free and open-source.
</p>

<a href="https://github.com/navidseyedain/ALAD-Mobile/releases/latest"><img src="https://img.shields.io/badge/Download_APK-v1.1.0_Release-FF5722?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" /></a>

<br/>

</div>

---

## 🌍 What is ALAD Mobile?

**ALAD (AI Live Audio Dubbing)** is a native Android application that completely removes language barriers on mobile devices. Working seamlessly in the background, ALAD captures internal system audio directly from any playing app (YouTube, Netflix, Spotify, Podcasts, Twitch, etc.), sends it to Google's **Gemini 3.5 Live Translate** model via high-speed WebSockets, and plays back fluid, natural translated speech in real time.

Unlike traditional subtitle or auto-captioning apps, ALAD produces **live vocal dubbing** — you *listen* to the content naturally without having your eyes glued to the screen.

> **Use Cases:**
> - 🎬 **Movies & Series:** Watch foreign cinema (Korean, Japanese, Turkish, Spanish, etc.) and hear real-time spoken translation in your preferred language.
> - 🎙️ **Podcasts & Music:** Listen to international podcasts and interviews with live voice dubbing.
> - 🎓 **Online Learning & Lectures:** Follow university courses and tech tutorials without missing spoken nuances.
> - 🎮 **Live Streams:** Understand international Twitch and YouTube streamers live as they speak.

---

## 📸 Screenshots & Showcase

<div align="center">
  <table>
    <tr>
      <td align="center" width="33%">
        <img src="docs/1.jpg" width="100%" alt="Main Screen - Ready State" /><br/>
        <b>🏠 Modern Glassmorphism Dashboard</b>
      </td>
      <td align="center" width="33%">
        <img src="docs/2.jpg" width="100%" alt="Active Dubbing State" /><br/>
        <b>🔴 Real-Time Waveform & Live Status</b>
      </td>
      <td align="center" width="33%">
        <img src="docs/3.jpg" width="100%" alt="Language Selection" /><br/>
        <b>🌐 78+ Languages Selector with Flags</b>
      </td>
    </tr>
    <tr>
      <td align="center" width="33%">
        <img src="docs/4.jpg" width="100%" alt="Floating Widget Overlay" /><br/>
        <b>🎛️ Floating Overlay over YouTube/Video</b>
      </td>
      <td align="center" width="33%">
        <img src="docs/5.jpg" width="100%" alt="Settings Screen" /><br/>
        <b>⚙️ Secure API Key & Config</b>
      </td>
      <td align="center" width="33%">
        <img src="docs/6.jpg" width="100%" alt="How to Use Guide" /><br/>
        <b>💡 Interactive Onboarding Guide</b>
      </td>
    </tr>
  </table>
</div>

<br/>

### 🎥 Live Demo Video

<div align="center">
  <video src="https://github.com/user-attachments/assets/54efbdc7-e19f-49ac-9afd-aafa0d73650a" controls="controls" width="360">
    Your browser does not support the video tag.
  </video>
</div>

---

## ✨ Features & Highlights

### 🎙️ 1. Ultra-Low Latency Live AI Dubbing
- **Real-Time Bidirectional Streaming:** Direct WebSocket connection (`BidiGenerateContent`) to Gemini Live for instantaneous voice-to-voice translation.
- **Crystal-Clear Internal Audio Capture:** Uses Android's native `MediaProjection API` to record system audio directly without room noise or mic degradation.
- **Smart Audio Ducking:** Automatically suppresses the original background media audio while the AI voice is speaking, ensuring pristine clarity.
- **Persistent Foreground Service:** Keeps dubbing continuously even when multitasking, switching between apps, or turning off the screen.

### 🎛️ 2. Smart Floating Overlay Widget
- **Overlay Multitasking:** Start, pause, and monitor dubbing without leaving your active video or game.
- **👆 Double-Tap Smart Control:** Double-tap anywhere on the floating widget to immediately pause or resume dubbing on the fly.
- **Pulsing Audio Halo:** Visual breathing animations reflect active translation states at a glance.
- **Draggable & Dismissible:** Place the widget anywhere along the screen edges with fluid touch physics.

### 📳 3. Tactile Haptic Feedback
- **Physical Button Sensation:** Every tap on the 3D start button and floating controller triggers subtle, premium haptic vibrations that give digital interactions a tactile, physical feel.

### 🎨 4. Futuristic Dark Glassmorphism UI
- **Obsidian Theme & Ambient Glow:** Deep space gradients with radiant cyan and violet neon mesh lighting.
- **Frosted Glass Cards:** Semi-translucent glass panels with crystalline borders and subtle blur effects.
- **Dynamic Neon Waveform:** Real-time audio frequency visualizer animating in harmony with your device's audio output.
- **3D Press Physics:** Start/Stop button with spring mechanics, dynamic shadows, and glowing aura.

---

## 🗺️ 78 Supported Languages

<details>
<summary><b>Click to expand full list of 78 supported languages</b></summary>

<br/>

| | | | |
|---|---|---|---|
| 🇿🇦 Afrikaans | 🇪🇹 Amharic | 🇸🇦 Arabic | 🇦🇲 Armenian |
| 🇦🇿 Azerbaijani | 🇧🇩 Bengali | 🇧🇬 Bulgarian | 🇲🇲 Burmese |
| 🇨🇳 Chinese (Simplified) | 🇹🇼 Chinese (Traditional) | 🇭🇷 Croatian | 🇨🇿 Czech |
| 🇩🇰 Danish | 🇳🇱 Dutch | 🇺🇸 English | 🇪🇪 Estonian |
| 🇵🇭 Filipino | 🇫🇮 Finnish | 🇫🇷 French | 🇬🇪 Georgian |
| 🇩🇪 German | 🇬🇷 Greek | 🇮🇳 Gujarati | 🇳🇬 Hausa |
| 🇮🇱 Hebrew | 🇮🇳 Hindi | 🇭🇺 Hungarian | 🇮🇸 Icelandic |
| 🇮🇩 Indonesian | 🇮🇹 Italian | 🇯🇵 Japanese | 🇮🇳 Kannada |
| 🇰🇿 Kazakh | 🇰🇭 Khmer | 🇷🇼 Kinyarwanda | 🇰🇷 Korean |
| 🇱🇦 Lao | 🇱🇻 Latvian | 🇱🇹 Lithuanian | 🇲🇰 Macedonian |
| 🇲🇾 Malay | 🇮🇳 Malayalam | 🇮🇳 Marathi | 🇲🇳 Mongolian |
| 🇳🇵 Nepali | 🇳🇴 Norwegian | 🇮🇷 Persian | 🇵🇱 Polish |
| 🇧🇷 Portuguese (Brazil) | 🇵🇹 Portuguese (Portugal) | 🇮🇳 Punjabi | 🇷🇴 Romanian |
| 🇷🇺 Russian | 🇷🇸 Serbian | 🇮🇳 Sindhi | 🇱🇰 Sinhala |
| 🇸🇰 Slovak | 🇸🇮 Slovenian | 🇪🇸 Spanish | 🇰🇪 Swahili |
| 🇸🇪 Swedish | 🇮🇳 Tamil | 🇮🇳 Telugu | 🇹🇭 Thai |
| 🇹🇷 Turkish | 🇺🇦 Ukrainian | 🇵🇰 Urdu | 🇺🇿 Uzbek |
| 🇻🇳 Vietnamese | 🇿🇦 Zulu | | |

</details>

---

## 🚀 Getting Started

### Prerequisites
1. **Android Device:** Android 10.0 (API 29) or newer (required for system audio capture).
2. **Gemini API Key:** Get a free API key from [Google AI Studio](https://aistudio.google.com/).

### Quick Installation
1. Go to the [Releases](https://github.com/navidseyedain/ALAD-Mobile/releases/latest) page.
2. Download the latest **`ALAD-Mobile-v1.1.0.apk`**.
3. Install the APK on your device.
4. Open the app, tap ⚙️ **Settings**, paste your **Gemini API Key**, and tap **Save Settings**.
5. Pick your target language, tap **Start Dubbing**, and enjoy!

### Build from Source
```bash
# Clone the repository
git clone https://github.com/navidseyedain/ALAD-Mobile.git
cd ALAD-Mobile

# Open in Android Studio or build via Gradle
./gradlew assembleRelease
```

---

## 🧠 System Architecture & Workflow

ALAD uses a reactive, pipeline-based event-driven streaming architecture designed for sub-second vocal dubbing latency:

```mermaid
flowchart TD
    subgraph Capture["📱 1. Internal Audio Capture"]
        APP["Active Media App<br/><i>(YouTube / Netflix / Spotify)</i>"]
        MP["MediaProjection API<br/><code>AudioPlaybackCaptureConfiguration</code>"]
        REC["AudioRecord Engine<br/><code>16kHz Mono PCM Buffer</code>"]
        APP -->|"Raw System Audio"| MP
        MP -->|"PCM Bytes"| REC
    end

    subgraph Service["⚡ 2. Core Service & Streaming Engine"]
        FS["AudioDubbingForegroundService<br/><i>(Persistent Lifecycle & State Sync)</i>"]
        WS["OkHttp WebSocket Client<br/><code>BidiGenerateContent Protocol</code>"]
        DUCK["Smart Audio Ducking<br/><i>(Dynamic Media Volume Attenuation)</i>"]
        REC -->|"16kHz PCM Chunks"| FS
        FS -->|"Real-time Upstream Stream"| WS
        FS -.->|"Ducking Signal"| DUCK
    end

    subgraph Cloud["☁️ 3. Google Gemini 3.5 Live AI"]
        GEMINI["<b>Gemini Live Translate Engine</b><br/><code>gemini-2.0-flash-exp / Live Bidi</code><br/><i>Ultra-Low Latency Speech-to-Speech</i>"]
        WS <-->|"Bi-directional Streaming Protocol"| GEMINI
    end

    subgraph Playback["🔊 4. Low-Latency Audio Rendering"]
        DEC["Audio Response Stream Parser<br/><i>Real-time PCM Decoder</i>"]
        AT["Low-Latency AudioTrack<br/><i>Direct PCM Buffer Streaming</i>"]
        OUT["User Earphones / Speaker 🎧"]
        WS -->|"Downstream AI Audio"| DEC
        DEC -->|"PCM Chunks"| AT
        AT -->|"Crystal Clear Dubbed Voice"| OUT
    end

    subgraph Controls["🎛️ 5. Modern UI & Floating Overlay"]
        UI["Jetpack Compose Dashboard<br/><i>(Dark Glassmorphism UI)</i>"]
        WIDGET["Floating Overlay Controller<br/><i>(👆 Double-Tap Gesture & Haptics)</i>"]
        UI <-->|"StateFlow / UI Sync"| FS
        WIDGET <-->|"Haptic & Overlay Control"| FS
    end

    classDef capture fill:#0f172a,stroke:#38bdf8,stroke-width:2px,color:#f8fafc;
    classDef service fill:#1e1b4b,stroke:#818cf8,stroke-width:2px,color:#f8fafc;
    classDef cloud fill:#064e3b,stroke:#34d399,stroke-width:2px,color:#f8fafc;
    classDef playback fill:#4c0519,stroke:#fb7185,stroke-width:2px,color:#f8fafc;
    classDef controls fill:#2e1065,stroke:#c084fc,stroke-width:2px,color:#f8fafc;

    class APP,MP,REC capture;
    class FS,WS,DUCK service;
    class GEMINI cloud;
    class DEC,AT,OUT playback;
    class UI,WIDGET controls;
```

### 🧩 Architectural Components Breakdown

| Layer / Component | Technology Stack | Responsibility & Highlights |
|---|---|---|
| **📱 Internal Audio Capture** | `MediaProjection API` + `AudioRecord` | Captures crisp, direct internal digital audio from target apps with 0% ambient mic noise. |
| **🌐 Bi-directional Streaming** | `OkHttp WebSocket Client` | Maintains persistent, ultra-low latency WebSocket connection using Gemini's `BidiGenerateContent` protocol. |
| **🧠 Real-Time AI Dubbing** | `gemini-3.5-live-translate-preview` | Google DeepMind's speech-to-speech engine performing continuous translation and vocal synthesis. |
| **🔊 Smart Audio Playback** | `AudioTrack (16kHz PCM)` + `AudioManager` | Streams synthesized speech directly with automatic **Audio Ducking** (lowering original media volume). |
| **⚡ Foreground Lifecycle** | `LifecycleService` + `Coroutines/Flows` | Guarantees resilient background execution without Android OS kills, synchronizing live states. |
| **🎛️ Smart Floating Overlay** | `WindowManager` + `Jetpack Compose` | Draggable widget with live halo animations, tactile **Haptic Feedback**, and **Double-Tap Pause/Resume**. |
| **🎨 Design System** | `Jetpack Compose + Material 3` | Futuristic **Dark Glassmorphism** with translucent surfaces, mesh glows, and 3D buttons. |
| **🔒 Secure Storage** | `Jetpack DataStore (Preferences)` | Encrypted, reactive local storage for API keys and language selections. |

---

## 📁 Project Structure

```text
ALAD-Mobile/
├── app/src/main/
│   ├── kotlin/com/alad/app/
│   │   ├── core/
│   │   │   ├── audio/          # Audio capture, ducking & playback managers
│   │   │   ├── network/        # Gemini WebSocket streaming manager
│   │   │   └── service/        # Foreground & Floating Widget services
│   │   ├── data/               # Preferences & DataStore repository
│   │   ├── presentation/
│   │   │   ├── main/           # Main screen UI & ViewModel
│   │   │   └── settings/       # Settings screen UI & ViewModel
│   │   ├── ui/theme/           # Glassmorphism design system & colors
│   │   └── MainActivity.kt     # App entry point & permissions flow
│   └── AndroidManifest.xml     # Permissions & Service declarations
├── docs/                       # High-res screenshots and demo video
└── build.gradle.kts            # Build configurations & dependencies
```

---

## 🔧 Troubleshooting

| Issue | Root Cause | Solution |
|---|---|---|
| **No audio output after starting** | Target app isn't playing audio or API quota is exhausted | Ensure audio is playing in the background app and verify your Gemini API key in AI Studio. |
| **"WebSocket connection failed"** | Invalid API Key or network timeout | Double check your API key in Settings or test your internet connection. |
| **Dubbing stops when switching apps** | Android battery optimization killed the background service | Go to *App Info > Battery > Unrestricted*. |
| **Specific app audio not capturing** | App restricts internal recording (DRM / Phone Calls) | Android OS blocks recording for DRM-protected content (like Netflix protected streams) and phone calls. |
| **"App not installed" on Samsung (One UI 6+)** | Auto Blocker / Play Protect heuristic check | Temporarily turn off *Auto Blocker* under *Settings > Security and privacy*, or install APK via *My Files* app. |

---

## 🗺️ Roadmap

- [x] **v1.0.0:** Initial public release (Gemini Live integration & 78 languages)
- [x] **v1.1.0:** Complete Glassmorphism redesign, Haptic feedback, Double-tap widget controls, and state sync fixes
- [x] **v1.2.0:** AI Voice Persona selector (Choose between *Puck, Aoede, Charon, Fenrir, Kore*)
- [x] **v1.2.0:** Live Floating Transcript / Subtitle overlay
- [x] **v1.3.0:** Dual Audio Mixer with independent volume sliders and mute controls for AI voice vs. media audio
- [x] **v1.3.0:** Regional Dialects & Accents support (Egyptian, Gulf, Levantine Arabic; US/UK English; Latin Spanish; Dari/Standard Persian)
- [x] **v1.3.0:** Dual Live Subtitles (Simultaneous Source Speech + Translated Subtitles)
- [x] **v1.3.0:** Ultra-low latency audio pipeline (40ms chunks + Android low-latency audio track)
- [ ] **v1.4.0:** Automatic source language detection
- [ ] **v1.5.0:** Bluetooth headset microphone input support

---

## 🤝 Contributing

Contributions are warmly welcomed! Feel free to report issues, open feature requests, or submit pull requests.

1. Fork the repo
2. Create a branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'feat: Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📜 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more details.

---

## 🙏 Acknowledgments

- **Google DeepMind** for the revolutionary Gemini Live architecture.
- **Google AI Studio** for generous free developer tier access.

<div align="center">
  <sub>Made with ❤️ by <a href="https://github.com/navidseyedain">Navid Seyedain</a>. If ALAD helped you, please consider giving it a ⭐!</sub>
</div>
