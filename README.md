# Smart Pantry Manager

## Overview
Smart Pantry Manager is a native Android application written in Java designed to reduce household food waste. The app tracks available pantry ingredients and suggests recipes that can be cooked strictly using what the user already has at home—no shopping trip required.

## Key Features
- **Pantry Management**: Full CRUD capabilities to add, view, edit, and delete ingredients with quantities, units, and expiry dates.
- **Strict-Matching Logic**: Algorithmic filtering that ensures a recipe is suggested only if 100% of its required ingredients are present in the user's pantry in sufficient quantity.
- **Pre-seeded Recipe Database**: Includes 15–20 pre-loaded recipes with detailed preparation instructions and ingredient requirements.
- **User Settings**: Configuration options for unit preferences and expiring-soon alerts.
- **Dynamic Navigation**: Clean interface using `RecyclerView` adapters and `BottomNavigationView`.

---

## Database Rationale

**Chosen Database**: SQLite (via standard `SQLiteOpenHelper`)

### Justification
1. **Offline Availability**: Food management should work offline inside kitchens or pantries where internet connections can be unreliable.
2. **Relational Structure**: Fits relational data models seamlessly (foreign key relationships between recipes and required ingredients).
3. **Seeding Support**: `SQLiteOpenHelper` lifecycle methods (`onCreate`) allow straightforward initial data seeding for pre-loaded recipes upon first app installation.
4. **Data Persistence**: Ensures all pantry inventory updates persist directly on-device across application restarts.

---

## Setup & Running Instructions

### Prerequisites
- Android Studio (Jellyfish / Ladybug or newer)
- Java Development Kit (JDK 17+)
- Minimum Android SDK Version: API 24 (Android 7.0)

### How to Run
1. Clone the repository:
   ```bash
   git clone [https://github.com/YOUR_USERNAME/SmartPantryManager.git](https://github.com/GIFTEDZA/SmartPantryManager.git)