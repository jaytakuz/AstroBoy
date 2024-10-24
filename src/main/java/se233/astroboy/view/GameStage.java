package se233.astroboy.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.astroboy.controller.GameController;

public class GameStage extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private GameController gameController;
    private final int stageWidth;
    private final int stageHeight;

    public GameStage(int width, int height) {
        this.stageWidth = width;
        this.stageHeight = height;

        // Set the pane's size
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);

        // Create the canvas at the specified size
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        // Set background color
        setStyle("-fx-background-color: black;");

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

    public int getStageWidth() {
        return stageWidth;
    }

    public int getStageHeight() {
        return stageHeight;
    }
}