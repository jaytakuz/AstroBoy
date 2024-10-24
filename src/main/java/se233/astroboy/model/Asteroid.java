// Asteroid objects
package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Asteroid extends GameObject {
    private static final Logger logger = LogManager.getLogger(Asteroid.class);

    private double rotationSpeed;
    private double speedX;
    private double speedY;
    private int size;  // 1:Large, 2:Medium, 3:Small
    private int points; // Points awarded when destroyed

    public Asteroid(double x, double y, int size) {
        super(x, y, getAsteroidSize(size), getAsteroidSize(size));
        this.size = size;
        initializeAsteroid();
        logger.debug("Created {} asteroid at ({}, {})", getSizeString(), x, y);
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
        double speed = 1 + Math.random() * 2; // Base speed between 1 and 3

        // Adjust speed and points based on size
        switch(size) {
            case 1: // Large
                speed *= 0.5;
                points = 20;
                break;
            case 2: // Medium
                speed *= 1.0;
                points = 50;
                break;
            case 3: // Small
                speed *= 1.5;
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
        // Update position
        x += speedX;
        y += speedY;

        // Update rotation
        rotation += rotationSpeed;

        // Wrap around screen
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
        gc.save();
        gc.translate(x, y);
        gc.rotate(rotation);

        // Draw asteroid
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Create irregular polygon shape
        gc.beginPath();
        int vertices = 8 + size; // More vertices for larger asteroids
        for (int i = 0; i < vertices; i++) {
            double angle = (Math.PI * 2 * i) / vertices;
            double radius = width/2 * (0.8 + Math.random() * 0.4); // Irregular radius
            double px = Math.cos(angle) * radius;
            double py = Math.sin(angle) * radius;
            if (i == 0) {
                gc.moveTo(px, py);
            } else {
                gc.lineTo(px, py);
            }
        }
        gc.closePath();
        gc.stroke();

        gc.restore();
    }

    public Asteroid[] split() {
        if (size >= 3) return new Asteroid[0]; // Smallest asteroids don't split

        // Create two smaller asteroids
        Asteroid[] fragments = new Asteroid[2];
        int newSize = size + 1;

        fragments[0] = new Asteroid(x, y, newSize);
        fragments[1] = new Asteroid(x, y, newSize);

        logger.debug("Asteroid split into two size {} asteroids", getSizeString(newSize));
        return fragments;
    }

    public int getPoints() {
        return points;
    }

    private String getSizeString() {
        return getSizeString(this.size);
    }

    private String getSizeString(int size) {
        switch(size) {
            case 1: return "LARGE";
            case 2: return "MEDIUM";
            case 3: return "SMALL";
            default: return "UNKNOWN";
        }
    }
}