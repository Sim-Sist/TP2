package particles;

import java.util.List;
import java.util.Random;

public class Particle {
    private int index;
    public double velocity, speedAngle;
    public double x, y, ax, ay;
    public double radius;

    public Particle(int index, double x, double y, double velocity, double speedAngle, double ax, double ay,
            double radius) {
        this.index = index;
        this.x = x;
        this.y = y;

        this.velocity = velocity;
        this.speedAngle = speedAngle;

        this.ax = ax;
        this.ay = ay;
        this.radius = radius;
    }

    public void moveWithAllForces(List<Particle> particles, double noise) {
        int particleCount = particles.size();
        if (particleCount == 0)
            return;

        // double meanVelocityAngle = 0;
        // double meanPositionAngle = 0;
        // double meanSeparationAngle = 0;

        // double sinAvg = particles.stream().mapToDouble(p ->
        // Math.sin(p.speedAngle)).average().getAsDouble();
        // double cosAvg = particles.stream().mapToDouble(p ->
        // Math.cos(p.speedAngle)).average().getAsDouble();

        // meanVelocityAngle = Math.atan2(sinAvg, cosAvg);

        // for (Particle other : particles) {
        // meanVelocityAngle += Math.atan2(other.getVy(), other.getVx());
        // meanPositionAngle += Math.atan2(other.getY(), other.getX());
        // meanSeparationAngle += Math.atan2((other.getY() - this.getY()) /
        // this.distanceTo(other),
        // (other.getX() - this.getX()) / this.distanceTo(other));
        // }

        // meanVelocityAngle /= particleCount;
        // meanPositionAngle /= particleCount;
        // meanSeparationAngle /= particleCount;

        // double meanAngle = (meanVelocityAngle + meanPositionAngle +
        // meanSeparationAngle) / 3;

        // this.vx = VELOCITY_MAGNITUDE * Math.cos(meanAngle + noise) + this.vx;
        // this.vy = VELOCITY_MAGNITUDE * Math.sin(meanAngle + noise) + this.vy;

        this.update();
    }

    private static double randomFloat(double minInclusive, double maxInclusive) {
        double precision = 0.00001;
        int max = (int) (maxInclusive / precision);
        int min = (int) (minInclusive / precision);
        Random rand = new Random();
        int randomInt = rand.nextInt((max - min) + 1) + min;
        double randomNum = randomInt * precision;
        return randomNum;
    }

    public void move(List<Double> neighbourAngles, double noise) {
        int particleCount = neighbourAngles.size();
        this.update();
        if (particleCount == 0) {
            return;
        }
        double sinAvg = neighbourAngles.stream().mapToDouble(a -> Math.sin(a)).average().getAsDouble();
        double cosAvg = neighbourAngles.stream().mapToDouble(a -> Math.cos(a)).average().getAsDouble();

        this.speedAngle = Math.atan2(sinAvg, cosAvg) + Particle.randomFloat(-noise / 2, noise / 2);

    }

    public void update() {
        // Position
        this.x = this.x + this.velocity * Math.cos(this.speedAngle);
        this.y = this.y + this.velocity * Math.sin(this.speedAngle);
    }

    public int getIndex() {
        return index;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public double getVx() {
        return velocity * Math.cos(this.speedAngle);
    }

    public double getVy() {
        return velocity * Math.sin(this.speedAngle);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
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
