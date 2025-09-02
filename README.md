SmartStudent — Admin Panel

Overview

  SmartStudent is a Java Swing desktop application for managing student records efficiently. It provides CRUD operations, search and filter functionality, CSV export, and summary statistics.
  
  The backend is handled via StudentDAO interacting with a relational database, while the frontend uses Java Swing for a responsive GUI.

Features

  Admin Login
  
  Secure login dialog for administrators.
  
  Prevents unauthorized access.

Student Management

Add, Edit, Delete student records.

Fields: Name, Roll No, Department, Email, Phone, Marks.

Search & Filter

Filter by Name, Department, Roll No, Marks range.

Clear filters with one click.

Toolbar Actions

Add, Edit, Delete, Refresh, Export CSV, Logout, Show All.

Statistics Panel

Displays Total Students, Max/Min Marks, Department-wise count.

CSV Export

Exports the current table view.

Handles commas, quotes, and newlines.

Responsive Layout

Top panel with toolbar.

Center panel with filters and table.

Bottom panel with statistics.

Screenshots

Login Screen:


Admin Panel:


Add/Edit Student:


Project Structure
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
├── README.md
├── screenshots/           // Screenshots used above
└── lib/                   // Optional: JDBC drivers

Dependencies

Java 8+

JDBC driver (MySQL, SQLite, etc.)

NumberFormat (built-in) for formatting marks.

How to Run

Compile:

javac -d bin src/*.java


Run:

java -cp bin Main


Login with admin credentials configured in AuthService.

Use the panel to manage student records.

Database

StudentDAO manages database interaction.

Ensures table creation on startup.

Supports methods: getAll(), create(), update(), delete(), search(), getStats().
