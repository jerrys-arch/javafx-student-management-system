package studentmanagementsystemm;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class GradeReportController {

    @FXML
    private TextField courseCodeField;

    @FXML
    private TableView<GradeRecord> gradeTable;

    @FXML
    private TableColumn<GradeRecord, Integer> studentIdCol;

    @FXML
    private TableColumn<GradeRecord, String> studentNameCol;

    @FXML
    private TableColumn<GradeRecord, String> gradeCol;

    private ObservableList<GradeRecord> gradeData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        studentIdCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty().asObject());
        studentNameCol.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        gradeCol.setCellValueFactory(cellData -> cellData.getValue().gradeProperty());

        gradeTable.setItems(gradeData);
    }

    @FXML
    private void handleLoadGrades() {
        String courseCode = courseCodeField.getText().trim();
        if (courseCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter a course code.");
            return;
        }

        gradeData.clear();

       String query = "SELECT g.student_id, u.username, g.grade " +
               "FROM grades g " +
               "JOIN users u ON g.student_id = u.id " +  // Changed 'g.user_id' to 'g.student_id'
               "JOIN courses c ON g.course_id = c.id " +
               "WHERE c.course_code = ?";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String studentName = rs.getString("username");
                String grade = rs.getString("grade");

                gradeData.add(new GradeRecord(studentId, studentName, grade));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading grade data.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Grade Report");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Integrated Model Class: GradeRecord
    public static class GradeRecord {
        private final IntegerProperty studentId;
        private final StringProperty studentName;
        private final StringProperty grade;

        public GradeRecord(int studentId, String studentName, String grade) {
            this.studentId = new SimpleIntegerProperty(studentId);
            this.studentName = new SimpleStringProperty(studentName);
            this.grade = new SimpleStringProperty(grade);
        }

        public IntegerProperty studentIdProperty() { return studentId; }
        public StringProperty studentNameProperty() { return studentName; }
        public StringProperty gradeProperty() { return grade; }
    }
}
