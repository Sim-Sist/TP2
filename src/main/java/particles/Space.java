package particles;


import java.util.Random;
import java.util.Set;

import output.OutputManager;
import particles.cim.CellIndexMethod;

public class Space {
    private double size;
    private Particle[] particles;
    private double criticalRadius = 50;
    private Set<Integer>[] neighboursSet;
    private Double constantRadius = null;
    private Double constantVelocity = null;
    private final double DEFAULT_MIN_RADIUS = 1, DEFAULT_MAX_RADIUS = 10;
    private double minRadius = DEFAULT_MIN_RADIUS, maxRadius = DEFAULT_MAX_RADIUS;
    private final double DEFAULT_MIN_VELOCITY = 0.3, DEFAULT_MAX_VELOCITY = 1;
    private double minVelocity = DEFAULT_MIN_VELOCITY, maxVelocity = DEFAULT_MAX_VELOCITY;
    private OutputManager oManager;
    private int step = 0;
    private final double NOISE = 1;

    // Defaults to random radii between 1 and 10, and default random velocities
    // between 0.3 and 1
    public Space(double size, double criticalRadius, int particlesAmount) {
        this.size = size;
        this.criticalRadius = criticalRadius;
        this.particles = new Particle[particlesAmount];
        this.oManager = new OutputManager(this);
    }

    // Sets constant radius for all particles
    public void setRadii(double constantRadius) {
        this.constantRadius = constantRadius;
    }

    // Sets particles' radii as a random number between minRadius and maxRadius
    public void setRadii(double minRadius, double maxRadius) {
        this.constantRadius = null;// turn off constant radii
        if (minRadius < 0 || minRadius > maxRadius)
            throw new RuntimeException("Invalid values for radius' limits");
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
    }

    public void setVelocities(double constantVelocity) {
        this.constantVelocity = constantVelocity;
    }

    public void setVelocities(double minVelocity, double maxVelocity) {
        this.constantVelocity = null;
        if (minVelocity < 0 || minVelocity > maxVelocity)
            throw new RuntimeException("Invalid values for radius' limits");
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
    }

    public void initialize() {
        generateSystem();
    }

    private boolean overlaps(Particle p) {
        for (int i = 0; i < particles.length; i++) {
            if (particles[i] == null || particles[i].getIndex() == p.getIndex())
                continue;
            if (p.distanceTo(particles[i]) < 0)
                return true;
        }
        return false;
    }

    private void generateSystem() {
        Random rnd = new Random();
        for (int i = 0; i < particles.length; i++) {
            double radius = (constantRadius == null) ? (rnd.nextDouble() * (maxRadius - minRadius) + minRadius)
                    : constantRadius;
            double velocity = (constantVelocity == null)
                    ? (rnd.nextDouble() * (maxVelocity - minVelocity) + minVelocity)
                    : constantVelocity;
            double speedAngle = rnd.nextDouble() * (2 * Math.PI);
            Particle p = new Particle(
                    i, // index
                    rnd.nextDouble() * size, // x
                    rnd.nextDouble() * size, // y
                    velocity * Math.cos(speedAngle), // vx
                    velocity * Math.sin(speedAngle), // vy
                    0.0, // ax
                    0.0, // ay
                    radius);
            if (p.x < p.radius || (p.x + p.radius) > this.size || p.y < p.radius || (p.y + p.radius) > size) {
                i--;
                continue;
            }
            if (overlaps(p)) {
                i--;
                continue;
            } else {
                particles[i] = p;
            }
        }
        outputInitialState();
        outputNextState();
        step++;
    }

    public void computeNextStep() {
        calculateNeighbours();
        for (Particle p : particles) {
            p.move(neighboursSet[p.getIndex()].stream().map(index -> particles[index]).toList(), NOISE);
        }
        outputNextState();
        step++;
    }

    public void calculateNeighbours() {
        neighboursSet = CellIndexMethod.apply(this);
    }

    public double getSize() {
        return size;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public double getCriticalRadius() {
        return criticalRadius;
    }

    public Set<Integer>[] getNeighbours() {
        return neighboursSet;
    }

    public void outputInitialState() {
        if (!this.oManager.outputInitialState()) {
            System.out.println("There was an error while generating initial's stat output");
        }
    }

    private void outputNextState() {
        if (!this.oManager.outputState(step)) {
            System.out.println("There was an error while generating dynamic states' output");
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Space:\n");
        for (Particle p : particles) {
            str.append(p).append('\n');
        }
        return str.toString();
    }
}