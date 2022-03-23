package com.kyancey.inventory;

import com.kyancey.inventory.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * <p>Main class - Starts the program.</p>
 * @author Kyle Yancey
 */
public class Main extends Application {
    /**
     * Initializes app.
     * @param primaryStage Primary Stage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/main.fxml"));
        Parent root = loader.load();


        // Set the scene and pass inventory to the controller
        Scene scene = new Scene(root);
        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        // Set the window up
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}