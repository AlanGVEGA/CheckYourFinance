# CheckYourFinance

## Short description

**CheckYourFinance** is a native Android app for personal finance. Users can **register**, **log in**, and **manage expenses** on their device. The app includes a **dashboard**, a **transaction list**, and full **create, read, update, and delete (CRUD)** support for expenses. All data is stored **locally** on the phone or tablet—there is no cloud server in this version.

## Purpose of the project

This project was built as an **academic Android application** to practice and demonstrate common mobile development topics:

- **Login and registration** with local user storage  
- **CRUD operations** on a domain model (expenses)  
- **Local persistence** so data survives when the app is closed  
- **Room** as a structured local database  
- **Light separation of concerns** (activities, repository, data layer)  
- **User interface and experience** using **Kotlin** and **XML layouts**

It is intended for classmates, instructors, and anyone learning how a small Android app can combine UI, storage, and session handling without a backend.

## Main features

- ✓ **Local registration and login** (users stored on the device)  
- ✓ **Session persistence** with SharedPreferences (stay logged in after reopening the app)  
- ✓ **Dashboard** with a financial overview and shortcuts to recent activity  
- ✓ **Expense list** with **category filters** (chips to narrow what you see)  
- ✓ **Create expense** (add a new transaction)  
- ✓ **Edit expense** (update an existing transaction)  
- ✓ **View expense detail** (full fields, favorite, share)  
- ✓ **Delete expense**  
- ✓ **Mark expenses as favorites** (stored in Room; visible in list and detail)  
- ✓ **Share expense** (Android share sheet with a text summary)  
- ✓ **Local Room database** for users and expenses  
- ✓ **Guest mode** with **sample data** only (demo browsing; saving real data requires signing in)  
- ✓ **No external backend or Firebase** in this project

## App screens

### Login / Registration (`MainActivity`)

The first screen lets you **sign in** with email and password, **create a new account**, or **continue as a guest**. Registered users are saved in Room. Guest mode shows sample transactions for exploration but does not persist your own expenses to the database.

### Dashboard (`DashboardActivity`)

After login, this screen acts as the **home hub**: greeting, summary-style cards, and **recent transactions** loaded from Room for signed-in users. Navigation leads to the full expense list and other flows.

### Expense List (`ExpenseListActivity`)

Shows **all expenses** for the current user in a scrollable list (**RecyclerView**). You can **filter by category type**, open an item for detail, or start **add** / **edit** flows. Guests see the sample list instead.

### Create / Edit Expense (`ExpenseFormActivity`)

A **form** to enter title, amount, category, date, and description. The same screen is used for **new** expenses and **editing** existing ones. Saving is tied to a logged-in user in Room.

### Expense Detail (`ExpenseDetailActivity`)

Shows one expense in full. From here you can **toggle favorite**, **share** a text summary, **edit**, or **delete** (with behavior adjusted for guest vs. signed-in user).

## Technologies used

| Technology | Role in this project |
|------------|----------------------|
| **Kotlin** | Main programming language for app logic and Android APIs |
| **XML layouts** | Declarative UI for each screen and list rows |
| **Android Studio** | Recommended IDE to open, build, and run the project |
| **Room** | Local SQLite database with compile-time checked queries |
| **SharedPreferences** | Lightweight key–value storage for the logged-in session |
| **RecyclerView** | Efficient scrolling list for many expense rows |
| **Material Components** | Material Design widgets (buttons, chips, theming) |
| **Gradle** | Build system; this repo uses Kotlin DSL (`build.gradle.kts`) |

The project uses the **Android Gradle Plugin** with **KSP** for Room code generation. See `gradle.properties` for any project-specific Gradle flags.

## Project structure

Understanding a few folders is enough to navigate the codebase.

### `app/src/main/java/com/example/checkyourfinance/`

| File | What it does |
|------|----------------|
| `MainActivity.kt` | Login, registration, guest entry |
| `DashboardActivity.kt` | Financial overview / home after sign-in |
| `ExpenseListActivity.kt` | List of expenses with filters and RecyclerView |
| `ExpenseFormActivity.kt` | Create or edit an expense |
| `ExpenseDetailActivity.kt` | Single expense: favorite, share, edit, delete |
| `ExpenseAdapter.kt` | Binds expense rows to the RecyclerView |
| `ExpenseUiModel.kt` | Plain data class used by the UI layer |
| `ExpenseSampleData.kt` | Static sample expenses for guest/demo mode |
| `CheckYourFinanceApplication.kt` | Application class: sets up database and repository |
| `ExpenseCategoryPicker.kt` | Helper UI for choosing expense categories |
| `ExpenseCategories.kt` | Category constants used by filters and forms |

### `data/model/`

| File | What it does |
|------|----------------|
| `UserEntity.kt` | Room **table** definition for users |
| `ExpenseEntity.kt` | Room **table** definition for expenses |

### `data/local/`

| File | What it does |
|------|----------------|
| `AppDatabase.kt` | Room database configuration |
| `UserDao.kt` | Queries for users (insert, lookup by email, etc.) |
| `ExpenseDao.kt` | Queries for expenses (CRUD, favorites) |
| `PasswordHasher.kt` | Educational SHA-256 hashing helper for passwords |

### `data/repository/`

| File | What it does |
|------|----------------|
| `FinanceRepository.kt` | Single place the UI talks to for user and expense operations; wraps DAOs and maps entities to `ExpenseUiModel` |

### `session/`

| File | What it does |
|------|----------------|
| `SessionManager.kt` | Saves and clears the logged-in user id and profile fields using SharedPreferences |

### `app/src/main/res/layout/`

XML files define **structure and widgets** for each screen (for example `activity_main.xml`, `activity_dashboard.xml`). List rows live in layouts like `item_expense.xml`.

### `app/src/main/res/drawable/`

Vector icons, card backgrounds, rounded shapes, and other **visual assets** referenced from XML or Kotlin.

### `app/src/main/res/values/`

| Resource file | Purpose |
|---------------|---------|
| `strings.xml` | All user-visible text (easier translation and consistency) |
| `colors.xml` | Color palette |
| `dimens.xml` | Reusable sizes (padding, margins, text sizes) |
| `themes.xml` | App-wide Material theme (light; see `values-night/` for dark) |

## Local database explanation

**Room** is an Android library from Google that sits on top of **SQLite**. It lets you define **tables** as Kotlin classes (**entities**), write **queries** in DAO interfaces, and get **compile-time checks** instead of only discovering SQL mistakes at runtime.

In this app, Room stores everything **on the device** in two main tables: **`users`** and **`expenses`**.

### User schema (`UserEntity` → table `users`)

| Field | Meaning |
|-------|---------|
| `id` | Unique primary key (auto-generated) |
| `name` | Display name |
| `email` | Login email (unique in the database) |
| `passwordHash` | Stored hash, not the raw password |
| `createdAt` | When the account was created (timestamp) |

### Expense schema (`ExpenseEntity` → table `expenses`)

| Field | Meaning |
|-------|---------|
| `id` | Unique primary key (auto-generated) |
| `userId` | Which user owns this expense |
| `title` | Short name of the expense |
| `amount` | Numeric amount |
| `category` | Human-readable category label |
| `categoryType` | Stable type key used for filtering (e.g. food, bills) |
| `date` | Date string shown in the UI |
| `description` | Optional longer notes |
| `isFavorite` | Whether the user starred this expense |
| `createdAt` | When the row was created (timestamp) |

## Authentication explanation

- **Users** are stored **locally** in Room when they register.  
- **Passwords** are not stored in plain text. They are passed through **`PasswordHasher`**, which applies **SHA-256** for **learning purposes only**.  
- After a successful login, **`SessionManager`** writes the session (for example user id and profile fields) to **SharedPreferences** so the app can restore the session on next launch.  
- **Logout** clears that session data.  

This is **local-only authentication**. It is **not** the same as production-grade security (no salt/pepper, no server-side verification, no OAuth). **Firebase Authentication** and other cloud backends are **not** used in this repository.

## CRUD explanation

**CRUD** is a standard way to talk about data operations:

| Letter | Meaning | In this app |
|--------|---------|-------------|
| **C**reate | Add new data | Add a new expense from the form |
| **R**ead | View data | List and detail screens load expenses from Room (or sample data for guests) |
| **U**pdate | Change existing data | Edit expense; toggle favorite (updates Room) |
| **D**elete | Remove data | Delete from detail (and list reflects the change) |

## How to run locally

### Requirements

- **Android Studio** (recent version compatible with the project’s Gradle/AGP)  
- **JDK** compatible with the Android Gradle Plugin in this repo  
- **Android SDK** installed (via Android Studio SDK Manager)  
- An **Android emulator** or a **physical device** with USB debugging (if you deploy from Studio)  
- **Git** (to clone the repository)

### Steps

1. **Clone the repository**

```bash
git clone https://github.com/AlanGVEGA/CheckYourFinance.git
```

2. **Open** the `CheckYourFinance` folder in **Android Studio** (Open, not Import, is usually fine for Gradle projects).

3. Wait for **Gradle sync** to finish. If prompted, accept SDK or plugin updates Studio suggests for this project.

4. **Build** a debug APK from the project root:

```bash
./gradlew assembleDebug
```

5. **Run** the app:
   - Click the green **Run** button in Android Studio and pick an emulator or device, **or**  
   - Install the built APK on a device from Studio’s Build output.

6. **Optional — install from terminal** (device connected and authorized):

```bash
./gradlew installDebug
```

## Demo flow for evaluator

Use this sequence for a quick **end-to-end** check of registration, session, Room CRUD, and UI updates:

1. Open the app.  
2. **Register** a new user (unique email; follow any password rules shown in the app).  
3. **Log out** from the profile / session control in the app.  
4. **Log in** again with the same credentials.  
5. Open the **dashboard** and confirm you are recognized as the signed-in user.  
6. Go to **transactions** (expense list).  
7. **Create** a new expense and confirm it appears in the list.  
8. Open its **detail** screen.  
9. **Toggle favorite** and confirm the UI (and list, if you navigate back) reflects it.  
10. **Edit** the expense and confirm fields update.  
11. **Delete** the expense from detail.  
12. Return to the list and confirm the row is **gone** or the empty state appears as expected.

Optionally try **Continue as guest** to see **sample data** and confirm that persisting real expenses still requires signing in.

## Important notes

- **Data is local** to the device. It does not sync to the cloud in this project.  
- **Uninstalling** the app typically **removes** the app’s private storage, including the Room database.  
- **Guest mode** uses **`ExpenseSampleData`** only; it is for **demo / exploration**, not for saving your own expenses to Room.  
- **SHA-256 without salt** is **educational only**; real apps should use vetted password APIs (for example **bcrypt** with salt on a server, or a managed identity provider).  
- **Schema changes** may use **destructive migration** (`fallbackToDestructiveMigration`) for simplicity in coursework—**do not rely on that for production**; add proper **migrations** before a real release.

## Current limitations / future improvements

- Introduce **ViewModels** and **LiveData** or **Kotlin Flow** for cleaner lifecycle-aware state  
- Replace plain SHA-256 with **salted** password handling or delegate auth to a secure service  
- Add real **Room migrations** instead of wiping data on schema changes  
- Add **charts** or richer analytics on the dashboard  
- **Budget goals** and alerts  
- **Export** expenses (CSV/PDF)  
- Optional **cloud sync** or **Firebase** later  
- Expand **unit and UI tests**

## Author

Developed by **Alan Gabriel Vega Tinajaca**.

## Academic context

This repository is part of a **native Android** coursework-style project. It demonstrates **local login**, **expense CRUD**, **Room persistence**, **SharedPreferences sessions**, and **Material-style UI** built with **Kotlin** and **XML**—a practical baseline for learning Android before adding networking, advanced architecture, or production hardening.
