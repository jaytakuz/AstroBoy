package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Boss extends GameObject{
    private static final Logger logger = LogManager.getLogger(Enemy.class);
    private double rotationSpeed;
    private double speedX;
    private double speedY;
    private double speed;
    private int size; // 1:Large, 2:Medium,
    private int points; // Points awarded when destroyed
    private boolean markedForDestruction;
    private Image bossImage;

    private static final double SHOOT_COOLDOWN = 2.0; // Seconds between shots
    private double currentShootCooldown = 0;
    private Player targetPlayer;

    private static final String image = "/se233/astroboy/asset/boss.png";


    public Boss(double x, double y, int size, Player player) {
        super(getImagePathForEnemySize(size), x, y, getEnemySize(size), getEnemySize(size));
        this.size = size;
        this.markedForDestruction = false;
        this.targetPlayer =  player;
        this.currentShootCooldown = Math.random() * SHOOT_COOLDOWN;
        initializeBoss();
        loadBossImage();

    }

    private void loadBossImage() {
        try {
            String imagePath = getImagePathForEnemySize(size);
            this.bossImage = new Image(getClass().getResourceAsStream(imagePath));
            if (this.bossImage == null) {
                logger.error("Failed to load enemy image for size: {}", size);
            }
        } catch (Exception e) {
            logger.error("Error loading asteroid image: {}", e.getMessage());
        }
    }

    private static String getImagePathForEnemySize(int size) {
        return switch(size) {
            case 1 -> image;


            default -> throw new IllegalArgumentException("Invalid enemy image: " + size);
        };
    }



    private static double getEnemySize(int size) {
        return switch(size) {
            case 1 -> 128.0;


            default -> throw new IllegalArgumentException("Invalid asteroid size: " + size);
        };
    }

    private void initializeBoss() {
        // Random movement direction
//        double angle = Math.random() * Math.PI * 2;
//        double speed = 3 + Math.random() * 2;

        //    double angle = Math.random() * Math.PI * 2;
        this. speed = 3 + Math.random() * 2;

        switch(this.size) {
            case 1: // Large
                speed *= 0.7;
                points = 10;
                break;

            default:
                throw new IllegalArgumentException("Invalid enemy size: " + size);
        }

//        speedX = Math.cos(angle) * speed;
//        speedY = Math.sin(angle) * speed;
        rotationSpeed = (Math.random() - 0.5) * 7;
        rotation = Math.random() * 360;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;

        if (targetPlayer != null && targetPlayer.isAlive()) {

            double dx = targetPlayer.getX() - x;
            double dy = targetPlayer.getY() - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                x += (dx / distance) * speed;
                y += (dy / distance) * speed;
            }

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
        if (bossImage != null) {
            gc.save();

            gc.translate(x + width/2, y + height/2);
            // Rotate to face player
            gc.rotate(rotation + 90);
            // Draw the image centered
            gc.drawImage(bossImage, -width/2, -height/2, width, height);

            gc.restore();
        } else {
            logger.warn("Boss sprite is null, cannot render");
        }
    }

    public void markForDestructionBoss() {
        this.markedForDestruction = true;
    }

    public boolean isMarkedForDestructionBoss() {
        return markedForDestruction;
    }

    public int getPointsBoss() {
        return points;
    }

    public int getSize() {
        return size;
    }
}
