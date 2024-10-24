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

    // Screen boundaries
    private final double screenWidth;
    private final double screenHeight;

    // Game state
    private int lives;
    private boolean isInvulnerable;
    private double invulnerabilityTimer;

    // Movement flags
    private boolean isMovingForward;
    private boolean isMovingBackward;
    private boolean isRotatingLeft;
    private boolean isRotatingRight;

    // Shooting properties
    private double shootCooldown = 0.25; // 250ms between shots
    private double timeSinceLastShot = 0;

    public Player(double x, double y, double screenWidth, double screenHeight) {
        super(x, y, 20, 20); // Smaller ship size
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.lives = 3;
        this.isInvulnerable = false;
        this.invulnerabilityTimer = 0;
        this.rotation = -90; // Point upward initially
        logger.info("Player created with dimensions: {}x{} at position ({}, {})", width, height, x, y);
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

        // Update shooting cooldown
        if (timeSinceLastShot < shootCooldown) {
            timeSinceLastShot += 0.016; // Assuming 60 FPS
        }
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
        if (x < 0) x = screenWidth;
        if (x > screenWidth) x = 0;
        if (y < 0) y = screenHeight;
        if (y > screenHeight) y = 0;
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

        // Set colors before drawing
        gc.setFill(isInvulnerable ? Color.GRAY : Color.WHITE);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Calculate ship points
        double[] xPoints = new double[3];
        double[] yPoints = new double[3];

        // Front of ship
        xPoints[0] = x + Math.cos(Math.toRadians(rotation)) * (width/2);
        yPoints[0] = y + Math.sin(Math.toRadians(rotation)) * (width/2);

        // Back left
        xPoints[1] = x + Math.cos(Math.toRadians(rotation + 140)) * (width/2);
        yPoints[1] = y + Math.sin(Math.toRadians(rotation + 140)) * (width/2);

        // Back right
        xPoints[2] = x + Math.cos(Math.toRadians(rotation - 140)) * (width/2);
        yPoints[2] = y + Math.sin(Math.toRadians(rotation - 140)) * (width/2);

        // Draw filled triangle
        gc.fillPolygon(xPoints, yPoints, 3);

        // Draw outline
        gc.strokePolygon(xPoints, yPoints, 3);

        // Restore the graphics context state
        gc.restore();

        // Debug visualization of ship's center
        gc.setFill(Color.RED);
        gc.fillOval(x - 2, y - 2, 4, 4);
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

    // Getters
    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public int getLives() {
        return lives;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public boolean canShoot() {
        return timeSinceLastShot >= shootCooldown;
    }

    public void resetShootCooldown() {
        timeSinceLastShot = 0;
    }
}