package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player extends GameObject {
    private static final Logger logger = LogManager.getLogger(Player.class);

    // Movement properties
    private double maxSpeed = 5.0;
    private double acceleration = 0.2;
    private double deceleration = 0.98;
    private double brakeStrength = 0.95; // Stronger deceleration when braking
    private double rotationSpeed = 5.0;

    // Velocity components
    private double velocityX = 0;
    private double velocityY = 0;

    // Screen boundaries
    private final double screenWidth;
    private final double screenHeight;

    // Game state
    private int lives;
    private boolean isInvulnerable;
    private double invulnerabilityTimer;

    // Movement flags
    private boolean isMovingForward;
    private boolean isBraking;
    private boolean isRotatingLeft;
    private boolean isRotatingRight;

    // Shooting properties
    private double shootCooldown = 0.25; // 250ms between shots
    private double timeSinceLastShot = 0.25;

    public Player(double x, double y, double screenWidth, double screenHeight) {
        super(x, y, 20, 20);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.lives = 3;
        this.isInvulnerable = false;
        this.rotation = -90;  // Start facing upward
        logger.info("Player created at position ({}, {})", x, y);
    }

    @Override
    public void update() {
        // Update rotation
        if (isRotatingLeft) {
            rotation -= rotationSpeed;
        }
        if (isRotatingRight) {
            rotation += rotationSpeed;
        }

        // Update movement
        if (isMovingForward) {
            double angleRad = Math.toRadians(rotation);
            velocityX += Math.cos(angleRad) * acceleration;
            velocityY += Math.sin(angleRad) * acceleration;
        }

        // Apply braking if brake is engaged
        if (isBraking) {
            velocityX *= brakeStrength;
            velocityY *= brakeStrength;
        }

        // Apply velocity
        x += velocityX;
        y += velocityY;

        // Apply normal drag
        velocityX *= deceleration;
        velocityY *= deceleration;

        // Wrap around screen
        if (x < 0) x = screenWidth;
        if (x > screenWidth) x = 0;
        if (y < 0) y = screenHeight;
        if (y > screenHeight) y = 0;

        // Update invulnerability
        if (isInvulnerable) {
            invulnerabilityTimer -= 0.016;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
            }
        }

        // Update shooting cooldown
        if (timeSinceLastShot < shootCooldown) {
            timeSinceLastShot += 0.016;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();

        // Set colors
        gc.setFill(isInvulnerable ? Color.GRAY : Color.WHITE);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Calculate ship points
        double[] xPoints = new double[3];
        double[] yPoints = new double[3];

        double angleRad = Math.toRadians(rotation);
        double shipSize = 15;

        // Front point
        xPoints[0] = x + Math.cos(angleRad) * shipSize;
        yPoints[0] = y + Math.sin(angleRad) * shipSize;

        // Back points
        double backAngle = Math.PI * 0.8;
        xPoints[1] = x + Math.cos(angleRad + backAngle) * shipSize;
        yPoints[1] = y + Math.sin(angleRad + backAngle) * shipSize;
        xPoints[2] = x + Math.cos(angleRad - backAngle) * shipSize;
        yPoints[2] = y + Math.sin(angleRad - backAngle) * shipSize;

        // Draw ship
        gc.fillPolygon(xPoints, yPoints, 3);
        gc.strokePolygon(xPoints, yPoints, 3);

        // Draw brake effect when braking
        if (isBraking && (velocityX != 0 || velocityY != 0)) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(1);
            double brakeLength = 10;
            gc.strokeLine(
                    x - Math.cos(angleRad) * brakeLength,
                    y - Math.sin(angleRad) * brakeLength,
                    x - Math.cos(angleRad) * (brakeLength + 5),
                    y - Math.sin(angleRad) * (brakeLength + 5)
            );
        }

        gc.restore();
    }

    // Movement setters
    public void setMovingForward(boolean moving) {
        this.isMovingForward = moving;
    }

    public void setBraking(boolean braking) {
        this.isBraking = braking;
        if (braking) {
            logger.debug("Brakes engaged");
        }
    }

    public void setRotatingLeft(boolean rotating) {
        this.isRotatingLeft = rotating;
    }

    public void setRotatingRight(boolean rotating) {
        this.isRotatingRight = rotating;
    }

    // Shooting methods
    public boolean canShoot() {
        return timeSinceLastShot >= shootCooldown;
    }

    public void resetShootCooldown() {
        timeSinceLastShot = 0;
        logger.debug("Weapon cooldown reset");
    }

    // Game state methods
    public void hit() {
        if (!isInvulnerable) {
            lives--;
            isInvulnerable = true;
            invulnerabilityTimer = 2.0;
            logger.info("Player hit! Lives remaining: {}", lives);
        }
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public int getLives() {
        return lives;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    // Get current velocity for external use
    public double getCurrentSpeed() {
        return Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    }
}