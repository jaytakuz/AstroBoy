package se233.astroBoy;

import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Enemy;
import se233.astroboy.model.EnemyProjectile;
import se233.astroboy.model.Player;
import static org.junit.jupiter.api.Assertions.*;


public class EnemyShootingTest {
    private Enemy enemy;
    private Player player;
    private EnemyProjectile projectile;
    private static final double STAGE_WIDTH = 800;
    private static final double STAGE_HEIGHT = 600;
    private static final double DELTA = 0.001; // For floating-point comparisons

    private GraphicsContext graphicsContext;

    @BeforeEach
    void setUp() {

        player = new Player(STAGE_WIDTH/2, STAGE_HEIGHT/2, STAGE_WIDTH, STAGE_HEIGHT);
        enemy = new Enemy(100, 100, 1, player);
        projectile = new EnemyProjectile(100, 100, 0, STAGE_WIDTH, STAGE_HEIGHT);
    }

    // Previous enemy shooting tests remain the same...

    @Test
    void testProjectileInitialization() {
        EnemyProjectile projectile = new EnemyProjectile(100, 100, 45, STAGE_WIDTH, STAGE_HEIGHT);

        assertEquals(100, projectile.getX(), DELTA, "Initial X position should be set correctly");
        assertEquals(100, projectile.getY(), DELTA, "Initial Y position should be set correctly");
        assertEquals(45, projectile.getRotation(), DELTA, "Rotation should be set correctly");
        assertFalse(projectile.isExpired(), "Projectile should not be expired initially");
    }

    @Test
    void testProjectileMovement() {
        // Test horizontal movement (0 degrees)
        EnemyProjectile projectileRight = new EnemyProjectile(100, 100, 0, STAGE_WIDTH, STAGE_HEIGHT);
        projectileRight.update();
        assertTrue(projectileRight.getX() > 100, "Projectile should move right");
        assertEquals(100, projectileRight.getY(), DELTA, "Y position should not change for horizontal movement");

        // Test vertical movement (90 degrees)
        EnemyProjectile projectileDown = new EnemyProjectile(100, 100, 90, STAGE_WIDTH, STAGE_HEIGHT);
        projectileDown.update();
        assertEquals(100, projectileDown.getX(), DELTA, "X position should not change for vertical movement");
        assertTrue(projectileDown.getY() > 100, "Projectile should move down");
    }

    @Test
    void testProjectileScreenWrapping() {
        // Test wrapping at right edge
        EnemyProjectile projectileRight = new EnemyProjectile(STAGE_WIDTH - 1, 100, 0, STAGE_WIDTH, STAGE_HEIGHT);
        projectileRight.update();
        assertTrue(projectileRight.getX() < STAGE_WIDTH, "Projectile should wrap to left side");

        // Test wrapping at bottom edge
        EnemyProjectile projectileDown = new EnemyProjectile(100, STAGE_HEIGHT - 1, 90, STAGE_WIDTH, STAGE_HEIGHT);
        projectileDown.update();
        assertTrue(projectileDown.getY() < STAGE_HEIGHT, "Projectile should wrap to top side");
    }

    @Test
    void testProjectileLifetime() {
        EnemyProjectile projectile = new EnemyProjectile(100, 100, 0, STAGE_WIDTH, STAGE_HEIGHT);
        assertFalse(projectile.isExpired(), "Projectile should not be expired initially");

        // Simulate updates for more than 1 second (assuming 60 FPS)
        for (int i = 0; i < 67; i++) { // 67 * 0.015 ≈ 1 second
            projectile.update();
        }

        assertTrue(projectile.isExpired(), "Projectile should expire after maximum lifetime");
    }

    @Test
    void testProjectileVelocityCalculation() {
        // Test 45-degree angle
        EnemyProjectile projectile45 = new EnemyProjectile(100, 100, 45, STAGE_WIDTH, STAGE_HEIGHT);
        double initialX = projectile45.getX();
        double initialY = projectile45.getY();
        projectile45.update();

        double deltaX = projectile45.getX() - initialX;
        double deltaY = projectile45.getY() - initialY;

        // At 45 degrees, x and y components should be approximately equal
        assertEquals(Math.abs(deltaX), Math.abs(deltaY), DELTA,
                "X and Y velocity components should be equal at 45 degrees");
    }

    @Test
    void testMultipleProjectileUpdates() {
        EnemyProjectile projectile = new EnemyProjectile(100, 100, 0, STAGE_WIDTH, STAGE_HEIGHT);
        double initialX = projectile.getX();

        // Update multiple times
        for (int i = 0; i < 10; i++) {
            projectile.update();
        }

        double totalDistance = projectile.getX() - initialX;
        assertTrue(totalDistance > 0, "Projectile should move consistently over multiple updates");
    }

    @Test
    void testProjectileBoundaries() {
        // Test projectile at stage boundaries
        double[] testPositions = {0, STAGE_WIDTH/2, STAGE_WIDTH};
        for (double x : testPositions) {
            for (double y : testPositions) {
                EnemyProjectile boundaryProjectile = new EnemyProjectile(x, y, 45, STAGE_WIDTH, STAGE_HEIGHT);
                boundaryProjectile.update();

                assertTrue(boundaryProjectile.getX() >= 0 && boundaryProjectile.getX() <= STAGE_WIDTH,
                        "Projectile X position should stay within or wrap around stage bounds");
                assertTrue(boundaryProjectile.getY() >= 0 && boundaryProjectile.getY() <= STAGE_HEIGHT,
                        "Projectile Y position should stay within or wrap around stage bounds");
            }
        }
    }
}