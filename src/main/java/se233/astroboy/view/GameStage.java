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
        setPrefSize(stageWidth, stageHeight);
        setMinSize(stageWidth, stageHeight);
        setMaxSize(stageWidth, stageHeight);

        // Create the canvas
        canvas = new Canvas(stageWidth, stageHeight);
        gc = canvas.getGraphicsContext2D();

        // Set background color
        setStyle("-fx-background-color: black;");

        // Add canvas to the pane
        getChildren().add(canvas);

        // Initialize game controller
        gameController = new GameController(this);

        // Set up key event handlers
        setFocusTraversable(true);
        requestFocus();

        // Handle key events
        setOnKeyPressed(event -> gameController.handleKeyPress(event.getCode()));
        setOnKeyReleased(event -> gameController.handleKeyRelease(event.getCode()));

        // Start the game
        gameController.startGameLoop();
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    public double getStageWidth() {
        return stageWidth;
    }

    public double getStageHeight() {
        return stageHeight;
    }
}