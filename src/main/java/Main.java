
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import particles.Space;

public class Main {
    private static final double SIZE = 2;
    private static final int PARTICLES = 8;
    private static final double RADIUS = .2;
    private static final double MIN_RADIUS = .1, MAX_RADIUS = .2;

    public static void main(String[] args) {
        Space s = new Space(SIZE, RADIUS, PARTICLES);
        s.setRadii(MIN_RADIUS, MAX_RADIUS);
        s.initialize();
        System.out.println(s);
        s.calculateCells();
        s.outputInitialState();
        s.outputNeighbours();
    }

    public static void test() {
        // final double size = 20, radius = 1;
        // int gridSize, particles;

        // Map<Integer, Double> elapsedTimes = new TreeMap<>();
        // // Test performance with fixed grid size as 25
        // gridSize = 25;
        // Space s;
        // for (particles = 0; particles < 50000; particles += 1000) {
        // List<Double> times = new ArrayList<>();
        // for (int i = 0; i < 30; i++) {
        // s = new Space(size);
        // s.setCriticalRadius(radius);
        // s.initialize(particles);
        // long start = System.nanoTime();
        // s.calculateCells(gridSize);
        // long end = System.nanoTime();
        // double elapsed = (end - start) / Math.pow(10, 9);
        // times.add(elapsed);
        // }
        // elapsedTimes.put(particles, times.stream().mapToDouble(x ->
        // x).average().getAsDouble());
        // }
        // System.out.println("Performance with increasing particles for grid size of "
        // + gridSize + ":");
        // System.out.println("Particles Elapsed Time");
        // elapsedTimes.forEach((k, v) -> System.out.println(String.format("%9d %12f",
        // k, v)));

    }

}
