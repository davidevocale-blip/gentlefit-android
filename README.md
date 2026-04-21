# 🎙 VoiceTasker

**VoiceTasker** è un'app Android nativa per registrare, trascrivere e organizzare note vocali legate ai tuoi impegni quotidiani.

---

## ✨ Funzionalità

- 🎤 **Registrazione vocale** con visualizzatore waveform in tempo reale
- 📝 **Trascrizione automatica** tramite speech-to-text on-device (italiano)
- 🏷️ **Categorizzazione intelligente** — suggerimento automatico della categoria basato sul contenuto
- 📅 **Calendario integrato** — visualizza i tuoi impegni in un calendario mensile
- ⏰ **Promemoria** — reminder configurabili (1 giorno, 12 ore, 2 ore prima)
- ✏️ **Modifica & Elimina** — gestisci completamente le tue note e categorie
- 💎 **Modello Freemium** — piano gratuito con opzioni Premium

## 🏗 Architettura

- **Linguaggio**: Kotlin 2.0+
- **UI**: Jetpack Compose + Material Design 3
- **Pattern**: MVVM + Clean Architecture
- **Database**: Room
- **DI**: Hilt
- **Background**: WorkManager
- **Navigation**: Compose Navigation

## 📱 Requisiti

- Android 8.0 (API 26) o superiore
- Android Studio Hedgehog o superiore
- JDK 17

## 🚀 Setup

1. Clona il repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/voice-tasker-android.git
   ```
2. Apri il progetto in **Android Studio**
3. Sincronizza Gradle e compila
4. Esegui su un dispositivo o emulatore

## 💰 Piani di Abbonamento

| Piano | Prezzo | Caratteristiche |
|---|---|---|
| **Free** | €0 | 5 note/mese, 1 min max, 3 categorie |
| **Premium Mensile** | €3,99/mese | Tutto illimitato |
| **Premium Annuale** | €29,99/anno | Risparmio 37% |
| **Lifetime** | €49,99 | Una tantum |

## 📁 Struttura Progetto

```
app/src/main/java/com/voicetasker/app/
├── data/           # Room DB, Repository Impl, Recorder
├── di/             # Hilt modules
├── domain/         # Models, Interfaces, Use Cases
├── navigation/     # Compose Navigation
├── ui/             # Theme, Components, Screens
├── worker/         # WorkManager Reminder
├── MainActivity.kt
└── VoiceTaskerApp.kt
```

## 📄 Licenza

Copyright © 2026 VoiceTasker. Tutti i diritti riservati.
