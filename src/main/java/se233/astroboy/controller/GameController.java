package se233.astroboy.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import se233.astroboy.model.Player;
import se233.astroboy.model.Asteroid;
import se233.astroboy.model.Projectile;
import se233.astroboy.view.GameStage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameController {
    private static final Logger logger = LogManager.getLogger(GameController.class);

    private GameStage gameStage;
    private AnimationTimer gameLoop;
    private boolean isRunning;

    // Game objects
    private Player player;
    private List<Asteroid> asteroids;
    private List<Projectile> projectiles;

    // Game state
    private int level;
    private int score;
    private double spawnTimer;
    private static final double SPAWN_INTERVAL = 3.0;

    public GameController(GameStage gameStage) {
        this.gameStage = gameStage;
        this.isRunning = false;
        initializeGame();
    }

    private void initializeGame() {
        // Create player at center of screen
        double centerX = gameStage.getStageWidth() / 2;
        double centerY = gameStage.getStageHeight() / 2;
        player = new Player(centerX, centerY, gameStage.getStageWidth(), gameStage.getStageHeight());
        logger.info("Player initialized at ({}, {})", centerX, centerY);

        // Initialize game objects
        asteroids = new ArrayList<>();
        projectiles = new ArrayList<>();
        level = 1;
        score = 0;
        spawnTimer = SPAWN_INTERVAL;

        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                // Limit updates to ~60 FPS
                if (lastUpdate == 0 || now - lastUpdate >= 16_666_666) { // 60 FPS = ~16.67ms
                    updateGame();
                    renderGame();
                    lastUpdate = now;
                }
            }
        };

        spawnAsteroids(3);
    }

    private void updateGame() {
        if (!player.isAlive()) return;

        // Update player
        player.update();

        // Update asteroids and check collisions
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            asteroid.update();

            if (asteroid.isMarkedForDestruction()) {
                asteroidIterator.remove();
                score += asteroid.getPoints();
            }
        }

        // Handle collisions
        CollisionController.handleCollisions(player, asteroids, projectiles);

        // Spawn new asteroids
        spawnTimer -= 0.016;
        if (spawnTimer <= 0) {
            spawnAsteroids(1);
            spawnTimer = SPAWN_INTERVAL;
        }
    }

    private void renderGame() {
        var gc = gameStage.getGraphicsContext();

        // Clear screen
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameStage.getStageWidth(), gameStage.getStageHeight());

        // Render asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.render(gc);
        }

        // Render player
        if (player.isAlive()) {
            player.render(gc);
        }

        // Render score
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Score: " + score, 10, 30);
    }

    public void handleKeyPress(KeyCode code) {
        if (!player.isAlive()) return;

        switch (code) {
            case W:
                player.setMovingForward(true);
                logger.debug("Moving forward");
                break;
            case S:
                player.setBraking(true);
                logger.debug("Braking engaged");
                break;
            case A:
                player.setRotatingLeft(true);
                break;
            case D:
                player.setRotatingRight(true);
                break;
            case SPACE:
                fireProjectile();
                break;
        }
    }

    public void handleKeyRelease(KeyCode code) {
        if (!player.isAlive()) return;

        switch (code) {
            case W:
                player.setMovingForward(false);
                break;
            case S:
                player.setBraking(false);
                break;
            case A:
                player.setRotatingLeft(false);
                break;
            case D:
                player.setRotatingRight(false);
                break;
        }
    }

    private void fireProjectile() {
        if (player.canShoot()) {
            // Implement projectile firing
            player.resetShootCooldown();
        }
    }

    private void spawnAsteroids(int count) {
        for (int i = 0; i < count; i++) {
            double x, y;
            if (Math.random() < 0.5) {
                x = Math.random() < 0.5 ? -30 : gameStage.getStageWidth() + 30;
                y = Math.random() * gameStage.getStageHeight();
            } else {
                x = Math.random() * gameStage.getStageWidth();
                y = Math.random() < 0.5 ? -30 : gameStage.getStageHeight() + 30;
            }

            asteroids.add(new Asteroid(x, y, 1));
        }
    }

    public void startGameLoop() {
        if (!isRunning) {
            gameLoop.start();
            isRunning = true;
            logger.info("Game loop started");
        }
    }
}