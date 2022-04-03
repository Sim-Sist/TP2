package particles;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import output.SpaceOutputManager;
import particles.cim.CellIndexMethod;

public class Space {
    /*** Simulation parameters ***/
    private double size;
    private Particle[] particles;
    private double criticalRadius = 50;
    private Double constantRadius = null;
    private Double constantVelocity = null;
    private final double DEFAULT_MIN_RADIUS = 1, DEFAULT_MAX_RADIUS = 10;
    private double minRadius = DEFAULT_MIN_RADIUS, maxRadius = DEFAULT_MAX_RADIUS;
    private final double DEFAULT_MIN_VELOCITY = 0.3, DEFAULT_MAX_VELOCITY = 1;
    private double minVelocity = DEFAULT_MIN_VELOCITY, maxVelocity = DEFAULT_MAX_VELOCITY;
    /*** Output file vars ***/
    private String staticFileName, dynamicFileName;
    /*** Class variables ***/
    private SpaceOutputManager oManager;
    private int step;
    private double noiseLimit;
    private Set<Integer>[] neighboursSetArray;

    // Defaults to random radii between 1 and 10, and default random velocities
    // between 0.3 and 1
    public Space(double size, double criticalRadius, int particlesAmount, double noiseLimit) {
        System.out.println("Space initialized with:");
        System.out.println(particlesAmount + " particles");
        System.out.println("Size of " + size);
        System.out.println("Noise of " + noiseLimit);
        System.out.println();
        this.size = size;
        this.criticalRadius = criticalRadius;
        this.noiseLimit = noiseLimit;
        this.particles = new Particle[particlesAmount];
        this.oManager = new SpaceOutputManager(this);
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
        step = 0;
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
                    velocity, // velocity
                    speedAngle, // speedAngle
                    radius);
            if (p.x < p.radius || (p.x + p.radius) > this.size || p.y < p.radius || (p.y + p.radius) > size) {
                i--;
                continue;
            }
            if (p.radius > 0 && overlaps(p)) {
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

    @SuppressWarnings("unchecked")
    public void computeNextStep() {
        calculateNeighbours();
        List<Double>[] oldAngles = (List<Double>[]) new LinkedList[particles.length];
        for (int i = 0; i < neighboursSetArray.length; i++) {
            oldAngles[i] = new LinkedList<>(
                    neighboursSetArray[i].stream().map(pIndex -> particles[pIndex].speedAngle).toList());
            oldAngles[i].add(particles[i].speedAngle);
        }

        for (Particle p : particles) {
            p.move(oldAngles[p.getIndex()], noiseLimit);
            if (p.x < 0) {
                p.x = p.x + size;
            }
            if (p.y < 0) {
                p.y = p.y + size;
            }
            if (p.x > size) {
                p.x = p.x - size;
            }
            if (p.y > size) {
                p.y = p.y - size;
            }

        }
        outputNextState();
        step++;
    }

    public void calculateNeighbours() {
        neighboursSetArray = CellIndexMethod.apply(this);
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
        return neighboursSetArray;
    }

    public void setStaticFileName(String filename) {
        this.staticFileName = filename;
    }

    public void setDynamicFileName(String filename) {
        this.dynamicFileName = filename;
    }

    public void outputInitialState() {
        boolean success;
        if (staticFileName == null) {
            success = this.oManager.outputInitialState();
        } else {
            success = this.oManager.outputInitialState(staticFileName);
        }
        if (!success) {
            System.out.println("There was an error while generating initial's stat output");
        }
    }

    private void outputNextState() {
        boolean success;
        if (dynamicFileName == null) {
            success = this.oManager.outputState(step);
        } else {
            success = this.oManager.outputState(step, dynamicFileName);
        }
        if (!success) {
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