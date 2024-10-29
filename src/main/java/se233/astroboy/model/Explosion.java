package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Explosion {
    private static final Logger logger = LogManager.getLogger(Explosion.class);

    private double x;
    private double y;
    private int currentFrame;
    private int frameCount;
    private int frameWidth;
    private int frameHeight;
    private boolean isFinished;
    private Image spriteSheet;
    private long lastFrameTime;
    private long frameDuration; // duration of each frame in milliseconds

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
        this.currentFrame = 0;
        this.frameCount = 5; // Adjust based on sprite sheet
        this.frameWidth = 48; // Adjust based on sprite sheet
        this.frameHeight = 48; // Adjust based on sprite sheet
        this.isFinished = false;
        this.frameDuration = 50; // 50ms per frame
        this.lastFrameTime = System.currentTimeMillis();
        loadSpriteSheet();
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = new Image(getClass().getResourceAsStream("/se233/astroboy/asset/explosion1.png"));
            if (spriteSheet == null) {
                logger.error("Failed to load explosion sprite sheet");
            }
        } catch (Exception e) {
            logger.error("Error loading explosion sprite sheet: " + e.getMessage());
        }
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame++;
            lastFrameTime = currentTime;

            if (currentFrame >= frameCount) {
                isFinished = true;
            }
        }
    }

    public void render(GraphicsContext gc) {
        if (spriteSheet != null && !isFinished) {
            // Calculate the source rectangle from the sprite sheet
            int sourceX = currentFrame * frameWidth;

            gc.drawImage(spriteSheet,
                    sourceX, 0, frameWidth, frameHeight, // source rectangle
                    x - (double) frameWidth /2, y - (double) frameHeight /2, frameWidth, frameHeight); // destination rectangle
        }
    }

    public boolean isFinished() {
        return isFinished;
    }
}