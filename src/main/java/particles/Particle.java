package particles;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Particle {
    private int index;
    public double x, y, vx, vy, ax, ay;
    public double radius;
    private final Double VELOCITY_MAGNITUDE = 0.03;

    public Particle(int index, double x, double y, double vx, double vy, double ax, double ay, double radius) {
        this.index = index;
        this.x = x;
        this.y = y;

        double angle = Math.atan2(vy, vx);
        this.vx = VELOCITY_MAGNITUDE * Math.cos(angle) + vx;
        this.vy = VELOCITY_MAGNITUDE * Math.sin(angle) + vy;

        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
        this.radius = radius;
    }

    public void move(List<Particle> particles, int particleCount) {
        if (particleCount == 0)
            return;

        double meanAngle = 0;
        for (Particle other : particles) {
            meanAngle += Math.atan2(other.getVy(), other.getVx());
        }

        meanAngle /= particleCount;

        this.vx = VELOCITY_MAGNITUDE * Math.cos(meanAngle) + this.vx;
        this.vy = VELOCITY_MAGNITUDE * Math.sin(meanAngle) + this.vy;

        this.update();
    }

    public void update() {
        // Position
        this.x = this.x + this.vx;
        this.y = this.y + this.vy;
        // Velocity
        this.vx = this.vx + this.ax;
        this.vy = this.vy + this.ay;
    }

    public int getIndex() {
        return index;
    }

    public List<Double> getVelocity() {
        List<Double> velocity = new ArrayList<>();
        velocity.add(this.vx);
        velocity.add(this.vy);
        return velocity;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double distanceTo(Particle p) {
        double deltaX = this.x - p.x;
        double deltaY = this.y - p.y;
        return (Math.sqrt(deltaX * deltaX + deltaY * deltaY)) - (this.radius + p.radius);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Particle))
            return false;
        Particle p = (Particle) o;
        return p.x == x && p.y == y && p.index == index && p.radius == radius;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%d: ", index)).append(String.format("r:%.3f x:%.3f  y:%.3f", radius, x, y));
        return str.toString();
    }

}
