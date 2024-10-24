package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Projectile extends GameObject {
    private static final Logger logger = LogManager.getLogger(Projectile.class);

    private double speedX;
    private double speedY;
    private boolean isMarkedForRemoval;
    private static final double PROJECTILE_SPEED = 10.0;
    private static final double MAX_LIFETIME = 2.0; // seconds
    private double lifetime;

    public Projectile(double x, double y, double rotation) {
        super(x, y, 4, 4); // Small projectile size
        this.rotation = rotation;

        // Calculate velocity based on rotation
        double angleRad = Math.toRadians(rotation);
        this.speedX = Math.cos(angleRad) * PROJECTILE_SPEED;
        this.speedY = Math.sin(angleRad) * PROJECTILE_SPEED;

        this.isMarkedForRemoval = false;
        this.lifetime = 0;

        logger.debug("Projectile created at ({}, {}) with rotation {}", x, y, rotation);
    }

    @Override
    public void update() {
        // Update position
        x += speedX;
        y += speedY;

        // Update lifetime
        lifetime += 0.016; // Assuming 60 FPS
        if (lifetime >= MAX_LIFETIME) {
            isMarkedForRemoval = true;
        }

        // Handle screen wrapping
        if (x < 0) x = 800;
        if (x > 800) x = 0;
        if (y < 0) y = 600;
        if (y > 600) y = 0;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.RED);
        gc.fillOval(x - width/2, y - height/2, width, height);
        gc.restore();
    }

    public boolean isMarkedForRemoval() {
        return isMarkedForRemoval;
    }

    public void markForRemoval() {
        this.isMarkedForRemoval = true;
    }
}