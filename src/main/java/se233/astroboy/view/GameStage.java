package se233.astroboy.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import se233.astroboy.controller.GameController;

// Main game stage
public class GameStage extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private GameController gameController;

    public GameStage(int width, int height) {
        // Create the canvas
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        // Add canvas to the pane
        getChildren().add(canvas);

        // Initialize game controller
        gameController = new GameController(this);

        // Set up key event handlers
        setUpInputHandlers();
    }

    private void setUpInputHandlers() {
        // Set focus traversable to receive key events
        this.setFocusTraversable(true);

        // Handle key press events
        this.setOnKeyPressed(event -> {
            gameController.handleKeyPress(event.getCode());
        });

        // Handle key release events
        this.setOnKeyReleased(event -> {
            gameController.handleKeyRelease(event.getCode());
        });
    }

    public void startGame() {
        gameController.startGameLoop();
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }
}