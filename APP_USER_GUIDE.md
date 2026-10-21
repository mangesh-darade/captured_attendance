# 📱 ElintOm@ — Smart Face Attendance Kiosk App Guide

A comprehensive operational and architectural manual for the **ElintOm@** Android Face Recognition & Geofenced Attendance Terminal.

---

## 📌 1. Overview & Capabilities

**ElintOm@** transforms standard Android tablets or smartphones into dedicated, contactless attendance kiosks for stores, offices, and warehouses.

### Key Highlights:
- **Contactless Face Capture**: Real-time face detection with smart oval guidance overlay and instant match.
- **Dynamic 4-Language System**: Modular architecture supporting **English (`en`)**, **Marathi (`mr`)**, **Hindi (`hi`)**, and **Gujarati (`gu`)**.
- **Haversine Geofencing**: Validates physical presence within the configured store/warehouse radius.
- **Zero-Downtime Offline Punching**: Automatic local storage in encrypted SQLite when internet is down; automatically syncs when connectivity returns.
- **Diagnostics & Audit Trail**: Real-time hardware health checks (GPS accuracy, latency, battery status, and device logs).

---

## 🚀 2. Device Onboarding & Setup Flow

When opening the app for the first time or after clearing data, the terminal connects to the merchant's dedicated POS environment.

### Step-by-Step Setup:
1. **Enter Merchant Setup Code**:
   - Input the store's unique 3–64 character alphanumeric code (e.g., `LH022` or `attendance`).
   - Or tap **"Scan Setup QR Code"** to read the setup token from the POS Web Settings page.
2. **Store Connection**:
   - Tap **"Connect & Activate Terminal"**.
   - The app sends a request to the central SaaS directory (`/attendance_api/setup`).
   - Validates active merchant subscription, resolves the tenant shop URL, and saves server credentials securely.
3. **Staff Phone Authentication**:
   - The terminal prompts for the registered supervisor or kiosk mobile number.
   - Upon verification, the terminal downloads the tenant's registered staff face recognition embeddings and site geofence coordinates.

---

## 👤 3. Face Attendance Kiosk Screen

The primary terminal interface designed for high-throughput, seamless attendance marking by staff members.

### Screen Components & Controls:

| Component | Description |
| :--- | :--- |
| **Language Switcher** | Top pills `[ EN | MR | HI | GU ]`. Switches language instantly across all UI texts, voice prompts, and status messages without restarting the kiosk. |
| **Site & Geofence Badge** | Displays current location name (`Main Warehouse`) and real-time distance from perimeter (`GPS Active • Within 15m`). |
| **Face Guide HUD** | Intelligent visual oval frame that changes color dynamically based on scanning conditions. |
| **Punch Mode Buttons** | Large distinct toggle buttons for **`CHECK IN`** (Green) and **`CHECK OUT`** (Grey/Teal). |
| **Live Status Bar** | Shows time, date, battery level, network connection state, and diagnostics access. |

### Smart Face Guide HUD States:
- **White Dashed Oval**: Looking for face... / चेहरा शोधत आहे... / ચહેરો શોધી રહ્યાં છીએ...
- **Glowing Green Oval**: Face Detected Successfully / चेहरा यशस्वीपणे ओळखला / ચહેરો સફળતાપૂર્વક ઓળખાયો
- **Red Glowing Oval**: Multiple faces detected! / एकापेक्षा जास्त चेहरे / એક કરતાં વધુ ચહેરા
- **Amber Alert Oval**: Hold still for better accuracy / स्थिर राहा / સ્થિર રહો

---

## 🌐 4. Modular Language System (`lang/` Architecture)

The app utilizes a decoupled multi-language module structure. All translations are maintained in dedicated directories under `com.example.lang`:

```
app/src/main/java/com/example/lang/
├── AppStrings.kt                       # Base contract interface
├── LangManager.kt                      # Dynamic resolver
├── english/EnglishStrings.kt           # English translations & mappings
├── marathi/MarathiStrings.kt           # Marathi (मराठी) translations & mappings
├── hindi/HindiStrings.kt               # Hindi (हिंदी) translations & mappings
└── gujrati/GujratiStrings.kt           # Gujarati (ગુજરાતી) translations & mappings
```

### Adding a New Language:
To add any future language (e.g. Kannada, Tamil, Bengali):
1. Create a folder `app/src/main/java/com/example/lang/<language>/`.
2. Implement `AppStrings` in `<Language>Strings.kt`.
3. Register the code in `LangManager.kt`.
*No UI code or screen logic needs to be touched.*

---

## 📍 5. Geofencing & Perimeter Validation

To prevent proxy punching or off-site attendance fraud:
1. Every punch captures real-time high-accuracy GPS coordinates (`latitude`, `longitude`).
2. The server and local engine calculate the **Haversine Distance**:
   $$d = 2R \cdot \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta \varphi}{2}\right) + \cos(\varphi_1)\cos(\varphi_2)\sin^2\left(\frac{\Delta \lambda}{2}\right)}\right)$$
3. If $d \le \text{Allowed Warehouse Radius}$ (e.g. $50\text{m}$), attendance is registered.
4. If $d > \text{Allowed Warehouse Radius}$, the punch is rejected with the message:
   - *EN*: "You are outside the registered office/site perimeter."
   - *MR*: "तुम्ही नोंदणीकृत ऑफिस / साईटच्या परिसराबाहेर आहात."
   - *HI*: "आप पंजीकृत कार्यालय/साइट की सीमा से बाहर हैं।"
   - *GU*: "તમે રજિસ્ટર્ડ ઓફિસ / સાઇટ પરિસરની બહાર છો."

---

## 💾 6. Offline Mode & Cloud Sync Diagnostics

When internet drops (e.g. power cuts, weak cellular signal in basements or remote warehouses), the kiosk continues operating uninterrupted.

### Offline Storage & Sync Workflow:
1. **Local Encryption**: Unsent attendance records are encrypted and stored in local device storage with high-precision timestamps.
2. **Network Listener**: Background Android worker continuously checks network status.
3. **Auto-Flush**: Once connectivity is restored, the queue is synced via `/attendance_api/sync_offline` in batches.
4. **Diagnostics Dialog**:
   - Displays count of pending local records (e.g. `3 Records Cached Locally`).
   - Geofence pass/fail status and GPS distance meter.
   - Manual **"Sync Pending Punches Now"** override button.
   - Remote error log synchronization to Web POS backend.

---

## 🔌 7. Backend API Summary

The terminal communicates with the following endpoints (available in `Capture_Attendance_API.postman_collection.json`):

| # | Endpoint | Method | Purpose |
| :-: | :--- | :-: | :--- |
| **01** | `/attendance_api/init` | `GET` | Health check & system time sync |
| **02** | `/attendance_api/setup` | `POST` | Merchant code pairing & tenant resolution |
| **03** | `/attendance_api/login` | `POST` | Supervisor/Kiosk authentication |
| **04** | `/attendance_api/captured` | `GET` | Fetch staff master face descriptors & geofence rules |
| **05** | `/attendance_api/store` | `POST` | Live punch submission (Face vector + GPS + IN/OUT) |
| **06** | `/attendance_api/sync_offline` | `POST` | Batch upload of locally queued offline records |
| **07** | `/attendance_api/sync_logs` | `POST` | Remote device hardware & error logging |

---

## 🛠️ 8. Troubleshooting & FAQs

- **"Face not recognized. Tap Retake."**: Ensure proper front-facing lighting; avoid strong backlights. Make sure only one person is in front of the camera.
- **"GPS is turned off / Location permission required."**: Open Android Settings -> Location -> Turn ON "High Accuracy GPS". Set Location permission to "Allow while using the app".
- **"Device is not ready for connect please contact customer care."**: Merchant plan is expired or Attendance Kiosk access is suspended by Super Admin.
