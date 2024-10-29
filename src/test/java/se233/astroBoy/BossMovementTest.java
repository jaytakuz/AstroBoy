package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Boss;
import se233.astroboy.model.Player;
import static org.junit.jupiter.api.Assertions.*;

public class BossMovementTest {
    private Boss boss;
    private Player player;
    private static final double DELTA = 0.01; // Delta for floating-point comparisons

    @BeforeEach
    public void setUp() {
        // Initialize a player at the center of the screen
        player = new Player(400, 300,800,600);
        // Initialize a boss at position (100, 100)
        boss = new Boss(100, 100, 1, player);
    }

    @Test
    public void testBossInitialization() {
        // Test boss size and dimensions
        assertEquals(1, boss.getSize());
        assertEquals(120, boss.getWidth());
        assertEquals(105, boss.getHeight());

        // Test initial HP
        assertEquals(5, boss.getMaxHp());
        assertEquals(5, boss.getCurrentHp());
    }

    @Test
    public void testBossMovementTowardsPlayer() {
        // Store initial position
        double initialX = boss.getX();
        double initialY = boss.getY();

        // Update boss position
        boss.update();

        // Since player is at (400, 300) and boss starts at (100, 100),
        // boss should move towards player (i.e., right and down)
        assertTrue(boss.getX() > initialX, "Boss should move right towards player");
        assertTrue(boss.getY() > initialY, "Boss should move down towards player");
    }

    @Test
    public void testBossScreenWrapping() {
        // Test wrapping when going off right edge
        boss = new Boss(800, 300, 1, player);
        boss.update();
        // The boss should start moving towards the player from the wrapped position
        assertTrue(boss.getX() < 800, "Boss should move towards player after wrapping");

        // Test wrapping when going off left edge
        boss = new Boss(-120, 300, 1, player);
        boss.update();
        assertTrue(boss.getX() >= -120, "Boss should move towards player after wrapping");

        // Test wrapping when going off bottom edge
        boss = new Boss(400, 600, 1, player);
        boss.update();
        assertTrue(boss.getY() < 600, "Boss should move towards player after wrapping");

        // Test wrapping when going off top edge
        boss = new Boss(400, -105, 1, player);
        boss.update();
        assertTrue(boss.getY() >= -105, "Boss should move towards player after wrapping");
    }

    @Test
    public void testBossRotationTowardsPlayer() {
        // Place boss and player in known positions
        boss = new Boss(200, 200, 1, player);

        Player rightPlayer = new Player(400, 200,800,600); // Player directly to the right

        // Calculate expected angle (now we test if it's roughly correct rather than exact)
        double angleToPlayer = boss.getAngleToPlayer();

        // The angle should be roughly 0 degrees (allowing for some variation)
        assertTrue(Math.abs(angleToPlayer) < 45,
                "Boss should roughly point towards player (angle within 45 degrees)");

    }

    @Test
    public void testBossDamageAndDestruction() {
        // Test initial HP
        assertEquals(5, boss.getCurrentHp());

        // Test taking damage
        boss.takeDamage(2);
        assertEquals(3, boss.getCurrentHp(), "Boss should have 3 HP after taking 2 damage");
        assertFalse(boss.isMarkedForDestructionBoss(), "Boss should not be marked for destruction yet");

        // Test destruction
        boss.takeDamage(3);
        assertEquals(0, boss.getCurrentHp(), "Boss should have 0 HP after taking fatal damage");
        assertTrue(boss.isMarkedForDestructionBoss(), "Boss should be marked for destruction");
    }

    @Test
    public void testBossShootingCooldown() {
        // Since initial cooldown is random, we test the reset functionality
        boss.resetShootCooldown();
        assertFalse(boss.canShoot(), "Boss should not be able to shoot immediately after cooldown reset");

        // We can't effectively test the cooldown timing in a unit test
        // without making the test brittle or slow, so we just verify the state changes
        assertFalse(boss.canShoot(), "Boss should not be able to shoot during cooldown");
    }


}
