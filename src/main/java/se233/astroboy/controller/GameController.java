package se233.astroboy.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import se233.astroboy.model.Explosion;
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
    private Image lifeIcon;

    // Game objects
    private Player player;
    private List<Asteroid> asteroids;
    private List<Projectile> projectiles;
    private List<Explosion> explosions;

    // Game state
    private int level;
    private int score;
    private double spawnTimer;
    private static final double SPAWN_INTERVAL = 3.0;

    public GameController(GameStage gameStage) {
        this.gameStage = gameStage;
        this.isRunning = false;
        this.lifeIcon = new Image(getClass().getResourceAsStream("/se233/astroboy/asset/player_ship.png"));
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
        explosions = new ArrayList<>();
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

        // Update explosions
        Iterator<Explosion> explosionIterator = explosions.iterator();
        while (explosionIterator.hasNext()) {
            Explosion explosion = explosionIterator.next();
            explosion.update();
            if (explosion.isFinished()) {
                explosionIterator.remove();
            }
        }

        // Update projectiles
        Iterator<Projectile> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            projectile.update();

            if (projectile.isExpired()) {
                projectileIterator.remove();
                continue;
            }

            // Check collisions with asteroids
            for (Asteroid asteroid : asteroids) {
                if (CollisionController.checkCollision(projectile, asteroid)) {
                    projectileIterator.remove();
                    asteroid.markForDestruction();
                    score += asteroid.getPoints();

                    explosions.add(new Explosion(asteroid.getX() + asteroid.getWidth()/2,
                            asteroid.getY() + asteroid.getHeight()/2));

                    logger.info("Asteroid hit! Score: {}", score);
                    break;
                }
            }
        }

        // Update asteroids
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            asteroid.update();

            if (asteroid.isMarkedForDestruction()) {
                asteroidIterator.remove();

            }
        }

        // Handle all collisions
        CollisionController.handleCollisions(player, asteroids, projectiles);

        // Update spawn timer
        spawnTimer -= 0.016;
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

        gc.clearRect(0, 0, gameStage.getStageWidth(), gameStage.getStageHeight());
        // Clear screen
//        gc.setFill(Color.DARKCYAN);
//        gc.fillRect(0, 0, gameStage.getStageWidth(), gameStage.getStageHeight());

        // Render asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.render(gc);
        }

        // Render projectiles
        for (Projectile projectile : projectiles) {
            projectile.render(gc);
        }

        // Render explosions
        for (Explosion explosion : explosions) {
            explosion.render(gc);
        }

        // Render player
        if (player.isAlive()) {
            player.render(gc);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Score: " + score, 10, 30);
        gc.fillText("Lives: ", 10, 60);

        // Draw life icons
        double iconSize = 20; // Size of each life icon
        double baseX = 70;    // Starting X position for life icons
        double baseY = 45;    // Y position for life icons (adjusted to align with "Lives:" text)
        double spacing = 25;  // Space between icons

        for (int i = 0; i < player.getLives(); i++) {
            gc.drawImage(lifeIcon,
                    baseX + (i * spacing),  // X position
                    baseY,                  // Y position
                    iconSize,               // Width
                    iconSize);              // Height
        }
    }

    public void handleKeyPress(KeyCode code) {
        if (!player.isAlive()) return;

        switch (code) {
            case W:
                player.setMovingForward(true);
                break;
            case S:
                player.setMovingBackward(true);
                break;
            case A:
                player.setMovingLeft(true);
                break;
            case D:
                player.setMovingRight(true);
                break;
            case LEFT:
                player.setRotatingLeft(true);
                break;
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
                player.setMovingForward(false);
                break;
            case S:
                player.setMovingBackward(false);
                break;
            case A:
                player.setMovingLeft(false);
                break;
            case D:
                player.setMovingRight(false);
                break;
            case LEFT:
                player.setRotatingLeft(false);
                break;
            case RIGHT:
                player.setRotatingRight(false);
                break;
        }
    }

    private void fireProjectile() {
        if (player.canShoot()) {
            // Calculate projectile spawn position (slightly in front of the ship)
            double angleRad = Math.toRadians(player.getRotation());
            double spawnDistance = 20; // Distance from ship center
            double projectileX = player.getX() + Math.cos(angleRad) * spawnDistance;
            double projectileY = player.getY() + Math.sin(angleRad) * spawnDistance;

            // Create and add projectile
            Projectile projectile = new Projectile(
                    projectileX, projectileY,
                    player.getRotation(),
                    gameStage.getStageWidth(),
                    gameStage.getStageHeight()
            );
            projectiles.add(projectile);
            player.resetShootCooldown();

            logger.debug("Projectile fired from ({}, {})", projectileX, projectileY);
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

            int asteroidSize = generateRandomAsteroidSize();
            asteroids.add(new Asteroid(x, y, asteroidSize));
            logger.debug("Spawned asteroid of size {} at ({}, {})", asteroidSize, x, y);
        }
    }

    private int generateRandomAsteroidSize() {
        // Generate random number between 0 and 1
        double random = Math.random();

        // Distribute sizes with different probabilities
        if (random < 0.6) {          // 50% chance for large asteroids
            return 1;                 // Large asteroid
        } else  {   // 30% chance for medium asteroids
            return 2;                 // Medium asteroid
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