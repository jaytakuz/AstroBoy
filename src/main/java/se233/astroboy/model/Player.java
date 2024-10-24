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
    private double rotationSpeed = 5.0;

    // Velocity components
    private double velocityX = 0;
    private double velocityY = 0;

    // Game state
    private int lives;
    private boolean isInvulnerable;
    private double invulnerabilityTimer;

    // Movement flags
    private boolean isMovingForward;
    private boolean isMovingBackward;
    private boolean isRotatingLeft;
    private boolean isRotatingRight;

    public Player(double x, double y) {
        super(x, y, 30, 30); // Player size 30x30 pixels
        this.lives = 3;
        this.isInvulnerable = false;
        logger.info("Player created at position ({}, {})", x, y);
    }

    @Override
    public void update() {
        // Update movement
        updateMovement();

        // Update position
        x += velocityX;
        y += velocityY;

        // Apply screen wrapping
        wrapAroundScreen();

        // Update invulnerability
        updateInvulnerability();
    }

    private void updateMovement() {
        if (isMovingForward) {
            // Apply forward thrust
            double angleRad = Math.toRadians(rotation);
            velocityX += Math.cos(angleRad) * acceleration;
            velocityY += Math.sin(angleRad) * acceleration;
        }

        if (isMovingBackward) {
            // Apply backward thrust
            double angleRad = Math.toRadians(rotation);
            velocityX -= Math.cos(angleRad) * acceleration;
            velocityY -= Math.sin(angleRad) * acceleration;
        }

        // Apply rotation
        if (isRotatingLeft) {
            rotation -= rotationSpeed;
        }
        if (isRotatingRight) {
            rotation += rotationSpeed;
        }

        // Limit speed
        double currentSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (currentSpeed > maxSpeed) {
            velocityX = (velocityX / currentSpeed) * maxSpeed;
            velocityY = (velocityY / currentSpeed) * maxSpeed;
        }

        // Apply deceleration
        velocityX *= deceleration;
        velocityY *= deceleration;
    }

    private void wrapAroundScreen() {
        // Screen wrapping logic
        if (x < 0) x = 800;
        if (x > 800) x = 0;
        if (y < 0) y = 600;
        if (y > 600) y = 0;
    }

    private void updateInvulnerability() {
        if (isInvulnerable) {
            invulnerabilityTimer -= 0.016; // Assuming 60 FPS
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
                logger.debug("Player invulnerability ended");
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Save the current graphics context state
        gc.save();

        // Move to player position and rotate
        gc.translate(x, y);
        gc.rotate(rotation);

        // Draw the player ship (temporary triangle shape)
        gc.setFill(isInvulnerable ? Color.GRAY : Color.WHITE);
        gc.beginPath();
        gc.moveTo(width/2, 0);
        gc.lineTo(-width/2, width/2);
        gc.lineTo(-width/2, -width/2);
        gc.closePath();
        gc.fill();

        // Draw the player ship outline
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.stroke();

        // Restore the graphics context state
        gc.restore();
    }

    // Movement control methods
    public void setMovingForward(boolean moving) {
        this.isMovingForward = moving;
    }

    public void setMovingBackward(boolean moving) {
        this.isMovingBackward = moving;
    }

    public void setRotatingLeft(boolean rotating) {
        this.isRotatingLeft = rotating;
    }

    public void setRotatingRight(boolean rotating) {
        this.isRotatingRight = rotating;
    }

    // Game state methods
    public void hit() {
        if (!isInvulnerable) {
            lives--;
            isInvulnerable = true;
            invulnerabilityTimer = 2.0; // 2 seconds of invulnerability
            logger.info("Player hit! Lives remaining: {}", lives);
        }
    }

    // Add these getters
    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public int getLives() {
        return lives;
    }

    public boolean isAlive() {
        return lives > 0;
    }
}