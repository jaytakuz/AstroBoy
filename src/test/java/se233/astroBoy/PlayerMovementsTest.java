package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Player;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerMovementsTest {
    private Player player;
    private final double SCREEN_WIDTH = 800;
    private final double SCREEN_HEIGHT = 600;
    private final double DELTA = 0.01; // Delta for floating point comparisons
    private final double INITIAL_ROTATION = -90;

    @BeforeEach
    void setUp() {
        // Initialize player at center of screen
        player = new Player(SCREEN_WIDTH/2, SCREEN_HEIGHT/2, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Test
    void testInitialPosition() {
        assertEquals(SCREEN_WIDTH/2, player.getX(), DELTA);
        assertEquals(SCREEN_HEIGHT/2, player.getY(), DELTA);
        assertEquals(INITIAL_ROTATION, player.getRotation(), DELTA);
    }

    @Test
    void testForwardMovement() {
        double initialX = player.getX();
        double initialY = player.getY();

        player.setMovingForward(true);
        player.update();

        // Since rotation is 0, player should move up (negative Y)
        assertEquals(initialX, player.getX(), DELTA);
        assertTrue(player.getY() < initialY);
    }

    @Test
    void testBackwardMovement() {
        double initialX = player.getX();
        double initialY = player.getY();

        player.setMovingBackward(true);
        player.update();

        // Since rotation is 0, player should move down (positive Y)
        assertEquals(initialX, player.getX(), DELTA);
        assertTrue(player.getY() > initialY);
    }

    @Test
    void testLeftMovement() {
        double initialX = player.getX();
        double initialY = player.getY();

        player.setMovingLeft(true);
        player.update();

        // Player should move left (negative X)
        assertTrue(player.getX() < initialX);
        assertEquals(initialY, player.getY(), DELTA);
    }

    @Test
    void testRightMovement() {
        double initialX = player.getX();
        double initialY = player.getY();

        player.setMovingRight(true);
        player.update();

        // Player should move right (positive X)
        assertTrue(player.getX() > initialX);
        assertEquals(initialY, player.getY(), DELTA);
    }

    @Test
    void testRotation() {
        double initialRotation = player.getRotation();

        player.setRotatingLeft(true);
        player.update();
        assertTrue(player.getRotation() < initialRotation);

        // Reset rotation
        player = new Player(SCREEN_WIDTH/2, SCREEN_HEIGHT/2, SCREEN_WIDTH, SCREEN_HEIGHT);
        initialRotation = player.getRotation();

        player.setRotatingRight(true);
        player.update();
        assertTrue(player.getRotation() > initialRotation);
    }

    @Test
    void testScreenBoundaries() {
        // Test left boundary
        player = new Player(0, SCREEN_HEIGHT/2, SCREEN_WIDTH, SCREEN_HEIGHT);
        player.setMovingLeft(true);
        player.update();
        assertTrue(player.getX() >= 0);

        // Test right boundary
        player = new Player(SCREEN_WIDTH, SCREEN_HEIGHT/2, SCREEN_WIDTH, SCREEN_HEIGHT);
        player.setMovingRight(true);
        player.update();
        assertTrue(player.getX() <= SCREEN_WIDTH);

        // Test top boundary
        player = new Player(SCREEN_WIDTH/2, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        player.setMovingForward(true);
        player.update();
        assertTrue(player.getY() >= 0);

        // Test bottom boundary
        player = new Player(SCREEN_WIDTH/2, SCREEN_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT);
        player.setMovingBackward(true);
        player.update();
        assertTrue(player.getY() <= SCREEN_HEIGHT);
    }

    @Test
    void testDiagonalMovement() {
        double initialX = player.getX();
        double initialY = player.getY();

        // Test diagonal movement (forward + right)
        player.setMovingForward(true);
        player.setMovingRight(true);
        player.update();

        assertTrue(player.getX() > initialX);
        assertTrue(player.getY() < initialY);
    }

}