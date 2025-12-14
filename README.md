🎓 Student Management System (JavaFX)

A role-based Student Management System built with JavaFX and MySQL, designed to manage courses, enrollments, attendance, and grades for Admins, Teachers, and Students.

This project demonstrates clean UI design, MVC architecture, database integration, and real-world academic workflows.

📌 Features
👤 Admin

Assign courses to teachers

Enroll students in courses

View attendance reports

View grade reports

Dynamic content loading inside admin dashboard

Secure logout

👨‍🏫 Teacher

View enrolled students

Mark student attendance

Assign grades to students

🎓 Student

View enrolled courses

View grades

Track attendance history

🛠️ Technologies Used
Technology	Purpose
Java (JDK 17+)	Core programming language
JavaFX	Desktop UI framework
FXML	UI layout definition
MySQL	Database
JDBC	Database connectivity
CSS (inline)	UI styling
Scene Builder	FXML design (optional)
🧱 Project Architecture
studentmanagementsystemm/
│
├── controllers/
│   ├── AdminController.java
│   ├── AssignUserController.java
│   ├── StudentGradesController.java
│   ├── StudentAttendanceController.java
│   └── TeacherDashboardController.java
│
├── models/
│   ├── User.java
│   ├── Course.java
│   ├── Enrollment.java
│   ├── Grade.java
│   └── Attendance.java
│
├── fxml/
│   ├── admin_dashboard.fxml
│   ├── assign_user.fxml
│   ├── student_grades.fxml
│   ├── student_attendance.fxml
│   └── teacher_dashboard.fxml
│
├── database/
│   └── DatabaseConnection.java
│
└── Main.java

🗄️ Database Schema (Overview)

users (id, username, password, role, email)

courses (id, course_code, course_name, teacher_id)

enrollments (id, student_id, course_id)

grades (id, student_id, course_id, grade)

attendance (id, student_id, course_id, date, status)

🔐 Authentication & Roles

Users log in using username and password

Role-based redirection:

Admin → Admin Dashboard

Teacher → Teacher Dashboard

Student → Student Dashboard

Logged-in user ID is tracked for personalized data retrieval

🎨 UI Design

Modern card-style UI

Gradient backgrounds

Rounded panels and buttons

Dynamic content loading using AnchorPane

Consistent layout across all dashboards

🚀 How to Run the Project
1️⃣ Prerequisites

Java JDK 17 or higher

MySQL Server

JavaFX SDK

IDE (IntelliJ IDEA recommended)

2️⃣ Database Setup

Create the database and tables using the provided schema

Update database credentials in:

DatabaseConnection.java

3️⃣ Run the Application

Open the project in your IDE

Run Main.java

Login with a valid user account



📚 Learning Outcomes

JavaFX & FXML integration

MVC architecture in desktop applications

JDBC database operations

Role-based access control

Clean UI/UX design

Real-world academic system modeling


🔮 Future Improvements

Password hashing

Role-based permissions enforcement

Export reports (PDF/Excel)

Search and filter functionality

Pagination for large datasets

Dark mode UI
