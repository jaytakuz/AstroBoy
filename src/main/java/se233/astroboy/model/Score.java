package se233.astroboy.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Score {
    private static final Logger logger = LogManager.getLogger(Score.class);

    private static int currentScore = 0;
    private static int highScore = 0;

    // Scoring multipliers for combos
    private static int consecutiveHits = 0;
    private static final double COMBO_MULTIPLIER = 1.2;
    private static final int COMBO_WINDOW = 2; // seconds
    private static double lastHitTime = 0;

    public static void resetScore() {
        currentScore = 0;
        consecutiveHits = 0;
        lastHitTime = 0;
        logger.info("Score reset to 0");
    }

    public static void addPoints(int points) {
        double currentTime = System.currentTimeMillis() / 1000.0;

        // Check if this hit is within the combo window
        if (currentTime - lastHitTime <= COMBO_WINDOW) {
            consecutiveHits++;
            // Apply combo multiplier
            double multiplier = 1 + (consecutiveHits * COMBO_MULTIPLIER - 1);
            points = (int)(points * multiplier);
            logger.debug("Combo x{} applied! Points multiplied to {}", consecutiveHits, points);
        } else {
            consecutiveHits = 1;
        }

        lastHitTime = currentTime;
        currentScore += points;

        // Update high score if necessary
        if (currentScore > highScore) {
            highScore = currentScore;
            logger.info("New high score achieved: {}", highScore);
        }

        logger.debug("Score updated: {} (High Score: {})", currentScore, highScore);
    }

    public static int getCurrentScore() {
        return currentScore;
    }

    public static int getHighScore() {
        return highScore;
    }

    public static int getCombo() {
        return consecutiveHits;
    }

    public static boolean isHighScore() {
        return currentScore >= highScore && currentScore > 0;
    }
}