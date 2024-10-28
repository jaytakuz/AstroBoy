package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    private BossState currentState = BossState.MOVING;

    private int maxHp;
    private int currentHp;

    private static final double SHOOT_COOLDOWN = 1.5; // Seconds between shots
    private double currentShootCooldown = 0;
    private Player targetPlayer;

    private static final String image = "/se233/astroboy/asset/boss.png";

    private int currentFrame;
    private double animationTimer;
    private static final int FRAME_COUNT = 2;
    private static final double FRAME_DURATION = 0.2;

    private static final double HP_BAR_WIDTH = 100;  // Wider bar for boss
    private static final double HP_BAR_HEIGHT = 8;   // Taller bar for boss
    private static final Color HP_BAR_BORDER = Color.WHITE;
    private static final Color HP_BAR_BACKGROUND = Color.RED;
    private static final Color HP_BAR_FILL = Color.rgb(255, 215, 0);

    private enum BossState {
        MOVING
    }
    public Boss(double x, double y, int size, Player player) {
        super(image, x, y, 80, 70);
        this.size = size;
        this.markedForDestruction = false;
        this.targetPlayer =  player;
        this.currentShootCooldown = Math.random() * SHOOT_COOLDOWN;
        initializeBoss();
        loadBossImage();
        initializeHp();
    }



    private void initializeHp() {
        switch(this.size) {
            case 1: // Large
                this.maxHp = 5;
                break;

        }
        this.currentHp = this.maxHp;
    }

    // Add method to handle taking damage
    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp <= 0) {
            markForDestructionBoss();
        }
    }

    // Add getters for HP
    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    private void loadBossImage() {
        try {
            String imagePath = getImagePathForBossSize(size);
            this.bossImage = new Image(getClass().getResourceAsStream(imagePath));
            if (this.bossImage == null) {
                logger.error("Failed to load enemy image for size: {}", size);
            }
        } catch (Exception e) {
            logger.error("Error loading asteroid image: {}", e.getMessage());
        }
    }

    private static String getImagePathForBossSize(int size) {
        return switch(size) {
            case 1 -> image;


            default -> throw new IllegalArgumentException("Invalid enemy image: " + size);
        };
    }



    private static String getBossSize(int size) {
        return switch(size) {
            case 1 -> image;


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
                width = 80;
                height = 70;
                initializeAnimation(80,70,2,0.2);
                break;

            default:
                throw new IllegalArgumentException("Invalid enemy size: " + size);
        }

//        speedX = Math.cos(angle) * speed;
//        speedY = Math.sin(angle) * speed;
        rotationSpeed = (Math.random() - 0.5) * 20;
        rotation = Math.random() * 360;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;
        currentState = BossState.MOVING;
        animationTimer += 0.016; // Assuming 60 FPS

        if (animationTimer >= FRAME_DURATION) {
            currentFrame = (currentFrame + 1) % FRAME_COUNT;
            animationTimer = 0;
        }

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
        currentShootCooldown = SHOOT_COOLDOWN ;
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
            // Calculate the source rectangle for the current frame
            double sourceX = currentFrame * width;
            // Draw the image centered
            gc.drawImage(bossImage,
                    sourceX, 0, width, height,  // source rectangle (sprite sheet coordinates)
                    -width/2, -height/2, width, height  // destination rectangle (screen coordinates)
            );

            gc.restore();
            renderHPBar(gc);

        } else {
            logger.warn("Boss sprite is null, cannot render");
        }
    }
    private void renderHPBar(GraphicsContext gc) {
        // Calculate HP bar position (above the boss)
        double hpBarX = x + (width - HP_BAR_WIDTH) / 2;
        double hpBarY = y - 20;  // 20 pixels above the boss

        // Draw background (empty health)
        gc.setFill(HP_BAR_BACKGROUND);
        gc.fillRect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);

        // Draw filled portion
        double fillWidth = (HP_BAR_WIDTH * currentHp) / maxHp;
        gc.setFill(HP_BAR_FILL);
        gc.fillRect(hpBarX, hpBarY, fillWidth, HP_BAR_HEIGHT);

        // Draw border
        gc.setStroke(HP_BAR_BORDER);
        gc.setLineWidth(2);  // Thicker border for boss
        gc.strokeRect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);

        // Draw HP text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        String hpText = currentHp + "/" + maxHp;
        gc.fillText(hpText, hpBarX + HP_BAR_WIDTH/2 - 15, hpBarY - 5);
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
