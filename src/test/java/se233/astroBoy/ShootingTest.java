package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Player;
import se233.astroboy.model.Projectile;
import static org.junit.jupiter.api.Assertions.*;

public class ShootingTest {
    private Player player;
    private static final double STAGE_WIDTH = 800;
    private static final double STAGE_HEIGHT = 600;
    private static final double DELTA = 0.001; // For floating-point comparisons
    private static final double SHOOT_COOLDOWN = 0.25; // Match Player.java's cooldown

    @BeforeEach
    void setUp() {
        // Create a player at the center of the stage
        player = new Player(STAGE_WIDTH/2, STAGE_HEIGHT/2, STAGE_WIDTH, STAGE_HEIGHT);
    }

    @Test
    void testInitialShootingState() {
        assertTrue(player.canShoot());
        assertTrue(player.isAlive());
        assertEquals(3, player.getLives());
    }

    @Test
    void testShootCooldownMechanism() {
        // Verify initial state
        assertTrue(player.canShoot());

        // Simulate shooting
        player.resetShootCooldown();

        // Verify cooldown is active
        assertFalse(player.canShoot());

        // Simulate time passing (more than cooldown duration)
        for (int i = 0; i < 20; i++) { // Simulate ~0.32 seconds (20 * 0.016)
            player.update();
        }

        // Should be able to shoot again
        assertTrue(player.canShoot());
    }

    @Test
    void testProjectileSpawnPosition() {
        // Set player rotation to 0 degrees (facing right)
        player.setRotation(0);

        // Calculate expected projectile spawn position based on player position
        double spawnDistance = 20; // Same as in original test
        double expectedX = player.getX() + spawnDistance * Math.cos(Math.toRadians(player.getRotation()));
        double expectedY = player.getY() + spawnDistance * Math.sin(Math.toRadians(player.getRotation()));

        // Create projectile at spawn position
        Projectile projectile = new Projectile(
                expectedX,
                expectedY,
                player.getRotation(),
                STAGE_WIDTH,
                STAGE_HEIGHT
        );

        // Verify projectile position
        assertEquals(expectedX, projectile.getX(), DELTA);
        assertEquals(expectedY, projectile.getY(), DELTA);
    }

    @Test
    void testProjectileRotationAlignment() {
        // Test different rotation angles
        double[] testAngles = {-90, 0, 45, 90, 180, 270};

        for (double angle : testAngles) {
            player.setRotation(angle);

            // Create projectile with player's rotation
            Projectile projectile = new Projectile(
                    player.getX(),
                    player.getY(),
                    player.getRotation(),
                    STAGE_WIDTH,
                    STAGE_HEIGHT
            );

            assertEquals(angle, projectile.getRotation(), DELTA);
        }
    }

    @Test
    void testShootingWhileInvulnerable() {
        // Make player invulnerable
        player.hit();
        assertTrue(player.isInvulnerable());

        // Verify can still shoot while invulnerable
        assertTrue(player.canShoot());

        player.resetShootCooldown();
        assertFalse(player.canShoot());
    }

    @Test
    void testShootingEffectReset() {
        // Verify initial state
        assertTrue(player.canShoot());

        // Simulate multiple shots
        for (int i = 0; i < 3; i++) {
            while (!player.canShoot()) {
                player.update();
            }
            player.resetShootCooldown();

            // Verify cooldown is active immediately after shooting
            assertFalse(player.canShoot());
        }
    }

    @Test
    void testPlayerDeathAndShooting() {
        // Deplete player lives
        while (player.getLives() > 0) {
            player.hit();
            // Wait for invulnerability to wear off
            for (int i = 0; i < 125; i++) { // ~2 seconds
                player.update();
            }
        }

        assertFalse(player.isAlive());
        // Note: Depending on game design, you might want to prevent shooting while dead

        // This test assumes dead players can still technically shoot
        assertTrue(player.canShoot());
    }
}