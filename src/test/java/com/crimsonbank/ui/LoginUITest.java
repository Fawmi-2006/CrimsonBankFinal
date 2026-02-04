package com.crimsonbank.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.assertions.api.Assertions.assertThat;

public class LoginUITest extends ApplicationTest {

    private Label resultLabel;

    @Override
    public void start(Stage stage) {
        TextField usernameField = new TextField();
        usernameField.setId("username");
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setId("password");
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setId("loginButton");

        resultLabel = new Label();
        resultLabel.setId("result");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if ("staff".equals(username) && "1234".equals(password)) {
                resultLabel.setText("Login Success");
            } else {
                resultLabel.setText("Login Failed");
            }
        });

        VBox root = new VBox(10, usernameField, passwordField, loginButton, resultLabel);
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
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
