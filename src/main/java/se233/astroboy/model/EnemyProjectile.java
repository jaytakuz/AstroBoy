package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyProjectile extends GameObject {
    private static final double PROJECTILE_SPEED = 8.0;
    private static final double MAX_LIFETIME = 1; // seconds

    private double velocityX;
    private double velocityY;
    private double lifetime;
    private boolean isExpired;
    private final double screenWidth;
    private final double screenHeight;

    private static final String Idle = "/se233/astroboy/asset/player_ship.png";

    public EnemyProjectile(double x, double y, double rotation, double screenWidth, double screenHeight) {
        super(Idle, x, y, 5, 5); // Small projectile size
        this.rotation = rotation;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.lifetime = 0;
        this.isExpired = false;

        // Calculate velocity based on rotation
        double angleRad = Math.toRadians(rotation);
        this.velocityX = Math.cos(angleRad) * PROJECTILE_SPEED;
        this.velocityY = Math.sin(angleRad) * PROJECTILE_SPEED;

    }

    @Override
    public void update() {
        // Update position
        x += velocityX;
        y += velocityY;

        // Update lifetime
        lifetime += 0.030; // Assuming 60 FPS
        if (lifetime >= MAX_LIFETIME) {
            isExpired = true;
            return;
        }

        // Wrap around screen
        if (x < 0) x = screenWidth;
        if (x > screenWidth) x = 0;
        if (y < 0) y = screenHeight;
        if (y > screenHeight) y = 0;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.BLUE);
        gc.fillOval(x - width/2, y - height/2, width, height);

        // Optional: Add trailing effect
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);
        double trailLength = 8;
        double angleRad = Math.toRadians(rotation + 180);
        gc.strokeLine(
                x, y,
                x + Math.cos(angleRad) * trailLength,
                y + Math.sin(angleRad) * trailLength
        );
        gc.restore();
    }

    public boolean isExpired() {
        return isExpired;
    }
}
