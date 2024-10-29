package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BossProjectile extends Character {
    private static final double PROJECTILE_SPEED = 7.0;
    private static final double MAX_LIFETIME = 1.25; // seconds

    private double velocityX;
    private double velocityY;
    private double lifetime;
    private boolean isExpired;
    private final double screenWidth;
    private final double screenHeight;

    // pattern
    private ProjectilePattern pattern;
    private double spiralAngle = 0;
    private double spiralRadius = 0;
    private static final double SPIRAL_ANGULAR_SPEED = 5.0;
    private static final double SPIRAL_EXPANSION_RATE = 0.5;

    public enum ProjectilePattern {
        STRAIGHT,
        MULTI_SHOT
    }

    private static final String Idle = "/se233/astroboy/asset/player_ship.png";

    public BossProjectile(double x, double y, double rotation, double screenWidth, double screenHeight, ProjectilePattern pattern) {
        super(Idle, x, y, 6, 6); // Small projectile size
        this.rotation = rotation;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.lifetime = 0;
        this.isExpired = false;
        this.pattern = pattern;

       initializePattern(rotation);
    }
    private void initializePattern(double rotation) {
        double angleRad = Math.toRadians(rotation);
        switch (pattern) {
            case STRAIGHT:
            case MULTI_SHOT:
                this.velocityX = Math.cos(angleRad) * PROJECTILE_SPEED;
                this.velocityY = Math.sin(angleRad) * PROJECTILE_SPEED;
                break;

        }
    }


    @Override
    public void update() {

        switch (pattern) {
            case STRAIGHT:
                updateStraight();
            case MULTI_SHOT:
                x += velocityX;
                y += velocityY;
                break;
        }

        // Update lifetime
        lifetime += 0.020; // Assuming 60 FPS
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
    private void updateStraight() {
      x += velocityX;
      y += velocityY;
    }


    @Override
    public void render(GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.CYAN);
        gc.fillOval(x - width/2, y - height/2, width, height);

        // Optional: Add trailing effect
        gc.setStroke(Color.BLUE);
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

    public static BossProjectile[] createMultiShotPattern(double x, double y, double rotation,
                                                          double screenWidth, double screenHeight,
                                                          int bulletCount, double spreadAngle) {
        BossProjectile[] projectiles = new BossProjectile[bulletCount];

        // Calculate the angle between each bullet
        double angleStep = spreadAngle / (bulletCount - 1);
        // Calculate the starting angle to center the spread
        double startAngle = rotation - (spreadAngle / 2);

        for (int i = 0; i < bulletCount; i++) {
            // Calculate the angle for this bullet
            double bulletAngle = startAngle + (angleStep * i);

            projectiles[i] = new BossProjectile(
                    x, y,  // All bullets start from the same point
                    bulletAngle,  // Each bullet has a different angle
                    screenWidth, screenHeight,
                    ProjectilePattern.MULTI_SHOT
            );
        }

        return projectiles;
    }



}
