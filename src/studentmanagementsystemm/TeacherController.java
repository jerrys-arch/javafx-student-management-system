
package studentmanagementsystemm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TeacherController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private AnchorPane contentPane;

    @FXML
    public void initialize() {
        // Show welcome message using username
        welcomeLabel.setText("👋 Welcome, " + FXMLDocumentController.loggedInUsername);
    }

    @FXML
    void handleViewEnrolledStudents(ActionEvent event) {
        loadContent("teacherstudent.fxml");
    }

    @FXML
    void handleMarkAttendance(ActionEvent event) {
        loadContent("teacherattendance.fxml");
    }

    @FXML
    void handleAssignGrades(ActionEvent event) {
        loadContent("teachergrades.fxml");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/studentmanagementsystemm/FXMLDocument.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlFile) {
    try {
        // Debugging: Print the resource path
        System.out.println("Loading FXML: " + getClass().getResource("/studentmanagementsystemm/" + fxmlFile));
        
        Parent content = FXMLLoader.load(getClass().getResource("/studentmanagementsystemm/" + fxmlFile));
        contentPane.getChildren().setAll(content);
    } catch (Exception e) {
        e.printStackTrace();  // Prints the exception stack trace for better debugging
    }
}

}

