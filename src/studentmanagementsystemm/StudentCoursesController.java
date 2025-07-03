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

public class StudentCoursesController {

    @FXML
    private TableView<CourseData> courseTable;

    @FXML
    private TableColumn<CourseData, String> colCourseCode;

    @FXML
    private TableColumn<CourseData, String> colCourseName;

    @FXML
    private TableColumn<CourseData, String> colTeacher;

    private ObservableList<CourseData> courseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        loadCourses();
    }

    private void loadCourses() {
        courseList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
           String query = 
    "SELECT c.course_code, c.course_name, u.username AS teacher_name " +
    "FROM enrollments e " +
    "JOIN courses c ON e.course_id = c.id " +
    "JOIN users u ON c.teacher_id = u.id " +
    "WHERE e.student_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, FXMLDocumentController.loggedInUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    courseList.add(new CourseData(
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getString("teacher_name")
                    ));
                }

                courseTable.setItems(courseList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Data class for holding course data
    public static class CourseData {
        private final String courseCode;
        private final String courseName;
        private final String teacherName;

        public CourseData(String courseCode, String courseName, String teacherName) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.teacherName = teacherName;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getTeacherName() {
            return teacherName;
        }
    }
}

