package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player extends GameObject {
    private static final Logger logger = LogManager.getLogger(Player.class);

    private static final String IDLE = "/se233/astroboy/asset/player_ship.png";
    private static final String Move = "/se233/astroboy/asset/player_ani.png";
    private static final String Hit = "/se233/astroboy/asset/explosion.png";

    private Image HitImage;// Sprite sheet for invulnerability effect
    private int HitFrame = 0;
    private double HitAnimationTimer = 0;
    private static final double Hit_FRAME_DURATION = 0.1; // 100ms per frame
    private static final int Hit_FRAME_COUNT = 5;
    private Image idleImage; // Add separate image for idle state
    private PlayerState currentState = PlayerState.IDLE;

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
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isRotatingLeft;
    private boolean isRotatingRight;

    // Shooting properties
    private double shootCooldown = 0.25; // 250ms between shots
    private double timeSinceLastShot = 0.25;

    private enum PlayerState {
        IDLE,
        MOVING
    }


    public Player(double x, double y, double screenWidth, double screenHeight) {
        super(Move,x, y, 20, 20);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.lives = 3;
        this.isInvulnerable = false;
        this.rotation = -90;  // Start facing upward

        try {
            this.idleImage = new Image(getClass().getResourceAsStream(IDLE));
            this.HitImage = new Image(getClass().getResourceAsStream(Hit));

        } catch (Exception e) {
            logger.error("Failed to load idle image: " + e.getMessage());
        }

        initializeAnimation(32, 32, 6, 0.1);
        logger.info("Player created at position ({}, {})", x, y);
    }

    @Override
    public void update() {

        if (isMovingForward ) {
            currentState = PlayerState.MOVING;
            updateAnimation(0.016);
        } else {
            currentState = PlayerState.IDLE;
        }

        // Update rotation
        if (isRotatingLeft) {
            rotation -= rotationSpeed;
        }
        if (isRotatingRight) {
            rotation += rotationSpeed;
        }

        // Update movement
        double angleRad = Math.toRadians(rotation);
        if (isMovingForward) {
            velocityX += Math.cos(angleRad) * acceleration;
            velocityY += Math.sin(angleRad) * acceleration;
        }
        if (isMovingBackward) {
            velocityX -= Math.cos(angleRad) * acceleration;
            velocityY -= Math.sin(angleRad) * acceleration;
        }
        // Left movement (perpendicular to forward, 90 degrees counterclockwise)
        if (isMovingLeft) {
            velocityX += Math.cos(angleRad - Math.PI / 2) * acceleration;
            velocityY += Math.sin(angleRad - Math.PI / 2) * acceleration;
        }

        // Right movement (perpendicular to forward, 90 degrees clockwise)
        if (isMovingRight) {
            velocityX += Math.cos(angleRad + Math.PI / 2) * acceleration;
            velocityY += Math.sin(angleRad + Math.PI / 2) * acceleration;
        }

        // Limit speed
        double currentSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (currentSpeed > maxSpeed) {
            velocityX = (velocityX / currentSpeed) * maxSpeed;
            velocityY = (velocityY / currentSpeed) * maxSpeed;
        }

        // Apply velocity
        x += velocityX;
        y += velocityY;

        // Apply drag
        velocityX *= deceleration;
        velocityY *= deceleration;

        // Wrap around screen
        if (x < 0) x = screenWidth;
        if (x > screenWidth) x = 0;
        if (y < 0) y = screenHeight;
        if (y > screenHeight) y = 0;

        // Update invulnerability
        if (isInvulnerable) {
            HitAnimationTimer += 0.016;
            if (HitAnimationTimer >= Hit_FRAME_DURATION) {
                HitFrame = (HitFrame + 1) % Hit_FRAME_COUNT;
                HitAnimationTimer = 0;
            }

            invulnerabilityTimer -= 0.016;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
                HitFrame = 0;
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

        // Calculate drawing position
        double drawX = x - frameWidth / 2;
        double drawY = y - frameHeight / 2;

        // Apply rotation
        gc.translate(x, y);
        gc.rotate(rotation + 90);
        gc.translate(-x, -y);

        // Apply invulnerability effect
        if (isInvulnerable && Math.floor(invulnerabilityTimer * 10) % 2 == 0) {
            gc.setGlobalAlpha(0.5);
        }

        // Draw based on current state
        if (currentState == PlayerState.IDLE) {
            // Draw idle image
            if (idleImage != null) {
                gc.drawImage(
                        idleImage,
                        drawX, drawY,
                        frameWidth, frameHeight
                );
            }
        } else {
            // Draw animation frame from sprite sheet
            if (spriteSheet != null) {
                double sourceX = currentFrame * frameWidth;
                double sourceY = 0;
                gc.drawImage(
                        spriteSheet,
                        sourceX, sourceY,
                        frameWidth, frameHeight,
                        drawX, drawY,
                        frameWidth, frameHeight
                );
            }
        }

        if (isInvulnerable && HitImage != null) {
            double effectSourceX = HitFrame * frameWidth;
            gc.setGlobalAlpha(0.7); // Make the effect slightly transparent
            gc.drawImage(
                    HitImage,
                    effectSourceX, 0,
                    frameWidth, frameHeight,
                    drawX, drawY,
                    frameWidth, frameHeight
            );
            gc.setGlobalAlpha(1.0);
        }

        gc.restore();
    }

    // Movement setters
    public void setMovingForward(boolean moving) {
        this.isMovingForward = moving;
        if (moving) {
            logger.debug("Moving forward");
        }
    }

    public void setMovingBackward(boolean moving) {
        this.isMovingBackward = moving;
        if (moving) {
            logger.debug("Moving backward");
        }
    }

    public void setMovingLeft(boolean moving) {
        this.isMovingLeft = moving;
        if (moving) {
            logger.debug("Moving left");
        }
    }

    public void setMovingRight(boolean moving) {
        this.isMovingRight = moving;
        if (moving) {
            logger.debug("Moving right");
        }
    }

    public void setRotatingLeft(boolean rotating) {
        this.isRotatingLeft = rotating;
        if (rotating) {
            logger.debug("Rotating left");
        }
    }

    public void setRotatingRight(boolean rotating) {
        this.isRotatingRight = rotating;
        if (rotating) {
            logger.debug("Rotating right");
        }
    }

    // Shooting methods
    public boolean canShoot() {
        return timeSinceLastShot >= shootCooldown;
    }

    public void resetShootCooldown() {
        timeSinceLastShot = 0;
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

    public double getShipAngle() {
        return rotation;
    }
}