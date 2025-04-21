package com.ej.bibletoppt;

import com.ej.bibletoppt.update.UpdateChecker;
import com.ej.bibletoppt.update.UpdatePrompt;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BibleToPPTApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Check for updates
        checkForUpdates();

        FXMLLoader fxmlLoader = new FXMLLoader(BibleToPPTApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

        // Add application icon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/ej/bibletoppt/icon/logo.png")));

        // Set application title
        stage.setTitle("BibleToPPT - 성경 구절 PPT 생성기");

        // Make the window resizable and set minimum size
        stage.setResizable(true);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);

        // Set the scene
        stage.setScene(scene);

        // Center the window on screen
        stage.centerOnScreen();

        // Show the window
        stage.show();

        // Add window resize listener to adjust UI elements
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Window width changed, could trigger UI adjustments if needed
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // Window height changed, could trigger UI adjustments if needed
        });
    }

    private void checkForUpdates() {
        try {
            // Load current version from properties
            Properties properties = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                }
            }

            String currentVersion = properties.getProperty("app.version", "1.0.0");

            // Check if update is available
            if (UpdateChecker.isUpdateAvailable()) {
                String latestVersion = UpdateChecker.getLatestVersion();
                UpdatePrompt.showUpdateDialog(currentVersion, latestVersion);
            }
        } catch (Exception e) {
            System.err.println("Error checking for updates: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
