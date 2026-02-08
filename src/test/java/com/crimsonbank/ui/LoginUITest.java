package com.crimsonbank.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.assertions.api.Assertions.assertThat;

public class LoginUITest extends ApplicationTest {

    private Label resultLabel;

    @Override
    public void start(Stage stage) {
        Label header = new Label("CrimsonBank");
        header.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        header.setId("header");

        TextField usernameField = new TextField();
        usernameField.setId("username");
        usernameField.setPromptText("\uD83D\uDC64  Username");
        usernameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setId("password");
        passwordField.setPromptText("\uD83D\uDD12  Password");
        passwordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8 10;");

        Button loginButton = new Button("Sign In");
        loginButton.setId("loginButton");
        loginButton.setStyle("-fx-background-color: linear-gradient(#4CA1AF, #2C3E50); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20;");

        resultLabel = new Label();
        resultLabel.setId("result");
        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if ("staff".equals(username) && "1234".equals(password)) {
                resultLabel.setText("Login Success");
                resultLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                resultLabel.setText("Login Failed");
                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        });

        VBox form = new VBox(10, usernameField, passwordField, loginButton);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(300);

        VBox root = new VBox(18, header, form, resultLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: linear-gradient(#f7f8fb, #e9eef6);");

        Scene scene = new Scene(root, 380, 260);
        stage.setScene(scene);
        stage.setTitle("CrimsonBank - Login");
        stage.show();
    }

    @Test
    public void testValidLogin() {
        clickOn("#username").write("staff");
        clickOn("#password").write("1234");
        clickOn("#loginButton");
        assertThat(lookup("#result").queryAs(Label.class)).hasText("Login Success");
    }

    @Test
    public void testInvalidLogin() {
        clickOn("#username").write("wronguser");
        clickOn("#password").write("wrongpass");
        clickOn("#loginButton");
        assertThat(lookup("#result").queryAs(Label.class)).hasText("Login Failed");
    }
}
