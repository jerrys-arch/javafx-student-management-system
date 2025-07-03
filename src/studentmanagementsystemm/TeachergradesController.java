package studentmanagementsystemm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TeachergradesController {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private TableView<GradeEntry> gradeTable;

    @FXML
    private TableColumn<GradeEntry, Integer> studentIdCol;

    @FXML
    private TableColumn<GradeEntry, String> studentNameCol;

    @FXML
    private TableColumn<GradeEntry, String> gradeCol;

    private ObservableList<GradeEntry> gradeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadCourses();
        setupTable();
        courseComboBox.setOnAction(e -> loadStudentsForSelectedCourse());
    }

    private void loadCourses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT course_code FROM courses WHERE teacher_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, FXMLDocumentController.loggedInUserId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    courseComboBox.getItems().add(rs.getString("course_code"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        gradeCol.setCellFactory(ComboBoxTableCell.forTableColumn("A", "B", "C", "D", "F"));

        gradeCol.setOnEditCommit(event -> {
            GradeEntry entry = event.getRowValue();
            entry.setGrade(event.getNewValue());
        });

        gradeTable.setEditable(true);
    }

    private void loadStudentsForSelectedCourse() {
        gradeList.clear();
        String selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT u.id, u.username, g.grade FROM users u " +
                         "JOIN enrollments e ON u.id = e.student_id " +
                         "JOIN courses c ON c.id = e.course_id " +
                         "LEFT JOIN grades g ON g.student_id = u.id AND g.course_id = c.id " +
                         "WHERE c.course_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, selectedCourse);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int studentId = rs.getInt("id");
                    String studentName = rs.getString("username");
                    String grade = rs.getString("grade");
                    if (grade == null) grade ="";
                    gradeList.add(new GradeEntry(studentId, studentName, grade));
                }
            }
            gradeTable.setItems(gradeList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveGrades() {
        String selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) {
            showAlert("Error", "Please select a course.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get course_id
            String courseQuery = "SELECT id FROM courses WHERE course_code = ?";
            int courseId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(courseQuery)) {
                stmt.setString(1, selectedCourse);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("id");
                }
            }

            if (courseId == -1) {
                showAlert("Error", "Invalid course selected.");
                return;
            }

            String insertSql = "INSERT INTO grades (student_id, course_id, grade) " +
                               "VALUES (?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE grade = VALUES(grade)";

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (GradeEntry entry : gradeList) {
                    stmt.setInt(1, entry.getStudentId());
                    stmt.setInt(2, courseId);
                    stmt.setString(3, entry.getGrade());
                    stmt.executeUpdate();
                }
            }

            showAlert("Success", "Grades saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save grades.");
        }
    }

    @FXML
    private void handleDeleteGrade() {
        GradeEntry selectedEntry = gradeTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert("Error", "Please select a grade to delete.");
            return;
        }

        String selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) {
            showAlert("Error", "Please select a course.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get course_id based on selected course
            String courseQuery = "SELECT id FROM courses WHERE course_code = ?";
            int courseId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(courseQuery)) {
                stmt.setString(1, selectedCourse);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("id");
                }
            }

            if (courseId == -1) {
                showAlert("Error", "Invalid course selected.");
                return;
            }

            // Prepare DELETE statement
            String deleteSql = "DELETE FROM grades WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, selectedEntry.getStudentId()); // Use the selected student's ID
                stmt.setInt(2, courseId); // Use the retrieved course ID
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    gradeList.remove(selectedEntry); // Remove from the table
                    showAlert("Success", "Grade deleted successfully.");
                } else {
                    showAlert("Error", "Failed to delete grade.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete grade.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Embedded GradeEntry Model Class ---
    public static class GradeEntry {
        private final IntegerProperty studentId;
        private final StringProperty studentName;
        private final StringProperty grade;

        public GradeEntry(int studentId, String studentName, String grade) {
            this.studentId = new SimpleIntegerProperty(studentId);
            this.studentName = new SimpleStringProperty(studentName);
            this.grade = new SimpleStringProperty(grade);
        }

        public int getStudentId() {
            return studentId.get();
        }

        public void setStudentId(int id) {
            this.studentId.set(id);
        }

        public IntegerProperty studentIdProperty() {
            return studentId;
        }

        public String getStudentName() {
            return studentName.get();
        }

        public void setStudentName(String name) {
            this.studentName.set(name);
        }

        public StringProperty studentNameProperty() {
            return studentName;
        }

        public String getGrade() {
            return grade.get();
        }

        public void setGrade(String g) {
            this.grade.set(g);
        }

        public StringProperty gradeProperty() {
            return grade;
        }
    }
}
