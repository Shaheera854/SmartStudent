# 🎓 SmartStudent — Admin Panel

A Java Swing desktop application for efficiently managing student records.
Includes CRUD operations, search & filter, CSV export, and real-time statistics with a clean admin interface.

## 📖 Table of Contents

  ✨ Features
  
  📸 Screenshots
  
  📂 Project Structure
  
  ⚙️ Dependencies
  
  🚀 How to Run
  
  🗄️ Database
  
  📜 License <br><br>


## ✨ Features
### 🔐 Admin Login

* Secure login dialog for administrators.

* Prevents unauthorized access.

### 👩‍🎓 Student Management

* ➕ Add, ✏️ Edit, ❌ Delete student records.

* Fields: Name, Roll No, Department, Email, Phone, Marks.

### 🔍 Search & Filter

* Filter by Name, Department, Roll No, Marks range.

* One-click to clear filters.

### 🛠️ Toolbar Actions

* Add | Edit | Delete | Refresh | Export CSV | Logout | Show All.

### 📊 Statistics Panel

* Total Students

* Max/Min Marks

* Department-wise count

### 📤 CSV Export

* Exports the current table view.

* Handles commas, quotes, and newlines gracefully.

### 📐 Responsive Layout

* Top panel: Toolbar

* Center panel: Filters & Table

* Bottom panel: Statistics

### 📸 Screenshots

* Login Screen
<img width="322" height="202" alt="image" src="https://github.com/user-attachments/assets/f0c859f0-a4b7-4429-b450-ed340bd2467d" />


* Admin Panel


* Add/Edit Student<br><br>


## 📂 Project Structure
```text
SmartStudent/
│
├── src/
│   ├── Main.java          // Entry point
│   ├── UI.java            // GUI and frontend logic
│   ├── Student.java       // Student model
│   ├── StudentDAO.java    // Database CRUD
│   ├── Stats.java         // Statistics model
│   └── AuthService.java   // Admin authentication
│
├── screenshots/           // Screenshots used above
├── README.md
└── lib/                   // JDBC drivers (optional)
```

## ⚙️ Dependencies

* ☕ Java 8+

* 🗄️ JDBC driver (MySQL, SQLite, etc.)

## 🚀 How to Run
1️⃣ Compile
* javac -d bin src/*.java

2️⃣ Run
* java -cp bin Main

3️⃣ Login

* Use admin credentials configured in AuthService.

4️⃣ Manage

* Add, edit, search, filter, and export student records via the admin panel. <br>

## 🗄️ Database

* StudentDAO manages all database interactions.

* Automatically creates tables on startup.

* Supported methods:
  - getAll()

  - create()

  - update()

  - delete()

  - search()

  - getStats() <br>
## 📜 License

* MIT License © 2025
