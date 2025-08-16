# Smart Daily Expense Tracker

## 📱 App Overview
Smart Daily Expense Tracker is an Android app built with **Jetpack Compose** and **MVVM architecture** for small business owners to quickly log, view, and analyze daily expenses. It includes multi‑screen navigation for expense entry, listings, and visual reports with charts, plus user‑friendly features like category chips, live totals, and date picking.

## 🤖 AI Usage Summary
Development followed an **AI‑First** approach using ChatGPT for architecture planning, navigation fixes, UI layout generation, and troubleshooting build/runtime errors. GitHub Copilot assisted with boilerplate Compose code, ViewModel patterns, and form validation routines. AI tools also helped resolve Gradle dependency issues, refactor navigation from type‑safe serialization to string routes, and improve UX design patterns efficiently.

## 📜 Prompt Logs (Key Prompts)
- How to integrate Material3 DatePicker in Compose
- Fix unresolved reference for DateUtils methods like getTodayStart and getSevenDaysAgo
- Resolve illegal escape sequences in regex for decimal validation in Compose TextField
- Fix crash: Serializer for class 'Main' not found in Navigation Compose with Kotlin Serialization
- Replace Kotlin Serialization with string-based navigation for stability
- Implement Expense filtering by selected category with chips
- Add charts (MPAndroidChart / Compose) for expense reports
- How to export expense data to PDF and CSV and share via Android Intent
- Move export features from List screen to Report screen with proper UI placement
- Fix expense total calculation to limit to today using date range queries
- Debug why exported PDF and CSV files contain no data, fix data passing issues
- Add UI for export options with dropdown menu and buttons in Report screen
- Correct date range filtering for expense totals and charts for last 7 days

## ✅ Checklist of Features Implemented
- [x] **MVVM Architecture** with Repository, ViewModel, and StateFlow
- [x] **Jetpack Compose UI** for all screens (XML‑free UI layer)
- [x] **Navigation Compose** multi‑screen setup (Main, Expense Entry, List, Report)
- [x] **Expense Entry Form** with validation, category selection, and optional notes
- [x] **Material 3 Date Picker** for selecting expense date
- [x] **Expense Listing** with filtering, grouping, summary totals, and empty state UI
- [x] **Category Chips** with dynamic color & icon support
- [x] **Expense Reports** with charts (MPAndroidChart integration)
- [x] **Real‑time Daily Total** display on the main screen
- [x] **Add/Delete/Edit Expense** actions wired to repository
- [x] **Material Design Theming** (primary/secondary colors, dark mode)
- [x] **Room Database** for local persistence
- [x] **PDF/CSV Export** with Android Share Intent integration
- [x] Fully resolved **Gradle & dependency setup** (JitPack, Material3, etc.)
- [x] **Improved Navigation Stability** by removing runtime serialization errors

## 📲 APK Download Link
**[Download APK from Google Drive](https://drive.google.com/drive/folders/1pl_oVyy793uNGWUqFZ4HIgiZCX3nyKwe?usp=drive_link)**

## 📄 Resume

You can view and download my professional resume here:  
[Download Resume (PDF)](https://drive.google.com/file/d/1MYzbVaNCLMB6XiTd_G_kFPT877d2jwPI/view?usp=drive_link)

## 📸 Screenshots

### Expense Reports with Analytics
<img width="200" height="400" alt="Screenshot_20250816_142701" src="https://github.com/user-attachments/assets/9f8dfbc2-7717-4d1e-b4be-efa06c51d8f3" /> <img width="200" height="400" alt="Screenshot_20250816_142713" src="https://github.com/user-attachments/assets/738b8459-bfc5-4ffe-931a-0022a5399071" />


View comprehensive expense reports with pie charts showing category-wise breakdown, daily averages, and transaction summaries for the last 7 days.

### Home screen
<img width="200" height="400" alt="Screenshot_20250816_142550" src="https://github.com/user-attachments/assets/7e86f859-257a-41e8-86ff-35fed1958f8b" />


### Expense List Management
<img width="200" height="400" alt="Screenshot_20250816_142647" src="https://github.com/user-attachments/assets/ec36a4c2-0621-4520-b84d-99d6ef9333b8" />


Browse all expenses with filtering options, summary totals, and easy-to-use edit/delete functionality.

### Add New Expenses
<img width="200" height="400" alt="Screenshot_20250816_142630" src="https://github.com/user-attachments/assets/94e7187c-5ba1-4b68-a097-9a7d3b301f63" />


Simple and intuitive expense entry form with category selection, date picker, and validation.

## 🛠️ Technical Stack
- **Frontend**: Jetpack Compose (100% declarative UI)
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Navigation**: Navigation Compose
- **Charts**: MPAndroidChart
- **Export**: PDF/CSV generation with File Provider
- **State Management**: StateFlow & Compose State
- **Dependency Injection**: Hilt
- **UI Design**: Material 3 Design System

## 🚀 Key Features
- **Real-time Analytics**: Live expense tracking with category-wise breakdown
- **Export Functionality**: Generate PDF and CSV reports with share functionality
- **Smart Filtering**: Filter expenses by category with visual chips
- **Date Range Analysis**: View expenses for custom date ranges
- **Responsive UI**: Adaptive layout for different screen sizes
- **Offline Support**: Full functionality without internet connection
