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
    private static final double SPAWN_INTERVAL = 3.0; // Seconds between spawns

    public GameController(GameStage gameStage) {
        this.gameStage = gameStage;
        this.isRunning = false;
        initializeGame();
    }

    private void initializeGame() {
        // Create player at center of screen
        player = new Player(
                gameStage.getWidth() / 2,  // Center X
                gameStage.getHeight() / 2, // Center Y
                gameStage.getWidth(),
                gameStage.getHeight()
        );

        // Initialize game objects and state
        asteroids = new ArrayList<>();
        projectiles = new ArrayList<>();
        level = 1;
        score = 0;
        spawnTimer = SPAWN_INTERVAL;

        // Set up game loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                renderGame();
            }
        };

        // Spawn initial asteroids
        spawnAsteroids(3);

        logger.info("Game initialized with {} asteroids", asteroids.size());
    }

    public void startGameLoop() {
        if (!isRunning) {
            gameLoop.start();
            isRunning = true;
            logger.info("Game loop started");
        }
    }

    private void updateGame() {
        if (!player.isAlive()) {
            return;
        }

        // Update player
        player.update();

        // Update projectiles
        Iterator<Projectile> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            projectile.update();
            if (projectile.isMarkedForRemoval()) {
                projectileIterator.remove();
            }
        }

        // Update asteroids and handle collisions
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            asteroid.update();

            if (asteroid.isMarkedForDestruction()) {
                asteroidIterator.remove();
                score += asteroid.getPoints();
                logger.info("Asteroid destroyed! Score: {}", score);
            }
        }

        // Handle all collisions
        CollisionController.handleCollisions(player, asteroids, projectiles);

        // Update spawn timer
        spawnTimer -= 0.016; // Assuming 60 FPS
        if (spawnTimer <= 0) {
            spawnAsteroids(1);
            spawnTimer = SPAWN_INTERVAL;
        }

        // Check game over condition
        if (!player.isAlive()) {
            logger.info("Game Over! Final score: {}", score);
        }
    }

    private void renderGame() {
        var gc = gameStage.getGraphicsContext();

        // Clear screen with black background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameStage.getWidth(), gameStage.getHeight());

        // Render asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.render(gc);
        }

        // Render player if alive
        if (player.isAlive()) {
            player.render(gc);
            logger.debug("Player rendered at ({}, {})", player.getX(), player.getY());
        }

        // Draw score
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Score: " + score, 10, 30);
    }

    private void spawnAsteroids(int count) {
        for (int i = 0; i < count; i++) {
            // Spawn from edge of screen
            double x, y;
            if (Math.random() < 0.5) {
                // Spawn from sides
                x = Math.random() < 0.5 ? -30 : gameStage.getWidth() + 30;
                y = Math.random() * gameStage.getHeight();
            } else {
                // Spawn from top/bottom
                x = Math.random() * gameStage.getWidth();
                y = Math.random() < 0.5 ? -30 : gameStage.getHeight() + 30;
            }

            asteroids.add(new Asteroid(x, y, 1)); // Spawn large asteroid
            logger.debug("Spawned asteroid at ({}, {})", x, y);
        }
        logger.info("Spawned {} new asteroids. Total asteroids: {}", count, asteroids.size());
    }

    public void handleKeyPress(KeyCode code) {
        if (!player.isAlive()) return;

        switch (code) {
            case W:
            case UP:
                player.setMovingForward(true);
                break;
            case S:
            case DOWN:
                player.setMovingBackward(true);
                break;
            case A:
            case LEFT:
                player.setRotatingLeft(true);
                break;
            case D:
            case RIGHT:
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
            case UP:
                player.setMovingForward(false);
                break;
            case S:
            case DOWN:
                player.setMovingBackward(false);
                break;
            case A:
            case LEFT:
                player.setRotatingLeft(false);
                break;
            case D:
            case RIGHT:
                player.setRotatingRight(false);
                break;
        }
    }

    private void fireProjectile() {
        double projectileX = player.getX() + Math.cos(Math.toRadians(player.getRotation())) * 20;
        double projectileY = player.getY() + Math.sin(Math.toRadians(player.getRotation())) * 20;
        Projectile projectile = new Projectile(projectileX, projectileY, player.getRotation());
        projectiles.add(projectile);
        logger.debug("Projectile fired from ({}, {})", projectileX, projectileY);
    }

    // Getters for game state
    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }
}