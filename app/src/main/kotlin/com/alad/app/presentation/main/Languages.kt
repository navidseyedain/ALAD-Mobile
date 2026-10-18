package com.alad.app.presentation.main

val supportedLanguages = listOf(
    "af" to "🇿🇦 Afrikaans", "sq" to "🇦🇱 Albanian", "am" to "🇪🇹 Amharic", "ar" to "🇸🇦 Arabic",
    "hy" to "🇦🇲 Armenian", "az" to "🇦🇿 Azerbaijani", "eu" to "🇪🇸 Basque", "be" to "🇧🇾 Belarusian",
    "bn" to "🇧🇩 Bengali", "bs" to "🇧🇦 Bosnian", "bg" to "🇧🇬 Bulgarian", "my" to "🇲🇲 Burmese",
    "ca" to "🇪🇸 Catalan", "ceb" to "🇵🇭 Cebuano", "ny" to "🇲🇼 Chichewa", "zh" to "🇨🇳 Chinese",
    "co" to "🇫🇷 Corsican", "hr" to "🇭🇷 Croatian", "cs" to "🇨🇿 Czech", "da" to "🇩🇰 Danish",
    "nl" to "🇳🇱 Dutch", "en" to "🇺🇸 English", "eo" to "🌍 Esperanto", "et" to "🇪🇪 Estonian",
    "tl" to "🇵🇭 Filipino", "fi" to "🇫🇮 Finnish", "fr" to "🇫🇷 French", "gl" to "🇪🇸 Galician",
    "ka" to "🇬🇪 Georgian", "de" to "🇩🇪 German", "el" to "🇬🇷 Greek", "gu" to "🇮🇳 Gujarati",
    "ht" to "🇭🇹 Haitian Creole", "ha" to "🇳🇬 Hausa", "haw" to "🇺🇸 Hawaiian", "iw" to "🇮🇱 Hebrew",
    "hi" to "🇮🇳 Hindi", "hmn" to "🇱🇦 Hmong", "hu" to "🇭🇺 Hungarian", "is" to "🇮🇸 Icelandic",
    "ig" to "🇳🇬 Igbo", "id" to "🇮🇩 Indonesian", "ga" to "🇮🇪 Irish", "it" to "🇮🇹 Italian",
    "ja" to "🇯🇵 Japanese", "jw" to "🇮🇩 Javanese", "kn" to "🇮🇳 Kannada", "kk" to "🇰🇿 Kazakh",
    "km" to "🇰🇭 Khmer", "rw" to "🇷🇼 Kinyarwanda", "ko" to "🇰🇷 Korean", "ku" to "🇹🇯 Kurdish",
    "ky" to "🇰🇬 Kyrgyz", "lo" to "🇱🇦 Lao", "la" to "🇻🇦 Latin", "lv" to "🇱🇻 Latvian",
    "lt" to "🇱🇹 Lithuanian", "lb" to "🇱🇺 Luxembourgish", "mk" to "🇲🇰 Macedonian", "mg" to "🇲🇬 Malagasy",
    "ms" to "🇲🇾 Malay", "ml" to "🇮🇳 Malayalam", "mt" to "🇲🇹 Maltese", "mi" to "🇳🇿 Maori",
    "mr" to "🇮🇳 Marathi", "mn" to "🇲🇳 Mongolian", "ne" to "🇳🇵 Nepali", "no" to "🇳🇴 Norwegian",
    "or" to "🇮🇳 Odia", "ps" to "🇦🇫 Pashto", "fa" to "🇮🇷 Persian", "pl" to "🇵🇱 Polish",
    "pt" to "🇵🇹 Portuguese", "pa" to "🇮🇳 Punjabi", "ro" to "🇷🇴 Romanian", "ru" to "🇷🇺 Russian",
    "sm" to "🇼🇸 Samoan", "gd" to "🏴󠁧󠁢󠁳󠁣󠁴󠁿 Scots Gaelic", "sr" to "🇷🇸 Serbian",
    "sn" to "🇿🇼 Shona", "sd" to "🇵🇰 Sindhi", "si" to "🇱🇰 Sinhala", "sk" to "🇸🇰 Slovak",
    "sl" to "🇸🇮 Slovenian", "so" to "🇸🇴 Somali", "st" to "🇿🇦 Southern Sotho", "es" to "🇪🇸 Spanish",
    "su" to "🇮🇩 Sundanese", "sw" to "🇹🇿 Swahili", "sv" to "🇸🇪 Swedish", "tg" to "🇹🇯 Tajik",
    "ta" to "🇮🇳 Tamil", "tt" to "🇷🇺 Tatar", "te" to "🇮🇳 Telugu", "th" to "🇹🇭 Thai",
    "tr" to "🇹🇷 Turkish", "tk" to "🇹🇲 Turkmen", "uk" to "🇺🇦 Ukrainian", "ur" to "🇵🇰 Urdu",
    "ug" to "🇨🇳 Uyghur", "uz" to "🇺🇿 Uzbek", "vi" to "🇻🇳 Vietnamese", "cy" to "🏴󠁧󠁢󠁷󠁬󠁳󠁿 Welsh",
    "xh" to "🇿🇦 Xhosa", "yi" to "🇮🇱 Yiddish", "yo" to "🇳🇬 Yoruba", "zu" to "🇿🇦 Zulu"
)

data class DialectOption(
    val code: String,
    val displayName: String,
    val promptInstruction: String
)

val supportedDialects: Map<String, List<DialectOption>> = mapOf(
    "ar" to listOf(
        DialectOption("ar-STANDARD", "🌍 Modern Standard (فصحی)", "Speak in clear Modern Standard Arabic (Fusha)"),
        DialectOption("ar-EG", "🇪🇬 Egyptian (مصری)", "Speak and translate using everyday Egyptian Arabic dialect (Masri) and local idioms"),
        DialectOption("ar-SA", "🇸🇦 Gulf / Saudi (خلیجی / سعودی)", "Speak and translate using Gulf / Saudi Arabian dialect (Khaliji)"),
        DialectOption("ar-MA", "🇲🇦 Moroccan (مراکشی / داریجة)", "Speak and translate using Moroccan Arabic (Darija)"),
        DialectOption("ar-DZ", "🇩🇿 Algerian (الجزایری)", "Speak and translate using Algerian Arabic dialect (Derja)"),
        DialectOption("ar-SY", "🇸🇾 Levantine (شامی)", "Speak and translate using natural Levantine Arabic dialect (Shami/Syrian/Lebanese)")
    ),
    "en" to listOf(
        DialectOption("en-US", "🇺🇸 American", "Speak in natural General American English pronunciation and vocabulary"),
        DialectOption("en-GB", "🇬🇧 British", "Speak in British English (Received Pronunciation)"),
        DialectOption("en-AU", "🇦🇺 Australian", "Speak in natural Australian English accent and terminology"),
        DialectOption("en-IN", "🇮🇳 Indian", "Speak in clear Indian English accent")
    ),
    "es" to listOf(
        DialectOption("es-419", "🌎 Latin American (Neutral)", "Speak in neutral Latin American Spanish (Español neutro)"),
        DialectOption("es-ES", "🇪🇸 Spain (Castilian)", "Speak in Castilian Spanish with Spain pronunciation (distinción)"),
        DialectOption("es-MX", "🇲🇽 Mexican", "Speak in natural Mexican Spanish dialect and vocabulary"),
        DialectOption("es-AR", "🇦🇷 Argentine (Rioplatense)", "Speak in Argentine Rioplatense Spanish with voseo")
    ),
    "fa" to listOf(
        DialectOption("fa-IR", "🇮🇷 Standard Persian (تهرانی معیار)", "Speak in standard, natural conversational Iranian Persian (Tehran standard)"),
        DialectOption("fa-AF", "🇦🇫 Dari (دری افغانستان)", "Speak in authentic Dari Persian dialect (Afghan Persian)"),
        DialectOption("tg-TJ", "🇹🇯 Tajiki (تاجیکی)", "Speak in Tajiki Persian phrasing")
    ),
    "fr" to listOf(
        DialectOption("fr-FR", "🇫🇷 France (Standard)", "Speak in standard metropolitan French pronunciation"),
        DialectOption("fr-CA", "🇨🇦 Canadian (Québécois)", "Speak in Quebec Canadian French accent and vocabulary")
    ),
    "pt" to listOf(
        DialectOption("pt-BR", "🇧🇷 Brazilian", "Speak in natural Brazilian Portuguese (Português do Brasil)"),
        DialectOption("pt-PT", "🇵🇹 European", "Speak in European Portuguese (Português de Portugal)")
    ),
    "de" to listOf(
        DialectOption("de-DE", "🇩🇪 Standard German", "Speak in Standard German (Hochdeutsch)"),
        DialectOption("de-AT", "🇦🇹 Austrian", "Speak in Austrian German phrasing")
    )
)
