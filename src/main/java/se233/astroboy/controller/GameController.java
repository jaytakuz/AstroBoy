package se233.astroboy.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import se233.astroboy.model.*;
import se233.astroboy.view.GameStage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
    private GameState gameState = GameState.MENU;
    private int level;
    private int score;
    private double spawnTimer;
    private static final double SPAWN_INTERVAL = 3.0;

    // Menu animation
    private double textAlpha = 1.0;
    private double textAlphaChange = -0.02;

    // Bomb ability display
    private static final Color BOMB_READY_COLOR = Color.GREEN;
    private static final Color BOMB_COOLDOWN_COLOR = Color.RED;

    public GameController(GameStage gameStage) {
        this.gameStage = gameStage;
        this.isRunning = false;
        this.lifeIcon = new Image(getClass().getResourceAsStream("/se233/astroboy/asset/player_ship1.png"));
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
                if (lastUpdate == 0 || now - lastUpdate >= 16_666_666) {
                    updateGame();
                    renderGame();
                    lastUpdate = now;
                }
            }
        };

        // Spawn some asteroids for menu background
        spawnAsteroids(3);
    }

    private void updateGame() {
        switch (gameState) {
            case MENU:
                updateMenu();
                break;
            case PLAYING:
                updatePlaying();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
    }

    private void updateMenu() {
        // Update asteroids for background movement
        for (Asteroid asteroid : asteroids) {
            asteroid.update();
        }

        // Update text fade effect
        textAlpha += textAlphaChange;
        if (textAlpha <= 0 || textAlpha >= 1) {
            textAlphaChange *= -1;
        }
    }

    private void updatePlaying() {
        if (!player.isAlive()) {
            gameState = GameState.GAME_OVER;
            return;
        }

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
                    handleAsteroidDestruction(asteroid);
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
    }

    private void updateGameOver() {
        // Update text fade effect
        textAlpha += textAlphaChange;
        if (textAlpha <= 0 || textAlpha >= 1) {
            textAlphaChange *= -1;
        }
    }

    private void renderGame() {
        var gc = gameStage.getGraphicsContext();
        gc.clearRect(0, 0, gameStage.getStageWidth(), gameStage.getStageHeight());

        switch (gameState) {
            case MENU:
                renderMenu(gc);
                break;
            case PLAYING:
                renderPlaying(gc);
                break;
            case GAME_OVER:
                renderGameOver(gc);
                break;
        }
    }

    private void renderMenu(GraphicsContext gc) {
        // Render background asteroids
        for (Asteroid asteroid : asteroids) {
            asteroid.render(gc);
        }

        // Draw title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        double titleX = gameStage.getStageWidth() / 2;
        double titleY = gameStage.getStageHeight() / 3;
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ASTROBOY", titleX, titleY);

        // Draw blinking "PUSH SPACE TO START" text
        gc.setGlobalAlpha(textAlpha);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.fillText("PUSH SPACE TO START", titleX, gameStage.getStageHeight() / 2);
        gc.setGlobalAlpha(1.0);

        // Draw controls info
        gc.setFont(Font.font("Arial", 16));
        double infoY = gameStage.getStageHeight() * 0.7;
        gc.fillText("Controls:", titleX, infoY);
        gc.fillText("WASD - Move", titleX, infoY + 25);
        gc.fillText("LEFT/RIGHT - Rotate", titleX, infoY + 50);
        gc.fillText("SPACE - Shoot", titleX, infoY + 75);
        gc.fillText("B - Activate Bomb", titleX, infoY + 100);  // Added bomb control info
    }

    private void renderPlaying(GraphicsContext gc) {
        // Render game objects
        for (Asteroid asteroid : asteroids) {
            asteroid.render(gc);
        }

        for (Projectile projectile : projectiles) {
            projectile.render(gc);
        }

        for (Explosion explosion : explosions) {
            explosion.render(gc);
        }

        if (player.isAlive()) {
            player.render(gc);
        }

        // Draw HUD
        renderHUD(gc);
    }

    private void renderHUD(GraphicsContext gc) {
        // Draw score and lives
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 20));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Score: " + score, 10, 30);
        gc.fillText("Lives: ", 10, 60);

        // Draw life icons
        double iconSize = 20;
        double baseX = 70;
        double baseY = 45;
        double spacing = 25;
        for (int i = 0; i < player.getLives(); i++) {
            gc.drawImage(lifeIcon,
                    baseX + (i * spacing),
                    baseY,
                    iconSize,
                    iconSize);
        }

        // Draw bomb status
        double cooldown = player.getBombCooldown();
        String bombText = cooldown > 0
                ? String.format("Bomb: %.1fs", cooldown)
                : "Bomb: READY";

        gc.setFill(cooldown > 0 ? BOMB_COOLDOWN_COLOR : BOMB_READY_COLOR);
        gc.setFont(Font.font("Arial", 16));
        gc.fillText(bombText, 10, gameStage.getStageHeight() - 10);
    }

    private void renderGameOver(GraphicsContext gc) {
        // Render the final game state in background
        renderPlaying(gc);

        // Draw semi-transparent overlay
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gameStage.getStageWidth(), gameStage.getStageHeight());

        // Draw game over text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        gc.setTextAlign(TextAlignment.CENTER);
        double centerX = gameStage.getStageWidth() / 2;
        double centerY = gameStage.getStageHeight() / 2;

        gc.fillText("GAME OVER", centerX, centerY - 40);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Final Score: " + score, centerX, centerY + 10);

        gc.setGlobalAlpha(textAlpha);
        gc.fillText("Press SPACE to Play Again", centerX, centerY + 50);
        gc.setGlobalAlpha(1.0);
    }

    private void handleAsteroidDestruction(Asteroid asteroid) {
        asteroid.markForDestruction();
        score += asteroid.getPoints();
        explosions.add(new Explosion(
                asteroid.getX() + asteroid.getWidth()/2,
                asteroid.getY() + asteroid.getHeight()/2
        ));
        logger.info("Asteroid destroyed! Score: {}", score);
    }

    private Optional<Asteroid> findNearestAsteroid() {
        double shortestDistance = Double.MAX_VALUE;
        Asteroid nearest = null;

        for (Asteroid asteroid : asteroids) {
            double dx = asteroid.getX() - player.getX();
            double dy = asteroid.getY() - player.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearest = asteroid;
            }
        }

        return Optional.ofNullable(nearest);
    }

    private void activateBomb() {
        if (player.canUseBomb()) {
            findNearestAsteroid().ifPresent(asteroid -> {
                handleAsteroidDestruction(asteroid);
                player.useBomb();
                logger.info("Bomb used on nearest asteroid");
            });
        }
    }

    public void handleKeyPress(KeyCode code) {
        switch (gameState) {
            case MENU:
                if (code == KeyCode.SPACE) {
                    startNewGame();
                    gameState = GameState.PLAYING;
                }
                break;
            case PLAYING:
                handlePlayingKeyPress(code);
                break;
            case GAME_OVER:
                if (code == KeyCode.SPACE) {
                    startNewGame();
                    gameState = GameState.PLAYING;
                }
                break;
        }
    }

    private void handlePlayingKeyPress(KeyCode code) {
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
            case B:
                activateBomb();
                break;
        }
    }

    public void handleKeyRelease(KeyCode code) {
        if (gameState != GameState.PLAYING || !player.isAlive()) return;

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
            double spawnDistance = 20;
            double projectileX = player.getX() + Math.cos(angleRad) * spawnDistance;
            double projectileY = player.getY() + Math.sin(angleRad) * spawnDistance;

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

    private void startNewGame() {
        // Reset game state
        score = 0;
        level = 1;
        spawnTimer = SPAWN_INTERVAL;

        // Clear existing objects
        asteroids.clear();
        projectiles.clear();
        explosions.clear();

        // Reset player
        double centerX = gameStage.getStageWidth() / 2;
        double centerY = gameStage.getStageHeight() / 2;
        player = new Player(centerX, centerY, gameStage.getStageWidth(), gameStage.getStageHeight());

        // Spawn initial asteroids
        spawnAsteroids(3);

        logger.info("New game started");
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
        }
    }

    private int generateRandomAsteroidSize() {
        return Math.random() < 0.6 ? 1 : 2;
    }

    public void startGameLoop() {
        if (!isRunning) {
            gameLoop.start();
            isRunning = true;
            logger.info("Game loop started");
        }
    }
}