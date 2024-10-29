package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Asteroid;
import static org.junit.jupiter.api.Assertions.*;

public class AsteroidsMovementTest {
    private Asteroid asteroid;
    private static final double DELTA = 0.001; // Delta for floating point comparisons

    @BeforeEach
    void setUp() {
        // Initialize a test asteroid at position (100, 100) with size 1 (Large)
        asteroid = new Asteroid(100.0, 100.0, 1);
    }

    @Test
    void testInitialPosition() {
        assertEquals(100.0, asteroid.getX(), DELTA, "Initial X position should be 100.0");
        assertEquals(100.0, asteroid.getY(), DELTA, "Initial Y position should be 100.0");
    }

    @Test
    void testMovement() {
        // Store initial position
        double initialX = asteroid.getX();
        double initialY = asteroid.getY();

        // Update position
        asteroid.update();

        // Position should change due to speedX and speedY
        assertNotEquals(initialX, asteroid.getX(), "Asteroid should move in X direction");
        assertNotEquals(initialY, asteroid.getY(), "Asteroid should move in Y direction");
    }

    @Test
    void testScreenWrappingRight() {
        // Place asteroid near right edge
        Asteroid rightAsteroid = new Asteroid(799.0, 100.0, 1);

        // Force movement to the right
        for (int i = 0; i < 10; i++) {
            rightAsteroid.update();
        }

        // Check if asteroid wrapped around
        assertTrue(rightAsteroid.getX() < 800.0, "Asteroid should wrap around when moving past right edge");
    }

    @Test
    void testScreenWrappingLeft() {
        // Place asteroid near left edge
        Asteroid leftAsteroid = new Asteroid(1.0, 100.0, 1);

        // Force movement to the left
        for (int i = 0; i < 10; i++) {
            leftAsteroid.update();
        }

        // Check if asteroid wrapped around
        assertTrue(leftAsteroid.getX() > -70.0, "Asteroid should wrap around when moving past left edge");
    }

    @Test
    void testScreenWrappingTop() {
        // Place asteroid near top edge
        Asteroid topAsteroid = new Asteroid(100.0, 1.0, 1);

        // Force movement upward
        for (int i = 0; i < 10; i++) {
            topAsteroid.update();
        }

        // Check if asteroid wrapped around
        assertTrue(topAsteroid.getY() > -70.0, "Asteroid should wrap around when moving past top edge");
    }

    @Test
    void testScreenWrappingBottom() {
        // Place asteroid near bottom edge
        Asteroid bottomAsteroid = new Asteroid(100.0, 599.0, 1);

        // Force movement downward
        for (int i = 0; i < 10; i++) {
            bottomAsteroid.update();
        }

        // Check if asteroid wrapped around
        assertTrue(bottomAsteroid.getY() < 600.0, "Asteroid should wrap around when moving past bottom edge");
    }

    @Test
    void testRotation() {
        double initialRotation = asteroid.getRotation();
        asteroid.update();
        assertNotEquals(initialRotation, asteroid.getRotation(), "Asteroid should rotate over time");
    }

    @Test
    void testSizeInitialization() {
        // Test size 1 (Large)
        Asteroid largeAsteroid = new Asteroid(100.0, 100.0, 1);
        assertEquals(1, largeAsteroid.getSize(), "Asteroid size should be 1 (Large)");
        assertEquals(35.0, largeAsteroid.getWidth(), DELTA, "Large asteroid should have width 35.0");

        // Test size 2 (Medium)
        Asteroid mediumAsteroid = new Asteroid(100.0, 100.0, 2);
        assertEquals(2, mediumAsteroid.getSize(), "Asteroid size should be 2 (Medium)");
        assertEquals(70.0, mediumAsteroid.getWidth(), DELTA, "Medium asteroid should have width 70.0");
    }

    @Test
    void testInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Asteroid(100.0, 100.0, 3);
        }, "Creating asteroid with invalid size should throw IllegalArgumentException");
    }
}