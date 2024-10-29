package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.BossProjectile;
import static org.junit.jupiter.api.Assertions.*;

public class BossShootingTest {
    private static final double SCREEN_WIDTH = 800;
    private static final double SCREEN_HEIGHT = 600;
    private static final double DELTA = 0.001; // Delta for floating-point comparisons

    private double initialX;
    private double initialY;

    @BeforeEach
    void setUp() {
        initialX = SCREEN_WIDTH / 2;
        initialY = SCREEN_HEIGHT / 2;
    }

    @Test
    void testStraightProjectileInitialization() {
        double rotation = 0.0; // Shooting right
        BossProjectile projectile = new BossProjectile(
                initialX, initialY, rotation, SCREEN_WIDTH, SCREEN_HEIGHT,
                BossProjectile.ProjectilePattern.STRAIGHT
        );

        assertNotNull(projectile);
        assertEquals(initialX, projectile.getX(), DELTA);
        assertEquals(initialY, projectile.getY(), DELTA);
        assertFalse(projectile.isExpired());
    }

    @Test
    void testStraightProjectileMovement() {
        // Test right-moving projectile (0 degrees)
        BossProjectile projectile = new BossProjectile(
                initialX, initialY, 0.0, SCREEN_WIDTH, SCREEN_HEIGHT,
                BossProjectile.ProjectilePattern.STRAIGHT
        );

        projectile.update();

        // Projectile should move right (x increases, y stays same)
        assertTrue(projectile.getX() > initialX);
        assertEquals(initialY, projectile.getY(), DELTA);
    }

    @Test
    void testProjectileWraparound() {
        // Test projectile going off right edge
        BossProjectile projectile = new BossProjectile(
                SCREEN_WIDTH - 1, initialY, 0.0, SCREEN_WIDTH, SCREEN_HEIGHT,
                BossProjectile.ProjectilePattern.STRAIGHT
        );

        projectile.update();

        // Should wrap to left side
        assertTrue(projectile.getX() < SCREEN_WIDTH);
    }

    @Test
    void testProjectileExpiration() {
        BossProjectile projectile = new BossProjectile(
                initialX, initialY, 0.0, SCREEN_WIDTH, SCREEN_HEIGHT,
                BossProjectile.ProjectilePattern.STRAIGHT
        );

        // Update many times to exceed MAX_LIFETIME (1.25 seconds)
        // At 60 FPS, need approximately 75 updates
        for (int i = 0; i < 80; i++) {
            projectile.update();
        }

        assertTrue(projectile.isExpired());
    }

    @Test
    void testMultiShotPattern() {
        int bulletCount = 5;
        double spreadAngle = 45.0;
        double centerRotation = 90.0; // Shooting upward

        BossProjectile[] projectiles = BossProjectile.createMultiShotPattern(
                initialX, initialY, centerRotation,
                SCREEN_WIDTH, SCREEN_HEIGHT,
                bulletCount, spreadAngle
        );

        // Verify correct number of projectiles created
        assertEquals(bulletCount, projectiles.length);

        // Verify all projectiles start from same position
        for (BossProjectile projectile : projectiles) {
            assertEquals(initialX, projectile.getX(), DELTA);
            assertEquals(initialY, projectile.getY(), DELTA);
        }

        // Update all projectiles and verify they spread out
        for (BossProjectile projectile : projectiles) {
            projectile.update();
        }

        // Check that projectiles have spread out (different positions)
        double firstX = projectiles[0].getX();
        double firstY = projectiles[0].getY();
        boolean hasDifferentPositions = false;

        for (int i = 1; i < projectiles.length; i++) {
            if (Math.abs(firstX - projectiles[i].getX()) > DELTA ||
                    Math.abs(firstY - projectiles[i].getY()) > DELTA) {
                hasDifferentPositions = true;
                break;
            }
        }

        assertTrue(hasDifferentPositions, "Projectiles should spread out in different directions");
    }

    @Test
    void testDiagonalProjectileMovement() {
        // Test diagonal movement (45 degrees)
        double rotation = 45.0;
        BossProjectile projectile = new BossProjectile(
                initialX, initialY, rotation, SCREEN_WIDTH, SCREEN_HEIGHT,
                BossProjectile.ProjectilePattern.STRAIGHT
        );

        double startX = projectile.getX();
        double startY = projectile.getY();

        projectile.update();

        // Both X and Y should increase for 45-degree movement
        assertTrue(projectile.getX() > startX);
        assertTrue(projectile.getY() > startY);
    }
}
