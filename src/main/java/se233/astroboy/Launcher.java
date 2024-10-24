package se233.astroboy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.astroboy.view.GameStage;

// Application entry point
public class Launcher extends Application {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        // Create the game stage
        GameStage gameStage = new GameStage(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create the scene with the game stage
        Scene scene = new Scene(gameStage);

        // Set up the primary stage
        primaryStage.setTitle("AstroBoy");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Start the game
        gameStage.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}