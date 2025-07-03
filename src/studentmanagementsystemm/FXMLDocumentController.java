package studentmanagementsystemm;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    public static String loggedInUsername;
    public static int loggedInUserId = -1;

    @FXML private Button exit_btn;
    @FXML private Button register_exitbtn;
    @FXML private Button register_btn;
    @FXML private Button login_btn;

    @FXML private CheckBox login_CheckBox;
    @FXML private CheckBox register_CheckBox1;

    @FXML private Hyperlink login_forgotpassword;
    @FXML private Hyperlink login_registerhere;
    @FXML private Hyperlink register_loginhere;

    @FXML private Label label__logquestion;
    @FXML private Label label_email;
    @FXML private Label label_login;
    @FXML private Label label_logpassword;
    @FXML private Label label_logusername;
    @FXML private Label label_register;
    @FXML private Label label_regpassword;
    @FXML private Label label_regquestion;
    @FXML private Label label_regrole;
    @FXML private Label label_regtitle;
    @FXML private Label label_regusername;
    @FXML private Label login_title;

    @FXML private TextField login_username;
    @FXML private PasswordField login_passwordField;
    @FXML private TextField login_showpassword;

    @FXML private TextField register_username;
    @FXML private PasswordField register_passwordField;
    @FXML private TextField register_email;
    @FXML private ComboBox<String> register_role;

    @FXML private AnchorPane login_form;
    @FXML private AnchorPane register_form;
    @FXML private TextField register_showpassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> roles = FXCollections.observableArrayList("Student", "Teacher");
        register_role.setItems(roles);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = login_username.getText().trim();
        String password = login_CheckBox.isSelected() ? login_showpassword.getText().trim() : login_passwordField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, role FROM users WHERE TRIM(username) = ? AND TRIM(password) = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    loggedInUserId = rs.getInt("id");
                    String role = rs.getString("role");
                    loggedInUsername = username;

                    System.out.println("✅ Login success for: " + username + " | Role: " + role + " | ID: " + loggedInUserId);

                    String fxmlToLoad = determineDashboard(role);
                    if (!fxmlToLoad.isEmpty()) {
                        loadDashboard(fxmlToLoad);
                    } else {
                        System.out.println("⚠️ Unknown role in DB: " + role);
                    }
                } else {
                    System.out.println("❌ Invalid credentials!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        if (login_CheckBox.isSelected()) {
            login_showpassword.setText(login_passwordField.getText());
            login_showpassword.setVisible(true);
            login_showpassword.setManaged(true);
            login_passwordField.setVisible(false);
            login_passwordField.setManaged(false);
        } else {
            login_passwordField.setText(login_showpassword.getText());
            login_passwordField.setVisible(true);
            login_passwordField.setManaged(true);
            login_showpassword.setVisible(false);
            login_showpassword.setManaged(false);
        }
    }

    @FXML
    void toggleRegisterPasswordVisibility(ActionEvent event) {
        if (register_CheckBox1.isSelected()) {
            register_showpassword.setText(register_passwordField.getText());
            register_showpassword.setVisible(true);
            register_showpassword.setManaged(true);
            register_passwordField.setVisible(false);
            register_passwordField.setManaged(false);
        } else {
            register_passwordField.setText(register_showpassword.getText());
            register_passwordField.setVisible(true);
            register_passwordField.setManaged(true);
            register_showpassword.setVisible(false);
            register_showpassword.setManaged(false);
        }
    }

    

    @FXML
    private void switchForm(ActionEvent event) {
        if (event.getSource() == login_registerhere) {
            login_form.setVisible(false);
            register_form.setVisible(true);
        } else if (event.getSource() == register_loginhere) {
            register_form.setVisible(false);
            login_form.setVisible(true);
        }
    }

   private String determineDashboard(String role) {
    switch (role.toLowerCase()) {
        case "student":
            return "/studentmanagementsystemm/student.fxml";
        case "teacher":
            return "/studentmanagementsystemm/teacher.fxml";
        case "admin":
            return "/studentmanagementsystemm/admin.fxml"; // Add this line for admin
        default:
            return "";
    }
}



    private void loadDashboard(String fxmlToLoad) {
        try {
            System.out.println("📦 Loading dashboard: " + fxmlToLoad);
            System.out.println("🧾 Passing loggedInStudentId = " + loggedInUserId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToLoad));
            Parent root = loader.load();
            Stage stage = (Stage) login_btn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String username = register_username.getText();
        String password = register_passwordField.getText();
        String email = register_email.getText();
        String role = register_role.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
            System.out.println("All fields are required.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT * FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, email);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    System.out.println("Username or Email already exists.");
                    return;
                }

                String insertQuery = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);
                    insertStmt.setString(3, email);
                    insertStmt.setString(4, role);

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Registration successful.");
                        register_form.setVisible(false);
                        login_form.setVisible(true);
                    } else {
                        System.out.println("Registration failed.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
