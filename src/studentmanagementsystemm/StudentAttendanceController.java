package studentmanagementsystemm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentAttendanceController {

    @FXML
    private TableView<AttendanceData> attendanceTable;

    @FXML
    private TableColumn<AttendanceData, String> colCourseCode;

    @FXML
    private TableColumn<AttendanceData, String> colCourseName;

    @FXML
    private TableColumn<AttendanceData, String> colDate;

    @FXML
    private TableColumn<AttendanceData, String> colStatus;

    private ObservableList<AttendanceData> attendanceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadAttendance();
    }

    private void loadAttendance() {
        attendanceList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = 
    "SELECT c.course_code, c.course_name, a.date, a.status " +
    "FROM attendance a " +
    "JOIN courses c ON a.course_id = c.id " +
    "WHERE a.user_id = ? " +
    "ORDER BY a.date DESC";


            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, FXMLDocumentController.loggedInUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    attendanceList.add(new AttendanceData(
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getString("date"),
                            rs.getString("status")
                    ));
                }

                attendanceTable.setItems(attendanceList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Data class for holding attendance data
    public static class AttendanceData {
        private final String courseCode;
        private final String courseName;
        private final String date;
        private final String status;

        public AttendanceData(String courseCode, String courseName, String date, String status) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.date = date;
            this.status = status;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }
    }
}
