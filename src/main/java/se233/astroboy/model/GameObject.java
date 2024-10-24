package se233.astroboy.model;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    protected double x;            // X position
    protected double y;            // Y position
    protected double width;        // Object width
    protected double height;       // Object height
    protected double velocity;     // Movement speed
    protected double rotation;     // Rotation angle in degrees

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocity = 0;
        this.rotation = 0;
    }

    // Abstract methods that must be implemented by child classes
    public abstract void update();
    public abstract void render(GraphicsContext gc);

    // Add getBounds method
    public Bounds getBounds() {
        return new javafx.geometry.BoundingBox(x - width/2, y - height/2, width, height);
    }

    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getVelocity() { return velocity; }
    public void setVelocity(double velocity) { this.velocity = velocity; }
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }
}