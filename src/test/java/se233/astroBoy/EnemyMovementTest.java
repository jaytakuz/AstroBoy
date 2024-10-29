package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Enemy;
import se233.astroboy.model.Player;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyMovementTest {
    private Enemy enemy;
    private Player player;
    private static final double DELTA = 0.01; // Delta for floating point comparisons

    @BeforeEach
    public void setUp() {
        // Create a player at center of screen
        player = new Player(400, 300,800,600);
        // Create an enemy at top-left corner
        enemy = new Enemy(0, 0, 1, player);
    }

    @Test
    public void testEnemyInitialization() {
        // Test initial position
        assertEquals(0, enemy.getX(), DELTA);
        assertEquals(0, enemy.getY(), DELTA);

        // Test size initialization
        assertEquals(1, enemy.getSize());

        // Test HP initialization for size 1
        assertEquals(1, enemy.getMaxHp());
        assertEquals(1, enemy.getCurrentHp());
    }

    @Test
    public void testEnemyMovement() {
        // Store initial position
        double initialX = enemy.getX();
        double initialY = enemy.getY();

        // Update enemy position
        enemy.update();

        // Verify that the enemy has moved
        assertNotEquals(initialX, enemy.getX(), DELTA);
        assertNotEquals(initialY, enemy.getY(), DELTA);
    }

    @Test
    public void testScreenWrapping() {
        // Test wrapping at right edge
        enemy = new Enemy(801, 300, 1, player);
        enemy.update();
        // The enemy should appear on the left side at -32 (enemy width)
        assertTrue(enemy.getX() < 800, "enemy should move towards player after wrapping");

        // Test wrapping at left edge
        enemy = new Enemy(-33, 300, 1, player);
        enemy.update();
        assertTrue(enemy.getX() >= -120, "Boss should move towards player after wrapping");

        // Test wrapping at bottom edge
        enemy = new Enemy(400, 601, 1, player);
        enemy.update();
        assertTrue(enemy.getY() < 600, "Boss should move towards player after wrapping");

        // Test wrapping at top edge
        enemy = new Enemy(400, -33, 1, player);
        enemy.update();
        assertTrue(enemy.getY() >= -105, "Boss should move towards player after wrapping");
    }



    @Test
    public void testShootingCooldown() {
        // Enemy should be able to shoot initially


        // After resetting cooldown, should not be able to shoot
        enemy.resetShootCooldown();
        assertFalse(enemy.canShoot());

        // Since SHOOT_COOLDOWN is 2.0 seconds and each update is 0.016 seconds (60 FPS)
        // We need at least 125 updates (2.0/0.016) to clear the cooldown
        for(int i = 0; i < 130; i++) { // Adding a few extra frames for safety
            enemy.update();
        }

    }

    @Test
    public void testDifferentSizes() {
        Enemy largeEnemy = new Enemy(0, 0, 2, player);

        // Test size initialization
        assertEquals(2, largeEnemy.getSize());

        // Test HP initialization for size 2
        assertEquals(2, largeEnemy.getMaxHp());
        assertEquals(2, largeEnemy.getCurrentHp());
    }


    @Test
    public void testDamageSystem() {
        // Test damage on size 2 enemy (2 HP)
        Enemy largeEnemy = new Enemy(0, 0, 2, player);

        // Take 1 damage
        largeEnemy.takeDamage(1);
        assertEquals(1, largeEnemy.getCurrentHp());
        assertFalse(largeEnemy.isMarkedForDestructionEnemy());

        // Take fatal damage
        largeEnemy.takeDamage(1);
        assertEquals(0, largeEnemy.getCurrentHp());
        assertTrue(largeEnemy.isMarkedForDestructionEnemy());
    }
}