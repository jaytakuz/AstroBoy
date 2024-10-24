package se233.astroboy.model;

import javafx.scene.canvas.GraphicsContext;

// Base class for game objects
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

    // Basic collision detection using bounding boxes
    public boolean collidesWith(GameObject other) {
        return x < other.getX() + other.getWidth() &&
                x + width > other.getX() &&
                y < other.getY() + other.getHeight() &&
                y + height > other.getY();
    }
}