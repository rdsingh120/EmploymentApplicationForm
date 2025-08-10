package final_exam_ripudaman_singh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EmploymentApplicationForm extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employment Application");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Employment Application");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        grid.add(title, 0, 1, 2, 1);

        Label personalInfo = new Label("Personal Information");
        personalInfo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(personalInfo, 0, 2, 2, 1);

        TextField fullNameField = new TextField();
        grid.add(new Label("Full Name:"), 0, 3);
        grid.add(fullNameField, 1, 3);

        TextField addressField = new TextField();
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        TextField contactField = new TextField();
        TextField emailField = new TextField();
        HBox contactEmailBox = new HBox(10);
        contactEmailBox.getChildren().addAll(new Label("Contact Number:"), contactField,
                new Label("Email:"), emailField);
        grid.add(contactEmailBox, 0, 5, 2, 1);

        ComboBox<String> educationBox = new ComboBox<>();
        educationBox.getItems().addAll("Masters", "Bachelors", "College Diploma");
        educationBox.setPromptText("Select Education");

        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton male = new RadioButton("Male");
        RadioButton female = new RadioButton("Female");
        RadioButton other = new RadioButton("Other");
        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);
        other.setToggleGroup(genderGroup);

        HBox educationGenderBox = new HBox(15);
        educationGenderBox.getChildren().addAll(new Label("Highest Education:"), educationBox,
                new Label("Gender:"), male, female, other);
        grid.add(educationGenderBox, 0, 6, 2, 1);

        Label employmentEligibility = new Label("Employment Eligibility");
        employmentEligibility.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(employmentEligibility, 0, 7, 2, 1);

        DatePicker dateAvailable = new DatePicker();
        grid.add(dateAvailable, 1, 8);

        TextField positionField = new TextField();
        grid.add(positionField, 1, 9);

        HBox datePostionBox = new HBox(15);
        datePostionBox.getChildren().addAll(new Label("Date Available:"), dateAvailable,
                new Label("Desired Position:"), positionField);
        grid.add(datePostionBox, 0, 8, 2, 1);

        TextField salaryField = new TextField();
        grid.add(new Label("Desired Salary:"), 0, 10);
        grid.add(salaryField, 1, 10);

        ToggleGroup authGroup = new ToggleGroup();
        RadioButton authYes = new RadioButton("Yes");
        RadioButton authNo = new RadioButton("No");
        authYes.setToggleGroup(authGroup);
        authNo.setToggleGroup(authGroup);
        grid.add(new Label("Legally authorized to work in Canada?"), 0, 11);
        grid.add(new HBox(10, authYes, authNo), 1, 11);

        ToggleGroup relativesGroup = new ToggleGroup();
        RadioButton relYes = new RadioButton("Yes");
        RadioButton relNo = new RadioButton("No");
        relYes.setToggleGroup(relativesGroup);
        relNo.setToggleGroup(relativesGroup);
        grid.add(new Label("Relatives working in company?"), 0, 12);
        grid.add(new HBox(10, relYes, relNo), 1, 12);

        TextField relativesExplain = new TextField();
        grid.add(new Label("If yes, explain:"), 0, 13);
        grid.add(relativesExplain, 1, 13);

        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(e -> {
            if (validateFields(fullNameField, contactField, educationBox, dateAvailable, salaryField)) {
                insertData(fullNameField.getText(), addressField.getText(), contactField.getText(),
                        emailField.getText(), educationBox.getValue(),
                        ((RadioButton) genderGroup.getSelectedToggle()).getText(),
                        dateAvailable.getValue(), positionField.getText(), salaryField.getText(),
                        ((RadioButton) authGroup.getSelectedToggle()).getText(),
                        ((RadioButton) relativesGroup.getSelectedToggle()).getText(),
                        relativesExplain.getText());
            }
        });
        grid.add(submitBtn, 1, 14);

        Scene scene = new Scene(grid, 550, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean validateFields(TextField fullName, TextField contact, ComboBox<String> education,
                                   DatePicker dateAvailable, TextField salary) {
        if (!fullName.getText().matches("^[A-Za-z ]{1,50}$")) {
            showAlert("Invalid Full Name");
            return false;
        }
        if (!contact.getText().matches("\\d{10}")) {
            showAlert("Contact must be 10 digits");
            return false;
        }
        if (education.getValue() == null) {
            showAlert("Select Highest Education");
            return false;
        }
        if (dateAvailable.getValue() == null || dateAvailable.getValue().isBefore(LocalDate.now())) {
            showAlert("Select a valid future date");
            return false;
        }
        if (!salary.getText().matches("^\\$?\\d{1,8}(\\.\\d{1,2})?$")) {
            showAlert("Salary must be up to 8 digits and 2 decimal places");
            return false;
        }
        return true;
    }

    private void insertData(String fullName, String address, String contact, String email,
                            String education, String gender, LocalDate dateAvailable,
                            String position, String salary, String authWork, String relatives,
                            String relativesExplain) {
        String sqlApplicant = "INSERT INTO ApplicantTable (FullName, Address, Contact, Email, Education, Gender) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEmployment = "INSERT INTO EmploymentTable (DateAvailable, Position, Salary, AuthWork, Relatives, RelativesExplain) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(sqlApplicant);
             PreparedStatement ps2 = conn.prepareStatement(sqlEmployment)) {

            ps1.setString(1, fullName);
            ps1.setString(2, address);
            ps1.setString(3, contact);
            ps1.setString(4, email);
            ps1.setString(5, education);
            ps1.setString(6, gender);
            ps1.executeUpdate();

            ps2.setString(1, dateAvailable.toString());
            ps2.setString(2, position);
            ps2.setString(3, salary);
            ps2.setString(4, authWork);
            ps2.setString(5, relatives);
            ps2.setString(6, relativesExplain);
            ps2.executeUpdate();

            showAlert("Application submitted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
