package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Enemy extends GameObject {
    private static final Logger logger = LogManager.getLogger(Enemy.class);
    private double rotationSpeed;
    private double speedX;
    private double speedY;
    private int size; // 1:Large, 2:Medium,
    private int points; // Points awarded when destroyed
    private boolean markedForDestruction;
    private Image enemyImage;

    private int maxHp;
    private int currentHp;

    private static final double SHOOT_COOLDOWN = 2.0; // Seconds between shots
    private double currentShootCooldown = 0;
    private Player targetPlayer;

    private static final String image1 = "/se233/astroboy/asset/player_ship.png";
    private static final String image2 = "/se233/astroboy/asset/enemy.png";

    private static final double HP_BAR_WIDTH = 40;
    private static final double HP_BAR_HEIGHT = 4;
    private static final Color HP_BAR_BORDER = Color.WHITE;
    private static final Color HP_BAR_BACKGROUND = Color.RED;
    private static final Color HP_BAR_FILL = Color.GREEN;

    public Enemy(double x, double y, int size, Player player) {
        super(getImagePathForEnemySize(size), x, y, getEnemySize(size), getEnemySize(size));
        this.size = size;
        this.markedForDestruction = false;
        this.targetPlayer =  player;
        this.currentShootCooldown = Math.random() * SHOOT_COOLDOWN;
        initializeHp();
        initializeEnemy();
        loadEnemyImage();
    }

    // Add new method to initialize HP
    private void initializeHp() {
        switch(this.size) {
            case 1: // Large
                this.maxHp = 1;
                break;
            case 2: // Medium
                this.maxHp = 2;
                break;
            default:
                this.maxHp = 1;
        }
        this.currentHp = this.maxHp;
    }

    // Add method to handle taking damage
    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp <= 0) {
            markForDestructionEnemy();
        }
    }

    // Add getters for HP
    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    private void loadEnemyImage() {
        try {
            String imagePath = getImagePathForEnemySize(size);
            this.enemyImage = new Image(getClass().getResourceAsStream(imagePath));
            if (this.enemyImage == null) {
                logger.error("Failed to load enemy image for size: {}", size);
            }
        } catch (Exception e) {
            logger.error("Error loading asteroid image: {}", e.getMessage());
        }
    }

    private static String getImagePathForEnemySize(int size) {
        return switch(size) {
            case 1 -> image1; // Enemy1
            case 2 -> image2; // Enemy2

            default -> throw new IllegalArgumentException("Invalid enemy image: " + size);
        };
    }

    private static double getEnemySize(int size) {
        return switch(size) {
            case 1 -> 32.0; // Medium
            case 2 -> 64.0; // Large

            default -> throw new IllegalArgumentException("Invalid asteroid size: " + size);
        };
    }

    private void initializeEnemy() {
        // Random movement direction
        double angle = Math.random() * Math.PI * 2;
        double speed = 3 + Math.random() * 2;

        switch(this.size) {
            case 1: // Large
                speed *= 0.8;
                points = 1;
                break;
            case 2: // Medium
                speed *= 0.6;
                points = 2;
                break;
            default:
                throw new IllegalArgumentException("Invalid enemy size: " + size);
        }

        speedX = Math.cos(angle) * speed;
        speedY = Math.sin(angle) * speed;
        rotationSpeed = (Math.random() - 0.5) * 7;
        rotation = Math.random() * 360;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;
        if (targetPlayer != null && targetPlayer.isAlive()) {
            double targetAngle = getAngleToPlayer();
            // Smoothly rotate towards player
            double angleDiff = targetAngle - rotation;
            // Normalize angle difference to -180 to 180
            while (angleDiff > 180) angleDiff -= 360;
            while (angleDiff < -180) angleDiff += 360;
            // Rotate towards player with smooth movement
            rotation += Math.signum(angleDiff) * Math.min(Math.abs(angleDiff), 3.0);
        }


        if (currentShootCooldown > 0) {
            currentShootCooldown -= 0.016; // Assuming 60 FPS
        }
        wrapAroundScreen();
    }

    public boolean canShoot() {
        return currentShootCooldown <= 0;
    }

    public void resetShootCooldown() {
        currentShootCooldown = SHOOT_COOLDOWN * (0.8 + Math.random() * 0.4);
    }

    public double getAngleToPlayer() {
        if (targetPlayer == null) return rotation;

        double dx = targetPlayer.getX() - x;
        double dy = targetPlayer.getY() - y;
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    private void wrapAroundScreen() {
        if (x < -width) x = 800;
        if (x > 800) x = -width;
        if (y < -height) y = 600;
        if (y > 600) y = -height;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (enemyImage != null) {
            gc.save();

            gc.translate(x + width/2, y + height/2);
            // Rotate to face player
            gc.rotate(rotation + 90);
            // Draw the image centered
            gc.drawImage(enemyImage, -width/2, -height/2, width, height);

            gc.restore();
            renderHpBar(gc);
        } else {
            logger.warn("Enemy sprite is null, cannot render");
        }
    }

    private void renderHpBar(GraphicsContext gc) {

        // Calculate HP bar position (above the enemy)
        double hpBarX = x + (width - HP_BAR_WIDTH) / 2;
        double hpBarY = y - 10;  // 10 pixels above the enemy

        // Draw background (empty health)
        gc.setFill(HP_BAR_BACKGROUND);
        gc.fillRect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);

        // Draw filled portion
        double fillWidth = (HP_BAR_WIDTH * currentHp) / maxHp;
        gc.setFill(HP_BAR_FILL);
        gc.fillRect(hpBarX, hpBarY, fillWidth, HP_BAR_HEIGHT);

        // Draw border
        gc.setStroke(HP_BAR_BORDER);
        gc.setLineWidth(1);
        gc.strokeRect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
    }

    public void markForDestructionEnemy() {
        this.markedForDestruction = true;
    }

    public boolean isMarkedForDestructionEnemy() {
        return markedForDestruction;
    }

    public int getPointsEnemy() {
        return points;
    }

    public int getSize() {
        return size;
    }
}
