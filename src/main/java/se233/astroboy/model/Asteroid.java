package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Asteroid extends GameObject {
    private static final Logger logger = LogManager.getLogger(Asteroid.class);
    private double rotationSpeed;
    private double speedX;
    private double speedY;
    private int size; // 1:Large, 2:Medium, 3:Small
    private int points; // Points awarded when destroyed
    private boolean markedForDestruction;
    private Image sprite;

    private static final String image = "/se233/astroboy/asset/asteroid.png";

    public Asteroid(double x, double y, int size) {
        super(image, x, y, getAsteroidSize(size), getAsteroidSize(size));
        this.size = size;
        this.markedForDestruction = false;
        initializeAsteroid();
        try {
            this.sprite = new Image(getClass().getResourceAsStream(image));
            if (this.sprite == null) {
                logger.error("Failed to load asteroid image");
            }
        } catch (Exception e) {
            logger.error("Error loading asteroid image: " + e.getMessage());
        }
    }

    private static double getAsteroidSize(int size) {
        switch(size) {
            case 1: return 60; // Large
            case 2: return 30; // Medium
            case 3: return 15; // Small
            default: return 60;
        }
    }

    private void initializeAsteroid() {
        // Random movement direction
        double angle = Math.random() * Math.PI * 2;
        double speed = 3 + Math.random() * 2;

        switch(size) {
            case 1: // Large
                speed *= 1.0;
                points = 1;
                break;
            case 2: // Medium
                speed *= 1.0;
                points = 2;
                break;
            case 3: // Small
                speed *= 1.0;
                points = 100;
                break;
        }

        speedX = Math.cos(angle) * speed;
        speedY = Math.sin(angle) * speed;
        rotationSpeed = (Math.random() - 0.5) * 4;
        rotation = Math.random() * 360;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;
        rotation += rotationSpeed;
        wrapAroundScreen();
    }

    private void wrapAroundScreen() {
        if (x < -width) x = 800;
        if (x > 800) x = -width;
        if (y < -height) y = 600;
        if (y > 600) y = -height;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (sprite != null) {
            gc.save();

            // Calculate the center point for rotation
            double centerX = x + width / 2;
            double centerY = y + height / 2;

            // Translate to rotation point
            gc.translate(centerX, centerY);
            gc.rotate(rotation);
            gc.translate(-centerX, -centerY);

            // Draw the image
            gc.drawImage(sprite,
                    x, y,
                    width, height);

            gc.restore();
        } else {
            logger.warn("Asteroid sprite is null, cannot render");
        }
    }

    public Asteroid[] split() {
        if (size >= 3) return new Asteroid[0];
        Asteroid[] fragments = new Asteroid[2];
        int newSize = size + 1;
        fragments[0] = new Asteroid(x, y, newSize);
        fragments[1] = new Asteroid(x, y, newSize);
        return fragments;
    }

    public void markForDestruction() {
        this.markedForDestruction = true;
    }

    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    public int getPoints() {
        return points;
    }
}