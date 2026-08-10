<div align="center">
  <a href="README.md">Read in English</a> | 🌐 <strong>خواندن به زبان فارسی</strong>
</div>

<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="ALAD Logo" width="120" />

<h1>🎙️ ALAD — دوبله زنده هوش مصنوعی (نسخه موبایل)</h1>

<p><strong>دوبله همزمان و زنده برای تمام اپلیکیشن‌های اندروید، با قدرت گرفتن از مدل Gemini 3.5 Live Translate.</strong></p>

<p>
  <a href="https://github.com/navidseyedain/alad-mobile/stargazers"><img src="https://img.shields.io/github/stars/navidseyedain/alad-mobile?style=for-the-badge&color=FFD700" alt="Stars"></a>
  <img src="https://img.shields.io/badge/Android-10%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Powered%20By-Gemini%203.5%20Live-00C896?style=for-the-badge&logo=google&logoColor=white" alt="Gemini">
  <img src="https://img.shields.io/badge/Languages-78-blueviolet?style=for-the-badge" alt="78 Languages">
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License">
</p>

<p>
  <b>هر ویدیویی را تماشا کنید یا به هر پادکستی گوش دهید. بلافاصله آن را به زبان خودتان بشنوید.</b><br/>
  بدون نیاز به اشتراک. بدون نیاز به ساخت حساب. ۱۰۰٪ رایگان و متن‌باز.
</p>

<br/>

</div>

---

## 🌍 ALAD Mobile چیست؟

**الاد (دوبله زنده صوتی با هوش مصنوعی)** یک اپلیکیشن بومی اندروید است که سد زبان‌ها را در گوشی هوشمند شما می‌شکند. این برنامه به صورت بی‌صدا در پس‌زمینه کار می‌کند — صدای خروجی از اپلیکیشن‌های فعال شما (مثل یوتیوب، اسپاتیفای یا نتفلیکس) را ضبط کرده، آن را از طریق یک اتصال پایدار WebSocket به پیشرفته‌ترین مدل **Gemini 3.5 Live Translate** گوگل ارسال می‌کند و صدای دوبله شده را در همان لحظه پخش می‌کند.

برخلاف ابزارهای سنتی زیرنویس، الاد یک **دوبله صوتی زنده** تولید می‌کند — شما ترجمه را *می‌شنوید*، نه اینکه فقط بخوانید.

> **مثال‌هایی از کاربرد:**
> - 🎬 یک سریال کره‌ای را بدون زیرنویس تماشا کنید و آن را به فارسی بشنوید.
> - 📰 به یک پادکست خبری آلمانی در اسپاتیفای گوش دهید که به صورت زنده به عربی دوبله می‌شود.
> - 🎓 یک کلاس درس ژاپنی را در لحظه به زبان مادری خود دنبال کنید.
> - 🎮 استریمرهای خارجی را در توییچ تماشا کنید و متوجه تمام حرف‌هایشان بشوید.

---

## ✨ ویژگی‌ها

### 🎙️ هسته اصلی: دوبله زنده با هوش مصنوعی
- **استریم دوطرفه و زنده:** اتصال با کمترین تاخیر ممکن به API جمینای از طریق وب‌سوکت.
- **ضبط صدای داخلی (MediaProjection API):** صدای شفاف را مستقیماً از سیستم اندروید بدون هیچ نویز محیطی ضبط می‌کند.
- **کاهش هوشمند صدای پس‌زمینه (Audio Ducking):** هنگام پخش صدای دوبله، صدای اصلی گوشی (مثل ویدیوی یوتیوب) به صورت خودکار کم می‌شود تا صدای ترجمه را واضح‌تر بشنوید.
- **سرویس پس‌زمینه پایدار:** به برنامه اجازه می‌دهد حتی اگر صفحه را قفل کردید یا به برنامه دیگری رفتید، به صورت مداوم به دوبله ادامه دهد.

### 🎛️ ویجت شناور کنترلی
- **مولتی‌تسکینگ بی‌نقص:** دوبله را مستقیماً از طریق یک ویجت شناور که روی برنامه‌های فعال شما قرار می‌گیرد کنترل (شروع/توقف) کنید. نیازی نیست مدام بین برنامه‌ها جابه‌جا شوید.
- **ویژوالایزر زنده:** ویجت شناور دارای یک موج صوتی زنده است که با فرکانس صدا نوسان می‌کند و به شما نشان می‌دهد که ترجمه به صورت فعال در حال انجام است.

### 🌐 سازگاری با همه برنامه‌ها
محدود به یک برنامه خاص نیست. اگر اپلیکیشنی در گوشی شما صدا پخش کند و اجازه ضبط داخلی بدهد — **الاد می‌تواند آن را دوبله کند**.

### 🏗️ معماری مدرن اندروید
- **۱۰۰٪ کاتلین (Kotlin)**
- طراحی رابط کاربری زیبا، واکنش‌گرا و تاریک با **Jetpack Compose**
- **Clean Architecture** (لایه‌های Core، Data، Presentation)
- استفاده از **Coroutines & Flows** برای استریم داده‌های ناهمگام

---

## 🗺️ پشتیبانی از ۷۸ زبان

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

---

## 🚀 نحوه نصب و اجرا

### پیش‌نیازها
1. **دستگاه اندرویدی:** برای پشتیبانی از ضبط صدای داخلی، باید از اندروید 10.0 (API 29) یا بالاتر استفاده کنید.
2. **کلید API جمینای:** شما به یک کلید API رایگان از [Google AI Studio](https://aistudio.google.com/) نیاز دارید.

### نصب
1. به صفحه [Releases](https://github.com/navidseyedain/alad-mobile/releases) بروید.
2. آخرین نسخه `app-debug.apk` را دانلود کنید.
3. فایل APK را روی گوشی اندرویدی خود نصب کنید.

### بیلد از سورس‌کد
1. مخزن را کلون کنید: `git clone https://github.com/navidseyedain/alad-mobile.git`
2. پروژه را در **Android Studio** باز کنید.
3. اجازه دهید Gradle پروژه را سینک کند.
4. روی دکمه **Run** کلیک کنید تا روی دستگاه متصل شده نصب شود.

---

## 🔒 حریم خصوصی و امنیت

- **اتصال مستقیم:** این اپلیکیشن به صورت *مستقیم* از گوشی شما به API جمینای گوگل از طریق وب‌سوکت متصل می‌شود. هیچ سرور واسطه‌ای وجود ندارد.
- **ذخیره‌سازی محلی:** کلید API و تنظیمات شما به صورت کاملاً امن و محلی روی گوشی شما با استفاده از Android DataStore ذخیره می‌شود.

---

## 📜 لایسنس

این پروژه تحت لایسنس MIT منتشر شده است - برای جزئیات بیشتر فایل [LICENSE](LICENSE) را ببینید.
