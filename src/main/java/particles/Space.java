package particles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;
import java.util.Set;

import particles.cim.CellIndexMethod;

public class Space {
    private double height, width;
    private Particle[] particles;
    private double criticalRadius = 50;
    private Set<Integer>[] neighbours;
    private final double DEFAULT_MIN_RADIUS = 1, DEFAULT_MAX_RADIUS = 10;
    private double minRadius = DEFAULT_MIN_RADIUS, maxRadius = DEFAULT_MAX_RADIUS;
    private final String INIT_STATE_DEFAULT_FILENAME = "particles.txt";
    private final String NEIGHBOURS_DEFAULT_FILENAME = "neighbours.txt";

    public Space(double size) {
        this(size, size);
    }

    private Space(double height, double width) {
        this.height = height;
        this.width = width;
    }

    // Particles with random radiuses
    public void initialize(int particlesAmount) {
        this.initialize(particlesAmount, null);
    }

    // Particles with defined constant radius
    public void initialize(int particlesAmount, Double particleRadius) {
        particles = new Particle[particlesAmount];
        generateSystem(particlesAmount, particleRadius);
    }

    public void setCriticalRadius(double radius) {
        this.criticalRadius = radius;
    }

    public void setRadiusLimits(double min, double max) {
        if (min < 0 || min > max)
            throw new RuntimeException("Invalid values for radius' limits");
        this.minRadius = min;
        this.maxRadius = max;
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

    private void generateSystem(int particlesAmount, Double particleRadius) {
        Random rnd = new Random();
        for (int i = 0; i < particlesAmount; i++) {
            double radius = (particleRadius == null) ? (rnd.nextDouble() * (maxRadius - minRadius) + minRadius)
                    : particleRadius;
            Particle p = new Particle(i, rnd.nextDouble() * width, rnd.nextDouble() * height, 0.0, 0.0, 0.0, 0.0,
                    radius);
            if (p.x < p.radius || (p.x + p.radius) > this.width || p.y < p.radius || (p.y + p.radius) > height) {
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
    }

    public void calculateCells() {
        neighbours = CellIndexMethod.apply(height, criticalRadius, particles);
    }

    public void calculateCells(int gridSize) {
        neighbours = CellIndexMethod.apply(height, criticalRadius, particles, gridSize);
    }

    private String getRoot() {
        URL u = getClass().getProtectionDomain().getCodeSource().getLocation();
        return (u.toString().replace("file:", "").replace("bin/", ""));
    }

    public boolean outputInitialState() {
        return this.outputInitialState(INIT_STATE_DEFAULT_FILENAME);
    }

    public boolean outputInitialState(String filename) {
        FileWriter fw;
        try {
            String filepath = getRoot() + "src/main/output/";
            File file = new File(filepath, filename);
            file.createNewFile();
            fw = new FileWriter(file);

            /**
             * Header stile goes like:
             * - Number of particles
             * - Size of Space
             * - Length of critical radius
             * - One free line to put a comment/name. This can also be left blank.
             */
            fw.append(Integer.toString(particles.length)).append('\n');
            fw.append(Double.toString(height)).append('\n');
            fw.append(Double.toString(criticalRadius)).append('\n');
            fw.append('\n');

            /**
             * Body of textfile consists of one line for each particle
             * with its radius and then its xy coordinates, all separated by a spaces
             */
            for (Particle p : particles) {
                fw.append(String.format("%f %f %f\n", p.radius, p.x, p.y));
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean outputNeighbours() {
        return this.outputNeighbours(NEIGHBOURS_DEFAULT_FILENAME);
    }

    public boolean outputNeighbours(String filename) {
        if (neighbours == null) {
            return false;
        }
        FileWriter fw;
        int defaultTarget = 1;
        try {
            String filepath = getRoot() + "src/main/output/";
            File file = new File(filepath, filename);
            file.createNewFile();
            fw = new FileWriter(file);

            /**
             * Header stile goes like:
             * - One free line to put a comment/name. This can also be left blank.
             */
            fw.append('\n');

            /**
             * Body of textfile consists of one line for each particle
             * with a list of all its neighbours separated by a space
             */
            for (Set<Integer> s : neighbours) {
                StringBuilder sb = new StringBuilder();
                for (Integer n : s) {
                    sb.append(n).append(" ");
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1); // Delete last space
                sb.append('\n');
                fw.append(sb.toString());
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
