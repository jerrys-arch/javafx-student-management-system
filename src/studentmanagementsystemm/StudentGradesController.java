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

public class StudentGradesController {

    @FXML
    private TableView<GradeData> gradesTable;

    @FXML
    private TableColumn<GradeData, String> colCourseCode;

    @FXML
    private TableColumn<GradeData, String> colCourseName;

    @FXML
    private TableColumn<GradeData, String> colGrade;

    private ObservableList<GradeData> gradeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        loadGrades();
    }

    private void loadGrades() {
        gradeList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
           String query = 
    "SELECT c.course_code, c.course_name, g.grade " +
    "FROM grades g " +
    "JOIN courses c ON g.course_id = c.id " +
    "WHERE g.student_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, FXMLDocumentController.loggedInUserId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    gradeList.add(new GradeData(
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getString("grade")
                    ));
                }

                gradesTable.setItems(gradeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Data class for holding grade data
    public static class GradeData {
        private final String courseCode;
        private final String courseName;
        private final String grade;

        public GradeData(String courseCode, String courseName, String grade) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.grade = grade;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getGrade() {
            return grade;
        }
    }
}
