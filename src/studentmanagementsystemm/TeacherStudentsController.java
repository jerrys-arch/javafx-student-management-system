package studentmanagementsystemm;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherStudentsController {

    @FXML
    private TableView<Student> studentsTable;

    @FXML
    private TableColumn<Student, Integer> studentIdCol;

    @FXML
    private TableColumn<Student, String> studentNameCol;

    @FXML
    private TableColumn<Student, String> courseNameCol;

    private ObservableList<Student> studentsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set cell value factories for each column
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        // Load students data for the logged-in teacher
        loadStudents();
    }

    private void loadStudents() {
        // SQL query to fetch students for the logged-in teacher
        String query = "SELECT u.id AS student_id, u.username AS student_name, c.course_name " +
               "FROM users u " +
               "JOIN enrollments ce ON u.id = ce.student_id " +
               "JOIN courses c ON ce.course_id = c.id " +
               "WHERE c.teacher_id = ? AND u.role = 'student'";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            // Set the teacher's ID in the query
            preparedStatement.setInt(1, FXMLDocumentController.loggedInUserId);  // loggedInUserId should contain the teacher's ID
            ResultSet resultSet = preparedStatement.executeQuery();

            studentsList.clear(); // Clear previous data
            while (resultSet.next()) {
                // Create student objects and add to the list
                int studentId = resultSet.getInt("student_id");
                String studentName = resultSet.getString("student_name");
                String courseName = resultSet.getString("course_name");

                studentsList.add(new Student(studentId, studentName, courseName));
            }

            // Set the table items
            studentsTable.setItems(studentsList);

        } catch (Exception e) {
            // Show error alert if there's an issue with database query
            showAlert("Error", "Error loading students: " + e.getMessage(), AlertType.ERROR);
        }
    }

    // Utility method to show alerts
    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class to hold student data
    public static class Student {
        private final int studentId;
        private final String studentName;
        private final String courseName;

        public Student(int studentId, String studentName, String courseName) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.courseName = courseName;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getCourseName() {
            return courseName;
        }
    }
}
