package studentmanagementsystemm;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AssignUserController {

    @FXML
    private TextField teacherUsernameField;

    @FXML
    private TextField courseCodeField;

    @FXML
    private TextField studentUsernameField;

    @FXML
    private TextField enrollCourseCodeField;

    private Connection conn;

    public AssignUserController() {
        conn = DatabaseConnection.getConnection();
    }

    @FXML
    private void handleAssignCourse() {
        String teacherUsername = teacherUsernameField.getText().trim();
        String courseCode = courseCodeField.getText().trim();

        if (teacherUsername.isEmpty() || courseCode.isEmpty()) {
            showAlert(AlertType.WARNING, "Both fields are required.");
            return;
        }

        try {
            // Get teacher ID
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND role = 'teacher'");
            stmt.setString(1, teacherUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int teacherId = rs.getInt("id");

                // Update course with teacher ID
                PreparedStatement update = conn.prepareStatement("UPDATE courses SET teacher_id = ? WHERE course_code = ?");
                update.setInt(1, teacherId);
                update.setString(2, courseCode);

                int rows = update.executeUpdate();
                if (rows > 0) {
                    showAlert(AlertType.INFORMATION, "Course assigned to teacher successfully.");
                } else {
                    showAlert(AlertType.ERROR, "Course code not found.");
                }

            } else {
                showAlert(AlertType.ERROR, "Teacher username not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error assigning course.");
        }
    }

    @FXML
    private void handleEnrollStudent() {
        String studentUsername = studentUsernameField.getText().trim();
        String courseCode = enrollCourseCodeField.getText().trim();

        if (studentUsername.isEmpty() || courseCode.isEmpty()) {
            showAlert(AlertType.WARNING, "Both fields are required.");
            return;
        }

        try {
            // Get student ID
            PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE username = ? AND role = 'student'");
            stmt.setString(1, studentUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int studentId = rs.getInt("id");

                // Get course ID
                PreparedStatement courseStmt = conn.prepareStatement("SELECT id FROM courses WHERE course_code = ?");
                courseStmt.setString(1, courseCode);
                ResultSet courseRs = courseStmt.executeQuery();

                if (courseRs.next()) {
                    int courseId = courseRs.getInt("id");

                    // Insert into enrollments table
                    PreparedStatement enroll = conn.prepareStatement("INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)");
                    enroll.setInt(1, studentId);
                    enroll.setInt(2, courseId);
                    enroll.executeUpdate();

                    showAlert(AlertType.INFORMATION, "Student enrolled successfully.");
                } else {
                    showAlert(AlertType.ERROR, "Course code not found.");
                }

            } else {
                showAlert(AlertType.ERROR, "Student username not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error enrolling student.");
        }
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Admin Action");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
