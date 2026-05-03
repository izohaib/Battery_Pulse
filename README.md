# Battery Pulse

> A professional Android app that turns your lock screen into a real-time battery monitor — firing a full-screen charging display the moment you plug in.

[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3.20-blue.svg)](https://kotlinlang.org)

---

## Demo

https://github.com/user-attachments/assets/516b91ab-fa01-497f-a9d5-9061ee49cd09

---

## What is Battery Pulse?

Battery Pulse is an Android app that displays a beautiful always-on charging screen the moment your charger is connected. It shows real-time battery stats — temperature, voltage, wattage, current, and estimated time to full — on a clean full-screen display, so you always know exactly what your battery is doing.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 + (Color Scheme) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Storage | DataStore |
| Background | Foreground Service + BroadcastReceiver |
| Lock Screen | Full-Screen Intent |
| Navigation | Navigation Compose |
| Async | Coroutines + Flow |

---

## Features

### Full-Screen Charging Display
- Fires automatically when charger is connected
- Shows a live speedometer with battery percentage
- Displays time, date, and real-time battery stats
- Always-on Display (AoD) mode keeps the screen active while charging
- Screen auto-dims and hides after configurable inactivity timeouts
- Long-press actions for torch and camera directly from the charging screen

### Battery Stats
- Temperature (°C)
- Voltage (mV)
- Wattage (W)
- Current (mA)
- Estimated time to full charge

### Display Customization
- Toggle individual stats on or off
- Hide or show the clock
- 12-hour or 24-hour time format
- Multiple date format options
- Configurable dim and hide timings

### App Usage *(in progress)*
- Per-app screen time tracking
- Usage breakdown by day

### Settings *(in progress)*
- Theme and appearance preferences
- Notification and behavior controls

### Charging History *(in progress)*
- Log of past charging sessions
- Duration, start/end percentage, and session stats

---

## Architecture & Technical Implementation

Battery Pulse is built with a clean **MVVM architecture** using **Jetpack Compose** for the UI, and relies on several Android system components to deliver its core experience.

### Broadcast Receivers
The app registers a `BroadcastReceiver` that listens for:
- `ACTION_POWER_CONNECTED` — triggers the full-screen charging display
- `ACTION_POWER_DISCONNECTED` — dismisses the display
- `ACTION_BATTERY_CHANGED` — streams live battery data (percent, voltage, temperature, current, wattage)

This allows the app to react instantly to charger events even when the app is not open.

### Foreground Service
A persistent **Foreground Service** keeps battery monitoring alive in the background. It:
- Holds a wake lock to ensure the display stays active while charging
- Continuously reads battery data and pushes updates to the UI
- Shows a persistent notification as required by Android for foreground services
- Survives app backgrounding and device sleep

### Full-Screen Intent
When the charger is connected, the app fires a **Full-Screen Intent** which launches `ChargingDisplayActivity` directly on the lock screen — even if the device is locked or the screen is off. This is the same mechanism used by alarm and call apps to appear over the lock screen.

The activity:
- Runs with `FLAG_KEEP_SCREEN_ON` to prevent the screen turning off
- Uses `setShowWhenLocked` and `setTurnScreenOn` for lock screen display
- Manages screen brightness programmatically — dims after inactivity, restores on touch

### Dependency Injection
Built with **Hilt** for clean dependency injection across ViewModels, Services, and Repositories.

### Data Persistence
User preferences and display settings are stored with **DataStore**, providing a reactive, coroutine-friendly settings layer.

---

## Run

Clone the repo, open in Android Studio, run on a physical device — battery APIs don't work on emulators.

> Active development — App Usage, Settings and Charging History coming soon.
