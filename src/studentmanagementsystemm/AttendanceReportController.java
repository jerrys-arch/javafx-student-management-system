package studentmanagementsystemm;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class AttendanceReportController {

    @FXML
    private TextField courseCodeField;

    @FXML
    private TableView<AttendanceRecord> attendanceTable;

    @FXML
    private TableColumn<AttendanceRecord, Integer> studentIdCol;

    @FXML
    private TableColumn<AttendanceRecord, String> studentNameCol;

    @FXML
    private TableColumn<AttendanceRecord, String> dateCol;

    @FXML
    private TableColumn<AttendanceRecord, String> statusCol;

    private ObservableList<AttendanceRecord> attendanceData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        studentIdCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty().asObject());
        studentNameCol.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        attendanceTable.setItems(attendanceData);
    }

    @FXML
    private void handleLoadAttendance() {
        String courseCode = courseCodeField.getText().trim();
        if (courseCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter a course code.");
            return;
        }

        attendanceData.clear();

        String query = "SELECT a.user_id AS student_id, u.username, a.date, a.status " +
               "FROM attendance a " +
               "JOIN users u ON a.user_id = u.id " +
               "JOIN courses c ON a.course_id = c.id " +
               "WHERE c.course_code = ? AND u.role = 'student'";



        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String studentName = rs.getString("username");
                String date = rs.getString("date");
                String status = rs.getString("status");

                attendanceData.add(new AttendanceRecord(studentId, studentName, date, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading attendance data.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Attendance Report");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Integrated Model Class: AttendanceRecord
    public static class AttendanceRecord {
        private final IntegerProperty studentId;
        private final StringProperty studentName;
        private final StringProperty date;
        private final StringProperty status;

        public AttendanceRecord(int studentId, String studentName, String date, String status) {
            this.studentId = new SimpleIntegerProperty(studentId);
            this.studentName = new SimpleStringProperty(studentName);
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
        }

        public IntegerProperty studentIdProperty() { return studentId; }
        public StringProperty studentNameProperty() { return studentName; }
        public StringProperty dateProperty() { return date; }
        public StringProperty statusProperty() { return status; }
    }
}
