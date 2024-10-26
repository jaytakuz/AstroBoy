package se233.astroboy.controller;

import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.astroboy.model.Asteroid;
import se233.astroboy.model.GameObject;
import se233.astroboy.model.Player;
import se233.astroboy.model.Projectile;

import java.util.List;

public class CollisionController {
    private static final Logger logger = LogManager.getLogger(CollisionController.class);

    // Collision margin for more forgiving gameplay
    private static final double COLLISION_MARGIN = 1;

    public static boolean checkCollision(GameObject obj1, GameObject obj2) {
        try {
            // Get the bounds of both objects
            Bounds bounds1 = obj1.getBounds();
            Bounds bounds2 = obj2.getBounds();

            // Apply collision margin
            double width1 = bounds1.getWidth() * COLLISION_MARGIN;
            double height1 = bounds1.getHeight() * COLLISION_MARGIN;
            double width2 = bounds2.getWidth() * COLLISION_MARGIN;
            double height2 = bounds2.getHeight() * COLLISION_MARGIN;

            // Calculate centers
            double centerX1 = bounds1.getMinX() + bounds1.getWidth() ;
            double centerY1 = bounds1.getMinY() + bounds1.getHeight() ;
            double centerX2 = bounds2.getMinX() + bounds2.getWidth() ;
            double centerY2 = bounds2.getMinY() + bounds2.getHeight() ;

            // Check for collision using center points and adjusted dimensions
            return Math.abs(centerX1 - centerX2) < (width1 + width2) / 2 &&
                    Math.abs(centerY1 - centerY2) < (height1 + height2) / 2;
        } catch (Exception e) {
            logger.error("Error checking collision between {} and {}: {}",
                    obj1.getClass().getSimpleName(),
                    obj2.getClass().getSimpleName(),
                    e.getMessage());
            return false;
        }
    }

    public static void handleCollisions(Player player, List<Asteroid> asteroids, List<Projectile> projectiles) {
        try {
            // Check player collision with asteroids
            if (!player.isInvulnerable()) {
                for (Asteroid asteroid : asteroids) {
                    if (checkCollision(player, asteroid)) {
                        player.hit();
                        logger.info("Player hit by asteroid. Lives remaining: {}", player.getLives());
                        break;
                    }
                }
            }

            // Check projectile collisions with asteroids
//            projectiles.removeIf(projectile -> {
//                for (Asteroid asteroid : asteroids) {
//                    if (checkCollision(projectile, asteroid)) {
//                    //    handleAsteroidHit(asteroid);
//                        logger.debug("Projectile hit asteroid");
//                        return true;
//                    }
//                }
//                return false;
//            });
        } catch (Exception e) {
            logger.error("Error handling collisions: {}", e.getMessage());
        }
    }

    private static void handleAsteroidHit(Asteroid asteroid) {
        try {
            // Handle asteroid destruction
            asteroid.markForDestruction();
            logger.info("Asteroid destroyed, points awarded: {}", asteroid.getPoints());
        } catch (Exception e) {
            logger.error("Error handling asteroid hit: {}", e.getMessage());
        }
    }
}