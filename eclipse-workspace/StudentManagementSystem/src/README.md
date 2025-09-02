SmartStudent — Admin Panel
Overview

SmartStudent is a Java Swing-based desktop application designed to help administrators manage student records efficiently. The application provides CRUD functionality, search and filter capabilities, export to CSV, and summary statistics about the student data.

The backend is handled through a StudentDAO class that interacts with a relational database (e.g., MySQL, SQLite), while the frontend is implemented with Java Swing.

Features:

1)Admin Login:

	-> Secure login dialog for administrator authentication.
	
	->Invalid credentials are prevented from accessing the panel.

2)Student Management

	->Add, edit, and delete student records.
	
	->Fields include: Name, Roll No, Department, Email, Phone, and Marks.

3)Search & Filter

	->Filter students by Name, Department, Roll No, or Marks range.
	
	->Supports clearing filters to show all records.

4)Toolbar Actions:

	->Add, Edit, Delete, Refresh records.
	
	->Export student data to CSV.
	
	->Logout and “Show All” buttons to reset filters.

4)Statistics Panel

	->Displays Total Students, Max Marks, Min Marks, and Department-wise student count.

5)CSV Export

	->Exports current table view into a CSV file.

6)Handles commas, quotes, and newlines properly.

	->Responsive GUI

	->Top panel with toolbar.

7)Center panel with filters and table.

	->Bottom panel with stats.

	->Layout adapts to window resizing.

Project Structure:

	SmartStudent/
	│
	├── src/
	│   ├── Main.java          // Entry point
	│   ├── UI.java            // All GUI and frontend logic
	│   ├── Student.java       // Student model class
	│   ├── StudentDAO.java    // Database access and CRUD operations
	│   ├── Stats.java         // Statistics model
	│   └── AuthService.java   // Simple login authentication service
	│
	├── README.md
	└── lib/                   // Optional: JDBC drivers or dependencies

Dependencies:

	Java 8+ (Swing, AWT)
	
	JDBC driver for your database (MySQL, SQLite, etc.)

How to Run:

	Compile the project:
	
	javac -d bin src/*.java
	
	
	Run the application:
	
	java -cp bin Main
	
	
	Login
	
	Enter your admin credentials (configured in AuthService).
	
	Use the Admin Panel
	
	Add, edit, delete, search, and export student data.
	
	View summary statistics at the bottom.
	
Database:
	
	The StudentDAO class manages all database interactions.
	
	It ensures that the required table exists on startup.
	
	Supports getAll(), create(), update(), delete(), search(), and getStats() methods.

Notes:

	The toolbar uses buttons implemented via a JTable for a scrollable, wrap-capable layout.
	
	Filters use JTextField and JFormattedTextField for integer marks.
	
	The app supports window resizing without breaking layout.