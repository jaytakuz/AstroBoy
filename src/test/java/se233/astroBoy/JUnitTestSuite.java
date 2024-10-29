package se233.astroBoy;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
@Suite
@SelectClasses({AsteroidsMovementTest.class, BossMovementTest.class, BossShootingTest.class,EnemyMovementTest.class
        , EnemyShootingTest.class, PlayerMovementsTest.class, PlayerShootingTest.class, ScoreTest.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JUnitTestSuite {

}
