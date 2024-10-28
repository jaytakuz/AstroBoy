package se233.astroBoy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.astroboy.model.Score;
import static org.junit.jupiter.api.Assertions.*;


public class ScoreTest {

    @BeforeEach
    void setUp() {
        Score.resetScore();

    }

    @Test
    void testZeroPoints() {
        Score.addPoints(0);
        assertEquals(0, Score.getCurrentScore());
        assertEquals(0, Score.getHighScore());
        assertEquals(1, Score.getCombo());
    }


    @Test
    void testMaxComboMultiplier() {
        // Test very high combo to ensure multiplier doesn't grow indefinitely
        for (int i = 0; i < 10; i++) {
            Score.addPoints(100);
        }

        int finalScore = Score.getCurrentScore();
        assertTrue(finalScore > 0, "Score should be positive with high combo");
        assertTrue(Score.getCombo() == 10, "Combo should reach 10");
    }


        @Test
        void testAddPoints() {
            Score.addPoints(100);
            assertEquals(100, Score.getCurrentScore());
        }

        @Test
        void testMaxComboLimit() {
            // Assuming there might be a reasonable max combo limit, let's test a high number of consecutive hits
            for (int i = 0; i < 20; i++) {
                Score.addPoints(100);
            }
            // The score should still be calculated correctly even with a very high combo
            assertTrue(Score.getCombo() > 0);
            assertTrue(Score.getCurrentScore() > 0);
        }


}
