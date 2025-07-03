package studentmanagementsystemm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentController {

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
    void handleViewCourses(ActionEvent event) {
        loadContent("studentcourses.fxml");
    }

    @FXML
    void handleViewGrades(ActionEvent event) {
        loadContent("studentgrades.fxml");
    }

    @FXML
    void handleViewAttendance(ActionEvent event) {
        loadContent("studentattendance.fxml");
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
            Parent content = FXMLLoader.load(getClass().getResource("/studentmanagementsystemm/" + fxmlFile));
            contentPane.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
