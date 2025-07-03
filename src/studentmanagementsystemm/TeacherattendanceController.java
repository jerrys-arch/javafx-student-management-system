package studentmanagementsystemm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

import java.sql.*;
import java.time.LocalDate;

public class TeacherattendanceController {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private TableView<AttendanceEntry> attendanceTable;

    @FXML
    private TableColumn<AttendanceEntry, Integer> studentIdCol;

    @FXML
    private TableColumn<AttendanceEntry, String> studentNameCol;

    @FXML
    private TableColumn<AttendanceEntry, String> statusCol;

    private ObservableList<AttendanceEntry> attendanceList = FXCollections.observableArrayList();

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
        studentIdCol.setCellValueFactory(data -> data.getValue().studentIdProperty().asObject());
        studentNameCol.setCellValueFactory(data -> data.getValue().studentNameProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("Present", "Absent"));

        statusCol.setOnEditCommit(event -> {
            AttendanceEntry entry = event.getRowValue();
            entry.setStatus(event.getNewValue());
        });

        attendanceTable.setEditable(true);
    }

    private void loadStudentsForSelectedCourse() {
        attendanceList.clear();
        String selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get course_id
            String getCourseIdQuery = "SELECT id FROM courses WHERE course_code = ?";
            int courseId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(getCourseIdQuery)) {
                stmt.setString(1, selectedCourse);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("id");
                }
            }

            if (courseId == -1) return;

            String sql = "SELECT u.id AS student_id, u.username, " +
                         "  (SELECT a.status FROM attendance a " +
                         "   WHERE a.user_id = u.id AND a.course_id = ? AND a.date = CURDATE() LIMIT 1) AS status " +
                         "FROM users u " +
                         "JOIN enrollments e ON u.id = e.student_id " +
                         "WHERE e.course_id = ?";

            LocalDate today = LocalDate.now();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.setInt(2, courseId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String studentName = rs.getString("username");
                    String status = rs.getString("status");
                    if (status == null) status = "Present"; // default if no attendance saved yet
                    attendanceList.add(new AttendanceEntry(studentId, studentName, status, courseId, today));
                }
            }

            attendanceTable.setItems(attendanceList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveAttendance() {
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

            String insertSql = "INSERT INTO attendance (user_id, course_id, date, status) " +
                               "VALUES (?, ?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE status = VALUES(status)";

            for (AttendanceEntry entry : attendanceList) {
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, entry.getStudentId());
                    stmt.setInt(2, entry.getCourseId());
                    stmt.setDate(3, Date.valueOf(entry.getDate()));
                    stmt.setString(4, entry.getStatus());
                    stmt.executeUpdate();
                }
            }

            showAlert("Success", "Attendance saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save attendance.");
        }
    }

    @FXML
    private void handleDeleteAttendance() {
        AttendanceEntry selectedEntry = attendanceTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert("Error", "Please select an attendance record to delete.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM attendance WHERE user_id = ? AND course_id = ? AND date = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selectedEntry.getStudentId());
                stmt.setInt(2, selectedEntry.getCourseId());
                stmt.setDate(3, java.sql.Date.valueOf(selectedEntry.getDate()));
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    attendanceList.remove(selectedEntry);
                    showAlert("Success", "Attendance record deleted.");
                } else {
                    showAlert("Error", "Failed to delete attendance record.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error while deleting attendance.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Updated AttendanceEntry model with courseId and date
    public static class AttendanceEntry {
        private final javafx.beans.property.IntegerProperty studentId;
        private final javafx.beans.property.StringProperty studentName;
        private final javafx.beans.property.StringProperty status;
        private final javafx.beans.property.IntegerProperty courseId;
        private final javafx.beans.property.ObjectProperty<java.time.LocalDate> date;

        public AttendanceEntry(int studentId, String studentName, String status, int courseId, java.time.LocalDate date) {
            this.studentId = new javafx.beans.property.SimpleIntegerProperty(studentId);
            this.studentName = new javafx.beans.property.SimpleStringProperty(studentName);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
            this.courseId = new javafx.beans.property.SimpleIntegerProperty(courseId);
            this.date = new javafx.beans.property.SimpleObjectProperty<>(date);
        }

        public int getStudentId() {
            return studentId.get();
        }

        public void setStudentId(int id) {
            this.studentId.set(id);
        }

        public javafx.beans.property.IntegerProperty studentIdProperty() {
            return studentId;
        }

        public String getStudentName() {
            return studentName.get();
        }

        public void setStudentName(String name) {
            this.studentName.set(name);
        }

        public javafx.beans.property.StringProperty studentNameProperty() {
            return studentName;
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String status) {
            this.status.set(status);
        }

        public javafx.beans.property.StringProperty statusProperty() {
            return status;
        }

        public int getCourseId() {
            return courseId.get();
        }

        public void setCourseId(int id) {
            this.courseId.set(id);
        }

        public javafx.beans.property.IntegerProperty courseIdProperty() {
            return courseId;
        }

        public java.time.LocalDate getDate() {
            return date.get();
        }

        public void setDate(java.time.LocalDate date) {
            this.date.set(date);
        }

        public javafx.beans.property.ObjectProperty<java.time.LocalDate> dateProperty() {
            return date;
        }
    }
}
